# Ludii Example AI

This project contains instructions and examples for the implementation of 
third-party AI algorithms / agents for the Ludii general game system. After
developing your own agent, you can load it locally through the GUI of the
Ludii application, and watch it play any game supported by Ludii! 

Agents implemented according to the instructions on this page will also be
suitable for submission to any future agent-based playing competitions
organised using the Ludii system.

## Requirements

As of this time, only the development of agents in Java is supported. The
**minimum version of Java** required is **Java 8**. Development of agents
in other programming languages may be supported in the future, but there
is no concrete timeline for when this will be supported.

## Getting Started

### AI Development

1. Download [Ludii.jar](http://www.ludeme.eu/) (TO DO: update URL). This is the
JAR file that can also be used to launch the Ludii application.

2. Create a new Java project using your favourite IDE. You can also create a
fork of this [github repository](https://github.com/DennisSoemers/LudiiExampleAI)
to get started with some example implementations of basic agents.

3. Make sure to add the Ludii.jar file downloaded in step 1 as a library for
your project.

4. Any agent that you'd like to implement will have to extend the abstract class
`util.AI`. This contains two methods that may be overridden:
	1. `public Move selectAction(final Game game, 
	final Context context, final SearchLimits limitType, final double limitValue)`.
	It takes a reference to the `game` being played, and the current 
	`context` (which contains, among other data, the current game state) as
	arguments, and should return the next `Move` to be played by the agent. 
	The final two arguments specify the type of any limit that the agent should
	obey (e.g. max search depth, or max iteration count, or max number of seconds),
	and the value of the limit (depth level, or number of iterations, or number of seconds).
	2. `public void initAI(final Game game)`. This method can be used
	to perform any initialisation of the AI when the game to be played has been
	determined, but before the initial game state has been generated. 
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

## Citing Information

When using Ludii in any publications (for example for running experiments, or
for visual inspections of your agent's behaviour during development, etc.), 
please cite [our paper on the Ludii system](https://arxiv.org/abs/1905.05013).
This can be done using the following BibTeX entry:

	@misc{Piette2019Ludii,
		Author = {{\'E}ric Piette and Dennis J. N. J. Soemers and Matthew Stephenson and Chiara F. Sironi and Mark H. M. Winands and Cameron Browne},
		Title = {Ludii - The Ludemic General Game System},
		Year = {2019},
		Eprint = {arXiv:1905.05013},
		url = {https://arxiv.org/abs/1905.05013}
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
development for Ludii, is [creating new Issues on the github repository](https://github.com/DennisSoemers/LudiiExampleAI/issues).
Alternatively, the following email address may be used: `dennis(dot)soemers(at)maastrichtuniversity(dot)nl`.

## Changelog

- 11 August, 2019: Initial release.

## Acknowledgements

This repository is part of the European Research Council-funded Digital Ludeme Project (ERC Consolidator Grant \#771292) run by Cameron Browne at Maastricht University's Department of Data Science and Knowledge Engineering. 

<a href="https://erc.europa.eu/"><img src="https://erc.europa.eu/sites/default/files/LOGO_ERC-FLAG_EU_.jpg" title="Funded by the European Research Council" alt="European Research Council Logo" align="center" style="max-height:256px"></a>
