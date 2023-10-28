package com.app.homework.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class TaskTimeDto {
    private String taskNumber;
    private Map<String, Duration> timeMap;
}