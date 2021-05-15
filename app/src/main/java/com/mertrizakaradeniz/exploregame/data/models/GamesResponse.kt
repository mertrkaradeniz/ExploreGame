package com.mertrizakaradeniz.exploregame.data.models

data class GamesResponse(
    val count: Int,
    val next: String?,
    val description: String,
    val results: MutableList<Game>
)