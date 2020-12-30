import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;

import java.awt.event.*;  // Needed for ActionListener
import javax.swing.event.*;  // Needed for ActionListener
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class LifeSimulationGUI extends JFrame implements ActionListener, ChangeListener{
	static Colony colony = new Colony (0.1);
    static JSlider speedSldr = new JSlider ();
    static Timer t;
    static Timer t2;
    //comboboxes
    private JComboBox topleftxCB = new JComboBox();
    private JComboBox topleftyCB = new JComboBox();
    private JComboBox sizexCB = new JComboBox();
    private JComboBox sizeyCB = new JComboBox();
    
    //text fields
    private JTextField input = new JTextField(10);
    private JTextField output = new JTextField(10);
    //changing label
    public JTextField gennum = new JTextField(5);//set this one to be public so that it can be altered by the Movement class
    
    //======================================================== constructor
    public LifeSimulationGUI ()
    {
        // 1... Create/initialize components
        //buttons
    	JButton simulateBtn = new JButton ("Simulate");
        JButton stopBtn = new JButton("Stop");
        JButton eradicateBtn = new JButton("Eradicate");
        JButton populateBtn = new JButton("Populate");
        JButton inputBtn = new JButton("Input Text File:");
        JButton outputBtn = new JButton("Output Text File:");
        JButton resetBtn = new JButton ("Reset");
        //labels
        JLabel topleft = new JLabel("Top Left Corner: ");
        JLabel size = new JLabel("Size");
        JLabel generation = new JLabel("Generation: ");
        gennum.setText("0");
        gennum.setEditable(false);
        //array used for the comboboxes
        String[] arr = new String[100];
        for (int i = 0; i < 100; i++) {
        	arr[i]=""+i;
        }
        
        //comboboxes
        topleftxCB = new JComboBox(arr);
        topleftyCB = new JComboBox(arr);
        sizexCB = new JComboBox(arr);
        sizeyCB = new JComboBox(arr);
        
        //adding action listeners
        simulateBtn.addActionListener (this);
        stopBtn.addActionListener(this);
        resetBtn.addActionListener(this);
        eradicateBtn.addActionListener(this);
        populateBtn.addActionListener(this);
        inputBtn.setActionCommand("Input");
        outputBtn.setActionCommand("Output");//just to make life easier for me
        inputBtn.addActionListener(this);
        outputBtn.addActionListener(this);
        speedSldr.addChangeListener (this);

        // 2... Create content pane, set layout
        JPanel content = new JPanel ();        // Create a content pane
        content.setLayout (new BorderLayout ()); // Use BorderLayout for panel
        JPanel north = new JPanel ();
        north.setLayout (new FlowLayout ()); // Use FlowLayout for input area
        JPanel mid = new JPanel();
        mid.setLayout(new FlowLayout());
        JPanel io = new JPanel();
        io.setLayout(new FlowLayout());
        DrawArea board = new DrawArea (500, 500);

        // 3... Add the components to the input area.

        north.add (simulateBtn);
        north.add(stopBtn);
        north.add (speedSldr);
        north.add(resetBtn);
        north.add(generation);
        north.add(gennum);
        mid.add(eradicateBtn);
        mid.add(populateBtn);
        mid.add(topleft);
        mid.add(topleftxCB);
        mid.add(topleftyCB);
        mid.add(size);
        mid.add(sizexCB);
        mid.add(sizeyCB);
        io.add(inputBtn);
        io.add(input);
        io.add(outputBtn);
        io.add(output);
        mid.add(io);

        content.add (north, "North"); // Input area
        content.add(mid);
        content.add (board, "South"); // Output area

        // 4... Set this window's attributes.
        setContentPane (content);
        pack ();
        setTitle ("Life Simulation Demo");
        setSize (600, 650);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo (null);           // Center window.
    }

    public void stateChanged (ChangeEvent e)
    {
        if (t != null) {
        	t.setDelay (400 - 4 * speedSldr.getValue ()); // 0 to 400 ms
        }
            
    }

    public void actionPerformed (ActionEvent e)
    {
        String ch = e.getActionCommand();
    	if (ch.equals ("Simulate"))
        {
            Movement moveColony = new Movement (colony); // ActionListener
            t = new Timer (200, moveColony); // set up timer
            t.start (); // start simulation
        }
        else if(ch.equals("Stop")) {
        	t.stop();
        }
        else if(ch.contentEquals("Reset")) {
        	colony = new Colony(0.1);//make a new colony
        	gennum.setText("0");//reset the generation counter
        	t.stop();
        }
        else if(ch.equals("Eradicate")) {
        	int topleftx = Integer.parseInt(""+topleftxCB.getSelectedItem());
        	int toplefty = Integer.parseInt(""+topleftyCB.getSelectedItem());
        	int sizex = Integer.parseInt("" + sizexCB.getSelectedItem());
        	int sizey = Integer.parseInt(""+ sizeyCB.getSelectedItem());
        	colony.eradicate(topleftx, toplefty, sizex, sizey);
        }
        else if(ch.equals("Populate")) {
        	int topleftx = Integer.parseInt(""+topleftxCB.getSelectedItem());
        	int toplefty = Integer.parseInt(""+topleftyCB.getSelectedItem());
        	int sizex = Integer.parseInt("" + sizexCB.getSelectedItem());
        	int sizey = Integer.parseInt(""+ sizeyCB.getSelectedItem());
        	colony.populate(topleftx, toplefty, sizex, sizey);
        }
        else if (ch.equals("Input")) {
        	try {
				colony.input(input.getText());
			} 
        	catch (FileNotFoundException e1) {
				System.out.print("BAD");
			}
        	gennum.setText("0");//reset the generation after user input
        }
        else if(ch.equals("Output")) {
        	try {
				colony.output(output.getText());
			} 
        	catch (FileNotFoundException e1) {
				System.out.print("BAD");
			}
        }
        repaint ();            // refresh display of deck
    }
    

    class DrawArea extends JPanel
    {
        public DrawArea (int width, int height)
        {
            this.setPreferredSize (new Dimension (width, height)); // size
        }

        public void paintComponent (Graphics g)
        {
            colony.show (g);
        }
    }

    class Movement implements ActionListener
    {
        private Colony colony;

        public Movement (Colony col)
        {
            colony = col;
        }

        public void actionPerformed (ActionEvent event)
        {
            colony.advance ();
            gennum.setText("" + (Integer.parseInt(gennum.getText()) + 1));
            repaint ();
        }
    }
    

    //======================================================== method main
    public static void main (String[] args)
    {
        LifeSimulationGUI window = new LifeSimulationGUI ();
        window.setVisible (true);
        System.out.println("Test");
    }
}
class Colony
{
    private boolean grid[] [];

    public Colony (double density)
    {
        grid = new boolean [100] [100];
        for (int row = 0 ; row < grid.length ; row++)
            for (int col = 0 ; col < grid [0].length ; col++)
                grid [row] [col] = Math.random () < density;
    }

    public void show (Graphics g)
    {
        for (int row = 0 ; row < grid.length ; row++)
            for (int col = 0 ; col < grid [0].length ; col++)
            {
                if (grid [row] [col]) // life
                    g.setColor (Color.black);
                else
                    g.setColor (Color.white);
                g.fillRect (col * 5 + 40, row * 5, 5, 5); // draw life form
        }
    }
    
    public void eradicate(int topleftx, int toplefty, int sizex, int sizey) {
    	for(int i = toplefty; i < toplefty+sizey;i++) {//each row is the y value
    		for(int j = topleftx; j < topleftx+sizex; j++) {//each column is the x value   	
    			grid[i][j] = Math.random() > 0.8;//so 20 percent will be true, 80 percent will be false	 
    		}
    	}
    }
    public void populate(int topleftx, int toplefty, int sizex, int sizey) {
    	for(int i = toplefty; i < toplefty+sizey;i++) {//each row is the y value
    		for(int j = topleftx; j < topleftx+sizex; j++) {//each column is the x value
    			grid[i][j] = Math.random() < 0.8;//so 20 percent will be false, 80 percent will be true 
    		}
    	}
    }
    
    public void input(String filename) throws FileNotFoundException{
    	File file = new File (filename);
    	Scanner filesc = new Scanner(file);
    	String[][] nums = new String[100][100];
    	for(int i = 0; i < 100;i++) {
    		nums[i] = filesc.nextLine().split(" ");//each string array is simply strings of 0s and 1s
    	}
    	for(int i = 0; i < 100; i++) {//interpreting the input 
    		for(int j = 0; j < 100;j++) {
    			if(nums[i][j].equals("1")) {
    				grid[i][j] = true;
    			}
    			else if(nums[i][j].equals("0")) {
    				grid[i][j] = false;
    			}
    		}
    	}
    }
    public void output(String filename) throws FileNotFoundException{
    	File file = new File(filename);
    	PrintWriter output = new PrintWriter(file);
    	System.out.println();
    	for(int i = 0; i < 100;i++) {//if the grid is true, output 1, if false, output 0
    		for(int j = 0; j < 100; j++) {
    			if(grid[i][j]) {
    				output.print("1 ");
    			}
    			else {
    				output.print("0 ");
    			}
    		}
    		output.println();
    	}
    	output.close();
    }

    public boolean live (int row, int col)
    {
        // count number of life forms surrounding to determine life/death
    	int alive = 0;
    	
    	if (row == 0 && col == 0) {//top left
    		if (grid[1][0]) {//right of top left
    			alive++;
    		}
    		if(grid[0][1]) {//under top left
    			alive++;
    		}
    		if(grid[1][1]) {//diag from top left
    			alive++;
    		}
    	}
    	else if (row == grid.length-1 && col == 0) {//bottom left
    		if (grid[row][1]) {//right of bottom left
    			alive++;
    		}
    		if(grid[row-1][1]) {//diag from bottom left
    			alive++;
    		}
    		if(grid[row-1][0]) {//above bottom left
    			alive++;
    		}
    	}
    	else if (row == 0 && col == grid[row].length-1) {//top right
    		if (grid[1][col]) {//below top right
    			alive++;
    		}
    		if(grid[1][col-1]) {//diag from top right
    			alive++;
    		}
    		if(grid[0][col-1]) {//left from top right
    			alive++;
    		}
    	}
    	else if (row == grid.length-1 && col == grid[row].length-1) {//bottom right
    		if (grid[row][col-1]) {//left of bottom right
    			alive++;
    		}
    		if (grid[row-1][col]) {//directly above bottom right
    			alive++;
    		}
    		if (grid[row-1][col-1]) {//diag from bottom right
    			alive++;
    		}
    	}
    	else if(row == 0) {//top row but not corners
    		if(grid[0][col-1]) {//left of slot
    			alive++;
    		}
    		if(grid[0][col+1]) {//right of slot
    			alive++;
    		}
    		for(int i = col-1;i<=col+1;i++ ) {//loop for the 3 below the slot
    			if(grid[1][i]){
    				alive++;
    			}
    		}
    	}
    	else if (row == grid.length-1) {//bottom row but not corners
    		if(grid[row][col-1]) {//left of slot
    			alive++;
    		}
    		if(grid[row][col+1]) {//right of slot
    			alive++;
    		}
    		for(int i = col-1;i<=col+1;i++ ) {//loop for the 3 above the slot
    			if(grid[row-1][i]){
    				alive++;
    			}
    		}
    	}
    	else if (col == 0) {//left column but not corners
    		if(grid[row-1][0]) {//above slot
    			alive++;
    		}
    		if(grid[row+1][0]) {//below slot
    			alive++;
    		}
    		for(int i = row-1;i<=row+1;i++ ) {//loop for the 3 right of the slot
    			if(grid[i][1]){
    				alive++;
    			}
    		}
    	}
    	else if (col == grid[row].length-1) {//right column but not corners
    		if(grid[row-1][col]) {//above slot
    			alive++;
    		}
    		if(grid[row+1][col]) {//below slot
    			alive++;
    		}
    		for(int i = row-1;i<=row+1;i++ ) {//loop for the 3 left of the slot
    			if(grid[i][col-1]){
    				alive++;
    			}
    		}
    	}
    	else {
    		for(int i = col-1;i<=col+1;i++) {//loop for 3 above and 3 below
    			if(grid[row-1][i]) {
    				alive++;
    			}
    			if(grid[row+1][i]) {
    				alive++;
    			}
    		}
    		
    		if(grid[row][col-1]) {//left of slot
    			alive++;
    		}
    		if(grid[row][col+1]) {//right of slot
    			alive++;
    		}
    	}
    	
    	//grid at r,c is alive
    	if(grid[row][col]) {
    		if(alive == 2 || alive == 3) {
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    	else {//grid at r,c is dead
    		if (alive == 3) {//dead cells become alive if there are exactly 3 live neighbours
    			return true;
    		}
    		else {
    			return false;
    		}
    	}
    }

    public void advance ()
    {
        boolean nextGen[] [] = new boolean [grid.length] [grid [0].length]; // create next generation of life forms
        for (int row = 0 ; row < grid.length ; row++)
            for (int col = 0 ; col < grid [0].length ; col++)
                nextGen [row] [col] = live (row, col); // determine life/death status
        grid = nextGen; // update life forms
    }
}
