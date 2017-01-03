package com.librishuffle.bookinfo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;

@SuppressWarnings("unused")
public class BookDao {
    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final String FIELDS_PARAMETER = "&fields=items(volumeInfo(title,subtitle,authors,publishedDate,industryIdentifiers,pageCount,imageLinks,language))";
    private static final String QUERY_TEMPLATE = BASE_URL + "?q=%s" + FIELDS_PARAMETER;
    private static final String ISBN_TEMPLATE = BASE_URL + "?q=isbn:%s" + FIELDS_PARAMETER;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String userAgent;
    private boolean cachingEnabled = true;
    private Cache<String, List<Book>> searchCache;
    private Cache<String, Book> bookCache;

    private BookDao() {
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public List<Book> search(String term) throws IOException {

        checkArgument(!isNullOrEmpty(term));

        term = term.toLowerCase();

        List<Book> list;

        if (cachingEnabled) {
            list = searchCache.getIfPresent(term);

            if (list != null) {
                return list;
            }
        }

        QueryResult queryResult;

        try (InputStream inputStream = fetch(QUERY_TEMPLATE, term)) {
            queryResult = objectMapper.readValue(inputStream, QueryResult.class);
        }

        checkNotNull(queryResult);
        checkNotNull(queryResult.getItems());

        list = new ArrayList<>(queryResult.getItems().size());

        for (Item item : queryResult.getItems()) {
            list.add(new Book(item));
        }

        if (cachingEnabled) {
            for (Book book : list) {
                if (book.getIsbn13().isPresent()) {
                    bookCache.put(book.getIsbn13().get(), book);
                }

                if (book.getIsbn10().isPresent()) {
                    bookCache.put(book.getIsbn10().get(), book);
                }
            }

            searchCache.put(term, list);
        }

        return list;
    }

    public Optional<Book> getByIsbn(String isbn) throws IOException {

        if (cachingEnabled) {
            Book book = bookCache.getIfPresent(isbn);

            if (book != null) {
                return Optional.of(book);
            }
        }

        QueryResult queryResult;

        try (InputStream inputStream = fetch(ISBN_TEMPLATE, isbn)) {
            queryResult = objectMapper.readValue(inputStream, QueryResult.class);
        }

        checkNotNull(queryResult);

        Optional<Book> bookOptional;

        if (queryResult.getItems().isEmpty()) {
            bookOptional = trySearchForIsbn(isbn);
        } else {
            bookOptional = queryResult
                    .getItems()
                    .stream()
                    .map(Book::new)
                    .findAny();
        }

        if (cachingEnabled && bookOptional.isPresent()) {
            bookCache.put(isbn, bookOptional.get());
        }

        return bookOptional;
    }

    private Optional<Book> trySearchForIsbn(String isbn) throws IOException {
        checkArgument(!isNullOrEmpty(isbn));

        //noinspection EqualsBetweenInconvertibleTypes
        return search(isbn)
                .stream()
                .filter(b -> b.getIsbn10().equals(isbn) || b.getIsbn13().equals(isbn))
                .findFirst();
    }

    private InputStream fetch(String format, Object term) throws IOException {
        try {
            URL url = new URL(format(format, encode(term.toString(), "UTF-8")));
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("Accept-Encoding", "gzip");
            urlConnection.addRequestProperty("User-Agent", userAgent);
            return new GZIPInputStream(urlConnection.getInputStream());
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            //should never happen
            throw new RuntimeException(e);
        }
    }

    public static class Builder {

        private String userAgent;
        private boolean enableCaching = true;
        private Cache<String, List<Book>> searchCache;
        private Cache<String, Book> bookCache;

        private Builder() {
        }

        public Builder userAgent(String userAgent) {
            checkArgument(!isNullOrEmpty(userAgent));
            checkArgument(userAgent.contains("gzip"), "userAgent must contain 'gzip'");
            this.userAgent = userAgent;
            return this;
        }

        public Builder searchCache(Cache<String, List<Book>> cache) {
            checkNotNull(cache);
            this.searchCache = cache;
            return this;
        }

        public Builder bookCache(Cache<String, Book> cache) {
            checkNotNull(cache);
            this.bookCache = cache;
            return this;
        }

        public Builder cachingEnabled(boolean cachingEnabled) {
            this.enableCaching = cachingEnabled;
            return this;
        }

        public BookDao build() {
            BookDao bookDao = new BookDao();

            bookDao.userAgent = userAgent != null ? userAgent : "librishuffle booklib ( gzip )";
            bookDao.cachingEnabled = enableCaching;

            if (enableCaching) {
                bookDao.bookCache = bookCache != null
                        ? bookCache
                        : CacheBuilder
                        .newBuilder()
                        .maximumSize(200)
                        .build();

                bookDao.searchCache = searchCache != null
                        ? searchCache
                        : CacheBuilder
                        .newBuilder()
                        .maximumSize(35)
                        .build();
            } else {
                checkArgument(bookCache == null, "bookCache is not null but enableCaching is set to false");
                checkArgument(searchCache == null, "searchCache is not null but enableCaching is set to false");
            }

            return bookDao;
        }
    }
}
