# JUnit Locale Extension

JUnit 5 test template extension that re-runs a test for each provided locale and temporarily sets `Locale.getDefault()` accordingly.

## Usage

Add the JitPack repository and the dependency (replace `TAG` with a git tag or commit hash, e.g. `0.1.0`):

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
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

If no tags are provided, the current default locale is used once.

## Local development

```bash
./gradlew test
```

Artifacts `sourcesJar` and `javadocJar` are published via the `mavenJava` publication for JitPack compatibility.

## License

MIT License – see [LICENSE](LICENSE).
