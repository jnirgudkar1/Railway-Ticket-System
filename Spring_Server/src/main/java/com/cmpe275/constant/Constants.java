package com.cmpe275.constant;

/**
 * @author arunabh.shrivastava
 */
public class Constants {

    public static Long DEFAULT_TRAIN_CAPACITY = 1000L;
    public static final String NORTHBOUND_NAME_PREFIX = "NB";
    public static final String SOUTHBOUND_NAME_PREFIX = "SB";
    public static final String INVALID_SEARCH_REQUEST = "Invalid Request: Departure Time, To Station and From Station" +
            "cannot be left blank";

    public static final String PARAMETER_VALUE_ANY = "any";
    public static final String PARAMETER_VALUE_EMPTY = "";

    public static final String CONNECTION_TYPE_NONE = "none";
    public static final String CONNECTION_TYPE_ONE = "one";

    public static final String TRAIN_TYPE_EXPRESS = "express";

    public static final int DEPARTURE_TIME_TWO_HOUR_WAITING_TIME = 120;
    public static final int DEPARTURE_TIME_FIVE_HOUR_WAITING_TIME = 300;
    public static final int DEPARTURE_TIME_MIN_WAITING_TIME = 5;
}

