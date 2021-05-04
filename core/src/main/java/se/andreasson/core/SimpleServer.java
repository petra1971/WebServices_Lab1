package se.andreasson.core;

import se.andreasson.utils.UtilsFileReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Build with:
// mvn package
//Open Terminal and do:
// cd core\target
//On Windows Run with:
// java --module-path core-1.0-SNAPSHOT.jar;modules -m se.andreasson.core/se.andreasson.core.SimpleServer

public class SimpleServer {

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

    private static void handleConnection(Socket socket) {
        System.out.println(Thread.currentThread());
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String url = readHeaders(input);

            var output = new PrintWriter(socket.getOutputStream());

            File file = new File("web" + File.separator + url);  //Prepare file url
            byte[] content = UtilsFileReader.readFromFile(file); //Read file contents to bit-array

            output.println("HTTP/1.1 200 OK");
            output.println("Content-Length:" + content.length);
//            output.println("Content-Length:" + page.getBytes().length);
            String contentType = Files.probeContentType(file.toPath()); //Find out content type
            output.println("Content-Type:" + contentType);
//            output.println("Content-Type:text/html");
            output.println("");
//            output.print(page);
            output.flush();

            var dataOut = new BufferedOutputStream(socket.getOutputStream());
            dataOut.write(content);
            dataOut.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String readHeaders(BufferedReader input) throws IOException {
        String requestedUrl = "";
        while (true) {
            String headerLine = input.readLine();
            if (headerLine.startsWith("GET")) {
                requestedUrl = headerLine.split(" ")[1];
            }
            System.out.println(headerLine);
            if (headerLine.isEmpty())
                break;
        }
        return requestedUrl;
    }

    private static String handleProductsURL() {
        return "";
    }

    private static void handleTodosURL() {
    }
}
