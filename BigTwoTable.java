import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * The BigTwoTable class implements the CardGameTable interface. 
 * It is used to build a GUI for the Big Two card game and handle all user actions. 
 * 
 * @author Kwok Chun Yu
 *
 */
public class BigTwoTable implements CardGameTable {
	
	private int frameWidth = 1000;
	private int frameHeight = 700;
	
	private BigTwoClient game;
	private boolean[] selected;
	private int activePlayer;
	private JFrame frame;
	private JPanel bigTwoPanel;
	private JButton playButton;
	private JButton passButton;
	private JTextArea msgArea;
	private JTextArea chatArea;
	private JTextField chatField;
	private Image[][] cardImages;
	private Image cardBackImage;
	private Image[] avatars;
	
	
	/**
	 * a constructor for creating a BigTwoTable. Create objects and add them to the frame.
	 * @param game ¡V a card game associates with this table
	 */
	public BigTwoTable(BigTwoClient game){
		this.game = game;
		getImageFromSrc();
		
		//frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.setTitle("Big Two");
		
		//menuBar
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		JMenuItem connect = new JMenuItem("Connect");
		JMenuItem quit = new JMenuItem("Quit");
		connect.addActionListener(new ConnectMenuItemListener());
		quit.addActionListener(new QuitMenuItemListener());
		menu.add(connect);
		menu.add(quit);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
		
		//bigTwoPanel
		bigTwoPanel = new BigTwoPanel();
		bigTwoPanel.setLayout(new BorderLayout());
		bigTwoPanel.setSize(frameWidth/2, frameHeight);

		//Buttons
		playButton = new JButton("Play");
		passButton = new JButton("Pass");
		playButton.addActionListener(new PlayButtonListener());
		passButton.addActionListener(new PassButtonListener());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.add(playButton);
		buttonPanel.add(passButton);
		
		//msgArea
		msgArea = new JTextArea(15,45);
		msgArea.setEditable(false);
		msgArea.setFont(new Font("Monospaced",1,12));
		JScrollPane msgAreaScroller = new JScrollPane(msgArea);
		msgAreaScroller.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		msgAreaScroller.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel msgPanel = new JPanel();
		msgPanel.add(msgAreaScroller);
		
		//chatArea
		chatArea = new JTextArea(14,45);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setBackground(Color.LIGHT_GRAY);
		chatArea.setFont(new Font("Monospaced",1,12));
		JScrollPane chatAreaScroller = new JScrollPane(chatArea);
		chatAreaScroller.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatAreaScroller.setHorizontalScrollBarPolicy(
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel chatPanel = new JPanel();
		chatPanel.add(chatAreaScroller);
		
		JLabel chatFieldLabel = new JLabel("Message: ");
		chatField = new JTextField(30);
		chatField.addActionListener(new ChatFieldListener());
		JPanel chatFieldLabelAndChatField = new JPanel();
		chatFieldLabelAndChatField.add(chatFieldLabel);
		chatFieldLabelAndChatField.add(chatField);
		
		JPanel completeChatPanel = new JPanel();
		completeChatPanel.setLayout(new BorderLayout());
		completeChatPanel.add(chatPanel, BorderLayout.CENTER);
		completeChatPanel.add(chatFieldLabelAndChatField, BorderLayout.SOUTH);
		
		//right panel
		JSplitPane rightPanel = new JSplitPane(SwingConstants.HORIZONTAL, msgPanel, completeChatPanel);
		
		//add panels
		bigTwoPanel.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(bigTwoPanel, BorderLayout.CENTER);
		frame.add(rightPanel, BorderLayout.EAST);

		frame.setSize(frameWidth, frameHeight);
		frame.setVisible(true);

	}

	/**
	 * a method for setting the index of the active player.
	 */
	@Override
	public void setActivePlayer(int activePlayer) {
		if (activePlayer < 0 || activePlayer >= game.getPlayerList().size()) {
			this.activePlayer = -1;
		} else {
			this.activePlayer = activePlayer;
		}

	}

	/**
	 * a method for getting an array of indices of the cards selected. 
	 */
	@Override
	public int[] getSelected() {

		int[] cardIdx = null;
		int count = 0;
		for (int j = 0; j < selected.length; j++) {
			if (selected[j]) {
				count++;
			}
		}

		if (count != 0) {
			cardIdx = new int[count];
			count = 0;
			for (int j = 0; j < selected.length; j++) {
				if (selected[j]) {
					cardIdx[count] = j;
					count++;
				}
			}
		}
		return cardIdx;
	}

	/**
	 * a method for resetting the list of selected cards.
	 */
	@Override
	public void resetSelected() { this.selected = null; }
	
	/**
	 * a method for retrieving image from src file.
	 */
	private void getImageFromSrc() {
		char[] suits = {'d','c','h','s'};
		char[] ranks = {'a', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'j', 'q', 'k'};
		cardImages = new Image[4][13];
		for(int suit = 0; suit < 4; suit++) {
			for(int rank = 0; rank < 13; rank++) {
				cardImages[suit][rank] = new ImageIcon("src/cards/" + ranks[rank] + suits[suit] + ".gif").getImage();
			}
		}
		cardBackImage = new ImageIcon("src/cards/b.gif").getImage();
		
		avatars = new Image[8];
		for (int i = 0; i < 4; i++) {
			avatars[i] = new ImageIcon("src/playerImage/" + i + ".jpg").getImage();
			avatars[i + 4] = new ImageIcon("src/playerImage/" + i + "d.jpg").getImage();
		}
	}

	/**
	 * a method for repainting the GUI. 
	 */
	@Override
	public void repaint() {
		resetSelected();
		frame.repaint();
	}

	/**
	 * a method for printing the specified string to the message area of the GUI.
	 */
	@Override
	public void printMsg(String msg) {
		msgArea.append(msg);
	}
	
	/**
	 * a method for printing the specified string to the message area of the GUI.
	 */
	public void printChat(String msg) {
		chatArea.append(msg);
	}

	/**
	 * a method for clearing the message area of the GUI. 
	 */
	@Override
	public void clearMsgArea() {
		msgArea.setText(null);
	}

	/**
	 * a method for resetting the GUI. 
	 */
	@Override
	public void reset() {
		this.resetSelected();
		this.clearMsgArea();
		this.enable();
	}

	/**
	 * a method for enabling user interactions with the GUI.
	 */
	@Override
	public void enable() {
		playButton.setEnabled(true);
		passButton.setEnabled(true);
		bigTwoPanel.setEnabled(true);
	}

	/**
	 * a method for disabling user interactions with the GUI. 
	 */
	@Override
	public void disable() {
		playButton.setEnabled(false);
		passButton.setEnabled(false);
		bigTwoPanel.setEnabled(false);
	}
	
	
	/**
	 * An inner class that implements ActionListener.
	 * This class provides the function to output message in the chatField to the chatArea.
	 * @author Kwok Chun Yu
	 *
	 */
	class ChatFieldListener implements ActionListener{
		
		/**
		 * print the message in the chatField to the chatArea.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String chat = chatField.getText();
			chatField.setText("");
			game.sendMessage(new CardGameMessage(CardGameMessage.MSG, -1, chat + "\n"));
		}
	}
	
	/**
	 * an inner class that extends the JPanel class and implements the MouseListener interface.
	 * This class provide the function to draw and to interact with the bigTwoPanel.
	 * 
	 * @author Kwok Chun Yu
	 *
	 */
	public class BigTwoPanel extends JPanel implements MouseListener{
		
		
		private static final long serialVersionUID = 1L;
		private int playerX = 5; //the x-coordinates of the starting position of player0 
		private int playerY = 15; //the y-coordinates of the starting position of player0
		private int cardX = 120; //the x-coordinates of the starting position of first card
		private int cardY = 20; //the y-coordinates of the starting position of first card
		private int cardWidth = 73; //the width of a card
		private int cardHeight = 97; //the height of a card
		private int cardNextTo = 15; //the horizontal distance of each card
		private int cardGoesUp = 15;
		private int rowHeight = 120; //the height of a row
		
		/**
		 * a constructor of BigTwoPanel.
		 */
		public BigTwoPanel() {
			this.addMouseListener(this);
		}
		
		/**
		 * a paint component for drawing players' name, image, their cards and the last hand on table.
		 */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2D = (Graphics2D) g;
			this.setBackground(Color.LIGHT_GRAY);
			this.setSize(frameWidth / 2, frameHeight);
			
			for (int i = 0; i < game.getNumOfPlayers(); i++) {
				if(i == game.getCurrentIdx()) {
					g.setColor(Color.WHITE);
				} else {
					g.setColor(Color.BLACK);
				}
				
				//print player's name and image
				if (i == activePlayer) {
					g.drawString(game.getPlayerList().get(i).getName() + " (You)", playerX, playerY + rowHeight*i);
					g.drawImage(avatars[i + 4], playerX, playerY + 5 + rowHeight*i, this);
				} else {
					g.drawString(game.getPlayerList().get(i).getName(), playerX, playerY + rowHeight*i);
					g.drawImage(avatars[i], playerX, playerY + 5 + rowHeight*i, this);
				}
				g.setColor(Color.BLACK);
				g2D.drawLine(0, playerY + rowHeight*(i+1) - 16, frameWidth / 2, playerY + rowHeight*(i+1) - 16);
				
				//print cards
				if (i == activePlayer) {
					for (int card = 0; card < game.getPlayerList().get(i).getNumOfCards(); card++) {
						int suit = game.getPlayerList().get(i).getCardsInHand().getCard(card).getSuit();
						int rank = game.getPlayerList().get(i).getCardsInHand().getCard(card).getRank();
						
						if (selected == null) {
							selected = new boolean[game.getPlayerList().get(i).getNumOfCards()];
						}
						if (selected[card]) {
							g.drawImage(cardImages[suit][rank], cardX + cardNextTo*card, cardY + rowHeight*i - cardGoesUp, this);
						} else {
							g.drawImage(cardImages[suit][rank], cardX + cardNextTo*card, cardY + rowHeight*i, this);
						}
					}
				} else {
					for (int card = 0; card < game.getPlayerList().get(i).getNumOfCards(); card++) {
						g.drawImage(cardBackImage, cardX + cardNextTo*card, cardY + rowHeight*i, this);
					}
				}
				
			}
			
			//print last hand on table
			Hand lastHandOnTable = (game.getHandsOnTable().isEmpty()) ? null : game.getHandsOnTable()
					.get(game.getHandsOnTable().size() - 1);
			if (lastHandOnTable != null) {
				g.drawString("Played by " + lastHandOnTable.getPlayer().getName(), 5, rowHeight*4 + 15);
				for (int card = 0; card < lastHandOnTable.size(); card++) {
					int suit = lastHandOnTable.getCard(card).getSuit();
					int rank = lastHandOnTable.getCard(card).getRank();
					g.drawImage(cardImages[suit][rank], 10 + cardNextTo*card, cardY + rowHeight*4, this);
				}
			} else {
				g.drawString("No card is played yet.", 10, rowHeight*4 + 20);
			}
		
		}
		
		/**
		 * a method for checking the coordinates of the mouse.
		 * 
		 * @param e the mouse event
		 * @param leftmostX the x-coordinates of the leftmost card
		 * @param numberOfCards number of cards to be printed
		 * @param ascendingPriority card of the left has a higher priority to be chosen
		 * @param isUpper return true if it is choosing the upper part of the card
		 */
		private void checkCards(MouseEvent e, int leftmostX, int numberOfCards, boolean ascendingPriority, boolean isUpper) {
			for (int rightmostCard = numberOfCards - 1; rightmostCard >= 0; rightmostCard--) {
				if (e.getX() >= leftmostX + cardNextTo * rightmostCard && e.getX() <= leftmostX + cardNextTo * rightmostCard + cardWidth) {
					if (selected[rightmostCard]) {
						if (ascendingPriority) {
							continue;
						}
						selected[rightmostCard] = false;
					}
					else {
						if (!ascendingPriority && isUpper) {
							continue;
						}
						selected[rightmostCard] = true;
					}
					break;
				}
			}
		}
		
		/**
		 * Select the card when the player clicks it.
		 */
		@Override
		public void mouseClicked(MouseEvent e) {
			int numberOfCards = game.getPlayerList().get(activePlayer).getNumOfCards();
			int rightmostX = cardX + (numberOfCards - 1)*cardNextTo + cardWidth;
			int leftmostX = cardX;
			
			if (e.getX() >= leftmostX && e.getX() <= rightmostX) {
					
				//disable selected card by clicking the upper part
				if (e.getY() >= rowHeight * activePlayer + cardY - cardGoesUp && e.getY() < rowHeight * activePlayer + cardY) {
					checkCards(e, leftmostX, numberOfCards, false, true);
				}
				//enable or disable card by clicking the middle part
				else if (e.getY() >= rowHeight * activePlayer + cardY && e.getY() <= rowHeight * activePlayer + cardY + cardHeight - cardGoesUp) {
					checkCards(e, leftmostX, numberOfCards, false, false);
				}
				//enable non-selected card by clicking the bottom part
				else if (e.getY() > rowHeight * activePlayer + cardY + cardHeight - cardGoesUp && e.getY() <= rowHeight * activePlayer + cardY + cardHeight) {
					checkCards(e, leftmostX, numberOfCards, true, false);
				}
				this.repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
		
	}
	
	/**
	 * an inner class that implements the ActionListener interface.
	 * This class is used to implement the play button.
	 * 
	 * @author Kwok Chun Yu
	 *
	 */
	class PlayButtonListener implements ActionListener{

		/**
		 * 
		 * Perform the action "play the selected cards" when this item is pressed.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (getSelected() != null){
				game.makeMove(activePlayer, getSelected());
			} else {
				printMsg("Not a legal move!!!\n");
			}
			repaint();
		}
		
	}
	
	/**
	 * an inner class that implements the ActionListener interface.
	 * This class is used to implement the pass button.
	 * 
	 * @author Kwok Chun Yu
	 *
	 */
	class PassButtonListener implements ActionListener{

		/**
		 * Perform the action "pass" when this item is pressed.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			game.makeMove(activePlayer, null);
			repaint();
		}
		
	}

	/**
	 * an inner class that implements the ActionListener interface.
	 * This class is used to implement the connect menu item.
	 * 
	 * @author Kwok Chun Yu
	 *
	 */
	class ConnectMenuItemListener implements ActionListener{

		/**
		 * Perform the action "connect the game" when this item is pressed.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if (game.isConnecting()) {
				printMsg("The connection is fine!\n");
				return; 
				}
			game.makeConnection();
		}
		
	}
	
	/**
	 * an inner class that implements the ActionListener interface.
	 * This class is used to implement the quit menu item.
	 * 
	 * @author Kwok Chun Yu
	 *
	 */
	class QuitMenuItemListener implements ActionListener{

		/**
		 * Perform the action "terminate the application" when this item is pressed.
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
		
	}
}
