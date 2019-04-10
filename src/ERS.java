import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.imageio.*;

public class ERS extends JComponent implements KeyListener, Runnable{
	////////////
	// Arrays //
	////////////
	private Hand[] players;
	private boolean[] keys;
	private Color[] player_colors;
	private ArrayList<Integer> key_codes;
	/* Arrays of Cards [ Decks ] */
	private Deck deck;
	private Deck burn_pile;
	/////////////////////////
	// States and Counters //
	/////////////////////////
	private int faceCardState;
	private boolean finished;
	private boolean final_form;
	private int last_man_standing;
	private int tick;
	////////////////////////
	// Graphic Components //
	////////////////////////
	private Image cardBack;
	private BufferedImage back;


	public ERS(int num_players){
		setBackground(Color.BLACK);
		
		players = new Hand[num_players];
		player_colors = new Color[num_players];
		deck = new Deck();
		burn_pile = new Deck();	burn_pile.clear(); //it should start empty		
		keys = new boolean[num_players*2];
		key_codes = new ArrayList<Integer>();
		faceCardState = -2;
		last_man_standing = -1;
		finished = false;
		final_form = false;
		
		/* Loads the card-back texture for the burn pile */
		try{	cardBack = ImageIO.read(new File("resources/card_sprites/cardb.png"));	}
		catch(Exception e){	System.out.println (e.getMessage()); }
		
		for(int i = 0; i < players.length;i++){
			players[i] = new Hand(deck.draw(deck.NUM_OF_CARDS_IN_DECK/num_players));
			/* assigns colors to every index of a Player */
			int max = 400;
			int first = (int)(Math.random()*200);
			int second = (int)(Math.random()*first);
			int third = Math.abs(first-second);
			
			player_colors[i] = new Color(55+(int)(Math.random()*200),55+(int)(Math.random()*200),55+(int)(Math.random()*200));
		}
		if(deck.sizeOfDeck() > 0){
			burn_pile = deck;
			deck = new Deck();
			deck.clear();
		}
		
		this.addKeyListener(this);
		setVisible(true);
	}
	
	private boolean assigned(){
		return key_codes.size() == keys.length;
	}
	
	public void paintComponent( Graphics graphToBack )
	{
		graphToBack.setFont(new Font("Comic Sans MS",Font.PLAIN,16));
		graphToBack.setColor(new Color(80,50,80));
		graphToBack.fillRect(0,0,800,600);
		if(!assigned()){
			/* Notifies the Player to press keys that they want assigned */
			graphToBack.setColor(Color.WHITE);
			graphToBack.drawString("Player "+(1+key_codes.size()/2)+" Assign " + actionAssignment() +" Key", (Game.WIDTH - graphToBack.getFontMetrics().stringWidth("Player = = Assign = = Key"))/2, 200);
			graphToBack.drawString("NOTICE: Player 1 goes first", (Game.WIDTH - graphToBack.getFontMetrics().stringWidth("NOTICE: Player 1 goes first"))/2, 300);
		}
		else{
			/* Method Desc. ==> Prints all the Player's hand sizes */
			displayPlayerHands(graphToBack);
			/* Prints the Player who takes the pile after a facecard is completed */
			if (faceCardState == -1){
				graphToBack.setColor(player_colors[Hand.playedLastFaceCard]);
				Hand.turn = Hand.playedLastFaceCard;
				graphToBack.drawString("PLAYER " + (Hand.playedLastFaceCard+1)+ " TAKES THE PILE", (Game.WIDTH - graphToBack.getFontMetrics().stringWidth("PLAYER " + Hand.playedLastFaceCard+ " TAKES THE PILE"))/2, 500);
			}graphToBack.setColor(Color.WHITE);
			/* Prints whether the SLAP-OFF is going on or not
			 * 	  SLAP-OFF is true	 ==> 	Draws the SLAP-OFF notification to the screen	*/			
			if (	final_form		){	graphToBack.drawString("SLAP OFF", (Game.WIDTH - graphToBack.getFontMetrics().stringWidth("SLAP OFF"))/2, 50);	}
			
			/* Method Desc. ==> Draws the current deck to the screen */
			displayCards(graphToBack,deck.displayDeck(),(Game.WIDTH - PlayingCard.CARD_WIDTH)/2, (Game.HEIGHT - PlayingCard.CARD_HEIGHT)/2);
			/* Draws the burn pile to the screen
			 * && displays the number of cards in it */
			graphToBack.drawImage(cardBack, 550, (Game.HEIGHT - PlayingCard.CARD_HEIGHT)/2, PlayingCard.CARD_WIDTH, PlayingCard.CARD_HEIGHT, null);
			graphToBack.drawString("BURN CARDS", 550+(PlayingCard.CARD_WIDTH - graphToBack.getFontMetrics().stringWidth("BURN CARDS"))/2, 5*Game.HEIGHT/17);
			
			/* ONLY PRINTS THE BURN PILE SIZE IF IT IS GREATER THAN ZERO */
			if (burn_pile.sizeOfDeck() > 0){
				graphToBack.setFont(new Font("Comic Sans MS", Font.HANGING_BASELINE, 25));
				graphToBack.drawString(""+burn_pile.sizeOfDeck(), 550+(PlayingCard.CARD_WIDTH-graphToBack.getFontMetrics().stringWidth(""+burn_pile.sizeOfDeck())-4/* 4 pixel offset*/)/2, Game.HEIGHT/2+4/* 4 pixels offset */);
				graphToBack.setFont(new Font("Comic Sans MS",Font.PLAIN,16));
			}
			if (	tick++ < 1000	){	graphToBack.drawString(String.format("%.2f",tick/1000.0)+" Seconds Before Play Resumes", (Game.WIDTH - graphToBack.getFontMetrics().stringWidth("===== Seconds Before Play Resumes"))/2, 50);	}
		}	
	}
	
	/* Displays all of Player hand sizes */	
	private void displayPlayerHands(Graphics g){
		for(int i = 0; i < players.length; i++){
			g.setColor(player_colors[i]);
			g.drawString("Player "+(i+1)+" Hand Size: "+ players[i].getSize(), 50, 50+(500/players.length)/3+(500/players.length)*i);
			int[] x = {25,30,25,45};
			int[] y = {54+(500/players.length)/3+(500/players.length)*i,   44+(500/players.length)/3+(500/players.length)*i,   34+(500/players.length)/3+(500/players.length)*i,   44+(500/players.length)/3+(500/players.length)*i};
			if (Hand.turn == i){	g.fillPolygon(x,y,4);	}
		}
	}
	
	/* Displays a particular Deck of cards to the screen */
	private void displayCards(Graphics g, ArrayList<PlayingCard> i, int x, int y) {
    	for (PlayingCard card : i){
    		((Graphics2D)g).rotate(card.getRotation(), 400, 300);
	    	g.drawImage(card.getImage(), x, y, PlayingCard.CARD_WIDTH , PlayingCard.CARD_HEIGHT, null);
	    	((Graphics2D)g).rotate(-card.getRotation(), 400, 300);	
    	}
	}
	
	/* Prints the current ACTION that the Players should ASSIGN */
	private String actionAssignment(){
		if (key_codes.size()%2 ==0)	{	return "Slap";	}
		else						{	return "Play";	}
	}
	/**
	 * Updates the keys array based on the key that was pressed
	 * Uses the arrow keys left, right, up and down
	 * @param e the KeyEvent representing the pressed key
	 */
	public void keyPressed(KeyEvent e){
		if(assigned()){
			for(int i = 0; i < keys.length; i++){
				if (e.getKeyCode() == key_codes.get(i)){
					keys[i] = true;
				}
			}
		}else{	assignKeys(e);	}
		repaint();
	}

	/**
	 * Updates the keys array based on the key that was released
	 * @param e the KeyEvent representing the released key
	 */
	public void keyReleased(KeyEvent e){
		if (assigned()){
			for(int i = 0; i < keys.length; i++){
				if (e.getKeyCode() == key_codes.get(i)){
					keys[i] = false;
				}
			}
		}
		repaint();
	}
	public void keyTyped(KeyEvent e){}
	
	/* Returns whether the Deck has a slappable match */
	public boolean hasMatch(){
		return deck.hasDoubleOrTen() || deck.hasSandwich();
	}
	
	/* Adds the pressed key to the KEY_CODE array */
	private void assignKeys(KeyEvent e){
		System.out.println (key_codes.size());
		key_codes.add(e.getKeyCode());
	}
	
	/* Returns the index of the next Player that has cards */
	private int findNextAvailablePlayer(int i){
		/* changes the turn to the next hand that has cards */
		int k = (i/2)+1;
		while(players[k%players.length].getSize() <= 0 && k <= players.length*2){
			k++;
		}
		return (k)%players.length;
	}
	
	private void keyEventHandling(){
		/* NUMBER OF KEYS IS TWICE THE NUMBER OF PLAYERS */
		for(int i = 0; i < keys.length; i++){
			if (keys[i] && tick > 1000 /* waits one second 1000 miliseconds = 1 second) after someone successfully slaps */){
				 /* SLAP */
				 if (i%2 == 0){
					System.out.println ("Player " +(1+i/2) +" slapping");
					
					/* sets Player's current slapping state to true */
					players[i/2].slap();
				}
				else{
					/* PLAY - can only be done if it is the Player's turn */
					if(i/2 == Hand.turn){
						/*	Checks if the game is waiting on a facecard or if the game is in the SLAP-OFF phase
						 *		Facecard has not	||	SLAP-OFF is  	
						 *		been completed		||	happenning	*/
						if (	faceCardState != -1	||	final_form	){
							if (players[i/2].getSize() > 0){
								System.out.println ("Player " +(1+i/2) +" playing");
								
								deck.addTop(players[i/2].playCard());
								/* If the top card is a face card */
								if (deck.triggerFaceCardState() != 0){
									Hand.playedLastFaceCard = i/2;
									faceCardState = deck.triggerFaceCardState();
									Hand.turn = findNextAvailablePlayer(i);
								}
								else{
									/* If the top card is a 10 */
									if(deck.isTen()){
										faceCardState = -2;
										Hand.playedLastFaceCard = -1;
									}
									/* playing the last card of a face card's count [ex. Jack = 1 card = (facecardState = 0)] 
									 *	OR
									 * playing your last card on a face card
									 *	OR
									 * Normal play	*/
									if (faceCardState <= 0 || players[i/2].getSize() <= 0){	
										Hand.turn = findNextAvailablePlayer(i);
									}
								}
							}
							// facecard decrementer 
							faceCardState--;
						}
					}
				}
				keys[i] = false;
			}
		}	
	}
	
	/* Take the current pile of cards and retains the Player's turn */
	private void takePile(int i){
		players[i].take(deck.draw(deck.sizeOfDeck())); /* Player takes the current */
		players[i].take(burn_pile.draw(burn_pile.sizeOfDeck())); /* Player takes the burn pile */
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		////////////////////////////////////////////////////// RESETS ALL THE GAME VARIABLES /////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/*	nullifies ability to		starts the 1 second	timer 			Sets the turn to the Player		Resets the current facecard counter 
		 *  take facecard pile			until the next person can play		who successfully slapped			[no facecard in action]			*/
		Hand.playedLastFaceCard = -1;			tick = 0;						Hand.turn = i;						faceCardState = -2;		
	}
	
	private void gameEventHandling(){
		if(assigned()){
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  			///////////////////////////////////////// SLAP EVALUATION AND SPECIAL CASES /////////////////////////////////////
  			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			/* disables all face card triggers during SLAP-OFF phase */
  			if (final_form){	faceCardState = -2;	Hand.playedLastFaceCard = -1;	}
  			/* Checks all the Players to see if any of them have slapped */
		  	for(int i = 0; i < players.length; i++){
				if(players[i].isSlapping()){
					/* The Player who played the last completed facecard gets the pile when they slap */
					if (i == Hand.playedLastFaceCard && faceCardState == -1){
					/* Refer to Method ==> takePile*/
						takePile(i);
					}
					else{
						/* The Player has slapped incorrectly */
						if(!hasMatch()){
							burn_pile.addBottom(players[i].burn()); /* They BURN a card */
							System.out.println ("Player " +(i+1)+ " burned");				
							
							/* if the Player burns out of cards then their turn is over */
							if (players[i].getSize() == 0){	Hand.turn = (i+1)%players.length;	}
						}
						else{
						/* Refer to Method ==> takePile */
							takePile(i);
							System.out.println ("Player " + (i+1)+" actually slapped correctly");
							
							/* Game ends if the slap off is active and the person with all the cards slaps correctly */
							if (final_form && last_man_standing == i){
								finished = true;
								System.out.println ("Player " +(i+1)+ " won");
							}else{
								final_form = false;
								last_man_standing = -1;
							}
						}
					}	
				}
				///////////////////////////////////////////////////////////////////////////////////////////////////////////
				///////////////////////////////// SLAP-OFF - CHECKING VARIALBES AND LOOPS /////////////////////////////////
				///////////////////////////////////////////////////////////////////////////////////////////////////////////
				int hasCards = 0;
				int player = -1;
				/* Loops through all the Players to see how many Players have cards */
				for(int p = 0; p < players.length; p++){
					if (players[p].getSize() > 0){
						hasCards++;
						player = p; /* keeps track of the last Player to have cards [used for SLAP-OFF] */
					}
				}
				/* if the last person played a face card then the face card is not considered */
				if (hasCards == 1 && Hand.playedLastFaceCard == player){	faceCardState = -2;	}
				
				/*	Checks if the Game should enter SLAP-OFF phase [Only 1 Player has cards and there is no face card being played] 
				 *		Only 1 person	&&		There is no current
				 *		has cards		&&		face card on the deck	*/
				if	(	hasCards == 1	&&		faceCardState < -1	){
					final_form = true; /* Sets the game into SLAP-OFF phase */
					last_man_standing = player; /* last Player to have cards becomes the last man standing */
					Hand.turn = last_man_standing;
				}
				/*	Checks if the Game has resulted in a DRAW [all hand sizes equal 0] 
				 *		No Player		&&	It is not possible
				 *		has cards 		&&	for anyone to slap	*/
				if	(	hasCards == 0	&&	!hasMatch()	){
					System.out.println ("The Cake is a Lie.");
					finished = true; /* EXITS THE GAME LOOP */
				}
			}
		}	
	}
	
	public void run(){
		try{
			/* Main Game Loop */
			while(!finished){
				Thread.currentThread().sleep(1);
		      	repaint();
		      	keyEventHandling();
		      	gameEventHandling();  	
			}	
			/*	Game loop ended here. */
		}catch(Exception e){
			System.out.println (e.getMessage());
			System.exit(0);
		}
	}
}