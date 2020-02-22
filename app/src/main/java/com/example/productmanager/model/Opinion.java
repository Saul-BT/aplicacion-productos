package com.example.productmanager.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Opinion {
    private String date;
    private String author;
    private String message;

    private SimpleDateFormat dateFormat;

    public Opinion(String date, String author, String message) {
        this.date = date;
        this.author = author;
        this.message = message;

        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    }

    public Opinion() { }

    public String getDate() {
        return date;
    }

    public String getDateFormatted() {
        Date date = new Date(Long.parseLong(this.date));
        return dateFormat.format(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
