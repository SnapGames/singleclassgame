# Single Class Game

[![Continuous Travis integration status](https://travis-ci.org/SnapGames/singleclassgame.svg?branch=master)](https://travis-ci.org/SnapGames/singleclassgame "Open the Travis-CI build job") [![VersionEye dependencies verification](https://www.versioneye.com/user/projects/5af2f8ed0fb24f0e57e3d81f/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/5af2f8ed0fb24f0e57e3d81f?child=summary "open the version eye status report")

## Presentation

A Single Class Game attempt to bring in a minimalistic scope all the commons and
standards game stack.

This small project based on some knowledge found on the internet (so, you know
that internet is the truth :) You will be happy to dicosver a simple way to
develop and build a small java game.

## Project structure

The game project git repository contains:

```txt
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

```txt
  Game
  |_ KeyInputListener
  |_ GameObject
  |_ GameKeyListener
  |_ PlayerKeyListener
```

## Classes

- `KeyInputListener` is the main KeyListenr for the window displaying the game,
- `GameObject`is a default object managed by the game to be updated and displayed,
- `GameKeyListener` is a key command manager for the Game itself. this is where  the **exit** and **pause** request are processed,
- `PlayerKeyListener` is a specific key listener to animate and move the **player** `GameObject` instance.

## Screenshot

<figure>
  <img src="src/main/resources/res/docs/images/screenshot-debug.png" title="this is a screenshot" alt="screenshot of debug mode"/>
  <figcaption>figure 1 - this is screenshot for debug mode</figcaption>
</figure>

<figure>
  <img src="src/main/resources/res/docs/images/screenshot-debug.png" title="this is a screenshot" alt="screenshot of display mode"/>
  <figcaption>figure 2 - this is screenshot for display mode</figcaption>
</figure>


To be continued ...

Fred D.