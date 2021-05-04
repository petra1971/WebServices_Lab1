package se.andreasson.core;

import se.andreasson.core.model.Request;
import se.andreasson.core.model.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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

    //Eventuellt skapa en Header-klass...
    public static void handleConnection(Socket socket) {
        System.out.println(Thread.currentThread());

        Map<String, URLHandler> routes = new HashMap<>();
        routes.put("/artists", new ArtistHandler());            //no query string -> all artists. Query string e.g. name=Petra -> getArtistByName
        routes.put("/artists/add", new ArtistHandler());

        try {
            BufferedInputStream byteInput = new BufferedInputStream(socket.getInputStream());

            Request request = readHeaderLines(byteInput);           //Läser headerns rader och avslutar vid tomrad då ev body börjar

            if (request.getRequestMethod().equals("GET") || (request.getRequestMethod().equals("POST"))) {    // Ex. GET /cat.png HTTP/1.1
                URLHandler handler = routes.get(request.getRequestUrl());                                     //Ex. GET /artists HTTP/1.1
                                                                                                              //Ex. GET /artists?name=Petra
                if (handler == null) {                                                                          //Ex. POST /artists/add
                    handler = new FileHandler();
                }
                Response response = handler.handleURL(request);
                postResponse(socket, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Request readHeaderLines(BufferedInputStream byteInput) throws IOException {
        var request = new Request();
        while (true) {
            String headerLine = readLine(byteInput);

            if( headerLine.startsWith("GET") ||  headerLine.startsWith("POST"))  /*||headerLine.startsWith("HEAD") */
            {
                request.setRequestMethod(headerLine.split(" ")[0]);                                   /* Ex. GET /artists?name=Petra HTTP/1.1  */
                if (headerLine.contains("?")) {
                    String requestUrl = (headerLine.split(" ")[1].split("[?]")[0]);
                    request.setRequestUrl(requestUrl);                                                      /* Ex.    /artists  */
                    String queryString = headerLine.split(" ")[1].split("[?]")[1];              /* Ex.    name=Petra   */

                    if (queryString.contains("=")) {                                //splitta på [=] och lägg in i en Map
                        Map <String, String> queryParameters = new HashMap<>();
                        queryParameters.put(queryString.split("=")[0], queryString.split("=")[1]);
                        request.setQueryParameters(queryParameters);
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
        if( request.getRequestMethod().equals("POST") && request.getRequestUrl().contains("/add")) {

                    //Read body.
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

