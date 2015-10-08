import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class Menu {

	public static final int WIDTH = 400;
	public static final int HEIGHT = 300;
	public static final int RED = 0;
	public static final int BLUE = 1;

	private int x, y;
	private int curTeam;
	private boolean visible;

	private boolean canThrow;

	private int selection;

	ArrayList<Shape> buttons;
	ArrayList<String> buttonText;
	ArrayList<String> buttonDesc;
	ArrayList<Image> buttonImages;

	private int[] items;
	private SpaceMan displayObj;

	public Menu() {
		x = WIDTH/2;
		y = HEIGHT/2;
		curTeam = RED;
		selection = -1;

		visible = false;
		canThrow = true;
		buttons = new ArrayList<Shape>();
		buttonText = new ArrayList<String>();
		buttonDesc = new ArrayList<String>();
		buttonImages = new ArrayList<Image>();
		displayObj = null;

		items = new int[12];

		makeButtons();
	}

	private void makeButtons() {
		//get images
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("Weapons.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);
		
		
		buttons.add(new Rectangle(x+8, y+152, 100, 64));
		buttonText.add("Throw Player");
		buttonImages.add(ss.grabSprite(128, 32, 50, 32));
		buttonDesc.add("Click and hold on the player. Drag\nthe mouse to aim. Release to \nthrow the player.");
		
		buttons.add(new Rectangle(x+8, y+224, 100, 64));
		buttonText.add("Skip This Turn");
		buttonImages.add(ss.grabSprite(192, 32, 50, 32));
		buttonDesc.add("Finish the current turn.");

		buttons.add(new Rectangle(x+116, y+152, 64, 64));
		buttonText.add("Bomb");
		buttonImages.add(ss.grabSprite(0, 0, 24, 24));
		buttonDesc.add("Click and hold on the bomb. Drag the\nmouse to aim. Release to throw\nthe bomb.");
		
		buttons.add(new Rectangle(x+188, y+152, 64, 64));
		buttonText.add("Missile");
		buttonImages.add(ss.grabSprite(32, 0, 32, 32));
		buttonDesc.add("Point in the direction. Click to launch.\nThe missile follows the mouse.\nExplodes on contact or mouse click.");

		buttons.add(new Rectangle(x+260, y+152, 64, 64));
		buttonText.add("Laser Cannon");
		buttonImages.add(ss.grabSprite(128, 64, 40, 32));
		buttonDesc.add("Point in the direction to fire. Click to\nfire laser. Travels in a straight line\nuntil hitting a wall.");

		buttons.add(new Rectangle(x+332, y+152, 64, 64));
		buttonText.add(" ");
		buttonImages.add(ss.grabSprite(0, 32, 32, 32));
		buttonDesc.add("");

		buttons.add(new Rectangle(x+116, y+224, 64, 64));
		buttonText.add("Defense Walls");
		buttonImages.add(ss.grabSprite(128, 0, 32, 32));
		buttonDesc.add("Click to place each of the three walls.");
		
		buttons.add(new Rectangle(x+188, y+224, 64, 64));
		buttonText.add(" ");
		buttonImages.add(ss.grabSprite(0, 32, 32, 32));
		buttonDesc.add("");

		buttons.add(new Rectangle(x+260, y+224, 64, 64));
		buttonText.add(" ");
		buttonImages.add(ss.grabSprite(0, 32, 32, 32));
		buttonDesc.add("");

		buttons.add(new Rectangle(x+332, y+224, 64, 64));
		buttonText.add("UFO Attack");
		buttonImages.add(ss.grabSprite(192, 0, 64, 32));
		buttonDesc.add("Click on an enemy player to\nattack them with a UFO.");

		buttons.add(new Ellipse2D.Double(x+WIDTH-24,y+4, 20,20));
		buttonText.add("Exit Menu");
		buttonImages.add(ss.grabSprite(192, 64, 20, 20));
		buttonDesc.add("Exit the menu.");
	}

	public void draw(Graphics2D g2) {
		if (visible) {

			g2.setColor(new Color(0, 0, 0, 128 ));
			g2.fillRect(0,0,GameComponent.LEVEL_HEIGHT, GameComponent.LEVEL_WIDTH);

			if (curTeam == RED) g2.setColor(Color.RED);
			if (curTeam == BLUE) g2.setColor(Color.BLUE);


			g2.fillRoundRect(x, y, WIDTH, HEIGHT, 10,10);

			g2.setColor(Color.WHITE);
			g2.drawRoundRect(x, y, WIDTH, HEIGHT,10,10);

			//draw weapon text description
			
			g2.setColor(Color.DARK_GRAY);
			g2.fillRoundRect(x+8, y+48, WIDTH - 16, 96, 10, 10);
			g2.setColor(Color.BLACK);
			g2.drawRoundRect(x+8, y+48, WIDTH - 16, 96, 10, 10);
			
			g2.setColor(Color.WHITE);
			
			//draw all buttons and text
			for (int i = 0; i < buttons.size(); i++) {
				g2.setFont(new Font("Impact", 0, 24));
				if (selection == i) {
					g2.setColor(Color.WHITE);
					g2.drawString(buttonText.get(i), x+8, y+32);
					g2.setColor(Color.GRAY);
					
					
					//draw description
					int lineNum = 0;
					g2.setColor(Color.WHITE.darker());
					for (String line : buttonDesc.get(i).split("\n")) {
				        g2.drawString(line, x+12, y +72 + g2.getFontMetrics().getHeight()*lineNum);
				        lineNum ++;
					}
				} else {
					g2.setColor(Color.WHITE);
					
				}
				if (selection == -1) g2.drawString("Pick a weapon: ", x+8, y+32);

				if (i == 0 && !canThrow)//if you can't throw the player anymore, display the button as grayed out
					g2.setColor(Color.DARK_GRAY);

				if (i > 1 && i < buttons.size()-1) {
					if (items[i-2] <= 0) {   ///darken menu spot if no items remaining
						g2.setColor(Color.DARK_GRAY);
					}
				}
				
				
				
				g2.fill(buttons.get(i));//fill the button

				if (i > 1 && i < 10 && i != 9) { //draw all weapon buttons (except UFO)
					g2.drawImage(buttonImages.get(i),buttons.get(i).getBounds().x+12, buttons.get(i).getBounds().y+4, 
							(int) (buttons.get(i).getBounds().width/1.5), (int) (buttons.get(i).getBounds().height/1.5),
							null);
				} else if (i != 9) { //draw move, skip turn, and Exit button
					g2.drawImage(buttonImages.get(i),buttons.get(i).getBounds().x, buttons.get(i).getBounds().y, 
							buttons.get(i).getBounds().width, buttons.get(i).getBounds().height,
							null);
				} else if (i == 9) { //draw ufo
					g2.drawImage(buttonImages.get(i),buttons.get(i).getBounds().x+2, buttons.get(i).getBounds().y, 
							60, 28,
							null);
				}
				
				g2.setColor(Color.BLACK);
				g2.draw(buttons.get(i));

				
				if (i > 1 && i != 10) {
					int numItems = items[i-2];
					g2.drawString(""+Integer.toString(numItems), buttons.get(i).getBounds().x+2, (int) buttons.get(i).getBounds().y+buttons.get(i).getBounds().width/2+28);
				}
			}
			

		}
	}

	public void update(int mouseX, int mouseY) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i).contains(mouseX, mouseY))  {
				selection = i;
				break;
			} else {
				selection = -1;
			}
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public void menuClick() {
		if (visible) {
			switch (selection) {
			case 0: 
				if (canThrow) {
					setVisible(false);
					GameComponent.setThrowing(true);
				}

				break;
			case 1: GameComponent.changeTurn();
			setVisible(false);
			break;
			case 2: //select bomb
				if (items[0] > 0) {
					items[0] --;
					setVisible(false);
					GameComponent.setAttacking(Weapon.BOMB, 100);
				}
				break;
			case 3: //select missile
				if (items[1] > 0) {
					items[1] --;
					setVisible(false);
					GameComponent.setAttacking(Weapon.MISSILE, 100);
				}
				break;
			case 4: //select laser
				if (items[2] > 0) {
					items[2] --;
					setVisible(false);
					GameComponent.setAttacking(Weapon.LASER, 100);
				}
				break;
			case 6: //select wall
				if (items[4] > 0) {
					items[4] --;
					setVisible(false);
					GameComponent.setAttacking(Weapon.WALL, 320);
				}
			break;
			case 9: //select UFO
				if (items[7] > 0) {
					items[7] --;
					setVisible(false);
					GameComponent.setAttacking(Weapon.UFO, 100);
				}
				break;
			case 10: setVisible(false);
			GameComponent.setSelected(null);
			break;
			}
		}
	}

	public void setItems(int[] items) {
		this.items = items;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

	public void setTeam(int team) {
		curTeam = team;
	}

	public void setCanThrow(Boolean b) {
		canThrow = b;
	}

	public void setObject(SpaceMan man) {
		displayObj = man;
	}
}
