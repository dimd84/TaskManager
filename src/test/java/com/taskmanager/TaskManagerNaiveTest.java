package com.taskmanager;

import com.taskmanager.dto.Priority;
import com.taskmanager.dto.Process;
import com.taskmanager.services.impl.TaskManagerNaiveImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.taskmanager.config.ApiConstants.NAIVE_SERVICE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "app.taskmanager.service=" + NAIVE_SERVICE })
public class TaskManagerNaiveTest extends TaskManagerServiceTest{
    Process p7 = new Process(7, Priority.MEDIUM);

    @Autowired
    TaskManagerNaiveTest(TaskManagerNaiveImpl taskManagerNaive){
        taskManager = taskManagerNaive;
    }

    @Test
    void loadContext(){
        assertTrue(taskManager instanceof TaskManagerNaiveImpl);
        assertNotNull(taskManager);
    }

    @Test
    void addProcessTest() {

        Process process = taskManager.add(p7);
        assertEquals(process.getPid(), p7.getPid());
        try {
            taskManager.add(p7);
            fail("Forbidden to add same process");
        } catch (Exception e) { }
    }

    @Test
    void addProcessRestTest() throws Exception {
       addRest(p7, status().isCreated());
       addRest(p7, status().isBadRequest());
    }

}
