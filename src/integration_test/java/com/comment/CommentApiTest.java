package com.comment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.hamcrest.Matchers.*;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommentApiTest {

	@Inject
	private MockMvc mvc;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCommentApiGetCommentsByPostId_statusOk() throws Exception {

		mvc.perform(get("/posts/1/comments")).andExpect(status().isOk()).andDo(print());

	}

	@Test
	@SqlGroup({ @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/test-data.sql"),
			@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from comment;") })
	public void getCommentsByPostId_whenDataPreset_returnsAllvalues() throws Exception {

		mvc.perform(get("/posts/1/comments")).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.[0].postId", is(1))).andExpect(jsonPath("$.[0].message", is("test comment1")))
				.andDo(print());
	}

	@Test
	public void getCommentsByPostId_invalidPostId_status400() throws Exception {

		mvc.perform(get("/posts/-1/comments")).andExpect(status().isBadRequest()).andDo(print());
	}

	@Test
	@SqlGroup({ @Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/test-data.sql"),
			@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from comment;") })
	public void getCommentsByPostId_postIdNotFound_returnsEmptyArrayAndStatusOk() throws Exception {

		mvc.perform(get("/posts/100/comments")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()", is(0))).andDo(print());

	}

	@Test
	public void addComments_validRequest_statusCreated() throws Exception {

		mvc.perform(post("/posts/1/comments").content("{\"message\": \"test comment\"}")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andDo(print());

	}

	@Test
	public void addComments_invalidRequestWithCommentId_status400() throws Exception {

		mvc.perform(post("/posts/1/comments").content("{\"id\": 1, \"message\": \"test comment2\"}")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andDo(print());

	}

	@Test
	public void addComments_invalidPostIdInRequest_status400() throws Exception {

		mvc.perform(post("/posts/-1/comments").content("{\"message\": \"test comment3\"}")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest()).andDo(print());

	}

	@Test
	public void addComments_validRequest_returnsCreatedCommentWithIdPostIdAndMessage() throws Exception {

		mvc.perform(post("/posts/4/comments").content("{\"message\": \"test comment4\"}")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.id", is(greaterThan(0))))
				.andExpect(jsonPath("$.postId", is(4))).andExpect(jsonPath("$.message", is("test comment4")))
				.andDo(print());

	}


	@Test
	@SqlGroup({
			@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = "insert into `comment` (id, post_id, message) values (1, 1, 'testCommentFor delete');"),
			@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from comment;") })
	public void deleteComment_forValidRequest_returnsResponseCode204() throws Exception {

		mvc.perform(delete("/comments/1")).andExpect(status().isNoContent()).andDo(print());

	}

	@Test
	@SqlGroup({
			@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = "insert into `comment` (id, post_id, message) values (1, 1, 'testCommentFor delete');"),
			@Sql(executionPhase = ExecutionPhase.AFTER_TEST_METHOD, statements = "delete from comment;") })
	public void deleteComment_forValidRequest_deletesComment() throws Exception {

		mvc.perform(get("/posts/1/comments"))
				.andExpect(jsonPath("$.length()", is(1)))
				.andExpect(jsonPath("$.[0].id", is(1)))
				.andDo(print());

		mvc.perform(delete("/comments/1")).andDo(print());
		
		mvc.perform(get("/posts/1/comments"))
		.andExpect(jsonPath("$.length()", is(0)))
		.andDo(print());

	}
	
	
	@Test
	@Sql(executionPhase = ExecutionPhase.BEFORE_TEST_METHOD, statements = "delete from comment;")
	public void deleteComment_whenCommentDoesNotExist_returnsResponseCode404() throws Exception {

		mvc.perform(delete("/comments/1")).andExpect(status().isNotFound()).andDo(print());

	}

}
