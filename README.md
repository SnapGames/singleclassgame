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
|  |  |  |_ Game.java                     | 
|  |_ resources                           |
|     |_ res                               > Project resources
|        |_ audio                         |
|           |_ sounds                     |
|        |_ images                        |
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

### Sound Manager

To make sounds, the game is enriched with a new SoundController and some entity to manage sounds: SoundClip.

- `SoundControl` is the sound manager class, loading, playing and stopping sounds,
- `SoundClip` is a piece of sound to be played and managed by the `SoundControl` manager.

### Game State Manager

One of the most important system in the game, is not only the **Game Loop**, but also the system that can switch from one state of the game (the title screen) to another (the main play state) one without pain: `GameStateManager`. This particular class will manage multiple implementations of the interface named `GameState` to change smoothly the game behavior, corresponding to any game play you want to provide. An abstract class, the `AbstractGameState` will provide the basic and default behavior of each game state.

Some, the classes added are :

- `GameState` defining the basic interface to provide a new behavior to your game,
- `AbstractGameState` to provide a default behavior and some implementation to the GameState interface,
- `GameStateManager` which is the main State manager, to switch from state to another with only a simple method call.

#### GameState

This simple interface provide the main methods to be implemented into any game state:

- `initialize(Game)` to initialize state at Game start,
- `activate(Game)` when the state must be activated by the `GameStateManager` (`GSM`),
- `deactivate(Game)`when the state will be deactivated by the `GSM`,
- `input(Game)` when the manager `GSM` will delegate the input processing to this particular GameState implementation,
- `update(Game,float)` called by `GSM` to update the activated state,
- `render(Game,Graphics2D)` to let `GSM` delegate the rendering of this state.

Let's see JavaDoc for more information.


#### GameStateManager

The `GameStateManager` is the main class to manage and delegate processing to some specific behavior classes implementing the `GameState` interface.

- You must add your GameState implementation to the stack by calling the `add(GameState)` method.
- then you can activate your just added state by calling the `start(String name)`. this will automatically called the `deactivate(Game)` method of the previous state, and then call the `activate(Game)` method of your **name** state.


Those class methods are called from the main game loop (see Game#run() method) to manage input, update and render things into each of the game states.
 

## Resources

### Audio

The audio part of this new feature is coming from the [freesound.org](https://freesound.org "go and fine som e sound !"). The magical **boing** sound is coming from [here](https://freesound.org/people/Greenhourglass/sounds/159376/ "Boing Boing Boing !")
 
Fred D.