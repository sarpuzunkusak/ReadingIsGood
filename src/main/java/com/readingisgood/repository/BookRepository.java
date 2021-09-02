package com.readingisgood.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.readingisgood.repository.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

	Set<Book> findAllByIdIn(List<String> ids);

}
