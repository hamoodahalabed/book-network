package com.mohammad.book_network.book;

import com.mohammad.book_network.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@Tag(name = "Book")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.save(request,connectedUser));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> findBookById(@PathVariable("book-id") Integer bookId) {

        return ResponseEntity.ok(bookService.findById(bookId));
    }

    //When you specify size = 5, it means you want to divide the entire dataset into pages, with each page containing 5 items.
    //The page parameter specifies which page of data you want to retrieve.
    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findAllBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooks(page, size, connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllMyBorrowedBooks(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.allMyBorrowedBooks(page, size, connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBooksBorrowedFromMe(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.allBooksBorrowedFromMe(page, size, connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.updateShareableStatus(bookId, connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.updateArchivedStatus(bookId, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<Integer> findAllBorrowedBooks(
            @PathVariable("book-id") Integer bookId,
            Authentication connectedUser
    ) {

        return ResponseEntity.ok(bookService.borrowBook(bookId,connectedUser));
    }
}
