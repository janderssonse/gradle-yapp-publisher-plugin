import se.svt.oss.gradle.yapp.extension.Developer

plugins {
    {plugin1}
    {plugin2}
    id("se.svt.oss.gradle-yapp-publisher-plugin") version "1.0.0-SNAPSHOT"
}

group = "{group}"
version = "{version}"

repositories {
    mavenLocal()
    mavenCentral()
}

{yappConf}

