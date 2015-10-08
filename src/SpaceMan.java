import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class SpaceMan implements Followable {

	public static final int SIZE = 32;

	private static final double SLOW_RATE = 0.05;

	private static final double EPSILON = 0.5;

	private ArrayList<Image> sprite;

	private double x, y;

	private double xVel, yVel;
	private double direction;
	private int[] items;

	private int team;

	private double health;

	private static boolean drawHealth;

	public SpaceMan(double x, double y, int team) {
		this.x = x;
		this.y = y;
		this.team = team;
		
		drawHealth = true;

		xVel = 0.0;
		yVel = 0.0;

		health = 100;

		direction = 90.0;

		sprite = new ArrayList<Image>();
		initImage();
		setInventory();

	}

	private void setInventory() {
		items = new int[12];
		items[0] = 5;//5 bombs
		items[1] = 1;//1 missiles
		items[2] = 0;//1 Laser
		items[4] = 2;//3 sets of defensive walls
		items[7] = 0;//UFO
	}

	private void initImage() {
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("SpaceMan.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		if (team == GameComponent.RED)
			sprite.add(ss.grabSprite(0, 0, 32, 32));
		if (team == GameComponent.BLUE)
			sprite.add(ss.grabSprite(0, 32, 32, 32));

	}

	public void draw(Graphics2D g2) {

		AffineTransform saveAt = g2.getTransform();

		g2.rotate((-direction) * Math.PI/180, x, y);
		g2.translate((int) x-SIZE/2, (int) y-SIZE/2);

		g2.drawImage(sprite.get(0), null, null);


		g2.setTransform(saveAt);

		//draw health bar
		if (drawHealth) {

			g2.setColor(Color.BLACK);
			g2.fillRect((int) x-SIZE/2, (int) y-SIZE, SIZE, SIZE/8);

			LinearGradientPaint p = null;
			if (team == GameComponent.RED) {
				Color[] colors = {Color.RED.darker(), Color.RED, Color.RED.darker()};
				p = new LinearGradientPaint((int) x, (int) y-SIZE, (int) x, (int) y-SIZE+4, new float[] {0.1f, 0.2f, 0.9f}, colors);
			}
			if (team == GameComponent.BLUE) {
				Color[] colors = {Color.BLUE.darker(), Color.BLUE, Color.BLUE.darker()};
				p = new LinearGradientPaint((int)x, (int)y-SIZE, (int) x, (int) y-SIZE+4, new float[] {0.1f, 0.2f, 0.9f}, colors);
			}

			g2.setPaint(p);
			g2.fillRect((int) x-SIZE/2, (int) y-SIZE, (int) (health/100*SIZE), SIZE/8);
			g2.setColor(Color.BLACK);
			g2.drawRect((int) x-SIZE/2, (int) y-SIZE, SIZE, SIZE/8);
		}

		////debug
		//		g2.setColor(Color.GREEN);
		//		g2.drawRect((int) (x-SIZE/2), (int) (y-SIZE/2), 32, 32);
		//		g2.drawRect((int) x-1, (int) y-1, 2, 2);

	}

	public void update(long diff, SolidObject above, SolidObject under, SolidObject left, SolidObject right) {
		if (drawHealth)
			direction = Math.atan2(-yVel, xVel) * 180 / Math.PI;


		//Reorients the player if stopped moving and gravity
		if (GameComponent.gravity != 0) {
			if (direction == 0 && xVel == 0 && yVel == 0) {
				direction = 90;
			}
		} else {
			if (above == null && under == null && left == null && right == null) {
				if (xVel == 0 && yVel == 0) {
					direction = 90.0;
				}
			}

			if (above != null) {
				if (xVel == 0 && yVel == 0) {
					direction = 270.0;
				}
			}
			if (under != null) {
				if (xVel == 0 && yVel == 0) {
					direction = 90;
				}
			}
			if (left != null) {
				if (xVel == 0 && yVel == 0) {
					direction = 0;
				}
			}
			if (right != null) {
				if (xVel == 0 && yVel == 0) {
					direction = 180.0;
				}
			}
		}

		//LEFT && RIGHT MOVEMENT
		if (left == null || right == null)
			x+=xVel*diff/100;
		else {
			xVel = 0;
		}

		if (left != null)
			x = left.getX()+SIZE;
		if (right != null)
			x = right.getX()-SIZE;

		////UP && DOWN MOVEMENT
		if (under == null && above == null) {
			y+=yVel*diff/100;
		} else {
			yVel = 0;
		}

		if (under != null) {
			y = under.getY()-SIZE;
			if (GameComponent.gravity != 0) xVel = 0;
		}
		if (above != null && above.getType() != Weapon.WALL) {
			y = above.getY()+SIZE;
		}


		//now update the yVelocity (due to gravity)
		if (under == null) 
			yVel+=GameComponent.gravity*(diff/100.0);

		if (direction % 360 == 0) direction = 0; 

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

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public double getXVel() {
		return xVel;
	}

	public double getYVel() {
		return yVel;
	}

	public int getTeam() {
		return team;
	}

	public double getDirection() {
		return direction;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public void setVelocity(double xVel, double yVel) {
		y -= 2;
		this.xVel = xVel;
		this.yVel = yVel;
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		if (mouseX>=x-SIZE/2 && mouseX<=x-SIZE/2+SIZE && mouseY >= y-SIZE/2 && mouseY <= y-SIZE/2+SIZE) {
			return true;
		}
		return false;
	}

	public int[] getItems() {
		return items;
	}

	public String toString() {
		String retVal = "SpaceMan[Team:";
		if (team == GameComponent.RED)
			retVal += "red";
		if (team == GameComponent.BLUE)
			retVal += "blue";

		retVal += " x:"+(int)x+" y:"+(int)y+"]";

		return retVal;
	}

	public boolean isHit(int checkX, int checkY) {
		if (checkX>=x && checkX<=x+SIZE && checkY>=y && checkY<=checkY+SIZE) return true;
		return false;
	}

	
	public static void setDrawHealth(boolean b) {
		drawHealth = b;
	}

	public void setDirection(double d) {
		direction = d;
	}

}
