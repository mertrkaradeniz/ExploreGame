package com.mertrizakaradeniz.exploregame.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "games")
data class Game(
    @PrimaryKey @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("released") val released: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("metacritic") val metacritic: Int?,
    @SerializedName("description") val description: String?,
    @SerializedName("background_image") val imageUrl: String?,
    @ColumnInfo(name = "is_fav") var isFavorite: Boolean = false
) : Serializable
// TODO: 5/14/2021 change with parcelable