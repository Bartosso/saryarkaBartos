package com.turlygazhy.entity;

import javax.print.DocFlavor;

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
    private String  CONTACT_INFORMATION;
    private boolean ADMIN_ACKNOWLEDGE;
    private boolean BY_ADMIN;
    private String  DRESS_CODE;
    private String  PROGRAM;
    private String  PAGE;
    private String  DOCUMENT;

    public long    getId()                                         {return id;}

    public String  getEVENT_NAME()                                 {return EVENT_NAME;}

    public String  getPLACE()                                      {return PLACE;}

    public String  getWHEN()                                       {return WHEN;}

    public String  getRULES()                                      {return RULES;}

    public String  getPHOTO()                                      {return PHOTO;}

    public String  getCONTACT_INFORMATION()                        {return CONTACT_INFORMATION;}

    public String getDRESS_CODE()                                  {return DRESS_CODE;}

    public String getPAGE()                                        {return PAGE;}

    public String getPROGRAM()                                     {return PROGRAM;}

    public boolean isADMIN_ACKNOWLEDGE()                           {return ADMIN_ACKNOWLEDGE;}

    public boolean isBY_ADMIN()                                    {return BY_ADMIN;}

    public String getDOCUMENT()                                    {return DOCUMENT;}

    public void setDOCUMENT(String DOCUMENT)                       {this.DOCUMENT = DOCUMENT;}

    public void setId(long id)                                     {this.id = id;}

    public void setEVENT_NAME(String EVENT_NAME)                   {this.EVENT_NAME = EVENT_NAME;}

    public void setPLACE(String PLACE)                             {this.PLACE = PLACE;}

    public void setWHEN(String WHEN)                               {this.WHEN = WHEN;}

    public void setRULES(String RULES)                             {this.RULES = RULES;}

    public void setPHOTO(String PHOTO)                             {this.PHOTO = PHOTO;}

    public void setCONTACT_INFORMATION(String CONTACT_INFORMATION) {this.CONTACT_INFORMATION = CONTACT_INFORMATION;}

    public void setADMIN_ACKNOWLEDGE(boolean ADMIN_ACKNOWLEDGE)    {this.ADMIN_ACKNOWLEDGE = ADMIN_ACKNOWLEDGE;}

    public void setDRESS_CODE(String DRESS_CODE)                   {this.DRESS_CODE = DRESS_CODE;}

    public void setPAGE(String PAGE)                               {this.PAGE = PAGE;}

    public void setPROGRAM(String PROGRAM)                         {this.PROGRAM = PROGRAM;}

    public void setBY_ADMIN(boolean BY_ADMIN)                      {this.BY_ADMIN = BY_ADMIN;}
}
