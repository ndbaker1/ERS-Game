import java.awt.Image;

public class PlayingCard{
	private int suit;
	private int number;
	public static final int CARD_WIDTH = 150;
	public static final int CARD_HEIGHT = 240;
	private Image image;
	private double rotation;
	
	public PlayingCard(int s, int n, Image im, double r){
		suit = s;
		number = n;
		image = im;
		rotation = r;
	}
	
	public int getSuit(){
		return suit;
	}
	
	public int getNumber(){
		return number;
	}
	
	public Image getImage(){
		return image;
	}
	
	public double getRotation(){
		return rotation;
	}
	
	public boolean equals(PlayingCard c){
		return this.number == c.number;
	}
	
	public String toString(){
		return "\nSuit: " + suit + " Number: " + number;
	}
	
	
}