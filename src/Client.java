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
import java.net.Socket;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.Scanner;

import javax.swing.JFrame;


public class Client extends JFrame implements Runnable, KeyListener, MouseListener{
	private static final long serialVersionUID = 1L;
	private Socket connection;
	private Scanner input;
	private Formatter output;
	
	Board board;
	Alliance myAlliance;
	
	private Thread thread;
	
	private boolean myTurn;
	private boolean gameOver;
	private boolean setupDone;
	private boolean mySetupDone;
	private long lastProcessed=0;
	
	private int [] move= new int[5];
	private boolean receiveInProgress;
	private boolean attackInProgress;
	
	private int x,y;
	private int iteration;
	private boolean restart;
	
	private int[][] ennemyBoard;
	
	public static void main(String [] args)
	{

		Client client= new Client();
		if(args.length==0)
			client.getStreams("localhost",31);
		else
			client.getStreams(args[0],31);
	}
	
	Client()
	{	
		myAlliance= Alliance.getOppositeAlliance(Utils.STARTING_PLAYER);

		board= new Board(myAlliance);
		
		setLayout(new BorderLayout(0,0));
		add(board);

		myTurn=true;
		setupDone=false;
		mySetupDone=false;
		gameOver=false;
		
		receiveInProgress=false;
		attackInProgress=false;
		
		x=0; y=0;
		iteration=0;
	
		restart=false;
		ennemyBoard= new int[10][10];
		
		add(board);
		addKeyListener(this);
		addMouseListener(this);
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	
	public void getStreams(String host, int port)
	{
		try {
			connection= new Socket(host, 31);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			input= new Scanner(connection.getInputStream());
		} catch (IOException e) {		
			e.printStackTrace();
		}
		try {
			output= new Formatter(connection.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		thread= new Thread(this);
		thread.start();
	}
	
	@Override
	public void run()
	{
		try {
			while(iteration<3)
			{
				restart=false;
				switch(iteration)
				{
				case 0:
					while(!setupDone)
					{
						if(input.hasNextInt())
							receiveBoard();
						
			   			setupDone=true;
					}
					if(!restart)
						iteration++;
					break;
				case 1:
					while(!mySetupDone)
					{
						synchronized(this)
						{
							try {
								wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
					myTurn=false;
					if(!restart)
						iteration++;
					break;
				case 2:
					while(!gameOver && !restart)
					{
						if(myTurn)
						{
							synchronized(this)
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
			        		   		attackInProgress=false;
			        		   		gameOver=board.isGameOver();			
			        		   		
		    					}
		    					myTurn=false;
							}
						}
						else
						{
							synchronized(this)
							{
								if(input.hasNextInt())
								{
									receiveMove();
									if(receiveInProgress)
									{
										try {
											wait(3000);
										} catch (InterruptedException e) {
											e.printStackTrace();
										}
										board.receiveMove(move,false);
										receiveInProgress=false;
										gameOver=board.isGameOver();
										
									}
									myTurn=true;
								}
							}
						}
					}
					
					if(!restart)
					{
						board.endOfGame();
					}
					break;	
				}
			}

			
		
		}
		finally {
			closeStreams();
		}
	}

	private void receiveBoard()
	{	
		for(int i=0;i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				ennemyBoard[i][j]= input.nextInt();
				if(ennemyBoard[0][0]==Utils.RESTART)
				{
					restart();
					restart=true;
					i=10;
					j=10;
				}
			}
		}
	}
	
	private void sendBoard()
	{
		for(int i=0;i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				if(!board.square[i][j].isEmpty())
				{
					if(board.square[i][j].getPawn().playerAlliance==myAlliance)
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
		if(!receiveInProgress)
		{
			for(int i=0; i<5; i++)
			{
				move[i]=input.nextInt();
			}
		}
		
		if(move[4]==Utils.RESTART)
		{
			restart();
		}
		else if(move[4]==Utils.MOVE)
			board.receiveMove(move,false);
		else
		{
			if(move[4]==Utils.SCOUT_DISTANT_ATTACK)
				move=board.moveScoutCloser(move,false);
			
			board.receiveMove(move,true);
			receiveInProgress=true;
		}
	}
	
	private void restart() {
		int []receive= new int[Utils.SETTINGS.length];
		boolean []settings= new boolean[receive.length];
		
		
		for(int i=0; i<receive.length; i++)
		{
			receive[i]=input.nextInt();
		}
		
		
		
		for(int i=0; i<receive.length;i++)
		{
			if(receive[i]==0)
				settings[i]=false;
			else
				settings[i]=true;
		}
		
		board.initializeComponents(settings[0],settings[1],settings[2],settings[3],settings[4],settings[5]);
		board.repaint();
		restart=true;
		mySetupDone=false;
		setupDone=false;
		
		iteration=0;
	}

	private void sendMove(int [] move)
	{
		for(int i=0; i<5; i++)
		{
			output.format("%d\n", move[i]);
			output.flush();
		}
		
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
	
	@Override 
	public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }
    
    @Override
    public synchronized void keyReleased(KeyEvent e) {
    	
        if (System.currentTimeMillis() - lastProcessed > 50) // To make sure that the pressedkey doesnt shoot 
        { 													 // events too fast
           if(numberEntered(e.getKeyCode()))
        	   board.fillSetup(e.getKeyCode());
           else if(e.getKeyCode()==Utils.ENTER && setupDone && board.D_KeyPressed())
           {
       		if(!restart)
    		{
    			
    			board.receiveBoard(ennemyBoard);
    		}
       		
			   mySetupDone=true;
			   board.setupDone();
			   notifyAll();
			   sendBoard();
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public synchronized void mousePressed(MouseEvent e) {
		 if (System.currentTimeMillis() - lastProcessed > 50 && !attackInProgress) // To make sure that the pressedkey doesnt shoot 
	     { 	
			 if(!mySetupDone)
		  		   board.selection(true,false,e.getX(),e.getY());
		  	 else if(myTurn)
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
		  		   }
		  		   x=e.getX();
		  		   y=e.getY();
		  	   }
			 
		   lastProcessed = System.currentTimeMillis();
	     }
	
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}
