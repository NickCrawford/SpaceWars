import java.awt.Graphics2D;


public interface Weapon extends Followable {

	///////WEAPON NUMBERS////////
	
	public static final int BOMB = 0;
	public static final int MISSILE = 1;
	public static final int LASER = 2;
	public static final int WALL = 4;
	public static final int UFO = 7;
	
	public static final int BEAM = 8;
	
	
	

	void draw(Graphics2D g2);

	void setVelocity(double d, double e);

	double getXVel();

	double getYVel();
	
	int getType();
	
	int getSize();

	void update(long diff, SolidObject above, SolidObject under, SolidObject left, SolidObject right, 
			SpaceMan abovePlayer, SpaceMan underPlayer, SpaceMan leftPlayer, SpaceMan rightPlayer);

	boolean isDead();

	void setDirection(double newDir);

	double getDirection();
	
	void setPosition(int newX, int newY);
	
}
