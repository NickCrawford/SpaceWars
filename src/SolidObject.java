

import java.awt.Graphics2D;

public interface SolidObject {

	public int SIZE = 32;
	public int getType();
	public int getX();
	public int getY();
	
	public void draw(Graphics2D g2);
	public boolean isHit(int x, int y);
	
}
