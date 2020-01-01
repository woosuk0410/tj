package com.example.android.tj

//TODO: use enum?
internal object Constants {


    val NOTIFICATION_ID = 1337
    val NOTIFICATION_CHANNEL_ID = "com.example.android.tj"

    //for sending to tjservice
    val SERVICE_CMD = "service_cmd"
    val SERVICE_CMD_SYNC = -1 // sync current playing status
    val SERVICE_CMD_SYNC_METADATA = -11 // sync metadata of all songs
    val SERVICE_CMD_PLAY = 1
    val SERVICE_CMD_PAUSE = 2
    val SERVICE_CMD_PRIORITY_SHUFFLE = 3
    val SERVICE_CMD_SHUFFLE = 31
    val SERVICE_CMD_SEEK = 4
    val SERVICE_CMD_PLAY_FROM = 6
    val SERVICE_CMD_PLAY_FROM_HASH = 61
    val SERVICE_CMD_PLAY_FROM_TOP = 62
    val SERVICE_CMD_NEXT = 7
    val SERVICE_CMD_PREVIOUS = 8
    val SERVICE_CMD_SORT = 9

    val SERVICE_CMD_SWITCH_TARGET_LIST = 5 // choose normal list or selected list before playing songs


    /******************** patching ************************/
    val SERVICE_PATCH_METADATA = 200
    val SERVICE_ADD_TO_SELECTED_LIST = 300
    val SERVICE_CLEAR_SELECTED_LIST = 301

    /******************** for status update ************************/
    val SERVICE_RESULT = "service_result"
    val SERVICE_RESULT_STATUS = "service_result_status"
    val SERVICE_RESULT_METADATA_NORMAL_LIST = "service_result_metadata_normal_list"
    val SERVICE_RESULT_METADATA_SELECTED_LIST = "service_result_metadata_selected_list"


    /******************** for Q & A ************************/
    val SERVICE_QUERY_METADATA_BY_HASH = 100
    val SERVICE_QUERY_SEARCH = 101 //search songs by partial title
    val SERVICE_ANSWER = "service_answer"
    val SERVICE_ANSWER_METADATA = "service_answer_metadata"
    val SERVICE_ANSWER_SEARCH = "service_answer_search"
}
