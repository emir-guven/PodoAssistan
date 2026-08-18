package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri

fun callPhone(context: Context, phoneNumber: String) {
    try {
        val sanitizedPhone = phoneNumber.replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$sanitizedPhone")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
    }
}
