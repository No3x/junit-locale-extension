package de.no3x.junit.extension.locale

import org.junit.jupiter.api.extension.*
import java.util.*
import java.util.stream.Stream

/**
 * JUnit 5 extension that temporarily switches the default locale for a test method
 * annotated with [WithLocale]. For each language tag provided, the test is re-run
 * with that locale set as the default.
 */
class WithLocaleExtension : TestTemplateInvocationContextProvider {
    override fun supportsTestTemplate(context: ExtensionContext): Boolean =
        context.testMethod.map { it.isAnnotationPresent(WithLocale::class.java) }.orElse(false)

    override fun provideTestTemplateInvocationContexts(context: ExtensionContext): Stream<TestTemplateInvocationContext> {
        val annotation = context.requiredTestMethod.getAnnotation(WithLocale::class.java)
        val languageTags = if (annotation.value.isNotEmpty()) annotation.value else arrayOf(Locale.getDefault().toLanguageTag())

        return languageTags.asSequence()
            .map { tag -> LocaleTemplateContext(tag) as TestTemplateInvocationContext }
            .toList()
            .stream()
    }

    private class LocaleTemplateContext(private val languageTag: String) : TestTemplateInvocationContext {
        override fun getDisplayName(invocationIndex: Int): String = "locale=$languageTag"

        override fun getAdditionalExtensions(): MutableList<Extension> =
            mutableListOf(
                LocaleSettingCallback(languageTag),
                LocaleParameterResolver(languageTag)
            )
    }

    private class LocaleSettingCallback(private val languageTag: String) : BeforeEachCallback, AfterEachCallback {
        private var previous: Locale? = null

        override fun beforeEach(context: ExtensionContext) {
            previous = Locale.getDefault()
            Locale.setDefault(Locale.forLanguageTag(languageTag))
        }

        override fun afterEach(context: ExtensionContext) {
            previous?.let { Locale.setDefault(it) }
            previous = null
        }
    }

    /**
     * Supplies the current test locale to parameters.
     *
     * Supports both `String` (language tag) and `Locale` parameters so test
     * methods can either use the tag directly or work with the Locale object.
     */
    private class LocaleParameterResolver(private val languageTag: String) : ParameterResolver {
        override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
            val type = parameterContext.parameter.type
            return type == String::class.java || type == Locale::class.java
        }

        override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
            when (parameterContext.parameter.type) {
                String::class.java -> languageTag
                Locale::class.java -> Locale.forLanguageTag(languageTag)
                else -> throw ParameterResolutionException("Unsupported parameter type ${parameterContext.parameter.type}")
            }
    }
}
