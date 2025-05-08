package com.apppanel.durablelinks

import android.net.Uri
import com.apppanel.durablelinks.durablelink.DurableLinkShortenResponse
import com.apppanel.durablelinks.durablelink.ExchangeLinkResponse

public interface DurableLinksClient {
    public fun shortenURL(
        longUrl: Uri,
        callback: (DurableLinkShortenResponse?, Exception?) -> Unit
    )

    public fun exchangeShortCode(
        requestedLink: Uri,
        callback: (ExchangeLinkResponse?, Exception?) -> Unit
    )
}