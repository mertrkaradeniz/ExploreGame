package com.mertrizakaradeniz.exploregame.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "game")
data class Game(

    @PrimaryKey()
    val id: Int,
    val name: String,
    val released: String,
    val rating: Float,
    val metacritic: Int?,
    val description: String?,
    @SerializedName("background_image")
    val imageUrl: String,
    @ColumnInfo(name = "is_fav")
    var isFav: Boolean = false
) {

}
