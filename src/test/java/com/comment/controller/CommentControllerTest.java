package com.comment.controller;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.comment.entity.Comment;
import com.comment.exception.InvalidParameterException;
import com.comment.exception.NotFoundException;
import com.comment.service.CommentService;

public class CommentControllerTest {
	
	@Mock
	private CommentService commentService;
	
	
	@InjectMocks
	private CommentController commentController;


	private List<Comment> comments;

	@Before
	public void setUp() throws Exception {
		
		comments = new ArrayList<Comment>();
		comments.add(new Comment(1));
		comments.add(new Comment(2));
		comments.add(new Comment(3));
		comments.add(new Comment(1));
		comments.add(new Comment(1));
		comments.add(new Comment(1));
		comments.add(new Comment(2));
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	//Returns a list of comments 
	@Test
	public void testGetCommentsByPostId_returnsAListOfComments() {
		
		when(commentService.getCommentsByPostId(1)).thenReturn(comments);
		
		assertThat(commentController.getCommentsByPostId(1).isEmpty(), is(false));
		
	}
	
	
	@Test
	public void testGetCommentsByPostId_delegatesToService() {
		
		int postId = 1;
		
		commentController.getCommentsByPostId(postId);
		
		verify(commentService, times(1)).getCommentsByPostId(postId);
	}
	
	
	
	//Return a list of comments with the same postId as the one passed in to the method.
	@Test
	public void testGetCommentsByPostId_allCommentsReturnedBelongToTheSamePost() {
		
		int postId = 1;
		assertThat(commentController.getCommentsByPostId(postId), everyItem(hasProperty("postId",is(postId))));
	}
	
	//Throw exception if post id is invalid
	@Test(expected = InvalidParameterException.class)
	public void testGetCommentsByPostId_throwsExceptions_forInvalidPostId() {
		
		//valid post id is a positive integer greater than 0
		commentController.getCommentsByPostId(-1);
	}
	
	//Return an empty list when no comments for the post
	@Test
	public void testGetCommentsByPostId_returnsEmptyList_whenNoCommentsForThePost() {
		
		assertThat(commentController.getCommentsByPostId(100).isEmpty(), is(true) );
	}
	
	@Test(expected = InvalidParameterException.class)
	public void addNewComment_withIdSet_throwsInvalidParmeterException() {
		
		Comment comment = new Comment();
		comment.setId(1);
		commentController.addComment(comment, 1);
	}
	
	@Test(expected = InvalidParameterException.class)
	public void addNewComment_withNoCommentMessage_throwsInvalidParmeterException() {
		
		commentController.addComment(new Comment(), 1);
	}
	
	@Test
	public void addNewComment_withValidComment_returnsCommentWithId() {
		
		Comment comment = new Comment();
		comment.setMessage("this is comment message");
		
		Comment returnedComment = new Comment();
		returnedComment.setId(1);
		when(commentService.createComment(comment)).thenReturn(returnedComment);
		
		assertThat(commentController.addComment(comment, 1).getId(), is(1));
	}
	
	@Test
	public void addNewComment_withValidComment_returnsCommentWithPostId() {
		
		Comment comment = new Comment();
		comment.setMessage("this is comment message");
		
		when(commentService.createComment(comment)).thenAnswer(new Answer<Comment>() {

			@Override
			public Comment answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				if(args !=null && args.length>0 && args[0]!=null) {
					Comment commentArg = (Comment) args[0];
					return commentArg;
				}
				return null;
			}
		});
		
		assertThat(commentController.addComment(comment, 1).getPostId(), is(1));
	}
	
	
	@Test
	public void addNewComment_withValidComment_returnsCommentWithMessage() {
		
		Comment comment = new Comment();
		comment.setMessage("this is comment message");
		
		when(commentService.createComment(comment)).thenAnswer(new Answer<Comment>() {

			@Override
			public Comment answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				if(args !=null && args.length>0 && args[0]!=null) {
					Comment commentArg = (Comment) args[0];
					return commentArg;
				}
				return null;
			}
		});
		
		assertThat(commentController.addComment(comment, 1).getMessage(), is("this is comment message"));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void addComment_validCommentButinvalidPostId_throwsInvalidParameterException() {
		
		Comment comment = new Comment();
		comment.setMessage("comment");
		commentController.addComment(comment, -1);
	}

	@Test(expected = InvalidParameterException.class)
	public void deleteComment_invalidCommentId_throwsInvalidParameterException() {
		
		commentController.deleteComment(-1);
	}
	
	@Test(expected = NotFoundException.class)
	public void deleteComment_whenCommentDoesNotExist_throwsNotFoundException() {
		
		doThrow(NotFoundException.class).when(commentService).deleteComment(1);
		
		commentController.deleteComment(1);
		
	}
	
}
