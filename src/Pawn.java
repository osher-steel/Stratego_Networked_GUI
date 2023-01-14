import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

abstract class Pawn implements Serializable{
    private static final long serialVersionUID = 1L;
	protected boolean alive;
    protected boolean placed;
    protected int rank;
    protected boolean wonFight;
    protected int player;
    public Alliance playerAlliance;
    protected int x;
    protected int y;

    Pawn(int r, Alliance  playerAlliance)
    {
        x=0;
        y=0;
        alive=true;
        placed=false;
        rank=r;
        wonFight = false;
        this.playerAlliance=playerAlliance;
    }

	public String toString() {
        if (player == 0) {
            return "Red " + getName();  
        }
        else {
            return "Blue " + getName();
        }
    }
	
	 public static BufferedImage getImage(Alliance alliance, Pawn p) {
	        try {
	            BufferedImage myImage = ImageIO.read(new File("" + getFilename(alliance, p)));
	            return myImage;
	        }
	        catch (IOException e) {
	        	e.printStackTrace();
	        }
	        return null;
	    }
	 
    public static String getFilename(Alliance alliance, Pawn p) {  //Used to retrieve correct pawn image from images folder
        String filepath = null;  				
        if(alliance==Alliance.red) {
            filepath = Paths.get("","images", "red" + p.getName() + ".gif").toString();
        }
        else {
            filepath = Paths.get("","images", "blue" + p.getName() + ".gif").toString();
        }
        //System.out.println(filepath);
        return filepath.toLowerCase();
    }

    public void setWonFight(boolean won) {
        wonFight = won;
    }

    public boolean getWonFight() {
        return wonFight;
    }

    public boolean isPlaced()
    {
        return placed;
    }

    public boolean isAlive()
    {
        return alive;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }
    
    public int getRank()
    {
        return rank;
    }

    public void placePawn(int x, int y )
    {
        this.x=x;
        this.y=y;
        placed=true;
        alive=true;

    }

    public void removePawn()
    {
        placed=false;
        alive=false;
    }

    public abstract char toChar();

    public abstract String getName();
    
    public boolean isEqual(Pawn pawn)
    {
    	if(pawn.rank==this.rank)
    		return true;
    	else
    		return false;
    }
}

class Dummy extends Pawn    
{
    private static final long serialVersionUID = 1L;

	Dummy(Alliance alliance)
    {
        super(-1, alliance);
    }
   
    public String getName()
    {
        return "Dummy";
    }

    public char toChar()
    {
        return '0';
    }
}

class Flag extends Pawn
{

    private static final long serialVersionUID = 1L;

	Flag(Alliance alliance)
    {
        super(0,alliance);

    }
    public String getName()
    {
        return "Flag";
    }

    public char toChar()
    {
        return 'F';
    }
}

class Bomb extends Pawn
{

    private static final long serialVersionUID = 1L;

	Bomb(Alliance alliance)
    {
        super(11,alliance);

    }
    public String getName()
    {
        return "Bomb";
    }

    public char toChar()
    {
        return 'B';
    }
}

class Marshall extends Pawn
{

    private static final long serialVersionUID = 1L;

	Marshall(Alliance alliance)
    {
        super(10,alliance);

    }
    public String getName()
    {
        return "Marshall";
    }

    public char toChar()
    {
        return 'M';
    }
}

class General extends Pawn
{

    private static final long serialVersionUID = 1L;

	General(Alliance alliance)
    {
        super(9,alliance);

    }
    public String getName()
    {
        return "General";
    }

    public char toChar()
    {
        return '9';
    }
}

class Colonel extends Pawn
{

    private static final long serialVersionUID = 1L;

	Colonel(Alliance alliance)
    {
        super(8,alliance);

    }
    public String getName()
    {
        return "Colonel";
    }

    public char toChar()
    {
        return '8';
    }
}

class Major extends Pawn
{

    private static final long serialVersionUID = 1L;

	Major(Alliance alliance)
    {
        super(7,alliance);

    }
    public String getName()
    {
        return "Major";
    }

    public char toChar()
    {
        return '7';
    }
}

class Captain extends Pawn
{

    private static final long serialVersionUID = 1L;

	Captain(Alliance alliance)
    {
        super(6,alliance);

    }
    public String getName()
    {
        return "Captain";
    }

    public char toChar()
    {
        return '6';
    }
}

class Lieutenant extends Pawn
{

    private static final long serialVersionUID = 1L;

	Lieutenant(Alliance alliance)
    {
        super(5,alliance);

    }
    public String getName()
    {
        return "Lieutenant";
    }

    public char toChar()
    {
        return '5';
    }
}

class Sergeant extends Pawn
{

    private static final long serialVersionUID = 1L;

	Sergeant(Alliance alliance)
    {
        super(4,alliance);

    }

    public String getName()
    {
        return "Sergeant";
    }

    public char toChar()
    {
        return '4';
    }
}

class Miner extends Pawn
{

    private static final long serialVersionUID = 1L;

	Miner(Alliance alliance)
    {
        super(3,alliance);

    }
    public String getName()
    {
        return "Miner";
    }

    public char toChar()
    {
        return '3';
    }
}

class Scout extends Pawn
{

    private static final long serialVersionUID = 1L;

	Scout(Alliance alliance)
    {
        super(2,alliance);

    }

    public String getName()
    {
        return "Scout";
    }

    public char toChar()
    {
        return '2';
    }
}

class Spy extends Pawn
{
    private static final long serialVersionUID = 1L;

	Spy(Alliance alliance)
    {
        super(1,alliance);

    }

    public String getName()
    {
        return "Spy";
    }

    public char toChar()
    {
        return '1';
    }
}