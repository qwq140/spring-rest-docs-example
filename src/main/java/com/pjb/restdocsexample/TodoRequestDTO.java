package com.pjb.restdocsexample;

public record TodoRequestDTO(
        String content,
        boolean completed
) {
    public Todo toEntity() {
        return Todo.builder()
                .content(content)
                .completed(completed)
                .build();
    }
}
