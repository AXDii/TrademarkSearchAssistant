package com.example.newproject.web.domain;

import com.example.newproject.web.cons.CommonConstant;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.UUID;

public abstract class BaseDomain<T> implements Serializable {

    private String statusCode = CommonConstant.INITIAL_STATE;//初始状态
    private String userPhone;
    private UUID sessionID;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public UUID getSessionID() {
        return sessionID;
    }

    public void setSessionID(UUID sessionID) {
        this.sessionID = sessionID;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

}
