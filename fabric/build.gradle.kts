//repositories {
//  maven { url "https://maven.shedaniel.me/" }
//  maven { url "https://maven.terraformersmc.com/" }
//  maven {
//    name = "CottonMC"
//    url = "https://server.bbkr.space/artifactory/libs-release"
//  }
//  mavenCentral()
//}

dependencies {
  // Depend on the common project. The "namedElements" configuration contains the non-remapped
  // classes and resources of the project.
  // It follows Gradle's own convention of xyzElements for "outgoing" configurations like apiElements.
  implementation(project(":common", configuration = "namedElements")) {
    isTransitive = false
  }

  modImplementation(libs.fabric.loader)
  modImplementation(libs.fabric.api)

  modApi(libs.cloth.config.fabric) {
    exclude(group = "net.fabricmc.fabric-api")
  }

  modImplementation(libs.modmenu)

//  implementation 'org.junit.jupiter:junit-jupiter:5.8.2'
}

tasks {
  processResources {
    // Mark that this task depends on the project version,
    // and should reset when the project version changes.
    inputs.property("version", project.version)

    // Replace the $version template in fabric.mod.json with the project version.
    filesMatching("fabric.mod.json") {
      expand("version" to project.version)
    }
  }
}
