import java.util.*;
import javax.swing.*;
import java.awt.Component;
import javax.swing.*;

public class Game extends JFrame{
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;
	private ERS ers; 
	public Thread ersThread;
	
	public Game(){
		super("ERS");
		setSize(WIDTH,HEIGHT);
		toFront();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public ERS getGame(){
		return ers;
	}
	
	public void setGame(ERS newGame){
		getContentPane().removeAll();
		ers = newGame;
		((Component)ers).setFocusable(true);
		getContentPane().add(ers);
		ersThread = new Thread((Runnable)ers);
		ersThread.start();
		setVisible(true);
		revalidate();
	}
	
	public static void main(String[] args){
		boolean actually_finished = false;
		
		Game ERS_GAME = new Game();
		while (!actually_finished){
			int num_players = 0;
			while(num_players <= 1){
				try{	num_players = input.getNumPlayers(ERS_GAME);	}
				catch(NumberFormatException e){	JOptionPane.showMessageDialog(null,"Enter a Number Please.");	}
				catch(IndexOutOfBoundsException e){	JOptionPane.showMessageDialog(null,e.getMessage());	}
			}
			
			ERS_GAME.setGame(new ERS(num_players));
			try{	ERS_GAME.ersThread.join();	}
			catch(InterruptedException e) {	System.out.print(e.getMessage());	}
			
			actually_finished = input.getPlayAgain();
			ERS_GAME.setVisible(false);
		}
		ERS_GAME.dispose();
	}
}

class input{
	public static int getNumPlayers(Game game){
		String in = JOptionPane.showInputDialog("How many Players?");
		if (in != null){
			if (in.equals("")){
				return 0;
			}
			else{
				int num = Integer.parseInt(in);
				if (num <= 1){
					throw new IndexOutOfBoundsException("Enter a Number of Players Greater than 1.");
				}else{
					return num;
				}
			}
		}else{
			System.exit(0);
			return -1;
		}
	}
	
	public static boolean getPlayAgain(){
		String[] options = {"YES","NO"};
		if (JOptionPane.showOptionDialog(null,
										 "Play Again?",
										 null,
										 JOptionPane.YES_NO_OPTION,
										 JOptionPane.QUESTION_MESSAGE,
										 null,
										 options,
										 options[0]) == 1){
			return true;
		}
		return false;	
	}
}


