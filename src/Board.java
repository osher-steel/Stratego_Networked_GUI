 import java.awt.*;
import javax.imageio.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Board extends JPanel implements Serializable{
    private static final long serialVersionUID = 1L;
    
    private JLabel gameOverLabel;
    private JLabel instructionBox;
    
	public Player player,oppPlayer;
    private Alliance boardAlliance;

    public Square square[][];

    private int xSelector,ySelector;
    private int xSelected,ySelected;
    private int xSelected2,ySelected2;
	private int pawnToPlace;
    
    private boolean squareSelected;
    private boolean unmovablePawnSelected;
    private boolean setupDone;
    private boolean gameOver;
    private boolean attackInProgress;
    private boolean thisPlayer;
    
    public boolean moveScoutCloser;
    
    private boolean allPawnsVisible, attackerAdvantage, unknownVictor, movableBombs, oneTimeBombs, advancedScout;
    
    
 
    							//INITIALIZATION//
    Board(Alliance alliance){
    	
    	  setLayout(new BorderLayout(0,0));

    	  boardAlliance=alliance;
    	  
    	  setBackground(Utils.BACKGROUND_COLOR);
   
          gameOverLabel= new JLabel("Game Over");
          gameOverLabel.setForeground(new Color(60,0,100));
          gameOverLabel.setFont(new Font("Impact",Font.BOLD,50));
          gameOverLabel.setVisible(false);
          gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
          this.add(gameOverLabel, BorderLayout.NORTH);
          
          instructionBox= new JLabel("Instructions: ");
          instructionBox.setBounds(1000, 100, 100, 100);
          instructionBox.setVisible(true);
          instructionBox.setBackground(Color.WHITE);
          
          initializeComponents(false,false,false,false,false,false);          
          
    }
  
	public void initializeComponents(boolean allPawnsVisible, boolean attackerAdvantage, boolean unknownVictor, 
		boolean movableBombs, boolean oneTimeBombs,boolean advancedScout) { 	
   	 	
		square = new Square[10][10]; // Board made up of 10x10 squares
	   	 
	     for (int i = 0; i < 10; i++) {			//Initializes the squares with their x,y coordinate and x, y indeces
	         for (int j = 0; j < 10; j++) {
	             int xLocation = (Utils.INITIAL_BOARD_X + (j * Utils.TILE_LENGTH));
	             int yLocation = (Utils.INITIAL_BOARD_Y + (i * Utils.TILE_LENGTH));
	             square[i][j] = new Square(xLocation, yLocation, i, j);
	         }
	     }
	     
	     try {
			player= new Player(boardAlliance,square);
			oppPlayer= new Player(Alliance.getOppositeAlliance(boardAlliance),square);
		} catch (IOException e) {
			e.printStackTrace();
		}
         
    	gameOver=false;
    	gameOverLabel.setVisible(false);
    	
    	
    	
    	this.allPawnsVisible=allPawnsVisible;        //Initializes the variants 
    	this.attackerAdvantage=attackerAdvantage;
    	this.unknownVictor=unknownVictor;
    	this.movableBombs=movableBombs;
    	this.oneTimeBombs=oneTimeBombs;
    	this.advancedScout=advancedScout;
 	
        xSelected=0; //Coordinates of the selected square
        ySelected=0;
        
        xSelected2=0;
        ySelected2=0;
       
        xSelector = Alliance.getXSelector(boardAlliance); //Coordinates of where user is
        ySelector = Alliance.getYSelector(boardAlliance); 

        setupDone=false;
        attackInProgress=false;
        unmovablePawnSelected=false;
        squareSelected=false;
    	thisPlayer=true;
    	moveScoutCloser=false;
        
        pawnToPlace=player.nextPawnToPlace();   
        repaint();
	}
	
	
								//PAINTING//
	@Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        
        BufferedImage strategoLogo=null;  
        try {
				strategoLogo= ImageIO.read(new File(Paths.get("","images","Stratego_logo_final.png").toString()));
			} catch (IOException e) {
				e.printStackTrace();
			}
        
        int logoX, logoY;
		int logoWidth=350;
		int logoHeight=logoWidth/4;
		
        Image strategoImage= strategoLogo.getScaledInstance(logoWidth,logoHeight,Image.SCALE_SMOOTH);
		
		logoX= (getWidth()/2)-logoWidth/2;
		logoY= 0;
		
        
        if(!gameOver)
        	g2d.drawImage(strategoImage,logoX,logoY,this);
        

        int boardX, boardY;
		
		boardX=(getWidth()-Utils.BOARD_LENGTH)/2;
		boardY=strategoImage.getHeight(this)+3;
		
        Rectangle bigRect = new Rectangle(boardX,boardY,   
        		(Utils.BOARD_LENGTH), (Utils.BOARD_LENGTH)); 

        g2d.setColor(Utils.TILE_COLOR);
        g2d.fill(bigRect); 
        
        
        
        for(int i=0; i<10; i++)
        {
        	for(int j=0; j<10; j++)
        	{
        		if(square[i][j]!=null)
        			square[i][j].newXandY(boardX+(j*Utils.TILE_LENGTH), boardY+(i*Utils.TILE_LENGTH));
        	}
        }
       
		if(!gameOver && setupDone)
		{
			int rectX= 0;
			int rectY=50;
			
			for(int i=0; i<12;i++)
			{
	            	if(boardAlliance==Alliance.red)
	            		g.drawImage(Utils.RED_IMGS.get(i), rectX, rectY, this);
	            	else
	            		g.drawImage(Utils.BLUE_IMGS.get(i), rectX, rectY, this);
	                
	            
	           String str= "x "+ player.pawnRemaining(i);
	           g.setColor(Color.BLACK);
	           g.drawString(str, rectX+Utils.RED_IMGS.get(0).getWidth()+5, rectY+(Utils.RED_IMGS.get(0).getHeight())/2+5);
	                
	           rectY+=50;
			}
			
			rectX= getWidth()-Utils.RED_IMGS.get(0).getWidth();
			rectY= 50;
			
			for(int i=0; i<12;i++)
			{
	           
				if(boardAlliance==Alliance.red)
            		g.drawImage(Utils.BLUE_IMGS.get(i), rectX, rectY, this);
            	else
            		g.drawImage(Utils.RED_IMGS.get(i), rectX, rectY, this);
	                
	            String str=oppPlayer.pawnRemaining(i) +" x";
	            g.setColor(Color.BLACK);
	            g.drawString(str, rectX-25, rectY+(Utils.RED_IMGS.get(0).getHeight())/2+5);
	             
	            rectY+=50;
	            
			}
		}

		g2d.setStroke( new BasicStroke( 1.5f ) );
		
        for (int i = 0; i < 10; i++)  // Paints each cell 
        {
            for (int j = 0; j < 10; j++) {
                if(square[0][0]!=null)
                	paintCell(i, j, g2d);
            }
        }
        
        
        
        if(attackInProgress)
        {
        	paintCell(xSelected,ySelected,g2d);
        	paintCell(xSelector,ySelector,g2d);
        }
		
    }
	
    private void paintCell(int i, int j, Graphics2D g2d) {
        Rectangle cell = new Rectangle();
        cell.setBounds(square[i][j].xCoordinate, square[i][j].yCoordinate, Utils.TILE_LENGTH, Utils.TILE_LENGTH); 

        if (square[i][j].isLake()) // if lake area fill square in blue
        {
            g2d.setColor(Utils.LAKE_COLOR);
            g2d.fill(cell );
        }
        else if (paintSelected(i,j))// if square is current square selected
        {
            g2d.setColor(Utils.SELECTED_COLOR);
            g2d.fill(cell );
            
        }
        else if(paintAvailableSquares(i,j) && setupDone) //Shows available in green
        {
            g2d.setColor(Utils.AVAILABLE_MOVES_COLOR);
            g2d.fill(cell );
        }
        else if(unmovablePawnSelected && i==xSelector && j==ySelector)
        {
            g2d.setColor(Utils.TILE_COLOR.darker());
            g2d.fill(cell );
 
        }
 
        if(attackInProgress && ((i==xSelector && j==ySelector)||(i==xSelected && j==ySelected)))
        {
            g2d.setColor(Color.BLACK); //Draws outline of square
            g2d.draw(cell);
            
            g2d.drawImage(Utils.FIRE_IMG, square[i][j].xCoordinate+Utils.FIRE_X_CORRECTION,square[i][j].yCoordinate+Utils.FIRE_Y_CORRECTION,this);
            
        }
        else
        {
            g2d.setColor(Color.BLACK); //Draws outline of square
            g2d.draw(cell);
        }

        if (!square[i][j].isEmpty()) // If square has a pawn on it
        {
            if (isPawnVisible(i, j)) //Calls isPawnVisible to know if it can paint the pawn
            {
                int tempX = square[i][j].xCoordinate + 6; 
                int tempY = square[i][j].yCoordinate + 6;

                g2d.setColor(Color.WHITE);
                
                if(square[i][j].getPawn().playerAlliance==Alliance.red)
            		g2d.drawImage(Utils.RED_IMGS.get(square[i][j].getPawn().rank), tempX, tempY, this);
            	else
            		g2d.drawImage(Utils.BLUE_IMGS.get(square[i][j].getPawn().rank), tempX, tempY, this);
            }
            else {
                int tempX = square[i][j].xCoordinate + (Utils.TILE_LENGTH / 6); //If the pawn is not visible then it just draws a red or blue square
                int tempY = square[i][j].yCoordinate + (Utils.TILE_LENGTH / 6);
                cell .setBounds(tempX, tempY, (Utils.TILE_LENGTH * 2 / 3), (Utils.TILE_LENGTH * 2 / 3));

                g2d.setColor(square[i][j].getSquareColor());
                g2d.fill(cell );
            }
        }
        
    }

    private boolean paintAvailableSquares(int i, int j) {
    	if(canPawnMoveToSquare(i,j) && setupDone&& squareSelected && !attackInProgress)
    		return true;
    	else
    		return false;
	}

	private boolean paintSelected(int i, int j) {
    	if((i == xSelected && j == ySelected )&& squareSelected && !attackInProgress)
    		return true;
    	else
    		return false;
	}

	private  boolean isPawnVisible(int i, int j) {
    	if(square[i][j].getPawn().playerAlliance==boardAlliance)
    		return true;
    	else if(attackInProgress && ((i==xSelector && j==ySelector)||(i==xSelected && j==ySelected)))
    	{
    		 if(!unknownVictor)
    			 return true;
    		 else if(thisPlayer &&unknownVictor && attackResult()!=-1)
    				 return true;
    		 else if(!thisPlayer && unknownVictor && attackResult()!=1)
    			 return true;
    	}
    	else if(allPawnsVisible)
    		return true;

    	return false;
    }

	
							//PLAYER SELECTION// 
    public int[]selection( boolean thisPlayer, boolean beginningOfTurn, int xLocation, int yLocation) //Deals with event handling for the ENTER key
    {
     	int []moveLocation= {Utils.SELECTED,Utils.SELECTED,Utils.SELECTED,Utils.SELECTED,Utils.SELECTED};
    	
    	if(clickIsInBounds(xLocation, yLocation) && !setupDone)
			setupAction();
    	else if(clickIsInBounds(xLocation, yLocation)&& setupDone)
            moveLocation=battleAction(thisPlayer,beginningOfTurn);
    	
    	repaint();
        return moveLocation;
    }


							//BOARD SETUP//

    private void setupAction() {

	if (square[xSelector][ySelector].isEmpty() && playerCanPlacePawn()) //If square is empty
	{
		player.placePawn(pawnToPlace, xSelector, ySelector); //player's pawn is "placed" with x y index
		pawnToPlace=player.nextPawnToPlace();
	}
	else if(player.allPawnsPlaced())  //If all pawns are placed then player can select a square to be swapped or to move
	{	
		if(!squareSelected) //if no square is selected make that square the square selected
		{
			xSelected=xSelector;
			ySelected=ySelector;
			squareSelected=true;
			}
		else  //swap pawns with selected square
		{
			swapPawns();
			squareSelected=false;
		
		}
	}
	
	repaint();
	
	}

    public void fillSetup(int key) {

Integer[][] setup= Utils.SETUPMAP.get(key);
player.removeAllPawns(); //Clears the player's pawns before filling it

if(boardAlliance==Alliance.blue) //Checks Alliance to retreive the correct part of the 2d array
{
for(int i=0; i<4;i++)
{
for(int j=0; j<10; j++)
{
player.placePawn(setup[i][j], i, j);
}
}

}
else
{
for(int i=9;i>=6;i--)
{
for(int j=9;j>=0;j--)
{
player.placePawn(setup[i][j], i, j);
}
}
}


repaint();

}

    private void swapPawns() 
{
if(!(xSelector==xSelected && ySelector==ySelected))
{
int temp1= square[xSelected][ySelected].getPawn().getRank();    
int temp2= square[xSelector][ySelector].getPawn().getRank();

player.removePawn(xSelector,ySelector);
player.removePawn(xSelected,ySelected);

player.placePawn(temp1, xSelector, ySelector);
player.placePawn(temp2, xSelected, ySelected);
}
}

    public void setupDone()
    {
    squareSelected=false;
    setupDone=true;
    repaint();
    }

    
    					    //BOARD SETUP BOOLEAN FUNCTIONS//
    
    public boolean D_KeyPressed() {  

if(player.allPawnsPlaced())
return true;
else
return false;
}

    public boolean isSettupDone()
{
if(player.allPawnsPlaced())
return true;
else 
return false;
}

    public boolean isEmpty(Board board)
{
for(int i=0; i<10; i++)
{
for(int j=0; j<10; j++)
{
if(!board.square[i][j].isEmpty())
return false;
}
}
return true;
}

    private boolean playerCanPlacePawn() {

    	if(boardAlliance==Alliance.blue)
    	{
    	if(xSelector<=3)
    	return true;
    	}
    	else
    	{
    	if(xSelector>=6)
    	return true;
    	}

    	return false;
    	}

    
    						//BATTLE PHASE MOVE//
	private int[] battleAction(boolean thisPlayer, boolean begginingOfTurn)
    {
		this.thisPlayer=thisPlayer;
    	int []moveLocation= {0,0,0,0,0};
    	
    	Alliance tempAlliance;
    	if(thisPlayer)
    		tempAlliance=boardAlliance;
    	else
    		tempAlliance=Alliance.getOppositeAlliance(boardAlliance);
    	attackInProgress=false;
    	
    	 if(!squareSelected) //if no square is currently selected
         {
             if(canPawnMove()) //Checks if the pawn in the square has at least one available move
             {
                 xSelected=xSelector; //Make current square selected
                 ySelected=ySelector;
                 squareSelected=true;
             }
             else if(square[xSelector][ySelector].getPawn().playerAlliance==boardAlliance)
             {
             	 if(unmovablePawnSelected && xSelected2==xSelector && ySelected2==ySelector)
             		 unmovablePawnSelected=false;
            	 else
            	 {
            		 unmovablePawnSelected=true;
            		 squareSelected=false;
            		 xSelected2=xSelector;
            		 ySelected2=ySelector;
            	 }
             }
             moveLocation[4]=Utils.SELECTED;
         }
         else //if a square is currently selected
         {
             if(canPawnMoveToSquare(xSelector, ySelector) || !thisPlayer) //if the selector is at a square where the selected pawn can move
             {             	
                 if(square[xSelector][ySelector].isEmpty()) //move the pawn to that square 
                 {
                 	movePawn(thisPlayer, xSelected, ySelected, xSelector,ySelector); 
                     moveLocation[4]=Utils.MOVE;
                     squareSelected=false;
                 }
                 else if(square[xSelector][ySelector].getPawn().playerAlliance==Alliance.getOppositeAlliance(tempAlliance))
                 {
                 	if(begginingOfTurn)
                 	{
                 		if(square[xSelected][ySelected].getPawn().rank==2)
                 		{
             				if(Math.abs(ySelected-ySelector)>1 || Math.abs(xSelected-xSelector)>1)
             				{
             					moveScoutCloser=true;
             					moveLocation[4]=Utils.SCOUT_DISTANT_ATTACK;
             				}
                 		}
                 		attackInProgress=true;
                 	}
                 	else
                 	{
                 		attack(thisPlayer);
                 		squareSelected=false;
                 	}
                 	if(moveLocation[4]!= Utils.SCOUT_DISTANT_ATTACK)
                 		moveLocation[4]=Utils.ATTACK;
                 }
                 
               
                 moveLocation[0]=xSelected;
                 moveLocation[1]=ySelected;
                 moveLocation[2]=xSelector;
                 moveLocation[3]=ySelector;
                 
             }
             else
             {
                 if(xSelector==xSelected && ySelector==ySelected) //if selector and selected are the same then square is deselected
                 {
                     squareSelected=false;
                     unmovablePawnSelected=false;
                 }
                 else if(square[xSelector][ySelector].getPawn().playerAlliance==boardAlliance  && canPawnMove() && !square[xSelector][ySelector].isEmpty())	 //if it is a player's pawn that can move then selected square is moved to that square
                 {
                	 xSelected=xSelector; ySelected=ySelector;
                	 
                 }
                 else if(square[xSelector][ySelector].getPawn().playerAlliance==boardAlliance)
                 {
                 	 if(unmovablePawnSelected && xSelected2==xSelector && ySelected2==ySelector)
                 		 unmovablePawnSelected=false;
                	 else
                	 {
                		 unmovablePawnSelected=true;
                		 squareSelected=false;
                		 xSelected2=xSelector;
                		 ySelected2=ySelector;
                	 }
                 }
                 	
                 moveLocation[4]=Utils.SELECTED;
             }
         }
    	 
    	 repaint();
    	 return moveLocation;
    }
	
	private void attack(boolean thisPlayer)
    {        
        if(attackResult()==0) //if both die
        {
          squareSelected=false;
          if(thisPlayer)
          {
     		 player.removePawn(xSelected,ySelected);              //Removes both pawns from board and player
             oppPlayer.removePawn(xSelector,ySelector);   
          }
          else
          {
     		 oppPlayer.removePawn(xSelected,ySelected);
             player.removePawn(xSelector,ySelector);   
          }
        }
        else if(attackResult()==1) //if attacker wins
        {
        	if(thisPlayer)
        		 oppPlayer.removePawn(xSelector,ySelector);
        	else
        		player.removePawn(xSelector,ySelector);
         
          squareSelected=false;
          movePawn(thisPlayer,xSelected, ySelected, xSelector,ySelector);       
        }
        else if(attackResult()==-1) //if defender wins
        {
          squareSelected=false;
          if(thisPlayer)
        	  player.removePawn(xSelected,ySelected);
          else
        	  oppPlayer.removePawn(xSelected,ySelected);
        }

        repaint();        
    }
    
    private int attackResult()
    {
        int currentPawnRank= square[xSelected][ySelected].getPawn().getRank();
        int oppPawnRank= square[xSelector][ySelector].getPawn().getRank();

        
        if(currentPawnRank==1) //If a spy attacks, if it attacks a marshall it wins else it loses
        {
            if(oppPawnRank==10) 
                return 1;
            else
                return -1;
        }

        if(currentPawnRank==3 && oppPawnRank==11) //If a miner attacks a bomb
        	return 1;
        
        
        if(oppPawnRank==11 && oneTimeBombs==true)
        {
        	return 0;
        }
       
        if(currentPawnRank<oppPawnRank) //If attacker loses return -1
            return -1;
        else if(currentPawnRank>oppPawnRank) //if attacker wins return 1
            return 1;
        else
        {
        	if(attackerAdvantage)
        		return 1;
        	else
        		return 0;
        		
        }
    }
   
	private void movePawn(boolean thisPlayer, int x1, int y1, int x2, int y2) //Moves a pawn to another square during battle phase
    {
    	
        int pawn= square[x1][y1].getPawn().getRank();
        
        if(thisPlayer==true)
        {
            player.removePawn(x1,y1);
            player.placePawn(pawn,x2,y2);
        }
        else
        {
            oppPlayer.removePawn(x1,y1);
            oppPlayer.placePawn(pawn,x2,y2);
        }
    }

	public int[] moveScoutCloser(int [] moveLocation, boolean thisPlayer)
	{
		if(!thisPlayer)
		{
			xSelected= moveLocation[0];
			ySelected= moveLocation[1];
			xSelector= moveLocation[2];
			ySelector= moveLocation[3];
			
		}
		
		if(xSelected==xSelector)
		{
			if(ySelected<ySelector)
			{
				movePawn(thisPlayer,xSelected, ySelected, xSelector,ySelector-1);
				ySelected=ySelector-1;
			}
			else
			{
				movePawn(thisPlayer,xSelected, ySelected, xSelector,ySelector+1);
				ySelected=ySelector+1;
			}
			
			
		}
		else if(ySelected==ySelector)
		{
			if(xSelected<xSelector)
			{
				movePawn(thisPlayer,xSelected, ySelected, xSelector-1,ySelector);
				xSelected=xSelector-1;
			}
			else
			{
				movePawn(thisPlayer,xSelected, ySelected, xSelector+1,ySelector);
				xSelected=xSelector+1;
			}
			
			
		}
		
		  moveLocation[0]=xSelected;
	      moveLocation[1]=ySelected;
	      moveLocation[2]=xSelector;
	      moveLocation[3]=ySelector;
          
		moveScoutCloser=false;
		
		return moveLocation;
	}
	
	public void endOfGame()
    {
    	squareSelected=false;
    	allPawnsVisible=true;
    	gameOver=true;
    	gameOverLabel.setVisible(true);
    	
    	if(player.pawnMap.get(0)[0].alive)
    		gameOverLabel.setText("You Win!");
    	else
    		gameOverLabel.setText("You Lose!");
    		
    	repaint();
    }

						//BATTLE PHASE BOOLEAN FUNCTIONS//
	
	
	private boolean canPawnMove()
    {
        int tempRank=square[xSelector][ySelector].getPawn().getRank();


        if(tempRank==0) //if pawn is a flag then it cannot move
            return false;
        else if(tempRank==11&& movableBombs==false) //if bomb is a flag and movableBombs is false then it cannot move
        	return false;

        if(!square[xSelector][ySelector].isEmpty() && (square[xSelector][ySelector].getPawn().playerAlliance==boardAlliance))
        {				//If the square selected has the current player's pawn

            if(ySelector!=0) //To avoid index going out of bounds
            {
                if(square[xSelector][ySelector - 1].isEmpty() &&( !square[xSelector][ySelector - 1].isLake()
                		||(advancedScout==true &&tempRank==2 && square[xSelector][ySelector - 3].isEmpty()))
                		|| (square[xSelector][ySelector-1].getPawn().playerAlliance==Alliance.getOppositeAlliance(boardAlliance) && tempRank!=11))
                    return true;  //Checks if at least one adjacent square is empty or has an enemy pawn
                				  //if advanced scouting is true, then scout can go across a lake but not on it
                			      //If it is a bomb it cannot attack, so it cannot move if it has no empty tiles to move 

            }
            if(ySelector!=9)
            {
                if(square[xSelector][ySelector + 1].isEmpty() &&( !square[xSelector][ySelector + 1].isLake()||
                		(advancedScout==true &&tempRank==2 && square[xSelector][ySelector +3].isEmpty()))
                		|| (square[xSelector][ySelector+1].getPawn().playerAlliance==Alliance.getOppositeAlliance(boardAlliance)&& tempRank!=11))
                    return true;
            }
            if(xSelector!=0)
            {
                if(square[xSelector - 1][ySelector].isEmpty() && (!square[xSelector - 1][ySelector].isLake()||
                		(advancedScout==true &&tempRank==2 && square[xSelector-3][ySelector].isEmpty())) 
                		|| (square[xSelector-1][ySelector].getPawn().playerAlliance==Alliance.getOppositeAlliance(boardAlliance)&& tempRank!=11))
                    return true;
            }
            if(xSelector!=9)
            {
                if(square[xSelector + 1][ySelector].isEmpty() &&( !square[xSelector + 1][ySelector].isLake()
                		||(advancedScout==true &&tempRank==2 && square[xSelector+3][ySelector].isEmpty()))
                		|| (square[xSelector+1][ySelector].getPawn().playerAlliance==Alliance.getOppositeAlliance(boardAlliance)&& tempRank!=11))
                    return true;
            }
        }

        return false;
    }
    	
    private boolean canPawnMoveToSquare(int x, int y)
    {	
        if(x==xSelected&& y==ySelected)
            return false;

        int currentPawnRank=square[xSelected][ySelected].getPawn().getRank();
        

        boolean isOpposite;
        if(square[x][y].getPawn().playerAlliance==Alliance.getOppositeAlliance(boardAlliance))
        	isOpposite=true;
        else
        	isOpposite=false;
        //Player can move to that square if it is one square away and is either empty or contains an enemy pawn (attack)

        if(isInBounds(x,y) && x==xSelected && (Math.abs(y-ySelected))==1 && 
        		(square[x][y].isEmpty() || isOpposite&& currentPawnRank!=11) && !square[x][y].isLake()) 
            return true;
        else if(isInBounds(x,y) && y==ySelected && (Math.abs(x-xSelected))==1 &&
        		(square[x][y].isEmpty() ||isOpposite && currentPawnRank!=11) && !square[x][y].isLake())
            return true;

        if(currentPawnRank==2) //if the pawn selected is the scout then it can move like a rook in chess
        {
            int distance; //To iterate through the squares between the square selected and the square(x,y) including the square(x,y)
            int direction;//To make sure that the iteration is in the right direction
            
            if(square[x][y].isLake()) //For advanced scouting, scounts can only pass through a lake not stop on one
            	return false;

            if(x==xSelected) //For vertical moves
            {
                if(y>ySelected)
                    direction=-1;
                else
                    direction=1;

                distance=(Math.abs(y-ySelected));
                
       

                for(int i=1; i<distance; i++)
                {
                    if(!square[x][y + (i * direction)].isEmpty() ||
                            (square[x][y + (i * direction)].isLake()&&advancedScout==false)) //if one square in between the two is not empty
                    {											 							// or a lake then that square is not available
                        return false;
                    }
                }
                if(square[x][y].isEmpty() || isOpposite)
                	return true;

            }
            if(y==ySelected) //Same but for horizontal moves
            {
                if(x>xSelected)
                    direction=-1;
                else
                    direction=1;

                distance=(Math.abs(x-xSelected));
                for(int i=1; i<distance; i++)
                {
                    if(!square[x + (i * direction)][y].isEmpty() ||
                            square[x + (i * direction)][y].isLake()&&advancedScout==false) //if one square in between the two is not empty
                    {													   // or not a lake then that square is not available
                        return false;
                    }
                }
                if(square[x][y].isEmpty() || isOpposite)
                	return true;
            }
            

        }

        return false; //if none of the conditions above are met then pawn cannot move to that square
    }
 
    private boolean isInBounds(int x, int y) {
        if(x >= 0 && x < 10 && y >= 0 && y < 10) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isGameOver()
    {
        if(player.pawnMap.get(0)[0].isAlive() && oppPlayer.pawnMap.get(0)[0].isAlive())
        	return false;
        else
        	return true;
    }

    private boolean clickIsInBounds(int xLocation, int yLocation) {
		for(int i=0; i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				if(clickInBoard(i,j,xLocation,yLocation))
				{
					if((i>=Alliance.getBottomRange(boardAlliance)&& i<=Alliance.getUpperRange(boardAlliance))
							|| setupDone)
					{
						xSelector=i;
						ySelector=j;
						return true;
					}
				}
			}
		}
			
		return false;
	}
    
    private boolean clickInBoard (int i, int j, int xLocation, int yLocation)
    {
    	int locationCorrection=Utils.TILE_LENGTH/2;
    	
    	if(xLocation>square[i][j].xCoordinate && yLocation>square[i][j].yCoordinate+locationCorrection && 
    			xLocation< (square[i][j].xCoordinate+Utils.TILE_LENGTH) && yLocation<(square[i][j].yCoordinate+Utils.TILE_LENGTH+locationCorrection) )
    		return true;
    	else
    		return false;
    }

    
    					//SERVER-CLIENT COMMUNICATION//
    public boolean receiveBoard(int [][] arr)
    {	
    	for(int i=0; i<10; i++)
    	{
    		for(int j=0; j<10; j++)
    		{
    			if(arr[i][j]!=-1)
    				oppPlayer.placePawn(arr[i][j], i, j);
    		}
    	}
		repaint();
		return true;
    }

    public void receiveMove(int [] location, boolean beginningOfTurn)
    {	
    	if(location[4]==2)
    		gameOver=true;
    	else {
        	xSelected=location[0];
        	ySelected=location[1];
        	xSelector=location[2];
        	ySelector=location[3];
        	
        	squareSelected=true;
        	
        	battleAction(false, beginningOfTurn);
    	}
    }
    
    public int[][] boardToInt()
    {
    	int [][] board= new int[10][10];
    
    	

		for(int i=0; i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				board[i][j]=square[i][j].getPawn().getRank();
			}
		}

    	return board;
    }
	}

