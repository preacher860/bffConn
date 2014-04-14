package com.lanouette.app.shared;

public class ServerPaths {
    public static final String BASE_URL="";
    //public static final String BASE_URL = "http://lanouette.ca:8000/";
    public static final String PATH_BASE = "/bffConn";
    public static final String SERVER_PATH_BASE = BASE_URL + PATH_BASE;

    public static final String RANDOM_VALUE_PARAM = "rnd_value";
    public static final String REQUEST_MODE_PARAM = "request_mode";
    public static final String USER_ID_PARAM = "user_id";
    public static final String SESSION_ID_PARAM = "session_id";

    public static final String GET_SFP_INFO_PATH = SERVER_PATH_BASE + "m=GSI&p={portId}";

    public static final String GET_MOTD_PATH = "/get_motd";
    public static final String GET_MOTD_PARAMS = "/{userId}/{sessionId}/{rndValue}";
    public static final String GET_MOTD_PATH_FULL = SERVER_PATH_BASE + GET_MOTD_PATH + GET_MOTD_PARAMS;
}

