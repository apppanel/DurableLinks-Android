package com.apppanel.durablelinks

public sealed class DurableLinksSDKError protected constructor(message: String? = null) :
    Exception(message) {
    public object DelegateUnavailable : DurableLinksSDKError("Delegate not set")
    public object InvalidDurableLink : DurableLinksSDKError("Link is invalid")
    public object UnknownDelegateResponse :
        DurableLinksSDKError("Delegate returned nil for both URL and error")
}
