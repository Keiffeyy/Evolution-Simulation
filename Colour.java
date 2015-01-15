import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
 * a dot that represents the animal's colour. follows that animal around.
 * 
 * @author Keith Wong
 * @version October 19. 2013
 */
public class Colour extends Actor
{
    private Fauna target; //the target that is is following
    private Color color;
    private GreenfootImage circle = new GreenfootImage(30, 30); //creates a GreenfootImage (oval) with size 30
    /**
     * Creates a new instance of Colour.
     *
     *@param r the r value of the RGB code.
     *@param g the g value of the RGB code.
     *@param b the b value of the RGB code.
     *@param target the Fauna that this follows.
     */   

    public Colour(int r, int g, int b, Fauna target){
        this.color = new Color(r, g, b); //sets the color of this
        this.target = target; //sets the target
        circle.setColor(color);
        //draw and fill the oval with the specified color
        circle.drawOval(0, 0, 20, 20);
        circle.fillOval(0, 0, 20, 20);
        this.setImage(circle); //updates the image
    }

    /**
     * runs every greenfoot frame. Makes this follow its target.
     */
    public void act() 
    {
        setLocation(target.getX() + 5, target.getY() + 5); //follow the actor
    }    
}
