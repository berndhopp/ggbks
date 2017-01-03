this is a very simple library to access google books

``` java

    BookDao bookDao = BookDao
        .builder()
        .caching(true)
        .build();
        
    List<Book> books = bookDao.search("new Testament");
    
    System.out.println("searching for 'new Testament'");
    
    for(Book book: book) {
        System.out.println("found " + book.getTitle() + " by " + book.getAuthors());
    }
    
    System.out.println("searching for ISBN 9780310435259");
    
    Book bible = bookDao.getByIsbn("9780310435259");
```
