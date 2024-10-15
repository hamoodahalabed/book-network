package com.mohammad.book_network.book;

import com.mohammad.book_network.common.PageResponse;
import com.mohammad.book_network.exceptions.OperationNotPermittedException;
import com.mohammad.book_network.file.FileStorageService;
import com.mohammad.book_network.history.BookTransactionHistory;
import com.mohammad.book_network.history.BookTransactionHistoryRepository;
import com.mohammad.book_network.user.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookTransactionHistoryRepository bookTransactionHistoryRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper mapper;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private User user;

    @Mock
    private Authentication connectedUser;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_Success() {
        // Given
        Integer bookId = 1;
        Book mockBook = Book.builder()
                .id(bookId)
                .title("Sample Book")
                .author("Sample Author")
                .isbn("123456789")
                .synopsis("Sample Synopsis")
                .build();

        BookResponse mockResponse = BookResponse.builder()
                .id(bookId)
                .title("Sample Book")
                .authorName("Sample Author")
                .isbn("123456789")
                .synopsis("Sample Synopsis")
                .build();

        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));
        when(mapper.toBookResponse(mockBook)).thenReturn(mockResponse);

        // When
        BookResponse result = bookService.findById(bookId);

        // Then
        assertNotNull(result);
        assertEquals(bookId, result.id());
        assertEquals("Sample Book", result.title());
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
    }

    @Test
    void testFindById_NotFound() {
        // Given
        Integer bookId = 1;

        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.findById(bookId);
        });

        assertEquals("No book found with ID:: " + bookId, exception.getMessage());
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
    }

    @Test
    void testFindAllBooks_Success() {
        // Given
        int page = 0;
        int size = 5;
        Integer userId = 1;

        User mockUser = new User();
        mockUser.setId(userId);

        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Simulate the data from the repository
        Book mockBook = Book.builder()
                .title("Sample Book")
                .author("Sample Author")
                .build();

        Page<Book> mockPage = new PageImpl<>(List.of(mockBook), pageable, 1);
        when(bookRepository.findAllDisplayableBooks(pageable, userId)).thenReturn(mockPage);

        // Simulate the mapping from Book to BookResponse
        BookResponse mockBookResponse = BookResponse.builder()
                .title("Sample Book")
                .authorName("Sample Author")
                .build();

        when(mapper.toBookResponse(mockBook)).thenReturn(mockBookResponse);

        // When
        PageResponse<BookResponse> result = bookService.findAllBooks(page, size, connectedUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(page, result.getNumber());
        assertEquals(size, result.getSize());
        assertEquals(1, result.getContent().size());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(bookRepository, times(1)).findAllDisplayableBooks(pageable, userId);
    }

    @Test
    void testFindAllBooks_EmptyResult() {
        // Given
        int page = 0;
        int size = 5;
        Integer userId = 1;

        User mockUser = new User();
        mockUser.setId(userId);

        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Simulate an empty page from the repository
        Page<Book> mockPage = new PageImpl<>(List.of(), pageable, 0);
        when(bookRepository.findAllDisplayableBooks(pageable, userId)).thenReturn(mockPage);

        // When
        PageResponse<BookResponse> result = bookService.findAllBooks(page, size, connectedUser);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(bookRepository, times(1)).findAllDisplayableBooks(pageable, userId);
    }

    @Test
    void testFindAllBooksByOwner_Success() {
        // Given
        int page = 0;
        int size = 5;
        Integer userId = 1;

        User mockUser = new User();
        mockUser.setId(userId);

        // Mock the connectedUser to return the mockUser as the principal
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Simulate the data from the repository
        Book mockBook = Book.builder()
                .title("Sample Book")
                .author("Sample Author")
                .build();

        Page<Book> mockPage = new PageImpl<>(List.of(mockBook), pageable, 1);
        when(bookRepository.findByCreatedBy(userId, pageable)).thenReturn(mockPage);

        // Simulate the mapping from Book to BookResponse
        BookResponse mockBookResponse = BookResponse.builder()
                .title("Sample Book")
                .authorName("Sample Author")
                .build();

        when(mapper.toBookResponse(mockBook)).thenReturn(mockBookResponse);

        // When
        PageResponse<BookResponse> result = bookService.findAllBooksByOwner(page, size, connectedUser);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(page, result.getNumber());
        assertEquals(size, result.getSize());
        assertEquals(1, result.getContent().size());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(bookRepository, times(1)).findByCreatedBy(userId, pageable);
    }

    @Test
    void testFindAllBooksByOwner_EmptyResult() {
        // Given
        int page = 0;
        int size = 5;
        Integer userId = 1;

        User mockUser = new User();
        mockUser.setId(userId);

        // Mock the connectedUser to return the mockUser as the principal
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        // Simulate an empty page from the repository
        Page<Book> mockPage = new PageImpl<>(List.of(), pageable, 0);
        when(bookRepository.findByCreatedBy(userId, pageable)).thenReturn(mockPage);

        // When
        PageResponse<BookResponse> result = bookService.findAllBooksByOwner(page, size, connectedUser);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.getContent().isEmpty());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(bookRepository, times(1)).findByCreatedBy(userId, pageable);
    }

    @Test
    void testAllMyBorrowedBooks() {
        // Arrange
        int page = 0;
        int size = 10;

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Create a mock BookTransactionHistory
        BookTransactionHistory borrowedBook = new BookTransactionHistory();
        borrowedBook.setId(1); // Set an ID for the book transaction history
        // Set other properties if needed

        // Set up a list and page response for the repository
        List<BookTransactionHistory> historyList = List.of(borrowedBook);
        Page<BookTransactionHistory> pageResponse = new PageImpl<>(historyList, PageRequest.of(page, size), historyList.size());

        // Define behavior for the repository mock
        when(bookTransactionHistoryRepository.findAllMyBorrowedBooks(any(Pageable.class), anyInt())).thenReturn(pageResponse);

        // Mock the mapper response
        BorrowedBookResponse borrowedBookResponse = new BorrowedBookResponse();
        borrowedBookResponse.setId(1);
        borrowedBookResponse.setTitle("Test Book");
        borrowedBookResponse.setAuthorName("Test Author");
        borrowedBookResponse.setIsbn("1234567890");
        borrowedBookResponse.setRate(4.5);
        borrowedBookResponse.setReturned(false);
        borrowedBookResponse.setReturnApproved(true);

        when(mapper.toBorrowedBookResponse(borrowedBook)).thenReturn(borrowedBookResponse);

        // Act
        PageResponse<BorrowedBookResponse> result = bookService.allMyBorrowedBooks(page, size, connectedUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(borrowedBookResponse, result.getContent().get(0));
        assertEquals(page, result.getNumber());
        assertEquals(size, result.getSize());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        // Verify interactions
        verify(bookTransactionHistoryRepository, times(1)).findAllMyBorrowedBooks(any(Pageable.class), eq(1));
        verify(mapper, times(1)).toBorrowedBookResponse(any(BookTransactionHistory.class));
    }


    @Test
    void testUpdateShareableStatus() {
        // Arrange
        Integer bookId = 1;
        Book mockBook = new Book();
        mockBook.setId(Math.toIntExact(Long.valueOf(bookId)));
        mockBook.setCreatedBy(2); // Assume created by user ID 2
        mockBook.setShareable(true); // Initially shareable

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Define behavior for the book repository mock
        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));

        // Act
        Integer result = bookService.updateShareableStatus(bookId, connectedUser);

        // Assert
        assertEquals(bookId, result);
        assertFalse(mockBook.isShareable()); // Shareable status should be toggled

        // Verify interactions
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
        verify(bookRepository, times(1)).save(mockBook);
    }





    @Test
    void testUpdateShareableStatus_EntityNotFound() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Arrange
        when(bookRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            bookService.updateShareableStatus(1, connectedUser);
        });

        assertEquals("No book found with ID:: 1", exception.getMessage());
    }

    @Test
    void testUpdateArchivedStatus() {
        // Arrange
        Integer bookId = 1;
        Book mockBook = new Book();
        mockBook.setId(Math.toIntExact(Long.valueOf(bookId)));
        mockBook.setCreatedBy(1); // Assume created by user ID 1
        mockBook.setArchived(false); // Initially not archived

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Define behavior for the book repository mock
        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));

        // Act
        Integer result = bookService.updateArchivedStatus(bookId, connectedUser);

        // Assert
        assertEquals(bookId, result);
        assertTrue(mockBook.isArchived()); // Archived status should be toggled

        // Verify interactions
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
        verify(bookRepository, times(1)).save(mockBook);
    }

    @Test
    void testUpdateArchivedStatus_NotOwner() {
        // Arrange
        Integer bookId = 1;
        Book mockBook = new Book();
        mockBook.setId(Math.toIntExact(Long.valueOf(bookId)));
        mockBook.setCreatedBy(2); // Assume created by user ID 2

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Define behavior for the book repository mock
        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));

        // Act & Assert
        assertThrows(OperationNotPermittedException.class, () -> bookService.updateArchivedStatus(bookId, connectedUser));

        // Verify interactions
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
        verify(bookRepository, never()).save(mockBook); // Save should not be called
    }

    @Test
    void testBorrowBook_ArchivedOrNotShareable() {
        // Arrange
        Integer bookId = 1;
        Book mockBook = new Book();
        mockBook.setId(Math.toIntExact(Long.valueOf(bookId)));
        mockBook.setShareable(false); // Not shareable
        mockBook.setArchived(false); // Not archived

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Define behavior for the book repository mock
        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));

        // Act & Assert
        assertThrows(OperationNotPermittedException.class, () -> bookService.borrowBook(bookId, connectedUser));

        // Verify interactions
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
        verify(bookTransactionHistoryRepository, never()).isAlreadyBorrowed(bookId); // Not called
    }

    @Test
    void testBorrowBook_OwnerCannotBorrowOwnBook() {
        // Arrange
        Integer bookId = 1;
        Book mockBook = new Book();
        mockBook.setId(Math.toIntExact(Long.valueOf(bookId)));
        mockBook.setCreatedBy(1); // Assume created by user ID 1
        mockBook.setShareable(true); // Initially shareable
        mockBook.setArchived(false); // Not archived

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Define behavior for the book repository mock
        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));

        // Act & Assert
        assertThrows(OperationNotPermittedException.class, () -> bookService.borrowBook(bookId, connectedUser));

        // Verify interactions
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
        verify(bookTransactionHistoryRepository, never()).isAlreadyBorrowed(bookId); // Not called
    }

    @Test
    void testReturnBorrowedBook_NotOwner() {
        // Arrange
        Integer bookId = 1;
        Book mockBook = new Book();
        mockBook.setId(Math.toIntExact(Long.valueOf(bookId)));
        mockBook.setArchived(false); // Not archived
        mockBook.setShareable(true); // Shareable

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(2); // Assume user ID is 2
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Define behavior for the book repository mock
        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));

        // Mocking return behavior when user did not borrow the book
        when(bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, connectedUser.getName()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OperationNotPermittedException.class, () -> bookService.returnBorrowedBook(bookId, connectedUser));

        // Verify interactions
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
        verify(bookTransactionHistoryRepository, times(1)).findByBookIdAndUserId(bookId, connectedUser.getName());
    }

    @Test
    void testUploadBookCoverPicture() {
        // Arrange
        Integer bookId = 1;
        Book mockBook = new Book();
        mockBook.setId(Math.toIntExact(Long.valueOf(bookId)));

        // Mock user and authentication
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Mock MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test_cover.jpg"); // Example filename

        // Define behavior for the book repository mock
        when(bookRepository.findById(Long.valueOf(bookId))).thenReturn(Optional.of(mockBook));

        // Mock file storage service
        String filePath = "path/to/file/test_cover.jpg";
        when(fileStorageService.saveFileToServer(mockFile, mockUser.getName())).thenReturn(filePath);

        // Act
        bookService.uploadBookCoverPicture(mockFile, connectedUser, bookId);

        // Assert
        assertEquals(filePath, mockBook.getBookCover()); // The book cover path should be updated

        // Verify interactions
        verify(bookRepository, times(1)).findById(Long.valueOf(bookId));
        verify(fileStorageService, times(1)).saveFileToServer(mockFile, mockUser.getName());
        verify(bookRepository, times(1)).save(mockBook);
    }
    @Test
    void testAllBooksBorrowedFromMe() {
        // Arrange
        int page = 0;
        int size = 10;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1); // Assume user ID is 1
        Authentication connectedUser = mock(Authentication.class);
        when(connectedUser.getPrincipal()).thenReturn(mockUser);

        // Mock pagination and book transaction history
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        BookTransactionHistory mockHistory1 = new BookTransactionHistory();
        BookTransactionHistory mockHistory2 = new BookTransactionHistory();
        List<BookTransactionHistory> mockHistories = Arrays.asList(mockHistory1, mockHistory2);
        Page<BookTransactionHistory> mockPage = new PageImpl<>(mockHistories, pageable, mockHistories.size());

        when(bookTransactionHistoryRepository.findAllBooksBorrowedFromMe(pageable, mockUser.getId())).thenReturn(mockPage);
        when(mapper.toBorrowedBookResponse(mockHistory1)).thenReturn(new BorrowedBookResponse(/* parameters */));
        when(mapper.toBorrowedBookResponse(mockHistory2)).thenReturn(new BorrowedBookResponse(/* parameters */));

        // Act
        PageResponse<BorrowedBookResponse> result = bookService.allBooksBorrowedFromMe(page, size, connectedUser);

        // Assert
        assertNotNull(result);
        assertEquals(mockPage.getContent().size(), result.getContent().size());
        assertEquals(mockPage.getNumber(), result.getNumber()); // Retrieves the page number
        assertEquals(mockPage.getSize(), result.getSize());
        assertEquals(mockPage.getTotalElements(), result.getTotalElements());
        assertEquals(mockPage.getTotalPages(), result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        // Verify interactions
        verify(bookTransactionHistoryRepository, times(1)).findAllBooksBorrowedFromMe(pageable, mockUser.getId());
    }

}
