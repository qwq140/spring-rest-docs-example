package com.pjb.restdocsexample;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureRestDocs
public class TodoControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private EntityManager entityManager;


    @BeforeEach
    public void setUp() {
        dataSetting();
        entityManager.clear();
    }


    @Test
    public void getAllTodosTest() throws Exception {
        // given

        // when
        ResultActions resultActions = mvc.perform(get("/api/todos"));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("get-all-todos",
                        responseFields(
                                fieldWithPath("[].id").description("Todo의 id"),
                                fieldWithPath("[].content").description("Todo의 내용"),
                                fieldWithPath("[].completed").description("Todo가 완료되었는지")
                        )
                ));
    }

    @Test
    public void getTodoByIdTest() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mvc.perform(get("/api/todos/{id}", id));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("get-todo-by-id",
                        pathParameters(
                                parameterWithName("id").description("조회하려고 하는 Todo의 id")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Todo의 id"),
                                fieldWithPath("content").description("Todo의 내용"),
                                fieldWithPath("completed").description("Todo가 완료되었는지")
                        )
                ));
    }

    @Test
    public void saveTodoTest() throws Exception {
        // given
        TodoRequestDTO todo = new TodoRequestDTO("할일3", false);

        String requestBody = objectMapper.writeValueAsString(todo);

        // when
        ResultActions resultActions = mvc.perform(post("/api/todos").content(requestBody).contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("save-todo",
                        requestFields(
                                fieldWithPath("content").description("Todo 내용"),
                                fieldWithPath("completed").description("Todo가 완료되었는지")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Todo의 id"),
                                fieldWithPath("content").description("Todo 내용"),
                                fieldWithPath("completed").description("Todo가 완료되었는지")
                        )
                ));

    }

    @Test
    public void updatedTodoTest() throws Exception {
        // given
        Long id = 1L;
        TodoRequestDTO todo = new TodoRequestDTO("할일1", true);

        String requestBody = objectMapper.writeValueAsString(todo);

        // when
        ResultActions resultActions = mvc.perform(put("/api/todos/{id}", id).content(requestBody).contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("할일1"))
                .andExpect(jsonPath("$.completed").value(true))
                .andDo(document("update-todo",
                        pathParameters(
                                parameterWithName("id").description("수정한 Todo의 id")
                        ),
                        requestFields(
                                fieldWithPath("content").description("수정한 Todo의 내용"),
                                fieldWithPath("completed").description("Todo가 완료되었는지")
                        ),
                        responseFields(
                                fieldWithPath("id").description("Todo의 id"),
                                fieldWithPath("content").description("Todo의 수정된 내용"),
                                fieldWithPath("completed").description("Todo가 완료되었는지")
                        )
                ));
    }

    @Test
    public void deleteTodoByIdTest() throws Exception {
        // given
        Long id = 2L;

        // when
        ResultActions resultActions = mvc.perform(delete("/api/todos/{id}", id));


        // then
        resultActions.andExpect(status().isOk())
                .andDo(document("delete-todo-by-id",
                        pathParameters(
                                parameterWithName("id").description("삭제하려는 Todo의 id")
                        )
                ));
    }

    private void dataSetting() {
        Todo todo1 = Todo.builder()
                .content("할일1")
                .completed(false)
                .build();

        Todo todo2 = Todo.builder()
                .content("할일2")
                .completed(false)
                .build();

        todoRepository.save(todo1);
        todoRepository.save(todo2);
    }

}
