import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;


public class WeaponSupply implements Followable {

	public static final int SIZE = 32;
	
	private int x, y;
	private double xVel, yVel;
	private int type;

	private Image backSprite;
	private Image iconSprite;

	private double drawDist;
	private String typeName;
	private boolean hit;
	
	public WeaponSupply() {
		Random rand = new Random();
		
		this.x = (rand.nextInt(GameComponent.LEVEL_WIDTH/32-4)+2)*32+SIZE/2;
		this.y = 0;

		xVel = 0;
		yVel = 0;

		drawDist = 0.0;
		hit = false;
		
		int i;
		do {
			i = rand.nextInt(8);
		} while (i == 3 || i == 5 || i == 6);
		type =  i;

		initImage();
	}

	private void initImage() {
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("Weapons.png");
		} catch (IOException e) {
			System.out.println("failed to load weapon images");
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		backSprite = ss.grabSprite(0, 64, 32, 32);

		if (type == 0) {
			typeName = "Bomb";
			iconSprite = ss.grabSprite(0, 0, 24, 24);
		}
		if (type == 1) {
			typeName = "Missile";
			iconSprite = ss.grabSprite(32, 0, 32, 32);
		}
		if (type == 2) {
			typeName = "Laser Cannon";
			iconSprite = ss.grabSprite(128, 64, 40, 32);
		}
		if (type == 4) {
			typeName = "Defense Wall";
			iconSprite = ss.grabSprite(128, 0, 32, 32);
		}
		if (type == 7) {
			typeName = "UFO";
			iconSprite = ss.grabSprite(192, 0, 64, 32);
		}
	}
	
	public void draw(Graphics2D g2) {
		AffineTransform saveAt = g2.getTransform();
		g2.translate(x-SIZE/2, y-SIZE/2);
		
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1.0 - drawDist/16));
		g2.setComposite(ac);
		
		g2.drawImage(backSprite, null, null);
		
		
		
		g2.setTransform(saveAt);
		
		if (type == 0)
			g2.drawImage(iconSprite, x-SIZE/4, y-SIZE/4, (int) (iconSprite.getWidth(null)/1.5), (int) (iconSprite.getHeight(null)/1.5),  null);
		
		if (type == 1 || type == 2 || type == 4)
			g2.drawImage(iconSprite, x-SIZE/4, y-SIZE/4, iconSprite.getWidth(null)/2, iconSprite.getHeight(null)/2,  null);
		
		if (type == 7)
			g2.drawImage(iconSprite, x-SIZE/4, y-SIZE/4, iconSprite.getWidth(null)/4, iconSprite.getHeight(null)/2,  null);
		
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (1.0));
		g2.setComposite(ac);
		
		if (hit) {
			g2.setColor(Color.WHITE);
			g2.drawString("+1 " + typeName, (int) (x-SIZE/2-typeName.length()-typeName.length()/2), (int) (y-drawDist-SIZE/2));
		}
		
	}
	
	
	public void update(long diff, SolidObject above, SolidObject under, SolidObject left, SolidObject right, 
			SpaceMan abovePlayer, SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer) {
		if (left == null || right == null )
			x+=xVel*diff/100;
		else 
			xVel = 0;

		if (under == null)
			y+=yVel*diff/100;
		else {
			yVel = 0;
			y = under.getY()-SIZE;
		}


		//now update the yVelocity (due to gravity)
		if (under == null) 
			if (yVel < 20)
				yVel+=GameComponent.gravity*(diff/100.0);
			else
				yVel = 20;

		
		

		if (!hit) {
			if (abovePlayer != null) {
				hit = true;
				int[] items = abovePlayer.getItems();
				items[type] ++;
			} else if (underPlayer != null) {
				hit = true;
				int[] items = underPlayer.getItems();
				items[type] ++;
			} else if (leftPlayer != null) {
				hit = true;
				int[] items = leftPlayer.getItems();
				items[type] ++;
			} else if (rightPlayer != null) {
				hit = true;
				int[] items = rightPlayer.getItems();
				items[type] ++;
			}
		}
		
		if (hit) {
			drawDist += 0.4;
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isDead() {
		if (drawDist > 16.0) return true;
		return false;
	}
}
