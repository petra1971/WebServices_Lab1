package se.andreasson.core.model;

import java.util.Map;

public class Request {
    private String requestMethod; //egen datatyp, t ex enum
    private String requestUrl;
    private String contentType;
    private int contentLength;
    private String host;
//    private String queryString;
    private Map<String, String> queryParams;
//    private byte[] body;
    private String body;

    //Lägg in alla rader i headern, även för POST-requestn

    public Request() {}

    public Request(String requestMethod, String url,
                   String contentType, int contentLength, Map<String, String> queryParams) {
        this.requestMethod = requestMethod;
        this.requestUrl = url;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.queryParams = queryParams;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
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

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

//    public void setQueryString(String queryString) {
//        this.queryString = queryString;
//    }

    public void setRequestParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }

//    public void setBody(byte[] body) {
//        this.body = body;
//    }

//    public byte[] getBody() {

    @Override
    public String toString() {
        return "Request{" +
                "requestType='" + requestMethod + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentLength=" + contentLength +
                '}';
    }
}
