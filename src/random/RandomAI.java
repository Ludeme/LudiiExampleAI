package random;

import java.util.concurrent.ThreadLocalRandom;

import game.Game;
import main.FastArrayList;
import util.AI;
import util.Context;
import util.Move;
import util.action.ActionPass;

/**
 * Example third-party implementation of a random AI for Ludii
 * 
 * @author Dennis Soemers
 */
public class RandomAI extends AI
{
	
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
		final SearchLimits limitType, 
		final double limitValue
	)
	{
		final FastArrayList<Move> legalMoves = game.moves(context).moves();
		
		if (legalMoves.isEmpty())
		{
			return new Move(new ActionPass());
		}
		
		final int r = ThreadLocalRandom.current().nextInt(legalMoves.size());
		return legalMoves.get(r);
	}
	
	@Override
	public void initAI(final Game game)
	{
		// do nothing
	}
	
	//-------------------------------------------------------------------------

}
