package com.mohammad.book_network.book;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

    //  SELECT book => mean all book field/column or choose book.whatever
    @Query("""
            SELECT book
            FROM Book book
            WHERE book.archived = false
            AND book.shareable = true
            AND book.createdBy != :userId
            """) /// apply pagination role object that will send to you to the fetched data
    Page<Book> findAllDisplayableBooks(Pageable pageable, Integer userId);
    // take pageable object and return one page for me



    Page<Book> findByCreatedBy(Integer ownerId, Pageable pageable);

}
