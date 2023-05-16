# [![InkyMessage](https://i.imgur.com/QTTQyml.png)](https://github.com/GlowingInk/InkyMessage)
Adventure API component serializer inspired by [MineDown](https://github.com/Phoenix616/MineDown/tree/kyori-adventure) 
and [MiniMessage](https://docs.advntr.dev/minimessage/index.html) projects. 
An attempt to make legacy-friendly serializer with modern features.

InkyMessage supports legacy format codes, e.g. `&l`, `&6`, `&a`, etc.
Besides that, we also have a special format for the modern features like interactable chat components.
It's very simple to follow: `&[My special text](key:parameter value)(otherkey:othervalue)`.
Those are its possible modifiers:
## Modifier formatting
| Key                | Parameter                                                                                                                           | Value                      | Effect                                    | Example                                                     |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------|----------------------------|-------------------------------------------|-------------------------------------------------------------|
| `hover`            | Only `text` ATM                                                                                                                     | Rich hover text            | Add hover effect to chat component        | `Stop. &[Hover time](hover:text What a meme)!`              |
| `click`            | `url`, `run`, `suggest`, `copy`, `insert` (same as `suggest`, but requires shift-click)                                             | Plain action parameters    | Add click functionality to chat component | `&[Get 100 robux!](click:url https://youtu.be/dQw4w9WgXcQ)` |
| `decor`            | `bold`, `obfuscated`, `strikethrough`, `underlined`, `italic`                                                                       | `true`, `unset`, `false`   | Force decorator on the text               | `&cThat's a &[bold](decor:bold) move!`                      |
| `font`             | Namespaced key of a font                                                                                                            | None                       | Change fonts of a text                    | `Wow, &[almost HD fonts](font:minecraft:uniform)!`          |
| `color`            | [Named color](https://jd.advntr.dev/api/4.13.1/net/kyori/adventure/text/format/NamedTextColor.html) (lower case) or hex (`#123456`) | None ATM                   | Colorize colorless text                   | `&[This text is green](color:green)`                        |
| `color` (gradient) | `spectrum` or `color1-color2-colorN` (see the original `color` modifier)                                                            | `pastel` for pastel colors | Colorize colorless text with gradient     | `&aLook!&r &[Fancy!](color:spectrum)(decor:bold)`           |

## Plans (until full release)
- Placeholders (for keybinds, selectors)
- Better symbolic styles stacking on serialization
- Some actual useful API
- More tests

## Get it ![Version](https://img.shields.io/github/v/tag/GlowingInk/InkyMessage?sort=semver&style=flat&label=release)
Versions in dependency sections may be outdated. Check the badge above for the latest one.
### Maven
Add to repositories
```xml
<repository>
    <id>glowing-ink</id>
    <url>https://repo.glowing.ink/releases</url> <!-- https://repo.glowing.ink/snapshots for snapshots -->
</repository>
```
Add to dependencies
```xml
<dependency>
    <groupId>ink.glowing</groupId>
    <artifactId>inkymessage</artifactId>
    <version>0.11.0</version>
</dependency>
```
### Gradle
```kotlin
repositories {
    maven {
        url = uri("https://repo.glowing.ink/releases") // https://repo.glowing.ink/snapshots for snapshots
    }
}

dependencies {
    implementation("ink.glowing:inkymessage:0.11.0")
}
```