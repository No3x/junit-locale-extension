package de.no3x.junit.extension.locale

import java.util.Locale

import org.junit.jupiter.api.TestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WithLocaleExtensionTest {

    @TestTemplate
    @WithLocale("de-DE", "en-US", "fr-FR", "ja-JP")
    fun `Locale getDefault() returns the locale for each run`(locale: String) {
        assertEquals(Locale.getDefault().toLanguageTag(), locale)
    }

    @TestTemplate
    @WithLocale
    fun `falls back to default locale when no tag is given`(localeTag: String) {
        assertEquals(Locale.getDefault().toLanguageTag(), localeTag)
    }

    @TestTemplate
    @WithLocale(" de-DE ")
    fun `trims provided language tags`(locale: Locale) {
        assertEquals("de", locale.language)
    }
}

@WithLocale("de-DE", "en-US")
class ClassLevelWithLocaleExtensionTest {

    @TestTemplate
    fun `class level annotation provides locales`(locale: Locale) {
        assertTrue(setOf("de", "en").contains(locale.language))
    }
}
