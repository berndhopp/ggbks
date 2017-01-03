package com.librishuffle.bookinfo;

import com.google.common.base.Objects;

import java.net.URL;
import java.util.Optional;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkNotNull;

public class Book {
    private final String isbn13;
    private final String isbn10;
    private final String title;
    private final String subTitle;
    private final String author;
    private final String publishedDate;
    private final short pageCount;
    private final String language;
    private final URL thumbnailUrl;
    private final URL smallThumbnailUrl;

    Book(Item item) {
        checkNotNull(item);

        VolumeInfo volumeInfo = checkNotNull(item.volumeInfo);

        this.publishedDate = volumeInfo.publishedDate;
        this.pageCount = volumeInfo.pageCount;

        this.isbn13 = volumeInfo.industryIdentifiers
                .stream()
                .filter(identifier -> "ISBN_13".equals(identifier.type))
                .map(industryIdentifier -> industryIdentifier.identifier)
                .findFirst()
                .orElse("");

        this.isbn10 = volumeInfo.industryIdentifiers
                .stream()
                .filter(i -> "ISBN_10".equals(i.type))
                .map(i -> i.identifier)
                .findFirst()
                .orElse("");

        this.title = volumeInfo.title;
        this.subTitle = volumeInfo.subtitle;
        this.author = on(',').join(volumeInfo.authors);

        if (volumeInfo.imageLinks != null) {
            this.thumbnailUrl = volumeInfo.imageLinks.thumbNail;
            this.smallThumbnailUrl = volumeInfo.imageLinks.smallThumbNail;
        } else {
            this.thumbnailUrl = this.smallThumbnailUrl = null;
        }

        this.language = volumeInfo.language;
    }

    public Optional<String> getIsbn13() {
        return Optional.ofNullable(isbn13);
    }

    public Optional<String> getIsbn10() {
        return Optional.ofNullable(isbn10);
    }

    public String getTitle() {
        return title;
    }

    public Optional<String> getSubTitle() {
        return Optional.ofNullable(subTitle);
    }

    public String getAuthor() {
        return author;
    }

    public Optional<String> getPublishedDate() {
        return Optional.ofNullable(publishedDate);
    }

    public short getPageCount() {
        return pageCount;
    }

    public Optional<String> getLanguage() {
        return Optional.ofNullable(language);
    }

    public Optional<URL> getThumbnailUrl() {
        return Optional.ofNullable(thumbnailUrl);
    }

    public Optional<URL> getSmallThumbnailUrl() {
        return Optional.ofNullable(smallThumbnailUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return getPageCount() == book.getPageCount() &&
                Objects.equal(getIsbn13(), book.getIsbn13()) &&
                Objects.equal(getIsbn10(), book.getIsbn10()) &&
                Objects.equal(getTitle(), book.getTitle()) &&
                Objects.equal(getSubTitle(), book.getSubTitle()) &&
                Objects.equal(getAuthor(), book.getAuthor()) &&
                Objects.equal(getPublishedDate(), book.getPublishedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIsbn13(), getIsbn10(), getTitle(), getSubTitle(), getAuthor(), getPublishedDate(), getPageCount());
    }
}
