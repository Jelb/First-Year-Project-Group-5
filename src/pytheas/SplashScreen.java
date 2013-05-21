package pytheas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;


/**
 * The splash screen and progress load bar that appears when the program is run.
 */
public class SplashScreen extends JFrame{

	/**
	 * Enums used to describe the allowed tasks for the Loader class
	 */
	public static enum Task{
		COAST, NODES, EDGES, GRAPH, MAP	
	}	
	
	private static SplashScreen instance;
	private static int noOfNodes, noOfEdges, noOfCoastPoints;
	private JProgressBar mainBar, overview;
	private Timer timer;
	private String[] tipArray;
	private JLabel tip, tipTitle;
	private JPanel tippanel;
	private Random random = new Random();
	private boolean[] shown;
	private int displayedTips = 0;
	//Field used to measure the time from system start to
	//the program actually is ready for the user to use.
//	private long startUpTime;
	
	private SplashScreen() {
		super();
//		startUpTime = System.currentTimeMillis();
		setPreferredSize(new Dimension(800, 500));
		setUndecorated(true);
		setUp();
		pack();
		setLocationRelativeTo(null);
		toFront();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Singleton design pattern.
	 * @return		Returns an instance of the object.
	 */
	public static SplashScreen use() {
		if(instance == null) {
			instance = new SplashScreen();
		}
		return instance;
	}
	
	/**
	 * Sets up the different progress bars, the splash and tooltips used.
	 */
	private void setUp() {
		tipArray = new String[] { 
			    "        Click, hold and drag with right mouse button to create zoom area        ", "        Use mouse wheel to zoom in and out        ",
			    "        Click the globe to reset zoom        ", "        Hold mouse over a road to get its name        ", "        Use the arrowkeys to pan around the map        ",
			    "        You can toggle ferry on/off by clicking the ship icon        ", "        Change you transport type by clicking the car/bike icon        ",
			    "        Toggle between location search and path finding by clicking the single pin or multi pin icon        ", "        Pytheas of Massalia was a Greek geographer and explorer        "
			};
		shown = new boolean[tipArray.length];
		int randomNumber = random.nextInt(tipArray.length);
		shown[randomNumber] = true;
		tipTitle = new JLabel("Tips & Tricks:");
		tipTitle.setAlignmentX(CENTER_ALIGNMENT);
		tip = new JLabel(tipArray[randomNumber]);
		tip.setOpaque(false);
		tipTitle.setOpaque(false);
		tip.setAlignmentX(CENTER_ALIGNMENT);
		tippanel = new JPanel();
		tippanel.setLayout(new BoxLayout(tippanel, BoxLayout.Y_AXIS));
		tippanel.add(tipTitle);
		tippanel.add(tip);
		tippanel.setOpaque(true);
		tippanel.setBackground(new Color(255, 255, 255));
		tippanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		
		
		JPanel contentPane = new JPanel(new GridBagLayout());
		JLabel background = new JLabel(new ImageIcon("logo.png"));
		background.setLayout(new GridBagLayout());
		contentPane.add(background);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.weighty = 0.5;
		background.add(Box.createGlue(), gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.weighty = 0.5;
		background.add(Box.createGlue(), gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		background.add(tippanel, gbc);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.weighty = 0.05;
		background.add(Box.createGlue(), gbc);
		
//		gbc = new GridBagConstraints();
//		gbc.gridx = 0;
//		gbc.gridy = 4;
//		gbc.fill = GridBagConstraints.NONE;
//		gbc.weightx = 0.0;
//		gbc.weighty = 0.0;
//		background.add(tip, gbc);
		
		mainBar = new JProgressBar(0, noOfNodes);
		overview = new JProgressBar(0, noOfNodes+noOfEdges+noOfCoastPoints);
		mainBar.setValue(0);
		mainBar.setOpaque(false);
		mainBar.setStringPainted(true);
		overview.setValue(0);
		overview.setOpaque(false);
		overview.setStringPainted(true);
		JPanel center = new JPanel();
		center.setPreferredSize(new Dimension(500, 50));
		center.setLayout(new BorderLayout());
		center.setOpaque(false);
		center.add(mainBar, BorderLayout.NORTH);
		center.add(overview, BorderLayout.SOUTH);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		background.add(center, gbc);
		setContentPane(contentPane);
		createTimer();
	}
	
	/**
	 * Sets the labels and values of the progress bar.
	 */
	public void updateProgress() {
		mainBar.setValue(mainBar.getValue()+1);
		mainBar.setString(mainBar.getValue()+" of "+mainBar.getMaximum()+" "+mainBar.getName());
		overview.setValue(overview.getValue()+1);
	}
	
	/**
	 * Changes the displayed name of the task currently being performed during start-up.
	 * @param task	Name of task
	 */
	public void setTaskName(Task task) {
		switch(task) {
		case COAST: 
			overview.setString("Finding land...");
			mainBar.setValue(0);
			mainBar.setName("coastpoints");
			mainBar.setMaximum(noOfCoastPoints);
			break;
		case NODES: 
			overview.setString("Marking intersections...");
			mainBar.setValue(0);
			mainBar.setName("intersections");
			mainBar.setMaximum(noOfNodes);
			break;
		case EDGES:
			overview.setString("Laying roads...");
			mainBar.setValue(0);
			mainBar.setMaximum(noOfEdges);
			mainBar.setName("roads");
			break;
		case GRAPH:
			overview.setString("Planning routes...");
			mainBar.setString("Working...");
			break;
		case MAP:
			overview.setString("Drawing map...");
			mainBar.setString("Working...");
			break;
		}
	}
	
	/**
	 * Sets up a timer.
	 * (For testing and debugging)
	 */
	private void createTimer() {
		timer = new Timer(8000, new ChangeTip());
		timer.start();
	}
	
	/**
	 * When the program has loaded, stops the timer and closes the splash.
	 */
	public void close() {
		timer.stop();
		Window.use().setVisible(true);
		dispose();
		instance = null;
		// Print statement used when measuring the startup time.
//		System.out.format("Time in seconds need for the system to startup: %.2f sec.",(System.currentTimeMillis()-startUpTime)/1000.0);
	}
	
	/**
	 * Sets the number of initializations to be displayed on the progress bars.
	 * @param nodes	Number of nodes
	 * @param edges	Number of edges
	 * @param coast	Number of points on coastline
	 * @param lake	Number of points in lakes
	 */
	public static void initialize(String nodes, String edges, String coast, String lake) {
		   noOfNodes = countLines(nodes);
		   noOfEdges = countLines(edges);
		   noOfCoastPoints = countLines(coast);
		   noOfCoastPoints += countLines(lake);
	}
	
	/**
	 * The method is used to count the number of RoadSegment within the 
	 * data files. 
	 * 
	 * @param filepath	The directory for the file which should be counted.
	 * @return			The number of new line character within the file.
	 */
	private static int countLines(String filepath) {
		InputStream is;
		int count = 0;
		    try {
				is = new BufferedInputStream(new FileInputStream(filepath));
				//1024 bytes equals 1 kilobyte.
		        byte[] c = new byte[1024];
		        int readChars = 0;
		        
		        //Reads the file byte for byte and counts all newline characters.
		        while ((readChars = is.read(c)) != -1) {
		            for (int i = 0; i < readChars; ++i) {
		                if (c[i] == '\n') {
		                    count++;
		                }
		            }
		        }
		        is.close();
		    } catch (Exception e) {
		        //Sets the value of the counter to 1 if an exception is caught.
		        //Makes the Splash-screen' progress bar look funny but the program will start.  
		        //If the file really is corrupt the DataReader will terminate the program.
		    	return 1;
			}
		return count;
	}
	
	/**
	 * Randomly picks a tool tip an changes the current one.
	 */
	private class ChangeTip implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			for(boolean b : shown) {
				if (b == true) displayedTips++;
			}
			if(displayedTips == tipArray.length) shown = new boolean[tipArray.length];
			int next = random.nextInt(tipArray.length-1);
			while (shown[next]) {
				next = random.nextInt(tipArray.length-1);
			}
			tipTitle.setText("Tips & Tricks:");
			shown[next] = true;
			tip.setText(tipArray[next]);
			repaint();
		}
	}
}


	

