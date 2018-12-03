package com.example.android.tj;

class Constants {


    static final int NOTIFICATION_ID = 1337;
    static final String NOTIFICATION_CHANNEL_ID = "com.example.android.tj";

    static final String INTENT_PARAM_POSITION = "Position";
    static final String INTENT_PARAM_HASH = "Hash";

    //for sending to tjservice
    static final String SERVICE_CMD = "service_cmd";
    static final int SERVICE_CMD_SYNC = 0;
    static final int SERVICE_CMD_PLAY = 1;
    static final int SERVICE_CMD_PAUSE = 2;
    static final int SERVICE_CMD_PRIORITY_SHUFFLE = 3;
    static final int SERVICE_CMD_SHUFFLE = 31;
    static final int SERVICE_CMD_SEEK = 4;
    static final int SERVICE_CMD_START = 5; //different from PLAY
    static final int SERVICE_CMD_PLAY_FROM = 6;
    static final int SERVICE_CMD_PLAY_FROM_HASH = 61;
    static final int SERVICE_CMD_NEXT = 7;
    static final int SERVICE_CMD_PREVIOUS = 8;
    static final int SERVICE_CMD_SORT = 9;


    static final int SERVICE_QUERY_METADATA = 100;
    static final int SERVICE_QUERY_METADATA_BY_HASH = 1001;
    static final int SERVICE_QUERY_SEARCH = 101;

    static final int SERVICE_PATCH_METADATA = 200;


    static final String SERVICE_RESULT = "service_result"; // used in MainActivity
    static final String SERVICE_RESULT_STATUS = "service_result_status";
    static final String SERVICE_ANSWER = "service_answer"; // used in MetadataActivity and
    // SearchableActivity
    static final String SERVICE_ANSWER_METADATA = "service_answer_metadata";
    static final String SERVICE_ANSWER_SEARCH = "service_answer_search";
}
