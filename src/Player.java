import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;

public class Player implements Serializable{
	    private static final long serialVersionUID = 1L;
	    
	    public HashMap<Integer,Pawn[]> pawnMap;
	    public Square square[][];
	    public Alliance alliance;
	    

	    public Player(Alliance alliance, Square square[][]) throws IOException
	    {
	    	this.square=square;
	    	this.alliance=alliance;
	    	
	    	pawnMap= new HashMap<>();
	    	
	    	
	    	Bomb bomb[]= new Bomb[6];
	    	for(int i=0; i<bomb.length;i++)
	    	{
	    		bomb[i]= new Bomb(alliance);
	    	}
	    	
	    	Marshall marshall[]= new Marshall[1];
	    	marshall[0]= new Marshall(alliance);
	    	
	    	General general[]= new General[1];
	    	general[0]= new General(alliance);
	    	
	    	Colonel colonel[]= new Colonel[2];
	    	for(int i=0; i<colonel.length;i++)
	    	{
	    		colonel[i]= new Colonel(alliance);
	    	}
	    	
	    	Major major[]= new Major[3];
	    	for(int i=0; i<major.length;i++)
	    	{
	    		major[i]= new Major(alliance);
	    	}

	    	Captain captain[]= new Captain[4];
	    	for(int i=0; i<captain.length;i++)
	    	{
	    		captain[i]= new Captain(alliance);
	    	}
	    	
	    	Lieutenant lieutenant []= new Lieutenant[4];
	    	for(int i=0; i<lieutenant.length;i++)
	    	{
	    		lieutenant[i]= new Lieutenant(alliance);
	    	}
	    	
	    	Sergeant sergeant[]= new Sergeant[4];
	    	for(int i=0; i<sergeant.length;i++)
	    	{
	    		sergeant[i]= new Sergeant(alliance);
	    	}
	     	
	    	Miner miner[]= new Miner[5];
	    	for(int i=0; i<miner.length;i++)
	    	{
	    		miner[i]= new Miner(alliance);
	    	}
	    	
	    	Scout scout[]= new Scout[8];
	    	for(int i=0; i<scout.length;i++)
	    	{
	    		scout[i]= new Scout(alliance);
	    	}
	    	
	    	Spy spy[]= new Spy[1];
	    	{
		    	spy[0]= new Spy(alliance);
	    	}
	    	
	    	Flag flag[]= new Flag[1];
	    	{
		    	flag[0]= new Flag(alliance);
	    	}
	  
	    	pawnMap.put(0, flag);
	    	pawnMap.put(1, spy);
	    	pawnMap.put(2, scout);
	    	pawnMap.put(3, miner);
	    	pawnMap.put(4, sergeant);
	    	pawnMap.put(5, lieutenant);
	    	pawnMap.put(6, captain);
	    	pawnMap.put(7, major);
	    	pawnMap.put(8, colonel);
	    	pawnMap.put(9, general);
	    	pawnMap.put(10, marshall);
	    	pawnMap.put(11, bomb);
	    }
	 
	    public int nextPawnToPlace()
	    {
	    	for(int i=0; i<12;i++)
	    	{
	    		for(int j=0; j<pawnMap.get(i).length;j++)
	    		{
	    			if(!pawnMap.get(i)[j].isPlaced())
	    				return i;
	    		}
	    	}
	    	
	    	return 0;
	    }

	    public boolean allPawnsPlaced()
	    {
	        for(int i=0; i<12; i++)
	        {
	        	for(int j=0; j<pawnMap.get(i).length;j++)
	        	{
	        		if(!pawnMap.get(i)[j].isPlaced())
	        			return false;
	        	}
	        			
	        }

	        return true;
	    }

	    public void removePawn(int x, int y) {
	    	int rank=square[x][y].getPawn().getRank();
	    	
	        square[x][y].removePawn();
	        
	        if(pawnMap!=null)
	        {
		        for(int i=0; i< pawnMap.get(rank).length;i++)
		        {
		        	if(pawnMap.get(rank)[i].getX()==x && pawnMap.get(rank)[i].getY()==y)
		        		pawnMap.get(rank)[i].removePawn();
		        }
	        }
	        

	    }

	    public void removeAllPawns()
	    {
	    	for(int i=0; i<12; i++)
	    	{
	    		for(int j=0; j<pawnMap.get(i).length;j++)
	    		{
	    			pawnMap.get(i)[j].removePawn();
	    			square[pawnMap.get(i)[j].getX()][pawnMap.get(i)[j].getY()].removePawn();
	    		}
	    	}
	    }

	    public void placePawn(int rank, int x, int y)
	    {
	    	if(pawnMap!=null)
	    	{
		    	for(int i=0; i<pawnMap.get(rank).length;i++)
		    	{
	    			if(!pawnMap.get(rank)[i].isPlaced())
	    	    	{
	    	    		pawnMap.get(rank)[i].placePawn(x, y);
	    	    		square[x][y].addPawn(pawnMap.get(rank)[0], alliance);
	    	    		i=pawnMap.get(rank).length;
	    	    	}
		    	}
	    	}
	    }
	    
	    public int pawnRemaining(int rank)
	    {
	    	int remaining=0;
	    	for(int i=0; i<pawnMap.get(rank).length;i++)
	    	{
	    		if(pawnMap.get(rank)[i].alive)
	    			remaining++;
	    			
	    	}
	    	
	    	return remaining;
	    }
	    
	    
	}