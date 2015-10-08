import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Laser implements Weapon {

	public static final int SIZE = 32;
	
	private int x, y;
	private double direction;
	
	private Image base;
	private Image gun;
	
	public Laser(int x, int y) {
		this.x = x;
		this.y = y;
		
		direction = 90;
		
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

		base = ss.grabSprite(64, 32, 32, 32);

		gun = ss.grabSprite(64, 0, 64, 32);
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
	public void draw(Graphics2D g2) {
		AffineTransform saveAt = g2.getTransform();

		//draw gun
		g2.rotate((-direction) * Math.PI/180, x, y+SIZE/2-4);
		g2.translate((int) x-SIZE/2, (int) y);
		g2.translate(-16, -4);
		g2.drawImage(gun, null, null);
		
		g2.setTransform(saveAt);
	
		//draw base
		g2.translate(x-SIZE/2, y-SIZE/2);
		g2.drawImage(base, null, null);
		g2.setTransform(saveAt);
	}

	@Override
	public void setVelocity(double d, double e) {
		
	}

	@Override
	public double getXVel() {
		return 0.0;
	}

	@Override
	public double getYVel() {
		return 0.0;
	}

	@Override
	public int getType() {
		return LASER;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void update(long diff, SolidObject above, SolidObject under,
			SolidObject left, SolidObject right, SpaceMan abovePlayer,
			SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer) {
		
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public void setDirection(double newDir) {
		direction = newDir;
	}

	@Override
	public double getDirection() {
		return direction;
	}

	@Override
	public void setPosition(int newX, int newY) {
		// TODO Auto-generated method stub

	}

}
