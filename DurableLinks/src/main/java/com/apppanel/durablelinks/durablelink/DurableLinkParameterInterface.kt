package com.apppanel.durablelinks.durablelink

internal interface DurableLinkParameter {
    fun toMap(): Map<String, String>
}
