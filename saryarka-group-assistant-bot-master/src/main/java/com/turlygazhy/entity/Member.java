package com.turlygazhy.entity;

/**
 * Created by user on 1/22/17.
 */
public class Member {
    private int id;
    private Integer userId;
    private long    chatId;
    private String  city;
    private String  nisha;
    private String  userName;
    private String  companyName;
    private String  contact;
    private String  FIO;
    private String  firstName;
    private String  lastName;
    private String  phoneNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }


    public String getNisha() {
        return nisha;
    }

    public void setNisha(String nisha) {
        this.nisha = nisha;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Member(int id,
                  Integer userId, long chatId, String nisha, String userName, String companyName, String contact, String FIO,
                  String firstName, String lastName, String phoneNumber, String city){
        this.id = id;
        this.userId=userId;
        this.chatId=chatId;
//        this.naviki=naviki;
        this.nisha = nisha;
        this.userName = userName;
        this.companyName = companyName;
        this.contact     = contact;
        this.FIO         = FIO;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.phoneNumber = phoneNumber;
        this.city        = city;
    }
    public Member(){

    }
}
