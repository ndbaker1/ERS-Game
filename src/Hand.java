import java.util.*;

public class Hand{
	
	private List<PlayingCard> hand;
	private boolean slapping;
	public static int turn = 0; // first player is player 0
	public static int playedLastFaceCard = -1;
	
	public Hand(ArrayList<PlayingCard> list){
		hand = new ArrayList<PlayingCard>(list);
		slapping = false;
	}
	
	public void take(ArrayList<PlayingCard> p){
		for(PlayingCard card: p){
			hand.add(card);
		}
		slapping = false;
	}
	
	public PlayingCard playCard(){
		if (hand.size() > 0){
			return hand.remove(0);
		}
		return null;
	}

	public int getSize(){
		return hand.size();
	}
	
	public void slap(){
		slapping = true;
	}
	
	public boolean isSlapping(){
		return slapping;
	}
	
	public PlayingCard burn(){
		slapping = false;
		if (hand.size() > 0){
			return hand.remove(0);	
		}
		return null;
	
	}
	
	public String toString(){
		return hand.toString();
	}
}