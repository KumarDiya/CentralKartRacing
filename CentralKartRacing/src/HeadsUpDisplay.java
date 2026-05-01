import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class HeadsUpDisplay extends JPanel{
    //everything will be drawn on a jpane that is on top of the main game 
    
        int time = 0;
        final int panW = 300, panH = 150; //arbitrary dimensions for the HUD
		
        HeadsUpDisplay() {
			this.setPreferredSize(new Dimension(panW, panH));
		}

    // public void drawHUD(){
    //     drawTimer();
    //     drawBoostBar();
    // }

    

    private void drawBoostBar(){

    }
    private void drawTimer(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        
        //Set the font and print the time
        g2.setFont(new Font("Arial", Font.BOLD, 100));
        g2.setPaint(Color.blue);
        double currentTime = (double)time / 1000;
        
        //use system time

        //This formats the value of currentTime to have 2 decimal places and saves as String
        String displayTime = String.format("%.2f",  currentTime);
        g2.drawString(displayTime, panW/3, panH/2);
    }	
	
    
}



/*

public class TimerCountUp extends JFrame implements ActionListener{
	
	DrawingPanel panel;
	Timer timer;
	final int tSpeed = 1;
	int time = 0;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new TimerCountUp();
			}
		});
	}
	
	TimerCountUp() {
		this.setTitle("Timer");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		panel = new DrawingPanel();
		this.add(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		//Add the KeyListener to the frame
		this.addKeyListener(new KeystrokeListener());
		//Set timer to count up and repaint the panel
		timer = new Timer(tSpeed, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				time++;
				panel.repaint();
			}
		});
		timer.start();	//start the timer
	}
	
	private class DrawingPanel extends JPanel {
		final int panW = 300, panH = 150;
		DrawingPanel() {
			this.setPreferredSize(new Dimension(panW, panH));
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			
			//Set the font and print the time
			g2.setFont(new Font("Arial", Font.BOLD, 50));
			g2.setPaint(Color.blue);
			double currentTime = (double)time / 1000;
			
			//This formats the value of currentTime to have 2 decimal places and saves as String
			String displayTime = String.format("%.2f",  currentTime);
			g2.drawString(displayTime, panW/3, panH/2);
		}	
	}
	private class KeystrokeListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_UP){
				if (timer.isRunning()) timer.stop();
				else{
					time = 0;
					timer.start();
				}
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {}
}
*/