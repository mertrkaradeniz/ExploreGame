package com.mertrizakaradeniz.exploregame.data.models

data class GamesResponse(
    val count: Int?,
    val next: String?,
    val results: List<Game>
)