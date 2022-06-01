package com.taskmanager;

import com.taskmanager.dto.Priority;
import com.taskmanager.dto.Process;
import com.taskmanager.services.impl.TaskManagerPriorityImpl;
import com.taskmanager.config.ApiConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "app.taskmanager.service=" + ApiConstants.PRIORITY_SERVICE })
public class TaskManagerPriorityTest extends TaskManagerServiceTest{

    Process p7 = new Process(7, Priority.MEDIUM);
    Process p8 = new Process(8, Priority.MEDIUM);
    Process p9 = new Process(9, Priority.LOW);

    @Autowired
    TaskManagerPriorityTest(TaskManagerPriorityImpl taskManagerPriority){
        taskManager = taskManagerPriority;
    }

    @Test
    void loadContext(){
        assertTrue(taskManager instanceof TaskManagerPriorityImpl);
        assertNotNull(taskManager);
    }

    @Test
    void addProcessByPriorityTest() {
        Assertions.assertEquals(taskManager.getAll().size(), 6);
        taskManager.add(p7);
        List<Process> list = taskManager.getAll();
        assertEquals(list.size(), 7);
        assertProcess(list.get(6), 7, Priority.MEDIUM);
        try {
            taskManager.add(p8);
            list = taskManager.getAll();
            assertEquals(list.size(), 7);
            assertProcess(list.get(0), 1, Priority.HIGH);
            assertProcess(list.get(2), 4, Priority.HIGH);
            assertProcess(list.get(6), 8, Priority.MEDIUM);
            taskManager.add(p9);
            fail("Process with pid 9 with LOW priority should be rejected");
        } catch (Exception e) {}
    }

    @Test
    void addProcessByPriorityRestTest() throws Exception {
        addRest(p7, status().isCreated());
        addRest(p8, status().isCreated());
        addRest(p9, status().isBadRequest());
    }
}
