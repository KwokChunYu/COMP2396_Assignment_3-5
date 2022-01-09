import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * The BigTwoClient class implements the CardGame interface and NetworkGame interface. 
 * It is used to model a Big Two card game that supports 4 players playing over the Internet.
 * 
 * @author Kwok Chun Yu
 *
 */
public class BigTwoClient implements CardGame, NetworkGame {	
	
	private int numOfPlayers = 4;
	private Deck deck;
	private ArrayList<CardGamePlayer> playerList;
	private ArrayList<Hand> handsOnTable;
	private Thread messagesReceiver;
	private int playerID;
	private String playerName;
	private String serverIP = "127.0.0.1";
	private int serverPort = 2396;
	private Socket sock;
	private ObjectOutputStream oos;
	private int currentIdx;
	private BigTwoTable table;
	
	/**
	 * a constructor for creating a Big Two client.
	 */
	public BigTwoClient() {
		playerList = new ArrayList<CardGamePlayer>();
		handsOnTable = new ArrayList<Hand>();
		for (int i = 0; i < numOfPlayers; i++) {
			playerList.add(new CardGamePlayer());
		}
		for (int i = 0; i < numOfPlayers; i++) {
			playerList.get(i).setName("");
		}
		table = new BigTwoTable(this);
		table.disable();
		
		playerName = JOptionPane.showInputDialog("Please enter your name first: ");
		if (playerName == null || playerName.isEmpty()) {
			playerName = "Player " + playerID;
		}
		
		makeConnection();
	}
	
	/**
	 * A method that check whether the client is connecting to the server or not.
	 * @return a boolean value on whether the connection is fine or broken.
	 */
	public boolean isConnecting() {
		return sock != null;
	}
	
	/**
	 * a method for getting the playerID of the local player.
	 */
	@Override
	public int getPlayerID() { return playerID; }

	/**
	 * a method for setting the playerID of the local player.
	 */
	@Override
	public void setPlayerID(int playerID) { this.playerID = playerID; }

	/**
	 * a method for getting the name of the local player.
	 */
	@Override
	public String getPlayerName() { return playerName; }

	/**
	 * ¡V a method for setting the name of the local player.
	 */
	@Override
	public void setPlayerName(String playerName) { this.playerName = playerName; }

	/**
	 * a method for getting the IP address of the game server.
	 */
	@Override
	public String getServerIP() { return serverIP; }

	/**
	 * a method for setting the IP address of the game server.
	 */
	@Override
	public void setServerIP(String serverIP) { this.serverIP = serverIP; }

	/**
	 * a method for getting the TCP port of the game server.
	 */
	@Override
	public int getServerPort() { return serverPort;	}

	/**
	 * a method for setting the TCP port of the game server.
	 */
	@Override
	public void setServerPort(int serverPort) { this.serverPort = serverPort; }

	/**
	 * a method for making a socket connection with the game server.
	 */
	@Override
	public void makeConnection() {
		try {
			sock = new Socket(serverIP, serverPort);
		} catch (Exception ex) {
			ex.printStackTrace(); 
			table.printMsg("Connection fail. Please connect again later.\n");
			sock = null;
			return;
		}
		try {
			oos = new ObjectOutputStream(sock.getOutputStream());
					
			Runnable threadJob = new ServerHandler();
			messagesReceiver = new Thread(threadJob);
			messagesReceiver.start();
			
			sendMessage(new CardGameMessage(CardGameMessage.JOIN,-1,playerName));
			sendMessage(new CardGameMessage(CardGameMessage.READY,-1,null));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Fail to create an ObjectOutputStream for the server.\n");
		}

	}

	/**
	 * a method for parsing the messages received from the game server.
	 */
	@Override
	public void parseMessage(GameMessage message) {
		switch (message.getType()) {
		case CardGameMessage.PLAYER_LIST:
			setPlayerID(message.getPlayerID());
			String[] otherPlayerNames = (String[]) message.getData();
			for (int id = 0; id < numOfPlayers; id++) {
				String name = otherPlayerNames[id];
				if (name == null) {
					name = "";
				}
				playerList.get(id).setName(name);
			}
			break;
			
		case CardGameMessage.JOIN:
			String name = (String) message.getData();
			playerList.get(message.getPlayerID()).setName(name);
			setPlayerName(name);
			table.setActivePlayer(message.getPlayerID());
			table.printMsg("HERE COMES A NEW CHALLENGER!!\nWelcome to the Big Two Game, " + (String) message.getData() + "\n");
			table.repaint();
			break;
			
		case CardGameMessage.FULL:
			table.printMsg("The server is FULL!!\nWhat a pity!!\n");
			break;
			
		case CardGameMessage.QUIT:
			String quitPlayerName = playerList.get(message.getPlayerID()).getName();
			playerList.get(message.getPlayerID()).setName("");
			table.printMsg(quitPlayerName + " lefts the game...\n");
			table.printMsg("BUT THERE ARE SOME ENDOTHERMIC PARTICIPANTS WAITING FOR THE NEXT GAME!!!");
			//if the game is in progress
			if (!endOfGame()) {
				sendMessage(new CardGameMessage(CardGameMessage.READY,-1,null));
			}
			break;
			
		case CardGameMessage.READY:
			table.printMsg(playerList.get(message.getPlayerID()).getName() + " is ready for an astonishing game.\n");
			break;
			
		case CardGameMessage.START:
			deck = (BigTwoDeck) message.getData();
			start(deck);
			table.enable();
			table.printMsg("GAME START!!!\n");
			break;
			
		case CardGameMessage.MOVE:
			checkMove(message.getPlayerID(), (int[]) message.getData());
			table.repaint();
			break;
			
		case CardGameMessage.MSG:
			table.printChat((String) message.getData());
			break;
			
		default: //invalid message
			table.printMsg("Wrong message type: " + message.getType());
			break;
		}

	}

	/**
	 * a method for sending the specified message to the game server
	 */
	@Override
	public void sendMessage(GameMessage message) {
		try{
			oos.writeObject(message);
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Fail to create an output message in the sendMessage method.\n");
			}
	}
	
	/**
	 * an inner class that implements the Runnable interface.
	 * This class provides the thread for receiving messages from the server.
	 * @author Kwok Chun Yu
	 *
	 */
	class ServerHandler implements Runnable {
		private ObjectInputStream ois;
		
		public ServerHandler() {
			try {
				ois = new ObjectInputStream(sock.getInputStream());
			} catch (Exception ex) {
				System.out.println("Error in creating an ObjectInputStream for the server!");
				ex.printStackTrace();
			}
		}
		@Override
		public void run() {
			CardGameMessage message;
			try {
				while ((message = (CardGameMessage) ois.readObject()) != null) {
					parseMessage(message);
				}
			} 
			catch (Exception ex) {
				ex.printStackTrace();
				table.printMsg("The connection to the server is broken or full.\nPlease reconnect to the server or wait for an empty slot.\n");
				sock = null;
			}
			table.repaint();
		}
		
	}



	/** 
	 * a method for retrieving the deck of cards being used
	 * @return deck of the game
	 */
	public Deck getDeck() { return deck; }
	
	/**
	 * a method for retrieving the list of players.
	 * @return a list of players
	 */
	public ArrayList<CardGamePlayer> getPlayerList(){ return playerList; }
	
	/**
	 * a method for retrieving the list of hands played on the table.
	 * @return the list of hands played on the table.
	 */
	public ArrayList<Hand> getHandsOnTable(){ return handsOnTable; }
	
	/**
	 * a method for retrieving the index of the current player.
	 * @return the index of the current player.
	 */
	public int getCurrentIdx() { return currentIdx; }
	
	/**
	 * a method for starting a Big Two card game
	 * @param args
	 */
	public static void main(String[] args) {
		new BigTwoClient();
	}
	
	/**
	 * a method for returning a valid hand from the specified list of cards of the player.
	 * @param player the player playing this hand
	 * @param cards the cards played
	 * @return a valid hand
	 */
	public static Hand composeHand(CardGamePlayer player, CardList cards) {
		if (cards.size() == 1) {
			Single single = new Single(player, cards);
			if (single.isValid()) {
				return single;
			}
			return null;
		}
		else if (cards.size() == 2) {
			Pair pair = new Pair(player, cards);
			if (pair.isValid()) {
				return pair;
			}
			return null;
		}
		else if (cards.size() == 3) {
			Triple triple = new Triple(player, cards);
			if (triple.isValid()) {
				return triple;
			}
			return null;
		}
		else if (cards.size() == 5) {
			Straight straight = new Straight(player, cards);
			Flush flush = new Flush(player, cards);
			StraightFlush straightFlush = new StraightFlush(player, cards);
			if (straight.isValid() && flush.isValid()) {
				return straightFlush;
			} else if (straight.isValid()) {
				return straight;
			} else if (flush.isValid()) {
				return flush;
			}
			
			FullHouse fullHouse = new FullHouse(player, cards);
			if (fullHouse.isValid()) {
				return fullHouse;
			}
			
			Quad quad = new Quad(player, cards);
			if (quad.isValid()) {
				return quad;
			}
			return null;
		} else { return null; }
	}
	
	/**
	 * a method for getting the number of players.
	 */
	@Override
	public int getNumOfPlayers() { return playerList.size(); }

	/**
	 * a method for starting/restarting the game with a given shuffled deck of cards.
	 */
	@Override
	public void start(Deck deck)  {
		
		this.deck = deck;
		
		//(i) remove all the cards from the players as well as from the table
		handsOnTable.clear();
		for (int i = 0; i < numOfPlayers; i++) {
			playerList.get(i).removeAllCards();
		}
		
		//(ii) distribute the cards to the players and (iii)  identify the player who holds the 3 of Diamonds
		Card diamondThree = new Card(0,2);
		for (int i = 0; i < playerList.size(); i++) {
			for (int j = 0; j < 13; j++) {
				playerList.get(i).getCardsInHand().addCard(deck.getCard(j + i * 13));
			}
			playerList.get(i).sortCardsInHand();
			while (playerList.get(i).getCardsInHand().getCard(0).rank < 2) {
				playerList.get(i).getCardsInHand().addCard(playerList.get(i).getCardsInHand().getCard(0));
				playerList.get(i).getCardsInHand().removeCard(0);
			}
		}
		for (int i = 0; i < getPlayerList().size(); i++) {
			if (playerList.get(i).getCardsInHand().getCard(0).equals(diamondThree)) {
					currentIdx = i;// (iv) set the currentIdx of the BigTwoClient instance to the player who holds the 3 of Diamonds
					table.printMsg(playerList.get(i).getName() + "\'s turn:\n");
					break;
				}
		}
		// (v) set the activePlayer of the BigTwoTable instance to the playerID
		table.setActivePlayer(playerID);
	}

	/**
	 * a method for making a move by a player with the specified playerID using the cards specified by the list of indices.
	 */
	@Override
	public void makeMove(int playerID, int[] cardIdx) {
		if (currentIdx == playerID) {
			sendMessage(new CardGameMessage(CardGameMessage.MOVE,-1,cardIdx));
		} else {
			table.printMsg("It's not your turn!\n");
		}
	}

	/**
	 * a method for checking a move made by a player.
	 */
	@Override
	public void checkMove(int playerID, int[] cardIdx) {
		CardGamePlayer lastPlayedPlayer = (handsOnTable.isEmpty()) ? null : handsOnTable
				.get(handsOnTable.size() - 1).getPlayer();
		Hand composedHand = null;
		CardList selectedCards = null;
		boolean isNextPlayerTurn = true;
		boolean goodFormat = true;
			
		if (cardIdx == null && playerList.get(playerID) != lastPlayedPlayer) {
			table.printMsg("{Pass}\n");
		} 
		// Define goodFormat as "The player played last hand cannot pass" and "The player should played same number of cards as the last hand if the player does not play the last hand".
		else if (playerList.get(playerID) == lastPlayedPlayer && cardIdx == null ||
				playerList.get(playerID) != lastPlayedPlayer && handsOnTable.size() != 0 && 
				cardIdx.length != handsOnTable.get(handsOnTable.size() - 1).size()) {
			table.printMsg("Not a legal move!!!\n");
			isNextPlayerTurn = false;
			goodFormat = false;
		}
		//if the hand is in good format, play or check the cards
		if (cardIdx != null && goodFormat) {
			selectedCards = new CardList();
			for (int i = 0; i < cardIdx.length; i++) {
				selectedCards.addCard(playerList.get(playerID).getCardsInHand().getCard(cardIdx[i]));
			}
			composedHand = composeHand(playerList.get(playerID), selectedCards);
			
			if (handsOnTable.size() < 1 && composedHand != null || playerList.get(currentIdx) == lastPlayedPlayer) {
				for (int i = 0; i < selectedCards.size(); i++) {
					playerList.get(playerID).getCardsInHand().removeCard(selectedCards.getCard(i));
				}
				handsOnTable.add(composedHand);
				table.printMsg("{" + composedHand.getType() + "} ");
				
				//print hand
				for (int i = 0; i < composedHand.size(); i++) {
					table.printMsg(" [" + composedHand.getCard(i) +"]");
				}
				table.printMsg("\n");
				
			} 
			//check whether one hand beats another hand
			else if (composedHand != null && handsOnTable.get(handsOnTable.size() - 1).beats(composedHand)){
				for (int i = 0; i < selectedCards.size(); i++) {
					playerList.get(playerID).getCardsInHand().removeCard(selectedCards.getCard(i));
				}
				handsOnTable.add(composedHand);
				table.printMsg("{" + composedHand.getType() + "} ");
				
				//print hand
				for (int i = 0; i < composedHand.size(); i++) {
					table.printMsg(" [" + composedHand.getCard(i) +"]");
				}
				table.printMsg("\n");
			} else {
				table.printMsg("Not a legal move!!!\n");
				isNextPlayerTurn = false;
			}
		}

			//update activePlayer
			if (isNextPlayerTurn) {
				if (currentIdx == 3) { currentIdx = 0; }
				else { currentIdx++; }
			}
			
			//game over
			if(endOfGame()) {
				table.setActivePlayer(-1);
				table.repaint();
				table.disable();
				String gameOverMessage = "Game ends\n";
				for (int i = 0; i < playerList.size(); i++) {
					if (playerList.get(i).getCardsInHand().size() != 0) {
						gameOverMessage += (playerList.get(i).getName() + " has " + playerList.get(i).getNumOfCards() + " cards in hand.\n");
					} else {
						gameOverMessage += (playerList.get(i).getName() + " wins the game.\n");
					}
				}
				JOptionPane.showMessageDialog(null, gameOverMessage);
				
			} else {
				table.printMsg(playerList.get(currentIdx).getName() + "\'s turn:\n");
			}
			table.repaint();
	}

	/**
	 * a method for checking if the game ends.
	 */
	@Override
	public boolean endOfGame() {
		for (int i = 0; i < playerList.size();i++) {
			if (playerList.get(i).getCardsInHand().size() == 0) {
				return true;
			}
		}
		return false;
	}
}
