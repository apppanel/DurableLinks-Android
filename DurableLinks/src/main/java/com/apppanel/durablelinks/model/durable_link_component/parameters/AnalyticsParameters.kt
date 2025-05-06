package com.apppanel.durablelinks.model.durable_link_component.parameters

/**
 * Represents the UTM parameters used for tracking analytics in a Durable Link.
 * These parameters are typically used to track the performance of marketing campaigns
 * and help analyze the source, medium, campaign, term, and content of the traffic.
 *
 * @property source The source of the traffic (e.g., "Google", "Facebook") - typically mapped to "utm_source".
 * @property medium The medium through which the traffic came (e.g., "CPC", "Email") - typically mapped to "utm_medium".
 * @property campaign The name of the marketing campaign - typically mapped to "utm_campaign".
 * @property term The search term or keyword associated with the traffic - typically mapped to "utm_term".
 * @property content The specific content or variation of the campaign - typically mapped to "utm_content".
 */
public data class AnalyticsParameters(
    public val source: String? = null,
    public val medium: String? = null,
    public val campaign: String? = null,
    public val term: String? = null,
    public val content: String? = null
) : DurableLinkParameter {
    override fun toMap(): Map<String, String> =
        listOfNotNull(
            source?.let { "utm_source" to it },
            medium?.let { "utm_medium" to it },
            campaign?.let { "utm_campaign" to it },
            term?.let { "utm_term" to it },
            content?.let { "utm_content" to it }
        ).toMap()
}
