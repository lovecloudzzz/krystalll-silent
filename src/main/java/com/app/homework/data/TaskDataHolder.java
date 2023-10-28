package com.app.homework.data;

import com.app.homework.models.Task;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskDataHolder {
    private final Map<String, Task> taskMap = new ConcurrentHashMap<>();

    public Map<String, Task> getTaskMap() {
        return taskMap;
    }
}
