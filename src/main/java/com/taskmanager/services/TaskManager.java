package com.taskmanager.services;

import com.taskmanager.config.Order;
import com.taskmanager.dto.Priority;
import com.taskmanager.dto.Process;

import java.util.List;

public interface TaskManager {
    Process add(Process process);
    List<Process> getAll(Order type);
    default List<Process> getAll() {return getAll(Order.CREATION_TIME);}
    Process kill(long id);
    List<Process> kill(Priority priority);
    List<Process> killAll();

}
