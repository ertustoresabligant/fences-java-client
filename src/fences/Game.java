package fences;

public class Game {
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
	
	public long id() {
		return gameID;
	}

	public Status status() {
		return status;
	}

	public Long width() {
		return width;
	}

	public Long height() {
		return height;
	}

	public Long player0() {
		return player0;
	}

	public Long player1() {
		return player1;
	}

	public Boolean activePlayer() {
		return activePlayer;
	}

	public Field[] content() {
		return content;
	}
	
	public String contentString() {
		if(content == null) return null;
		
		String s = "";
		for(Field f : content) {
			s += f;
		}
		return s;
	}
}