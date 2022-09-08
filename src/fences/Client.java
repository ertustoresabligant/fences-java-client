package fences;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;

import json.JSONObject;
import json.JSONArray;
import json.parser.JSONParser;

public class Client {
	private HttpClient client;
	private String parentURL;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("start");
		Client c = new Client("localhost", "3141");
		
		Result<Game> gr = c.createGame();
		if(gr.status() != APIStatus.ok) {
			System.out.println(gr.status());
			return;
		}
		
		Game g = gr.value();
		System.out.println(g);
		
		Result<Player> pr = c.createPlayer("andreas");
		if(pr.status() != APIStatus.ok) {
			System.out.println(pr.status());
			return;
		}
		
		Player p = pr.value();
		System.out.println(p);
		
		Result<JoinResult> jr = c.joinGame(g.id(), p.id());
		if(jr.status() != APIStatus.ok) {
			System.out.println(jr.status());
			return;
		}
		
		JoinResult j = jr.value();
		System.out.println(j);
		
		Result<Player> pr2 = c.createPlayer("andreas-2");
		if(pr2.status() != APIStatus.ok) {
			System.out.println(pr2.status());
			return;
		}
		
		Player p2 = pr2.value();
		System.out.println(p2);
		
		Result<JoinResult> jr2 = c.joinGame(g.id(), p2.id());
		if(jr2.status() != APIStatus.ok) {
			System.out.println(jr2.status());
			return;
		}
		
		JoinResult j2 = jr2.value();
		System.out.println(j2);
		
		Result<TurnResult> tr = c.makeTurn(g.id(), p.id(), 0, 0);
		if(tr.status() != APIStatus.ok) {
			System.out.println(tr.status());
			return;
		}
		
		TurnResult t = tr.value();
		System.out.println(t);
	}
	
	public Client(String ipAddress, String port) throws IOException, InterruptedException {
		this.parentURL = "http://" + ipAddress + ":" + port + "/fences";
		this.client = HttpClient.newHttpClient();
	}
	
	protected HttpClient client() {
		return this.client;
	}
	
	protected String parentURL() {
		return this.parentURL;
	}
	
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
	 * Only URL-friendly characters are allowed, like letters, numbers, dashes, underscores etc.
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
}
