package com.turlygazhy.entity;

/**
 * Created by Eshu on 30.06.2017.
 */
public class Book {
    private int    id;
    private String bookName;
    private String book;
    private String category;

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getBookName() {return bookName;}

    public void setBookName(String bookName) {this.bookName = bookName;}

    public String getBook() {return book;}

    public void setBook(String book) {this.book = book;}

    public String getCategory() {
        return category;
    }

    public Book(int id, String bookName, String book) {
        this.id = id;
        this.bookName = bookName;
        this.book = book;
    }
    public Book(int id, String bookName, String book, String category) {
        this.id       = id;
        this.bookName = bookName;
        this.book     = book;
        this.category = category;
    }
}
