package com.apppanel.durablelinks.model.durable_link_component.parameters

/**
 * Represents additional parameters that can be included in a Durable Link for other platforms
 *
 * This class is used to provide a fallback URL, which can be used as a secondary link to redirect
 * users if they are not on an Android or iOS device.
 *
 * @property fallbackURL The URL to redirect users to if they are not on an Android or iOS device.
 */
public data class OtherPlatformParameters(
    val fallbackURL: String? = null
) : DurableLinkParameter {
    override fun toMap(): Map<String, String> =
        fallbackURL?.let { mapOf("ofl" to it) } ?: emptyMap()
}
