package fences;

public class Player {
	protected long playerID;
	protected String name;
	
	public Player(long playerID) {
		this.playerID = playerID;
	}
	
	public Player(long playerID, String name) {
		this.playerID = playerID;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "Player { playerID=" + playerID
		+ (name != null ? ", name=\"" + name + "\"" : "")
		+ " }";
	}

	public long id() {
		return playerID;
	}

	public String name() {
		return name;
	}
}