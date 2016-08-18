package com.projects.karan.pufftracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;
import com.projects.karan.pufftracker.utils.Constants;

import java.util.HashMap;

/**
 * Created by ADMIN on 8/16/2016.
 */
public class Cigarette {
    private String deviceId;
    private HashMap<String, Object> timestamp;

    public Cigarette() {
    }

    public Cigarette(String deviceId) {
        this.deviceId = deviceId;
        HashMap<String, Object> timestampObj = new HashMap<String, Object>();
        timestampObj.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestamp = timestampObj;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public HashMap<String, Object> getTimestamp() {
        return timestamp;
    }


    @JsonIgnore
    public long getTimestampLong() {
        return (long)timestamp.get(Constants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
