import java.awt.Color;

//Alliance enum to handle differentiate the two players and their pawns 
//Inspired by the video series on chess programming by @amir650 on Youtube

public enum Alliance {   
	blue,red,none;
	
	public static Alliance getOppositeAlliance(Alliance alliance)  
	{
		if(alliance==red)
			return blue;
		else
			return red;
	}
	
	public static int getUpperBound(Alliance alliance, boolean settingUpDone)  
	{
		if (settingUpDone)
			return 0;
		else if(alliance==red)
           return 6;
		else
			return 0;
	}
	
	public static int getLowerBound(Alliance alliance, boolean settingUpDone)
	{
		if(settingUpDone)
			return 9;
		else if(alliance==red)
			return 9;
		else
			return 3;
	}
	
	public static int getBottomRange(Alliance alliance)  
	{
		if(alliance==red)
           return 6;
		else
			return 0;
	}
	
	public static int getUpperRange(Alliance alliance)
	{

		if(alliance==red)
			return 10;
		else
			return 4;
	}
	
	public static Color getColor(Alliance alliance)
	{
		if(alliance==red)
			return Utils.RED_PAWN_COLOR;
		else
			return Utils.BLUE_PAWN_COLOR;
	}
	
	public static int getXSelector(Alliance alliance)
	{
		if(alliance==red)
			return 6;
		else
			return 3;
	}
	
	public static int getYSelector(Alliance alliance)
	{
		return 4;
	}
	
	public static String getString(Alliance alliance)
	{
		if(alliance==red)
			return "Red";
		else
			return "Blue";
	}
	
	public static Alliance stringToAlliance(String string)
	{
		if(string=="Red"|| string=="red")
			return Alliance.red;
		else
			return Alliance.blue;
	}

}
