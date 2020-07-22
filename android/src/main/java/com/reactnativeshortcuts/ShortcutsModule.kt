package com.reactnativeshortcuts

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.os.Build
import com.facebook.react.bridge.*
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.modules.core.DeviceEventManagerModule



@ReactModule(name = ShortcutsModule.MODULE_NAME)
class ShortcutsModule(reactContext: ReactApplicationContext) :
        ReactContextBaseJavaModule(reactContext),
        ActivityEventListener {

    companion object {
        const val MODULE_NAME = "RNShortcuts"
        const val INTENT_ACTION_SHORTCUT = "com.react_native_shortcuts.action.SHORTCUT"
        const val EVENT_ON_SHORTCUT_ITEM_PRESSED = "onShortcutItemPressed"
    }

    init {
        reactContext.addActivityEventListener(this)
    }

    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
        // No implementation needed
        println("onActivityResult")
    }

    override fun onNewIntent(intent: Intent?) {
        println("onNewIntent")

        if(intent?.action !== INTENT_ACTION_SHORTCUT) {
           return
       }

        reactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(EVENT_ON_SHORTCUT_ITEM_PRESSED, "hello")

    }

    override fun getName(): String {
        return MODULE_NAME
    }

    @ReactMethod
    @TargetApi(25)
    fun setShortcuts(items: ReadableArray, promise: Promise) {
        if(!isSupported()) {
            promise.reject(Throwable("Feature not supported, requires version 25 or above"))
        }

        val activity = currentActivity

        if(activity == null) {
            return
        }


        val shortcuts = items.toArrayList().mapIndexed { index, _ ->
            val item = items.getMap(index)

            val type = item?.getString("type") ?: ""
            val longLabel = item?.getString("title") ?: ""
            val hasShortLabel = item?.hasKey("shortTitle") ?: false

            val shortLabel = if(hasShortLabel) item?.getString("shortTitle") else longLabel

            val intent = Intent(reactApplicationContext, activity!!::class.java)
            intent.setAction(INTENT_ACTION_SHORTCUT)

            ShortcutInfo
                    .Builder(reactApplicationContext, type)
                    .setLongLabel(longLabel)
                    .setShortLabel(shortLabel)
                    .setIntent(intent)
                    .build()
        }

        val shortcutManager = currentActivity?.getSystemService<ShortcutManager>(ShortcutManager::class.java)

        shortcutManager?.dynamicShortcuts = shortcuts
    }

    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= 25
    }
}
