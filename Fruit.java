import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Fruit objects are created once the fruit growing timer on a tree reaches the fruit grow time,
 * after which the fruit will rot until eaten.
 * 
 * @author Daniel Chung
 * @version Monday, October 21st, 2013
 */
public class Fruit extends Flora
{
    //Lifetime stats
    private double nutrition;
    private double water;
    private double foodProvidedPerTurn;
    private double waterProvidedPerTurn;
    private double rotRate;
    private int rotTime;
    private boolean eaten;
    //Spawning stats
    private Tree parent;    //A replica of the tree which produced this fruit
    private GreenfootImage image = new GreenfootImage("apple.png");

    /**
     * @param   Tree    parent  A reference for the replication of a parent with all the stats required for spawning another tree from this fruit
     */
    public Fruit(Tree parent){
        this.parent = new Tree(parent, false);

        this.nutrition = parent.getNutrition();
        this.water = parent.getWater();
        this.foodProvidedPerTurn = parent.getFoodProvidedPerTurn();
        this.waterProvidedPerTurn = parent.getWaterProvidedPerTurn();
        this.rotRate = parent.getRotRate();
        image.scale(10, 10);
        setImage(image);
    }

    //Accessors =========================================================
    /**
     * @return  boolean Whether the fruit has been eaten or not
     */
    public boolean getEaten(){
        return eaten;
    }

    //Modifiers =========================================================
    /**
     * Sets the value of eaten to true
     */
    public void setAsEaten(){
        eaten = true;
    }

    /**
     * Ensures that a fruit, if eaten, follows whoever ate it, and provides them food and water
     * 
     * @param   Organism    eater   The organism which has consumed this fruit
     * @return  double[]    Array containing [0] amount of hunger satiated, [1] amount of thirst satiated
     */
    public double[] digest(Organism eater){
        setLocation(eater.getX(), eater.getY());

        foodProvidedPerTurn = (double) (nutrition > foodProvidedPerTurn ? foodProvidedPerTurn : 0.0);
        waterProvidedPerTurn = (double) (water > waterProvidedPerTurn ? waterProvidedPerTurn : 0.0);
        if (nutrition <= 0.0){
            sprout();
        }

        double[] foodAndWaterProvided = {foodProvidedPerTurn, waterProvidedPerTurn};

        return foodAndWaterProvided;
    }

    /**
     * Depletes values of nutrition and water every turn that the fruit is not eaten
     */
    private void rot(){
        nutrition -= rotRate;
        water -= rotRate;

        if (nutrition <= 0.0){
            sprout();
        }
    }

    /**
     * Creates a brand new tree in the spot that this fruit disappears, 20% of the time
     */
    public void sprout(){
        try{
            if(Greenfoot.getRandomNumber(5) == 1){
                getWorld().addObject(new Tree(parent, true), getX(), getY());
            }
            getWorld().removeObject(this);
        } catch (Exception e){}
    }

    /**
     * Rot the fruit if it is not being eaten
     */
    public void act() 
    {
        if (!eaten){
            rot();
        }
    }    
}
