package com.example.test_app

import io.flutter.embedding.android.FlutterActivity
import androidx.annotation.NonNull
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
// package com.backgroundactiveappslists
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
//import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi

class MainActivity: FlutterActivity() {

    private val ONE_HR = 1 * 60 * 60

    private val CHANNEL = "bgChannel"
    // private val CHANNEL = "battery"
    @RequiresApi(VERSION_CODES.Q)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->
            if(call.method=="bgApps"){
                if (needPermissionForBlocking()) {
                    //intent to usage access permission
                    startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                } else {
                    getAppsList()
                }
            }
            else {
                result.notImplemented()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun needPermissionForBlocking(): Boolean {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appOpsManager = getSystemService(APP_OPS_SERVICE) as AppOpsManager
            val mode = appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                applicationInfo.uid,
                applicationInfo.packageName
            )
            mode != AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            true
        }
    }



    @RequiresApi(VERSION_CODES.LOLLIPOP)
    private fun getAppsList() {
        val usm = this.getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val startTime = currentTime - (1000 * 360 * 1) //1 hr difference
        val allAppLists =
            usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, currentTime)
        val activeAppList = mutableListOf<String>()
        if (allAppLists != null && allAppLists.isNotEmpty()) {
            for (usageStats in allAppLists) {
                val timeDiff = (currentTime - usageStats.lastTimeUsed) / 3600 //in hrs

                //lastTimeUseinForeGroundService

                if (timeDiff < ONE_HR) {
                    activeAppList.add(usageStats.packageName)
                }
            }
        }
        if (activeAppList.isNotEmpty()) {
            activeAppList.forEach {
                Log.d("Active App Package name", it)
            }
        }
    }
}
