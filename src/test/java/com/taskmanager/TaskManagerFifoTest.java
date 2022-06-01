package com.taskmanager;

import com.taskmanager.dto.Priority;
import com.taskmanager.dto.Process;
import com.taskmanager.services.impl.TaskManagerFifoImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.taskmanager.config.ApiConstants.FIFO_SERVICE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "app.taskmanager.service=" + FIFO_SERVICE })
public class TaskManagerFifoTest extends TaskManagerServiceTest{

    Process p7 = new Process(7, Priority.MEDIUM);
    Process p8 = new Process(8, Priority.MEDIUM);

    @Autowired
    TaskManagerFifoTest(TaskManagerFifoImpl taskManagerFifo){
        taskManager = taskManagerFifo;
    }


    @Test
    void loadContext(){
        assertTrue(taskManager instanceof TaskManagerFifoImpl);
        assertNotNull(taskManager);
    }

    @Test
    void addProcessByFifo() {
       taskManager.add(p7);
        assertEquals(taskManager.getAll().size(), 7);
        try {
            taskManager.add(p8);
            List<Process> list = taskManager.getAll();
            assertProcess(list.get(0), 2, Priority.MEDIUM);
            assertProcess(list.get(6), 8, Priority.MEDIUM);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void addProcessFifoRestTest() throws Exception {
       addRest(p7, status().isCreated());
       addRest(p8, status().isCreated());
    }



}
