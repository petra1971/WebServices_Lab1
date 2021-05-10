package se.andreasson.core;

import se.andreasson.core.model.Request;
import se.andreasson.core.model.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//java -p core-1.0-SNAPSHOT.jar;plugin-1.0-SNAPSHOT.jar;spi-1.0-SNAPSHOT.jar -m se.andreasson.core/se.andreasson.core.ServerLoadingPlugins
//Build with:
// mvn package
//Open Terminal and do:
// cd core\target
//On Windows Run with:
// java --module-path core-1.0-SNAPSHOT.jar;modules -m se.andreasson.core/se.andreasson.core.SimpleServer
// java --module-path core-1.0-SNAPSHOT.jar;modules -m se.andreasson.core/se.andreasson.core.ComplexServer

public class ComplexServer {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            //TCP/IP
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println(Thread.currentThread());

            while (true) {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleConnection(Socket socket) {
        System.out.println(Thread.currentThread());

        Map<String, URLHandler> routes = new HashMap<>();
        routes.put("/artists", new ArtistHandler());            // no query string -> all artists. Query string e.g. name=Petra -> getArtistByName

        try {
            BufferedInputStream byteInput = new BufferedInputStream(socket.getInputStream());

            Request request = readRequest(byteInput);           //Läser headerns rader och avslutar vid tomrad då ev body börjar
            URLHandler handler = routes.get(request.getRequestUrl());

            if (handler == null) {                          // Ex. GET /cat.png HTTP/1.1
                handler = new FileHandler();                //Ex. POST /artists/add
            }                                               //Ex. GET /artists HTTP/1.1

            Response response = handler.handleURL(request);
            postResponse(socket, response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Request readRequest(BufferedInputStream byteInput) throws IOException {
        var request = new Request();
        while (true) {
            String headerLine = readLine(byteInput);

            if( headerLine.startsWith("GET") ||  headerLine.startsWith("POST") ||headerLine.startsWith("HEAD"))
            {
                request.setRequestMethod(headerLine.split(" ")[0]);                                   /* Ex. GET /artists?name=Petra HTTP/1.1  */
                if (headerLine.contains("?")) {
                    String requestUrl = (headerLine.split(" ")[1].split("[?]")[0]);
                    request.setRequestUrl(requestUrl);                                                      /* Ex.    /artists  */
                    String queryString = headerLine.split(" ")[1].split("[?]")[1];              /* Ex.    name=Petra   */

                    String decodedQueryString = URLDecoder.decode(queryString, "UTF-8");
                    System.out.println("Decoded quesry string: " + decodedQueryString);
                //   Lägg även till loopad hantering av flera queryParams:      if (queryString.contains("&")) {

                    if (decodedQueryString.contains("=")) {                                //splitta på [=] och lägg in i en Map
                        Map<String, String> requestParams = new HashMap<>();
                        requestParams.put(decodedQueryString.split("=")[0], decodedQueryString.split("=")[1]);
                        request.setRequestParams(requestParams);
                    }
                } else
                    request.setRequestUrl(headerLine.split(" ")[1]);        /* Ex.    /artists/add  */
            }

            if( headerLine.startsWith("Content-Length: "))
                request.setContentLength(Integer.parseInt(headerLine.split(" ")[1]));

            if( headerLine.startsWith("Content-Type: "))
                request.setContentType(headerLine.split(" ")[1]);

            if (headerLine.isEmpty())
                break;
        }

        // efter att readeHeaderlines läst tom rad är nästa tecken det första i bodyn
        if (request.getContentLength() > 0) {
            byte[] body = new byte[request.getContentLength()];

            int i = byteInput.read(body);                           //Läser bytes från byteInput och sparar i body. Returnerar storleken i en int
            String bodyText = new String(body);                     //skapar en String av body-arrayen

            request.setBody(bodyText);
            System.out.println("Actual: " + i + ", Expected: " + request.getContentLength());
            System.out.println(bodyText);
        }

        System.out.println("ReadHeaderLines. " + request);
        return request;
    }

    private static String readLine(BufferedInputStream inputStream) throws IOException {
        final int MAX_READ = 4096;
        byte[] buffer = new byte[MAX_READ];
        int bytesRead = 0;
        while (bytesRead < MAX_READ) {
            buffer[bytesRead++] = (byte) inputStream.read();
            if (buffer[bytesRead - 1] == '\r') {
                buffer[bytesRead++] = (byte) inputStream.read();
                if (buffer[bytesRead - 1] == '\n')
                    break;
            }
        }
        return new String(buffer, 0, bytesRead - 2, StandardCharsets.UTF_8);
    }

    public static void postResponse(Socket socket, Response response) {
        //Lägg detta i Response-klassen eventuellt
        try {
            var output = new PrintWriter(socket.getOutputStream());

            //Print header
            output.println(response.getFirstHeaderLine());
            output.println("Content-Length:" + response.getContentLength());
            output.println("Content-Type:" + response.getContentType());
            output.println("");
            output.flush();

            //Print body/file-contents
            if(response.getContent() != null) {
                var dataOut = new BufferedOutputStream(socket.getOutputStream());
                dataOut.write(response.getContent());
                dataOut.flush();
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

