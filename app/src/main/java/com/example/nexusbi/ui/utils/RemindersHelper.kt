package com.example.nexusbi.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.nexusbi.ui.language.AppLanguage
import java.net.URLEncoder

object RemindersHelper {

    fun generateReminderText(
        customerName: String,
        amountDue: Double,
        currencySymbol: String = "FCFA",
        language: AppLanguage
    ): String {
        val formattedAmount = String.format("%,.0f", amountDue)
        return when (language) {
            AppLanguage.FRENCH -> {
                "Bonjour $customerName, rappel amical de votre boutique : votre solde restant s'élève à $formattedAmount $currencySymbol. Merci de votre confiance !"
            }
            AppLanguage.LINGALA -> {
                "Mbote $customerName, rapel ya boutique na biso : nyongo otikali na yango ezali $formattedAmount $currencySymbol. Matondi mingi !"
            }
            AppLanguage.SWAHILI -> {
                "Jambo $customerName, kikumbusho cha kirafiki kutoka duka : salio la deni lako ni $formattedAmount $currencySymbol. Asante kwa uaminifu !"
            }
            AppLanguage.WOLOF -> {
                "Naka mu mel $customerName, rapel bu am jam ci boutique : asee bu ci des mungii tollu ci $formattedAmount $currencySymbol. Jërejëf !"
            }
        }
    }

    fun sendSms(context: Context, phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber")
            putExtra("sms_body", message)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendWhatsApp(context: Context, phoneNumber: String, message: String) {
        val cleanPhone = phoneNumber.replace("+", "").replace(" ", "").replace("-", "")
        val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=${URLEncoder.encode(message, "UTF-8")}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            sendSms(context, phoneNumber, message)
        }
    }
}
