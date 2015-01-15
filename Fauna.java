import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;
import java.util.*;
/**
 * Superclass of Prey and Predator, contains methods that are acessible by both.
 * 
 * @author Keith Wong, James Ly
 * @version Oct 21, 2013
 */
public abstract class Fauna extends Organism
{
    /**
     * Act - do whatever the Fauna wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    //     //inherited from Organisms 
    //     protected int age;
    // 
    //     //color
    //     protected int r;
    //     protected int g;
    //     protected int b;
    //     protected Color color;
    // 
    //     //immunity
    //     protected int immunity;
    //     protected int maxImmunity;
    // 
    //     protected int health;
    //     protected int maxHealth;
    // 
    //     protected int mutationRate;
    // 
    //     protected boolean diseased;
    // 
    //     //exclusive to Fauna
    //     private boolean gestation = false;
    //     private int gestationPeriod;
    //     private int currentGestation;
    // 
    //     private int speed;
    // 
    //     private int hunger;
    //     private int maxHunger;
    //     private int thirst;
    //     private int maxThirst;
    // 
    //     private int sleepTime;
    //     private boolean isSleeping;
    // 
    //     private boolean poisoned;
    //     private boolean isMale; // male = true
    // 
    //     private Fauna mateID;
    // 
    //     private int worldX;
    //     private int worldY;
    // 
    //     private int moveStatus;
    //     private int restCounter;
    // 
    //     //destination for the animal
    //     private int desX;
    //     private int desY;

    /**
     * Averages the values of the male and female, and mutates it slightly based on mutation rates
     * 
     * @param female The value of the female that you are trying to get for the baby
     * @param male The value of them ale that you are trying to get for the baby
     * @param mutationRateF The mutation rate of the female
     * @param mutationRateM The mutation rate of the male
     * 
     */
    protected int getBabyValue(int female, int male, int mutationRateF, int mutationRateM){
        boolean adding;
        //50% change to increase or decrease stats
        if (Greenfoot.getRandomNumber(2) == 0)
            adding = true;
        else
            adding = false;
        int value;
        //finds how much to mutate the value by
        int mutationRate = ((Greenfoot.getRandomNumber(mutationRateF) + Greenfoot.getRandomNumber(mutationRateM))/2);
        if (adding == true)
            value = (((female + male)/2) + mutationRate);
        else
            value = (((female + male)/2) - mutationRate);

        //makes sure the values do not go too low
        if (value < 2)
            value = 2;
        return value;
    }

    /**
     * Finds the angle between two objects. Organism a is the organism that you are basing the angle off of (the "Origin")
     * 
     *@param a the first organism, angle will be based off this one.
     *@param b the 2nd organism.
     */ 
    protected int angleBetweenObjects(Organism a, Organism b){
        //finding the coordinates and then the distances between them.
        int aXCoor = a.getX();
        int aYCoor = a.getY();
        int bXCoor = b.getX();
        int bYCoor = b.getY();
        int deltaX;
        int deltaY;

        deltaX = aXCoor - bXCoor;
        deltaY = aYCoor - bYCoor;
        //tangent.
        return (int) Math.toDegrees(Math.atan2((double)deltaY,(double) deltaX));
    }

    /**
     * finds the distance between two objects.
     * 
     * @param a The first organism.
     * @param b The 2nd organism. 
     */
    protected int distanceBetweenObjects(Organism a, Organism b){
        //finding the coordinates and then the distances between them.
        double aXCoor = a.getX();
        double aYCoor = a.getY();
        double bXCoor = b.getX();
        double bYCoor = b.getY();
        double deltaX;
        double deltaY;

        if (aXCoor >= bXCoor)
            deltaX = aXCoor - bXCoor;
        else 
            deltaX = bXCoor - aXCoor;

        if (aYCoor >= bYCoor)
            deltaY = aYCoor = bYCoor;
        else
            deltaY = bYCoor - aYCoor;
        //pythagorean theorem
        return (int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
    }

    public abstract void subtractSpeed(int subtract);

    public abstract void subtractHunger(int subtract);

    public abstract void subtractThirst(int subtract);
}
