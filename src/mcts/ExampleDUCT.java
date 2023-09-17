package mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.collections.FastArrayList;
import other.AI;
import other.RankUtils;
import other.action.Action;
import other.context.Context;
import other.move.Move;
import utils.AIUtils;

/**
 * A simple example implementation of Decoupled UCT, for simultaneous-move
 * games. Note that this example is primarily intended to show how to build
 * a search tree for simultaneous-move games in Ludii. This implementation
 * is by no means intended to be an optimal (in terms of optimisations /
 * computational efficiency) implementation of the algorithm.
 * 
 * Only supports deterministic, simultaneous-move games.
 * 
 * @author Dennis Soemers
 */
public class ExampleDUCT extends AI
{
	
	//-------------------------------------------------------------------------
	
	/** Our player index */
	protected int player = -1;
	
	//-------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public ExampleDUCT()
	{
		this.friendlyName = "Example Decoupled UCT";
	}
	
	//-------------------------------------------------------------------------

	@Override
	public Move selectAction
	(
		final Game game,
		final Context context, 
		final double maxSeconds, 
		final int maxIterations, 
		final int maxDepth
	)
	{
		// Start out by creating a new root node (no tree reuse in this example)
		final Node root = new Node(null, context);
		
		// We'll respect any limitations on max seconds and max iterations (don't care about max depth)
		final long stopTime = (maxSeconds > 0.0) ? System.currentTimeMillis() + (long) (maxSeconds * 1000L) : Long.MAX_VALUE;
		final int maxIts = (maxIterations >= 0) ? maxIterations : Integer.MAX_VALUE;
				
		int numIterations = 0;
		
		// Our main loop through MCTS iterations
		while 
		(
			numIterations < maxIts && 					// Respect iteration limit
			System.currentTimeMillis() < stopTime && 	// Respect time limit
			!wantsInterrupt								// Respect GUI user clicking the pause button
		)
		{
			// Start in root node
			Node current = root;
			
			// Traverse tree
			while (true)
			{
				if (current.context.trial().over())
				{
					// We've reached a terminal state
					break;
				}
				
				current = select(current);
				
				if (current.totalVisitCount == 0)
				{
					// We've expanded a new node, time for playout!
					break;
				}
			}
			
			Context contextEnd = current.context;
			
			if (!contextEnd.trial().over())
			{
				// Run a playout if we don't already have a terminal game state in node
				contextEnd = new Context(contextEnd);
				game.playout
				(
					contextEnd, 
					null, 
					-1.0, 
					null, 
					0, 
					-1, 
					ThreadLocalRandom.current()
				);
			}
			
			// This computes utilities for all players at the of the playout,
			// which will all be values in [-1.0, 1.0]
			final double[] utilities = RankUtils.utilities(contextEnd);
			
			// Backpropagate utilities through the tree
			while (current != null)
			{
				if (current.totalVisitCount > 0)
				{
					// This node was not newly expanded in this iteration
					for (int p = 1; p <= game.players().count(); ++p)
					{
						if (current.visitCounts[p].length > 0)
						{
							current.visitCounts[p][current.lastSelectedMovesPerPlayer[p]] += 1;
							current.scoreSums[p][current.lastSelectedMovesPerPlayer[p]] += utilities[p];
						}
					}
				}
				
				current.totalVisitCount += 1;
				current = current.parent;
			}
			
			// Increment iteration count
			++numIterations;
		}
		
		// Return the move we wish to play
		return finalMoveSelection(root);
	}
	
	/**
	 * Selects child of the given "current" node according to UCB1 equation.
	 * This method also implements the "Expansion" phase of MCTS, and creates
	 * a new node if the given current node has unexpanded moves.
	 * 
	 * @param current
	 * @return Selected node (if it has 0 visits, it will be a newly-expanded node).
	 */
	public static Node select(final Node current)
	{
		// Every player selects its move based on its own, decoupled statistics
		final List<Action> playerMoves = new ArrayList<Action>();
		final Game game = current.context.game();
		final int numPlayers = game.players().count();
		
		for (int p = 1; p <= numPlayers; ++p)
		{
			Move bestMove = null;
	        double bestValue = Double.NEGATIVE_INFINITY;
	        final double twoParentLog = 2.0 * Math.log(Math.max(1, current.totalVisitCount));
	        int numBestFound = 0;
	        
	        final int numChildren = current.legalMovesPerPlayer.get(p).size();
	
	        for (int i = 0; i < numChildren; ++i) 
	        {
	        	final Move move = current.legalMovesPerPlayer.get(p).get(i);
	        	final double exploit = (current.visitCounts[p][i] == 0) ? 1.0 : current.scoreSums[p][i] / current.visitCounts[p][i];
	        	final double explore = Math.sqrt(twoParentLog / Math.max(1, current.visitCounts[p][i]));
	        
	            final double ucb1Value = exploit + explore;
	            
	            if (ucb1Value > bestValue)
	            {
	                bestValue = ucb1Value;
	                bestMove = move;
	                numBestFound = 1;
	                current.lastSelectedMovesPerPlayer[p] = i;
	            }
	            else if 
	            (
	            	ucb1Value == bestValue && 
	            	ThreadLocalRandom.current().nextInt() % ++numBestFound == 0
	            )
	            {
	            	// this case implements random tie-breaking
	            	bestMove = move;
	            	current.lastSelectedMovesPerPlayer[p] = i;
	            }
	        }
	        
	        playerMoves.add(bestMove);
		}
		
		if (current.children.containsKey(playerMoves))
		{
			// We already have a node for this combination of moves
			return current.children.get(playerMoves);
		}
		else
		{
			// We need to create a new node for this combination of moves
			final Move combinedMove = new Move(playerMoves);
			combinedMove.setMover(numPlayers + 1);
			
			final Context context = new Context(current.context);
			context.game().apply(context, combinedMove);
			
			final Node newNode = new Node(current, context);
			current.children.put(playerMoves, newNode);
			return newNode;
		}
	}
	
	/**
	 * Selects the move we wish to play as the one with the
	 * highest expected value.
	 * 
	 * @param rootNode
	 * @return
	 */
	public Move finalMoveSelection(final Node rootNode)
	{
		Move bestMove = null;
        double bestAvgScore = Double.NEGATIVE_INFINITY;
        int numBestFound = 0;
        
        final int numChildren = rootNode.legalMovesPerPlayer.get(player).size();

        for (int i = 0; i < numChildren; ++i) 
        {
        	final Move move = rootNode.legalMovesPerPlayer.get(player).get(i);
        	final double sumScores = rootNode.scoreSums[player][i];
        	final int visitCount = rootNode.visitCounts[player][i];
        	final double avgScore = (visitCount == 0) ? -1.0 : sumScores / visitCount;
            
            if (avgScore > bestAvgScore)
            {
                bestAvgScore = avgScore;
                bestMove = move;
                numBestFound = 1;
            }
            else if 
            (
            	avgScore == bestAvgScore && 
            	ThreadLocalRandom.current().nextInt() % ++numBestFound == 0
            )
            {
            	// this case implements random tie-breaking
            	bestMove = move;
            }
        }
        
        return bestMove;
	}
	
	@Override
	public void initAI(final Game game, final int playerID)
	{
		this.player = playerID;
	}
	
	@Override
	public boolean supportsGame(final Game game)
	{
		// Don't allow stochastic games
		if (game.isStochasticGame())
			return false;
		
		// Don't allow games which are NOT simultaneous-move games
		if (game.isAlternatingMoveGame())
			return false;
		
		return true;
	}
	
	//-------------------------------------------------------------------------
	
	/**
	 * Inner class for nodes used by example Decoupled UCT
	 * 
	 * @author Dennis Soemers
	 */
	private static class Node
	{
		/** Our parent node */
		private final Node parent;
		
		/** This objects contains the game state for this node (this is why we don't support stochastic games) */
		private final Context context;
		
		/** Total visit count going through this node */
		private int totalVisitCount = 0;
		
		/** For every player, for every child move, a visit count */
		private final int[][] visitCounts;
		
		/** For every player, for every child move, a sum of backpropagated scores */
		private final double[][] scoreSums;
		
		/** Mapping from lists of actions (one per active player) to child nodes */
		private final Map<List<Action>, Node> children = new HashMap<List<Action>, Node>();
		
		/** 
		 * For every player, the index of the legal move we selected for 
		 * that player in this node in the last (current) MCTS iteration.
		 */
		private final int[] lastSelectedMovesPerPlayer;
		
		/** For every player index, a list of legal moves in this node */
		private final List<FastArrayList<Move>> legalMovesPerPlayer;
		
		/**
		 * Constructor
		 * 
		 * @param parent
		 * @param context
		 */
		public Node(final Node parent, final Context context)
		{
			this.parent = parent;
			this.context = context;
			final Game game = context.game();
			final int numPlayers = game.players().count();
			
			final FastArrayList<Move> allLegalMoves = game.moves(context).moves();
			
			// For every active player in this state, compute their legal moves
			legalMovesPerPlayer = new ArrayList<FastArrayList<Move>>(numPlayers + 1);
			legalMovesPerPlayer.add(null);
			for (int p = 1; p <= numPlayers; ++p)
			{
				legalMovesPerPlayer.add(AIUtils.extractMovesForMover(allLegalMoves, p));
			}
			
			// Prepare some arrays
			visitCounts = new int[numPlayers + 1][];
			for (int p = 1; p <= numPlayers; ++p)
			{
				visitCounts[p] = new int[legalMovesPerPlayer.get(p).size()];
			}
			
			scoreSums = new double[numPlayers + 1][];
			for (int p = 1; p <= numPlayers; ++p)
			{
				scoreSums[p] = new double[legalMovesPerPlayer.get(p).size()];
			}
			
			lastSelectedMovesPerPlayer = new int[numPlayers + 1];
		}
		
	}
	
	//-------------------------------------------------------------------------

}
