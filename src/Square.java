import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

public class Square extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
	private JLabel imgLabel;

    private Pawn pawn;
    private boolean empty;
    boolean lake;

    public int xCoordinate,yCoordinate;

    public int x,y;

    Color squareColor;

    public Square(int xCoor, int yCoor,int x, int y)
    {
        this.xCoordinate=xCoor;
        this.yCoordinate=yCoor;

        this.x=x;
        this.y=y;
        empty=true;

        if((x==4||x==5)&&(y==2||y==3||y==6||y==7))
            lake=true;
        else
            lake=false;
        
        pawn= new Dummy(Alliance.none);

        this.setLayout(new BorderLayout(1, 1));
        imgLabel = new JLabel("text");
        add(imgLabel, BorderLayout.CENTER);
        imgLabel.setVisible(true);
    }

    public void changeCoordinates(int x,int y)
    {
    	xCoordinate=x;
    	yCoordinate=y;
    }

    public void addPawn(Pawn p, Alliance playerTurn)
    {
    	squareColor= Alliance.getColor(playerTurn);
        pawn= p;
        empty=false;
    }

    public void removePawn()
    {
        empty=true;
        pawn=new Dummy(Alliance.none);
    }

    public Color getSquareColor()
    {
        return squareColor;
    }

    boolean isEmpty()
    {
    	return empty;
    }

    boolean isLake()
    {
        return lake;
    }

    public Pawn getPawn()
    {
        return pawn;
    }
    
    public void newXandY(int x, int y)
    {
    	xCoordinate=x;
    	yCoordinate=y;
    }
}