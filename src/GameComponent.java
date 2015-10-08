import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.Timer;


public class GameComponent extends JComponent implements KeyListener, MouseListener{

	public static final int LEVEL_WIDTH = 1600;
	public static final int LEVEL_HEIGHT = 1200;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;

	public static final int RED = 0;
	public static final int BLUE = 1;
	public static final double PLAYER_THROW_SPEED = 0.25;//0-1
	public static final double BOMB_THROW_SPEED = 0.2; //0-1
	public static final double MISSILE_SPEED = 15.0;

	public static final double BOMB_DAMAGE = 5;
	public static final double MISSILE_DAMAGE = 6;
	private static final double LASER_DAMAGE = 7.5;


	private BufferedImage backgroundImage;
	private BufferedImage midgroundImage;

	public static double gravity;//the amount o gravity in the level

	private long curTime;//the current time in milliseconds

	private Camera cam;//the camera object for the level

	private ArrayList<SpaceMan> redTeam;
	private ArrayList<SpaceMan> blueTeam;

	private ArrayList<SolidObject> walls;
	private ArrayList<DefensePlate> defensePlates;
	private ArrayList<Explosion> explosion;
	private ArrayList<deadPlayer> deadPlayers;
	private ArrayList<LaserBeam> laserBeams;
	private ArrayList<WeaponSupply> weaponCrates;

	private static Weapon weaponObj;

	private static Menu menu; //menu for selecting moves and weapons;

	private int top;//the top inset of the JFrame
	private Followable followTarget;

	private int mouseX, mouseY;
	private double distToMouse;
	private static int maxDist;//max throwing distance
	private static SpaceMan selected;//the currently selected spaceman

	private static int turn;// the current team that is going (0 - RED, 1 - BLUE)
	private static int weapon;//the current weapon being used

	private static int wallsRemaining;

	private static boolean throwing;//is a player currently being thrown?
	private static boolean attacking;//Is a player currently attacking


	private boolean drawLine;//true if drawing a line from mouse to a selected object
	private double redTeamMaxHealth;
	private double blueTeamMaxHealth;
	private long diff;
	private static boolean ufoSelect;//true if drawing a target by the mouse.

	private static Timer turnTimer;//timer used to delay follow targets in between turns
	private static boolean drawRadius;

	public GameComponent(int level, long curTime, int top) {
		this.top = top;
		this.curTime = curTime;

		menu = new Menu();
		redTeam = new ArrayList<SpaceMan>();
		blueTeam = new ArrayList<SpaceMan>();

		walls = new ArrayList<SolidObject>();
		explosion = new ArrayList<Explosion>();
		deadPlayers = new ArrayList<deadPlayer>();
		defensePlates = new ArrayList<DefensePlate>();
		laserBeams = new ArrayList<LaserBeam>();
		weaponCrates = new ArrayList<WeaponSupply>();

		weaponObj = null;
		wallsRemaining = 0;

		cam = new Camera(400, 300, 800, 600);

		followTarget = cam;
		selected = null;
		weapon = -1;
		throwing = false;//not throwing the player
		attacking = false;
		drawLine = false;//not drawing a line to mouse
		drawRadius = false;
		ufoSelect = false;

		redTeamMaxHealth = 0;
		blueTeamMaxHealth = 0;

		mouseX = 0;
		mouseY = 0;
		distToMouse = 0;

		maxDist = 100;

		ActionListener act = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (turn == RED) {
					if (blueTeam.size()>0)
						followTarget = redTeam.get(0);
				} else {
					if (blueTeam.size()>0)
						followTarget = blueTeam.get(0);
				}
				
				double rand = Math.random();
				if (rand < 0.5) {
					weaponCrates.add(new WeaponSupply());
					followTarget = weaponCrates.get(weaponCrates.size()-1);
				}
				
				cam.startFollowing();
				
				turnTimer.stop();
			}
		};
		turnTimer = new Timer(1000, act);
		diff = 50;
		
		initLevel(level);
	}

	/** Method that initializes all objects for the level depending on which level number the player chose.
	 * 
	 * @param level The level number that the player chose.
	 */
	private void initLevel(int level) {
		String fileName = "";
		if (level == 1) {
			fileName = "MoonMap.txt";
			gravity = 2;
		}
		FileReader reader = null;
		try {
			reader = new FileReader("res/maps/"+fileName);
		} catch (FileNotFoundException e1) {
			System.out.println("ERROR - Could not locate map file: \"MoonMap.txt\"");
			System.exit(0);
		}
		Scanner in = new Scanner(reader);
		ArrayList<String> lines = new ArrayList<String>();
		while(in.hasNext()) {
			lines.add(in.nextLine());
		}

		int x = 0, y = 0;
		for (int i = 0; i < lines.size(); i++) {
			for (int j = 0; j < lines.get(i).length(); j ++) {
				String id = lines.get(i).substring(j,j+1);

				//create ground objects
				if (id.equals("1")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 0));
				if (id.equals("2")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 1));
				if (id.equals("3")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 2));
				if (id.equals("4")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 3));
				if (id.equals("5")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 4));
				if (id.equals("6")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 5));
				if (id.equals("7")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 6));
				if (id.equals("8")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 7));
				if (id.equals("9")) walls.add(new Ground((j*32)+Ground.SIZE/2, i*32, 8));

				//initialize red Team
				if (id.equalsIgnoreCase("R")) {
					redTeamMaxHealth += 100;
					redTeam.add(new SpaceMan((j*32)+SpaceMan.SIZE/2, i*32, RED));
				}

				//initialize Blue Team
				if (id.equalsIgnoreCase("B")) {
					blueTeamMaxHealth += 100;
					blueTeam.add(new SpaceMan((j*32)+SpaceMan.SIZE/2, i*32, BLUE));
				}

			}
		}

		followTarget = redTeam.get(0);
		cam.startFollowing();


		////////  LEVEL 1 Images ////////////
		if (level == 1) {
			///Get images
			BufferedImageLoader loader = new BufferedImageLoader();
			BufferedImage spriteSheet = null;
			try {
				spriteSheet = loader.loadImage("MoonMidGround.png");
			} catch (IOException e) {
				System.out.println("failed to load midground images");
			}
			SpriteSheet ss = new SpriteSheet(spriteSheet);

			midgroundImage = ss.grabSprite(0, 0, 1600, 1200);

			try {
				spriteSheet = loader.loadImage("MoonBackGround.png");
			} catch (IOException e) {
				System.out.println("failed to load background images");
			}
			ss = new SpriteSheet(spriteSheet);

			backgroundImage = ss.grabSprite(0, 0, 1600, 1200);
		}

		////////LEVEL 2 ////////////
		if (level == 2) {
			gravity = 0;

			redTeam.add(new SpaceMan(200, 1100, RED));
			blueTeam.add(new SpaceMan(100, 1100, BLUE));

			followTarget = redTeam.get(0);
			cam.startFollowing();

			for (int i = 0; i < LEVEL_WIDTH; i += 32) {
				walls.add(new Ground(i, LEVEL_HEIGHT, 0));
			}
			for (int i = 0; i < LEVEL_WIDTH; i += 32) {
				walls.add(new Ground(i, 0, 0));
			}

			for (int i = 736; i < 864; i+=32) {
				for (int j = LEVEL_HEIGHT-256; j > LEVEL_HEIGHT-384; j-=32) {
					walls.add(new Ground(i, j-10, 0));
				}
			}
		}

	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.drawImage(backgroundImage, null, null);

		//draw background
		AffineTransform saveAt = g2.getTransform();
		g2.translate(-cam.getX()*0.05, -cam.getY()*0.05);
		g2.drawImage(backgroundImage, null, null);
		g2.setTransform(saveAt);

		//draw midGround
		g2.translate(-cam.getX()*0.05-300, -cam.getY()*0.05);

		g2.drawImage(midgroundImage, null, null);
		g2.setTransform(saveAt);

		///////////DRAW HERE////////////////
		g2.translate(-cam.getX()+400, -cam.getY()+300);//beginning of camera

		//draw RED spacemen
		for (SpaceMan man: redTeam) {
			man.draw(g2);
		}

		//draw BLUE spacemen
		for (SpaceMan man: blueTeam) {
			man.draw(g2);
		}

		//draw supply crates
		for (WeaponSupply ws: weaponCrates) {
			ws.draw(g2);
		}

		//draw ground
		for (SolidObject so: walls) {
			so.draw(g2);
		}

		//draw weapons
		if (weaponObj != null)
			weaponObj.draw(g2);

		//draw Defense walls
		for (DefensePlate dp: defensePlates) {
			dp.draw(g2);
		}

		//draw laser beams
		for (LaserBeam lb: laserBeams) {
			lb.draw(g2);

			//draw line 
			if (weaponObj.getType() == Weapon.LASER) {
				g2.setStroke(new BasicStroke(4));
				g2.setColor(Color.CYAN);
				g2.drawLine(weaponObj.getX(), weaponObj.getY()+Laser.SIZE/4, lb.getX(), lb.getY());
				g2.setStroke(new BasicStroke(1));
			}
		}

		//Selection box around selected player
		ArrayList<SpaceMan> team =  null; 
		if (turn == RED) {
			if (ufoSelect) {
				team = blueTeam;
			} else {
				team = redTeam;
			}
		} 
		if (turn == BLUE) {
			if (ufoSelect) {
				team = redTeam;
			} else {
				team = blueTeam;
			}
		} 

		for (int i = 0; i < team.size(); i++) {
			if (team.get(i).isMouseOver(mouseX+cam.getX()-400, mouseY+cam.getY()-300)) {
				if ((!attacking || !throwing) || ((attacking || throwing) && selected == team.get(i)) ) {
					if (selected == team.get(i) || selected == null) {
						///draw selection box
						if (turn == RED) g2.setColor(Color.RED);
						if (turn == BLUE) g2.setColor(Color.BLUE);
						g2.setStroke(new BasicStroke(4));

						ArrayList<Line2D.Double> selectionBox = new ArrayList<Line2D.Double>();
						//top left corner
						selectionBox.add(new Line2D.Double(team.get(i).getX()-team.get(i).SIZE/2, team.get(i).getY()-team.get(i).SIZE/2, team.get(i).getX()-team.get(i).SIZE/2, team.get(i).getY()-team.get(i).SIZE/4));
						selectionBox.add(new Line2D.Double(team.get(i).getX()-team.get(i).SIZE/2, team.get(i).getY()-team.get(i).SIZE/2, team.get(i).getX()-team.get(i).SIZE/4, team.get(i).getY()-team.get(i).SIZE/2));

						//top right corner 
						selectionBox.add(new Line2D.Double(team.get(i).getX()+team.get(i).SIZE/2, team.get(i).getY()-team.get(i).SIZE/2, team.get(i).getX()+team.get(i).SIZE/2, team.get(i).getY()-team.get(i).SIZE/4));
						selectionBox.add(new Line2D.Double(team.get(i).getX()+team.get(i).SIZE/2, team.get(i).getY()-team.get(i).SIZE/2, team.get(i).getX()+team.get(i).SIZE/4, team.get(i).getY()-team.get(i).SIZE/2));

						//bottom left corner
						selectionBox.add(new Line2D.Double(team.get(i).getX()-team.get(i).SIZE/2, team.get(i).getY()+team.get(i).SIZE/2, team.get(i).getX()-team.get(i).SIZE/2, team.get(i).getY()+team.get(i).SIZE/4));
						selectionBox.add(new Line2D.Double(team.get(i).getX()-team.get(i).SIZE/2, team.get(i).getY()+team.get(i).SIZE/2, team.get(i).getX()-team.get(i).SIZE/4, team.get(i).getY()+team.get(i).SIZE/2));

						//bottom right corner 
						selectionBox.add(new Line2D.Double(team.get(i).getX()+team.get(i).SIZE/2, team.get(i).getY()+team.get(i).SIZE/2, team.get(i).getX()+team.get(i).SIZE/2, team.get(i).getY()+team.get(i).SIZE/4));
						selectionBox.add(new Line2D.Double(team.get(i).getX()+team.get(i).SIZE/2, team.get(i).getY()+team.get(i).SIZE/2, team.get(i).getX()+team.get(i).SIZE/4, team.get(i).getY()+team.get(i).SIZE/2));

						for (Line2D.Double line: selectionBox) {
							g2.draw(line);
						}
						g2.setStroke(new BasicStroke(1));
					}


				}

			} 
		}

		//draw explosions
		for (Explosion ex: explosion) {
			ex.draw(g2);
		}

		//draw dead players
		for (deadPlayer dead: deadPlayers) {
			dead.draw(g2);
		}

		//draw throwing line
		if (drawLine) {
			int objX = 0, objY = 0;
			double multiplier = 1.0;

			if (throwing) {
				objX = selected.getX();
				objY = selected.getY();
				multiplier = PLAYER_THROW_SPEED;

			}
			if (attacking) {
				objX = weaponObj.getX();
				objY = weaponObj.getY();
				multiplier = BOMB_THROW_SPEED;
			}

			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(2));

			double dirX = mouseX - objX + cam.getX()-400;
			double dirY = mouseY - objY + cam.getY()-300;

			double dirLen = Math.sqrt(dirX * dirX + dirY * dirY); // The length of dir
			dirX = dirX / dirLen;
			dirY = dirY / dirLen;

			double lineX = dirX * distToMouse;
			double lineY = dirY * distToMouse;

			//draw line to mouse
			g.drawLine(objX, selected.getY(), objX + (int) lineX, selected.getY() + (int) lineY);

			//draw dotted line away from player
			Stroke dotted = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {10}, 0);
			g2.setStroke(dotted);

			if (gravity == 0) {

				g.drawLine(objX, selected.getY(), objX + (int) (-lineX/multiplier), objY + (int) -(lineY/multiplier));
			} else {
				ArrayList<Point2D.Double> path = getPath(objX, objY,-Math.atan2(-lineY, lineX) * 180 / Math.PI, 
						lineX*multiplier,
						lineY*multiplier, multiplier);

				int[] xPoints = new int[path.size()];
				int[] yPoints = new int[path.size()];
				for (int i = 0; i < path.size(); i ++) {
					xPoints[i] = (int) path.get(i).x;
					yPoints[i] = (int) path.get(i).y;
				}


				g2.drawPolyline(xPoints, yPoints, path.size());

				g2.setStroke(new BasicStroke(1));
			}




			g2.drawString("Velocity: "+(int) distToMouse +" lineX:"+(int)lineX+" lineY:"+(int) lineY+" dirX:"+(int) dirX+" dirY:" +(int) dirY+" Angle:"+(int) (Math.atan2(-lineY, lineX) * 180 / Math.PI), mouseX+cam.getX()-400, mouseY+cam.getY()-300);
		} 

		if (drawRadius) {
			g2.setColor(new Color(255,255,255, 100));
			g2.fillOval(selected.getX()- maxDist/2, selected.getY() - maxDist/2, maxDist, maxDist);
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(3));
			g2.drawOval(selected.getX()- maxDist/2, selected.getY() - maxDist/2, maxDist, maxDist);
		}

		//debug
		//		g2.setColor(Color.RED);
		//		g2.drawRect(2, 2, LEVEL_WIDTH-2, LEVEL_HEIGHT-2);
		//		g2.drawLine(2, 2, LEVEL_WIDTH-2, LEVEL_HEIGHT-2);
		//		g2.drawLine(2, LEVEL_HEIGHT-2, LEVEL_WIDTH-2, 2);

		g2.translate(cam.getX()-400, cam.getY()-300);//end of camera



		//draw HUD
		if (turn == RED) g2.setColor(Color.RED);
		if (turn == BLUE) g2.setColor(Color.BLUE);
		g2.setFont(new Font("Impact", 2, 24));
		g2.drawString((turn == 0?"RED":"BLUE"), 16,32);

		g2.setColor(Color.WHITE);
		g2.setFont(new Font("Impact", 0, 24));
		g2.drawString((turn == 0?"":"   ")+"team's turn.", 64,32);

		//minimap
		g2.setColor(new Color(128,128,128,128));
		g2.fillRect(SCREEN_WIDTH-200-16, 16, 200, 150);
		g2.setStroke(new BasicStroke(2));

		//draw walls
		g2.setColor(new Color(0,0,0, 128));
		for (SolidObject so: walls) {
			g2.drawRect(SCREEN_WIDTH-200-16+so.getX()/8, 16+so.getY()/8, 2,2);
		}

		//draw red players
		g2.setColor(new Color(255,0,0, 255));
		for (SpaceMan r: redTeam) {
			g2.drawRect(SCREEN_WIDTH-200-16+r.getX()/8, 16+r.getY()/8, 2,2);
		}

		//draw blue players
		g2.setColor(new Color(0,0,255, 255));
		for (SpaceMan b: blueTeam) {
			g2.drawRect(SCREEN_WIDTH-200-16+b.getX()/8, 16+b.getY()/8, 2,2);
		}

		//draw supply crates
		g2.setColor(new Color(255,255,255, 255));
		for (WeaponSupply ws: weaponCrates) {
			g2.drawRect(SCREEN_WIDTH-200-16+ws.getX()/8, 16+ws.getY()/8, 2,2);
		}

		g2.setColor(new Color(128,128,128,255));
		g2.setStroke(new BasicStroke(2));
		g2.drawRect(SCREEN_WIDTH-200-16, 16, 200, 150);

		///draw health bars
		double redTeamHealth = 0;
		double blueTeamHealth = 0;

		for (SpaceMan s: redTeam) {
			redTeamHealth += s.getHealth();
		}

		for (SpaceMan s: blueTeam) {
			blueTeamHealth += s.getHealth();
		}

		g2.setColor(Color.BLACK);
		g2.fillRect(32, SCREEN_HEIGHT-64, 200, 32);
		g2.fillRect(SCREEN_WIDTH-200-32, SCREEN_HEIGHT-64, 200, 32);

		g2.setColor(Color.RED);
		g2.fillRect(32, SCREEN_HEIGHT-64,(int) (redTeamHealth*100/redTeamMaxHealth)*2, 32);

		g2.setColor(Color.BLUE);
		g2.fillRect(SCREEN_WIDTH-200-32, SCREEN_HEIGHT-64,(int) (blueTeamHealth*100/blueTeamMaxHealth)*2, 32);

		g2.setColor(Color.WHITE);
		g2.drawRect(32, SCREEN_HEIGHT-64, 200, 32);
		g2.drawRect(SCREEN_WIDTH-200-32, SCREEN_HEIGHT-64, 200, 32);
		//draw Camera
		g2.setColor(Color.GREEN);
		g2.drawRect(SCREEN_WIDTH-200-16+(cam.getX()-400)/8, 16+(cam.getY()-300)/8, 100, 75);


		//draw menu
		menu.draw(g2);

		///////////debugging///////
//				cam.draw(g2);
//				g2.setColor(Color.GREEN);
//				g2.setStroke(new BasicStroke(1));
//				g2.drawLine(getWidth()/2, 0, getWidth()/2, getHeight());
//				g2.drawLine(0, getHeight()/2+top, getWidth(), getHeight()/2+top);
//				g2.fillOval(mouseX-4, mouseY-4, 8, 8);//debug cursor
//				g2.setColor(Color.WHITE);
//				g2.drawString("Camera: x: "+cam.getX()+"\ty: "+cam.getY(), 16, 16);
//				g2.drawString("Follow target: "+followTarget, 16, 32);
//		
//				g2.drawString("Mouse: x: "+mouseX+"\ty: "+mouseY, 16, 64);
//				if (selected != null) g2.drawString("Selected: "+selected.toString(), 16, 80);
//				if (weaponObj != null) g2.drawString("weaponObj: "+weaponObj.toString(), 64, 80);
//		
//				g2.drawString("Turn: "+turn, 16, 96);
//				g2.drawString("Throwing: "+throwing, 16, 112);
//				g2.drawString("Attacking: "+attacking, 16, 128);

	}

	private ArrayList<Point2D.Double> getPath(int startX, int startY, double angle, double xVel, double yVel, double multiplier) {
		double xCor = startX;
		double yCor = startY;

		if (xVel > 0) {
			xVel = -xVel;
		}
		if (angle < 0) {
			angle = -angle;
		}

		ArrayList<Point2D.Double> points = new ArrayList<Point2D.Double>();
		for (int i = 0; i < multiplier*75; i++) {
			xCor += Math.cos(angle*Math.PI/180)*xVel;
			yCor -= Math.sin(angle*Math.PI/180)*yVel;
			yVel -= gravity*diff/75.0;

			points.add(new Point2D.Double(xCor, yCor));
		}
		return points;
	}

	public void update(long nextCurTime) {
		long elapsedTime = nextCurTime - curTime;

		diff = elapsedTime;
		cam.update(elapsedTime, getWidth(), getHeight(), followTarget.getX(), followTarget.getY());

		if (selected != null)
			distToMouse = Math.sqrt(Math.pow(selected.getX()-cam.getX()+400 - mouseX,2) + Math.pow(selected.getY()-cam.getY()+300 - mouseY, 2));

		if (distToMouse > maxDist) distToMouse = maxDist;


		//UPDATE RED TEAM SPACEMEN
		for (SpaceMan man: redTeam) {
			int checkX = man.getX();
			int checkY = man.getY();

			man.update(elapsedTime, 
					checkSolidObject(checkX, checkY-man.SIZE/2-1),
					checkSolidObject(checkX, checkY+man.SIZE/2),
					checkSolidObject(checkX-man.SIZE/2-1, checkY),
					checkSolidObject(checkX+man.SIZE/2+1, checkY));
		}

		//UPDATE BLUE TEAM SPACEMEN
		for (SpaceMan man: blueTeam) {
			int checkX = man.getX();
			int checkY = man.getY();

			man.update(elapsedTime, 
					checkSolidObject(checkX, checkY-man.SIZE/2-1),
					checkSolidObject(checkX, checkY+man.SIZE/2),
					checkSolidObject(checkX-man.SIZE/2, checkY),
					checkSolidObject(checkX+man.SIZE/2, checkY));
		}

		//Update dead players
		for (deadPlayer dp: deadPlayers) {
			int checkX =(int) dp.getX()+dp.SIZE/2;
			int checkY =(int) dp.getY()+dp.SIZE/2;

			dp.update(elapsedTime, 
					checkSolidObject(checkX, checkY-1),
					checkSolidObject(checkX, checkY+1),
					checkSolidObject(checkX-1, checkY),
					checkSolidObject(checkX+1, checkY));
		}

		//update weapon object and check its surrounding objects
		if (weaponObj != null) {
			/////update bomb weapon
			if (weaponObj.getType() == Weapon.BOMB) {
				int checkX = weaponObj.getX();
				int checkY = weaponObj.getY();


				weaponObj.update(elapsedTime, 
						checkSolidObject(checkX, checkY-Bomb.SIZE/2-1),
						checkSolidObject(checkX, checkY+Bomb.SIZE/2+1),
						checkSolidObject(checkX-Bomb.SIZE/2-1, checkY),
						checkSolidObject(checkX+Bomb.SIZE/2+1, checkY),
						checkSpaceMan(checkX, checkY-Bomb.SIZE/2-1),
						checkSpaceMan(checkX, checkY+Bomb.SIZE/2+1),
						checkSpaceMan(checkX-Bomb.SIZE/2-1, checkY),
						checkSpaceMan(checkX+Bomb.SIZE/2+1, checkY));
			}

			////update missile weapon
			if (weaponObj.getType() == Weapon.MISSILE) {
				int checkX = weaponObj.getX();
				int checkY = weaponObj.getY();


				weaponObj.update(elapsedTime, 
						checkSolidObject(checkX, checkY-Missile.SIZE/3),
						checkSolidObject(checkX, checkY+Missile.SIZE/3),
						checkSolidObject(checkX-Missile.SIZE/3, checkY),
						checkSolidObject(checkX+Missile.SIZE/3, checkY),
						checkSpaceMan(checkX, checkY-Missile.SIZE/3),
						checkSpaceMan(checkX, checkY+Missile.SIZE/3),
						checkSpaceMan(checkX-Missile.SIZE/3, checkY),
						checkSpaceMan(checkX+Missile.SIZE/3, checkY));


				double dirX = mouseX - weaponObj.getX() + cam.getX()-400;
				double dirY = mouseY - weaponObj.getY() + cam.getY()-300;

				double dirLen = Math.sqrt(dirX * dirX + dirY * dirY); // The length of dir
				dirX = dirX / dirLen;
				dirY = dirY / dirLen;

				double lineX = dirX * distToMouse;
				double lineY = dirY * distToMouse;

				double newDir = Math.atan2(-lineY, lineX) * 180 / Math.PI;

				weaponObj.setDirection(newDir);
				if (weaponObj.getXVel() != 0 && weaponObj.getYVel() != 0 && weaponObj.getDirection() != 0) {
					double dir = weaponObj.getDirection();

					double newXVel = Math.cos(dir*Math.PI/180)*MISSILE_SPEED;
					double newYVel = -Math.sin(dir*Math.PI/180)*MISSILE_SPEED;
					if (Math.sqrt(Math.pow(weaponObj.getX()-cam.getX()+400 - mouseX,2) + Math.pow(weaponObj.getY()-cam.getY()+300 - mouseY, 2)) > Missile.SIZE/2)
						weaponObj.setVelocity(newXVel, newYVel);
					else 
						weaponObj.setVelocity(weaponObj.getXVel()/1.2, weaponObj.getYVel()/1.2);

				}
			}

			if (weaponObj.getType() == Weapon.LASER) {
				double dirX = mouseX - weaponObj.getX() + cam.getX()-400;
				double dirY = mouseY - weaponObj.getY() + cam.getY()-300-4;

				double dirLen = Math.sqrt(dirX * dirX + dirY * dirY); // The length of dir
				dirX = dirX / dirLen;
				dirY = dirY / dirLen;

				double lineX = dirX * distToMouse;
				double lineY = dirY * distToMouse;

				double newDir = Math.atan2(-lineY, lineX) * 180 / Math.PI;

				weaponObj.setDirection(newDir);
			}


			/////update wall weapon
			if (weaponObj.getType() == Weapon.WALL) {

				weaponObj.setPosition(mouseX+cam.getX()-400, mouseY+cam.getY()-300);

			}



			/////update UFO weapon
			if (weaponObj.getType() == Weapon.UFO) {

				weaponObj.update(elapsedTime, null,null,null,null,null,null,null,null);

			}

			if (weaponObj.isDead()) {
				if (weaponObj.getType() == Weapon.BOMB)
					explosion.add(new Explosion(weaponObj.getX(), weaponObj.getY(), 32, Color.ORANGE, 250, BOMB_DAMAGE));
				if (weaponObj.getType() == Weapon.MISSILE) {
					explosion.add(new Explosion(weaponObj.getX(), weaponObj.getY(), 48, Color.YELLOW, 200, MISSILE_DAMAGE));
					attacking = false;
				}
				if (weaponObj.getType() == Weapon.UFO) {
					attacking = false;
				}
				changeTurn();
				weaponObj = null;
			}
		}

		///Update explosion and check for player collision
		for (int i = 0; i < explosion.size(); i++) {

			for (SpaceMan man: redTeam) {
				if (explosion.get(i).isHit(man.getX(), man.getY())) {
					man.setHealth(man.getHealth()-explosion.get(i).getDamage());
				}
			}
			for (SpaceMan man: blueTeam) {
				if (explosion.get(i).isHit(man.getX(), man.getY())) {
					man.setHealth(man.getHealth()-explosion.get(i).getDamage());
				}
			}

			for (int j = 0; j < defensePlates.size(); j++) {
				if (explosion.get(i).isHit(defensePlates.get(j).getX(), defensePlates.get(j).getY())) {
					defensePlates.remove(j);
					j--;
				}
			}

			if (!explosion.get(i).isAlive()) 
				explosion.remove(i);

		}

		//////check for dead players
		for (int i = 0; i <redTeam.size(); i ++) { ///red team
			if (redTeam.get(i).getHealth() <= 0) {
				deadPlayers.add(new deadPlayer(redTeam.get(i).getX(), redTeam.get(i).getY(), RED, redTeam.get(i).getDirection()));
				redTeam.remove(i);
				i--;
			}
		}

		for (int i = 0; i <blueTeam.size(); i ++) { ///blue team
			if (blueTeam.get(i).getHealth() <= 0) {
				deadPlayers.add(new deadPlayer(blueTeam.get(i).getX(), blueTeam.get(i).getY(), BLUE, blueTeam.get(i).getDirection()));
				blueTeam.remove(i);
				i--;
			}
		}

		////update defense walls 
		for (DefensePlate dp: defensePlates) {
			int checkX = dp.getX();
			int checkY = dp.getY();


			dp.update(elapsedTime, 
					checkSolidObject(checkX, checkY-DefensePlate.SIZE/2),
					checkSolidObject(checkX, checkY+DefensePlate.SIZE/2),
					checkSolidObject(checkX-DefensePlate.SIZE/2, checkY),
					checkSolidObject(checkX+DefensePlate.SIZE/2, checkY),
					checkSpaceMan(checkX, checkY-DefensePlate.SIZE/2),
					checkSpaceMan(checkX, checkY+DefensePlate.SIZE),
					checkSpaceMan(checkX-DefensePlate.SIZE/2, checkY),
					checkSpaceMan(checkX+DefensePlate.SIZE/2, checkY));
		}

		//update weapon crates
		for (int i = 0; i < weaponCrates.size(); i++) {
			int checkX = weaponCrates.get(i).getX();
			int checkY = weaponCrates.get(i).getY();


			weaponCrates.get(i).update(elapsedTime, 
					checkSolidObject(checkX, checkY-WeaponSupply.SIZE/2),
					checkSolidObject(checkX, checkY+WeaponSupply.SIZE/2),
					checkSolidObject(checkX-WeaponSupply.SIZE/2, checkY),
					checkSolidObject(checkX+WeaponSupply.SIZE/2, checkY),
					checkSpaceMan(checkX, checkY-WeaponSupply.SIZE/2),
					checkSpaceMan(checkX, checkY+WeaponSupply.SIZE),
					checkSpaceMan(checkX-WeaponSupply.SIZE/2, checkY),
					checkSpaceMan(checkX+WeaponSupply.SIZE/2, checkY));
			
			if (weaponCrates.get(i).isDead()) {
				weaponCrates.remove(i);
				i --;
			}
		}

		//update laser beams
		for (int i = 0; i < laserBeams.size(); i ++) {
			int checkX = laserBeams.get(i).getX();
			int checkY = laserBeams.get(i).getY();

			if (laserBeams.get(i).isDead()) {
				explosion.add(new Explosion(laserBeams.get(i).getX(), laserBeams.get(i).getY(), 48, Color.CYAN, 50, LASER_DAMAGE));
				laserBeams.remove(laserBeams.get(i));
				attacking = false;
				weaponObj = null;
				changeTurn();
				break;
			}

			laserBeams.get(i).update(elapsedTime, 
					checkSolidObject(checkX, checkY),
					checkSolidObject(checkX, checkY),
					checkSolidObject(checkX, checkY),
					checkSolidObject(checkX, checkY),
					checkSpaceMan(checkX, checkY),
					checkSpaceMan(checkX, checkY),
					checkSpaceMan(checkX, checkY),
					checkSpaceMan(checkX, checkY));


		}

		menu.update(mouseX, mouseY);
		getMouse();
		curTime = nextCurTime;
	}

	public static void changeTurn() {
		selected = null;
		menu.setCanThrow(true);
		if (turn != RED) 
			turn = RED;
		else
			turn = BLUE;
		turnTimer.start();

	}

	public static void setSelected(SpaceMan selected) {
		GameComponent.selected = selected;
	}

	public static void setThrowing(Boolean b) {
		throwing = b;
		if (b) maxDist = 100;
	}

	public static void setAttacking(int newWeapon, int newMaxDist) {
		attacking = true;
		weapon = newWeapon;
		maxDist = newMaxDist;

		if (newWeapon == Weapon.BOMB) {
			weaponObj = new Bomb(selected.getX(), selected.getY());
		}

		if (newWeapon == Weapon.MISSILE){
			weaponObj = new Missile(selected.getX(), selected.getY());
		}

		if (newWeapon == Weapon.LASER){
			weaponObj = new Laser(selected.getX(), selected.getY());
		}

		if (newWeapon == Weapon.WALL){
			wallsRemaining = 3;
			weaponObj = new DefensePlate(selected.getX(), selected.getY());
			drawRadius = true;
		}

		if (newWeapon == Weapon.UFO){
			ufoSelect = true;
			selected = null;
		}
	}

	public void throwPlayer(double newXVel, double newYVel) {
		if (newXVel != 0 && newYVel != 0) {
			selected.setVelocity(selected.getXVel()-newXVel*PLAYER_THROW_SPEED,selected.getYVel()-newYVel*PLAYER_THROW_SPEED);
			followTarget = selected;
			cam.startFollowing();
			throwing = false;
			drawLine = false;
			menu.setCanThrow(false);
		}
	}

	public static boolean isThrowing() {
		return throwing;
	}

	public void throwWeapon(double newXVel, double newYVel) {
		double speed = 1.0;
		if (weapon == Weapon.BOMB){
			speed = BOMB_THROW_SPEED;
		}

		if (newXVel != 0 && newYVel != 0) {
			weaponObj.setVelocity(weaponObj.getXVel()-newXVel*speed,weaponObj.getYVel()-newYVel*speed);
			followTarget = weaponObj;
			cam.startFollowing();
			attacking = false;
			drawLine = false;
		}
	}

	private SolidObject checkSolidObject(int checkX, int checkY) {
		for (SolidObject so: walls) {
			if(so.isHit(checkX, checkY)) return so;
		}
		for (SolidObject so: defensePlates) {
			if(so.isHit(checkX, checkY)) return so;
		}
		return null;
	}

	private SpaceMan checkSpaceMan(int checkX, int checkY) {

		for (SpaceMan s: redTeam) {
			if(s.isHit(checkX, checkY)) return s;
		}

		for (SpaceMan s: blueTeam) {
			if(s.isHit(checkX, checkY)) return s;
		}
		return null;
	}

	private void getMouse() {
		try {
			mouseX = getMousePosition().x;
		} catch (NullPointerException e) {
			mouseX = MouseInfo.getPointerInfo().getLocation().x + getLocation().x;
		}

		try {
			mouseY = getMousePosition().y;
		} catch (NullPointerException e) {
			mouseY = MouseInfo.getPointerInfo().getLocation().y + getLocation().y;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!menu.isVisible()) 
			cam.move(e.getKeyCode());

		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (menu.isVisible()) menu.setVisible(false);
			else System.exit(ABORT);
		}

		/////debug
//		if (e.getKeyCode() == KeyEvent.VK_C) changeTurn(); 
//		if (e.getKeyCode() == KeyEvent.VK_U) weaponCrates.add(new WeaponSupply());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		cam.stopMove(e.getKeyCode());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {


		if (turn == RED) {
			for (int i = 0; i < redTeam.size(); i++) {
				if (redTeam.get(i).isMouseOver(mouseX+cam.getX()-400, mouseY+cam.getY()-300)) {
					if (throwing) {
						if (selected == redTeam.get(i) || selected == null) {
							drawLine = true;
						}
					} 
				}
			}
		}

		if (turn == BLUE) {
			for (int i = 0; i < blueTeam.size(); i++) {
				if (blueTeam.get(i).isMouseOver(mouseX+cam.getX()-400, mouseY+cam.getY()-300)) {
					if (throwing) {
						if (selected == blueTeam.get(i) || selected == null) {
							drawLine = true;
						}
					} 
				}
			}
		}

		if (attacking) {

			//if bomb is selected
			if (weapon == Weapon.BOMB) {
				drawLine = true;
			}

			//if missile is selected
			if (weapon == Weapon.MISSILE && weaponObj != null) {
				if (weaponObj.getXVel() == 0 && weaponObj.getYVel() == 0) {

					double dir = weaponObj.getDirection();

					double newXVel = Math.cos(dir*Math.PI/180)*MISSILE_SPEED;
					double newYVel = -Math.sin(dir*Math.PI/180)*MISSILE_SPEED;

					weaponObj.setVelocity(newXVel, newYVel);
					
					followTarget = weaponObj;
					cam.startFollowing();
				} else {
					Missile.kill();
				}	
			}

			if (weapon == Weapon.LASER && weaponObj != null) {

				laserBeams.add(new LaserBeam(weaponObj.getX(), weaponObj.getY()+Laser.SIZE/4, weaponObj.getDirection()));


				followTarget = weaponObj;
				cam.startFollowing();
			}

			if (weapon == Weapon.WALL) {
				if (Math.sqrt(Math.pow((mouseX+cam.getX()-400) - selected.getX(), 2) + Math.pow((mouseY+cam.getY()-300) - selected.getY(), 2)) < maxDist/2) {
					if (wallsRemaining > 0) {
						defensePlates.add(new DefensePlate(mouseX+cam.getX()-400, mouseY+cam.getY()-300));
						wallsRemaining --;
					} 
					if (wallsRemaining <= 0) {
						changeTurn();
						drawRadius = false;
						attacking = false;
						weaponObj = null;
					}
				}
			}

			if (weapon == Weapon.UFO) {
				if (turn == RED) {
					for (int i = 0; i < blueTeam.size(); i++) {
						if (blueTeam.get(i).isMouseOver(mouseX+cam.getX()-400, mouseY+cam.getY()-300)) {
							weaponObj = new UFO(blueTeam.get(i));
							ufoSelect = false;
							followTarget = weaponObj;
							cam.startFollowing();
						}
					}
				} else {
					for (int i = 0; i < redTeam.size(); i++) {
						if (redTeam.get(i).isMouseOver(mouseX+cam.getX()-400, mouseY+cam.getY()-300)) {
							weaponObj = new UFO(redTeam.get(i));
							ufoSelect = false;
							followTarget = weaponObj;
							cam.startFollowing();
						}
					}
				}

			}


		}


		if (menu.isVisible()) menu.menuClick();


	}

	@Override
	public void mouseReleased(MouseEvent e) {
		//opening menu

		if (turn == RED) {
			for (int i = 0; i < redTeam.size(); i++) {
				if (redTeam.get(i).isMouseOver(mouseX+cam.getX()-400, mouseY+cam.getY()-300)) {
					if (!attacking) {
						if (selected == redTeam.get(i) || selected == null) {
							followTarget = redTeam.get(i);
							cam.startFollowing();
							selected = redTeam.get(i);
							menu.setObject(redTeam.get(i));
							menu.setItems(redTeam.get(i).getItems());
							menu.setVisible(true);
							menu.setTeam(menu.RED);
						}
					}
				}
			}
		}

		if (turn == BLUE) {
			for (int i = 0; i < blueTeam.size(); i++) {
				if (blueTeam.get(i).isMouseOver(mouseX+cam.getX()-400, mouseY+cam.getY()-300)) {
					if (!attacking) {
						if (selected == blueTeam.get(i) || selected == null) {
							followTarget = blueTeam.get(i);
							cam.startFollowing();
							selected = blueTeam.get(i);
							menu.setObject(blueTeam.get(i));
							menu.setItems(blueTeam.get(i).getItems());
							menu.setVisible(true);
							menu.setTeam(menu.BLUE);
						}
					}
				}
			}
		}

		if (throwing && drawLine) {
			throwing = false;

			double dirX = mouseX + cam.getX()-400 - selected.getX();
			double dirY = mouseY + cam.getY()-300 - selected.getY();

			double dirLen = Math.sqrt(dirX * dirX + dirY * dirY); // The length of dir
			dirX = dirX / dirLen;
			dirY = dirY / dirLen;

			double lineX = dirX * distToMouse;
			double lineY = dirY * distToMouse;

			if (distToMouse > 16) {
				throwPlayer(lineX, lineY);
			} else {
				drawLine = false;
			}
		}

		if (attacking && drawLine) {

			//if the current weapon is a bomb
			if (weapon == Weapon.BOMB) {
				attacking = false;

				double dirX = mouseX + cam.getX()-400 - selected.getX();
				double dirY = mouseY + cam.getY()-300 - selected.getY();

				double dirLen = Math.sqrt(dirX * dirX + dirY * dirY); // The length of dir
				dirX = dirX / dirLen;
				dirY = dirY / dirLen;

				double lineX = dirX * distToMouse;
				double lineY = dirY * distToMouse;

				if (distToMouse > 16) {
					throwWeapon(lineX, lineY);
				} else {
					drawLine = false;
				}
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public static void suicidePlayer(final SpaceMan displayObj) {
		displayObj.setHealth(0);
	}

	public int isGameOver() {
		if (redTeam.size() == 0) {
			return BLUE;
		}
		if (blueTeam.size() == 0) {
			return RED;
		}

		return -1;
	}

}
