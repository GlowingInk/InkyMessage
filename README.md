# InkyMessage
Adventure API component serializer inspired by [MineDown](https://github.com/Phoenix616/MineDown/tree/kyori-adventure) 
and [MiniMessage](https://docs.advntr.dev/minimessage/index.html) projects. 
An attempt to make legacy-friendly serializer with modern features.

InkyMessage supports legacy format codes, e.g. `&l`, `&6`, `&a`, etc.
Besides that, we also have a special format for the modern features like interactable chat components. 
It's very simple to follow: `&[My special text](key:value parameters)(otherkey:othervalue)`. 
Those are its possible tags:
## Tag formatting
| Key        | Value                                                                                                                               | Parameter                | Effect                                    | Example                                                             |
|------------|-------------------------------------------------------------------------------------------------------------------------------------|--------------------------|-------------------------------------------|---------------------------------------------------------------------|
| `hover`    | Only `text` ATM                                                                                                                     | Hover text               | Add hover effect to chat component        | `Stop. &[hover time](hover:text What a meme)!`                      |
| `click`    | `url`, `run`, `suggest`, `copy`, `insert` (same as `suggest`, but requires shift-click)                                             | Action parameters        | Add click functionality to chat component | `&[Click to get 100 robux](click:url https://youtu.be/dQw4w9WgXcQ)` |
| `decor`    | `bold`, `obfuscated`, `strikethrough`, `underlined`, `italic`                                                                       | `true`, `unset`, `false` | Force decorator on the text               | `&cThat's a &[bold](decor:bold) move!`                              |
| `font`     | Namespaced key of a font                                                                                                            | None                     | Change fonts of a text                    | `Wow, &[almost HD fonts](font:minecraft:uniform)!`                  |
| `color`    | [Named color](https://jd.advntr.dev/api/4.13.1/net/kyori/adventure/text/format/NamedTextColor.html) (lower case) or hex (`#123456`) | None ATM                 | Colorize colorless text                   | `&[This text is green](color:green)`                                |
| `gradient` | `spectrum` (`rainbow`) or `color1-color2-colorN` (look `color` tag)                                                                 | None ATM                 | Colorize colorless text with gradient     | `&aLook!&r &[Fancy!](gradient:spectrum)(decor:bold)`                |

## TODO (until full release)
- Placeholders (for keybinds, translations, selectors)
- Fix symbolic styles stacking on serialization
- Simplified MineDown-like style tags (e.g. `&[GitHub Link](https://github.com/)`, `&[Stop the server](/stop)`, `&[Bold text](bold)`)
- Refactor to not use special characters to detect nodes
- Closing colors(?)
- More tests

## Get it ![Version](https://img.shields.io/github/v/tag/GlowingInk/InkyMessage?sort=semver)
### Maven
Add to repositories
```xml
<repository>
    <id>glowing-ink</id>
    <url>http://repo.glowing.ink/releases</url>
</repository>
```
Or for latest snapshots
```xml
<repository>
    <id>glowing-ink</id>
    <url>http://repo.glowing.ink/snapshots</url>
</repository>
```
Add to dependencies
```xml
<dependency>
    <groupId>ink.glowing</groupId>
    <artifactId>inkymessage</artifactId>
    <version>0.8.0</version> <!-- Check the version above -->
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
    implementation("ink.glowing:inkymessage:0.8.0") // Check the version above
}
```