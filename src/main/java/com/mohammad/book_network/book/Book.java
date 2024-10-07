package com.mohammad.book_network.book;

import com.mohammad.book_network.common.BaseEntity;
import com.mohammad.book_network.feedback.FeedBack;
import com.mohammad.book_network.history.BookTransactionHistory;
import com.mohammad.book_network.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String title;
    private String author;
    private String isbn; // The International Standard Book Number (ISBN) is a unique identifier for the book, typically used for cataloging and selling purposes.
    private String synopsis; // A brief summary or description of the book’s content
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<FeedBack> feedbacks;

    @OneToMany
    private List<BookTransactionHistory> histories;


    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(FeedBack::getReview)
                .average()
                .orElse(0.0);

        return rate;
    }
}
