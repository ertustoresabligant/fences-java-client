package fences;

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
	
	public Game game() {
		return this.game;
	}
	
	public Boolean winner() {
		return this.winner;
	}
}