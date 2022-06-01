package com.taskmanager;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.config.Order;
import com.taskmanager.dto.Priority;
import com.taskmanager.dto.Process;
import com.taskmanager.services.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.taskmanager.config.ApiConstants.REST_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class TaskManagerServiceTest {
    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    MockMvc mockMvc;
    Map map = new TreeMap();

    Process p1 = new Process(1, Priority.HIGH);
    Process p2 = new Process(2, Priority.MEDIUM);
    Process p3 = new Process(3, Priority.LOW);
    Process p4 = new Process(4, Priority.HIGH);
    Process p5 = new Process(5, Priority.MEDIUM);
    Process p6 = new Process(6, Priority.LOW);

    TaskManager taskManager;

    @BeforeEach
    void addProcesses() throws Exception {
        taskManager.killAll();
        taskManager.add(p1);
        taskManager.add(p2);
        taskManager.add(p3);
        taskManager.add(p4);
        taskManager.add(p5);
        taskManager.add(p6);
        killAllRestTest();
        addRest(p1, status().is2xxSuccessful());
        addRest(p2, status().is2xxSuccessful());
        addRest(p3, status().is2xxSuccessful());
        addRest(p4, status().is2xxSuccessful());
        addRest(p5, status().is2xxSuccessful());
        addRest(p6, status().is2xxSuccessful());
    }

    @Test
    void killProcessMethodTest() {
        p1.kill(taskManager);
        taskManager.getAll().forEach(p-> {
            assertFalse(p.equals(p1));
        });
    }
    @Test
    void getAllTest() {
        List<Process> list = taskManager.getAll();
        assertEquals(list.size(), 6);
        assertProcess(list.get(0), 1, Priority.HIGH);
        assertProcess(list.get(2), 3, Priority.LOW);
        assertProcess(list.get(5), 6, Priority.LOW);

    }
    @Test
    void getAllRestTest() throws Exception {
        getRestTest(REST_URL + "?orderBy=CREATION_TIME", Order.CREATION_TIME);
    }

    @Test
    void getAllByPriorityTest() {
        List<Process> list = taskManager.getAll(Order.PRIORITY);
        assertEquals(list.size(), 6);
        assertProcess(list.get(0), 3, Priority.LOW);
        assertProcess(list.get(5), 4, Priority.HIGH);
    }

    @Test
    void getAllByPriorityRestTest() throws Exception {
        getRestTest(REST_URL + "?orderBy=PRIORITY", Order.PRIORITY);
    }

    @Test
    void getAllByPidTest() {
        List<Process> list = taskManager.getAll(Order.PID);
        assertEquals(list.size(), 6);
        assertProcess(list.get(0), 1, Priority.HIGH);
        assertProcess(list.get(5), 6, Priority.LOW);

    }
    @Test
    void getAllByPidRestTest() throws Exception {
        getRestTest(REST_URL + "?orderBy=PID", Order.PID);
    }

    private void getRestTest(String url, Order order) throws Exception {

        String resJSON = mockMvc.perform(MockMvcRequestBuilders
                        .get(url))
                .andReturn()
                .getResponse()
                .getContentAsString();
        Process[] processes = mapper.readValue(resJSON, Process[].class);
        assertArrayEquals(processes, taskManager.getAll(order).toArray());
    }

    @Test
    void killProcessTest() {
        taskManager.kill(p1.getPid());
        List<Process> list = taskManager.getAll();
        assertEquals(list.size(), 5);
        taskManager.getAll().forEach(p-> {
            assertNotEquals(p.getPid(), p1.getPid());
        });
    }
    @Test
    void killProcessRestTest() throws Exception {
        deleteRest("/1", status().is2xxSuccessful());
        deleteRest("/223", status().is4xxClientError());
    }

    @Test
    void killByPriorityTest() {
        taskManager.kill(Priority.HIGH);
        taskManager.getAll().forEach(p-> {
            assertNotEquals(p.getPriority(), Priority.HIGH);
        });
    }
    @Test
    void killByPriorityRestTest() throws Exception {
        deleteRest("/?priority=LOW", status().is2xxSuccessful());
        deleteRest("/?priority=NOT_FOUND", status().isBadRequest());

    }

    @Test
    void killAllTest() {
        assertEquals(6, taskManager.getAll().size());
        taskManager.killAll();
        List<Process> list = taskManager.getAll();
        assertEquals(list.size(), 0);
    }

    @Test
    void killAllRestTest() throws Exception {
        deleteRest("", status().is2xxSuccessful());
    }
    protected void assertProcess(Process process, long pid, Priority priority) {
        assertEquals(process.getPid(), pid);
        assertEquals(process.getPriority(), priority);
    }

    protected void addRest(Process process, ResultMatcher matcher) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(REST_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(process)))
                .andExpect(matcher);
    }

    protected void deleteRest(String url, ResultMatcher matcher) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(REST_URL + url))
                .andExpect(matcher);
    }
    protected static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
