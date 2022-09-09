package fences;

/**
 * An <code>AdminToken</code> stores an identifiable token for privileged API use.
 * @author Jakob Danckwerts
 */
public class AdminToken {
	protected String sessionID;
	protected String token;
	
	protected AdminToken(String sessionID, String token) {
		this.sessionID = sessionID;
		this.token = token;
	}
	
	public String toString() {
		return "AdminToken " + sessionID;
	}
	
	/**
	 * Returns the unique session ID.
	 * @author Jakob Danckwerts
	 */
	public String sessionID() {
		return this.sessionID;
	}
	
	/**
	 * Returns the token.
	 * @author Jakob Danckwerts
	 */
	public String token() {
		return this.token;
	}
}