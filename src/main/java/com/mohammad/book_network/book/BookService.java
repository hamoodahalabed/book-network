package com.mohammad.book_network.book;

import com.mohammad.book_network.common.PageResponse;
import com.mohammad.book_network.exceptions.OperationNotPermittedException;
import com.mohammad.book_network.history.BookTransactionHistory;
import com.mohammad.book_network.history.BookTransactionHistoryRepository;
import com.mohammad.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class    BookService {

    private final BookRepository bookRepository;
    private final BookMapper mapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    public Integer save(BookRequest request, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Book book = mapper.toBook(request);
        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {

        return bookRepository.findById(Long.valueOf(bookId))
                .map(mapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        // is used to encapsulate pagination information, such as the page number and size, and it's sent to the repository to fetch the relevant data
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> booksResponse = books.stream()
                .map(mapper::toBookResponse)
                .toList();

        System.out.println("book page is: " + books);
        System.out.println("book page is: " + books.getContent());

        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Book> books = bookRepository.findByCreatedBy(user.getId(), pageable);

        List<BookResponse> booksResponse = books.stream()
                .map(mapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                booksResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }


    public PageResponse<BorrowedBookResponse> allMyBorrowedBooks(int page, int size, Authentication connectedUser) {
         User user = ((User) connectedUser.getPrincipal());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllMyBorrowedBooks(pageable, user.getId());

        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(mapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> allBooksBorrowedFromMe(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBooksBorrowedFromMe(pageable, user.getId());

        List<BorrowedBookResponse> booksResponse = allBorrowedBooks.stream()
                .map(mapper::toBorrowedBookResponse)
                .toList();

        return new PageResponse<>(
                booksResponse,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );

    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(Long.valueOf(bookId))
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

         User user = ((User) connectedUser.getPrincipal());

        if (Objects.equals(book.getCreatedBy(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(Long.valueOf(bookId))
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

         User user = ((User) connectedUser.getPrincipal());

        if (!Objects.equals(book.getCreatedBy(), user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archived status");
        }

        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(Long.valueOf(bookId))
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID:: " + bookId));

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("The requested book cannot be borrowed since it is archived or not shareable");
        }

         User user = ((User) connectedUser.getPrincipal());

        if (Objects.equals(book.getCreatedBy(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }

        final boolean isAlreadyBorrowed = bookTransactionHistoryRepository.isAlreadyBorrowed(bookId);

        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("You already borrowed this book or the book" +
                    " is borrowed by other user and it is still not returned or the return is" +
                    " not approved by the owner");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();

        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();

    }
}
