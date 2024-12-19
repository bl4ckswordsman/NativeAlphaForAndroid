package com.cylonid.nativealpha.util

import android.app.Activity
import android.content.Intent
import com.cylonid.nativealpha.BuildConfig
import com.cylonid.nativealpha.activities.NewsActivity
import com.cylonid.nativealpha.model.DataManager

object EntryPointUtils {
    @JvmStatic
    fun entryPointReached(a: Activity) {
        if (kotlin.math.abs(DataManager.getInstance().lastShownUpdate - BuildConfig.VERSION_CODE) > 10) {
            a.startActivity(Intent(a, NewsActivity::class.java))
        }
        DataManager.getInstance().loadAppData()
    }
}