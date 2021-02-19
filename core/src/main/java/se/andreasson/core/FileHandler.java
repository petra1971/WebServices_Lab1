package se.andreasson.core;

import se.andreasson.core.model.Request;
import se.andreasson.core.model.Response;
import se.andreasson.utils.FileReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileHandler implements URLHandler {

    public Response handleURL(Request request) {
        Response response = new Response();
        try {
            File file = new File("web" + File.separator + request.getRequestUrl());
            System.out.println("Filehandler filepath: " + request.getRequestUrl());

            byte[] content = FileReader.readFromFile(file);                             //Read file contents to bit-array

            if (content.length != 0) {                                                      //Kolla om filen finns, om inte returnera felkod 404
                String contentType = Files.probeContentType(file.toPath());             //Find out content type
                response.setContent(content);
                response.setContentType(Files.probeContentType(file.toPath()));
                response.setContentLength(content.length);
                response.setStatusMessage("HTTP/1.1 200");
            } else {
                response.setStatusMessage("HTTP/1.1 400");
                response.setContentLength(0);
                String error = "Bad Request";
                response.setContent(error.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}

