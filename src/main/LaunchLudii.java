package main;

import app.StartDesktopApp;
import manager.ai.AIRegistry;
import mcts.ExampleDUCT;
import mcts.ExampleUCT;
import random.RandomAI;

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
		AIRegistry.registerAI("Example Random AI", () -> {return new RandomAI();}, (game) -> {return true;});
		AIRegistry.registerAI("Example UCT", () -> {return new ExampleUCT();}, (game) -> {return new ExampleUCT().supportsGame(game);});
		AIRegistry.registerAI("Example DUCT", () -> {return new ExampleDUCT();}, (game) -> {return new ExampleDUCT().supportsGame(game);});
		
		// Run Ludii
		StartDesktopApp.main(new String[0]);
	}

}
