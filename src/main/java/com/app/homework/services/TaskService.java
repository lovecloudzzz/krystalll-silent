package com.app.homework.services;

import com.app.homework.data.TaskDataHolder;
import com.app.homework.dtos.TaskDto;
import com.app.homework.mappers.TaskMapper;
import com.app.homework.models.Task;
import com.app.homework.models.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;


@Service
@Slf4j
public class TaskService {
    private final Queue<Task> taskQueue = new ConcurrentLinkedQueue<>();
    private final TaskDataHolder taskDataHolder;
    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    public TaskService(TaskDataHolder taskDataHolder) {
        this.taskDataHolder = taskDataHolder;
    }

    public TaskDto createTask() {
        Task task = new Task();
        task.setNumber(generateUniqueNumber());
        task.setStatus(TaskStatus.NEW);
        task.setTimes(new HashMap<>());
        task.getTimes().put(TaskStatus.NEW, LocalDateTime.now());
        taskQueue.offer(task);
        taskDataHolder.getTaskMap().put(task.getNumber(), task);
        log.info("Created task: {}", task.getNumber());
        return taskMapper.toDto(task);
    }

    public Task getTaskByNumber(String number) {
        Task task = taskDataHolder.getTaskMap().get(number);
        if (task != null) {
            LocalDateTime creationTime = task.getTimes().get(TaskStatus.NEW);
            if (creationTime != null && creationTime.plusMinutes(30).isBefore(LocalDateTime.now())) {
                task.setStatus(TaskStatus.CANCEL);
                log.info("Task {} auto-canceled due to timeout", task.getNumber());
            }
        }

        return task;
    }


    public Map<TaskStatus, List<TaskDto> > getTasksByStatus() {
        Map<TaskStatus, List<TaskDto>> taskStatusMap = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            if (status != TaskStatus.CLOSE) {
                List<TaskDto> tasks = new ArrayList<>();
                for (Task task : taskQueue) {
                    if (task.getStatus() == status) {
                        tasks.add(taskMapper.toDto(task));
                    }
                }
                taskStatusMap.put(status, tasks);
            }
        }
        return taskStatusMap;
    }

    public TaskDto updateTaskStatus(String number, TaskStatus newStatus) {
        Task task = taskDataHolder.getTaskMap().get(number);
        if (task != null) {
            TaskStatus currentStatus = task.getStatus();

            if (currentStatus == TaskStatus.CLOSE) {
                return taskMapper.toDto(task);
            }

            if (isStatusTransitionValid(currentStatus, newStatus)) {
                HashMap<TaskStatus, LocalDateTime> times = task.getTimes();
                times.put(newStatus, LocalDateTime.now());
                task.setTimes(times);
                task.setStatus(newStatus);

                if (newStatus == TaskStatus.WAITING && currentStatus == TaskStatus.PROCESSED) {
                    taskQueue.offer(task);
                }

                log.info("Updated task status: {} -> {}", number, newStatus);
                return taskMapper.toDto(task);
            }
        }
        return null;
    }

    private boolean isStatusTransitionValid(TaskStatus currentStatus, TaskStatus newStatus) {
        if (currentStatus == TaskStatus.NEW) {
            return newStatus == TaskStatus.WAITING || newStatus == TaskStatus.CANCEL;
        } else if (currentStatus == TaskStatus.WAITING) {
            return newStatus == TaskStatus.PROCESSED || newStatus == TaskStatus.CANCEL;
        } else if (currentStatus == TaskStatus.PROCESSED) {
            return newStatus == TaskStatus.CLOSE || newStatus == TaskStatus.WAITING || newStatus == TaskStatus.CANCEL;
        }
        return false;
    }

    public TaskDto getNextWaitingTask() {
        for (Task task : taskQueue) {
            if (task.getStatus() == TaskStatus.WAITING) {
                return taskMapper.toDto(task);
            }
        }
        return null;
    }

    public void deleteTask(String number) {
        Task task = taskDataHolder.getTaskMap().remove(number);
        if (task != null) {
            taskQueue.remove(task);
            log.info("Deleted task: {}", number);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    public void checkTaskStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        for (Task task : taskQueue) {
            if (task.getStatus() == TaskStatus.NEW) {
                LocalDateTime creationTime = task.getTimes().get(TaskStatus.NEW);
                if (creationTime != null) {
                    Duration duration = Duration.between(creationTime, currentTime);
                    if (duration.toMinutes() >= 30) {
                        task.setStatus(TaskStatus.CANCEL);
                        log.info("Task {} auto-canceled due to timeout", task.getNumber());
                    }
                }
            }
        }
    }


    private String generateUniqueNumber() {
        String number;
        do {
            number = "TASK-" + UUID.randomUUID();
        } while (taskDataHolder.getTaskMap().containsKey(number));
        return number;
    }
}
