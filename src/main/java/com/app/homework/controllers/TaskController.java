package com.app.homework.controllers;

import com.app.homework.dtos.TaskDto;
import com.app.homework.models.Task;
import com.app.homework.models.TaskStatus;
import com.app.homework.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("")
    public TaskDto createTask() {
        return taskService.createTask();
    }

    @GetMapping("")
    public TaskDto getNextWaitingTask() {
        return taskService.getNextWaitingTask();
    }

    @GetMapping("/{number}")
    public ResponseEntity<Task> getTaskByNumber(@PathVariable String number) {
        Task task = taskService.getTaskByNumber(number);
        if (task != null) {
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/tasks")
    public Map<TaskStatus, List<TaskDto>> getTasksByStatus() {
        return taskService.getTasksByStatus();
    }
    @PatchMapping("/{number}")
    public TaskDto updateTaskStatus(@PathVariable String number, @RequestBody Map<String, String> requestBody) {
        String newStatus = requestBody.get("newStatus");
        if (newStatus != null) {
            return taskService.updateTaskStatus(number, TaskStatus.valueOf(newStatus));
        }
        return null;
    }

    @DeleteMapping("/{number}")
    public void deleteTask(@PathVariable String number) {
        taskService.deleteTask(number);
    }
}
