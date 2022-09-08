package fences;

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
	
	public Game game() {
		return this.game;
	}
	
	public Player player() {
		return this.player;
	}
	
	public boolean playerRole() {
		return this.role;
	}
}