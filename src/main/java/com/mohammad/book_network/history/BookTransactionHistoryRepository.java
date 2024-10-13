package com.mohammad.book_network.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Long> {

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :userId
            """) // all borrowed transaction regardless of weather the books are returned or not for specific user
                    // see all books i borrowed
    Page<BookTransactionHistory> findAllMyBorrowedBooks(Pageable pageable, @Param("userId") Integer userId);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :userId
            """) // track all transactions related to the books they own. (all my books that have been borrowed)
                // see my own books that have been borrowed
    Page<BookTransactionHistory> findAllBooksBorrowedFromMe(Pageable pageable, @Param("userId") Integer userId);


    @Query("""
        SELECT COUNT(history) > 0
        FROM BookTransactionHistory history
        WHERE history.book.id = :bookId
        AND history.returned = false
        """)
    boolean isAlreadyBorrowed(@Param("bookId") Integer bookId);

    @Query("""
            SELECT transaction
            FROM BookTransactionHistory  transaction
            WHERE transaction.user.id = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = false
            AND transaction.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(@Param("bookId") Integer bookId, @Param("userId") String userId);


    @Query("""
            SELECT transaction
            FROM BookTransactionHistory  transaction
            WHERE transaction.book.createdBy = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = true
            AND transaction.returnApproved = false
            """) // is there transaction for this book that owned by this user. is there a borrowed book with this id and owned by this user
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(@Param("bookId") Integer bookId, @Param("userId") String userId);
}
