package fences;

/**
 * A <code>Game</code> is one of the two basic entities of the Fences API. It represents a single game being played by two players.
 * It stores important values of the game like size and content of the board or the IDs of the two players.<br>
 * Please note that a <code>Game</code> object only stores information once provided by the API, it will not be updated automatically.
 * @author Jakob Danckwerts
 */
public class Game {
	
	/**
	 * Represents the status of a game. Options are <code>waiting</code> (value 1), <code>running</code> (value 2) and <code>finished</code> (value 3).
	 * <ul>
	 * 	<li><code>waiting</code> means that the game is currently waiting for players and has not started. It may be joined by any player.</li>
	 * 	<li><code>running</code> means that the game is full and running. Joining is not possible, players who have already joined can play now.</li>
	 * 	<li><code>finished</code> means that the game has come to an end. No actions are possible any more.</li>
	 * </ul>
	 * @author Jakob Danckwerts
	 */
	public static enum Status {
		waiting(1), running(2), finished(3);
		
		public final long value;
		
		Status(long value) {
			this.value = value;
		}
		
		public static Status from(long value) {
			for(Status s : Status.values()) {
				if(s.value == value) return s;
			}
			return null;
		}
		
		public String toString() {
			return this.name();
		}
	}
	
	/**
	 * Represents the content of a single field of a game. Options are <code>empty</code> (value 0), <code>player0</code> (value 1) and <code>player1</code> (value 2).
	 * <ul>
	 * 	<li><code>empty</code> means that the field is unoccupied and a turn can be made there.</li>
	 * 	<li><code>player0</code> means that the field is already occupied by the first player (player 0).</li>
	 * 	<li><code>player1</code> means that the field is already occupied by the second player (player 1).</li>
	 * </ul>
	 * @author Jakob Danckwerts
	 */
	public static enum Field {
		empty(0), player0(1), player1(2);
		
		public final int value;
		
		Field(int value) {
			this.value = value;
		}
		
		public static Field from(int value) {
			for(Field f : Field.values()) {
				if(f.value == value) return f;
			}
			return null;
		}
		
		public String toString() {
			return "" + value;
		}
	}
	
	protected long gameID;
	protected Status status;
	protected Long width;
	protected Long height;
	protected Long player0;
	protected Long player1;
	protected Boolean activePlayer;
	protected Field[] content;
	
	public Game(long gameID) {
		this.gameID = gameID;
	}
	
	public Game(long gameID, Status status, Long width, Long height, Long player0, Long player1, Boolean activePlayer, Field[] content) {
		this.gameID = gameID;
		this.status = status;
		this.width = width;
		this.height = height;
		this.player0 = player0;
		this.player1 = player1;
		this.activePlayer = activePlayer;
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "Game { gameID=" + gameID
		+ (this.status != null ? ", status=" + status : "")
		+ (this.width != null ? ", width=" + width : "")
		+ (this.height != null ? ", height=" + height : "")
		+ (this.player0 != null ? ", player0=" + player0 : "")
		+ (this.player1 != null ? ", player1=" + player1 : "")
		+ (this.activePlayer != null ? ", activePlayer=" + activePlayer : "")
		+ (this.content != null ? ", content=[" + contentString() + "]" : "")
		+ " }";
	}
	
	/**
	 * Returns this game's unique ID.
	 * @author Jakob Danckwerts
	 */
	public long id() {
		return gameID;
	}
	
	/**
	 * Returns the current status of this game.
	 * @see fences.Game.Status
	 * @author Jakob Danckwerts
	 */
	public Status status() {
		return status;
	}
	
	/**
	 * Returns the width of this game's board, usually 5.
	 * @see fences.Game#content()
	 * @author Jakob Danckwerts
	 */
	public Long width() {
		return width;
	}
	
	/**
	 * Returns the height of this game's board, usually 5.
	 * @see fences.Game#content()
	 * @author Jakob Danckwerts
	 */
	public Long height() {
		return height;
	}
	
	/**
	 * Returns the ID of this game's first player (player 0). Returns <code>null</code> if there is no player 0.
	 * @author Jakob Danckwerts
	 */
	public Long player0() {
		return player0;
	}
	
	/**
	 * Returns the ID of this game's second player (player 1). Returns <code>null</code> if there is no player 1.
	 * @author Jakob Danckwerts
	 */
	public Long player1() {
		return player1;
	}
	
	/**
	 * Returns this game's currently active player, meaning the player who is currently allowed to play his turn. <code>true</code> for player 1, <code>false</code> for player 0.
	 * @author Jakob Danckwerts
	 */
	public Boolean activePlayer() {
		return activePlayer;
	}
	
	/**
	 * Returns the board of this game, consisting of <code>Field</code> values.
	 * The board is stored as a single concatenation of all its rows, starting at the top left corner.
	 * @author Jakob Danckwerts
	 */
	public Field[] content() {
		return content;
	}
	
	/**
	 * Returns the board in string form. Probably not useful.
	 * @author Jakob Danckwerts
	 */
	public String contentString() {
		if(content == null) return null;
		
		String s = "";
		for(Field f : content) {
			s += f;
		}
		return s;
	}
}