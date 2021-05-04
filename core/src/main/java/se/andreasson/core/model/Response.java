package se.andreasson.core.model;

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
}
