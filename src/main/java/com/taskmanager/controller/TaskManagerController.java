package com.taskmanager.controller;

import com.taskmanager.config.Order;
import com.taskmanager.dto.Priority;
import com.taskmanager.dto.Process;
import com.taskmanager.services.TaskManager;
import com.taskmanager.config.ApiConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(ApiConstants.REST_URL)
@Validated
@CrossOrigin
@Log4j2
public class TaskManagerController {

    @Autowired
    private  Map<String, TaskManager> taskManagerMap;

    @Autowired
    ApplicationContext ctx;

    @Value("${app.taskmanager.service}")
    private String taskManagerType;

    private TaskManager taskManager;

    @PostConstruct
    void setService() {
        taskManager = taskManagerMap.get(taskManagerType);
        if (taskManager == null) {
            ((ConfigurableApplicationContext) ctx).close();
            throw new RuntimeException(String.format("Service %s not found in application context", taskManagerType));
        }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Process addProcess(@RequestBody @Valid Process process) {
        Process newProcess = taskManager.add(process);
        log.debug("add order with id {} and priority {}", process.getPid(), process.getPriority());
        return newProcess;
    }

    @GetMapping(params = {"orderBy"})
    public List<Process> getAll(@RequestParam(value = "orderBy") Order orderBy) {
        List<Process> list = taskManager.getAll(orderBy);
        log.debug("get {} processes order by {}", list.size(), orderBy);
        return list;
    }

    @DeleteMapping("/{id}")
    public Process killById(@PathVariable(name = "id") long id) {
        Process process = taskManager.kill(id);
        log.debug("deleted process with id {}", id);
        return process;
    }
    @DeleteMapping(params = {"priority"})
    public List<Process> killByPriority(@RequestParam(value = "priority") Priority priority) {
        List<Process> list = taskManager.kill(priority);
        log.debug("deleted {} processes by priority {}", list.size(), priority.getPriority());
        return list;
    }
    @DeleteMapping
    public List<Process> killAll() {
        List<Process> list = taskManager.killAll();
        log.debug("deleted all {} processes", list.size());
        return list;
    }



}
