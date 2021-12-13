package com.cuongnv.test_remote_config

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigManager @Inject constructor(@RemoteModule.GsonAnnotation private val gson: Gson){
    private val TAG: String = "RemoteConfigManager"
    private val remoteConfig = Firebase.remoteConfig
    private val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }

    fun init(activity: SettingsActivity) {
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) {task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    val someCool = getSomeCoolParameter()
                    val holyday = getHolydayParameter()
                    Log.d("someCool", "someCool: $someCool, $holyday")
                } else {
                    Log.d(TAG, "Config params updated: Fetch failed")
                }
            }
    }

    fun getSomeCoolParameter(): String =
        read<String>(ConfigParam.SOME_COOL_PARAMETER) ?: SOME_DEFAULT_VALUE

    fun getHolydayParameter(): Boolean =
        read<Boolean>(ConfigParam.HOLYDAY) ?: false

    private inline fun <reified T> read(param: ConfigParam): T? = read(param, T::class.java)

    private fun <T> read(param: ConfigParam, returnType: Class<T>): T? {
        val value: Any? = when (returnType) {
            String::class.java -> remoteConfig.getString(param.key)
            Boolean::class.javaObjectType -> remoteConfig.getBoolean(param.key)
            Long::class.javaObjectType -> remoteConfig.getLong(param.key)
            Int::class.javaObjectType -> remoteConfig.getLong(param.key).toInt()
            Double::class.javaObjectType -> remoteConfig.getDouble(param.key)
            Float::class.javaObjectType -> remoteConfig.getDouble(param.key).toFloat()
            else -> {
                val json = remoteConfig.getString(param.key)
                json.takeIf { it.isNotBlank() }?.let { gson.toJson(json, returnType) }
            }
        }
        @Suppress("UNCHECKED_CAST")
        return (value as? T)
    }

    private enum class ConfigParam(val key: String) {
        SOME_COOL_PARAMETER("some_cool_parameter"),
        HOLYDAY("holyday")
    }

    fun test() {}

    private companion object {
        /**
         * Config expiration interval 30 minutes.
         */
        private const val CONFIG_CACHE_EXPIRATION_SECONDS = 60L
        private const val SOME_DEFAULT_VALUE = "Any default value"
    }
}