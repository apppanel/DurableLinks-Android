package com.apppanel.durablelinks.model.durable_link_component

import android.net.Uri
import androidx.core.net.toUri
import com.apppanel.durablelinks.model.durable_link_component.parameters.AnalyticsParameters
import com.apppanel.durablelinks.model.durable_link_component.parameters.AndroidParameters
import com.apppanel.durablelinks.model.durable_link_component.parameters.DurableLinkOptionsParameters
import com.apppanel.durablelinks.model.durable_link_component.parameters.DurableLinkParameter
import com.apppanel.durablelinks.model.durable_link_component.parameters.IOSParameters
import com.apppanel.durablelinks.model.durable_link_component.parameters.ItunesConnectAnalyticsParameters
import com.apppanel.durablelinks.model.durable_link_component.parameters.OtherPlatformParameters
import com.apppanel.durablelinks.model.durable_link_component.parameters.SocialMetaTagParameters


/**
 * Data class representing the components that make up a Durable Link.
 *
 * This class is used to construct a Durable Link, containing the base URI (`domainUriPrefix`),
 * query parameters for various platforms (iOS, Android, etc.), and additional parameters like analytics,
 * social media tags, and options.
 *
 * @property link The target URI that the Durable Link represents. eg. mydomain.com/signup.
 * @property domainUriPrefix The base URI to be used for the Durable Link.
 * @property iOSParameters Parameters specific to iOS devices for the Durable Link.
 * @property androidParameters Parameters specific to Android devices for the Durable Link.
 * @property iTunesConnectParameters Parameters for iTunes Connect analytics related to the link.
 * @property socialMetaTagParameters Parameters for social media meta tags associated with the link.
 * @property options Additional options related to the Durable Link.
 * @property otherPlatformParameters Parameters for other platforms supported by the Durable Link.
 * @property analyticsParameters Parameters related to analytics for tracking the Durable Link.
 */
public data class DurableLinkComponents(
    public val link: Uri,
    public val domainUriPrefix: String,
    public val iOSParameters: IOSParameters = IOSParameters(),
    public val androidParameters: AndroidParameters? = null,
    public val iTunesConnectParameters: ItunesConnectAnalyticsParameters? = null,
    public val socialMetaTagParameters: SocialMetaTagParameters? = null,
    public val options: DurableLinkOptionsParameters = DurableLinkOptionsParameters(),
    public val otherPlatformParameters: OtherPlatformParameters? = null,
    public val analyticsParameters: AnalyticsParameters? = null
) {

    /**
     * Builds the final URI for the Durable Link by appending query parameters for various platforms and options.
     *
     * This function ensures that the domain URI prefix starts with "https://", then constructs the URI
     * by adding the necessary parameters for the link, including those for iOS, Android, analytics, social
     * media tags, and any other relevant parameters.
     *
     * @return The constructed URI for the Durable Link, including all the query parameters.
     * @throws IllegalArgumentException if the domainUriPrefix does not start with "https://".
     */
    @Throws(IllegalArgumentException::class)
    public fun buildUri(): Uri? {
        if (!domainUriPrefix.startsWith("https://")) {
            throw IllegalArgumentException("Domain URI must start with https://")
        }

        val builder = domainUriPrefix.toUri().buildUpon()
            .appendQueryParameter("link", link.toString())

        fun addParams(encodable: DurableLinkParameter?) {
            encodable?.toMap()?.forEach { (key, value) ->
                val encodedKey = Uri.encode(key)
                val encodedValue = Uri.encode(value.toString())
                builder.appendQueryParameter(encodedKey, encodedValue)
            }
        }

        addParams(analyticsParameters)
        addParams(socialMetaTagParameters)
        addParams(iOSParameters)
        addParams(androidParameters)
        addParams(iTunesConnectParameters)
        addParams(otherPlatformParameters)
        addParams(options)

        return builder.build()
    }
}
