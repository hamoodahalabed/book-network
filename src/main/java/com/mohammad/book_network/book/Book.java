package com.mohammad.book_network.book;

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
public class Book extends BaseEntity {

    private String title;
    private String author;
    private String isbn; // The International Standard Book Number (ISBN) is a unique identifier for the book, typically used for cataloging and selling purposes.
    private String synopsis; // A brief summary or description of the book’s content
    private String bookCover;
    private boolean archived;
    private boolean shareable;

}
