package com.taskmanager.dto;

import lombok.Getter;

@Getter
public enum Priority {
    LOW("low", 0),
    MEDIUM("medium", 1),
    HIGH("high", 2);

    private String priority;
    private int priorityKey;
    Priority(String priority, int priorityKey) {
        this.priority = priority;
        this.priorityKey = priorityKey;
    }
}
