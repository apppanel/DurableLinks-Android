package com.apppanel.durablelinks

import android.net.Uri
import com.apppanel.durablelinks.model.DurableLinkShortenResponse
import com.apppanel.durablelinks.model.ExchangeLinkResponse

public interface DurableLinkShortenerDelegate {
    public fun shortenURL(
        longUrl: Uri,
        callback: (DurableLinkShortenResponse?, Exception?) -> Unit
    )

    public fun exchangeShortCode(
        requestedLink: Uri,
        callback: (ExchangeLinkResponse?, Exception?) -> Unit
    )
}