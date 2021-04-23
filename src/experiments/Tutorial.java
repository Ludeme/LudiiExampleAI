package experiments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.Game;
import game.types.state.GameType;
import main.FileHandling;
import main.collections.FastArrayList;
import mcts.ExampleUCT;
import other.AI;
import other.GameLoader;
import other.context.Context;
import other.model.Model;
import other.move.Move;
import other.state.container.ContainerState;
import other.trial.Trial;
import random.RandomAI;

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
		final String[] games = FileHandling.listGames();
		System.out.println("Built-in games = " + Arrays.toString(games));
		
		// one of the games is "Amazons.lud". Let's load it
		Game game = GameLoader.loadGameFromName("Amazons.lud");
		
		// the game's "stateFlags" contain properties of the game that may be
		// important for some AI algorithms to know about
		final long gameFlags = game.gameFlags();
		
		// for example, we may like to know whether our game has stochastic elements
		final boolean isStochastic = ((gameFlags & GameType.Stochastic) != 0L);
		if (isStochastic)
			System.out.println(game.name() + " is stochastic.");
		else
			System.out.println(game.name() + " is not stochastic.");
		
		// figure out how many players are expected to play this game
		final int numPlayers = game.players().count();
		System.out.println(game.name() + " is a " + numPlayers + "-player game.");
		
		// to be able to play the game, we need to instantiate "Trial" and "Context" objects
		Trial trial = new Trial(game);
		Context context = new Context(game, trial);
		
		// let's start a game (setting up the initial game state)
		game.start(context);
		
		// loop through all the "container states" of the game state
		// (in many games there is just a board, but there may also be hands, etc.)
		for (final ContainerState containerState : context.state().containerStates())
		{
			// for every container state we find (often just 1), we'll print a few things:
			
			// print the collection of locations that are empty
			System.out.println("Empty locations = " + containerState.emptyChunkSetCell());
			
			// for every location that is owned by a player, print the owner
			System.out.println("Who = " + containerState.cloneWhoCell().toChunkString());
			
			// for every location that is occupied by a piece, print what piece occupies it
			System.out.println("What = " + containerState.cloneWhatCell().toChunkString());
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
		for (final ContainerState containerState : context.state().containerStates())
		{
			System.out.println("Empty locations = " + containerState.emptyChunkSetCell());
			System.out.println("Who = " + containerState.cloneWhoCell().toChunkString());
			System.out.println("What = " + containerState.cloneWhatCell().toChunkString());
		}
		
		// request legal moves again and play one of them again
		// in Amazons, it should this time be a shoot for the first player
		final Move secondMove = game.moves(context).moves().get(0);
		System.out.println("Applying move: " + secondMove);
		game.apply(context, secondMove);
		
		// let's have a final look at how our state looks after this second move
		for (final ContainerState containerState : context.state().containerStates())
		{
			System.out.println("Empty locations = " + containerState.emptyChunkSetCell());
			System.out.println("Who = " + containerState.cloneWhoCell().toChunkString());
			System.out.println("What = " + containerState.cloneWhatCell().toChunkString());
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
				// for the other half of the agents, we'll use our example UCT agent
				agents.add(new ExampleUCT());
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
			for (int p = 1; p < agents.size(); ++p)
			{
				agents.get(p).initAI(game, p);
			}
			
			// keep going until the game is over
			while (!context.trial().over())
			{
				// figure out which player is to move
				final int mover = context.state().mover();
								
				// retrieve mover from list of agents
				final AI agent = agents.get(mover);
				
				// ask agent to select a move
				// we'll give them a search time limit of 0.2 seconds per decision
				// IMPORTANT: pass a copy of the context, not the context object directly
				final Move move = agent.selectAction
						(
							game, 
							new Context(context),
							0.2,
							-1,
							-1
						);
								
				// apply the chosen move
				game.apply(context, move);
			}
			
			// let's see who won
			System.out.println(context.trial().status());
		}
		
		// The above implementation explicitly encodes the control flow of an alternating-move game,
		// and would not work correctly with a simultaneous-move game.
		//
		// Games can also be run using the following approach, which will work for
		// alternating-move AND simultaneous-move games.
		// We'll demonstrate this with Hex (an alternating-move game), 
		// and Rock-Paper-Scissors (a simultaneous-move game)
		for 
		(
			final String gameName : new String[]{
				"board/space/connection/Hex.lud", 
				"mathematical/hand/Rock-Paper-Scissors.lud"}
		)
		{
			game = GameLoader.loadGameFromName(gameName);
			
			trial = new Trial(game);
			context = new Context(game, trial);
			game.start(context);
			
			System.out.println("We're playing " + game.name() + "!");
			
			// Create and init two UCT agents
			final List<AI> ais = new ArrayList<AI>(3);
			ais.add(null);
			ais.add(new ExampleUCT());
			ais.add(new ExampleUCT());
			ais.get(1).initAI(game, 1);
			ais.get(2).initAI(game, 2);
			
			// This model object is the thing that will handle control flow for us
			final Model model = context.model();
			
			// Keep going until the game is over...
			while (!trial.over())
			{
				// The following call tells the model it should start a new "step"
				// using the given list of AIs, with 0.2 seconds of thinking time
				// per decision, per agent.
				//
				// The behaviour of this call depends on whether the model is for
				// alternating-move games or simultaneous-move games, but the basic
				// premise is that whatever AI(s) is (are) supposed to make a move
				// will start thinking about its move, and apply it to the game
				// state once the decision has been made.
				//
				// In an alternating-move game, this means a single agent is
				// queried to return a move, and that move is applied. In a
				// simultaneous-move game, it means that ALL active players are
				// queried to return moves, and they are all applied together.
				model.startNewStep(context, ais, 0.2);
				
				// In the following loop, we wait around until the model tells
				// us that it's ready with the processing of the step we asked
				// it to start.
				//
				// Generally this wouldn't be needed, because the startNewStep()
				// call should block and only return once any relevant AIs have
				// selected and applied their move to the game state. However,
				// the behaviour of the startNewStep() can be modified in various
				// ways by adding additional arguments. One possible modification
				// is to have it return immediately (which implies that any
				// AI-thinking must be performed in a separate Thread). A loop
				// like the one below may then, for example, be used for 
				// visualisation of the AI's thinking process, or for processing
				// human-player input in case any of the AIs were set to null.
				while (!model.isReady())
				{
					try
					{
						Thread.sleep(100);
					}
					catch (final InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				
				// There is no need to explicitly apply any moves here anymore;
				// if model.isReady() returns true, this means we're ready for
				// the next time step!
			}
			
			// let's see what the result is
			System.out.println(context.trial().status());
		}
	}
	
}
