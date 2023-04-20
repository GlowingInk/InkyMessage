# Inky Message
Adventure API component serializer inspired by [MineDown](https://github.com/Phoenix616/MineDown/tree/kyori-adventure) 
and MiniMessage projects. An attempt to make legacy-friendly serializer with modern features.

Inky Message supports legacy format codes, e.g. `&l`, `&6`, `&a`, etc.
Besides that, we also have a special format for the modern features like interactable chat components. It's very simple
to follow: `&[My special text](key:value parameters)`. Those are its possible combinations:
## Tag formatting
| Key                | Values                                                                                                                               | Parameters                                        | Effect                                    | Example                                                             |
|--------------------|--------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------|-------------------------------------------|---------------------------------------------------------------------|
| `hover`            | Only `text` ATM                                                                                                                      | Hover text                                        | Add hover effect to chat component        | `Stop. &[hover time](hover:text What a meme)!`                      |
| `click`            | `url`, `run`, `suggest`, `copy`, `insert` (same as `suggest`, but requires shift-click)                                              | Action parameters                                 | Add click functionality to chat component | `&[Click to get 100 robux](click:url https://youtu.be/dQw4w9WgXcQ)` |
| `decor`            | `bold`, `obfuscated`, `strikethrough`, `underlined`, `italic`                                                                        | `true`, `unset`, `false`                          | Force decorator on the text               | `&cThat's a &[bold](decor:bold) move!`                              |
| `font`             | Namespaced key of a font                                                                                                             | None                                              | Change fonts of a text                    | `Wow, &[almost HD fonts](font:minecraft:uniform)!`                  |
| `color`            | [Named color](https://jd.advntr.dev/api/4.13.1/net/kyori/adventure/text/format/NamedTextColor.html) (lower case) and hex (`#123456`) | None ATM                                          | Colorize empty text                       | `&[This text is green](color:green)`                                |
| `color` (gradient) | `gradient`                                                                                                                           | `rainbow`, `color1-color2-colorN` (look `colors`) | Colorize empty text with gradient         | `&aLook!&r &[Fancy!](color:gradient rainbow)(decor:bold)`           |


## TODO (until full release)
- Placeholders (for keybinds, translations, selectors)
- Actually implement serializer (as all we have now is deserializer lol)
- Custom regex formatted replacers
- Simplified MineDown-like style tags (e.g. `&[GitHub Link](https://github.com/)`)
- Closing colors(?)

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
    <version>0.6.3</version> <!-- Check the version above -->
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
    implementation("ink.glowing:inkymessage:0.6.3") // Check the version above
}
```