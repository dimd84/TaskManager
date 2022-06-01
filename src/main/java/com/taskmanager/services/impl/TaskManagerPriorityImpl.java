package com.taskmanager.services.impl;

import com.taskmanager.dto.Process;
import com.taskmanager.exceptions.SizeOverflowException;
import com.taskmanager.services.TaskManagerAbstract;
import com.taskmanager.config.ApiConstants;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service(ApiConstants.PRIORITY_SERVICE)
public class TaskManagerPriorityImpl extends TaskManagerAbstract {

    @Override
    synchronized public Process add(Process process) {
        checkExist(process);
        if(size == maxCapacity) {
            Process oldestProcess = priorityProcessMap.entrySet().stream()
                    .filter(e -> (e.getKey().getPriorityKey() < process.getPriority().getPriorityKey()) && (e.getValue().size() > 0))
                    .sorted(Comparator.comparingInt(p -> p.getKey().getPriorityKey()))
                    .limit(1)
                    .flatMap(e -> e.getValue().stream()).findFirst().orElse(null);
            if (oldestProcess == null) {
                throw new SizeOverflowException();
            }
            removeProcess(oldestProcess);
        }
        addLast(process);
        return process;
    }

}
