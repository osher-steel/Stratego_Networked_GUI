import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.Scanner;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Server extends JFrame implements Runnable, KeyListener, MouseListener{
	private static final long serialVersionUID = 1L;
	private ServerSocket server;
	private Socket connection;
	private Scanner input;
	private Formatter output;
	
	
	private Board board;

	private Alliance myAlliance;
	private Thread thread;
	
	private boolean myTurn;
	private boolean gameOver;
	private boolean setupDone;
	private boolean mySetupDone;
	
	private int x,y;
	private int iteration;
	private boolean restart;
	
	private long lastProcessed;
	
	private CheckBoxItem []settingItem;
	private boolean settingBool[];
	
	private int [] move= new int[5];
	private boolean attackInProgress;
	
	Timer time;
	
	public static void main(String [] args)
	{
		
		Server server= new Server();
		server.getStreams();
		
		
	}

	Server()
	{
		
		
		try {
			server= new ServerSocket(31,1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		myAlliance= Utils.STARTING_PLAYER;
	
	    
		
		board= new Board(myAlliance);
		
		setLayout(new BorderLayout(0,0));
		add(board);

		
		
		myTurn=true;
		setupDone=false;
		mySetupDone=false;
		gameOver=false;
		attackInProgress=false;
		
		x=0; y=0;
		iteration=0;
		restart=false;

		
		addKeyListener(this);
		addMouseListener(this);
		setMenu();
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	private void setMenu() {
		JMenuBar menuBar= new JMenuBar();
		setJMenuBar(menuBar);

		
		
		SettingsHandler settingHandler= new SettingsHandler();  //Handlers for menu items
		RestartHandler restartHandler= new RestartHandler();
		RulesHandler rulesHandler= new RulesHandler();
		UserGuideHandler userHandler= new UserGuideHandler();
		

		JMenu optionsMenu= new JMenu("Options");  //Menus
		
		JMenu settingsMenu= new JMenu("Variations");//Menu inside Options menu
		
		JMenuItem restart= new JMenuItem("Restart"); //Menu items for optionMenu
		restart.addActionListener(restartHandler);
		
		JMenuItem quit= new JMenuItem("Quit");
		quit.addActionListener( new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		
		settingItem= new CheckBoxItem[Utils.SETTINGS.length];  //Initialize the settingItems with the settingString array and add to settingMenu
		for(int i=0;i<Utils.SETTINGS.length;i++)
		{
			settingItem[i]=new CheckBoxItem(Utils.SETTINGS[i]);
			settingsMenu.add(settingItem[i]);
			settingItem[i].addItemListener(settingHandler);
		}
		
		settingBool= new boolean[Utils.SETTINGS.length];
		for(int i=0; i<settingBool.length;i++)
		{
			settingBool[i]=false;
		}
		
		optionsMenu.add(settingsMenu);
		optionsMenu.add(restart);
		optionsMenu.add(quit);
		
		JMenu howToMenu= new JMenu ("How To Play");  //How to menu and its items
		
		JMenuItem rules= new JMenuItem("Rules");  
		rules.addActionListener(rulesHandler);
		JMenuItem userGuide= new JMenuItem("User Guide");
		userGuide.addActionListener(userHandler);
		
		howToMenu.add(rules);
		howToMenu.add(userGuide);

		menuBar.add(optionsMenu);
		menuBar.add(howToMenu);	
	}

						//STREAMS//
	public void getStreams()
	{
		try {
			connection= server.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			output= new Formatter(connection.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			input= new Scanner(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		thread= new Thread(this);
		thread.start();
		
	}
	
	private void closeStreams()
	{
		input.close();
		output.close();
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
						//RUN//
	@Override
	public synchronized void run()
	{
		try {
			while(iteration<3)
			{
				switch(iteration)
				{
				case 0:
					restart=false;
					
					while(!mySetupDone)
					{
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(!restart)
						iteration++;
					break;
				case 1:
					while(!setupDone)
					{
						if(input.hasNextInt())
							receiveBoard();
					}
					myTurn=true;
					if(!restart)
						iteration++;
					break;
				case 2:
					while(!gameOver && !restart)
					{
						if(myTurn)		
						{
							try {
								wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							

	    					if(attackInProgress)
	    					{
		        		   		
		        		   		try {
									wait(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}    					
		        		   		
	    						board.selection(true,false,x,y);
		        		   		gameOver=board.isGameOver();
		        		   		attackInProgress=false;
		        		   		
	    					}
	    					myTurn=false;
						}
						else
						{

							if(input.hasNextInt())
							{
								receiveMove();
								if(attackInProgress)
								{
									try {
										wait(3000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									board.receiveMove(move,false);
									gameOver=board.isGameOver();
									attackInProgress=false;
									
								}
								myTurn=true;
							}
						}
					}
					
					if(!restart)
					{
						board.endOfGame();
						myTurn=true;
					}
					
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
					
					break;
				}
			}
		}
		finally {
			closeStreams();
			
		}
	}
		
	
						//RECEIVE AND SEND//
	private void receiveBoard()
	{
		int[][] ennemyBoard= new int[10][10];
		for(int i=0;i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				ennemyBoard[i][j]= input.nextInt();
			}
		}
		
		board.receiveBoard(ennemyBoard);
		setupDone=true;
	}

	private void sendBoard()
	{
		for(int i=0;i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				if(!board.square[i][j].isEmpty() && board.square[i][j].getPawn().playerAlliance==myAlliance )
				{
					output.format("%d\n", board.square[i][j].getPawn().rank );
					output.flush();
				}
				else
				{
					output.format("%d\n", -1);
					output.flush();
				}
			}
		}
	}
	
	private void receiveMove()
	{	
		for(int i=0; i<5; i++)
		  move[i]=input.nextInt();
		
		if(move[4]==Utils.MOVE)
			board.receiveMove(move,false);
		else
		{
			if(move[4]==Utils.SCOUT_DISTANT_ATTACK)
				move=board.moveScoutCloser(move,false);
			
			board.receiveMove(move,true);
			attackInProgress=true;
		}
		
	}
	
	private void sendMove(int [] move)
	{
		for(int i=0; i<5; i++)
		{
			output.format("%d\n", move[i]);
			output.flush();
		}
		
	}
	
	
						//RESTART//
	private synchronized void restart()
	{
		for(int i=0; i<5;i++)
		{
			output.format("%d\n", Utils.RESTART);
			output.flush();
		}
		
		int []settings= new int[Utils.SETTINGS.length];
		
		for(int i=0; i<settings.length;i++)
		{
			if(settingBool[i])
				settings[i]=1;
			else
				settings[i]=0;
				
		}
		
		for(int i=0; i<settings.length;i++)
		{
			output.format("%d\n", settings[i]);
			output.flush();
		}
		
		board.initializeComponents(settingBool[0],settingBool[1],settingBool[2],settingBool[3],settingBool[4],settingBool[5]);
		
		mySetupDone=false;
		setupDone=false;
		restart=true;
		notifyAll();
		iteration=0;
	}
    
	
						//KEY EVENT//
    @Override
    public synchronized void keyReleased(KeyEvent e) {
    	
        if (System.currentTimeMillis() - lastProcessed > 50) // To make sure that the pressedkey doesnt shoot 
        { 
        	// events too fast
           if(numberEntered(e.getKeyCode()))
           {
        	   board.fillSetup(e.getKeyCode());
           }
           else if(e.getKeyCode()==Utils.KEY_D && !mySetupDone && board.D_KeyPressed())
           {
			   mySetupDone=true;
			   board.setupDone();
			   sendBoard();
			   notifyAll();
           }
           lastProcessed = System.currentTimeMillis();
        }
           
          
    }
    
    private boolean numberEntered(int key) {
    	if(key>=Utils.KEY_0 && key<=Utils.KEY_9 && !mySetupDone && !gameOver)
    		return true;
    	else
    		return false;
	}

    					//MOUSE EVENT//
	@Override
	public synchronized void mousePressed(MouseEvent e) {
		if (System.currentTimeMillis() - lastProcessed > 50 && !attackInProgress) // To make sure that the pressedkey doesnt shoot 
	     { 	
			if(myTurn)
			{
				 if(!mySetupDone)
			  		   board.selection(true,false,e.getX(),e.getY());
			  	 else if(myTurn && setupDone)
			  	   {
			  		   int []tempArr=board.selection(true,true,e.getX(),e.getY());
			  		   
			  		   if(board.moveScoutCloser)
			  			   board.moveScoutCloser(move,true);
			  		   
			  		   if(tempArr[4]!=Utils.SELECTED)
			  		   {
			     		   	if(tempArr[4]==Utils.ATTACK || tempArr[4]==Utils.SCOUT_DISTANT_ATTACK)
			     		   		attackInProgress=true;
			     		   	
			   		   		sendMove(tempArr);
			  		   		notifyAll();
			  		   		
			  		   		x=e.getX();
			  		   		y=e.getY();
			  		   }
			  	   }
				 
			   lastProcessed = System.currentTimeMillis();
			}
	     }
	
	}

	
						//MENU HANDLERS//
	private class RulesHandler implements ActionListener
    {
    	@Override
    	public synchronized void actionPerformed(ActionEvent e) {
    		try 
    		{
    			//Redirects to a webpage that contains the game rules
    			Desktop.getDesktop().browse(new URL("https://www.ultraboardgames.com/stratego/game-rules.php").toURI()); 
    		}
    		catch(Exception ex)
    		{
    			ex.printStackTrace();
;    		}
    	}
    }
	
	private class SettingsHandler implements ItemListener //Handles the checkbox items
    {

		@Override
		public void itemStateChanged(ItemEvent e) {
			for(int i=0; i<Utils.SETTINGS.length;i++)
			{
				if(settingItem[i].isSelected())
					settingBool[i]=true;
				else
					settingBool[i]=false;
			}
			
		}
    }
	
	private class RestartHandler implements ActionListener
    {
    	@Override
    	public synchronized void actionPerformed(ActionEvent e) {
    				restart();
    	}
    }
	
	private class UserGuideHandler implements ActionListener
	{

		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			Icon image = new ImageIcon(Paths.get("","images", "StrategoIconSmall.gif").toString());
				// adds message and image to the userguide
				JOptionPane.showInternalMessageDialog(null, Utils.userGuideString, "User Guide", JOptionPane.INFORMATION_MESSAGE, image);			
				 // to continue proper sequence of the game			
		}		
	}
    
	private class CheckBoxItem extends JCheckBoxMenuItem {  
		
	    public CheckBoxItem(String text) {
	        super(text);
	    }


	    @Override
	    protected  void processMouseEvent(MouseEvent e) {
	        if (e.getID() == MouseEvent.MOUSE_RELEASED && contains(e.getPoint())) { //To prevent menu from closing after a checkbox is selected
	            doClick();
	            setArmed(true);
	        } else {
	            super.processMouseEvent(e);
	        }
	    }
	}
    
	
						//UNUSED INTERFACE FUNCTIONS//
	
	@Override 
	
	public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
    
	@Override
	public void mouseClicked(MouseEvent e) {
		
		
	}
	
	@Override
	
	public void mouseEntered(MouseEvent e) {
		
	}
	
    @Override
	public void mouseReleased(MouseEvent e) {
		
	}
    
	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
