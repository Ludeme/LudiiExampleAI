package main;

import app.StartDesktopApp;
import mcts.ExampleDUCT;
import mcts.ExampleUCT;
import random.RandomAI;
import utils.AIRegistry;

/**
 * The main method of this launches the Ludii application with its GUI, and registers
 * the example AIs from this project such that they are available inside the GUI.
 *
 * @author Dennis Soemers
 */
public class LaunchLudii
{
	
	/**
	 * The main method
	 * @param args
	 */
	public static void main(final String[] args)
	{
		// Register our example AIs
		if (!AIRegistry.registerAI("Example Random AI", () -> {return new RandomAI();}, (game) -> {return true;}))
			System.err.println("WARNING! Failed to register AI because one with that name already existed!");
		
		if (!AIRegistry.registerAI("Example UCT", () -> {return new ExampleUCT();}, (game) -> {return new ExampleUCT().supportsGame(game);}))
			System.err.println("WARNING! Failed to register AI because one with that name already existed!");
		
		if (!AIRegistry.registerAI("Example DUCT", () -> {return new ExampleDUCT();}, (game) -> {return new ExampleDUCT().supportsGame(game);}))
			System.err.println("WARNING! Failed to register AI because one with that name already existed!");
		
		// Run Ludii
		StartDesktopApp.main(new String[0]);
	}

}
