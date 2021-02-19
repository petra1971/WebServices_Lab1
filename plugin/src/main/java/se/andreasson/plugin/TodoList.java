package se.andreasson.plugin;

import java.util.ArrayList;
import java.util.List;

public class TodoList {

    List<Todo> list = new ArrayList<>();

    public List<Todo> getList() {
        return list;
    }

    public void addTodo(Todo td) {
        list.add(td);
    }
}
