import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JOptionPane;


public class MenuComponent extends JComponent implements KeyListener {

	private long curTime;

	private int selection;
	private int level;
	private int phase;

	private String[] menuOpt;

	private Image backgroundImage, midgroundImage, moonIcon;
	
	public MenuComponent(long curTime) {
		this.curTime = curTime;
		menuOpt = new String[3];
		menuOpt[0] = "Start";
		menuOpt[1] = "Help";
		menuOpt[2] = "Exit";

		level = 0;//set the level to an unselected level

		phase = 1;

		selection = 0;
		
		
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("MoonMidGround.png");
		} catch (IOException e) {
			System.out.println("failed to load midground images");
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		midgroundImage = ss.grabSprite(0, 0, 1600, 1200);

		try {
			spriteSheet = loader.loadImage("MoonBackGround.png");
		} catch (IOException e) {
			System.out.println("failed to load background images");
		}
		ss = new SpriteSheet(spriteSheet);

		backgroundImage = ss.grabSprite(0, 0, 1600, 1200);
		
		try {
			spriteSheet = loader.loadImage("MoonIcon.png");
		} catch (IOException e) {
			System.out.println("failed to load background images");
		}
		ss = new SpriteSheet(spriteSheet);
		
		moonIcon = ss.grabSprite(0, 0, 128, 128);
	}

	public void paintComponent(Graphics g) {
		//Recover Graphics 2D
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(),  getHeight());
		
		g2.drawImage(backgroundImage, null, null);

		
		if (phase == 1) {
			g2.setFont(new Font("Impact", 0, 72));
			g2.setColor(Color.WHITE);
			g2.drawString("Space Wars", getWidth()/4, getHeight()/4);

			g2.setFont(new Font("Impact", 0, 32));
			for (int i = 0; i < menuOpt.length; i ++) {
				if (selection == i) g2.setColor(Color.BLUE);
				else g2.setColor(Color.WHITE);

				g2.drawString(menuOpt[i], getWidth()/2-80, getHeight()/2 + (i*64));
			}
		}
		
		if (phase == 2) {
			g2.setFont(new Font("Impact", 0, 40));
			g2.setColor(Color.WHITE);
			g2.drawString("Select a Level:", getWidth()/3, 40);
			
			g2.setFont(new Font("Impact", 0, 32));
			for (int i = 0; i < menuOpt.length-1; i ++) {
				if (selection == i) g2.setColor(Color.BLUE);
				else g2.setColor(Color.WHITE);

				g2.drawString(menuOpt[i], (i+1*menuOpt[i].length()*32), getHeight()/2 + getHeight()/4);
			}
			
			//draw icons
			AffineTransform saveAt = g2.getTransform();
			if (selection == 0) {
				g2.scale(1.5, 1.5);
				g2.drawImage(moonIcon, 48, 128, null,null);
			} else {
				g2.drawImage(moonIcon, 100, 240, null,null);
			}
			
			g2.setTransform(saveAt);
			
		}
		
		if (phase == 3) {
			int lineNum = 0;
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Helvetica",0, 20));
			String helpText = "Space Wars is a two-player, same device, turn-based fighting game.\n"
					+ "\nControls:\n"
					+ "\t-Use the arrow keys or WASD keys to move the camera.\n"
					+ "\t-Click on a player on your team to open the weapons menu.\n"
					+ "\t\t-From the weapons menu, you can choose to move the player or attack.\n"
					+ "\t\t-If you choose to move, you can attack afterwards.\n"
					+ "\t\t-Once, you've attacked, the turn ends.\n"
					+ "\t-Controls for each weapon are listed in the weapons menu.\n"
					+ "\nThe game continues until all of the players on one team are killed.\n"
					+ "\nRandom weapon supply crates have the chance to spawn around the map.\n"
					+ "\tThrow your player at the crate to collect the weapon supply.\n"
					+ "\n\n\nPress SPACEBAR to return to the main menu.";
			
			for (String line : helpText.split("\n")) {
		        g2.drawString(line, 64, 64 + g2.getFontMetrics().getHeight()*lineNum);
		        lineNum ++;
			}
		}

	}



	@Override
	public void keyTyped(KeyEvent e) {


	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		//up
		if (e.getKeyCode() == KeyEvent.VK_W || e.getKeyCode() == KeyEvent.VK_UP) {
			if (phase == 1) {
				if (selection > 0) selection--;
				else selection = menuOpt.length-1;
			}
		}

		//down
		if (e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) {
			if (phase == 1) {
				if (selection < menuOpt.length-1) selection++;
				else selection = 0;
			}
		}
		
		//left
		if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_LEFT) {
			if (phase == 2) {
				if (selection > 0 ) selection--;
				else selection = menuOpt.length-2;
			}
		}
		
		//right
		if (e.getKeyCode() == KeyEvent.VK_D || e.getKeyCode() == KeyEvent.VK_RIGHT) {
			if (phase == 2) {
				if (selection < menuOpt.length-2) selection++;
				else selection = 0;
			}
		}

		//Enter key/Space Key
		if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE){
			if (phase == 3) {
				phase = 1;
				
			} else if (phase == 2) {
				if (selection == 0)
					level = selection+1;
				if (selection == 1)
					JOptionPane.showMessageDialog(getParent(), "Sorry! This level isn't ready for gameplay yet!");
			} else if (phase == 1) {
				switch (selection) {
				case 0: phase++;//if the user selects "Start"
				menuOpt = new String[3];
				menuOpt[0] = "Moon";
				menuOpt[1] = "Asteroid Field ";
				menuOpt[2] = "Level 3???";
				break;
				
				case 1: phase = 3;
				
				break;
				
				case 2: System.exit(0);//If the User selects "Exit"
				}
			}
			
			
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(ABORT);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public int getLevel() {
		return level;
	}
}
