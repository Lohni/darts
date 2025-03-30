package com.lohni.darts.common

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class FormatUtil {
    companion object {
        private val localeSymbols = DecimalFormatSymbols(Locale.US)

        fun createDecimalFormat(pattern: String): DecimalFormat {
            return DecimalFormat(pattern, localeSymbols)
        }
    }
}