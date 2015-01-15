import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;

/**
 * Radial Meter
 * 
 * Represents an amount out of a total in a radial format, capable of depletion and replenishment.
 * 
 * @author Daniel Chung 
 * @version V1.1 (Monday, September 30, 2013)
 */
public class RadialMeter extends Actor
{
    private Actor target;   //If the meter is to follow a target, then this will have a value
    private boolean targetAlive;    //The status of the target, used for removal of the object
    private double maxValue, currentValue;  //The values to be represented by the bar (circumference = maxValue)
    private double radius, width, circumference;    //The measurements of the meter and bar
    private int centreX, centreY;   //The position of the centre of the meter
    private GreenfootImage meterImage;  //The image of the meter
    private Color fillColour, gaugeColour, centreColour;  //The colour of the background of the meter, and the bar

    /**
     * Creates RadialMeter object with parameters below
     * 
     * @param   Actor   target          The Actor object to which this health bar will follow.
     * @param   double  maxValue        The maximum value to be represented by the full bar
     * @param   double  currentValue    The current value of the variable represented by the bar
     * @param   double  radius          The radius of the entire circle on which the bar is drawn
     * @param   double  width           The width of the actual bar around the circumference
     * @param   Color   gaugeColour     The colour of the space behind the bar
     * @param   Color   fillColour      The colour of the bar
     */
    public RadialMeter(Actor target, double maxValue, double currentValue,
    double radius, double width, Color gaugeColour, Color centreColour){
        if (target == null){    //If there is no target, accept the location assigned by the world and set alive to false
            targetAlive = false;    //The condition of the target is irrelevant to targetless situations
        } else {    //Meter will always display 20 pixels above its target if there is one
            targetAlive = true; //The target's existence will be monitored
            this.target = target;
        }
        //Sets the radius to a default value if the parameter value is less than 2.0
        if (radius <= 2.0){
            this.radius = 100.0;
        } else {
            this.radius = radius;
        }
        //Sets the width to a default value if the parameter value is less than 2.0
        if (width <= 2.0){
            this.width = 20.0;
        } else {
            this.width = width;
        }
        //Sets the colour of the meter's background to a new colour if provided, or to black if not
        if (gaugeColour != null){
            this.gaugeColour = gaugeColour;
        } else {
            this.gaugeColour = new Color(0, 0, 0);
        }

        if (centreColour != null){
            this.centreColour = centreColour;
        } else {
            this.centreColour = new Color(50, 255, 180);
        }
        //Sets the fill colour of the bar to a default value
        this.fillColour = new Color(85, 255, 30);
        //Sets the maximum value of the bar to a user-specified value, and the current value to the same value unless the user
        //specifies both a maximum value and a current value
        if (maxValue < 0.0){
            this.maxValue = 1000.0;
            this.currentValue = maxValue;
        } else {
            this.maxValue = maxValue;
            this.currentValue = currentValue;
        }

        //Calculates the circumference of the bar (currently of no use)
        circumference = (this.radius * 2.0 * Math.PI);
        //Creates a new image for the meter
        meterImage = new GreenfootImage((int)(radius*2.0), (int)(radius*2.0));

        //Finds the centre of the image, for reference in the method
        centreX = meterImage.getWidth()/2;
        centreY = meterImage.getHeight()/2;

        //Creates the basic image of the meter, with a background and a bar
        meterImage.setColor(fillColour);
        meterImage.fillOval(0, 0, (int)(radius*2), (int)(radius*2));
        meterImage.setColor(gaugeColour);
        //Draws a coloured circle in the centre of the bar for aesthetic purposes
        meterImage.fillOval((int)width, (int)width, (int)(radius - width) * 2, (int)(radius - width) * 2);
        meterImage.setColor(centreColour);
        meterImage.fillOval((int)(width * 0.9), (int)(width * 0.9), (int)(radius - width * 0.9) * 2, (int)(radius - width * 0.9) * 2);

        //Sets the image of the meter to the object
        setImage(meterImage);
    }

    /**
     * Every turn, the bar will change its position to remain above its target (if applicable), and will be removed
     * if its target is destroyed. Its image is also updated to include changes from the turn.
     */
    public void act(){
        if (target != null){
            followTarget(); //The meter follows the target
        } else if (target == null && targetAlive == true){  //If the target is no longer extant ...
            removeFromWorld();  //remove this object
        }
        setImage(meterImage);   //Set the image of the object to account for changes
    }

    /**
     * Deducts current value of bar by specified double amount, going no lower than zero,
     * and updates image of the bar.
     * 
     * @param   double  amount  The amount that is to be deducted from the current value of the bar
     */
    public void decreaseCurrentValue(double amount){
        if (currentValue != 0.0){
            currentValue -= amount; //Changes the current value of the bar
            if (currentValue < 0.0){    //Does not allow the bar's value to drop below 0.0
                currentValue = 0.0;
            }
        }

        //Updates the image to be set in the act method
        meterImage = updateImage();
    }

    /**
     * The bar's location is set to remain hovering over its target
     */
    private void followTarget(){
        setLocation(this.target.getX(), 
            this.target.getY() - this.target.getImage().getHeight() / 2 - 35);
    }

    /**
     * 
     */
    public double getCurrentValue(){
        return currentValue;
    }

    /**
     * Increases current value of bar by specified double amount, going no higher than its maximum value,
     * and updates image of the bar.
     * 
     * @param   double  amount  The amount that is to be added to the current value of the bar
     */
    public void increaseCurrentValue(double amount){
        if (currentValue != maxValue){
            currentValue += amount; //Changes the current value of the bar
            if (currentValue > maxValue){   //Does not allow the bar's value to exceed the maximum value
                currentValue = maxValue;
            }
        }

        //Updates the image to be set in the act method
        meterImage = updateImage();
    }

    /**
     * Changes current value of bar to specified double amount,
     * and updates image of the bar.
     * 
     * @param   double  amount  The amount that is to be the current value of the bar
     */
    public void setCurrentValue(double amount){
        currentValue = amount;

        //Updates the image to be set in the act method
        meterImage = updateImage();
    }

    /**
     * 
     */
    public void removeFromWorld(){
        getWorld().removeObject(this);  //remove this object
    }

    /**
     * Creates a new image for the the bar object, based on its current value.
     * 
     * @return  GreenfootImage  New image of the meter with appropriate changes.
     */
    private GreenfootImage updateImage(){
        GreenfootImage tempImage = new GreenfootImage((int)radius * 2, (int)radius * 2);    //Prepares a new blank image

        //Sets the fill colour according to the value of the bar; it changes with the value
        fillColour = new Color((255 - (int)(currentValue/maxValue * 255)), 
            (int)(currentValue/maxValue * 255), 0);

        //fills meter with appropriate colour of the bar
        tempImage.setColor(gaugeColour);
        tempImage.fillOval(0, 0, (int)radius * 2, (int)radius * 2);

        //Draws ovals around the circumference of the meter in order to mark off the filled fractions of the gauge
        tempImage.setColor(fillColour);
        for(double counter = Math.floor(currentValue)/maxValue * 2.0 * Math.PI, end = 0.1; counter >= end; counter -= 0.02){
            tempImage.fillOval(
                (int)Math.floor(centreX - (width * 0.5) + ((radius - width * 0.6) * Math.sin(counter))),
                (int)Math.floor(centreY - (width * 0.5) + ((radius - width * 0.6) * Math.cos(counter))),
                (int)(width),
                (int)(width));
        }

        //Draws a coloured circle in the centre of the bar for aesthetic purposes
        //tempImage.setColor(gaugeColour);
        //tempImage.fillOval((int)width, (int)width, (int)(radius - width) * 2, (int)(radius - width) * 2);
        tempImage.setColor(centreColour);
        //tempImage.fillOval((int)(width * 1.1), (int)(width * 1.1), (int)((radius - width) * 1.9), (int)((radius - width) * 1.9));
        tempImage.fillOval((int)(width), (int)(width), (int)(radius - width) * 2, (int)(radius - width) * 2);

        return tempImage;
    }
}
