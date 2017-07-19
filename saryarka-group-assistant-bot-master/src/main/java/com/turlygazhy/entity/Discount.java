package com.turlygazhy.entity;

/**
 * Created by Eshu on 22.06.2017.
 */
public class Discount {
    private int    id;
    private String discountType;
    private String name;
    private String textAbout;
    private String photo;
    private String address;
    private String page;
    private String discount;
    private String memberId;

    public int getId() {return id;}

    public String getDiscountType() {return discountType;}

    public String getName() {return name;}

    public String getTextAbout() {return textAbout;}

    public String getPhoto() {return photo;}

    public String getAddress() {return address;}

    public String getPage() {return page;}

    public String getDiscount() {return discount;}

    public String getMemberId() {return memberId;}

    public void setId(int id) {this.id = id;}

    public void setDiscountType(String discountType) {this.discountType = discountType;}

    public void setName(String name) {this.name = name;}

    public void setTextAbout(String textAbout) {this.textAbout = textAbout;}

    public void setPhoto(String photo) {this.photo = photo;}

    public void setAddress(String address) {this.address = address;}

    public void setPage(String page) {this.page = page;}

    public void setDiscount(String discount) {this.discount = discount;}

    public void setMemberId(String memberId) {this.memberId = memberId;}

    public Discount(int id, String discountType, String name, String textAbout, String photo, String address, String page, String discount, String memberId){
        this.id           = id;
        this.discountType = discountType;
        this.name         = name;
        this.textAbout    = textAbout;
        this.photo        = photo;
        this.address      = address;
        this.page         = page;
        this.discount     = discount;
        this.memberId     = memberId;
    }
    public Discount(String discountType, String name, String textAbout, String photo, String address, String page, String discount, String memberId){
        this.discountType = discountType;
        this.name         = name;
        this.textAbout    = textAbout;
        this.photo        = photo;
        this.address      = address;
        this.page         = page;
        this.discount     = discount;
        this.memberId     = memberId;
    }
    //That all for version 2 discount
    public Discount(int id,String discountType, String name, String discount, String photo){
        this.id           = id;
        this.photo        = photo;
        this.discountType = discountType;
        this.discount     = discount;
        this.name         = name;
    }

}
