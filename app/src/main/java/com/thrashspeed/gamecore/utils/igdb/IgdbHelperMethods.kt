package com.thrashspeed.gamecore.utils.igdb

class IgdbHelperMethods {
    companion object {
        fun getImageUrl(hash: String, size: IgdbImageSizes ): String{
            return "${"https://images.igdb.com/igdb/image/upload/"}t_${size.imageSize}/$hash.jpg"
        }
    }
}