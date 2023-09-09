pluginManagement {
	repositories {
		maven { url = uri("https://repo.spring.io/milestone") }
		gradlePluginPortal()
	}
}

plugins {
  // See https://splitties.github.io/refreshVersions
  id("de.fayard.refreshVersions") version "0.60.2"
}
