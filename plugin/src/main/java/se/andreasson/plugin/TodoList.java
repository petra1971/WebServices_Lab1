package se.andreasson.plugin;

import java.util.ArrayList;
import java.util.List;

public class TodoList {

    List<se.andreasson.plugin.Todo> list = new ArrayList<se.andreasson.plugin.Todo>();

    public List<se.andreasson.plugin.Todo> getList() {
        return list;
    }

    public void addTodo(se.andreasson.plugin.Todo td) {
        list.add(td);
    }
}
