package com.taskmanager.services.impl;

import com.taskmanager.dto.Process;
import com.taskmanager.services.TaskManagerAbstract;
import com.taskmanager.config.ApiConstants;
import org.springframework.stereotype.Service;

@Service(ApiConstants.FIFO_SERVICE)
public class TaskManagerFifoImpl extends TaskManagerAbstract {

    @Override
    synchronized public Process add(Process process) {
       checkExist(process);
       if (size == maxCapacity) {
            removeFirst();
            size--;
        }
       addLast(process);
       return process;
    }



}
