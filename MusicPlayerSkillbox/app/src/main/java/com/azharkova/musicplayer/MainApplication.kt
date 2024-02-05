package com.azharkova.musicplayer

import android.app.Application
import android.content.Context

class MainApplication : Application() {
    companion object {
       lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        MainApplication.context = this.applicationContext
    }
}