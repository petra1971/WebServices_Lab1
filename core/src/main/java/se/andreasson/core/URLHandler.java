package se.andreasson.core;

import se.andreasson.core.model.Request;
import se.andreasson.core.model.Response;

@FunctionalInterface
public interface URLHandler {

    Response handleURL(Request request);

}