import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class DefensePlate implements Followable, SolidObject, Weapon {
	public static final int SIZE = 32;
	private double x, y, xVel, yVel;

	private ArrayList<Image> sprite;

	public DefensePlate(double x, double y) {
		this.x = x;
		this.y = y;

		xVel = 0.0;
		yVel = 0.0;


		sprite = new ArrayList<Image>();
		initImage();

	}

	private void initImage() {
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("Weapons.png");
		} catch (IOException e) {
			System.out.println("failed to load weapons sprite");
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		sprite.add(ss.grabSprite(128, 0, 32, 32));
	}


	@Override
	public void setVelocity(double newXVel, double newYVel) {
		xVel = newXVel;
		yVel = newYVel;
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
	public int getSize() {
		return SIZE;
	}

	@Override
	public void update(long diff, SolidObject above, SolidObject under, SolidObject left, SolidObject right, 
			SpaceMan abovePlayer, SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer) {
		if (left == null || right == null )
			x+=xVel*diff/100;
		else 
			xVel = 0;

		if (under == null && underPlayer == null)
			y+=yVel*diff/100;
		else {
			yVel = 0;
			if (under != null)
				y = under.getY()-SIZE;
			else
				y = underPlayer.getY()-SIZE;
		}


		//now update the yVelocity (due to gravity)
		if (under == null && underPlayer == null) 
			yVel+=GameComponent.gravity*(diff/100.0);

	}

	@Override
	public boolean isDead() {
		if (100 < 0) return true;
		else return false;
	}

	@Override
	public void setDirection(double newDir) {
		//N/A
	}

	@Override
	public double getDirection() {
		return 0;
	}

	@Override
	public int getType() {
		return WALL;
	}

	@Override
	public void draw(Graphics2D g2) {

		AffineTransform saveAt = g2.getTransform();
		g2.translate((int) x-SIZE/2, (int) y-SIZE/2);
//		g2.setColor(Color.WHITE);
//		g2.fillRoundRect((int) x-SIZE/2,(int) y-SIZE/2,SIZE,SIZE, 10, 10);
		g2.drawImage(sprite.get(0), null, null);


		g2.setTransform(saveAt);
	}

	@Override
	public boolean isHit(int checkX, int checkY) {
		Rectangle bounding = new Rectangle((int) x-SIZE/2, (int) y-SIZE/2,SIZE,SIZE);
		if (bounding.contains(checkX, checkY)) return true;
		return false;
	}

	@Override
	public int getX() {
		return (int) x;
	}

	@Override
	public int getY() {
		return (int) y;
	}

	@Override
	public void setPosition(int newX, int newY) {
		
		
		x = newX;
		y = newY;
	}

}
