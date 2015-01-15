import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
/**
 * Write a description of class Predator here.
 * 
 * @author James Ly
 * @version October 21 2013
 */
public class Predator extends Fauna
{
    /**
     * Act - do whatever the Predator wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    //inherited variables
    private boolean gestation = false;
    private int gestationPeriod = randInt(300) + 700;;
    private int speed = 1;
    private int thirst;
    private int maxThirst;
    private int lethality;
    private int exp;
    private int sleepTime;
    private boolean isSleeping;
    private boolean poisened;
    private boolean isMale; // male = true
    private int immunity;
    private int maxImmunity;
    private boolean diseased = false;
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private int maxHealth = 100;
    private int health = maxHealth;
    private double maxHunger = 250;
    private double hunger = maxHunger;

    //MY OWN VARIABLES TY VERY MUCH
    private final int worldX = 960;
    private final int worldY = 640;
    private int desX = Greenfoot.getRandomNumber(worldX);
    private int desY = Greenfoot.getRandomNumber(worldY);
    private int status = 0;// 0 = walking 1 = resting 2 = hunting
    private int kills = 0;
    private int restCounter;
    private ArrayList<Prey> preyInRange;
    private ArrayList<Predator> possibleMates;
    private int numPreyInRange;
    private int numMatesInRange;
    private final double hungerDecayRate = .18;
    private int rangeOfSight = 150;
    private boolean findMate = false;
    private int mateFoundID;
    private boolean mateAquired = false;
    private double distanceToMate;
    private int preyR;
    private int preyG;
    private int preyB;
    private int mySightR;
    private int mySightG;
    private int mySightB;
    private int rangeOfColor;
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean alive = true;
    private int rotTime = 200;

    public Predator()
    {
        if (Greenfoot.getRandomNumber(2) == 0)
        {
            isMale = true;
            this.setImage("Lion.png");
        }
        else
        {
            isMale = false;
            this.setImage("F_Lion1.png");
        }
        mySightR = randInt(256);
        mySightG = randInt(256);
        mySightB = randInt(256);
        rangeOfColor = randInt(20) + 25;
    }

    public void act()
    {
        // Add your action code
        if (alive)
        {
            if (findMate == false)
            {
                if (status == 0)
                {
                    turnTowards(desX,desY);
                    move(speed);
                    if (CheckDesReached())
                    {
                        if (randInt(2) == 0)
                        {
                            newDes();
                        }
                        else
                        {
                            startRest();
                        }
                    }
                }
                if (status == 1)//if resting
                {
                    restCounter --;
                    if (restCounter == 0)
                    {
                        status = 0;
                    }
                }
                getHungery();
                if (hunger <= 0)
                {
                    status = 2;
                }

                if (status == 2)
                {
                    if (checkForPrey())
                    {
                        huntForPrey();
                    }
                    else
                    {
                        turnTowards(desX,desY);
                        move(speed);
                        if (CheckDesReached())
                        {
                            if (randInt(2) == 0)
                            {
                                newDes();
                            }
                            else
                            {
                                status = 1;
                                restCounter = randInt(300);
                            }
                        }
                    }
                }
                matingProcedures();
            }
            else
            {
                possibleMates = (ArrayList) getNeighbours(10000, true, Predator.class);
                numMatesInRange = possibleMates.size();
                for (int i = 0; i < numMatesInRange; i++)
                {
                    if (possibleMates.get(i).getMateStatus() && this.isMale != possibleMates.get(i).getGender())
                    {
                        mateFoundID = i;
                        mateAquired = true;
                        possibleMates = (ArrayList) getNeighbours(10000, true, Predator.class);
                        distanceToMate =  Math.sqrt((double)((Math.pow((double)(this.getX() - possibleMates.get(mateFoundID).getX()), 2) + Math.pow((double)(this.getY() - possibleMates.get(mateFoundID).getY()), 2))));
                        turnTowards (possibleMates.get(mateFoundID).getX(),possibleMates.get(mateFoundID).getY());
                        break;
                    }
                }
                if (mateAquired)
                {
                    if (distanceToMate > (double)85)
                    {
                        move(1);
                        gestationPeriod = 200;
                    }
                    else
                    {
                        gestationPeriod--;
                    }
                    if (gestationPeriod <= 0)
                    {
                        if (isMale == false)
                        {
                            getWorld().addObject(new Predator(), this.getX(), this.getY());
                        }
                        mateAquired = false;
                        findMate = false;
                        gestationPeriod = randInt(300) + 600;
                    }
                }
                else
                {
                    if (CheckDesReached())
                    {
                        if (randInt(2) == 0)
                        {
                            newDes();
                        }
                        else
                        {
                            startRest();
                        }
                    }
                    turnTowards(desX,desY);
                    move(speed);
                }
            }
            getHungery();
            checkIfStarving();
            checkIfDie();
        }
        else
        {
            rotTime--;
            if (rotTime <= 0)
            {
                getGrid().changeMoisture(getGrid().getMoisture()+1500);
                getGrid().changeFertility(getGrid().getFertility()+1500);
                getWorld().removeObject(this);
            }
        }
    }

    private void newDes()
    {
        desX = Greenfoot.getRandomNumber(worldX);
        desY = Greenfoot.getRandomNumber(worldY);
    }

    private boolean CheckDesReached()
    {
        boolean reached = false;
        double distance =  Math.sqrt((double)((Math.pow((double)(this.getX() - desX), 2) + Math.pow((double)(this.getY() - desY), 2))));
        if (distance <= 10)
        {
            reached = true;
        }
        return reached;
    }

    private int randInt(int exclusiveNum)
    {
        int randInt = Greenfoot.getRandomNumber(exclusiveNum);
        return randInt;
    }

    private void startRest()
    {
        status = 1;
        restCounter = randInt(300);
    }

    private void getHungery()
    {
        hunger = hunger - hungerDecayRate;
    }

    private boolean checkForPrey()
    {
        preyInRange = (ArrayList) getNeighbours(rangeOfSight, true, Prey.class);
        numPreyInRange = preyInRange.size();
        boolean preyInRangeTrue = false;
        if (numPreyInRange > 0)
        {
            preyInRangeTrue = true;
        }
        return preyInRangeTrue;
    }

    private void huntForPrey()
    {
        preyInRange = (ArrayList) getNeighbours(rangeOfSight, true, Prey.class);
        numPreyInRange = preyInRange.size();
        if (preyInRange != null)
        {
            double distanceOfClosest = 1000000;
            int newTargetID = 0;
            for (int i = 0; i < numPreyInRange ; i++)
            {
                if (distanceOfClosest < Math.sqrt((double)((Math.pow((double)(this.getX() - preyInRange.get(i).getX()), 2) + Math.pow((double)(this.getY() - preyInRange.get(i).getY()), 2)))))
                {
                    distanceOfClosest =  Math.sqrt((double)((Math.pow((double)(this.getX() - preyInRange.get(i).getX()), 2) + Math.pow((double)(this.getY() - preyInRange.get(i).getY()), 2))));

                    if (preyInRange.get(i).getR() > mySightR - rangeOfColor && preyInRange.get(i).getR() < mySightR + rangeOfColor)
                    {
                        if (preyInRange.get(i).getG() > mySightG - rangeOfColor && preyInRange.get(i).getG() < mySightG + rangeOfColor)
                        {
                            if (preyInRange.get(i).getB() > mySightB - rangeOfColor && preyInRange.get(i).getB() < mySightB + rangeOfColor)
                            {
                                preyInRange.get(newTargetID).setTarget(null);
                                newTargetID = i;
                                preyInRange.get(newTargetID).setTarget(this);
                            }
                        }
                    }
                }
            }
            turnTowards(preyInRange.get(newTargetID).getX(),preyInRange.get(newTargetID).getY());
            move(speed*2);
            if (20 >= Math.sqrt((double)((Math.pow((double)(this.getX() - preyInRange.get(newTargetID).getX()), 2) + Math.pow((double)(this.getY() - preyInRange.get(newTargetID).getY()), 2)))))
            {
                preyInRange.get(newTargetID).die();
                health = maxHealth;
                hunger = maxHunger;
                kills++;
                lvlUp();
                startRest();
            }
        }
    }

    private void lvlUp()
    {
        lethality = kills*4;
        rangeOfSight = rangeOfSight + (int)((double)lethality * 1.5);
    }

    private void checkIfStarving()
    {
        if (hunger <= -250)
        {
            health--;
        }
    }

    private void checkIfDie()
    {
        if (health <= 0)
        {
            alive = false;
            if (isMale)
            {
                this.setImage("LionDead.png");
            }
            else
            {
                this.setImage("F_Lion1Dead.png");
            }
        }
    }

    private void matingProcedures()
    {
        gestationPeriod--;
        if (gestationPeriod <= 0)
        {
            findMate = true;
        }
    }

    public boolean getDiseased()
    {
        return diseased;
    }

    public int getImmunity()
    {
        return immunity;
    }

    public int getMaxImmunity()
    {
        return maxImmunity;
    }

    public int getHealth()
    {
        return health;
    }

    public void changeHealth(int newHealth)
    {
        health = newHealth;
    }

    public void cureMe(boolean cure)
    {
        diseased = cure;
    }

    public boolean getMateStatus()
    {
        return findMate;
    }

    public boolean getGender()
    {
        return isMale;
    }

    public void subtractSpeed(int subtract)
    {
    }

    public void subtractHunger(int subtract)
    {
    }

    public void subtractThirst(int subtract)
    {
    }

    private Grid getGrid(){
        return (Grid)getOneObjectAtOffset(0, 0, Grid.class);
    }
}