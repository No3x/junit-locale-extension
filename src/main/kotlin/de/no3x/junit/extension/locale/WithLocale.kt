package de.no3x.junit.extension.locale

import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.ResourceAccessMode
import org.junit.jupiter.api.parallel.ResourceLock

@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER
)
@ExtendWith(WithLocaleExtension::class)
@ResourceLock(value = "de.no3x.junit.extension.locale:jvm-locale", mode = ResourceAccessMode.READ_WRITE)
annotation class WithLocale(vararg val value: String)
