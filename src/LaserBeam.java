import java.awt.Color;
import java.awt.Graphics2D;


public class LaserBeam implements Weapon {

	public static final int SIZE = 4;
	public static final double SPEED = 100.0;
	
	private int x, y;
	private double xVel, yVel;
	private boolean dead;
	private double direction;
	
	public LaserBeam(int x, int y, double direction) {
		this.x = x;
		this.y = y;
		

		this.direction = direction;
		
		xVel = Math.cos(direction*Math.PI/180)*SPEED;
		yVel = -Math.sin(direction*Math.PI/180)*SPEED;
		
		dead = false;
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
		g2.setColor(Color.CYAN);
		g2.fillRect(x-SIZE/2, y-SIZE/2, SIZE, SIZE);
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
	public int getType() {
		return BEAM;
	}

	@Override
	public int getSize() {
		return SIZE;
	}

	@Override
	public void update(long diff, SolidObject above, SolidObject under,
			SolidObject left, SolidObject right, SpaceMan abovePlayer,
			SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer) {
		
		
		//move
		x+=xVel*diff/100;
		y+=yVel*diff/100; 
		
		//check collision
		if ( above != null || under != null || left != null || right != null) {
			dead = true;
			xVel = 0;
			yVel = 0;
		}
	}

	@Override
	public boolean isDead() {
		if (x < 0 || x > GameComponent.LEVEL_WIDTH) dead = true;
		if (y < 0 || y > GameComponent.LEVEL_HEIGHT) dead = true;
		
		return dead;
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
		x = newX;
		y = newY;
	}

}
