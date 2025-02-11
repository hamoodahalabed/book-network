package com.mohammad.book_network.feedback;

import com.mohammad.book_network.book.Book;
import com.mohammad.book_network.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class FeedBack extends BaseEntity {

    private double review;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;
}