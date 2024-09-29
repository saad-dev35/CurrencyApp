package com.playaxis.currencyapp.utils

import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import java.io.File

fun View.showKeyboard(view: EditText? = null) {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    (this.context.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.toVisibleAndFocus() {
    this.visibility = View.VISIBLE
    if (this is EditText) {
        this.requestFocus()
        showKeyboard(this)
    }
}

fun View.toVisible() {
    this.visibility = View.VISIBLE
}

fun View.toGone() {
    this.visibility = View.GONE
}

fun View.toInvisible() {
    this.visibility = View.INVISIBLE
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Uri.toBase64(): String? {
    val path = this.path ?: return null // Check if path exists
    val file = File(path)
    if (!file.isFile) return null
    val bytes = file.readBytes()
    return Base64.encodeToString(bytes, Base64.URL_SAFE)
}

fun Context.copyToClipboard(label: String = "", content: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, content)
    clipboard.setPrimaryClip(clip)
}

private val htmlEntitiesMap = mapOf(
    "&#39;" to "'",
    "&quot;" to "\"",
    "&amp;" to "&",
    "&lt;" to "<",
    "&gt;" to ">"
)

// Extension function to replace HTML entities
fun String?.replaceHtmlEntities(): String? {
    if (this == null) return null
    var result = this
    var replaced = false
    // Check if the string contains any HTML entities
    for (entity in htmlEntitiesMap.keys) {
        if (result!!.contains(entity)) {
            replaced = true
            result = result.replace(entity, htmlEntitiesMap[entity] ?: "")
        }
    }
    return if (replaced) result else this
}