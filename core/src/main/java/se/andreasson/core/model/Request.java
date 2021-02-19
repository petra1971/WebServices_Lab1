package se.andreasson.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private String requestMethod; //egen datatyp, t ex enum
    private String requestUrl;
    private String protocolVersion;
    private String contentType;
    private int contentLength;
    private String host;
    private String urlParameterString;
    private byte[] body;

    //Lägg in alla rader i headern, även för POST-requestn

    public Request() {}

    public Request(String requestMethod, String url, String protocolVersion,
                   String contentType, int contentLength, String urlParameterString) {
        this.requestMethod = requestMethod;
        this.requestUrl = url;
        this.protocolVersion = protocolVersion;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.urlParameterString = urlParameterString;
    }

    public Map<String, String> getParametersFromUrl2() {         //Method returning Map with parameter pairs
        System.out.println(urlParameterString);

        Map<String, String> urlParameters = new HashMap<>();

        String[] parameterPairs = this.urlParameterString.split("[&]");   //Får ut par av key och värde    //http://localhost:8080/artists?name=Petra
        for(String parameterPair : parameterPairs) {                            //Skriver jag rätt?
            String key = parameterPair.split("=")[0];                      //splitta på [=] och lägg in i en Map
            String value = parameterPair.split("=")[1];

            urlParameters.put(key, value);

            System.out.println("Key: " + key + " Value: " + value);
        }
        return urlParameters;
    }

    public List<UrlParameter> getParametersFromUrl() {  //Method returning List with UrlParameter instances
        System.out.println(urlParameterString);

        List<UrlParameter> urlParameters = new ArrayList<>();

        String[] parameterPairs = this.urlParameterString.split("[&]");   //Får ut par av key och värde    //http://localhost:8080/Order?id=2345
        for(String parameterPair : parameterPairs) {
            String key = parameterPair.split("=")[0];                      //splitta på [=] och lägg in i en Map
            String value = parameterPair.split("=")[1];

            urlParameters.add(new UrlParameter(key, value));

            System.out.println("In getParametersFromUrl method (RequestClass) Key: " + key + " Value: " + value);
        }
        return urlParameters;
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

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrlParameterString() {
        return urlParameterString;
    }

    public void setUrlParameterString(String urlParameterString) {
        this.urlParameterString = urlParameterString;
    }


    @Override
    public String toString() {
        return "Request{" +
                "requestType='" + requestMethod + '\'' +
                ", requestUrl='" + requestUrl + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentLength=" + contentLength +
                ", urlParameterString=" + urlParameterString +
                '}';
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }
}
