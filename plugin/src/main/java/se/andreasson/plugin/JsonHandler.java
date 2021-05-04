package se.andreasson.plugin;

import se.andreasson.spi.Json;
import se.andreasson.utils.JsonConverter;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JsonHandler implements Json {

    @Override
    public void execute() {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println(Thread.currentThread());

            while(true) {
                Socket socket = serverSocket.accept();
                //Instantiating and executing threads to handle client requests
                executorService.execute(()-> handleConnection(socket));
            }
        } catch (IOException e) {
            System.out.println("Error in main");
            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket socket) {

        //------- Handle client input/request from client
        System.out.println(Thread.currentThread());

        try {
            InputStream ins = socket.getInputStream();
            InputStreamReader insr = new InputStreamReader(ins);
            BufferedReader inputString = new BufferedReader(insr);

            while (true) {
                String headerLine = inputString.readLine();
                System.out.println(headerLine);
                System.out.println("read header line");
                if (headerLine.isEmpty())
                    break;
            }

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter output = new PrintWriter(outputStream);

            //Something to post, the body
            String json = createJsonResponse();

            //Writing header and body to client socket output stream
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Length:" + json.getBytes().length);
            output.println("Content-Type:application/json");
            output.println("");
            output.println(json);

            output.flush();
            socket.close();

        } catch (IOException e) {
            System.out.println("Error in ManageClientConnection");
            e.printStackTrace();
        }
    }

    public static String createJsonResponse() {
        TodoList todoList = fillTodoListWithTodos();
        JsonConverter jsonConverter = new JsonConverter();
        return jsonConverter.convertToJson(todoList);
    }

    public static TodoList fillTodoListWithTodos() {

        TodoList todoList = new TodoList();
        todoList.list.add(new Todo(1, "Study WS", false));
        todoList.list.add(new Todo(2, "Study JavaIntro", false));
        return todoList;
    }
}
