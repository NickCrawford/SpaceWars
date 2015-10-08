import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;


public class Camera  implements Followable{

	public static final int FOLLOW_SPEED = 5;
	public static final int MOVING_SPEED = 20;
	
	private int x, y;
	private Rectangle2D shape;

	private boolean movingUp;
	private boolean movingLeft;
	private boolean movingDown;
	private boolean movingRight;
	private boolean following;


	public Camera(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		shape = new Rectangle2D.Double(x-400, y-300, 800, 600);

		movingUp = false;
		movingDown = false;
		movingLeft = false;
		movingRight = false;

		following = false;
	}

	public void draw(Graphics2D g2) {
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.GREEN);
		g2.draw(shape);
		
		g2.setColor(new Color(255,255,255,100));
		
		
//		if (movingUp) {
//			if (movingLeft) {
//				
//			} else if (movingRight) {
//				
//			} else {
//				g2.fillRect(0,0,800,64);
//			}
//		}
//		
//		if (movingDown) {
//			if (movingLeft) {
//				
//			} else if (movingRight) {
//				
//			} else {
//				g2.fillRect(0,GameComponent.SCREEN_HEIGHT-64,800,64);
//			}
//		}
	}

	public void update(long diff, int width, int height, int targetX, int targetY) {
		//if the camera is following an object
		if (following) {
			double xSpeed = (targetX - x) / FOLLOW_SPEED;
			double ySpeed = (targetY - y) / FOLLOW_SPEED;
			
			x += xSpeed;
		    y += ySpeed;
		}
		
		
		if (movingUp) { //up key pressed?
				y -= MOVING_SPEED*diff/100;
		}

		if (movingDown) { //left key pressed?
			y += MOVING_SPEED*diff/100;
		}

		if (movingLeft) { //down key pressed?			
				x -= MOVING_SPEED*diff/100;
		}

		if (movingRight) { //right key pressed?

				x += MOVING_SPEED*diff/100;
		}

		
		//keep camera in bounds
		if (y < height/2) y = height/2; //top
		if (y > GameComponent.LEVEL_HEIGHT-height/2) y = GameComponent.LEVEL_HEIGHT-height/2;//bottom
		if (x < width/2) x = width/2;//left
		if (x > GameComponent.LEVEL_WIDTH-width/2) x = GameComponent.LEVEL_WIDTH-width/2; //right
		
		

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void startFollowing() {
		following = true;
	}

	public void move(int keyPressed) {
		
		//moving up
		if (keyPressed == KeyEvent.VK_W || keyPressed == KeyEvent.VK_UP) {
			movingUp = true;
			following = false;
		}

		//moving down
		if (keyPressed == KeyEvent.VK_S || keyPressed == KeyEvent.VK_DOWN) {
			movingDown = true;
			following = false;
		}

		//moving left
		if (keyPressed == KeyEvent.VK_A || keyPressed == KeyEvent.VK_LEFT) {
			movingLeft = true;
			following = false;
		}

		//moving right
		if (keyPressed == KeyEvent.VK_D || keyPressed == KeyEvent.VK_RIGHT) {
			movingRight = true;
			following = false;
		}

	}

	public void stopMove(int keyReleased) {

		//moving up
		if (keyReleased == KeyEvent.VK_W || keyReleased == KeyEvent.VK_UP) {
			movingUp = false;
		}

		//moving down
		if (keyReleased == KeyEvent.VK_S || keyReleased == KeyEvent.VK_DOWN) {
			movingDown = false;
		}

		//moving left
		if (keyReleased == KeyEvent.VK_A || keyReleased == KeyEvent.VK_LEFT) {
			movingLeft = false;
		}

		//moving right
		if (keyReleased == KeyEvent.VK_D || keyReleased == KeyEvent.VK_RIGHT) {
			movingRight = false;
		}

	}
}
