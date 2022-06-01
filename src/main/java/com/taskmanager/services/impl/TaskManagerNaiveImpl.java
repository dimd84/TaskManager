package com.taskmanager.services.impl;

import com.taskmanager.dto.Process;
import com.taskmanager.exceptions.SizeOverflowException;
import com.taskmanager.services.TaskManagerAbstract;
import org.springframework.stereotype.Service;

import static com.taskmanager.config.ApiConstants.NAIVE_SERVICE;

@Service(NAIVE_SERVICE)
public class TaskManagerNaiveImpl extends TaskManagerAbstract {

    @Override
    synchronized public Process add(Process process) {
        checkExist(process);
        if (size == maxCapacity) {
            throw  new SizeOverflowException();
        }
        addLast(process);
        return process;
    }



}
