package com.example.myralyn.smack.Model

/**
 * Created by myralyn on 17/02/18.
 */
class Channel(val name: String, val description: String, val id: String) {
    override fun toString(): String {
        return "#$name"
    }
}