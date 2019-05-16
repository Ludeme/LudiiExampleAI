package experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.Game;
import main.FastArrayList;
import player.GameLoader;
import random.RandomAI;
import search.mcts.MCTS;
import util.AI;
import util.AI.SearchLimits;
import util.Context;
import util.Move;
import util.Trial;
import util.state.StateType;
import util.state.containerState.ContainerState;

/**
 * A simple tutorial that demonstrates a variety of useful methods provided
 * by the Ludii general game system.
 * 
 * @author Dennis Soemers
 */
public class Tutorial
{

	public static void main(final String[] args)
	{
		// first, let's request and print the list of all built-in
		// game that can be directly loaded
		final String[] games = GameLoader.listGames();
		System.out.println("Built-in games = " + Arrays.toString(games));
		
		// one of the games is "Amazons.lud". Let's load it
		final Game game = GameLoader.loadGameFromName("Amazons.lud");
		game.create();
		
		// the game's "stateFlags" contain properties of the game that may be
		// important for some AI algorithms to know about
		final int stateFlags = game.stateFlags();
		
		// for example, we may like to know whether our game has stochastic elements
		final boolean isStochastic = ((stateFlags & StateType.Stochastic) != 0);
		if (isStochastic)
			System.out.println(game.name() + " is stochastic.");
		else
			System.out.println(game.name() + " is not stochastic.");
		
		// figure out how many players are expected to play this game
		final int numPlayers = game.mode().numPlayers();
		System.out.println(game.name() + " is a " + numPlayers + "-player game.");
		
		// to be able to play the game, we need to instantiate "Trial" and "Context" objects
		final Trial trial = new Trial(game);
		final Context context = new Context(game, trial);
		
		// let's start a game (setting up the initial game state)
		game.start(context);
		
		// loop through all the "container states" of the game state
		// (in many games there is just a board, but there may also be hands, etc.)
		for (final ContainerState containerState : trial.state().containerStates())
		{
			// for every container state we find (often just 1), we'll print a few things:
			
			// print the collection of locations that are empty
			System.out.println("Empty locations = " + containerState.empty().bitSet());
			
			// for every location that is owned by a player, print the owner
			System.out.println("Who = " + containerState.whoChunkSet().toChunkString());
			
			// for every location that is occupied by a piece, print what piece occupies it
			System.out.println("What = " + containerState.whatChunkSet().toChunkString());
		}
		
		// print the full list of all legal moves
		final FastArrayList<Move> legalMoves = game.moves(context).moves();
		System.out.println("Legal Moves = " + legalMoves);
		
		// apply the first move in the list of legal moves
		// in the game of Amazons, this would be the movement of a Queen for Player 1
		final Move firstMove = legalMoves.get(0);
		System.out.println("Applying move: " + firstMove);
		game.apply(context, firstMove);
		
		// let's print our empty/who/what again, see how they have changed
		for (final ContainerState containerState : trial.state().containerStates())
		{
			System.out.println("Empty locations = " + containerState.empty().bitSet());
			System.out.println("Who = " + containerState.whoChunkSet().toChunkString());
			System.out.println("What = " + containerState.whatChunkSet().toChunkString());
		}
		
		// request legal moves again and play one of them again
		// in Amazons, it should this time be a shoot for the first player
		final Move secondMove = game.moves(context).moves().get(0);
		System.out.println("Applying move: " + secondMove);
		game.apply(context, secondMove);
		
		// let's have a final look at how our state looks after this second move
		for (final ContainerState containerState : trial.state().containerStates())
		{
			System.out.println("Empty locations = " + containerState.empty().bitSet());
			System.out.println("Who = " + containerState.whoChunkSet().toChunkString());
			System.out.println("What = " + containerState.whatChunkSet().toChunkString());
		}
		
		//---------------------------------------------------------------------
		
		// now we're going to have a look at playing a few full games, using AI
		
		// first, let's instantiate some agents
		final List<AI> agents = new ArrayList<AI>();
		agents.add(null);	// insert null at index 0, because player indices start at 1
		
		for (int p = 1; p <= numPlayers; ++p)
		{
			if (p % 2 != 0)
			{
				// for half the agents, we'll use the Example Random AI from this repo
				agents.add(new RandomAI());
			}
			else
			{
				// for the other half of the agents, we'll use a standard UCT agent
				// which is built-in in Ludii
				agents.add(MCTS.createUCT());
			}
		}
		
		// number of games we'd like to play
		final int numGames = 10;
		
		// NOTE: in our following loop through number of games, the different
		// agents are always assigned the same player number. For example,
		// Player 1 will always be random, Player 2 always UCT, Player 3
		// always random, etc.
		//
		// For a fair comparison of playing strength, agent assignments to
		// player numbers should rotate through all possible permutations,
		// to correct for possible first-mover-advantages or disadvantages, etc.
		for (int i = 0; i < numGames; ++i)
		{
			// (re)start our game
			game.start(context);
			
			// (re)initialise our agents
			for (final AI agent : agents)
			{
				if (agent != null)
					agent.initAI(game);
			}
			
			// keep going until the game is over
			while (!context.trial().over())
			{
				for (final ContainerState containerState : trial.state().containerStates())
				{
					System.out.println("Empty locations = " + containerState.empty().bitSet());
					System.out.println("Who = " + containerState.whoChunkSet().toChunkString());
					System.out.println("What = " + containerState.whatChunkSet().toChunkString());
				}
				
				game.moves(context);
				
				// figure out which player is to move
				final int mover = context.state().mover();
				System.out.println("mover = " + mover);
				System.out.println();
				
				// retrieve mover from list of agents
				final AI agent = agents.get(mover);
				
				// ask agent to select a move
				// we'll give them a search time limit of 1.0 seconds
				// IMPORTANT: pass a copy of the context, not the context object directly
				final Move move = agent.selectAction
						(
							game, 
							new Context(context),
							SearchLimits.Seconds, 
							1.0
						);
				
				// apply the chosen move
				game.apply(context, move);
			}
			
			// let's see who won
			System.err.println("Winner = " + context.trial().status().winner());
		}
	}
	
}
