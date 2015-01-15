import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;

/**
 * GenericBar is a Greenfoot Actor that displays a bar that is filled more as the current value approaches the max value. Multiples of this object may be created with no ill effects.
 * Methods included allow for the user to change the color of the bar, place a gradient, or create a new bar with an updated max value, but with all other parameters the same.
 * 
 * 
 * @author Keith Wong
 * @version September 25, 2013
 */
public class GenericBar extends Actor{
    //Variables for the image
    private GreenfootImage genericBar;
    private Color barColor;
    private Color white = new Color (255,255,255);
    //Variables for if the bar is following an actor
    private Prey target;
    private boolean followsActor = false;
    private int offset;
    //variables for the bar's position
    private int genericBarX;
    private int genericBarY;
    private int genericBarWidth;
    private int genericBarHeight;
    //Variables for the amount of the bar that is filled
    private double percentOfBarFilled;
    private int barSize;
    private int barRemaining;
    //Variables for the maximum value of the bar and the current value of the bar
    private int maxValue;
    private int currentValue;
    //Color code for bar color
    private int r;
    private int g;
    private int b;
    //Color code for the bar color when the bar is empty
    private int rEmpty;
    private int gEmpty;
    private int bEmpty;
    boolean gradient = false; //turns true if user chooses to use code to gradient the bar
    //the actual color that is displayed when using color gradient
    private int rDisplayed;
    private int gDisplayed;
    private int bDisplayed;
    //gradient differential from the minimum value
    private double gradientR;
    private double gradientG;
    private double gradientB;
    /**
     * Creates the bar with a certain height, width, location, and color.
     * @param maxValue the upper limit of the bar.
     * @param genericBarX the x coordinate of the centre of the bar.
     * @param genericBarY the y coordinate of the centre of the bar.
     * @param genericBarHeight the height of the bar.
     * @param genericBarWidth the width of the bar.
     * @param r the color code for r (the "redness" of the color). Accepts a value from 0 to 255.
     * @param g the color code for g (the "greeness" of the color). Accepts a value from 0 to 255.
     * @param b the color code for b (the "blueness" of the color). Accepts a value from 0 to 255.
     * 
     */
    public GenericBar (int maxValue, int genericBarX, int genericBarY, int genericBarHeight, int genericBarWidth, int r, int g, int b){
        this.maxValue = maxValue;
        currentValue = 0;
        this.genericBarX = genericBarX; //xCoordinate of the bar
        this.genericBarY = genericBarY;//yCoordinate of the bar
        this.genericBarWidth = genericBarWidth; //width of the bar
        this.genericBarHeight = genericBarHeight; //height of the bar

        //color code
        this.r = r;
        this.g = g;
        this.b = b;

        //creates an image with the height and width specified, and then sets the color
        genericBar =  new GreenfootImage(genericBarWidth, genericBarHeight);
        barColor = new Color (r, g, b);
        genericBar.setColor(barColor);
        this.setImage(genericBar);
        update(currentValue);
    }

    /**
     * Creates a bar with a certain height, width and color that follows a certain actor.
     * @param maxValue the upper limit of the bar.
     * @param target the Actor that you want the bar to follow.
     * @param genericBarHeight the height of the bar.
     * @param genericBarWidth the width of the bar.
     * @param r the color code for r (the "redness" of the color). Accepts a value from 0 to 255.
     * @param g the color code for g (the "greeness" of the color). Accepts a value from 0 to 255.
     * @param b the color code for b (the "blueness" of the color). Accepts a value from 0 to 255.
     */
    public GenericBar(int maxValue, Prey target, int genericBarHeight, int genericBarWidth, int r, int g, int b, int offset){
        this.maxValue = maxValue;
        currentValue = 0;
        //sets which Actor the bar will follow
        this.target = target;
        followsActor = true;

        //color code
        this.r = r;
        this.g = g;
        this.b = b;

        //sets the height and width of the bar, creates the image and sets the paint color
        this.genericBarWidth = genericBarWidth;
        this.genericBarHeight = genericBarHeight;
        
        this.offset = offset;
        
        genericBar = new GreenfootImage(genericBarWidth, genericBarHeight);
        barColor = new Color (r, g, b);
        genericBar.setColor(barColor);
    }

    /**
     * Keeps the bar in its specified location, or followng a certain actor.
     */
    public void act() 
    {
        //checks if the bar is supposed to follow a certain Actor, if there is a value for actor it will follow the Actor
        if (this.target != null)
            setLocation(target.getX(), target.getY() - offset);
        else if (this.getX() != genericBarX || this.getY() != genericBarY)
            setLocation(genericBarX,genericBarY);
        //test code
        //         if (Greenfoot.isKeyDown("left"))
        //             currentValue--;
        //         if (Greenfoot.isKeyDown("right"))
        //             currentValue++;
        //checks to keep the value within the upper and lower limits
        if (currentValue > maxValue)
            currentValue = maxValue;
        if (currentValue < 0)
            currentValue = 0;
    }

    /**
     * Removes this bar from the world.
     */
    public void removeThis(){
        getWorld().removeObject(this);
    }

    /**
     *Sets the color that the bar will be when it is empty. Used to set the lower limit for the gradient option.
     *@param rEmpty the color code for r when the bar is empty (the "redness" of the color). Accepts a value from 0 to 255.
     *@param gEmpty the color code for g when the bar is empty (the "greeness" of the color). Accepts a value from 0 to 255.
     *@param bEmpty the color code for b when the bar is empty (the "blueness" of the color). Accepts a value from 0 to 255.
     */
    public void setEmptyColor(int rEmpty, int gEmpty, int bEmpty){
        //color code for when bar is empty
        this.rEmpty = rEmpty;
        this.gEmpty = gEmpty;
        this.bEmpty = bEmpty;
        gradient = true;  //fulfills condition for gradient code to run in update method
    }

    /**
     * Sets the color of the bar based on the gradient, using the percentage of the bar that is filled to determine how far the gradient is.
     */
    private void setGradientColor(){
        //checks to see which value is lower, to determine whether to add or subtract fromthe rgb values to approach the desired color as current value approaches 0
        if (r >= rEmpty){
            gradientR = (r - rEmpty)*percentOfBarFilled;
            rDisplayed = (int) (rEmpty + gradientR);
        } else {
            gradientR = (rEmpty - r)*percentOfBarFilled;
            rDisplayed = (int) (rEmpty - gradientR);
        }
        if (g >= gEmpty){
            gradientG = (g - gEmpty)*percentOfBarFilled;
            gDisplayed = (int) (gEmpty + gradientG);
        } else {
            gradientG = (gEmpty - g)*percentOfBarFilled;
            gDisplayed = (int) (gEmpty - gradientG);
        }
        if (b >= bEmpty){
            gradientB = (b - bEmpty)*percentOfBarFilled;
            bDisplayed = (int) (bEmpty + gradientB);
        } else {
            gradientB = (bEmpty - b)*percentOfBarFilled;
            bDisplayed = (int) (bEmpty - gradientB);
        }
        barColor = new Color(rDisplayed, gDisplayed, bDisplayed); //updates the color of the bar
    }

    /**
     * Changes the color of the bar.
     * @param r the color code for r (the "redness" of the color). Accepts a value from 0 to 255.
     * @param g the color code for g (the "greeness" of the color). Accepts a value from 0 to 255.
     * @param b the color code for b (the "blueness" of the color). Accepts a value from 0 to 255.
     */ 
    public void update(int r, int g, int b){
        this.r = r;
        this.g = g;
        this.b = b;
        barColor = new Color(r, g, b);
    }

    /**
     * Updates the Bar with the new current value, and displays it as a percentage of the bar that is filled.
     * @param updatedValue the current value of the bar.
     * @return boolean returns true if currentValue is equal to the maxValue (when the bar is full), returns false if not.
     */
    public boolean update(int updatedValue){
        currentValue = updatedValue; //sets the new parameter as the currentValue
        genericBar.setColor(barColor); //sets the bar to the desired color
        //finds how much of the bar needs to be filled
        percentOfBarFilled = ((double) currentValue/(double) maxValue);
        barSize = (int) (genericBarWidth*percentOfBarFilled);
        barRemaining = (genericBarWidth - barSize); //finds the amount of the bar that is blank
        if (gradient == true){
            setGradientColor();
        }
        genericBar.fillRect(0, 0, barSize, genericBarHeight);
        //sets the color to white and draws the amount of the bar that is blank
        genericBar.setColor(white);
        genericBar.fillRect(barSize, 0, barRemaining, genericBarHeight);
        this.setImage(genericBar);
        //when true, bar is full
        if (currentValue == maxValue)
            return true;
        else
            return false;
    }
}
