package de.no3x.junit.extension.locale

import java.util.Locale

import org.junit.jupiter.api.TestTemplate
import kotlin.test.assertEquals

class WithLocaleExtensionTest {

    @TestTemplate
    @WithLocale("de-DE", "en-US", "fr-FR", "ja-JP")
    fun `Locale getDefault() returns the locale for each run`(locale: String) {
        assertEquals(Locale.getDefault().toLanguageTag(), locale)
    }

}
