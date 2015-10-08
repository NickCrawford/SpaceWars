import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Ground implements SolidObject {

	public static final int SIZE = 32;
	private int x, y;
	private int type;

	private Rectangle bounding;

	private Image sprite;

	public Ground(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;

		bounding = new Rectangle(x-16,y-16,32,32);
		initImage();
	}

	private void initImage() {
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("MoonTile.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		SpriteSheet ss = new SpriteSheet(spriteSheet);

		if (type == 0) {
			sprite = ss.grabSprite(0, 0, 32, 32);
		}
		if (type == 1) {
			sprite = ss.grabSprite(32, 0, 32, 32);
		}
		if (type == 2) {
			sprite = ss.grabSprite(64, 0, 32, 32);
		}
		if (type == 3) {
			sprite = ss.grabSprite(0, 32, 32, 32);
		}
		if (type == 4) {
			sprite = ss.grabSprite(32, 32, 32, 32);
		}
		if (type == 5) {
			sprite = ss.grabSprite(64, 32, 32, 32);
		}
		if (type == 6) {
			sprite = ss.grabSprite(0, 64, 32, 32);
		}
		if (type == 7) {
			sprite = ss.grabSprite(32, 64, 32, 32);
		}
		if (type == 8) {
			sprite = ss.grabSprite(64, 64, 32, 32);
		}

	}

	public void draw(Graphics2D g2) {

		AffineTransform saveAt = g2.getTransform();
		g2.translate(x-SIZE/2, y-SIZE/2);
		g2.drawImage(sprite, null, null);
		g2.setTransform(saveAt);
		
		////debug
		//		g2.setColor(Color.GREEN);
		//		g2.drawRect((int) (x-SIZE/2), (int) (y-SIZE/2), 32, 32);
		//		g2.drawRect((int) x-1, (int) y-1, 2, 2);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getType() {
		return type;
	}

	public boolean isHit(int checkX, int checkY) {
		if (bounding.contains(checkX, checkY)) return true;
		return false;
	}

}
