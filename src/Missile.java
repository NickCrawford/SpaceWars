import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Timer;


public class Missile implements Followable, Weapon {

	public static final int SIZE = 32;

	public static final double DECAY_RATE = 2;
	
	private int x, y;
	private double xVel, yVel, direction;
	
	private static double health;
	
	private ArrayList<Image> sprite;
	private ArrayList<Image> flames;

	private int imageIndex;

	private int maxImageIndex;
	
	private Timer animation;
	
	public Missile(int x, int y) {
		this.x = x;
		this.y = y;
		
		xVel = 0.0;
		yVel = 0.0;
		
		direction = 90;
		health = 100.0;
	
		sprite = new ArrayList<Image>();
		flames = new ArrayList<Image>();
		
		initImage();
		
		imageIndex = 0;//set the sprite being displayed to the first one in the array.
		maxImageIndex = 3;
		
		//set up the timer that controls animation
		ActionListener act = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (imageIndex < maxImageIndex)
					imageIndex ++;
				else
					imageIndex = 0;
			}
		};
		
		animation = new Timer(100, act);
		animation.setInitialDelay(0);

	}


	private void initImage() {
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("Weapons.png");
		} catch (IOException e) {
			System.out.println("failed to load weapons images");
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		sprite.add(ss.grabSprite(32, 0, 32, 32));

		flames.add(ss.grabSprite(32, 32, 8, 8));
		flames.add(ss.grabSprite(32, 40, 8, 8));
		flames.add(ss.grabSprite(32, 48, 8, 8));
		flames.add(ss.grabSprite(32, 56, 8, 8));
	}
	
	
	@Override
	public void draw(Graphics2D g2) {

		AffineTransform saveAt = g2.getTransform();

		//draw missile
		g2.rotate((-direction) * Math.PI/180, x, y);
		g2.translate((int) x-SIZE/2, (int) y-SIZE/2);

		g2.drawImage(sprite.get(0), null, null);
		
		g2.setTransform(saveAt);
		
		//draw flames
		g2.rotate((-direction) * Math.PI/180, x, y);
		g2.translate((int) x-SIZE/2-8, (int) y-SIZE/2+11);
		
		g2.drawImage(flames.get(imageIndex), null, null);

		g2.setTransform(saveAt);

		
		if (xVel != 0 && yVel != 0) {
			//draw health bar
			g2.setColor(Color.BLACK);
			g2.fillRect((int) x-SIZE/2, (int) y-SIZE, SIZE, SIZE/8);

			Color[] colors = {Color.GREEN.darker(), Color.GREEN, Color.GREEN.darker()};
			LinearGradientPaint p = new LinearGradientPaint((int) x, (int) y-SIZE, (int) x, (int) y-SIZE+4, new float[] {0.1f, 0.2f, 0.9f}, colors);

			g2.setPaint(p);
			g2.fillRect((int) x-SIZE/2, (int) y-SIZE, (int) (health/100.0*SIZE), SIZE/8);
			g2.setColor(Color.BLACK);
			g2.drawRect((int) x-SIZE/2, (int) y-SIZE, SIZE, SIZE/8);
		}
		
		////debug
//		g2.setColor(Color.GREEN);
//		g2.drawRect((int) (x-SIZE/2), (int) (y-SIZE/2), 32, 32);
//		g2.drawRect((int) x-1, (int) y-1, 2, 2);
	}
	

	@Override
	public void update(long diff, SolidObject above, SolidObject under,SolidObject left, SolidObject right, 
			SpaceMan abovePlayer, SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer) {
		if (xVel != 0 || yVel != 0) {
			//only deduct health if moving
			health -= DECAY_RATE * (diff/100.0);
		}
		
		//move
		x+=xVel*diff/100;
		y+=yVel*diff/100;

		//fix angle over 360
		if (direction % 360 == 0) direction = 0; 
		
		//check collision
		if ((health < 95) && (above != null || under != null || left != null || right != null || 
				abovePlayer != null || underPlayer != null || leftPlayer != null || rightPlayer != null)) {
			health = 0;
		}
	}

	@Override
	public void setVelocity(double newXVel, double newYVel) {
		xVel = newXVel;
		yVel = newYVel;
		animation.start();
	}
	
	public void setDirection(double newDir) {
		direction = newDir;
	}

	@Override
	public double getXVel() {
		return xVel;
	}

	@Override
	public double getYVel() {
		return yVel;
	}

	@Override
	public int getType() {
		return MISSILE;
	}

	@Override
	public int getSize() {
		return SIZE;
	}
	
	public double getDirection() {
		return direction;
	}

	@Override
	public boolean isDead() {
		if (health <= 0 ) return true;
		
		return false;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}


	@Override
	public void setPosition(int newX, int newY) {
		x = newX;
		y = newY;
	}

	
	public static void kill() {
		health = 0;
	}
}
