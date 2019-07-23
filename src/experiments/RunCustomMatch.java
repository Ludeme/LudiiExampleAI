package experiments;

import game.Game;
import player.GameLoader;
import random.RandomAI;
import search.mcts.MCTS;
import util.AI;
import util.Context;
import util.Move;
import util.Trial;

/**
 * An example of a custom implementation of a match between different AIs,
 * i.e. not using the built-in Match functionality of Ludii.
 * 
 * By creating custom implementations of matches/experiments, we can more
 * easily add our own custom stats to track, and request mvoes from AIs
 * that do not implement Ludii's abstract AI class. The downside is that
 * more boilerplate code must be written.
 * 
 * See RunLudiiMatch for an example that uses Ludii's built-in Match 
 * implementation.
 * 
 * Note that this example does not provide all of the functionality included
 * in the built-in Match implementation. For instance, this example will always
 * use the same agents for the same player number (e.g. Random AI always player
 * 1, UCT always player 2), whereas Ludii's built-in Match implementation can
 * rotate through assignments of agents to player numbers.
 * 
 * @author Dennis Soemers
 */
public class RunCustomMatch
{
	
	//-------------------------------------------------------------------------
	
	/** Name of game we wish to play */
	static final String GAME_NAME = "Amazons.lud";
	
	/** Number of games to play */
	static final int NUM_GAMES = 10;

	/** List of agents for playing the match */
	static final AI[] AGENTS = new AI[]{
			null,
			new RandomAI(),
			MCTS.createUCT()
	};

	//-------------------------------------------------------------------------

	/**
	 * Constructor
	 */
	private RunCustomMatch()
	{
		// do not instantiate
	}

	//-------------------------------------------------------------------------

	public static void main(final String[] args)
	{
		// load and create game
		final Game game = GameLoader.loadGameFromName(GAME_NAME);
		game.create(0);

		final Trial trial = new Trial(game);
		final Context context = new Context(game, trial, null);
		
		for (int gameCounter = 0; gameCounter < NUM_GAMES; ++gameCounter)
		{
			// play a game
			game.start(context);
			
			// in this example, we're still using agents that extend Ludii's
			// abstract AI class, and therefore we call initAI() and 
			// selectAction() on them
			//
			// note that it is also possible to use different kinds of
			// agents which do not extend this class, and call whatever methods
			// you like on them
			for (int p = 1; p < AGENTS.length; ++p)
			{
				AGENTS[p].initAI(game, p);
			}
			
			while (!context.trial().over())
			{
				final int mover = context.trial().state().mover();
				final AI ai = AGENTS[mover];
				final Move move = ai.selectAction
						(
							game, 
							new Context(context), 
							1.0,
							-1,
							-1
						);

				// apply chosen action
				game.apply(context, move);
			}
			
			System.out.println("Outcome = " + context.trial().status());
		}
	}

	//-------------------------------------------------------------------------

}
