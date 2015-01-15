import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
/**
 * Write a description of class Organism here.
 * 
 * @author Keith Wong, James Ly, Andy Lui, Daniel Chung
 * @version October 21, 2013
 */
public class Organism extends Actor
{
    //Life
    protected int age;  //The number of turns for which the organism has lived
    protected int lifeSpan; //The number of turns that the organism will live for
    protected int growthRate;   //Multiplied with actual growth time of an organism to determine how many turns it takes to 'level up'
    protected int health;
    protected int maxHealth;
    //Mating
    protected int r;    //Red
    protected int g;    //Green
    protected int b;    //Blue
    protected Color color;  //Colour generated from above 3
    protected int size;
    protected int mutationRate; //Rate of change in stats -- each generation differs from its parents by this absolute value
    //Immune System
    protected int immunity; //Current ability to fight off infection
    protected int maxImmunity;  //Highest capability to fight off infection 
    protected boolean diseased;
    //Combat/Defense
    protected int positionY,positionX;
    //Accessors ================================
    protected Color getColor(){
        return color;
    }

    protected int[] getColorRGB(){
        int[] colour = {r, g, b};
        return colour;
    }

    protected boolean getDiseased(){
        return diseased;
    }

    protected int getGrowthRate(){
        return growthRate;
    }

    protected int getHealth(){
        return health;
    }

    protected int getImmunity(){
        return immunity;
    }

    protected int getLifeSpan(){
        return lifeSpan;
    }

    protected int getMaxHealth(){
        return maxHealth;
    }

    protected int getMaxImmunity(){
        return maxImmunity;
    }

    protected int getMutationRate(){
        return mutationRate;
    }

    protected int getSize(){
        return size;
    }

    //Life and Death --------------------
    protected void die(){
        getWorld().removeObject(this);
    }

    //Other -----------------------------
    protected void hitDetection(){
        positionY = this.getY();
        //Collision detection for the left side 
        Actor b=(Actor)getOneObjectAtOffset(-this.getImage().getWidth()/2 ,0, Fauna.class);
        if (b!=null) {
            positionX = b.getX() + b.getImage().getWidth()/2 + this.getImage().getWidth()/2;
            this.setLocation (positionX, (int)positionY );
        }

        //Collision detection for right side 
        b=(Actor)getOneObjectAtOffset(this.getImage().getWidth()/2 ,0, Fauna.class);
        if (b!=null) {
            positionX = b.getX() - b.getImage().getWidth()/2 - this.getImage().getWidth()/2;
            this.setLocation (positionX, (int)positionY);
        }

        //Top
        b=(Actor)getOneObjectAtOffset(0 , -this.getImage().getHeight()/2, Fauna.class);
        if (b!=null) {
            positionY = b.getY()+ b.getImage().getHeight()/2 + this.getImage().getHeight()/2;
            this.setLocation (this.getX(), (int)positionY);
        }

        //Bottom
        b=(Actor)getOneObjectAtOffset(0 , this.getImage().getHeight()/2, Fauna.class);
        if (b!=null) {
            positionY = b.getY()- b.getImage().getHeight()/2 - this.getImage().getHeight()/2;
            this.setLocation (this.getX(), (int)positionY);
        }
    }

    /**
     * Will calculate acceptable value for double mutation, not dipping below 0
     * Note: Mutation rate must be an integer
     * 
     * @param   double  originalValue   The value to be altered
     * @param   int     maxMutationRate The maximum amount that could be added/subtracted
     * 
     * @return  double  The new mutated value, following appropriate calculations
     */
    protected double mutateDoubleAboveZero(double originalValue, int maxMutationRate){
        return (originalValue - Math.abs(maxMutationRate) >= 0) ? //Checks if the value would be lower than zero at its lowest extremity
            originalValue + Greenfoot.getRandomNumber(maxMutationRate) * (Greenfoot.getRandomNumber(3) - 1) :   //If greater than 0, add or subtract the randomised mutation rate
        originalValue + Greenfoot.getRandomNumber(maxMutationRate); //If not greater than 0, only add the randomised mutation rate
    }

    /**
     * Will calculate acceptable value for integer mutation, not dipping below 0
     * 
     * @param   int originalValue   The value to be altered
     * @param   int maxMutationRate The maximum amount that could be added/subtracted
     * 
     * @return  int The new mutated value, following appropriate calculations
     */
    protected int mutateIntAboveZero(int originalValue, int maxMutationRate){
        return (originalValue - Math.abs(maxMutationRate) >= 0) ? //Checks if the value would be lower than zero at its lowest extremity
            originalValue + Greenfoot.getRandomNumber(maxMutationRate) * (Greenfoot.getRandomNumber(3) - 1) :   //If greater than 0, add or subtract the randomised mutation rate
        originalValue + Greenfoot.getRandomNumber(maxMutationRate); //If not greater than 0, only add the randomised mutation rate
    }

    /**
     * Will calculate an acceptable value for an integer mutation, that does not surpass given boundaries
     * 
     * @param   int originalValue   The original value to be altered
     * @param   int maxMutationRate The maximum amount that the above value can be changed
     * @param   int max             The maximum of the range of values to be returned
     * @param   int min             The minimum of the range of values to be returned
     * 
     * @return  int The new value, calculated to fit within the range
     */
    protected int mutateWithinIntBounds(int originalValue, int maxMutationRate, int max, int min){
        int rate = Greenfoot.getRandomNumber(maxMutationRate) * (Greenfoot.getRandomNumber(3) - 1); //Determines the change to be applied

        int newValue = (originalValue + rate <= max) && (originalValue + rate >= min) ? //Checks if adding the mutation rate would exceed the limits
                originalValue + rate :  //Assigns the rate added to the original value if within bounds
            (originalValue + rate >= max) ? max : min;  //If exceeding max, assign max, else min

        return newValue;
    }
}
