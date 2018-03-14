package com.example.myralyn.smack.Model

/**
 * Created by myralyn on 14/03/18.
 */
/**
 * UserName: sender
 * channelId: message where it was sent
 *
 */
class Message constructor(val message: String, val userName: String, val channelId : String,
                          val userAvatar: String, val userAvatarColor: String,
                          val id: String, val timeStamp: String)