package com.projects.karan.pufftracker.utils;

/**
 * Created by ADMIN on 8/16/2016.
 */
public class Constants {

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_DEVICE_ID = "deviceId";

    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where user lists are stored (ie "userLists")
     */
    public static final String FIREBASE_LOCATION_SMOKING ="smoking";
    public static final String FIREBASE_LOCATION_TOTAL_COUNT ="totalCount";
    public static final String FIREBASE_LOCATION_WELCOME_MESSAGE ="welcomeMessage";
    public static final String FIREBASE_LOCATION_TIP_OF_THE_DAY ="tipOfTheDay";
    public static final String FIREBASE_LOCATION_TRACKER ="tracker";
    public static final String FIREBASE_LOCATION_COUNT ="count";
    public static final String FIREBASE_LOCATION_MAX_COUNT ="maxCount";

    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = "https://letstictactoe.firebaseio.com/";
    public static final String FIREBASE_URL_SMOKING = FIREBASE_URL +"/"+FIREBASE_LOCATION_SMOKING;
    public static final String FIREBASE_URL_TRACKER = FIREBASE_URL_SMOKING + "/" + FIREBASE_LOCATION_TRACKER;

}