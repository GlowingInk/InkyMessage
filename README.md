# [![InkyMessage](/.github/assets/Logo.png)](https://github.com/GlowingInk/InkyMessage)
> Adventure API component serializer inspired by [MineDown](https://github.com/Phoenix616/MineDown) 
and [MiniMessage](https://docs.advntr.dev/minimessage/index.html) projects. 
An attempt to make legacy-friendly serializer with modern features.

[JavaDoc](https://repo.glowing.ink/javadoc/snapshots/ink/glowing/inkymessage/latest)

InkyMessage supports legacy format codes, e.g. `&l`, `&6`, `&a`, etc.
Besides that, InkyMessage also has a special format for the modern features like interactable chat components.
It's very simple to follow: `&[My special text](key:parameter value)(otherkey:otherparam)`.

## Modifiers
| Key                | Parameter                                                                                                                           | Value                      | Information                                | Example                                                     |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------|----------------------------|--------------------------------------------|-------------------------------------------------------------|
| `hover`            | Only `text` ATM                                                                                                                     | Rich hover text            | Adds hover effect to chat component        | `Stop. &[Hover time](hover:text What a meme)!`              |
| `click`            | `url`, `run`, `suggest`, `copy`, `insert` (same as `suggest`, but requires shift-click)                                             | Plain action parameters    | Adds click functionality to chat component | `&[Get 100 robux!](click:url https://youtu.be/dQw4w9WgXcQ)` |
| `decor`            | `bold`, `obfuscated`, `strikethrough`, `underlined`, `italic`                                                                       | `true`, `unset`, `false`   | Forces decorator on the text               | `&cThat's a &[bold](decor:bold) move!`                      |
| `font`             | Namespaced key of a font                                                                                                            | None                       | Changes fonts of a text                    | `Wow, &[almost HD fonts](font:minecraft:uniform)!`          |
| `color`            | [Named color](https://jd.advntr.dev/api/4.20.0/net/kyori/adventure/text/format/NamedTextColor.html) (lower case) or hex (`#123456`) | `pastel` for pastel colors | Colorizes colorless text                   | `&[This text is green](color:green)`                        |
| `color` (gradient) | `spectrum`, `random` or `color1-color2-colorN` (named or hex)                                                                       | `pastel` for pastel colors | Colorizes colorless text with gradient     | `&aLook!&r &[Fancy!](color:spectrum)(decor:bold)`           |
## Placeholders
Placeholders are using similar format: `&{placeholder:ph_parameter}`. Placeholders may also have additional modifiers 
bound to them, which are different from the default ones.

| Key        | Parameter                | Information                                                                   | Example                                                                                                     |
|------------|--------------------------|-------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------|
| `lang`     | Translation key          | Shows client's translation. Use `arg` (rich) and `fallback` (plain) modifiers | `&4&lDeath>&r &{lang:death.attack.cramming.player}(arg &eSteve)(arg &cChicken Jockey)(fallback Steve died)` |
| `keybind`  | Keybind name             | Shows keybind in client's localization                                        | `Press &{keybind:key.sneak} to crouch!`                                                                     |
| `score`    | Score name and objective | Shows score                                                                   | `Steve ate &{score:Steve chickensAte} chickens!`                                                            |
| `selector` | Selector                 | Shows results of selection. Use `separator` (rich) modifier                   | `Mobs nearby: &{selector:@e[distance..10]}(separator &e, )`                                                 |

## Plans (until 1.0.0 release)
- Shadow color modifier (~0.12.x)
- Arbitrary type and amount of modifiers values (~0.13.x)
- Better API for marking (de)serializable elements / precise escaping API (~0.14.x)
- Better serialization
- More and better unit tests

## Get it ![Version](https://img.shields.io/github/v/tag/GlowingInk/InkyMessage?sort=semver&style=flat&label=release)
Versions in dependency sections may be outdated. Check the badge above for the latest one.
### Maven
Add to repositories
```xml
<repository>
    <id>glowing-ink</id>
    <url>https://repo.glowing.ink/releases</url>
    <!-- https://repo.glowing.ink/snapshots for snapshots -->
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
        url = uri("https://repo.glowing.ink/releases")
        // https://repo.glowing.ink/snapshots for snapshots
    }
}

dependencies {
    implementation("ink.glowing:inkymessage:0.11.0")
}
```