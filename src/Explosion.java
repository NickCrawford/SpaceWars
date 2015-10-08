import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;


public class Explosion implements SolidObject {

	private int x, y, maxRadius, aliveTime;
	private Color color;
	
	private double damage;
	
	private int curRadius;// the current radius of the explosion
	
	private Timer updateRadius;
	
	private Rectangle bounding;//the bounding box of the explosion
	
	private boolean alive;
	
	public Explosion(int x, int y, int radius, Color color, int aliveTime, double damage) {
		this.x = x;
		this.y = y;
		this.maxRadius = radius;
		this.color = color;
		this.aliveTime = aliveTime;
		this.damage = damage;
		
		curRadius = 0;
		alive = true;
		
		bounding = new Rectangle(x-maxRadius, y-maxRadius, maxRadius*2, maxRadius*2);

		//set up the timer that controls animation
		ActionListener act = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (curRadius < maxRadius)
					curRadius ++;
				else
					alive = false;
			}
		};

		updateRadius = new Timer(maxRadius*aliveTime/1000, act);
		updateRadius.setInitialDelay(0);
		
		updateRadius.start();
	}
	
	@Override
	public int getType() {
		return 0;
	}

	@Override
	public int getX() {
		return x+maxRadius;
	}

	@Override
	public int getY() {
		return y+maxRadius;
	}

	public double getDamage() {
		return damage;
	}
	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.fillOval(x-curRadius, y-curRadius, curRadius*2, curRadius*2);
		
		
//		/////DEBUG//////
//		g2.setColor(Color.GREEN);
//		g2.drawOval(x-maxRadius, y-maxRadius, maxRadius*2, maxRadius*2);
	}

	@Override
	public boolean isHit(int x, int y) {
		if (bounding.contains(x, y)) return true;
		return false;
	}

	public boolean isAlive() {
		return alive;
	}
}
