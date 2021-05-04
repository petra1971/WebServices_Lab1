package se.andreasson.core;

import com.google.gson.Gson;
import se.andreasson.core.model.Artist;
import se.andreasson.core.model.Request;
import se.andreasson.core.model.Response;
import se.andreasson.core.repository.ArtistDAO;
import se.andreasson.core.repository.ArtistDaoJpaImpl;
import se.andreasson.utils.JsonConverter;

import java.util.ArrayList;
import java.util.List;

public class ArtistHandler implements URLHandler {

    @Override
    public Response handleURL(Request request) {
        ArtistDAO artistDAO = new ArtistDaoJpaImpl();
        Response response = new Response();
        List<Artist> artists = new ArrayList<>();

        if (request.getRequestMethod().equals("GET")) {

            if (request.getQueryParameters() == null)         // -> no query parameters (no '?')
            {
                artists = artistDAO.getAllArtists();
                System.out.println("handleURL. Get all artists" + artists.toString());
            }
            else if (request.getQueryParameters().containsKey("name"))   //E. Query string: name=Petra
            {
                System.out.println("handleURL. QueryParameters: " + request.getQueryParameters().values());
                String artistName = request.getQueryParameters().get("name");
                System.out.println("handleUrl. Artist name:" + artistName);
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
            try {
                //Make artist objects from json string in the body
                Gson gson = new Gson();
                String bodyText = request.getBody();                        //String body = IOUtils.toString(request.getBody(), "StandardCharsets.UTF_8");
                System.out.println("Artisthandler. BodyText. " + bodyText);
                var artist = gson.fromJson(bodyText, Artist.class);                   //Employee empObject = gson.fromJson(jsonString, Employee.class); var obj = JSON.parse('{ "name":"John", "age":30, "city":"New York"}');
                System.out.println("Artist from Json-input: " + artist.toString());

                artistDAO.create(artist);
                setCreatedResponse(response);

            } catch (Exception e) {
                System.out.println("Cannot add posted artist to DB. " + e);
                setBadRequestResponse(response);
            }
        }
        return response;
    }

    private void setJsonResponse(Response response, List<Artist> artists) {
        JsonConverter jsonConverter = new JsonConverter();
        var jsonResponse = jsonConverter.convertToJson(artists);
        byte[] jsonBytes = jsonResponse.getBytes();
        response.setContentType("application/json");          //ska det vara nån annan typ?
        response.setContentLength(jsonBytes.length);
        response.setContent(jsonBytes);
        response.setFirstHeaderLine("HTTP/1.1 200 OK");
    }

    private void setNotFoundResponse(Response response) {
        response.setFirstHeaderLine("HTTP/1.1 404 Not Found");
        response.setContentLength(0);
    }

    private void setCreatedResponse(Response response) {
        response.setFirstHeaderLine("HTTP/1.1 201 Created");
        response.setContentLength(0);
    }

    private void setBadRequestResponse(Response response) {
        response.setFirstHeaderLine("HTTP/1.1 400 Bad Request");
        response.setContentLength(0);
    }

}
