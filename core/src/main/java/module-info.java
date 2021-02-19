
module se.andreasson.core {

    requires java.persistence;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires com.fasterxml.classmate;
    requires net.bytebuddy;
    requires java.xml.bind;
    requires com.google.gson;
    requires org.apache.commons.io;
    requires se.andreasson.utils;
    requires se.andreasson.spi;
    uses se.andreasson.spi.Json;

    opens se.andreasson.core to com.google.gson, org.hibernate.orm.core;
    opens se.andreasson.core.model to org.hibernate.orm.core, com.google.gson;
}
