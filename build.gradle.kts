plugins {
    java
    application
    id("io.franzbecker.gradle-lombok") version "1.14"
}

group = "se.totalorder"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

application {
    mainClassName = "se.totalorder.basen.Application"
}

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/palantir/releases")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compile("io.javalin:javalin:2.2.0")
    compile("ch.qos.logback:logback-classic:1.2.3")
    compile("com.zaxxer:HikariCP:3.2.0")
    compile("org.flywaydb:flyway-core:5.1.4")
    compile("org.postgresql:postgresql:42.2.5")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.7")
    compile("com.google.guava:guava:27.0-jre")
    compile("com.typesafe:config:1.3.2")
    compile("se.deadlock:txman:0.0.2-SNAPSHOT")
    compile("se.deadlock:okok:0.0.1-SNAPSHOT")

    testCompile("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testCompile("org.hamcrest:hamcrest-library:1.3")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.1")
    testCompile("org.mockito:mockito-core:2.23.0")
    testCompile("se.deadlock:composed:0.0.1")
}

lombok {
    version = "1.18.2"
    sha256 = ""
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    distZip {
        enabled = false
    }

    val untarDist by creating(Copy::class) {
        dependsOn(distTar)

        val dockerDir by extra { "${buildDir}/docker/${distTar.get().archiveFileName.get().substring(0, distTar.get().archiveFileName.get().lastIndexOf("."))}" }

        from(tarTree(distTar.get().archiveFile))
        into("${buildDir}/docker")
    }

    val copyDockerfile by registering(Copy::class) {
        dependsOn(untarDist)

        val dockerDir: String by untarDist.extra

        from("Dockerfile")
        into(dockerDir)
    }

    val distDocker by registering(Exec::class) {
        dependsOn(copyDockerfile)

        val dockerDir: String by untarDist.extra

        workingDir(dockerDir)
        commandLine("bash", "-c", "docker build -t ${project.name} .")
    }
}

tasks.register<Exec>("buildPostgres") {
    commandLine("bash", "-c", "docker-compose build postgres")
}

tasks.register<Exec>("startPostgres") {
    commandLine("bash", "-c", "docker-compose up -d postgres")
}

tasks.register<Exec>("stopPostgres") {
    commandLine("bash", "-c", "docker-compose stop -t 0 postgres")
}
