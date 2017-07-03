package com.turlygazhy.entity;

/**
 * Created by Eshu on 16.06.2017.
 */
public class ListData {
    private int MemberId;
    private long id;
//    private String photo;
    private String text;
    private String date;

    public long getMemberId() {return MemberId;}

    public String getDate() {return date;}

//    public String getPhoto() {return photo;}

    public String getText() {return text;}

    public long getId() {return id;}

    public void setDate(String date) {this.date = date;}

    public void setMemberId(int memberId) {MemberId = memberId;}

//    public void setPhoto(String photo) {this.photo = photo;}

    public void setText(String text) {this.text = text;}

    public void setId(Long id) {this.id = id;}
}
