package com.example.android.tj;

class Constants {


    static final int NOTIFICATION_ID = 1337;
    static final String NOTIFICATION_CHANNEL_ID = "com.example.android.tj";

    //for sending to tjservice
    static final String SERVICE_CMD = "service_cmd";
    static final int SERVICE_CMD_SYNC = 0;
    static final int SERVICE_CMD_PLAY = 1;
    static final int SERVICE_CMD_PAUSE = 2;
    static final int SERVICE_CMD_PRIORITY_SHUFFLE = 3;
    static final int SERVICE_CMD_SEEK = 4;
    static final int SERVICE_CMD_START = 5; //different from PLAY
    static final int SERVICE_CMD_PLAY_FROM = 6;
    static final int SERVICE_CMD_NEXT = 7;
    static final int SERVICE_CMD_PREVIOUS = 8;
    static final int SERVICE_CMD_SORT = 9;
    static final int SERVICE_CMD_SHUFFLE = 10;


    static final String SERVICE_RESULT = "service_result";
    static final String SERVICE_RESULT_STATUS = "service_result_status";
}
