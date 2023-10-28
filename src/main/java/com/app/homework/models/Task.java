package com.app.homework.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class Task {
    private String number;
    private TaskStatus status;
    @Getter
    private HashMap<TaskStatus, LocalDateTime> times;

    public Map<String, LocalDateTime> getTimesAsMap() {
        Map<String, LocalDateTime> timesMap = new HashMap<>();
        for (Map.Entry<TaskStatus, LocalDateTime> entry : times.entrySet()) {
            timesMap.put(entry.getKey().name(), entry.getValue());
        }
        return timesMap;
    }

    public void setTimes(Map<TaskStatus, LocalDateTime> times) {
        if (times instanceof HashMap) {
            this.times = (HashMap<TaskStatus, LocalDateTime>) times;
        } else {
            this.times = new HashMap<>(times);
        }
    }

}
