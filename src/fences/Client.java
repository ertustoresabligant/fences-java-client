package fences;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;

import json.JSONObject;
import json.JSONArray;
import json.parser.JSONParser;

/**
 * A <code>Client</code> is the basic object for interaction with the Fences API.
 * Use it to send requests and retrieve the information provided by the server.<br>
 * For further information and documentation, see <a href="https://jaleda.notion.site/Fences-Server-7d2ee9990cfe405790bb789f5dce7f7f">this</a> and <a href="https://tinyurl.com/5t4fburh">this</a>.
 * @author Jakob Danckwerts
 */
public class Client {
	private KeyFactory keyFactory;
	private HttpClient client;
	private String parentURL;
	
	/**
	 * Initializes a <code>Client</code> object that communicates with the API server at the given location.
	 * @author Jakob Danckwerts
	 */
	public Client(String ipAddress, String port) throws IOException, InterruptedException {
		try {
			this.keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			this.keyFactory = null;
		}
		this.parentURL = "http://" + ipAddress + ":" + port + "/fences";
		this.client = HttpClient.newHttpClient();
	}
	
	protected HttpClient client() {
		return this.client;
	}
	
	protected String parentURL() {
		return this.parentURL;
	}
	
	/**
	 * Fetches all publicly visible games.
	 * @author Jakob Danckwerts
	 */
	public Result<ArrayList<Game>> getPublicGames() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/get"))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONArray array = (JSONArray)parser.parse(body);
					ArrayList<Game> list = new ArrayList<>();
					
					for(Object obj : array) {
						if(obj == null) continue;
						JSONObject o = (JSONObject) obj;
						
						try {
							long gameID = (Long) o.get("gameID");
							Game.Status status = Game.Status.from((Long) o.get("status"));
							Long width = (Long) o.get("width");
							Long height = (Long) o.get("height");
							Long player0 = (Long) o.get("player0");
							Long player1 = (Long) o.get("player1");
							Game g = new Game(gameID, status, width, height, player0, player1, null, null);
							list.add(g);
						} catch(Exception e) { 
							continue;
						}
					}
					return new Result<ArrayList<Game>>(list);
				} catch(Exception e) {
					return new Result<ArrayList<Game>>(APIStatus.error);
				}
			case 400:
				return new Result<ArrayList<Game>>(APIStatus.invalid);
			case 403:
				return new Result<ArrayList<Game>>(APIStatus.forbidden);
			case 404:
				return new Result<ArrayList<Game>>(APIStatus.notFound);
			case 500:
				return new Result<ArrayList<Game>>(APIStatus.serverError);
			default:
				return new Result<ArrayList<Game>>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<ArrayList<Game>>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<ArrayList<Game>>(APIStatus.error);
		}
	}
	
	/**
	 * Fetches a single game which is identified by the given ID number.
	 * @author Jakob Danckwerts
	 */
	public Result<Game> getGame(long gameID) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/game/get?gameID=" + gameID))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					
					if(o == null) return new Result<Game>(APIStatus.error);
					
					long id = (Long) o.get("gameID");
					Game.Status status = Game.Status.from((Long) o.get("status"));
					Long width = (Long) o.get("width");
					Long height = (Long) o.get("height");
					Long player0 = (Long) o.get("player0");
					Long player1 = (Long) o.get("player1");
					
					long activePlayerInteger = (Long) o.get("currentPlayer");
					boolean activePlayer = activePlayerInteger == 1;
					
					String contentString = (String) o.get("content");
					char[] contentChars = contentString.toCharArray();
					Game.Field[] content = new Game.Field[contentChars.length];
					for(int i = 0; i<contentChars.length; i++) {
						content[i] = Game.Field.from(Integer.parseInt(contentChars[i] + ""));
					}
					
					return new Result<Game>(new Game(id, status, width, height, player0, player1, activePlayer, content));
				} catch(Exception e) {
					return new Result<Game>(APIStatus.error);
				}
			case 400:
				return new Result<Game>(APIStatus.invalid);
			case 403:
				return new Result<Game>(APIStatus.forbidden);
			case 404:
				return new Result<Game>(APIStatus.notFound);
			case 500:
				return new Result<Game>(APIStatus.serverError);
			default:
				return new Result<Game>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<Game>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<Game>(APIStatus.error);
		}
	}
	
	/**
	 * Requests to create a new game.
	 * @author Jakob Danckwerts
	 */
	public Result<Game> createGame() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/game/create"))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					if(o == null) return new Result<Game>(APIStatus.error);
					
					return new Result<Game>(new Game((Long) o.get("gameID")));
				} catch(Exception e) {
					return new Result<Game>(APIStatus.error);
				}
			case 400:
				return new Result<Game>(APIStatus.invalid);
			case 403:
				return new Result<Game>(APIStatus.forbidden);
			case 404:
				return new Result<Game>(APIStatus.notFound);
			case 500:
				return new Result<Game>(APIStatus.serverError);
			default:
				return new Result<Game>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<Game>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<Game>(APIStatus.error);
		}
	}
	
	/**
	 * Fetches a single player which is identified by the given ID number.
	 * @author Jakob Danckwerts
	 */
	public Result<Player> getPlayer(long playerID) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/player/get?playerID=" + playerID))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					
					if(o == null) return new Result<Player>(APIStatus.error);
					
					long id = (Long) o.get("playerID");
					String name = (String) o.get("name");
					
					return new Result<Player>(new Player(id, name));
				} catch(Exception e) {
					return new Result<Player>(APIStatus.error);
				}
			case 400:
				return new Result<Player>(APIStatus.invalid);
			case 403:
				return new Result<Player>(APIStatus.forbidden);
			case 404:
				return new Result<Player>(APIStatus.notFound);
			case 500:
				return new Result<Player>(APIStatus.serverError);
			default:
				return new Result<Player>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<Player>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<Player>(APIStatus.error);
		}
	}
	
	/**
	 * Requests to create a new player with the given name.<br>
	 * Please note that only URL-friendly characters are allowed, e.g. letters, numbers, dashes, and underscores.
	 * @author Jakob Danckwerts
	 */
	public Result<Player> createPlayer(String name) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/player/create?name=" + name))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					if(o == null) return new Result<Player>(APIStatus.error);
					
					return new Result<Player>(new Player((Long) o.get("playerID")));
				} catch(Exception e) {
					return new Result<Player>(APIStatus.error);
				}
			case 400:
				return new Result<Player>(APIStatus.invalid);
			case 403:
				return new Result<Player>(APIStatus.forbidden);
			case 404:
				return new Result<Player>(APIStatus.notFound);
			case 500:
				return new Result<Player>(APIStatus.serverError);
			default:
				return new Result<Player>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<Player>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<Player>(APIStatus.error);
		}
	}
	
	/**
	 * Requests that a specific player, identified by <code>playerID</code>, joins a specific game, identified by <code>gameID</code>.
	 * @author Jakob Danckwerts
	 */
	public Result<JoinResult> joinGame(long gameID, long playerID) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/game/join?gameID=" + gameID + "&playerID=" + playerID))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					
					if(o == null) return new Result<JoinResult>(APIStatus.error);
					
					long gID = (Long) o.get("gameID");
					long pID = (Long) o.get("playerID");
					Long roleNumber = (Long) o.get("player");
					
					return new Result<JoinResult>(new JoinResult(gID, pID, roleNumber == 1));
				} catch(Exception e) {
					return new Result<JoinResult>(APIStatus.error);
				}
			case 400:
				return new Result<JoinResult>(APIStatus.invalid);
			case 403:
				return new Result<JoinResult>(APIStatus.forbidden);
			case 404:
				return new Result<JoinResult>(APIStatus.notFound);
			case 500:
				return new Result<JoinResult>(APIStatus.serverError);
			default:
				return new Result<JoinResult>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<JoinResult>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<JoinResult>(APIStatus.error);
		}
	}
	
	/**
	 * Requests that the player identified by the given ID number joins an available game.
	 * @author Jakob Danckwerts
	 */
	public Result<JoinResult> findGame(long playerID) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/game/find?playerID=" + playerID))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					
					if(o == null) return new Result<JoinResult>(APIStatus.error);
					
					long gID = (Long) o.get("gameID");
					long pID = (Long) o.get("playerID");
					Long roleNumber = (Long) o.get("player");
					
					return new Result<JoinResult>(new JoinResult(gID, pID, roleNumber == 1));
				} catch(Exception e) {
					return new Result<JoinResult>(APIStatus.error);
				}
			case 400:
				return new Result<JoinResult>(APIStatus.invalid);
			case 403:
				return new Result<JoinResult>(APIStatus.forbidden);
			case 404:
				return new Result<JoinResult>(APIStatus.notFound);
			case 500:
				return new Result<JoinResult>(APIStatus.serverError);
			default:
				return new Result<JoinResult>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<JoinResult>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<JoinResult>(APIStatus.error);
		}
	}
	
	/**
	 * Requests that the player identified by <code>playerID</code> makes a turn in the game identified by <code>gameID</code>.<br>
	 * The board coordinates of the turn, i.e. where to place the fence, are given by
	 * <code>y</code> which identifies the row (counting from the top) and
	 * <code>x</code> which identifies the position inside the row (counting from the left side).<br>
	 * Please note that each small row is regarded as an extension of the big one above, so their <code>y</code> values are equal and the
	 * <code>x</code> values continuously count from one to (2*width)-1.
	 * @author Jakob Danckwerts
	 */
	public Result<TurnResult> makeTurn(long gameID, long playerID, long x, long y) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/game/set-field?gameID=" + gameID + "&playerID=" + playerID + "&x=" + x + "&y=" + y))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					
					if(o == null) return new Result<TurnResult>(APIStatus.error);
					
					long id = (Long) o.get("gameID");
					Boolean winner = (Boolean) o.get("winner");
					
					return new Result<TurnResult>(new TurnResult(id, winner));
				} catch(Exception e) {
					return new Result<TurnResult>(APIStatus.error);
				}
			case 400:
				return new Result<TurnResult>(APIStatus.invalid);
			case 403:
				return new Result<TurnResult>(APIStatus.forbidden);
			case 404:
				return new Result<TurnResult>(APIStatus.notFound);
			case 500:
				return new Result<TurnResult>(APIStatus.serverError);
			default:
				return new Result<TurnResult>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<TurnResult>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<TurnResult>(APIStatus.error);
		}
	}
	
	/**
	 * Fetches all games, not only the public ones.<br>
	 * Requires administrator authentication.
	 * @author Jakob Danckwerts
	 * @see fences.AdminToken
	 * @see fences.Client#createToken(String)
	 */
	public Result<ArrayList<Game>> getAllGames(AdminToken token) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/get-all?sessionID=" + token.sessionID + "&key=" + token.token))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONArray array = (JSONArray)parser.parse(body);
					ArrayList<Game> list = new ArrayList<>();
					
					for(Object obj : array) {
						if(obj == null) continue;
						JSONObject o = (JSONObject) obj;
						
						try {
							long gameID = (Long) o.get("gameID");
							Game.Status status = Game.Status.from((Long) o.get("status"));
							Long width = (Long) o.get("width");
							Long height = (Long) o.get("height");
							Long player0 = (Long) o.get("player0");
							Long player1 = (Long) o.get("player1");
							Game g = new Game(gameID, status, width, height, player0, player1, null, null);
							list.add(g);
						} catch(Exception e) { 
							continue;
						}
					}
					return new Result<ArrayList<Game>>(list);
				} catch(Exception e) {
					e.printStackTrace();
					return new Result<ArrayList<Game>>(APIStatus.error);
				}
			case 400:
				return new Result<ArrayList<Game>>(APIStatus.invalid);
			case 401:
			case 403:
				return new Result<ArrayList<Game>>(APIStatus.forbidden);
			case 404:
				return new Result<ArrayList<Game>>(APIStatus.notFound);
			case 500:
				return new Result<ArrayList<Game>>(APIStatus.serverError);
			default:
				return new Result<ArrayList<Game>>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<ArrayList<Game>>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<ArrayList<Game>>(APIStatus.error);
		}
	}
	
	/**
	 * Requests that the game identified by <code>gameID</code> is deleted.<br>
	 * Requires administrator authentication.
	 * @author Jakob Danckwerts
	 * @see fences.AdminToken
	 * @see fences.Client#createToken(String)
	 */
	public Result<Game> deleteGame(AdminToken token, long gameID) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/game/delete?gameID=" + gameID + "&sessionID=" + token.sessionID + "&key=" + token.token))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					
					if(o == null) return new Result<Game>(APIStatus.error);
					
					long id = (Long) o.get("gameID");
					return new Result<Game>(new Game(id));
				} catch(Exception e) {
					e.printStackTrace();
					return new Result<Game>(APIStatus.error);
				}
			case 400:
				return new Result<Game>(APIStatus.invalid);
			case 401:
			case 403:
				return new Result<Game>(APIStatus.forbidden);
			case 404:
				return new Result<Game>(APIStatus.notFound);
			case 500:
				return new Result<Game>(APIStatus.serverError);
			default:
				return new Result<Game>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<Game>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<Game>(APIStatus.error);
		}
	}
	
	/**
	 * Requests the deletion of all games.<br>
	 * Requires administrator authentication.
	 * @author Jakob Danckwerts
	 * @see fences.AdminToken
	 * @see fences.Client#createToken(String)
	 */
	public APIStatus deleteAllGames(AdminToken token) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/delete-all?sessionID=" + token.sessionID + "&key=" + token.token))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				return APIStatus.ok;
			case 400:
				return APIStatus.invalid;
			case 401:
			case 403:
				return APIStatus.forbidden;
			case 404:
				return APIStatus.notFound;
			case 500:
				return APIStatus.serverError;
			default:
				return APIStatus.error;
			}
		} catch(IOException e) {
			e.printStackTrace();
			return APIStatus.error;
		} catch(InterruptedException e) {
			e.printStackTrace();
			return APIStatus.error;
		}
	}
	
	/**
	 * Requests and generates a single-use administrator token for privileged API access.
	 * @author Jakob Danckwerts
	 * @see fences.AdminToken
	 */
	public Result<AdminToken> createToken(String authKey) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(this.parentURL + "/key"))
					.build();
			HttpResponse<String> response = this.client.send(request, BodyHandlers.ofString());
			
			int code = response.statusCode();
			switch(code) {
			case 200:
				String body = response.body();
				
				try {
					JSONParser parser = new JSONParser();
					JSONObject o = (JSONObject)parser.parse(body);
					
					if(o == null) return new Result<AdminToken>(APIStatus.error);
					
					String sessionID = (String) o.get("sessionID");
					
					String dataString = (String) o.get("puk");
					String[] dataSplit = dataString.split("-");
					byte[] data = new byte[dataSplit.length];
					for(int i = 0; i<dataSplit.length; i++) {
						data[i] = (byte) Short.parseShort(dataSplit[i]);
					}

			        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(data);
			        PublicKey key = keyFactory.generatePublic(keySpec);
			        
			        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			    	cipher.init(Cipher.ENCRYPT_MODE, key);
			    	byte[] encrypted = cipher.doFinal("0eea8ab0-6788-4f8d-8f41-0283b07f8d06".getBytes());
			    	
			    	String s = "";
			    	if(encrypted.length > 1) {
			    		for(int i = 0; i<encrypted.length-1; i++) {
			    			s += encrypted[i] + ",";
			    		}
			    		s += encrypted[encrypted.length-1];
			    	} else if(encrypted.length == 1) {
			    		s = "" + encrypted[0];
			    	}
			        
			        return new Result<AdminToken>(new AdminToken(sessionID, s));
				} catch(Exception e) {
					e.printStackTrace();
					return new Result<AdminToken>(APIStatus.error);
				}
			case 400:
				return new Result<AdminToken>(APIStatus.invalid);
			case 403:
				return new Result<AdminToken>(APIStatus.forbidden);
			case 404:
				return new Result<AdminToken>(APIStatus.notFound);
			case 500:
				return new Result<AdminToken>(APIStatus.serverError);
			default:
				return new Result<AdminToken>(APIStatus.error);
			}
		} catch(IOException e) {
			e.printStackTrace();
			return new Result<AdminToken>(APIStatus.error);
		} catch(InterruptedException e) {
			e.printStackTrace();
			return new Result<AdminToken>(APIStatus.error);
		}
	}
}
