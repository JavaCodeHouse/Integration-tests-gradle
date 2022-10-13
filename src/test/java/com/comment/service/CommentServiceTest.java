package com.comment.service;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.comment.entity.Comment;
import com.comment.exception.InvalidParameterException;
import com.comment.exception.NotFoundException;
import com.comment.repository.CommentRepository;

public class CommentServiceTest {
	
	@Mock
	private CommentRepository commentRepository;

	@InjectMocks
	private CommentService commentService;

	private ArrayList<Comment> comments;

	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);

	}
	

	@Test
	public void testGetCommentsByPostId() {
		int postId = 1;
		
		comments = new ArrayList<Comment>();
		comments.add(new Comment(1));
		comments.add(new Comment(2));
		comments.add(new Comment(3));
		comments.add(new Comment(1));
		comments.add(new Comment(1));
		comments.add(new Comment(1));
		comments.add(new Comment(2));
		when(commentRepository.findAllByPostId(postId)).thenReturn(comments.stream());
		
		assertThat(commentService.getCommentsByPostId(postId).isEmpty(), is(false));
	}

	@Test 
	public void testGetCommentsByPostId_callsRepositoryToFetchComments() {
		
		int postId = 1;
		commentService.getCommentsByPostId(postId);
		
		verify(commentRepository, times(1)).findAllByPostId(postId);
		
		
	}
	
	@Test
	public void createComment_withValidComment_returnsCommentWithId() {
		
		Comment comment = new Comment();
		comment.setPostId(1);
		comment.setMessage("test comment");
		
		Comment savedComment = new Comment();
		savedComment.setId(1);
		when(commentRepository.save(comment)).thenReturn(savedComment);
		
		assertThat(commentService.createComment(comment).getId(), is(1));
	}

	//comment must have a post id
	
	@Test(expected = InvalidParameterException.class)
	public void createComment_commentWithoutPostId_throwsInvalidParameterException() {
		
		Comment comment = new Comment();
		
		comment.setMessage("test comment");
		
		commentService.createComment(comment);
		

	}
	
	@Test(expected = NotFoundException.class)
	public void deleteComment_whenCommentDoesNotExist_throwsNotFoundException() {
		
		when(commentRepository.existsById(1)).thenReturn(false);
		
		commentService.deleteComment(1);
		
	}
	
	@Test
	public void deleteComment_whenCommentExists_delegatesToRepository() {
		
		when(commentRepository.existsById(1)).thenReturn(true);
		
		commentService.deleteComment(1);
		
		verify(commentRepository).deleteById(1);
		
	}
}
