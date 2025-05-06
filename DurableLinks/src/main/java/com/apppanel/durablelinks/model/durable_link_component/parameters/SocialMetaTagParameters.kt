package com.apppanel.durablelinks.model.durable_link_component.parameters

/**
 * Represents the parameters used for social media meta tags in a Durable Link.
 * These parameters are used to customize the content that shows up on the preview page.
 * For example if this Durable Link represents a piece of content in your app,
 * the Durable Link preview page can represent that with these parameters
 *
 * @property title The title text to be shown on the preview page.
 * @property descriptionText The description text to be shown on the preview page.
 * @property imageURL The URL of the image to be shown on the preview page.
 */
public data class SocialMetaTagParameters(
    public val title: String? = null,
    public val descriptionText: String? = null,
    public val imageURL: String? = null
) : DurableLinkParameter {
    override fun toMap(): Map<String, String> =
        listOfNotNull(
            title?.let { "st" to it },
            descriptionText?.let { "sd" to it },
            imageURL?.let { "si" to it }
        ).toMap()
}