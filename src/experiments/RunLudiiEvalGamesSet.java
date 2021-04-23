package experiments;

import java.util.Arrays;

import other.AI;
import random.RandomAI;
import search.mcts.MCTS;
import supplementary.experiments.EvalGamesSet;

/**
 * Example of an experiment that uses Ludii's built-in EvalGamesSet class to
 * run games between AIs.
 * 
 * See RunCustomMatch for an example that does not use Ludii's built-in 
 * EvalGamesSet implementation.
 * 
 * @author Dennis Soemers
 */
public class RunLudiiEvalGamesSet
{
	
	//-------------------------------------------------------------------------
	
	/** Name of game we wish to play */
	static final String GAME_NAME = "Amazons.lud";
	
	/** Whether to create a small GUI that can be used to manually interrupt the experiment */
	static final boolean USE_GUI = false;
	
	/** Wall-time limit in minutes (-1 for no limit) */
	static final int MAX_WALL_TIME = -1;
	
	/** List of agents for playing the match */
	static final AI[] AGENTS = 
			new AI[]
			{
				new RandomAI(),
				MCTS.createUCT()
			};
	
	//-------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	private RunLudiiEvalGamesSet()
	{
		// do not instantiate
	}
	
	//-------------------------------------------------------------------------
	
	public static void main(final String[] args)
	{
		// set up our match
		final EvalGamesSet evalGamesSet = 
				new EvalGamesSet(USE_GUI, MAX_WALL_TIME)
				.setGameName(GAME_NAME)
				.setAgents(Arrays.asList(AGENTS))
				.setNumGames(10)
				.setMaxSeconds(1.0)
				.setRotateAgents(true);
		
		// start playing
		evalGamesSet.startGames();
	}
	
	//-------------------------------------------------------------------------

}
