<img align="right" src="./resources/ludii-logo-64x64.png">

# Ludii Example AI

[![license](https://img.shields.io/github/license/Ludeme/LudiiExampleAI)](LICENSE)
[![release-version](https://img.shields.io/github/release-pre/Ludeme/LudiiExampleAI)](https://github.com/Ludeme/LudiiExampleAI/releases)
![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)
![code-size](https://img.shields.io/github/languages/code-size/Ludeme/LudiiExampleAI)
![top-language](https://img.shields.io/github/languages/top/Ludeme/LudiiExampleAI)
[![twitter](https://img.shields.io/twitter/follow/ludiigames?style=social)](https://twitter.com/intent/follow?screen_name=ludiigames)

This project contains instructions and examples for the implementation of 
third-party AI algorithms / agents for the Ludii general game system. After
developing your own agent, you can load it locally through the GUI of the
Ludii application, and watch it play any game supported by Ludii! 

Agents implemented according to the instructions on this page will also be
suitable for submission to any future agent-based playing competitions
organised using the Ludii system.

Most of the documentation found in this repository may also be found in
the Ludii User Guide, accessible from the 
[Ludii webpage](http://ludii.games/index.php).

We also recommend taking a look at the [Ludii Tutorials](https://ludiitutorials.readthedocs.io)
for tutorials on AI development as well as any other aspects of Ludii.

## Table of Contents
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Example Agents](#example-agents)
- [Citing Information](#citing-information)
- [Background Info](#background-info)
- [Contact Info](#contact-info)
- [Changelog](#changelog)
- [Acknowledgements](#acknowledgements)

## Requirements

As of this time, only the development of agents in Java is supported. The
**minimum version of Java** required is **Java 8**. Development of agents
in other programming languages may be supported in the future, but there
is no concrete timeline for when this will be supported.

## Getting Started

### AI Development

1. Download [Ludii's JAR file](http://ludii.games/download.php). This is the
JAR file that can also be used to launch the Ludii application.
2. Create a new Java project using your favourite IDE. You can also create a
fork of this [github repository](https://github.com/Ludeme/LudiiExampleAI)
to get started with some example implementations of basic agents.
3. Make sure to add the Ludii's JAR file downloaded in step 1 as a library for
your project.
4. Any agent that you'd like to implement will have to extend the abstract class
`util.AI`. This contains three methods that may be overridden:
	1. `public Move selectAction(final Game game, final Context context, 
	final double maxSeconds, final int maxIterations, final int maxDepth)`.
	It takes a reference to the `game` being played, and the current 
	`context` (which contains, among other data, the current game state) as
	arguments, and should return the next `Move` to be played by the agent. 
	The final three arguments can be used to restrict the agent's processing
	(its search time, or its maximum iteration count or search depth for example).
	2. `public void initAI(final Game game, final int playerID)`. This method can be used
	to perform any initialisation of the AI when the game to be played has been
	determined, but before the initial game state has been generated. 
	3. `public boolean supportsGame(final Game game)`. This method has a default implementation
	to return `true` for any game, but may be overridden to return `false` for games
	that your agent cannot play. For example, it may be unable to play simultaneous-move
	games, and then be implemented to always return `false` for those. Ludii will then
	know not to try to make your AI play such a game.
	4. `public void closeAI()`. This method can be used to perform any cleanup of resources
	when a game has been finished.
For a simple example class that extends this abstract class, see the 
[Example Random AI](src/random/RandomAI.java).
5. Export your project to a new JAR file.

### Loading AI in the Ludii Application

In the Ludii application, the dialog in which agent types can be assigned to
players can be opened by clicking one of the player names in the GUI, or by
selecting `Ludii > Preferences...` in the menubar. In addition to a
number of built-in agents, the drop-down menus contain a `From JAR` option.

To load your own custom AI implementation into Ludii, select the `From JAR`
option, and then select the JAR file containing your custom AI's .class file.
A dialog will appear with all the different classes in the selected JAR file
that extend Ludii's `util.AI` abstract class, and you will be required to
choose one of them. Note that this means that it is fine if you have a single
JAR file containing many different, custom AI implementations; they can all be
loaded.

Ludii will attempt to instantiate an agent of the selected class by calling
a zero-arguments constructor of that class. **This will only work correctly
if your class does indeed provide a zero-args constructor, and it will have
to be public as well!.** After loading it as instructed here, the custom AI
can be used to play any games in the Ludii application, just like any other
built-in AI.

**Note:** while the Ludii application is running, it will only load all the
.class files of any selected JAR file once. If you have already selected a
JAR file once, and then re-build your custom JAR file without changing its
filepath, you will have to close and re-open the Ludii application if you
wish to try loading agents from the modified JAR file.

## Example Agents

- [Random AI](src/random/RandomAI.java).
- [Example UCT](src/mcts/ExampleUCT.java) (only supports deterministic, alternating-move games).
- [Example Decoupled UCT](src/mcts/ExampleDUCT.java) (only supporst deterministic, simultaneous-move games).

## Citing Information

When using Ludii in any publications (for example for running experiments, or
for visual inspections of your agent's behaviour during development, etc.), 
please cite [our paper on the Ludii system](https://arxiv.org/abs/1905.05013).
This can be done using the following BibTeX entry:

	@inproceedings{Piette2020Ludii,
            author      = "{\'E}. Piette and D. J. N. J. Soemers and M. Stephenson and C. F. Sironi and M. H. M. Winands and C. Browne",
            booktitle   = "Proceedings of the 24th European Conference on Artificial Intelligence (ECAI 2020)",
            title       = "Ludii -- The Ludemic General Game System",
            pages       = "411-418",
            year        = "2020",
            editor      = "G. De Giacomo and A. Catala and B. Dilkina and M. Milano and S. Barro and A. Bugarín and J. Lang",
            series      = "Frontiers in Artificial Intelligence and Applications",
            volume      = "325",
			publisher	= "IOS Press"
        }

## Background Info

This repository contains information and examples for the development of third-
party AI implementations which can be loaded into the Ludii General Game System.
Note that this repository does not contain the full Ludii system, or its
built-in AI options.

This work, as well as the full Ludii system itself, are developed for the
Digital Ludeme Project. More info on the project and the system can be found on:

- http://www.ludeme.eu/
- http://ludii.games/

## Contact Info

The preferred method for getting help with troubleshooting, suggesting or
requesting additional functionality, or asking other questions about AI
development for Ludii, is [creating new Issues on the github repository](https://github.com/Ludeme/LudiiExampleAI/issues).
Alternatively, the following email address may be used: `ludii(dot)games(at)gmail(dot)com`.

## Changelog

- 16 August, 2020: Updated repository for compatibility with new version 1.0.3 of Ludii.
- 24 July, 2020: Updated repository for compatibility with new version 1.0.0 of Ludii.
- 4 July, 2020: Updated repository for compatibility with new version 0.9.4 of Ludii.
- 3 April, 2020: Updated repository for compatibility with new version 0.6.1 of Ludii.
- 13 December, 2019: Updated repository for compatibility with new version 0.5.0 of Ludii.
- 27 November, 2019: Updated repository for compatibility with new version 0.4.1 of Ludii.
- 6 September, 2019: Updated repository for compatibility with new version 0.3.0 of Ludii.
- 13 August, 2019: Initial release.

## Acknowledgements

This repository is part of the European Research Council-funded Digital Ludeme Project (ERC Consolidator Grant \#771292), being run by Cameron Browne at Maastricht University's Department of Data Science and Knowledge Engineering. 

<a href="https://erc.europa.eu/"><img src="./resources/LOGO_ERC-FLAG_EU_.jpg" title="Funded by the European Research Council" alt="European Research Council Logo" height="384"></a>
