package com.apppanel.durablelinks

import android.content.Intent
import android.net.Uri
import com.apppanel.durablelinks.durablelink.DurableLink
import com.apppanel.durablelinks.durablelink.DurableLinkShortenResponse
import com.apppanel.durablelinks.durablelink.DurableLinkComponents
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public object DurableLinksSDK {

    /**
     * Returns the SDK version.
     * This value should be passed as a header to your backend, it's purpose is to enable schema breaking changes
     */
    public val sdkVersion: String
        get() = BuildConfig.SDK_VERSION

    private var allowedHosts: List<String> = emptyList()

    /**
     * The client-supplied implementation that handles Durable Link shortening and exchange logic.
     *
     * This must be set before calling any methods that require network interaction.
     * If not set, {@link DurableLinksSDKError.DelegateUnavailable} will be thrown.
     */
    @Volatile
    public var delegate: DurableLinksClient? = null

    /**
     * Configures the DurableLinks SDK by providing a list of allowed hosts. eg. (acme.wayp.link, acme-preview.wayp.link, preview.acme.wayp.link)
     *
     * @param allowedHosts The list of domains that the SDK will support.
     */
    @Synchronized
    public fun configure(allowedHosts: List<String>) {
        DurableLinksSDK.allowedHosts = allowedHosts
    }

    /**
     * Handles the Durable Link passed within the intent and returns a DurableLink object.
     *
     * @param intent The Intent containing the durable link URI.
     * @return The DurableLink object extracted from the URI in the intent.
     * @throws DurableLinksSDKError.InvalidDurableLink if the intent data is null or invalid.
     */
    @Throws(DurableLinksSDKError::class)
    public suspend fun handleDurableLink(intent: Intent): DurableLink {
        val uri = intent.data ?: throw DurableLinksSDKError.InvalidDurableLink
        return handleDurableLink(uri)
    }

    /**
     * Handles the durable link passed in the form of a URI and returns a DurableLink object.
     *
     * @param incomingUrl The URI representing the durable link.
     * @return The DurableLink object.
     * @throws DurableLinksSDKError.InvalidDurableLink if the URL is not valid.
     * @throws DurableLinksSDKError.DelegateUnavailable if the delegate is not configured.
     * @throws DurableLinksSDKError.UnknownDelegateResponse if there is an unknown response from the delegate.
     */
    @Throws(DurableLinksSDKError::class)
    public suspend fun handleDurableLink(incomingUrl: Uri): DurableLink {
        if (!isValidDurableLink(incomingUrl)) {
            throw DurableLinksSDKError.InvalidDurableLink
        }

        val shortener = delegate ?: throw DurableLinksSDKError.DelegateUnavailable

        return suspendCancellableCoroutine { continuation ->
            shortener.exchangeShortCode(incomingUrl) { exchangeLinkResponse, error ->
                when {
                    exchangeLinkResponse != null -> {
                        continuation.resume(DurableLink(exchangeLinkResponse.longLink))
                    }

                    error != null -> {
                        continuation.resumeWithException(error)
                    }

                    else -> {
                        continuation.resumeWithException(DurableLinksSDKError.UnknownDelegateResponse)
                    }
                }
            }
        }
    }

    /**
     * Shortens a durable link and returns the response containing the shortened URL.
     *
     * @param durableLink The DurableLinkComponents that will be used to build the URI.
     * @return A DurableLinkShortenResponse containing the shortened link.
     * @throws DurableLinksSDKError.DelegateUnavailable if the delegate is not configured.
     * @throws DurableLinksSDKError.InvalidDurableLink if the link is invalid.
     * @throws DurableLinksSDKError.UnknownDelegateResponse if there is an unknown response from the delegate.
     */
    @Throws(DurableLinksSDKError::class)
    public suspend fun shorten(durableLink: DurableLinkComponents): DurableLinkShortenResponse {
        val shortener = delegate ?: throw DurableLinksSDKError.DelegateUnavailable
        val longUrl = durableLink.buildUri() ?: throw DurableLinksSDKError.InvalidDurableLink

        return suspendCancellableCoroutine { continuation ->
            shortener.shortenURL(longUrl) { durableLinkResponse, error ->
                when {
                    durableLinkResponse != null -> continuation.resume(durableLinkResponse)
                    error != null -> continuation.resumeWithException(error)
                    else -> continuation.resumeWithException(DurableLinksSDKError.UnknownDelegateResponse)
                }
            }
        }
    }

    /**
     * Checks if the given intent contains a valid durable link.
     *
     * @param intent The Intent that may contain a durable link.
     * @return True if the intent data is a valid durable link, false otherwise.
     */
    public fun isValidDurableLink(intent: Intent): Boolean {
        val uri = intent.data ?: return false
        return isValidDurableLink(uri)
    }

    /**
     * Checks if the given URI represents a valid durable link.
     *
     * @param url The URI representing the durable link.
     * @return True if the URL's host matches one of the allowed hosts and the path matches the expected format, false otherwise.
     */
    public fun isValidDurableLink(url: Uri): Boolean {
        val host = url.host ?: return false
        val pathMatches = Regex("/[^/]+").containsMatchIn(url.path ?: "")
        return allowedHosts.contains(host) && pathMatches
    }
}
