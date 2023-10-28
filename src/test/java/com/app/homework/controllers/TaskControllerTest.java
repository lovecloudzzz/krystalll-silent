package com.app.homework.controllers;

import com.app.homework.dtos.TaskDto;
import com.app.homework.models.TaskStatus;
import com.app.homework.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TaskControllerTest {

    private TaskController taskController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        TaskService taskService = Mockito.mock(TaskService.class);
        taskController = new TaskController(taskService);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    public void createTaskTest() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setNumber("TASK-1");
        taskDto.setStatus(TaskStatus.NEW);

        Mockito.when(taskController.createTask()).thenReturn(taskDto);

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("TASK-1"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    public void getNextWaitingTaskTest() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setNumber("TASK-2");
        taskDto.setStatus(TaskStatus.WAITING);

        Mockito.when(taskController.getNextWaitingTask()).thenReturn(taskDto);

        mockMvc.perform(get("/task")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("TASK-2"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void getTaskByNumberTest() throws Exception {
        Mockito.when(taskController.getTaskByNumber("TASK-3")).thenReturn(null);

        mockMvc.perform(get("/task/TASK-3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void updateTaskStatusTest() throws Exception {
        TaskDto taskDto = new TaskDto();
        taskDto.setNumber("TASK-4");
        taskDto.setStatus(TaskStatus.WAITING);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newStatus", "PROCESSED");

        Mockito.when(taskController.updateTaskStatus("TASK-4", requestBody)).thenReturn(taskDto);

        mockMvc.perform(patch("/task/TASK-4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newStatus\": \"PROCESSED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("TASK-4"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    public void deleteTaskTest() throws Exception {
        mockMvc.perform(delete("/task/TASK-5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
