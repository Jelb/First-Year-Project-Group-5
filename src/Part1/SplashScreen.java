package Part1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.BufferedInputStream;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

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
	
	private SplashScreen() {
		super();
		setPreferredSize(new Dimension(800, 600));
		setUndecorated(true);
		setUp();
		pack();
		setLocationRelativeTo(null);
		toFront();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static SplashScreen use() {
		if(instance == null) {
			instance = new SplashScreen();
		}
		return instance;
	}
	
	private void setUp() {
		JPanel contentPane = new JPanel(new GridBagLayout());
		JLabel background = new JLabel(new ImageIcon("logo.jpg"));
		background.setLayout(new GridBagLayout());
		contentPane.add(background);
		
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
		background.add(center);
		setContentPane(contentPane);
	}
	
	public void updateProgress() {
		mainBar.setValue(mainBar.getValue()+1);
		mainBar.setString(mainBar.getValue()+" of "+mainBar.getMaximum()+" "+mainBar.getName());
		overview.setValue(overview.getValue()+1);
	}
	
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
	
	public void close() {
		Window.use().setVisible(true);
		dispose();
		instance = null;
	}
	
	public static void initialize(String nodes, String edges, String coast, String lake, String island) {
		   noOfNodes = countLines(nodes);
		   noOfEdges = countLines(edges);
		   noOfCoastPoints = countLines(coast);
		   noOfCoastPoints += countLines(lake);
		   noOfCoastPoints += countLines(island);
	}
	
	/**
	 * The method is used to count the number of RoadSegment within the 
	 * data files. 
	 * 
	 * @param filepath The directory for the file which should be counted.
	 * @return	The number of new line character within the file.
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
}
	

