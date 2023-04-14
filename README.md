# Inky Message
Adventure API component serializer inspired by [MineDown](https://github.com/Phoenix616/MineDown/tree/kyori-adventure) 
and MiniMessage projects. An attempt to make legacy-friendly serializer with modern features.

## TODO
- Gradient stuff
- Placeholders (for keys, translations, selectors)
- Actually implement serializer (as all we have now is deserializer lol)
- Closing colors(?)

## Get it ![Version](https://img.shields.io/github/v/release/GlowingInk/InkyMessage?logo=github)
### Maven
Add to repositories
```xml
<repository>
    <id>glowing-ink</id>
    <url>http://repo.glowing.ink/</url>
</repository>
```
Add to dependencies
```xml
<dependency>
    <groupId>ink.glowing</groupId>
    <artifactId>inkymessage</artifactId>
    <version>0.4.0</version> <!-- Check the version above -->
</dependency>
```
### Gradle
```kotlin
repositories {
    maven {
        url = uri("http://repo.glowing.ink/releases")
    }
}

dependencies {
    implementation("ink.glowing:inkymessage:0.4.0") // Check the version above
}
```