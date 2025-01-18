package com.vtn.Yame.controller;

import com.vtn.Yame.repository.CategoryRepository;
import com.vtn.Yame.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/camunda")
@RequiredArgsConstructor
public class CamundaController {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/start-process/{name}")
    public String startProcess(@PathVariable String name) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);

        runtimeService.startProcessInstanceByKey("add_category", variables);
        return "Process started with category: " + name;
    }

    @PutMapping("/task/change-status")
    public String changeTaskStatus(
            @RequestParam String processInstanceId,
            @RequestParam String taskDefinitionKey,
            @RequestParam(required = false) String assignee) {
        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .list();

        if (tasks.isEmpty()) {
            return "No tasks found for the given process instance and task definition key.";
        }

        for (Task task : tasks) {
            if (assignee != null && !assignee.isEmpty()) {
                taskService.claim(task.getId(), assignee);
                return "Task claimed by: " + assignee;
            } else {
                taskService.setAssignee(task.getId(), null);
                return "Task unclaimed";
            }
        }

        return "Task not found for the given process instance and task definition key.";
    }

    @PostMapping("/task/complete")
    public ResponseEntity<Map<String, Object>> completeTask(@RequestBody(required = false) Map<String, Object> variables) {

        String taskId = "";
        String taskName = "";

        Map<String, Object> response = new HashMap<>();

        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery().initializeFormKeys().list();

        for (Task task : tasks) {
            taskId = task.getId();
            taskName = task.getName();
        }

        if (taskId == null) {
            response.put("status", "Task not found");
            response.put("taskId", taskId);
            return ResponseEntity.status(404).body(response);
        }

        // Complete the task with provided variables (if any)
        taskService.complete(taskId, variables != null ? variables : new HashMap<>());

        response.put("status", "Task completed successfully");
        response.put("taskId", taskId);
        response.put("taskName", taskName);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/tasks")
    public String getAllTasks() {
        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery().initializeFormKeys().list();

        for (Task task : tasks) {
            System.out.println("Task ID: " + task.getId() + ", Task Name: " + task.getName() + "get id " + task.getTaskDefinitionKey());
        }

        return "OK";
    }


    @PutMapping("/task/set-variables")
    public String setTaskVariables(@RequestParam String taskDefinitionKey, @RequestParam String processInstanceId, @RequestParam Map<String, Object> variables) {
        TaskService taskService = processEngine.getTaskService();

        List<Task> tasks = taskService.createTaskQuery()
                .taskDefinitionKey(taskDefinitionKey)
                .processInstanceId(processInstanceId)
                .list();

        if (tasks != null && !tasks.isEmpty()) {
            for (Task task : tasks) {
                taskService.setVariables(task.getId(), variables);
            }

            return "Variables added to tasks with taskDefinitionKey: " + taskDefinitionKey + " in process instance: " + processInstanceId;
        } else {
            return "No task found with the given taskDefinitionKey and processInstanceId";
        }
    }
}