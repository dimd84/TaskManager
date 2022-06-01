package com.taskmanager.dto;

import com.taskmanager.services.TaskManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class Process {
    @NotNull(message = "PID must be a long value")
    private  long pid;
    @NotNull
    private  Priority priority;

    public Process(long pid, Priority priority) {
        this.pid = pid;
        this.priority = priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Process process = (Process) o;

        return getPid() == process.getPid();
    }

    @Override
    public int hashCode() {
        return (int) (getPid() ^ (getPid() >>> 32));
    }

    @Override
    public String toString() {
        return String.format("Process[pid=%s; priority=%s]", pid, priority);
    }



    public void kill(TaskManager taskManager) {
        taskManager.kill(this.pid);
    }
}
