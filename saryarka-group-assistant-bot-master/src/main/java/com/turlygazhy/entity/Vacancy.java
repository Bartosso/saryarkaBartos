package com.turlygazhy.entity;

/**
 * Created by Eshu on 26.06.2017.
 */
public class Vacancy {
    private long   id;
    private String companyName;
    private String sfera;
    private String experience;
    private String place;
    private String workingConditions;
    private String salary;
    private String contact;
    private String photo;
    private String memberId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSfera() {
        return sfera;
    }

    public void setSfera(String sfera) {
        this.sfera = sfera;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getWorkingConditions() {
        return workingConditions;
    }

    public void setWorkingConditions(String workingConditions) {
        this.workingConditions = workingConditions;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Vacancy(long id, String companyName, String sfera, String experience, String place, String workingConditions, String salary, String contact, String photo, String memberId) {
        this.id = id;
        this.companyName = companyName;
        this.sfera = sfera;
        this.experience = experience;
        this.place = place;
        this.workingConditions = workingConditions;
        this.salary = salary;
        this.contact = contact;
        this.photo = photo;
        this.memberId = memberId;
    }

    public Vacancy(String companyName, String sfera, String experience, String place, String workingConditions, String salary, String contact, String photo,String memberId) {
        this.companyName = companyName;
        this.sfera = sfera;
        this.experience = experience;
        this.place = place;
        this.workingConditions = workingConditions;
        this.salary = salary;
        this.contact = contact;
        this.photo = photo;
        this.memberId = memberId;
    }
}
