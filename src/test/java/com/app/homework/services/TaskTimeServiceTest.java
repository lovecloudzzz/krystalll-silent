package com.app.homework.services;

import com.app.homework.data.TaskDataHolder;
import com.app.homework.dtos.TaskTimeDto;
import com.app.homework.models.Task;
import com.app.homework.models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaskTimeServiceTest {
    // Сделал  сравнение с погрешностью(с пол секундой, так как обычно все было +- четь меньше) так как время разное всегда, и тесты выдают ошибку

    private TaskTimeService taskTimeService;
    private TaskDataHolder taskDataHolder;

    @BeforeEach
    public void setUp() {
        taskDataHolder = Mockito.mock(TaskDataHolder.class);
        taskTimeService = new TaskTimeService(taskDataHolder);
    }

    @Test
    public void getAverageTimeByStatusForAllTasks() {
        Task task1 = new Task();
        task1.setNumber("TEST-1");
        task1.setStatus(TaskStatus.NEW);
        Map<TaskStatus, LocalDateTime> times1 = new HashMap<>();
        LocalDateTime creationTime1 = LocalDateTime.now();
        times1.put(TaskStatus.NEW, creationTime1);
        task1.setTimes(times1);

        Task task2 = new Task();
        task2.setNumber("TEST-2");
        task2.setStatus(TaskStatus.NEW);
        Map<TaskStatus, LocalDateTime> times2 = new HashMap<>();
        LocalDateTime creationTime2 = LocalDateTime.now();
        times2.put(TaskStatus.NEW, creationTime2);
        task2.setTimes(times2);

        Map<String, Task> taskMap = new HashMap<>();
        taskMap.put("TEST-1", task1);
        taskMap.put("TEST-2", task2);
        Mockito.when(taskDataHolder.getTaskMap()).thenReturn(taskMap);

        Map<TaskStatus, Duration> result = taskTimeService.getAverageTimeByStatusForAllTasks();

        assertNotNull(result);
        assertEquals(1, result.size());

        assertTrue(result.get(TaskStatus.NEW).toMillis() < 500);
    }

    @Test
    public void getTaskTimeByNumber() {
        Task task = new Task();
        task.setNumber("TEST-1");
        task.setStatus(TaskStatus.NEW);
        Map<TaskStatus, LocalDateTime> times = new HashMap<>();
        LocalDateTime creationTime = LocalDateTime.now();
        times.put(TaskStatus.NEW, creationTime);
        task.setTimes(times);

        Map<String, Task> taskMap = new HashMap<>();
        taskMap.put("TEST-1", task);
        Mockito.when(taskDataHolder.getTaskMap()).thenReturn(taskMap);

        TaskTimeDto result = taskTimeService.getTaskTimeByNumber("TEST-1");

        assertNotNull(result);
        assertEquals("TEST-1", result.getTaskNumber());
        assertEquals(1, result.getTimeMap().size());

        assertTrue(result.getTimeMap().get(TaskStatus.NEW.name()).toMillis() < 500);
    }

    @Test
    public void getTaskTimeByNumberAndStatus() {
        Task task = new Task();
        task.setNumber("TEST-1");
        task.setStatus(TaskStatus.NEW);
        Map<TaskStatus, LocalDateTime> times = new HashMap<>();
        LocalDateTime creationTime = LocalDateTime.now();
        times.put(TaskStatus.NEW, creationTime);
        task.setTimes(times);

        Map<String, Task> taskMap = new HashMap<>();
        taskMap.put("TEST-1", task);
        Mockito.when(taskDataHolder.getTaskMap()).thenReturn(taskMap);

        Map<String, Duration> result = taskTimeService.getTaskTimeByNumberAndStatus("TEST-1", TaskStatus.NEW);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertTrue(result.get(TaskStatus.NEW.name()).toMillis() < 500);
    }

    @Test
    public void getAverageTimeByStatus() {
        Task task1 = new Task();
        task1.setNumber("TEST-1");
        task1.setStatus(TaskStatus.NEW);
        Map<TaskStatus, LocalDateTime> times1 = new HashMap<>();
        LocalDateTime creationTime1 = LocalDateTime.now();
        times1.put(TaskStatus.NEW, creationTime1);
        task1.setTimes(times1);

        Task task2 = new Task();
        task2.setNumber("TEST-2");
        task2.setStatus(TaskStatus.NEW);
        Map<TaskStatus, LocalDateTime> times2 = new HashMap<>();
        LocalDateTime creationTime2 = LocalDateTime.now();
        times2.put(TaskStatus.NEW, creationTime2);
        task2.setTimes(times2);

        Map<String, Task> taskMap = new HashMap<>();
        taskMap.put("TEST-1", task1);
        taskMap.put("TEST-2", task2);
        Mockito.when(taskDataHolder.getTaskMap()).thenReturn(taskMap);

        Map<String, Duration> result = taskTimeService.getAverageTimeByStatus(TaskStatus.NEW);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertTrue(result.get(TaskStatus.NEW.name()).toMillis() < 500);
    }

    @Test
    public void getAverageTimeByStatusWhenNoTasks() {
        Map<String, Task> taskMap = new HashMap<>();
        Mockito.when(taskDataHolder.getTaskMap()).thenReturn(taskMap);

        Map<String, Duration> result = taskTimeService.getAverageTimeByStatus(TaskStatus.NEW);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

}
