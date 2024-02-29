package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.utils

import java.util.*


object LanguageUtils {
    private val ENGLISH = "en"
    private val SLOVAK = "sk"

    fun isLanguageSlovak(): Boolean {
        val language = Locale.getDefault().language
        return language.equals(ENGLISH) || language.equals(SLOVAK)
    }
}