package uct;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import game.Game;
import game.mode.model.Model;
import mcts.ExampleUCT;
import player.GameLoader;
import util.AI;
import util.Context;
import util.Trial;

/**
 * A unit test to ensure that two Example UCT agents playing Tic-Tac-Toe
 * ends in a draw.
 * 
 * @author Dennis Soemers
 */
public class TicTacToeUCT
{
	
	@Test
	public void test()
	{
		final Game game = GameLoader.loadGameFromName("board/space/line/Tic-Tac-Toe.lud");
		game.create(0);
		final int numPlayers = game.mode().numPlayers();
		
		final Trial trial = new Trial(game);
		final Context context = new Context(game, trial);
		
		game.start(context);
		
		final List<AI> agents = new ArrayList<AI>();
		agents.add(null);
		
		for (int p = 1; p <= numPlayers; ++p)
		{
			final ExampleUCT agent = new ExampleUCT();
			agents.add(agent);
			agent.initAI(game, p);
		}
		
		final Model model = context.model();
		while (!trial.over())
		{
			model.startNewStep(context, agents, 0.5);
		}
		
		assert(trial.status().winner() == 0);
	}

}
