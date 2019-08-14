package random;

import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.FastArrayList;
import util.AI;
import util.Context;
import util.Move;
import util.action.ActionPass;
import util.state.StateType;
import utils.AIUtils;

/**
 * Example third-party implementation of a random AI for Ludii
 * 
 * @author Dennis Soemers
 */
public class RandomAI extends AI
{
	
	//-------------------------------------------------------------------------
	
	/** Our player index */
	protected int player = -1;
	
	//-------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public RandomAI()
	{
		this.friendlyName = "Example Random AI";
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
		FastArrayList<Move> legalMoves = game.moves(context).moves();
		
		if (legalMoves.isEmpty())
		{
			final Move passMove = new Move(new ActionPass());
			passMove.setMover(player);
			return passMove;
		}
		
		// If we're playing a simultaneous-move game, some of the legal moves may be 
		// for different players. Extract only the ones that we can choose.
		if ((game.stateFlags() & StateType.Simultaneous) != 0)
			legalMoves = AIUtils.extractMovesForMover(legalMoves, player);
		
		final int r = ThreadLocalRandom.current().nextInt(legalMoves.size());
		return legalMoves.get(r);
	}
	
	@Override
	public void initAI(final Game game, final int playerID)
	{
		this.player = playerID;
	}
	
	//-------------------------------------------------------------------------

}
