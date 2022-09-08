package fences;

/**
 * A <code>Player</code> is one of the two basic entities of the Fences API. It represents a player with an ID number and a name.<br>
 * Please note that a <code>Player</code> object only stores information once provided by the API, it will not be updated automatically.
 * @author Jakob Danckwerts
 */
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
	
	/**
	 * Returns this player's unique ID.
	 * @author Jakob Danckwerts
	 */
	public long id() {
		return playerID;
	}
	
	/**
	 * Returns this player's name. This should be used as a kind of description rather than for identification as it is not guaranteed to be unique.
	 * @author Jakob Danckwerts
	 */
	public String name() {
		return name;
	}
}