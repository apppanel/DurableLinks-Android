package com.apppanel.durablelinks

import android.content.Intent
import android.net.Uri
import com.apppanel.durablelinks.model.DurableLink
import com.apppanel.durablelinks.model.DurableLinkShortenResponse
import com.apppanel.durablelinks.model.durable_link_component.DurableLinkComponents
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public object DurableLinks {

    /**
     * Returns the SDK version from the BuildConfig.
     * This value should be passed as a header to your backend, it's purpose is to enable schema breaking changes
     */
    public val sdkVersion: String
        get() = BuildConfig.SDK_VERSION

    private var allowedHosts: List<String> = emptyList()

    @Volatile
    public var delegate: DurableLinkShortenerDelegate? = null

    /**
     * Configures the DurableLinks SDK by providing a list of allowed hosts. eg. (acme.wayp.link, acme-preview.wayp.link, preview.acme.wayp.link)
     * This should only be called once, otherwise it will throw an IllegalStateException.
     *
     * @param allowedHosts The list of domains that the SDK will support.
     * @throws IllegalStateException if the configure function is called more than once.
     */
    @Synchronized
    @Throws(IllegalStateException::class)
    public fun configure(allowedHosts: List<String>): DurableLinks {
        if (DurableLinks.allowedHosts.isNotEmpty()) {
            throw IllegalStateException("configure(...) called multiple times")
        }
        DurableLinks.allowedHosts = allowedHosts
        return this
    }

    /**
     * Handles the Durable Link passed within the intent and returns a DurableLink object.
     *
     * @param intent The Intent containing the durable link URI.
     * @return The DurableLink object extracted from the URI in the intent.
     * @throws DurableLinksError.InvalidDurableLink if the intent data is null or invalid.
     */
    @Throws(DurableLinksError::class)
    public suspend fun handleDurableLink(intent: Intent): DurableLink {
        val uri = intent.data ?: throw DurableLinksError.InvalidDurableLink
        return handleDurableLink(uri)
    }

    /**
     * Handles the durable link passed in the form of a URI and returns a DurableLink object.
     *
     * @param incomingUrl The URI representing the durable link.
     * @return The DurableLink object.
     * @throws DurableLinksError.InvalidDurableLink if the URL is not valid.
     * @throws DurableLinksError.DelegateUnavailable if the delegate is not configured.
     * @throws DurableLinksError.UnknownDelegateResponse if there is an unknown response from the delegate.
     */
    @Throws(DurableLinksError::class)
    public suspend fun handleDurableLink(incomingUrl: Uri): DurableLink {
        if (!isValidDurableLink(incomingUrl)) {
            throw DurableLinksError.InvalidDurableLink
        }

        val shortener = delegate ?: throw DurableLinksError.DelegateUnavailable

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
                        continuation.resumeWithException(DurableLinksError.UnknownDelegateResponse)
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
     * @throws DurableLinksError.DelegateUnavailable if the delegate is not configured.
     * @throws DurableLinksError.InvalidDurableLink if the link is invalid.
     * @throws DurableLinksError.UnknownDelegateResponse if there is an unknown response from the delegate.
     */
    @Throws(DurableLinksError::class)
    public suspend fun shorten(durableLink: DurableLinkComponents): DurableLinkShortenResponse {
        val shortener = delegate ?: throw DurableLinksError.DelegateUnavailable
        val longUrl = durableLink.buildUri() ?: throw DurableLinksError.InvalidDurableLink

        return suspendCancellableCoroutine { continuation ->
            shortener.shortenURL(longUrl) { durableLinkResponse, error ->
                when {
                    durableLinkResponse != null -> continuation.resume(durableLinkResponse)
                    error != null -> continuation.resumeWithException(error)
                    else -> continuation.resumeWithException(DurableLinksError.UnknownDelegateResponse)
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
