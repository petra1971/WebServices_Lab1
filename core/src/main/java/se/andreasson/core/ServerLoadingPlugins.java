package se.andreasson.core;

import se.andreasson.spi.Json;
import se.andreasson.spi.URLHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Build with:
// mvn package
//Open Terminal and do:
// cd core\target
//On Windows Run with:
// java --module-path core-1.0-SNAPSHOT.jar;modules -m core/x.snowroller.PluginExample
//On Mac Run with:
// java --module-path core-1.0-SNAPSHOT.jar:modules -m core/x.snowroller.PluginExample

public class ServerLoadingPlugins {

    public static void main(String[] args) {

        ServiceLoader<Json> jsonServiceLoader = ServiceLoader.load(Json.class);
        for(Json jsonService : jsonServiceLoader) {
            jsonService.execute();
        }

//        ServiceLoader<URLHandler> urlHandlerServiceLoader = ServiceLoader.load(URLHandler.class);
//        for(URLHandler urlHandler : urlHandlerServiceLoader) {
//            urlHandler.handleURLs();
//        }
    }
}
