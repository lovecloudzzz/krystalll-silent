package com.app.homework.services;

import com.app.homework.data.TaskDataHolder;
import com.app.homework.dtos.TaskDto;
import com.app.homework.models.Task;
import com.app.homework.models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        TaskDataHolder taskDataHolder = new TaskDataHolder();

        taskService = new TaskService(taskDataHolder);
    }

    @Test
    public void createTask() {
        TaskDto taskDto = taskService.createTask();
        assertNotNull(taskDto);
        assertEquals(TaskStatus.NEW, taskDto.getStatus());
    }

    @Test
    public void getTaskByNumber() {
        TaskDto taskDto = taskService.createTask();
        String taskNumber = taskDto.getNumber();

        Task retrievedTask = taskService.getTaskByNumber(taskNumber);
        assertNotNull(retrievedTask);
        assertEquals(taskNumber, retrievedTask.getNumber());
    }

    @Test
    public void getTasksByStatus() {
        TaskDto task1 = taskService.createTask();
        TaskDto task2 = taskService.createTask();
        taskService.updateTaskStatus(task1.getNumber(), TaskStatus.WAITING);
        taskService.updateTaskStatus(task2.getNumber(), TaskStatus.WAITING);
        taskService.updateTaskStatus(task1.getNumber(), TaskStatus.PROCESSED);

        Map<TaskStatus, List<TaskDto>> taskStatusMap = taskService.getTasksByStatus();
        assertNotNull(taskStatusMap);

        List<TaskDto> newTasks = taskStatusMap.get(TaskStatus.NEW);
        List<TaskDto> waitingTasks = taskStatusMap.get(TaskStatus.WAITING);
        List<TaskDto> processedTasks = taskStatusMap.get(TaskStatus.PROCESSED);
        List<TaskDto> closeTasks = taskStatusMap.get(TaskStatus.CLOSE);
        List<TaskDto> cancelTasks = taskStatusMap.get(TaskStatus.CANCEL);

        assertNotNull(newTasks);
        assertNotNull(waitingTasks);
        assertNotNull(processedTasks);
        assertNull(closeTasks);
        assertNotNull(cancelTasks);

        assertEquals(0, newTasks.size());
        assertEquals(1, waitingTasks.size());
        assertEquals(1, processedTasks.size());
        assertEquals(0, cancelTasks.size());
    }


    @Test
    public void updateTaskStatus() {
        TaskDto taskDto = taskService.createTask();
        String taskNumber = taskDto.getNumber();

        TaskDto updatedTask = taskService.updateTaskStatus(taskNumber, TaskStatus.WAITING);
        assertNotNull(updatedTask);
        assertEquals(TaskStatus.WAITING, updatedTask.getStatus());
    }

    @Test
    public void getNextWaitingTask() {
        TaskDto task1 = taskService.createTask();

        taskService.updateTaskStatus(task1.getNumber(), TaskStatus.WAITING);

        TaskDto nextWaitingTask = taskService.getNextWaitingTask();
        assertNotNull(nextWaitingTask);
        assertEquals(TaskStatus.WAITING, nextWaitingTask.getStatus());
    }

    @Test
    public void deleteTask() {
        TaskDto taskDto = taskService.createTask();
        String taskNumber = taskDto.getNumber();

        Task retrievedTask = taskService.getTaskByNumber(taskNumber);
        assertNotNull(retrievedTask);

        taskService.deleteTask(taskNumber);

        retrievedTask = taskService.getTaskByNumber(taskNumber);
        assertNull(retrievedTask);
    }

    @Test
    public void checkTaskStatusAutoCancel() {
        TaskDto taskDto = taskService.createTask();
        String taskNumber = taskDto.getNumber();

        Task task = taskService.getTaskByNumber(taskNumber);

        LocalDateTime creationTime = task.getTimes().get(TaskStatus.NEW);
        task.getTimes().put(TaskStatus.NEW, creationTime.minusMinutes(31));

        taskService.checkTaskStatus();

        Task retrievedTask = taskService.getTaskByNumber(taskNumber);
        assertNotNull(retrievedTask);
        assertEquals(TaskStatus.CANCEL, retrievedTask.getStatus());
    }


}
