import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class Final {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Final");
		frame.setSize(800,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);


		long curTime = System.currentTimeMillis();
		MenuComponent menu = new MenuComponent(curTime);
		frame.addKeyListener(menu);
		
		frame.add(menu);

		frame.setVisible(true);

			int level = 0;

			while(level == 0) {

				curTime = System.currentTimeMillis();
				level = menu.getLevel();

				frame.repaint();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			frame.remove(menu);

			GameComponent game = new GameComponent(level, curTime, frame.getInsets().top);
			frame.addKeyListener(game);
			frame.addMouseListener(game);
			frame.add(game);
			frame.setVisible(true);

			while(game.isGameOver() == -1) {

				curTime = System.currentTimeMillis();
				game.update(curTime);

				frame.repaint();
				try {
					Thread.sleep(33);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			String teamName = "";
			if (game.isGameOver() == GameComponent.RED) {
				teamName = "RED";
			} else {
				teamName = "BLUE";
			}

			
			JOptionPane.showMessageDialog(frame, "Game Over! The "+teamName+ " team won!\nThanks for playing!");
			
			frame.remove(game);
			System.exit(0);
		
	}

}
