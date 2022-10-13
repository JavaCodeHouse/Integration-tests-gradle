package com.comment.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.comment.entity.Comment;
import com.comment.exception.InvalidParameterException;
import com.comment.exception.NotFoundException;
import com.comment.repository.CommentRepository;

@Service
public class CommentService {

	@Inject
	private CommentRepository commentRepository;

	@Transactional(readOnly = true)
	public List<Comment> getCommentsByPostId(int postId) {

		try (Stream<Comment> commentsStream = commentRepository.findAllByPostId(postId)) {

			return commentsStream.collect(Collectors.toList());

		}
	}

	public Comment createComment(Comment comment) {
		
		if(comment.getPostId()<1) {
			throw new InvalidParameterException();
		}
		
		return commentRepository.save(comment);
	}
	
	public CommentRepository getCommentRepository() {
		return commentRepository;
	}

	public void setCommentRepository(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}

	public void deleteComment(int commentId) {

		if(commentRepository.existsById(commentId)) {
			commentRepository.deleteById(commentId);
			
		}else {
			throw new NotFoundException();
		}
	}

}
