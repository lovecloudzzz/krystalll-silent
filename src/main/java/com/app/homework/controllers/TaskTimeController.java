package com.app.homework.controllers;

import com.app.homework.dtos.TaskTimeDto;
import com.app.homework.models.TaskStatus;
import com.app.homework.services.TaskTimeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/times")
@AllArgsConstructor
public class TaskTimeController {
    private final TaskTimeService taskTimeService;

    @GetMapping("")
    public ResponseEntity<Map<TaskStatus, Duration>> getTaskTimes() {
        Map<TaskStatus, Duration> averageTimes = taskTimeService.getAverageTimeByStatusForAllTasks();
        return ResponseEntity.ok(averageTimes);
    }

    @GetMapping("/{taskNumber}")
    public TaskTimeDto getTaskTimeByNumber(@PathVariable String taskNumber) {
        return taskTimeService.getTaskTimeByNumber(taskNumber);
    }

    @GetMapping("/{taskNumber}/{status}")
    public Map<String, Duration> getTaskTimeByNumberAndStatus(@PathVariable String taskNumber, @PathVariable TaskStatus status) {
        return taskTimeService.getTaskTimeByNumberAndStatus(taskNumber, status);
    }

    @GetMapping("/status/{status}")
    public Map<String, Duration> getAverageTimeByStatus(@PathVariable TaskStatus status) {
        return taskTimeService.getAverageTimeByStatus(status);
    }
}
