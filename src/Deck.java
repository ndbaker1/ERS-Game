import java.util.*;
import java.io.File;
import java.awt.Image;
import javax.imageio.ImageIO;

public class Deck{
	public static final int NUM_OF_CARDS_IN_DECK = 52;
	public static final int NUM_OF_SUITS = 4;
	public static final int NUM_OF_NUMBERS = 13;
	
	private List<PlayingCard> deck;
	
	public Deck(){
		deck = new ArrayList<PlayingCard>();
		for(int i = 1; i < NUM_OF_NUMBERS+1; i++){
			for(int j = 0; j < NUM_OF_SUITS; j++){
				Image image = null;
				try{ image = ImageIO.read(getClass().getClassLoader().getResource("resources/card_sprites/card (" + (NUM_OF_SUITS*(i-1)+j+1) + ").png")); }
				catch(Exception e){	System.out.println (image); }
				deck.add(new PlayingCard(j, i, image, Math.random()*(Math.PI/3) - Math.PI/6));
			}
		}
		shuffleDeck();
	}
	
	public void clear(){
		deck.clear();
	}
	
	public void shuffleDeck(){
		ArrayList<PlayingCard> temp = new ArrayList<PlayingCard>();
		while(deck.size() > 0){
			temp.add(deck.remove((int)(Math.random()*deck.size())));
		}
		deck = temp;
	}
	
	public void addBottom(PlayingCard c){
		if (c!=null){
			deck.add(deck.size(), c);
		}
	}
	
	public void addTop(PlayingCard c){
		if (c != null){
			deck.add(0,c);
		}
	}
	
	public ArrayList<PlayingCard> draw(int num){ // returns a draw of a certain size from the deck
		ArrayList<PlayingCard> temp = new ArrayList<PlayingCard>();
		
		for(int i = 0; i < num; i++){
			temp.add(deck.remove(0));
		}
		
		return temp;
	}
	
	public int sizeOfDeck(){
		return deck.size();
	}
	
	public boolean hasSandwich(){
		if (deck.size() > 2){
			return deck.get(0).equals(deck.get(2)) || (deck.get(0).getNumber()+ deck.get(2).getNumber()) == 10;
		}
		return false;
	}
	
	public boolean hasDoubleOrTen(){
		if (deck.size() > 1)
			return deck.get(0).equals(deck.get(1)) || (deck.get(0).getNumber()+ deck.get(1).getNumber()) == 10;
		return false;
	}
	
	public int triggerFaceCardState(){
		if (deck.size() > 0){
			if (deck.get(0).getNumber() == 1){
				return 4;
			}
			if (deck.get(0).getNumber() == 11){
				return 1;
			}
			if (deck.get(0).getNumber() == 12){
				return 2;
			}
			if (deck.get(0).getNumber() == 13){
				return 3;
			}
			
		}
		
		return 0;
	}
	
	public boolean isTen(){
		if (deck.size() > 0){
			return deck.get(0).getNumber() == 10;
		}
		return false;
	}
	
	public String toString(){
		return deck.toString();
	}
	
	public ArrayList<PlayingCard> displayDeck(){
		ArrayList<PlayingCard> images = new ArrayList<PlayingCard>();
		int cnt = 0;
		while(cnt < deck.size() && cnt < 5){
			images.add(0,deck.get(cnt));
			cnt++;
		}
		return images;
	}
}