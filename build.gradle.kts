plugins {
    kotlin("jvm") version "2.2.0"
    `maven-publish`
    `java-library`
}

group = "com.github.no3x"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.14.1"
    implementation(platform("org.junit:junit-bom:$junitVersion"))
    implementation(group = "org.junit.jupiter", name = "junit-jupiter-api")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter-engine")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("junit-locale-extension")
                description.set("JUnit 5 test template extension to run a test for multiple locales.")
                url.set("https://github.com/no3x/junit-locale-extension")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("no3x")
                    }
                }
                scm {
                    url.set("https://github.com/no3x/junit-locale-extension")
                    connection.set("scm:git:git://github.com/no3x/junit-locale-extension.git")
                    developerConnection.set("scm:git:ssh://github.com:no3x/junit-locale-extension.git")
                }
            }
        }
    }
}
