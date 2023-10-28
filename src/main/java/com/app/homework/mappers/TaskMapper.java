package com.app.homework.mappers;

import com.app.homework.dtos.TaskDto;
import com.app.homework.models.Task;
import org.mapstruct.Mapper;

@Mapper
public interface TaskMapper {
    TaskDto toDto(Task task);
}