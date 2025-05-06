package com.apppanel.durablelinks.model.durable_link_component.parameters

/**
 * Represents the parameters specific to Android devices in a Durable Link.
 * These parameters are used to configure the behavior of the Durable Link on Android devices,
 * including the package name, a fallback URL, and the minimum app version required.
 *
 * @property packageName The package name of the Android app associated with the link.
 * @property fallbackURL The URL to redirect to if the app is not installed on the Android device.
 * By default the user will be taken to your apps Google Play Store listing
 * @property minimumVersion The minimum version of the app required to open the link.
 */
public data class AndroidParameters(
    public val packageName: String,
    public val fallbackURL: String? = null,
    public val minimumVersion: Int = 0
) : DurableLinkParameter {
    override fun toMap(): Map<String, String> =
        buildMap {
            put("apn", packageName)
            fallbackURL?.let { put("afl", it) }
            put("amv", minimumVersion.toString())
        }
}