# JUnit Locale Extension

JUnit 5 test template extension that re-runs a test for each provided locale and temporarily sets `Locale.getDefault()` accordingly.

## Usage

Add the JitPack repository and the dependency (use a git tag or commit hash, e.g. `1.0.0`):

Add it in your settings.gradle.kts at the end of repositories:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

dependencies {
    testImplementation("com.github.no3x:junit-locale-extension:TAG")
}
```

Annotate a test template with `@WithLocale` to have it executed once per language tag:

```kotlin
class WithLocaleExtensionTest {

    @TestTemplate
    @WithLocale("de-DE", "en-US")
    fun `default locale matches parameter`(localeTag: String) {
        assertEquals(Locale.getDefault().toLanguageTag(), localeTag)
    }

    @TestTemplate
    @WithLocale("fr-FR")
    fun `works with Locale parameter`(locale: Locale) {
        assertEquals("fr", locale.language)
    }
}
```

You can also annotate the test class to apply the extension (and the same set of locales) to multiple `@TestTemplate` methods:

```kotlin
@WithLocale("de-DE", "en-US")
class ClassLevelWithLocaleExtensionTest {
    @TestTemplate
    fun `class level annotation provides locales`(locale: Locale) { /* ... */ }
}
```

If no tags are provided, the current default locale is used once.

## Local development

```bash
./gradlew test
```

Artifacts `sourcesJar` and `javadocJar` are published via the `mavenJava` publication for JitPack compatibility.

## License

MIT License – see [LICENSE](LICENSE).
