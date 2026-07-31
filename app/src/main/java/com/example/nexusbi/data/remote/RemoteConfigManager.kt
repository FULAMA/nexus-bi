package com.example.nexusbi.data.remote

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

/**
 * Gestionnaire utilitaire Firebase Remote Config pour NexusBI.
 * Permet de contrôler dynamiquement les fonctionnalités et paramètres de l'application sans republier une APK.
 */
object RemoteConfigManager {
    private const val TAG = "RemoteConfigManager"

    // Clés de configuration à distance
    const val KEY_FEATURE_AI_RISK = "feature_ai_risk_enabled"
    const val KEY_MAINTENANCE_MODE = "maintenance_mode"
    const val KEY_MIN_REQUIRED_VERSION = "min_required_version"
    const val KEY_ANNOUNCEMENT_MESSAGE = "announcement_message"

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance()
    }

    /**
     * Initialise Firebase Remote Config avec les valeurs par défaut et effectue la synchronisation.
     */
    fun initialize(onComplete: ((Boolean) -> Unit)? = null) {
        try {
            // Configuration des paramètres de récupération (1h en prod)
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build()
            remoteConfig.setConfigSettingsAsync(configSettings)

            // Définition des valeurs par défaut dans l'application
            val defaults = mapOf<String, Any>(
                KEY_FEATURE_AI_RISK to true,
                KEY_MAINTENANCE_MODE to false,
                KEY_MIN_REQUIRED_VERSION to "1.0.0",
                KEY_ANNOUNCEMENT_MESSAGE to "Bienvenue sur NexusBI !"
            )
            remoteConfig.setDefaultsAsync(defaults)

            // Récupération et activation automatique des paramètres en ligne
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Remote Config synchronisé avec succès. Mise à jour: ${task.result}")
                        onComplete?.invoke(true)
                    } else {
                        Log.w(TAG, "Échec de la récupération Remote Config, utilisation des valeurs par défaut.", task.exception)
                        onComplete?.invoke(false)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Erreur lors de l'initialisation de RemoteConfigManager", e)
            onComplete?.invoke(false)
        }
    }

    // Getters pour accéder facilement aux fonctionnalités
    fun isFeatureAiRiskEnabled(): Boolean = remoteConfig.getBoolean(KEY_FEATURE_AI_RISK)
    fun isMaintenanceMode(): Boolean = remoteConfig.getBoolean(KEY_MAINTENANCE_MODE)
    fun getMinRequiredVersion(): String = remoteConfig.getString(KEY_MIN_REQUIRED_VERSION)
    fun getAnnouncementMessage(): String = remoteConfig.getString(KEY_ANNOUNCEMENT_MESSAGE)

    fun getString(key: String): String = remoteConfig.getString(key)
    fun getBoolean(key: String): Boolean = remoteConfig.getBoolean(key)
    fun getLong(key: String): Long = remoteConfig.getLong(key)
}
