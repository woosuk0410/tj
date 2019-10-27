package com.example.android.tj

internal object Constants {


    val NOTIFICATION_ID = 1337
    val NOTIFICATION_CHANNEL_ID = "com.example.android.tj"

    val INTENT_PARAM_POSITION = "Position"
    val INTENT_PARAM_HASH = "Hash"

    //for sending to tjservice
    val SERVICE_CMD = "service_cmd"
    val SERVICE_CMD_SYNC = 0
    val SERVICE_CMD_PLAY = 1
    val SERVICE_CMD_PAUSE = 2
    val SERVICE_CMD_PRIORITY_SHUFFLE = 3
    val SERVICE_CMD_SHUFFLE = 31
    val SERVICE_CMD_SEEK = 4
    val SERVICE_CMD_START = 5 //unused
    val SERVICE_CMD_PLAY_FROM = 6
    val SERVICE_CMD_PLAY_FROM_HASH = 61
    val SERVICE_CMD_NEXT = 7
    val SERVICE_CMD_PREVIOUS = 8
    val SERVICE_CMD_SORT = 9


    val SERVICE_QUERY_METADATA = 100
    val SERVICE_QUERY_METADATA_BY_HASH = 1001
    val SERVICE_QUERY_SEARCH = 101

    val SERVICE_PATCH_METADATA = 200


    val SERVICE_RESULT = "service_result" // used in MainActivity
    val SERVICE_RESULT_STATUS = "service_result_status"
    val SERVICE_ANSWER = "service_answer" // used in MetadataActivity and
    // SearchableActivity
    val SERVICE_ANSWER_METADATA = "service_answer_metadata"
    val SERVICE_ANSWER_SEARCH = "service_answer_search"
}
