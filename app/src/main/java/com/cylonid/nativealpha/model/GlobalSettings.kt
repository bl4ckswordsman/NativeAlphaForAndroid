package com.cylonid.nativealpha.model

class GlobalSettings {
    var isClearCache: Boolean
    private var clear_cookies: Boolean
    var isTwoFingerMultitouch: Boolean
    var isThreeFingerMultitouch: Boolean
    var isShowProgressbar: Boolean
    var isMultitouchReload: Boolean
    var themeId: Int
    var globalWebApp: WebApp
    var alwaysShowSoftwareButtons: Boolean
    var isAllowPasswordAutofill: Boolean

    constructor(other: GlobalSettings) {
        this.isClearCache = other.isClearCache
        this.clear_cookies = other.clear_cookies
        this.isTwoFingerMultitouch = other.isTwoFingerMultitouch
        this.isThreeFingerMultitouch = other.isThreeFingerMultitouch
        this.themeId = other.themeId
        this.isMultitouchReload = other.isMultitouchReload
        this.isShowProgressbar = other.isShowProgressbar
        this.globalWebApp = other.globalWebApp
        this.alwaysShowSoftwareButtons = other.alwaysShowSoftwareButtons
isAllowPasswordAutofill = other.isAllowPasswordAutofill
        this.isAllowPasswordAutofill = other.isAllowPasswordAutofill
    }

    constructor() {
        isClearCache = false
        clear_cookies = false
        isTwoFingerMultitouch = true
        isThreeFingerMultitouch = false
        isMultitouchReload = true
        themeId = 0
        isShowProgressbar = false
        globalWebApp = WebApp("about:blank", Int.MAX_VALUE)
        alwaysShowSoftwareButtons = false
isAllowPasswordAutofill = false
        isAllowPasswordAutofill = false
    }

    fun setClearCookies(clear_cookies: Boolean) {
        this.clear_cookies = clear_cookies
    }
}
