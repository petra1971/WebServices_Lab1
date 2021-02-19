import se.andreasson.spi.Json;
import se.andreasson.plugin.JsonHandler;

module se.andreasson.plugin {
    requires se.andreasson.spi;
//    requires com.google.gson;
    requires se.andreasson.utils;
    uses se.andreasson.spi.Json;
    provides Json with JsonHandler;
//    opens se.andreasson.plugin to com.google.gson;
}