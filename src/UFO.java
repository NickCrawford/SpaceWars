import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;


public class UFO implements Followable, Weapon {

	public static final int SIZE = 64;

	public static final double MOVING_SPEED = 0.1;
	
	private int x, y;
	private double direction;
	private SpaceMan targetPlayer;
	
	private ArrayList<Image> sprite;
	
	private Polygon beam;

	private int phase;
	
	private boolean dead;
	
	public UFO(SpaceMan targetPlayer) {
		this.x = (int) Math.cos(targetPlayer.getDirection()+90)*GameComponent.LEVEL_WIDTH;
		this.y = targetPlayer.getY()-SIZE;
				
		phase = 0;
		dead = false;
		
		this.direction = targetPlayer.getDirection();
		this.targetPlayer = targetPlayer;
		
		sprite = new ArrayList<Image>();
		beam = new Polygon();
		beam.addPoint(-SIZE/4, -SIZE/4);
		beam.addPoint(SIZE/4, -SIZE/4);
		beam.addPoint(SIZE/2, SIZE+SIZE/4);
		beam.addPoint(-SIZE/2, SIZE+SIZE/4);
		
		initImage();//x:192, y:0
	}
	
	private void initImage() {
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("Weapons.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		sprite.add(ss.grabSprite(192, 0, 64, 32));

	}
	
	@Override
	public void draw(Graphics2D g2) {
		if (phase == 1) {
			Area outside = calculateRectOutside(beam);
			g2.setClip(outside);
			g2.setColor(new Color(0,0,0,200));
			g2.fillRect(0, 0, 1600, 1200);
			g2.setClip(null);
		}
		
		AffineTransform saveAt = g2.getTransform();
		g2.translate(x, y);
		if (phase == 1) {
			g2.setColor(new Color(0, 255, 255, 128));
			g2.fill(beam);
		}
		g2.setTransform(saveAt);
		
		
		g2.rotate((direction-90) * Math.PI/180, x, y);
		g2.translate((int) x-SIZE/2, (int) y-SIZE/2);
	
		g2.drawImage(sprite.get(0), null, null);
		
		g2.setTransform(saveAt);
		
		g2.setColor(Color.WHITE);
		//g2.drawString("Phase "+phase +" x: "+x, x, y-32);
		
	}

	 private Area calculateRectOutside(Shape s) {
	    	Area outside = new Area(new Rectangle(0, 0, 1600, 1200));
	    	outside.subtract(new Area(s));
	    	return outside;
	    }
	
	@Override
	public void setVelocity(double newXVel, double newYVel) {
		
	}

	@Override
	public double getXVel() {
		return 0;
	}

	@Override
	public double getYVel() {
		return 0;
	}

	@Override
	public int getType() {
		return UFO;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void update(long diff, SolidObject above, SolidObject under, SolidObject left, SolidObject right, 
			SpaceMan abovePlayer, SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer) {
		
		if (phase == 0) {///move to player location
			double xSpeed = (targetPlayer.getX() - x+SIZE/4) * MOVING_SPEED;
			x += xSpeed*(diff/100.0);
			
			if (Math.abs(targetPlayer.getX() - x) < 4)
				x = targetPlayer.getX();
			
			if (x == targetPlayer.getX()) {
				targetPlayer.setDrawHealth(false);
				phase++;
			}
		}
		
		if (phase == 1) {///"Suck" up the player
			if (Math.abs(y-SIZE/4 - targetPlayer.getY()) > 2) {
				targetPlayer.setVelocity(targetPlayer.getXVel(),0.25);
				targetPlayer.setDirection(targetPlayer.getDirection() + 1);
			} else {
				phase ++;
			}
		}
		
		int endX = x+200;
		
		if (phase == 2) {//kill player
			targetPlayer.setDirection(90);
			targetPlayer.setHealth(0);
			targetPlayer.setDrawHealth(true);
			phase++;
		}
		
		if (phase == 3) { ///return player dead body back to his location
			
			if (x < endX) {
				double xSpeed = (endX - (x+SIZE/4)) * MOVING_SPEED;
				x += xSpeed*(diff/100.0);
			} else {
				x += diff/100;
			}
			
			if (x > GameComponent.LEVEL_WIDTH) {
				dead=true;
			}
		}
		
	}

	@Override
	public boolean isDead() {
		return dead;
	}

	@Override
	public void setDirection(double newDir) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getDirection() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPosition(int newX, int newY) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

}
