package com.librishuffle.bookinfo;

import com.google.common.collect.ImmutableList;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BookDaoTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_empty_query() throws IOException {
        BookDao
                .newBuilder()
                .cachingEnabled(false)
                .build()
                .search("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_null_query() throws IOException {
        BookDao
                .newBuilder()
                .cachingEnabled(false)
                .build()
                .search(null);
    }

    @Test
    public void test_non_result_query() throws IOException {
        final List<Book> books = BookDao
                .newBuilder()
                .cachingEnabled(false)
                .build()
                .search("grmblpfrmpft");

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    public void test_query() throws IOException {

        BookDao bookDao = BookDao
                .newBuilder()
                .cachingEnabled(false)
                .build();

        final Iterable<String> terms = ImmutableList.of(
                "rumo und die wunder im dunkeln",
                "the stand, das letzte gefecht",
                "mohammed, eine abrechnung",
                "walter moers",
                "stephen king"
        );

        for (String term : terms) {

            boolean atLeastOneSubtitleFound = false;
            boolean atLeastOnePublishedDateFound = false;
            boolean atLeastOnePageCountFound = false;
            boolean atLeastOneReloadedFound = false;
            boolean atLeastOneISBN13Found = false;
            boolean atLeastOneISBN10Found = false;
            boolean atLeastOneLanguageFound = false;
            boolean atLeastOneThumbnailFound = false;
            boolean atLeastOneThumbnailSmallFound = false;

            final List<Book> books = bookDao.search(term);

            assertNotNull(books);
            assertFalse(books.isEmpty());

            for (Book book : books) {
                assertNotNull(book);
                assertFalse(isNullOrEmpty(book.getAuthor()));
                assertFalse(isNullOrEmpty(book.getTitle()));
                atLeastOnePageCountFound |= book.getPageCount().isPresent();
                atLeastOneSubtitleFound |= book.getSubTitle().isPresent();
                atLeastOnePublishedDateFound |= book.getPublishedDate().isPresent();
                atLeastOneISBN13Found |= book.getIsbn13().isPresent();
                atLeastOneISBN10Found |= book.getIsbn10().isPresent();
                atLeastOneLanguageFound |= book.getLanguage().isPresent();
                atLeastOneISBN13Found |= book.getIsbn13().isPresent();
                atLeastOneThumbnailFound |= book.getThumbnailUrl().isPresent();
                atLeastOneThumbnailSmallFound |= book.getSmallThumbnailUrl().isPresent();

                Optional<Book> reloaded;

                if (book.getIsbn13().isPresent()) {
                    reloaded = bookDao.getByIsbn(book.getIsbn13().get());
                } else if (book.getIsbn10().isPresent()) {
                    reloaded = bookDao.getByIsbn(book.getIsbn10().get());
                } else {
                    reloaded = Optional.empty();
                }

                if (reloaded.isPresent()) {
                    atLeastOneReloadedFound = true;
                    assertEquals(book, reloaded.get());
                }
            }

            assertTrue(atLeastOneSubtitleFound);
            assertTrue(atLeastOnePublishedDateFound);
            assertTrue(atLeastOnePageCountFound);
            assertTrue(atLeastOneReloadedFound);
            assertTrue(atLeastOneISBN13Found);
            assertTrue(atLeastOneISBN10Found);
            assertTrue(atLeastOneLanguageFound);
            assertTrue(atLeastOneThumbnailFound);
            assertTrue(atLeastOneThumbnailSmallFound);
        }
    }
}