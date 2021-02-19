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
                executorService.execute(() -> handleRequest(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Eventuellt skapa en Header-klass...
    public static void handleRequest(Socket socket) {

        Map<String, URLHandler> routes = new HashMap<>();
        routes.put("/artists", new ArtistHandler());                  //http://localhost:8080/artists?name=Petra

        try {
            BufferedInputStream byteInput = new BufferedInputStream(socket.getInputStream());

            Request request = readHttpRequest(byteInput);                                                       //Läser headerns rader och avslutar vid tomrad då ev body börjar

            if (request.getRequestMethod().equals("GET") || (request.getRequestMethod().equals("POST"))) {
                URLHandler handler = routes.get(request.getRequestUrl());

                if (handler == null) {
                    handler = new FileHandler();
                }
                Response response = handler.handleURL(request);
                postHttpResponse(socket, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Request readHttpRequest(BufferedInputStream byteInput) throws IOException {
        Request request = new Request();
        while (true) {
            String headerLine = readLine(byteInput);              //Läser en rad i taget med Martins byte-metod

            if (headerLine.isEmpty())
                break;

            if (isFirstheaderline(headerLine)) {                  //Regel: Om if, switch eller while, det ska vara det första som finns i en metod
                System.out.println("ReadHttpRequest:" + headerLine);
                parseFirstheaderline(request, headerLine);
            }
            else {
                System.out.println("ReadHttpRequest:" + headerLine);
                parseHeaderline(request, headerLine);
            }
            System.out.println(headerLine);
        }                                                              // efter att readeHeaderlines läst tom rad är nästa tecken det första i bodyn
        request.setBody(parseBody(byteInput, request.getRequestMethod(), request.getContentLength()));
        return request;
    }

//    private static Request readHeaders(BufferedInputStream input) throws IOException {
//        var request = new Request();
//        while (true) {
//            String headerLine = readLine(input);
//            if( headerLine.startsWith("GET")||headerLine.startsWith("POST")||headerLine.startsWith("HEAD") )
//            {
//                request.requestType = headerLine.split(" ")[0];
//                request.requestedUrl = headerLine.split(" ")[1];
//                request.requestedUrl = request.requestedUrl.split("[?]")[0];
//            }
//            if( headerLine.startsWith("Content-Length: "))
//            {
//                request.length = Integer.parseInt(headerLine.split(" ")[1]);
//            }
//            System.out.println(headerLine);
//            if (headerLine.isEmpty())
//                break;
//        }
//        return request;
//    }
//
//    if( request.requestType.equals("POST") && request.requestedUrl.equals("/upload"))
//        {
//            //Read body.
//            byte[] body = new byte[request.length];
//            int i = input.read(body);
//            System.out.println("Actual: " + i + ", Expected: " + request.length);
//            String bodyText = new String(body);
//            System.out.println(bodyText);
//            //Put to database
//        }

    private static byte[] parseBody(BufferedInputStream input, String requestMethod, int contentLength) throws IOException {
        byte[] body = new byte[0];                                          //Bättre att initiera med 0-array än null
        if(requestMethod.equals("POST")) {
            body = new byte[contentLength];
            int i = input.read(body);
            System.out.println("ParseBody. Actual: " + i + ", Expected: " + contentLength);
//            input.read(body);
            String bodyText = new String(body);
            System.out.println(bodyText);
            //read läser från input och lägger i body. Returnerar hur mkt den lyckas läsa
        }
        return body;
    }

    private static boolean isFirstheaderline(String headerLine) {
        return headerLine.startsWith("GET") || headerLine.startsWith("POST") || headerLine.startsWith("HEAD") ||
                headerLine.startsWith("PUT") || headerLine.startsWith("DELETE");
    }

    private static void parseFirstheaderline(Request request, String headerLine) {          //if url.contains(["[?]" ta del nummer [1]])
        String[] firstLine = headerLine.split(" ");
        request.setRequestMethod(firstLine[0]);
        String[] url = firstLine[1].split("[?]");                                   //request.requestedUrl.split("[?]")[0];
        request.setRequestUrl(url[0]);
        if(url.length > 1) {
            request.setUrlParameterString(url[1]);
        }
        System.out.println("ParseFirstHeaderline. Url:" + request.getRequestUrl() + " " + request.getUrlParameterString());
    }


    private static void parseHeaderline(Request request, String headerLine) {
        if (headerLine.startsWith("Content-Length: ")) {                                     //http://localhost:8080/Order?id=2345
            request.setContentLength(Integer.parseInt(headerLine.split(" ")[1]));
            System.out.println("parseHeaderline-Content-length."+ request.getContentLength());
        } else if (headerLine.startsWith("Content-Type: ")) {
            request.setContentType(headerLine.split(" ")[1]);
            System.out.println("parseHeaderline-Content-Type." +request.getContentType());
        }
    }

    public static String readLine(BufferedInputStream inputStream) throws IOException {
        final int MAX_READ = 4096;
        byte[] buffer = new byte[MAX_READ];
        int bytesRead = 0;
        while (bytesRead < MAX_READ) {
            buffer[bytesRead++] = (byte) inputStream.read();
            if (buffer[bytesRead - 1] == '\r') {
                buffer[bytesRead++] = (byte) inputStream.read();
                if( buffer[bytesRead - 1] == '\n')
                    break;
            }
        }
        return new String(buffer,0,bytesRead-2, StandardCharsets.UTF_8);
    }

    public static void postHttpResponse(Socket socket, Response response) {
        //Lägg detta i Response-klassen eventuellt
        try {
            var output = new PrintWriter(socket.getOutputStream());

            //Print header
            output.println(response.getStatusMessage());
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

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
