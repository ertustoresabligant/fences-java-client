package fences;

/**
 * A <code>JoinResult</code> stores information provided by the API when a player joins a game,
 * meaning the game's and player's ID and the position ("role") of the player in the game.
 * @author Jakob Danckwerts
 */
public class JoinResult {
	protected Game game;
	protected Player player;
	protected boolean role;
	
	public JoinResult(long gameID, long playerID, boolean role) {
		this.game = new Game(gameID);
		this.player = new Player(playerID);
		this.role = role;
	}
	
	public String toString() {
		return "JoinResult { gameID=" + game.id() + ", playerID=" + player.id() + ", role=" + role + " }";
	}
	
	/**
	 * Returns the game that was joined.
	 * @author Jakob Danckwerts
	 */
	public Game game() {
		return this.game;
	}
	
	/**
	 * Returns the player who joined the game.
	 * @author Jakob Danckwerts
	 */
	public Player player() {
		return this.player;
	}
	
	/**
	 * Returns the role of the player in the newly joined game. <code>true</code> if this player is the new player 1, <code>false</code> for player 0.
	 * @author Jakob Danckwerts
	 */
	public boolean playerRole() {
		return this.role;
	}
}