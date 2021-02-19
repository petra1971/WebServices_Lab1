
module se.andreasson.utils {
    requires com.google.gson;
    exports se.andreasson.utils;
    opens se.andreasson.utils to se.andreasson.plugin, se.andreasson.core, com.google.gson;
}