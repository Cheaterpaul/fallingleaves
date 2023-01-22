Falling Leaves for Minecraft 1.19 - Forge [![](http://cf.way2muchnoise.eu/short_463155_downloads.svg)](https://www.curseforge.com/minecraft/mc-mods/falling-leaves-forge) [![](https://img.shields.io/modrinth/dt/enchantmentmachine?color=00AF5C&label=modrinth&style=flat&logo=modrinth)](https://modrinth.com/mod/fallingleavesforge) [![License: LGPL v3](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0) [![](https://badgen.net/maven/v/metadata-url/https/maven.paube.de/releases/de/cheaterpaul/fallingleaves/Fallingleaves/maven-metadata.xml)](https://maven.paube.de/#/releases/de/cheaterpaul/fallingleaves/Fallingleaves)
===================================

## Mod Description

This Forge mod for Minecraft 1.18.x adds a neat little particle effect to leaf blocks. Users can configure which types
of leaf blocks will drop leaves and the frequency that these leaves are dropped at.

![](https://i.imgur.com/Tek7xJe.gif)

## Links

[Curseforge](https://www.curseforge.com/minecraft/mc-mods/falling-leaves-forge)
[Github](https://github.com/Cheaterpaul/fallingleaves)
[Issues](https://github.com/Cheaterpaul/fallingleaves/issues)

Original Fabric Version:

[Fabric Curseforge](https://www.curseforge.com/minecraft/mc-mods/falling-leaves-fabric)
[Fabric Github](https://github.com/RandomMcSomethin/fallingleaves)

## Resource packs

### Changing leaf settings

For every leaf block you can configure spawn rate, leaf type and if it is a conifer block.
This is done by modifying/creating the file `assets/<modid>/fallingleaves/settings/<blockid>.json` with the following content:

```json
{
  "spawnrate": <double>,
  "leaf_type": <leaftype>,
  "consider_as_conifer": <boolean>
}
```

The leaf type supports the default values `fallingleaves:default` and `fallingleaves:conifer` or add a [new one](#create-leaf-types).  
the conifer setting is only relevant if no leaf type is set or for different spawn rates for normal and conifer leaves.

If no leaf type is set, the mod checks if there is a custom leaf type with the same name as the block id. If not either `fallingleaves:default` or `fallingleaves:conifer` is used depending on the `consider_as_conifer` value

### Changing textures
All textures in the `assets/fallingleaves/textures/particle` folder can simply be overwritten by a resource pack.

### Create leaf types
Leaf types define the textures that can be used for the leaves. By default, there are two leaf types: `fallingleaves:default` and `fallingleaves:conifer`.

You can create a file `assets/<modid>/fallingleaves/leaftypes/<type>.json` with the following content:

```json
{
  "textures": [
    "<modid>:<particle texture>",
    "<modid>:<particle texture>",
    "<modid>:<particle texture>",
    "<modid>:<particle texture>"
  ]
}
```

The value `fallingleaves:falling_leaf_1` will map to the texture `assets/fallingleaves/textures/particle/falling_leaf_1.png`.

Creating the type `assets/byg/fallingleaves/leaftypes/pink_cherry_leaves.json` will be automatically used for the block `byg:pink_cherry_leaves` without the need to create a settings file.


## Licence

This mod is licenced under [LGPLv3](https://raw.githubusercontent.com/TeamLapen/Werewolves/master/LICENSE) ***except***
the following parts:

#### Assets

Any texture or particle type
configs [dir](https://github.com/Cheaterpaul/fallingleaves/tree/main/src/main/resources/assets/fallingleaves) is
licensed under MIT by RandomMCSomethin

#### Code

Any Java class with a licence header or referenced source is licensed under the respected license
