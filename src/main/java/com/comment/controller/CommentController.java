package com.comment.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.comment.entity.Comment;
import com.comment.exception.InvalidParameterException;
import com.comment.service.CommentService;

@RestController
public class CommentController {

	@Inject
	private CommentService commentService;

	@GetMapping("/posts/{id}/comments")
	public List<Comment> getCommentsByPostId(@PathVariable("id") int postId) {

		if (postId < 1) {
			throw new InvalidParameterException();
		}

		return commentService.getCommentsByPostId(postId);
	}

	public CommentService getCommentService() {
		return commentService;
	}

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	@PostMapping("posts/{id}/comments")
	@ResponseStatus(HttpStatus.CREATED)
	public Comment addComment(@RequestBody Comment comment, @PathVariable("id") int postId) {

		validateComment(comment, postId);

		comment.setPostId(postId);

		return commentService.createComment(comment);

	}

	private void validateComment(Comment comment, int postId) {
		if (postId < 1) {
			throw new InvalidParameterException();
		}

		if (comment.getId() > 0) {
			throw new InvalidParameterException();
		}

		if (comment.getMessage() == null || comment.getMessage().isEmpty()) {
			throw new InvalidParameterException();
		}
	}

	@DeleteMapping("/comments/{commentId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteComment(@PathVariable("commentId") int commentId) {

		if (commentId < 1) {
			throw new InvalidParameterException();
		}

		commentService.deleteComment(commentId);

	}

}
