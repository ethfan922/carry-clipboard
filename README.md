# Carry Clipboard

## Setup

For setup instructions, please see the [Fabric Documentation page](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up) related to the IDE that you are using.

## Bleh

For all features, you will need to download and re-build [Odin](https://github.com/odtheking/Odin) with one file changed:
src/main/kotlin/com/odtheking/odin/features/impl/boss/ExtraStats.kt

Change `modMessage(message, "")` to
```
val client = Minecraft.getInstance();
client.player?.sendSystemMessage(message);
```

There might be a better way to implement that but idk