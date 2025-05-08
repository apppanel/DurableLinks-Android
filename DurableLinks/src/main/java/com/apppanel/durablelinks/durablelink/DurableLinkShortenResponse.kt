package com.apppanel.durablelinks.durablelink

import android.net.Uri

public data class DurableLinkShortenResponse(
    val shortLink: Uri,
    val warnings: List<Warning>
) {
    public data class Warning(
        val warningCode: String,
        val warningMessage: String
    )
}