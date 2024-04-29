package com.thrashspeed.gamecore.utils.igdb

enum class IgdbSortOptions(val sortOption: String) {
    RATING("rating"),
    MOST_PLAYED("total_rating_count"),
    HYPE("hypes"),
    GENERATION("generation")
}