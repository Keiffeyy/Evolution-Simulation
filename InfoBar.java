import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.*;
/**
 * Displays text with the number of each class on screen.
 * 
 * @author Keith Wong 
 * @version October 21 2013
 */
public class InfoBar extends Actor
{
    private GreenfootImage infoBar = new GreenfootImage (960, 50);
    private Color textColor = new Color(0, 0, 0);
    private Font textFont = new Font ("Arial", Font.BOLD, 24);
    private int numTrees = 0;
    private int numBushes = 0;
    private int numPrey = 0;
    private int numPredators = 0;
    /**
     * Creates an instance of InfoBar.
     */
    public InfoBar(){
        infoBar.setFont(textFont);
        infoBar.setColor(textColor);
    }

    /**
     * updates the values to be outputted onto the screen.
     * 
     * @param trees The number of trees.
     * @param bushes The number of bushes.
     * @param prey The number of prey.
     * @param predators The number of predators.
     */
    public void update (int trees, int bushes, int prey, int predators){
        numTrees = trees;
        numBushes = bushes;
        numPrey = prey;
        numPredators = predators;
        this.update();
    }

    /**
     * Updates the bar with current values.
     */
    public void update(){
        infoBar = new GreenfootImage (960, 50);
        infoBar.setFont(textFont);
        infoBar.setColor(textColor);
        String output = ("Trees: " + numTrees + " Bushes: " + numBushes + " Prey: " + numPrey + " Predators: " + numPredators);

        //centres the output on the bar.
        int centre = (960/2) - ((output.length() * 14)/2);

        infoBar.drawString(output, centre, 22); //draw the bar.
        this.setImage(infoBar);
    }
}
