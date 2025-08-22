package org.example.designPatterns.iteratorPattern;

import java.util.*;

/**
 * Iterator Pattern Example: Book Collection System
 * 
 * This example demonstrates the Iterator Pattern with a book collection
 * that supports multiple types of iteration (by genre, by author, chronological).
 */

// Book class representing items in our collection
class Book {
    private String title;
    private String author;
    private String genre;
    private int publicationYear;
    
    public Book(String title, String author, String genre, int publicationYear) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publicationYear = publicationYear;
    }
    
    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getPublicationYear() { return publicationYear; }
    
    @Override
    public String toString() {
        return String.format("%s by %s (%s, %d)", title, author, genre, publicationYear);
    }
}

// Custom iterator interface
interface BookIterator {
    boolean hasNext();
    Book next();
    void remove();
}

// Concrete iterator for genre-based iteration
class GenreIterator implements BookIterator {
    private List<Book> books;
    private String targetGenre;
    private int position = 0;
    private Book lastReturned = null;
    
    public GenreIterator(List<Book> books, String genre) {
        this.books = new ArrayList<>(books);
        this.targetGenre = genre;
        // Filter books by genre
        this.books.removeIf(book -> !book.getGenre().equalsIgnoreCase(genre));
    }
    
    @Override
    public boolean hasNext() {
        return position < books.size();
    }
    
    @Override
    public Book next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more books in " + targetGenre + " genre");
        }
        lastReturned = books.get(position);
        position++;
        return lastReturned;
    }
    
    @Override
    public void remove() {
        if (lastReturned == null) {
            throw new IllegalStateException("next() must be called before remove()");
        }
        books.remove(lastReturned);
        position--;
        lastReturned = null;
    }
}

// Concrete iterator for author-based iteration
class AuthorIterator implements BookIterator {
    private List<Book> books;
    private String targetAuthor;
    private int position = 0;
    private Book lastReturned = null;
    
    public AuthorIterator(List<Book> books, String author) {
        this.books = new ArrayList<>(books);
        this.targetAuthor = author;
        // Filter books by author
        this.books.removeIf(book -> !book.getAuthor().equalsIgnoreCase(author));
    }
    
    @Override
    public boolean hasNext() {
        return position < books.size();
    }
    
    @Override
    public Book next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more books by " + targetAuthor);
        }
        lastReturned = books.get(position);
        position++;
        return lastReturned;
    }
    
    @Override
    public void remove() {
        if (lastReturned == null) {
            throw new IllegalStateException("next() must be called before remove()");
        }
        books.remove(lastReturned);
        position--;
        lastReturned = null;
    }
}

// Concrete iterator for chronological iteration
class ChronologicalIterator implements BookIterator {
    private List<Book> books;
    private int position = 0;
    private Book lastReturned = null;
    
    public ChronologicalIterator(List<Book> books) {
        this.books = new ArrayList<>(books);
        // Sort books by publication year
        this.books.sort(Comparator.comparing(Book::getPublicationYear));
    }
    
    @Override
    public boolean hasNext() {
        return position < books.size();
    }
    
    @Override
    public Book next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more books");
        }
        lastReturned = books.get(position);
        position++;
        return lastReturned;
    }
    
    @Override
    public void remove() {
        if (lastReturned == null) {
            throw new IllegalStateException("next() must be called before remove()");
        }
        books.remove(lastReturned);
        position--;
        lastReturned = null;
    }
}

// Book collection class (Aggregate)
class BookCollection {
    private List<Book> books;
    
    public BookCollection() {
        this.books = new ArrayList<>();
    }
    
    public void addBook(Book book) {
        books.add(book);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
    }
    
    // Factory methods for different types of iterators
    public BookIterator createGenreIterator(String genre) {
        return new GenreIterator(books, genre);
    }
    
    public BookIterator createAuthorIterator(String author) {
        return new AuthorIterator(books, author);
    }
    
    public BookIterator createChronologicalIterator() {
        return new ChronologicalIterator(books);
    }
    
    // Default iterator (all books)
    public BookIterator createIterator() {
        return new BookIterator() {
            private int position = 0;
            private Book lastReturned = null;
            
            @Override
            public boolean hasNext() {
                return position < books.size();
            }
            
            @Override
            public Book next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more books");
                }
                lastReturned = books.get(position);
                position++;
                return lastReturned;
            }
            
            @Override
            public void remove() {
                if (lastReturned == null) {
                    throw new IllegalStateException("next() must be called before remove()");
                }
                books.remove(lastReturned);
                position--;
                lastReturned = null;
            }
        };
    }
    
    public int size() {
        return books.size();
    }
}

// Client code demonstrating the Iterator Pattern
public class BookCollectionExample {
    public static void main(String[] args) {
        // Create book collection
        BookCollection library = new BookCollection();
        
        // Add books
        library.addBook(new Book("1984", "George Orwell", "Dystopian Fiction", 1949));
        library.addBook(new Book("Animal Farm", "George Orwell", "Satire", 1945));
        library.addBook(new Book("The Hobbit", "J.R.R. Tolkien", "Fantasy", 1937));
        library.addBook(new Book("Lord of the Rings", "J.R.R. Tolkien", "Fantasy", 1954));
        library.addBook(new Book("Brave New World", "Aldous Huxley", "Dystopian Fiction", 1932));
        library.addBook(new Book("Foundation", "Isaac Asimov", "Science Fiction", 1951));
        library.addBook(new Book("Dune", "Frank Herbert", "Science Fiction", 1965));
        
        System.out.println("=== Book Collection Iterator Pattern Demo ===\n");
        
        // Demonstrate all books iterator
        System.out.println("All Books:");
        BookIterator allBooksIterator = library.createIterator();
        while (allBooksIterator.hasNext()) {
            System.out.println("- " + allBooksIterator.next());
        }
        
        // Demonstrate genre-based iterator
        System.out.println("\nFantasy Books:");
        BookIterator fantasyIterator = library.createGenreIterator("Fantasy");
        while (fantasyIterator.hasNext()) {
            System.out.println("- " + fantasyIterator.next());
        }
        
        // Demonstrate author-based iterator
        System.out.println("\nBooks by George Orwell:");
        BookIterator orwellIterator = library.createAuthorIterator("George Orwell");
        while (orwellIterator.hasNext()) {
            System.out.println("- " + orwellIterator.next());
        }
        
        // Demonstrate chronological iterator
        System.out.println("\nBooks in Chronological Order:");
        BookIterator chronoIterator = library.createChronologicalIterator();
        while (chronoIterator.hasNext()) {
            System.out.println("- " + chronoIterator.next());
        }
        
        // Demonstrate multiple simultaneous iterators
        System.out.println("\nDemonstrating Multiple Simultaneous Iterators:");
        BookIterator iterator1 = library.createGenreIterator("Science Fiction");
        BookIterator iterator2 = library.createAuthorIterator("J.R.R. Tolkien");
        
        System.out.println("Science Fiction books:");
        while (iterator1.hasNext()) {
            System.out.println("- " + iterator1.next());
        }
        
        System.out.println("Tolkien books:");
        while (iterator2.hasNext()) {
            System.out.println("- " + iterator2.next());
        }
        
        // Demonstrate iterator with removal
        System.out.println("\nRemoving Dystopian Fiction books:");
        BookIterator dystopianIterator = library.createGenreIterator("Dystopian Fiction");
        while (dystopianIterator.hasNext()) {
            Book book = dystopianIterator.next();
            System.out.println("Removing: " + book);
            dystopianIterator.remove();
        }
        
        System.out.println("\nRemaining books count: " + library.size());
    }
}

/*
Expected Output:
=== Book Collection Iterator Pattern Demo ===

All Books:
- 1984 by George Orwell (Dystopian Fiction, 1949)
- Animal Farm by George Orwell (Satire, 1945)
- The Hobbit by J.R.R. Tolkien (Fantasy, 1937)
- Lord of the Rings by J.R.R. Tolkien (Fantasy, 1954)
- Brave New World by Aldous Huxley (Dystopian Fiction, 1932)
- Foundation by Isaac Asimov (Science Fiction, 1951)
- Dune by Frank Herbert (Science Fiction, 1965)

Fantasy Books:
- The Hobbit by J.R.R. Tolkien (Fantasy, 1937)
- Lord of the Rings by J.R.R. Tolkien (Fantasy, 1954)

Books by George Orwell:
- 1984 by George Orwell (Dystopian Fiction, 1949)
- Animal Farm by George Orwell (Satire, 1945)

Books in Chronological Order:
- Brave New World by Aldous Huxley (Dystopian Fiction, 1932)
- The Hobbit by J.R.R. Tolkien (Fantasy, 1937)
- Animal Farm by George Orwell (Satire, 1945)
- 1984 by George Orwell (Dystopian Fiction, 1949)
- Foundation by Isaac Asimov (Science Fiction, 1951)
- Lord of the Rings by J.R.R. Tolkien (Fantasy, 1954)
- Dune by Frank Herbert (Science Fiction, 1965)

Demonstrating Multiple Simultaneous Iterators:
Science Fiction books:
- Foundation by Isaac Asimov (Science Fiction, 1951)
- Dune by Frank Herbert (Science Fiction, 1965)
Tolkien books:
- The Hobbit by J.R.R. Tolkien (Fantasy, 1937)
- Lord of the Rings by J.R.R. Tolkien (Fantasy, 1954)

Removing Dystopian Fiction books:
Removing: 1984 by George Orwell (Dystopian Fiction, 1949)
Removing: Brave New World by Aldous Huxley (Dystopian Fiction, 1932)

Remaining books count: 5
*/