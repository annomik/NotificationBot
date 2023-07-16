package com.example.notificationbot.service;


import com.example.notificationbot.model.Task;
import com.example.notificationbot.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.Specification.where;

@RequiredArgsConstructor
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public void save(Task task) {
        taskRepository.save(task);
    }

    public void update(Long id, Task task) {
        task.setId(id);
        taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Task> search(String keyword) {
        if (keyword != null) {
            List<Task> tasks = new ArrayList<>();
            tasks.addAll(taskRepository.findAll(where(TaskRepository.topicContains(keyword))));
            tasks.addAll(taskRepository.findAll(where(TaskRepository.textContains(keyword))));

            return tasks;
        }

        return findAll();
    }

    @Transactional(readOnly = true)
    public List<Task> search(String keyword, Long userId) {
        if (keyword != null) {
            List<Task> tasks = new ArrayList<>();
            tasks.addAll(taskRepository.findAll(where(TaskRepository.topicContains(keyword))
                    .and(where(TaskRepository.userId(userId)))));
            tasks.addAll(taskRepository.findAll(where(TaskRepository.textContains(keyword))
                    .and(where(TaskRepository.userId(userId)))));

            return tasks;
        }

        return taskRepository.findAll(where(TaskRepository.userId(userId)));
    }

    @Transactional(readOnly = true)
    public List<Task> findAllByUserId(Long userId) {
        return taskRepository.findAllByUserId(userId);
    }
}
