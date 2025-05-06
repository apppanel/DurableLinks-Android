package com.apppanel.durablelinks.model.durable_link_component.parameters

internal interface DurableLinkParameter {
    fun toMap(): Map<String, String>
}
