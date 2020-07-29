package com.reactnativeactionsshortcuts

import android.annotation.TargetApi
import android.os.PersistableBundle
import com.facebook.react.bridge.*
import org.json.JSONArray
import org.json.JSONObject

data class ShortcutItem(
        val type: String,
        val title: String,
        val shortTitle: String,
        val iconName: String?,
        val data: JSONObject?) {

    @TargetApi(25)
    fun toBundle(): PersistableBundle {
        val bundle = PersistableBundle()
        bundle.putString(KeyName.type, type)
        bundle.putString(KeyName.title, title)
        bundle.putString(KeyName.shortTitle, shortTitle)
        if(iconName != null) {
            bundle.putString(KeyName.iconName, iconName)
        }
        if (data != null) {
            bundle.putString(KeyName.data, data.toString())
        }
        return bundle
    }

    fun toMap(): WritableMap {
        val map = Arguments.createMap()
        map.putString(KeyName.type, type)
        map.putString(KeyName.title, title)
        map.putString(KeyName.shortTitle, shortTitle)
        if(iconName != null) {
            map.putString(KeyName.iconName, iconName)
        }
        if (data != null) {
            map.putMap(KeyName.data, Helper.toWritableMap(data))
        }
        return map
    }

    companion object {

        object KeyName {
            const val type = "type"
            const val title = "title"
            const val shortTitle = "shortTitle"
            const val iconName = "iconName"
            const val data = "data"
        }

        fun fromReadableMap(map: ReadableMap): ShortcutItem? {
            val type = map.getString(KeyName.type) ?: return null
            val title = map.getString(KeyName.title) ?: return null
            val shortTitle = if (map.hasKey(KeyName.shortTitle)) map.getString(KeyName.shortTitle) else null
            val iconName = if (map.hasKey(KeyName.iconName)) map.getString(KeyName.iconName) else null
            val data = if (map.hasKey(KeyName.data)) map.getMap(KeyName.data) else null
            val jsonObject = if (data != null) Helper.toJsonObject(data) else null

            return ShortcutItem(type, title, shortTitle ?: title, iconName, jsonObject)
        }

        @TargetApi(25)
        fun fromPersistentBundle(bundle: PersistableBundle): ShortcutItem? {
            val type = bundle.getString(KeyName.type) ?: return null
            val title = bundle.getString(KeyName.title) ?: return null
            val shortTitle = bundle.getString(KeyName.shortTitle)
            val iconName = bundle.getString(KeyName.iconName)
            val jsonString = bundle.getString(KeyName.data)
            val jsonObject = JSONObject(jsonString)

            return ShortcutItem(type, title, shortTitle ?: title, iconName, jsonObject)
        }

        fun toWritableArray(items: List<ShortcutItem>): WritableArray {
            val shortcutItems = Arguments.createArray()

            items.forEach {
                shortcutItems.pushMap(it.toMap())
            }

            return shortcutItems
        }
    }
}

private object Helper {
    fun toJsonObject(map: ReadableMap?): JSONObject? {
        val map = map ?: return null

        var jsonObject = JSONObject()
        val iterator = map.keySetIterator()

        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()

            when (map.getType(key)) {
                ReadableType.Null -> jsonObject.put(key, null)
                ReadableType.Boolean -> jsonObject.put(key, map.getBoolean(key))
                ReadableType.Number -> jsonObject.put(key, map.getDouble(key))
                ReadableType.String -> jsonObject.put(key, map.getString(key))
                ReadableType.Map -> jsonObject.put(key, toJsonObject(map.getMap(key)!!))
                ReadableType.Array -> jsonObject.put(key, toJsonArray(map.getArray(key)!!))
            }
        }

        return jsonObject
    }

    fun toJsonArray(array: ReadableArray): JSONArray {
        val jsonArray = JSONArray()

        for (i in 0 until array.size()) {
            when (array.getType(i)) {
                ReadableType.Null -> jsonArray.put(i, null)
                ReadableType.Boolean -> jsonArray.put(i, array.getBoolean(i))
                ReadableType.Number -> jsonArray.put(i, array.getDouble(i))
                ReadableType.String -> jsonArray.put(i, array.getString(i))
                ReadableType.Map -> jsonArray.put(i, toJsonObject(array.getMap(i)!!))
                ReadableType.Array -> jsonArray.put(i, toJsonArray(array.getArray(i)!!))
            }
        }

        return jsonArray
    }

    fun toWritableMap(jsonObject: JSONObject): WritableMap {
        val map = Arguments.createMap()

        val keysIterator = jsonObject.keys()

        while (keysIterator.hasNext()) {
            val key = keysIterator.next()
            val value = jsonObject.get(key)

            when (value) {
                is JSONObject -> map.putMap(key, toWritableMap(value))
                is JSONArray -> map.putArray(key, toWritableArray(value))
                is Boolean -> map.putBoolean(key, value)
                is Int -> map.putInt(key, value)
                is Double -> map.putDouble(key, value)
                is String -> map.putString(key, value)
                else -> {
                    map.putString(key, value.toString())
                }
            }
        }

        return map
    }

    fun toWritableArray(jsonArray: JSONArray): WritableArray {
        val array = Arguments.createArray()

        for (i in 0 until jsonArray.length()) {
            val value = jsonArray.get(i)
            when (value) {
                is JSONObject -> array.pushMap(toWritableMap(value))
                is JSONArray -> array.pushArray(toWritableArray(value))
                is Boolean -> array.pushBoolean(value)
                is Int -> array.pushInt(value)
                is Double -> array.pushDouble(value)
                is String -> array.pushString(value)
                else -> {
                    array.pushString(value.toString())
                }
            }
        }

        return array
    }
}