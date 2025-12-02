package de.no3x.junit.extension.locale

import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.util.Locale
import kotlin.test.assertEquals

@Execution(ExecutionMode.CONCURRENT)
class WithLocaleParallelRaceTest {

    @TestTemplate
    @WithLocale("en-US", "fr-FR", "ja-JP", "de-DE", "it-IT", "es-ES")
    fun `locale stays consistent under concurrent runs`(localeTag: String) {
        // Sleep a little to increase overlap between concurrent template invocations
        Thread.sleep(50)
        assertEquals(localeTag, Locale.getDefault().toLanguageTag())
    }
}
