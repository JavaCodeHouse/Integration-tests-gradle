package com.comment.repository;

import java.util.stream.Stream;

import org.springframework.data.repository.CrudRepository;

import com.comment.entity.Comment;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

	Stream<Comment> findAllByPostId(int postId);

}
