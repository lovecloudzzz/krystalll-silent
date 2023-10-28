package com.app.homework.services;

import com.app.homework.data.TaskDataHolder;
import com.app.homework.dtos.TaskTimeDto;
import com.app.homework.models.Task;
import com.app.homework.models.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TaskTimeService {
    private final TaskDataHolder taskDataHolder;

    public TaskTimeService(TaskDataHolder taskDataHolder) {
        this.taskDataHolder = taskDataHolder;
    }

    public Map<TaskStatus, Duration> getAverageTimeByStatusForAllTasks() {
        Map<TaskStatus, Map<String, Duration>> statusToTimesMap = new HashMap<>();

        for (Task task : taskDataHolder.getTaskMap().values()) {
            Map<String, Duration> taskTimes = calculateTimeBetweenStatuses(task.getNumber());
            for (Map.Entry<String, Duration> entry : taskTimes.entrySet()) {
                TaskStatus status = TaskStatus.valueOf(entry.getKey());
                Duration duration = entry.getValue();

                statusToTimesMap.computeIfAbsent(status, k -> new HashMap<>()).merge(task.getNumber(), duration, Duration::plus);
            }
        }

        Map<TaskStatus, Duration> result = new HashMap<>();
        for (Map.Entry<TaskStatus, Map<String, Duration>> entry : statusToTimesMap.entrySet()) {
            TaskStatus status = entry.getKey();
            Map<String, Duration> taskTimes = entry.getValue();

            long totalMillis = taskTimes.values().stream().mapToLong(Duration::toMillis).sum();
            long count = taskTimes.size();

            if (count > 0) {
                result.put(status, Duration.ofMillis(totalMillis / count));
            }
        }

        return result;
    }


    public TaskTimeDto getTaskTimeByNumber(String taskNumber) {
        TaskTimeDto taskTimeDto = new TaskTimeDto();
        taskTimeDto.setTaskNumber(taskNumber);
        taskTimeDto.setTimeMap(calculateTimeBetweenStatuses(taskNumber));
        return taskTimeDto;
    }

    public Map<String, Duration> getTaskTimeByNumberAndStatus(String taskNumber, TaskStatus status) {
        Task task = taskDataHolder.getTaskMap().get(taskNumber);
        if (task != null) {
            return calculateTimeBetweenStatuses(taskNumber);
        }
        return null;
    }

    public Map<String, Duration> getAverageTimeByStatus(TaskStatus status) {
        Map<String, Duration> result = new HashMap<>();
        int count = 0;
        long totalDuration = 0;
        for (Task task : taskDataHolder.getTaskMap().values()) {
            Map<String, Duration> times = calculateTimeBetweenStatuses(task.getNumber());
            Duration duration = times.get(status.name());
            if (duration != null) {
                totalDuration += duration.toMillis();
                count++;
            }
        }
        if (count > 0) {
            result.put(status.name(), Duration.ofMillis(totalDuration / count));
        }
        return result;
    }


    public Map<String, Duration> calculateTimeBetweenStatuses(String taskNumber) {
        Task task = taskDataHolder.getTaskMap().get(taskNumber);
        if (task != null) {
            Map<String, Duration> result = new HashMap<>();
            Map<TaskStatus, LocalDateTime> timeMap = task.getTimes();
            List<TaskStatus> statusesInOrder = new ArrayList<>();

            // Порядок сделал как в реадми
            if (timeMap.containsKey(TaskStatus.NEW)) {
                statusesInOrder.add(TaskStatus.NEW);
            }
            if (timeMap.containsKey(TaskStatus.WAITING)) {
                statusesInOrder.add(TaskStatus.WAITING);
            }
            if (timeMap.containsKey(TaskStatus.PROCESSED)) {
                statusesInOrder.add(TaskStatus.PROCESSED);
            }
            if (timeMap.containsKey(TaskStatus.CLOSE)) {
                statusesInOrder.add(TaskStatus.CLOSE);
            }
            if (timeMap.containsKey(TaskStatus.CANCEL)) {
                statusesInOrder.add(TaskStatus.CANCEL);
            }

            for (int i = 0; i < statusesInOrder.size(); i++) {
                TaskStatus currentStatus = statusesInOrder.get(i);
                LocalDateTime currentStatusTime = timeMap.get(currentStatus);
                TaskStatus nextStatus = (i == statusesInOrder.size() - 1) ? null : statusesInOrder.get(i + 1);
                LocalDateTime nextStatusTime = (nextStatus != null) ? timeMap.get(nextStatus) : LocalDateTime.now();

                if (currentStatusTime != null) {
                    if (currentStatus == TaskStatus.CANCEL || currentStatus == TaskStatus.CLOSE) {
                        LocalDateTime newStatusTime = timeMap.get(TaskStatus.NEW);
                        Duration duration = Duration.between(newStatusTime, currentStatusTime);
                        result.put(currentStatus.name(), duration);
                    } else {
                        Duration duration = Duration.between(currentStatusTime, nextStatusTime);
                        result.put(currentStatus.name(), duration);
                    }
                }
            }
            return result;
        }
        return null;
    }

}
