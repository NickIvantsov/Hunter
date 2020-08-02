package com.gmail.rewheel.helpers

import android.app.Application
import com.gmail.rewheel.BuildConfig
import com.gmail.rewheel.helpers.logging.ReleaseTree
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber
import timber.log.Timber.DebugTree


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}