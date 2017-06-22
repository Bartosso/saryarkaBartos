package com.turlygazhy.entity;

/**
 * Created by Eshu on 19.06.2017.
 */
public class Event {
    private long    id;
    private String  EVENT_NAME;
    private String  PLACE;
    private String  WHEN;
    private String  RULES;
    private String  PHOTO;
    private String  VIDEO;
    private String  CONTACT_INFORMATION;
    private boolean ADMIN_ACKNOWLEDGE;

    public long    getId() {return id;}

    public String  getEVENT_NAME() {return EVENT_NAME;}

    public String  getPLACE() {return PLACE;}

    public String  getWHEN() {return WHEN;}

    public String  getRULES() {return RULES;}

    public String  getPHOTO() {return PHOTO;}

    public String  getVIDEO() {return VIDEO;}

    public String  getCONTACT_INFORMATION() {return CONTACT_INFORMATION;}

    public boolean isADMIN_ACKNOWLEDGE() {return ADMIN_ACKNOWLEDGE;}

    public void setId(long id) {this.id = id;}

    public void setEVENT_NAME(String EVENT_NAME) {this.EVENT_NAME = EVENT_NAME;}

    public void setPLACE(String PLACE) {this.PLACE = PLACE;}

    public void setWHEN(String WHEN) {this.WHEN = WHEN;}

    public void setRULES(String RULES) {this.RULES = RULES;}

    public void setPHOTO(String PHOTO) {this.PHOTO = PHOTO;}

    public void setVIDEO(String VIDEO) {this.VIDEO = VIDEO;}

    public void setCONTACT_INFORMATION(String CONTACT_INFORMATION) {this.CONTACT_INFORMATION = CONTACT_INFORMATION;}

    public void setADMIN_ACKNOWLEDGE(boolean ADMIN_ACKNOWLEDGE) {this.ADMIN_ACKNOWLEDGE = ADMIN_ACKNOWLEDGE;}
}
