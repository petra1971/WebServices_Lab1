package se.andreasson.core.model;

import se.andreasson.utils.JsonConverter;

public class Response {
    private byte[] content = new byte[0];
    private String contentType = "";
    private int contentLength;
    private String firstHeaderLine = ""; //200, 400, 404

    public Response() {
    }

    public Response(byte[] content, String contentType, int contentLength) {
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.content = content;
    }

    public byte[] getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFirstHeaderLine() {
        return firstHeaderLine;
    }

    public void setFirstHeaderLine(String firstHeaderLine) {
        this.firstHeaderLine = firstHeaderLine;
    }

    public void setNotFoundResponse() {
        setFirstHeaderLine("HTTP/1.1 404 Not Found");
        setContentLength(0);
    }

    public void setCreatedResponse() {
        setFirstHeaderLine("HTTP/1.1 201 Created");
        setContentLength(0);
    }

    public void setBadRequestResponse() {
        setFirstHeaderLine("HTTP/1.1 400 Bad Request");
        setContentLength(0);
    }

    // <T> talar om att det är generisk metod
    public <T> void setJsonResponse(T data) {
        JsonConverter jsonConverter = new JsonConverter();
        var jsonResponse = jsonConverter.convertToJson(data);
        byte[] jsonBytes = jsonResponse.getBytes();
        setContentType("application/json");          //ska det vara nån annan typ?
        setContentLength(jsonBytes.length);
        setContent(jsonBytes);
        setFirstHeaderLine("HTTP/1.1 200 OK");
    }

    public <T> void setEmptyJsonResponse(T data) {
        JsonConverter jsonConverter = new JsonConverter();
        var jsonResponse = jsonConverter.convertToJson(data);
        byte[] jsonBytes = jsonResponse.getBytes();
        setContentType("application/json");          //ska det vara nån annan typ?
        setContentLength(jsonBytes.length);
        setFirstHeaderLine("HTTP/1.1 200 OK");
    }
}
