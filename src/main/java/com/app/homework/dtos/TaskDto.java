package com.app.homework.dtos;

import com.app.homework.models.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class TaskDto {
    private String number;
    private TaskStatus status;
    private Map<TaskStatus, LocalDateTime> times;
}