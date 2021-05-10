package se.andreasson.core;

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
            handleGetRequest(request, artistDAO, response, artists);
        }
        else if (request.getRequestMethod().equals("HEAD")) {
            handleHeadRequest(request, artistDAO, response, artists);
        }
        else if (request.getRequestMethod().equals("POST")) {
            handlePostRequest(request, artistDAO, response);
        }
        else
            response.setBadRequestResponse();

        return response;
    }

    private void handleHeadRequest(Request request, ArtistDAO artistDAO, Response response, List<Artist> artists) {
        if (request.getQueryParams() == null)         // -> no query parameters (no '?')
        {
            artists = artistDAO.getAllArtists();
            System.out.println("handleURL. Get all artists" + artists.toString());
        }
        else if (request.getQueryParams().containsKey("name"))   //E. Query string: name=Petra
        {
            System.out.println("handleURL. QueryParameters: " + request.getQueryParams().get("name"));
            String artistName = request.getQueryParams().get("name");
            System.out.println("handleUrl. Artist name:" + artistName);
            artists = artistDAO.getByName(artistName);
        }
        if (!artists.isEmpty())
        {
            response.setEmptyJsonResponse(artists);
        }
        else {
            response.setNotFoundResponse();
        }
    }

    private void handleGetRequest(Request request, ArtistDAO artistDAO, Response response, List<Artist> artists) {
        if (request.getQueryParams() == null)         // -> no query parameters (no '?')
        {
            artists = artistDAO.getAllArtists();
            System.out.println("handleURL. Get all artists" + artists.toString());
        }
        else if (request.getQueryParams().containsKey("name"))   //E. Query string: name=Petra
        {
            System.out.println("handleURL. QueryParameters: " + request.getQueryParams().get("name"));
            String artistName = request.getQueryParams().get("name");
            System.out.println("handleUrl. Artist name:" + artistName);
            artists = artistDAO.getByName(artistName);
        }
        if (!artists.isEmpty())
        {
            response.setJsonResponse(artists);
        }
        else {
            response.setNotFoundResponse();
        }
    }

    private void handlePostRequest(Request request, ArtistDAO artistDAO, Response response) {
        if (request.getContentType().equals("application/json")) {
            try {
                handlePostJsonRequest(request, artistDAO, response);
            } catch (Exception e) {
                System.out.println("Cannot add posted artist to DB. " + e);
                response.setBadRequestResponse();
            }
        }
    }
    private void handlePostJsonRequest(Request request, ArtistDAO artistDAO, Response response) {
        //Make artist objects from json string in the body
        String bodyText = request.getBody();                        //String body = IOUtils.toString(request.getBody(), "StandardCharsets.UTF_8");
        System.out.println("Artisthandler. BodyText. " + bodyText);

        JsonConverter converter = new JsonConverter();
        Artist artist = converter.convertToObject(bodyText, Artist.class);                   //Jag skulle vilja flytta detta till jsonConverter men blir svårt om jag vill ha det generiskt
        System.out.println("Artist from Json-input: " + artist.toString());

        artistDAO.create(artist);
        response.setCreatedResponse();
    }
}
