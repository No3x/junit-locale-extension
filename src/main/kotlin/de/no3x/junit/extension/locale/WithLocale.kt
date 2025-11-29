package de.no3x.junit.extension.locale

import org.junit.jupiter.api.extension.ExtendWith

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER)
@ExtendWith(WithLocaleExtension::class)
annotation class WithLocale(vararg val value: String)
