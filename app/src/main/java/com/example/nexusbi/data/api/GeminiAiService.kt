package com.example.nexusbi.data.api

import com.example.nexusbi.BuildConfig
import com.example.nexusbi.ui.language.AppLanguage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AiRiskEvaluation(
    val score: Int, // 0 to 100
    val level: String, // "Fiable", "Prudence", "Risqué"
    val recommendation: String,
    val maxRecommendedCreditFcfa: Double
)

object GeminiAiService {

    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (e: Throwable) {
            ""
        }

        if (apiKey.isBlank()) {
            return@withContext fallbackResponse(prompt)
        }

        try {
            val url = URL("$BASE_URL?key=$apiKey")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.doOutput = true

            val requestJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            connection.outputStream.use { os ->
                os.write(requestJson.toString().toByteArray(Charsets.UTF_8))
            }

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(responseText)
                val candidates = jsonObj.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text", "")
                        if (text.isNotBlank()) {
                            return@withContext text.trim()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext fallbackResponse(prompt)
    }

    suspend fun evaluateCustomerRisk(
        customerName: String,
        netBalanceFcfa: Double,
        totalPaidFcfa: Double,
        recordCount: Int
    ): AiRiskEvaluation = withContext(Dispatchers.IO) {
        val prompt = """
            Tu es un expert en gestion de crédit pour petits commerçants en Afrique Centrale et de l'Ouest.
            Analyse le client suivant:
            - Nom: $customerName
            - Solde actuel dû: $netBalanceFcfa FCFA
            - Total remboursé à ce jour: $totalPaidFcfa FCFA
            - Nombre d'opérations: $recordCount

            Réponds UNIQUEMENT au format JSON strict avec les clés:
            {"score": <entier 0 à 100>, "level": "<Fiable|Prudence|Risqué>", "recommendation": "<phrase explicative courte>", "maxCredit": <montant max recommandé en FCFA>}
        """.trimIndent()

        val responseText = generateContent(prompt)
        try {
            // Try extracting JSON from output
            val jsonStart = responseText.indexOf('{')
            val jsonEnd = responseText.lastIndexOf('}')
            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                val cleanJson = responseText.substring(jsonStart, jsonEnd + 1)
                val obj = JSONObject(cleanJson)
                return@withContext AiRiskEvaluation(
                    score = obj.optInt("score", 75),
                    level = obj.optString("level", "Prudence"),
                    recommendation = obj.optString("recommendation", "Accordez de petits crédits avec suivi régulier."),
                    maxRecommendedCreditFcfa = obj.optDouble("maxCredit", 25000.0)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Heuristic Fallback
        val score = when {
            netBalanceFcfa <= 0 -> 95
            totalPaidFcfa >= netBalanceFcfa -> 80
            netBalanceFcfa > 50000 -> 35
            else -> 65
        }
        val level = when {
            score >= 80 -> "Fiable"
            score >= 50 -> "Prudence"
            else -> "Risqué"
        }
        val rec = when (level) {
            "Fiable" -> "Client très régulier. Vous pouvez renouveler le crédit en toute confiance."
            "Prudence" -> "Solde en attente. Fixez une date limite claire avant d'accorder une nouvelle dette."
            else -> "Solde élevé non réglé. Procédez à une relance amiable par SMS/WhatsApp avant tout nouveau crédit."
        }
        val maxCredit = when (level) {
            "Fiable" -> 100000.0
            "Prudence" -> 30000.0
            else -> 10000.0
        }

        return@withContext AiRiskEvaluation(
            score = score,
            level = level,
            recommendation = rec,
            maxRecommendedCreditFcfa = maxCredit
        )
    }

    suspend fun generateCustomReminder(
        customerName: String,
        balanceFcfa: Double,
        language: AppLanguage
    ): String = withContext(Dispatchers.IO) {
        val langName = when (language) {
            AppLanguage.FRENCH -> "Français"
            AppLanguage.LINGALA -> "Lingala"
            AppLanguage.SWAHILI -> "Swahili"
            AppLanguage.WOLOF -> "Wolof"
        }

        val prompt = """
            Rédige un message SMS/WhatsApp de relance courtoise et respectueuse pour un commerçant africain s'adressant à son client:
            - Nom du client: $customerName
            - Montant dû: $balanceFcfa FCFA
            - Langue souhaitée: $langName

            Sois poli, fraternel, concis et évite toute agressivité. Rédige uniquement le message final sans introduction ni commentaires.
        """.trimIndent()

        val result = generateContent(prompt)
        if (result.isNotBlank() && !result.startsWith("Erreur")) {
            return@withContext result
        }

        return@withContext when (language) {
            AppLanguage.FRENCH -> "Bonjour $customerName, j'espère que vous allez bien. Sauf erreur de ma part, votre solde au carnet s'élève à ${String.format("%,.0f", balanceFcfa)} FCFA. Merci de penser à régler à votre convenance !"
            AppLanguage.LINGALA -> "Mbote $customerName. Mbongo ya carnet ezali ${String.format("%,.0f", balanceFcfa)} FCFA. Soki okoki futa tele, ekozala malamu mingi. Matondi !"
            AppLanguage.SWAHILI -> "Jambo $customerName. Kumbukumbu ya deni lako ni ${String.format("%,.0f", balanceFcfa)} FCFA. Tafadhali kumbuka kulipa utakapoweza. Ahsante!"
            AppLanguage.WOLOF -> "Naka rekk $customerName. Bor bi ci kaye bi méngóona ak ${String.format("%,.0f", balanceFcfa)} FCFA. Jërejëf !"
        }
    }

    private fun fallbackResponse(prompt: String): String {
        return when {
            prompt.contains("commerçant", ignoreCase = true) || prompt.contains("impai", ignoreCase = true) ->
                "💡 **Conseil IA Commerçant** : Pour réduire les impayés, fixez des échéances précises (ex: tous les samedis) et activez l'envoi de rappels automatiques hebdomadaires."
            else ->
                "💡 **Conseil Carnet Kredi** : Suivez quotidiennement les encaissements et privilégiez les remises pour paiement comptant afin de préserver votre trésorerie."
        }
    }
}
