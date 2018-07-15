package com.gp.dao.model;

import java.util.Date;

public class BaseModel {
    public static final String YES ="y";
    public static final String IS_DELETED = "is_deleted";
    public static final String CREATOR    = "creator";
    public static final String MODIFIER    = "modifier";
    public static final String GMT_CREATE    = "gmt_create";
    public static final String GMT_MODIFIER    = "gmt_modifier";

    protected Long id;
    protected String isDeleted;
    protected String creator;
    protected String modifier;
    protected Date   gmtCreate;
    protected Date   gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
