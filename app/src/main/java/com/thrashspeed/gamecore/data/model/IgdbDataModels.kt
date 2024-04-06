package com.thrashspeed.gamecore.data.model

data class Game(
    val age_ratings: List<Int>,
    val alternative_names: List<Int>,
    val artworks: List<Int>,
    val bundles: List<Int>,
    val category: Int,
    val checksum: String,
    val collection: Int,
    val collections: List<Int>,
    val cover: Int,
    val created_at: Int,
    val external_games: List<Int>,
    val first_release_date: Int,
    val follows: Int,
    val franchises: List<Int>,
    val game_engines: List<Int>,
    val game_localizations: List<Int>,
    val game_modes: List<Int>,
    val genres: List<Int>,
    val hypes: Int,
    val id: Int,
    val involved_companies: List<Int>,
    val keywords: List<Int>,
    val language_supports: List<Int>,
    val name: String,
    val platforms: List<Int>,
    val player_perspectives: List<Int>,
    val rating: Double,
    val rating_count: Int,
    val release_dates: List<Int>,
    val remakes: List<Int>,
    val screenshots: List<Int>,
    val similar_games: List<Int>,
    val slug: String,
    val standalone_expansions: List<Int>,
    val storyline: String,
    val summary: String,
    val tags: List<Int>,
    val themes: List<Int>,
    val total_rating: Double,
    val total_rating_count: Int,
    val updated_at: Int,
    val url: String,
    val videos: List<Int>,
    val websites: List<Int>
)

data class GameItem(
    val id: Int,
    val name: String,
    val cover: GameCover
)

data class GameCover(
    val id: Int,
    val image_id: String
)