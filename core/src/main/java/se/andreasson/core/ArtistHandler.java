package se.andreasson.core;

import se.andreasson.core.repository.ArtistDAO;
import se.andreasson.core.repository.ArtistDaoJpaImpl;
import se.andreasson.core.model.*;
import se.andreasson.utils.JsonConverter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ArtistHandler implements URLHandler {

    @Override
    public Response handleURL(Request request) {

        ArtistDAO artistDAO = new ArtistDaoJpaImpl();
        System.out.println("handleUrl. DAO:" + artistDAO.toString());
        Response response = new Response();
        List<Artist> artists = new ArrayList<>();

        if (request.getRequestMethod().equals("GET")) {
            System.out.println("handleURL. RequestParameterString: " + request.getUrlParameterString());
            if (request.getUrlParameterString() == null)         // -> no url parameters
            {
                artists = artistDAO.getAllArtists();
                System.out.println("handleURL-get all artists" + artists.toString());
            }
            else if (request.getUrlParameterString().split("[=]")[0].equals("name"))
            {
                String artistName = request.getUrlParameterString().split("[=]")[1];
                System.out.println("Artisthandler: " + artistName);
                artists = artistDAO.getByName(artistName);
            }
            if (!artists.isEmpty())
            {
                setJsonResponse(response, artists);
            }
            else {
                setNotFoundResponse(response);
            }

        } else if (request.getRequestMethod().equals("POST") && request.getContentType().equals("application/json")) {
            setNotFoundResponse(response);
            try {
                Gson gson = new Gson();
                String bodyText = new String(request.getBody());                        //String body = IOUtils.toString(request.getBody(), "StandardCharsets.UTF_8");
                System.out.println(bodyText);
                Artist artist = gson.fromJson(bodyText, Artist.class);                   //Employee empObject = gson.fromJson(jsonString, Employee.class); var obj = JSON.parse('{ "name":"John", "age":30, "city":"New York"}');
                System.out.println("Artist from Json-input: " + artist.toString());

                artistDAO.create(artist);
                response.setStatusMessage("HTTP/1.1 200 OK");
                response.setContentLength(0);
                setJsonResponse(response, artists);
            } catch (Exception e) {
                throw new RuntimeException("Cannot convert posted Json body to Artist object" + e);
            }
        }
        return response;
    }

    private void setNotFoundResponse(Response response) {
        response.setStatusMessage("HTTP/1.1 404 Not Found");
        response.setContentLength(0);
        response.setContentType("");
    }

    private void setJsonResponse(Response response, List<Artist> artists) {
        JsonConverter jsonConverter = new JsonConverter();
        var jsonResponse = jsonConverter.convertToJson(artists);
        response.setContentType("application/json");          //ska det vara nån annan typ?
        byte[] jsonBytes = jsonResponse.getBytes();
        response.setContentLength(jsonBytes.length);
        response.setContent(jsonBytes);
        response.setStatusMessage("HTTP/1.1 200 OK");
    }
}
