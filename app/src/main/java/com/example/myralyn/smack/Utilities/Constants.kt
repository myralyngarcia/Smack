package com.example.myralyn.smack.Utilities

/**
 * Created by myralyn on 28/01/18.
 */
const val BASE_URL ="https://chattymyra.herokuapp.com/v1/"
//const val BASE_URL = "http://192.168.1 .71:3005/v1/"
const val SOCKET_URL ="https://chattymyra.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}account/register"
const val URL_LOGIN = "${BASE_URL}account/login"
const val URL_CREATE_USER = "${BASE_URL}user/add"
const val URL_GET_USER = "${BASE_URL}user/byEmail/"
const val URL_GET_CHANNELS = "${BASE_URL}channel"
const val URL_GET_MESSAGES ="${BASE_URL}message/byChannel/"

//broadcast constant
const val BROADCAST_USER_DATA_CHANGED = "BROADCAST_USER_DATA_CHANGED"
