package com.apppanel.durablelinks.model.durable_link_component.parameters

/**
 * Represents the parameters used for iTunes Connect analytics in a Durable Link.
 * These parameters are typically used to track affiliate marketing campaigns and measure
 * the effectiveness of marketing efforts for apps in the iTunes Store.
 *
 * @property affiliateToken The affiliate token used for tracking affiliate marketing.
 * @property campaignToken The campaign token used to track specific marketing campaigns.
 * @property providerToken The provider token used to identify the provider for iTunes Connect.
 */
public data class ItunesConnectAnalyticsParameters(
    public val affiliateToken: String? = null,
    public val campaignToken: String? = null,
    public val providerToken: String? = null
) : DurableLinkParameter {
    override fun toMap(): Map<String, String> =
        listOfNotNull(
            affiliateToken?.let { "at" to it },
            campaignToken?.let { "ct" to it },
            providerToken?.let { "pt" to it }
        ).toMap()
}