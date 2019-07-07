package experiments;

import java.util.Arrays;

import game.Game;
import player.GameLoader;
import player.experiments.Match;
import random.RandomAI;
import search.mcts.MCTS;
import util.AI;
import util.AI.SearchLimits;

/**
 * Example of an experiment that uses Ludii's built-in Match class to
 * run games between AIs.
 * 
 * See RunCustomMatch for an example that does not use Ludii's built-in Match
 * implementation.
 * 
 * @author Dennis Soemers
 */
public class RunLudiiMatch
{
	
	//-------------------------------------------------------------------------
	
	/** Name of game we wish to play */
	static final String GAME_NAME = "Amazons.lud";
	
	/** Whether to create a small GUI that can be used to manually interrupt the experiment */
	static final boolean USE_GUI = false;
	
	/** Wall-time limit in minutes (-1 for no limit) */
	static final int MAX_WALL_TIME = -1;
	
	/** List of agents for playing the match */
	static final AI[] AGENTS = new AI[]{
			new RandomAI(),
			MCTS.createUCT()
	};
	
	//-------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	private RunLudiiMatch()
	{
		// do not instantiate
	}
	
	//-------------------------------------------------------------------------
	
	public static void main(final String[] args)
	{
		// load and create game
		final Game game = GameLoader.loadGameFromNameForTesting(GAME_NAME);
		game.create(0);
		
		// set up our match
		final Match match = 
				new Match(USE_GUI, MAX_WALL_TIME)
				.setGameName(GAME_NAME)
				.setAgents(Arrays.asList(AGENTS))
				.setNumGames(10)
				.setLimitType(SearchLimits.Seconds)
				.setLimitValue(1.0)
				.setRotateAgents(true);
		
		// start playing
		match.startMatch();
	}
	
	//-------------------------------------------------------------------------

}
