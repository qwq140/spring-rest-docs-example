package com.pjb.restdocsexample;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional(readOnly = true)
    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Todo getTodoById(Long id) {
        return todoRepository.findById(id).orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    @Transactional
    public Todo saveTodo(TodoRequestDTO todoRequestDTO) {
        return todoRepository.save(todoRequestDTO.toEntity());
    }

    @Transactional
    public Todo updateTodo(Long id, TodoRequestDTO updatedTodo) {
        Todo todo = getTodoById(id);
        todo.setContent(updatedTodo.content());
        todo.setCompleted(updatedTodo.completed());
        return todo;
    }

    @Transactional
    public void deleteTodoById(Long id) {
        todoRepository.deleteById(id);
    }

}
