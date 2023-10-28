package com.app.homework.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskStatus {
    NEW("New"), WAITING("Waiting"), PROCESSED("Processed"), CLOSE("Close"), CANCEL("Cancel");

    private final String displayName;
}