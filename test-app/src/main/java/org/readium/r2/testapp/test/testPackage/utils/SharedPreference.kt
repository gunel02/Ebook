package org.readium.r2.testapp.test.testPackage.utils

import Const
import android.content.Context
import android.content.SharedPreferences

object SharedPreference {

    private const val SHARED_PREFERENCES_NAME = "MY_SHARED_PREFERENCES"
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var sharedPreferences: SharedPreferences
    private const val KEY_LANGUAGE = "language"
    private const val HAS_BOOKS = "has_books"

    fun init(context: Context) {
        sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun setLang(language: String) {
        editor.putString(KEY_LANGUAGE, language).apply()
    }

    fun getLang(): String? {
        return sharedPreferences.getString(KEY_LANGUAGE, Const.ENGLISH_LANG)
    }

    fun setHasBooks(hasBooks: Boolean) {
        editor.putBoolean(HAS_BOOKS, hasBooks).apply()
    }

    fun hasBooks(): Boolean {
        return sharedPreferences.getBoolean(HAS_BOOKS, false)
    }



    fun clearSharedPreference() {
        editor.clear().apply()
    }

}

