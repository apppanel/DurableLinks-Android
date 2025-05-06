package com.apppanel.durablelinks.model.durable_link_component.parameters

/**
 * Represents the parameters specific to iOS devices in a Durable Link.
 * These parameters help configure the behavior of the link on iOS devices, such as defining
 * the App Store ID, fallback URLs, and the minimum app version required to open the link.
 *
 * @property appStoreID The App Store ID for the app associated with the link.
 * @property fallbackURL The URL to redirect to if the app is not installed on the iOS device.
 * By default the user will be taken to your iOS app store listing
 * @property iPadFallbackURL The URL to be used as a fallback on iPad devices when the app is not installed.
 * By default the user will be taken to your iOS app store listing
 * @property minimumAppVersion The minimum version of the iOS app required to open the link.
 */
public data class IOSParameters(
    public val appStoreID: String? = null,
    public val fallbackURL: String? = null,
    public val iPadFallbackURL: String? = null,
    public val minimumAppVersion: String? = null
) : DurableLinkParameter {
    override fun toMap(): Map<String, String> =
        listOfNotNull(
            appStoreID?.let { "isi" to it },
            fallbackURL?.let { "ifl" to it },
            iPadFallbackURL?.let { "ipfl" to it },
            minimumAppVersion?.let { "imv" to it }
        ).toMap()
}
