package com.app.homework.controllers;

import com.app.homework.dtos.TaskTimeDto;
import com.app.homework.models.TaskStatus;
import com.app.homework.services.TaskTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskTimeControllerTest {

    private TaskTimeController taskTimeController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        TaskTimeService taskTimeService = Mockito.mock(TaskTimeService.class);
        taskTimeController = new TaskTimeController(taskTimeService);
        mockMvc = MockMvcBuilders.standaloneSetup(taskTimeController).build();
    }

    @Test
    public void getTaskTimesTest() throws Exception {
        Map<TaskStatus, Duration> averageTimes = new HashMap<>();
        averageTimes.put(TaskStatus.NEW, Duration.ofMinutes(10));
        averageTimes.put(TaskStatus.WAITING, Duration.ofMinutes(15));

        TaskTimeService taskTimeService = Mockito.mock(TaskTimeService.class);

        Mockito.when(taskTimeService.getAverageTimeByStatusForAllTasks()).thenReturn(averageTimes);

        TaskTimeController taskTimeController = new TaskTimeController(taskTimeService);

        mockMvc = MockMvcBuilders.standaloneSetup(taskTimeController).build();

        mockMvc.perform(get("/times")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.NEW").value("600.0"))
                .andExpect(jsonPath("$.WAITING").value("900.0"));
    }




    @Test
    public void getTaskTimeByNumberTest() throws Exception {
        TaskTimeDto taskTimeDto = new TaskTimeDto();
        taskTimeDto.setTaskNumber("TASK-1");
        taskTimeDto.setTimeMap(new HashMap<>());

        Mockito.when(taskTimeController.getTaskTimeByNumber("TASK-1")).thenReturn(taskTimeDto);

        mockMvc.perform(get("/times/TASK-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskNumber").value("TASK-1"))
                .andExpect(jsonPath("$.timeMap").isEmpty());
    }

    @Test
    public void getTaskTimeByNumberAndStatusTest() throws Exception {
        Map<String, Duration> timeMap = new HashMap<>();
        timeMap.put(TaskStatus.NEW.name(), Duration.ofMinutes(10));
        timeMap.put(TaskStatus.WAITING.name(), Duration.ofMinutes(15));

        Mockito.when(taskTimeController.getTaskTimeByNumberAndStatus("TASK-1", TaskStatus.NEW)).thenReturn(timeMap);

        mockMvc.perform(get("/times/TASK-1/NEW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("NEW").value("600.0"))
                .andExpect(jsonPath("WAITING").value("900.0"));
    }

    @Test
    public void getAverageTimeByStatusTest() throws Exception {
        Map<String, Duration> timeMap = new HashMap<>();
        timeMap.put(TaskStatus.NEW.name(), Duration.ofMinutes(10));
        timeMap.put(TaskStatus.WAITING.name(), Duration.ofMinutes(15));

        Mockito.when(taskTimeController.getAverageTimeByStatus(TaskStatus.NEW)).thenReturn(timeMap);

        mockMvc.perform(get("/times/status/NEW")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("NEW").value("600.0"))
                .andExpect(jsonPath("WAITING").value("900.0"));
    }
}
