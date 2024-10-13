package com.mohammad.book_network.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FeedBackRepository extends JpaRepository<FeedBack, Integer> {
    @Query("""
            SELECT feedback
            FROM FeedBack  feedback
            WHERE feedback.book.id = :bookId
""")
    Page<FeedBack> findAllByBookId(@Param("bookId") Integer bookId, Pageable pageable);
    // or //
   // Page<FeedBack> findAllByBookId2(Integer bookId, Pageable pageable);

}