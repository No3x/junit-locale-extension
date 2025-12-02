package de.no3x.junit.extension.locale

import org.junit.jupiter.api.extension.*
import org.junit.platform.commons.PreconditionViolationException
import org.junit.platform.commons.support.AnnotationSupport
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Stream
import kotlin.streams.asStream

/**
 * JUnit 5 extension that temporarily switches the default locale for a test method
 * annotated with [WithLocale]. For each language tag provided, the test is re-run
 * with that locale set as the default.
 */
class WithLocaleExtension : TestTemplateInvocationContextProvider {

    override fun supportsTestTemplate(context: ExtensionContext): Boolean =
        findWithLocaleAnnotation(context).isPresent

    override fun provideTestTemplateInvocationContexts(context: ExtensionContext): Stream<TestTemplateInvocationContext> {
        val annotation = findWithLocaleAnnotation(context).orElseThrow {
            PreconditionViolationException("@WithLocale must be present on the test method or class")
        }

        val languageTags = annotation.value
            .asSequence()
            .map(String::trim)
            .filter(String::isNotEmpty)
            .ifEmpty { sequenceOf(Locale.getDefault().toLanguageTag()) }

        return languageTags
            .map { tag -> LocaleTemplateContext(tag) as TestTemplateInvocationContext }
            .asStream()
    }

    private fun findWithLocaleAnnotation(context: ExtensionContext) =
        AnnotationSupport.findAnnotation(context.testMethod, WithLocale::class.java)
            .or { AnnotationSupport.findAnnotation(context.testClass, WithLocale::class.java) }

    private class LocaleTemplateContext(private val languageTag: String) : TestTemplateInvocationContext {
        override fun getDisplayName(invocationIndex: Int): String = "locale=$languageTag"

        override fun getAdditionalExtensions(): MutableList<Extension> =
            mutableListOf(
                LocaleSettingCallback(languageTag),
                LocaleParameterResolver(languageTag)
            )
    }

    private class LocaleSettingCallback(private val languageTag: String) : BeforeEachCallback, AfterEachCallback {
        override fun beforeEach(context: ExtensionContext) {
            val lock = getLocaleLock(context)
            lock.lock()
            val store = context.getStore(contextNamespace(context))
            store.put(PREVIOUS_KEY, Locale.getDefault())
            Locale.setDefault(Locale.forLanguageTag(languageTag))
        }

        override fun afterEach(context: ExtensionContext) {
            val lock = getLocaleLock(context)
            try {
                val store = context.getStore(contextNamespace(context))
                (store.get(PREVIOUS_KEY, Locale::class.java))?.let { Locale.setDefault(it) }
                store.remove(PREVIOUS_KEY)
            } finally {
                lock.unlock()
            }
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
            return setOf(String::class.java, Locale::class.java).any { it.isAssignableFrom(type) }
        }

        override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any =
            when {
                String::class.java.isAssignableFrom(parameterContext.parameter.type) -> languageTag
                Locale::class.java.isAssignableFrom(parameterContext.parameter.type) -> Locale.forLanguageTag(languageTag)
                else -> throw ParameterResolutionException("Unsupported parameter type ${parameterContext.parameter.type}")
            }
    }

    private companion object {
        private const val LOCK_KEY = "locale-default-lock"
        private const val PREVIOUS_KEY = "previous-locale"

        /**
         * JUnit best practice: store shared mutable state in the root Store instead of static singletons
         * so it is scoped to the test run and works across different class loaders/test engines.
         */
        fun getLocaleLock(context: ExtensionContext): ReentrantLock =
            context.root.getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(LOCK_KEY) { ReentrantLock(true) } as ReentrantLock

        fun contextNamespace(context: ExtensionContext): ExtensionContext.Namespace =
            ExtensionContext.Namespace.create(WithLocaleExtension::class.java, context.uniqueId)
    }
}
