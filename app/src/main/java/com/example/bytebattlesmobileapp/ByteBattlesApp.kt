package com.example.bytebattlesmobileapp

import android.app.Application
import com.wakaztahir.codeeditor.BuildConfig
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ByteBattlesApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Настройка логирования
        if (BuildConfig.DEBUG) {
        }

    }
}