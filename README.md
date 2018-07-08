# Single Class Game

[![Continuous Travis integration status](https://travis-ci.org/SnapGames/singleclassgame.svg?branch=master)](https://travis-ci.org/SnapGames/singleclassgame "Open the Travis-CI build job")

## Presentation

A Single Class Game attempt to bring in a minimum scope all the commons and 
standards game stack.

This small project based on some knowledge found on the Internet (so, you know 
that Internet is the truth :) You will be happy to discover a simple way to 
develop and build a small java game.

## Project structure

The [game project](https://github.com/snapgames/singleclassgame) repository contains: 

```
singleclassgame
|_ .github                               \
|  |_ ISSUE_TEMPLATE                      |
|     |_ Bug_report.md                     > Some github specific files
|     |_ Feature_request.md               |
|                                        /
|_ src
|  |_ main                               \ 
|  |  |_ fr.snapgames.game.oneclassgame   |
|  |  |  |_ Game.java                      > Project resources
|  |_ resources                           |
|     |_ res                              |
|        |_ application.ico              /
|
|_ .travis.yml                           >  Continuous build config file
| 
|_ CODE_OF_CONDUCT.md                    \  Other specific 
|_ LICENSE                               /  github and license file
|
|_ README.md                             > this fantastic file
|_ pom.xml                               > where the magical wizard work.

```


## Game Structure

The main parent class is Game, from the Game.java file.  But all classes which 
used here are declared into the Game class.

```
  Game
  |_ COnfiguration
  |_ KeyInputListener
  |_ GameObject
  |_ GameKeyListener
  |_ PlayerKeyListener
  |_ ...
```

## Classes Overview

### Some basics

- `Configuration` class is used to store `key=value` as a configuration store purpose,
- `ResourceManager`provides a basic service to load images and reduce memory usage,
- `Vector2D`proposes a simplified 2D math vector implementation to manage physics computation,
- `Window` is what it means to be a Window encapsulation of JDK one,
- `VersionTracer` class proposes a dependencies and versions tracker about embedded libraries. 

### Game specific

- `GameObject`is a default object managed by the game to be updated and displayed,
- a `Camera` object to make window view follow another object,
- `KeyInputListener` is the main KeyListenr for the window displaying the game,
- `GameKeyListener` is a key command manager for the Game itself. this is where  the **exit** and **pause** request are processed,
- `PlayerKeyListener` is a specific key listener to animate and move the **player** `GameObject` instance.

### Physics

- `World` a class to manage a world context containing some physics parameters for the Physic computation performed at GameObject level (see `GameObect#updatePhysic(float)`), like wind, world gravity, and soon other parameters. 

### Collision detection and processing

- To manage Objects, I add a `BoundingBox` and a `BoundingBoxType` to encapsulate object with multiple box types (Rectangle, Circle, Ellipse, Capsules, etc...)
- A `QuadTree` implementation (from internet) provide the necessary structure to manage 2D space splitting to reduce Collision computation.
- `CollisionManager` is a class to detect and manage collision events between `GameObject`s. A `CollisionResponseProcessor` interface can be implemented to manage those events for some specific case; `ColliderResponse` is one of it.

> **NOTE**
> Must add some interactions between "player" and a map.

## Resources

### Audio

The audio part of this new feature is coming from the [freesound.org](https://freesound.org "go and fine som e sound !"). The magical **boing** is coming from [here](https://freesound.org/people/Greenhourglass/sounds/159376/ "Boing Boing Boing !")
 
Fred D.