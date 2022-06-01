package com.taskmanager.services;

import com.taskmanager.config.Order;
import com.taskmanager.dto.Priority;
import com.taskmanager.dto.Process;
import com.taskmanager.exceptions.ResourceNotFoundException;
import com.taskmanager.exceptions.SizeOverflowException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log4j2
public abstract class TaskManagerAbstract implements TaskManager{
    protected int size;
    @Value("${app.taskmanager.maxCapacity:5}")
    protected int maxCapacity;
    protected final Set<Process> processSet = new LinkedHashSet<>();
    protected final Map<Priority, Set<Process>> priorityProcessMap = new HashMap<>();
    protected final Map<Long, Process> pidProcessMap = new HashMap<>();

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock();
    Lock writeLock = lock.writeLock();

    @Override
    synchronized public List<Process> getAll(Order type) {
        readLock.lock();
        try {
            if (type == null) {
                throw new IllegalArgumentException("Invalid parameter order");
            }
            return switch (type) {
                case CREATION_TIME -> getAllProcesses();
                case PRIORITY -> getAllByPriority();
                case PID -> getAllByPid();
                default -> throw new IllegalArgumentException("Invalid parameter order");
            };
        } finally {
            readLock.unlock();
        }
    }

    @Override
    synchronized public Process kill(long id) {
        writeLock.lock();
        try {
            Process process = pidProcessMap.get(id);
            if (process == null) {
                throw new ResourceNotFoundException("Process does not exists");
            }
            removeProcess(process);
            return process;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    synchronized public List<Process> kill(Priority priority) {
        Set<Process> set = priorityProcessMap.get(priority);
        if(set == null) {
            throw new NoSuchElementException("No processes with this priority");
        }
        List<Process> res = new ArrayList<>(set);
        res.forEach(this::removeProcess);
        return res;
    }

    @Override
    synchronized public List<Process> killAll() {
        List<Process> res = getAllProcesses();
        res.forEach(this::removeProcess);
        return res;
    }


    private List<Process> getAllProcesses() {
        List<Process> res = new ArrayList<>();
        processSet.forEach(res::add);
        return res;
    }


    private List<Process> getAllByPriority() {
        return priorityProcessMap.entrySet().stream()
                .sorted(Comparator.comparingInt(p -> p.getKey().getPriorityKey()))
                .flatMap(e-> e.getValue().stream())
                .toList();
    }

    private List<Process> getAllByPid() {
        return pidProcessMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }

    protected void addLast(Process process) {
        processSet.add(process);
        priorityProcessMap.computeIfAbsent(process.getPriority(), e -> new LinkedHashSet<>()).add(process);
        pidProcessMap.put(process.getPid(), process);
        size++;
    }

    protected void checkExist(Process process) {
        if (process == null) {
            throw new NullPointerException("Process can not be null");
        }
        if (processSet.contains(process)) {
            throw new IllegalArgumentException("The process is already in the task manager");
        }
        if (maxCapacity == 0) {
            throw new SizeOverflowException();
        }
    }
    protected Process removeFirst() {
        Iterator<Process> it = processSet.iterator();
        Process firstProcess = it.next();
        it.remove();
        removeProcessMap(firstProcess);
        return firstProcess;
    }
    protected void removeProcess(Process process) {
        if (processSet.remove(process)) {
            removeProcessMap(process);
            size--;
        }
    }
    protected void removeProcessMap(Process process) {
        priorityProcessMap.get(process.getPriority()).remove(process);
        pidProcessMap.remove(process.getPid());
    }
}
