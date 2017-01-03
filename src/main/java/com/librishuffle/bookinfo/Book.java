package com.librishuffle.bookinfo;

import com.google.common.base.Objects;

import java.net.URL;
import java.util.Optional;

import static com.google.common.base.Joiner.on;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

public class Book {
    private final String title;
    private final String subTitle;
    private final String author;
    private final String publishedDate;
    private final short pageCount;
    private final String language;
    private final URL thumbnailUrl;
    private final URL smallThumbnailUrl;
    private String isbn13;
    private String isbn10;

    Book(Item item) {
        checkNotNull(item);

        VolumeInfo volumeInfo = checkNotNull(item.volumeInfo);

        this.publishedDate = volumeInfo.publishedDate;
        this.pageCount = volumeInfo.pageCount;

        for (IndustryIdentifier industryIdentifier : volumeInfo.industryIdentifiers) {
            switch (industryIdentifier.type) {
                case "ISBN_10":
                    this.isbn10 = industryIdentifier.identifier;
                    break;
                case "ISBN_13":
                    this.isbn13 = industryIdentifier.identifier;
                    break;
            }
        }

        checkArgument(!isNullOrEmpty(volumeInfo.title));

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
        return Optional.of(isbn13);
    }

    public Optional<String> getIsbn10() {
        return Optional.of(isbn10);
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

    public Optional<Short> getPageCount() {
        return pageCount == 0 ? Optional.empty() : Optional.of(pageCount);
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
        return Objects.equal(getPageCount(), book.getPageCount()) &&
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
