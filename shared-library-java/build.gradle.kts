plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.kordamp.gradle.jandex") version "2.2.0"
}

group = "de.bethibande.k8s"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val quarkusVersion = "3.26.0"

    compileOnly("io.fabric8:generator-annotations:7.3.1")
    compileOnly("io.fabric8:kubernetes-client:7.3.1")
    compileOnly("io.quarkus:quarkus-kubernetes-client:$quarkusVersion")
    implementation("io.quarkus:quarkus-rest-jackson:$quarkusVersion")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name = project.name
                description = project.description

                url = "https://github.com/Bethibande/monitor"

                licenses {
                    license {
                        name = "GPL-3.0"
                        url = "https://raw.githubusercontent.com/Bethibande/monitor/refs/heads/master/LICENSE"
                    }
                }

                developers {
                    developer {
                        id = "bethibande"
                        name = "Max Bethmann"
                        email = "contact@bethibande.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Bethibande/monitor.git"
                    developerConnection = "scm:git:ssh://github.com/Bethibande/monitor.git"
                    url = "https://github.com/Bethibande/monitor"
                }
            }
        }
    }

    repositories {
        maven {
            name = "Maven-Releases"
            url = uri("https://pckg.bethibande.com/repository/maven-snapshots/")
            credentials {
                username = providers.gradleProperty("mavenUsername").get()
                password = providers.gradleProperty("mavenPassword").get()
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}