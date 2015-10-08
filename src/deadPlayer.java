import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class deadPlayer {

	public static final int SIZE = 32;
	private double x, y, xVel, yVel;
	private int team;

	private ArrayList<Image> sprite;
	private double direction;

	public deadPlayer(double x, double y, int team, double direction) {
		this.x = x;
		this.y = y;
		this.team = team;

		xVel = 0.0;
		yVel = 0.0;

		this.direction = direction;

		sprite = new ArrayList<Image>();
		initImage();

	}

	private void initImage() {
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("SpaceManDead.png");
		} catch (IOException e) {
			System.out.println("failed to load deadPlayer sprite");
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		sprite.add(ss.grabSprite(0, 0, 32, 32));

	}

	public void draw(Graphics2D g2) {

		AffineTransform saveAt = g2.getTransform();

		g2.rotate((-direction+90) * Math.PI/180, x, y);
		g2.translate((int) x-SIZE/2, (int) y-SIZE/2);
//
//		if (team == GameComponent.RED) g2.setColor(Color.RED);
//		else g2.setColor(Color.BLUE);
//		g2.fillRect((int) x-SIZE/2,(int) y-SIZE/2,SIZE,SIZE);
		g2.drawImage(sprite.get(0), null, null);


		g2.setTransform(saveAt);

	}

	public void update(long diff, SolidObject above, SolidObject under, SolidObject left, SolidObject right) {
		if (left == null || right == null)
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
			yVel+=GameComponent.gravity*(diff/100.0);

		if (direction % 360 == 0) direction = 0; 
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public String toString() {
		String retVal = "deadPlayer[Team:";
		if (team == GameComponent.RED)
			retVal += "red";
		if (team == GameComponent.BLUE)
			retVal += "blue";

		retVal += " x:"+(int)x+" y:"+(int)y+"]";

		return retVal;
	}



}
