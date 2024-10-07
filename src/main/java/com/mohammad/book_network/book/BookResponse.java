package com.mohammad.book_network.book;

import lombok.Builder;


@Builder
public record BookResponse (

        Integer id,
        String title,
        String authorName,
        String isbn,
        String synopsis,
        String owner,
        byte[] cover,
        double rate,
        boolean archived,
        boolean shareable
) {
}
