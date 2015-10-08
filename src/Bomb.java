import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class Bomb implements Followable, Weapon {

	private int x, y;
	private double xVel, yVel;

	public static final int SIZE = 24;
	
	private static final double SLOW_RATE = 0.05;

	private static final double EPSILON = 1;

	private boolean hasBeenFired;//determines if the bomb has been fired or not
	private boolean dead;//true if the bomb is dead, else false.

	private Ellipse2D.Double shape;
	
	private ArrayList<Image> sprite;

	public Bomb(int x, int y) {
		this.x = x;
		this.y = y;

		xVel = 0.0;
		yVel = 0.0;

		hasBeenFired = false;
		dead = false;

		sprite = new ArrayList<Image>();
		shape = new Ellipse2D.Double(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
		initImage();
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

		sprite.add(ss.grabSprite(0, 0, 24, 24));

	}
	
	public void draw(Graphics2D g2) {
		AffineTransform saveAt = g2.getTransform();
		g2.translate((int) x-SIZE/2, (int) y-SIZE/2);
		
		g2.drawImage(sprite.get(0), null, null);
		
		g2.setTransform(saveAt);
		
	////debug
//			g2.setColor(Color.GREEN);
//			g2.drawRect((int) (x-SIZE/2), (int) (y-SIZE/2), SIZE, SIZE);
//			g2.drawRect((int) x-1, (int) y-1, 2, 2);
	}

	@Override
	public void update(long diff, SolidObject above, SolidObject under, SolidObject left, SolidObject right, 
			SpaceMan abovePlayer, SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer) {
		if (hasBeenFired) {

			//LEFT && RIGHT MOVEMENT
			if (left == null || right == null)
				x+=xVel*diff/100;
			else {
				xVel = 0;
			}

			if (left != null)
				dead = true;
			if (right != null)
				dead = true;
			
			////UP && DOWN MOVEMENT
			if (under == null && above == null) {
				y+=yVel*diff/100;
			} else {
				dead = true;
				yVel = 0;
			}
			
			if (under != null) {
				y = under.getY()-SIZE;
				dead=true;
			}
			if (above != null) {
				y = above.getY()+SIZE;
				dead=true;
			}
			
			//if stopped moving, explode
			if (xVel == 0 && yVel == 0) dead = true;

			//now update the yVelocity (due to gravity)
			if (under == null) 
				yVel+=GameComponent.gravity*(diff/100.0);
			
			if (GameComponent.gravity == 0) {
				if (xVel < -EPSILON || xVel > EPSILON)
					xVel -= xVel*SLOW_RATE*diff/100;
				else
					xVel = 0;

				if (yVel < -EPSILON || yVel > EPSILON)
					yVel -= yVel*SLOW_RATE*diff/100;
				else
					yVel = 0;
			}
		}
		shape.setFrame(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	public int getSize() {
		return SIZE;
	}
	
	public int getType() {
		return BOMB;
	}

	@Override
	public void setVelocity(double xVel, double yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
		hasBeenFired = true;
	}

	@Override
	public double getXVel() {
		return xVel;
	}

	@Override
	public double getYVel() {
		return yVel;
	}

	public boolean isDead() {
		return dead;
	}


	@Override
	public void setDirection(double newDir) {
		////N/A
	}


	@Override
	public double getDirection() {
		//N/A
		return 0;
	}


	@Override
	public void setPosition(int newX, int newY) {
		x = newX;
		y = newY;
	}
}
