package com.mohammad.book_network.feedback;

import com.mohammad.book_network.book.Book;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public FeedBack toFeedback(FeedbackRequest request, Book book) {
        return FeedBack.builder()
                .review(request.note())
                .comment(request.comment())
                .book(book)
                .build();
    }

    public FeedbackResponse toFeedbackResponse(FeedBack feedback, Integer id) {
        return FeedbackResponse.builder()
                .note(feedback.getReview())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), id))
                .build();
    }
}