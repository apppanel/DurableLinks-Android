package com.apppanel.durablelinks.model.durable_link_component.parameters

/**
 * Represents the options for a Durable Link, such as the length of the path used in the link.
 * This class allows setting the path length, which determines whether the path should be easily guessable
 * or more secure (unguessable).
 *
 * @property pathLength The length of the Durable Link path. The default value is `UNGUESSABLE`.
 */
public data class DurableLinkOptionsParameters(
    val pathLength: DurableLinkPathLength = DurableLinkPathLength.UNGUESSABLE
) : DurableLinkParameter {
    override fun toMap(): Map<String, String> =
        mapOf("pathLength" to pathLength.toQueryValue())
}

/**
 * Enum representing the possible lengths for the Durable Link path.
 *
 * - `UNGUESSABLE` indicates that the path should be long and secure, making it difficult to guess.
 * - `SHORT` indicates a shorter, easier-to-guess path, suitable for cases where non user specific content is being shared.
 *
 * @property value The integer value representing the path length option.
 */
public enum class DurableLinkPathLength private constructor(public val value: Int) {
    UNGUESSABLE(0),
    SHORT(1);

    public fun toQueryValue(): String = when (this) {
        SHORT -> "SHORT"
        UNGUESSABLE -> "UNGUESSABLE"
    }
}
