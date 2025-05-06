package com.apppanel.durablelinks

public sealed class DurableLinksError protected constructor(message: String? = null) :
    Exception(message) {
    public object DelegateUnavailable : DurableLinksError("Delegate not set")
    public object InvalidDurableLink : DurableLinksError("Link is invalid")
    public object UnknownDelegateResponse :
        DurableLinksError("Delegate returned nil for both URL and error")
}
