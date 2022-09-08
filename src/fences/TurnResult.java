package fences;

/**
 * A <code>TurnResult</code> stores information provided by the API when a player makes a turn.
 * It contains the ID of the corresponding game and, if it was won by this turn, the winner.
 * @author Jakob Danckwerts
 */
public class TurnResult {
	protected Game game;
	protected Boolean winner;
	
	public TurnResult(long gameID, Boolean winner) {
		this.game = new Game(gameID);
		this.winner = winner;
	}
	
	public TurnResult(long gameID) {
		this.game = new Game(gameID);
		this.winner = null;
	}
	
	public String toString() {
		return "TurnResult { gameID=" + game.id()
		+ (winner == null ? "" : ", winner=" + (winner ? 1 : 0))
		+ " }";
	}
	
	/**
	 * Returns the game in which the turn was made.
	 * @author Jakob Danckwerts
	 */
	public Game game() {
		return this.game;
	}
	
	/**
	 * Returns the winner, if any (otherwise <code>null</code>). <code>true</code> if the winner is player 1, <code>false</code> if the winner is player 0.
	 * @author Jakob Danckwerts
	 */
	public Boolean winner() {
		return this.winner;
	}
}