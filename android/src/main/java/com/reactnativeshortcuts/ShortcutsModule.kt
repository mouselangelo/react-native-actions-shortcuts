package com.reactnativeactionsshortcuts

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.PersistableBundle
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

    override fun onCatalystInstanceDestroy() {
        reactApplicationContext.removeActivityEventListener(this)
        super.onCatalystInstanceDestroy()
    }

    override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
        // No implementation needed
    }

    override fun onNewIntent(intent: Intent?) {
        emitEvent(intent)
    }

    override fun getName(): String {
        return MODULE_NAME
    }

    @ReactMethod
    @TargetApi(25)
    fun setShortcuts(items: ReadableArray, promise: Promise) {
        if (!isSupported()) {
            promise.reject(NotSupportedException)
        }

        val context = reactApplicationContext ?: return
        val activity = currentActivity ?: return

        val shortcutItems = items.toArrayList().mapIndexed { index, _ ->
            val map = items.getMap(index) ?: return
            ShortcutItem.fromReadableMap(map)
        }.filterNotNull()

        val shortcuts = shortcutItems.map {
            val intent = Intent(reactApplicationContext, activity::class.java)
            intent.action = INTENT_ACTION_SHORTCUT
            intent.putExtra("shortcutItem", it.toBundle())

            val (type, title, shortTitle, iconName) = it

            val builder = ShortcutInfo
                    .Builder(reactApplicationContext, type)
                    .setLongLabel(title)
                    .setShortLabel(shortTitle)
                    .setIntent(intent)

            if(iconName != null) {
                val resourceId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
                builder.setIcon(Icon.createWithResource(context, resourceId))
            }

            builder.build()
        }

        val shortcutManager = activity.getSystemService<ShortcutManager>(ShortcutManager::class.java)
        shortcutManager?.dynamicShortcuts = shortcuts

        promise.resolve(ShortcutItem.toWritableArray(shortcutItems))
    }

    @ReactMethod
    @TargetApi(25)
    fun getShortcuts(promise: Promise) {
        if (!isSupported()) {
            promise.reject(NotSupportedException)
        }

        val shortcutManager = currentActivity?.getSystemService<ShortcutManager>(ShortcutManager::class.java)
        val shortcutItems = shortcutManager?.dynamicShortcuts?.map {
            ShortcutItem(it.id, it.longLabel.toString(), it.shortLabel.toString(), null, null)
        }

        promise.resolve(ShortcutItem.toWritableArray(shortcutItems ?: arrayListOf()))
    }

    @ReactMethod
    @TargetApi(25)
    fun getInitialShortcut(promise: Promise) {
        if (!isSupported()) {
            promise.reject(NotSupportedException)
        }

        val shortcutItem = getShortcutItemFromIntent(currentActivity?.intent)

        promise.resolve(shortcutItem?.toMap())
    }

    @ReactMethod
    @TargetApi(25)
    fun clearShortcuts() {
        if (!isSupported()) {
            return
        }

        val shortcutManager = currentActivity?.getSystemService<ShortcutManager>(ShortcutManager::class.java)
        shortcutManager?.removeAllDynamicShortcuts()
    }

    private fun getShortcutItemFromIntent(intent: Intent?): ShortcutItem? {
        if (intent?.action !== INTENT_ACTION_SHORTCUT) {
            return null
        }

        val bundle = intent.getParcelableExtra<PersistableBundle>("shortcutItem") ?: return null
        return ShortcutItem.fromPersistentBundle(bundle)
    }

    private fun emitEvent(intent: Intent?) {
        val shortcutItem = getShortcutItemFromIntent(intent) ?: return

        reactApplicationContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(EVENT_ON_SHORTCUT_ITEM_PRESSED, shortcutItem.toMap())
    }

    fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= 25
    }
}

object NotSupportedException: Throwable("Feature not supported, requires version 25 or above")