import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;

/**
 * The fruit-bearing organism which sustains the livelihood of prey
 * 
 * @author Daniel Chung 
 * @version Monday, October 21st, 2013
 */
public class Tree extends Flora
{
    //Fruit-bearing determinants
    private double nutrition;   //The amount of nutrition that a fruit spawned will provide
    private double water;   //The amount of water that a fruit spawned will provide
    private double foodProvidedPerTurn; //The food provided by a fruit per turn
    private double waterProvidedPerTurn;    //The water provided by a fruit per turn
    private double rotRate;
    //Counters and growth
    private int fruitGrowTime;  //The number of fruitGrowthCounters that a fruit needs to spawn
    private int fruitGrowthCounter; //The amount of time that the tree has grown without problems, counted toward the spawning of fruit
    private int treeGrowTime;   //Tree will grow in stages, which each require the treeGrowTime-specified number of turns to reach
    private int treeGrowthCounter;

    private boolean living; //Used to keep replicas from doing anything

    //A copy of the world, for reference purposes
    private static EvolutionWorld world;

    //Image
    private final static GreenfootImage classImage = new GreenfootImage("tree.png");
    private GreenfootImage image;

    /**
     * The first tree, with default values
     * 
     * @param   EvolutionWorld  world   The EvolutionWorld into which the tree has been spawned
     */
    public Tree(EvolutionWorld world){ 
        this.world = world;

        //Life
        living = true;
        age = 0;
        lifeSpan = world.getLengthOfDay() * 20;    //Tree will live for 20 days

        //Colour
        r = 100;
        g = 255;
        b = 100;
        color = new Color(r, g, b);

        //Immunity
        immunity = 0;
        maxImmunity = 1000;

        //Nutrition and water levels and rates of change
        nutritionLevel = 50.0;
        waterLevel = 50.0;
        maxNutritionLevel = 300.0;
        maxWaterLevel = 300.0;
        nutritionPerTurn = 1.0;
        waterPerTurn = 1.0;

        //Health
        health = 1000;
        maxHealth = 1000;

        //Size
        size = 1;
        growthRate = 1;

        //Rate of mutation
        mutationRate = 1;

        //Illness?
        diseased = false;

        //Attributes of fruit created by this tree
        nutrition = 150.0;
        water = 150.0;
        foodProvidedPerTurn = 4.0;
        waterProvidedPerTurn = 4.0;
        rotRate = 5.0;
        radiusOfInfluence = 100;    //The range within which fruit can naturally spawn

        //The counters used for growth in the tree
        fruitGrowTime = 200;
        treeGrowthCounter = 0;
        treeGrowTime = 300;
        treeGrowthCounter = 0;

        resize();
    }

    /**
     * Constructor for a tree with a parent; mutates values of the parent
     * 
     * @param   Tree    parent  A Tree object reference to the parent tree
     * @param   boolean living  Whether the tree is living, or just a duplicate kept as a reference in a fruit
     */
    public Tree(Tree parent, boolean living){
        //Life
        age = 0;
        this.living = living;
        lifeSpan = world.getLengthOfDay() * (20 + Greenfoot.getRandomNumber(parent.getMutationRate()) * (Greenfoot.getRandomNumber(3) - 1));

        //Adds a mutation-rate-determined amount to each colour, and ensures that the result is within the bounds of 0 to 255
        r = mutateWithinIntBounds(parent.getColorRGB()[0], parent.getMutationRate(), 0, 255);
        g = mutateWithinIntBounds(parent.getColorRGB()[1], parent.getMutationRate(), 0, 255);
        b = mutateWithinIntBounds(parent.getColorRGB()[2], parent.getMutationRate(), 0, 255);
        color = new Color(r, g, b);

        //Immunity
        this.maxImmunity = parent.getMaxImmunity() + Greenfoot.getRandomNumber(parent.getMutationRate()) * (Greenfoot.getRandomNumber(3) - 1);
        immunity = maxImmunity / 10;    //Begin with a tenth of maximum immunity

        //Nutrition and water levels and rates of change
        nutritionLevel = 50.0;
        waterLevel = 50.0;
        maxNutritionLevel = mutateDoubleAboveZero(parent.getMaxNutritionLevel(), parent.getMutationRate());
        maxWaterLevel = mutateDoubleAboveZero(parent.getMaxWaterLevel(), parent.getMutationRate());
        nutritionPerTurn = mutateDoubleAboveZero(parent.getNutritionPerTurn(), parent.getMutationRate());
        waterPerTurn = mutateDoubleAboveZero(parent.getWaterPerTurn(), parent.getMutationRate());

        //Health
        this.maxHealth = parent.getMaxHealth() + Greenfoot.getRandomNumber(parent.getMutationRate()) * (Greenfoot.getRandomNumber(3) - 1);
        health = maxHealth;

        //Size
        size = 1;
        this.growthRate = parent.getGrowthRate() + Greenfoot.getRandomNumber(parent.getMutationRate()) * (Greenfoot.getRandomNumber(3) - 1);

        //Rate of mutation
        this.mutationRate = parent.getMutationRate() + Greenfoot.getRandomNumber(parent.getMutationRate()) * (Greenfoot.getRandomNumber(3) - 1);

        //Is the tree ill?
        diseased = false;

        //Attributes of fruit created by this tree
        nutrition = mutateIntAboveZero((int)parent.getNutrition(), parent.getMutationRate());
        water = mutateIntAboveZero((int)parent.getWater(), parent.getMutationRate());
        foodProvidedPerTurn = 1.0;
        waterProvidedPerTurn = 1.0;
        rotRate = 2.0;
        radiusOfInfluence = mutateIntAboveZero(parent.getRadiusOfInfluence(), parent.getMutationRate());

        //Counters for tree;
        fruitGrowTime = mutateIntAboveZero(parent.getFruitGrowTime(), parent.getMutationRate());
        treeGrowTime = mutateIntAboveZero(parent.getTreeGrowTime(), parent.getMutationRate());

        resize();
    }

    //Accessors ==================================
    /**
     * @return  double  The food provided by a fruit produced by this tree, per turn
     */
    public double getFoodProvidedPerTurn(){
        return foodProvidedPerTurn;
    }

    /**
     * @return  int the time it takes for a fruit to grow in turns
     */
    public int getFruitGrowTime(){
        return fruitGrowTime;
    }

    /**
     * @return  double  The amount of nutrition currently stored in the tree
     */
    public double getNutrition(){
        return nutrition;
    }

    /**
     * @return  double  The water provided by a fruit produced by this tree, per turn
     */
    public double getWaterProvidedPerTurn(){
        return waterProvidedPerTurn;
    }

    /**
     * @return  int The radius within which the tree's fruit can spawn alone
     */
    public int getRadiusOfInfluence(){
        return radiusOfInfluence;
    }

    /**
     * @return  double  The rate at which fruit rot
     */
    public double getRotRate(){
        return rotRate;
    }

    /**
     * @return  int The number of turns that it takes for the tree to grow up one stage
     */
    public int getTreeGrowTime(){
        return treeGrowTime;
    }

    /**
     * @return  double  The water currently stored within the tree
     */
    public double getWater(){
        return water;
    }

    //Life and death --------------------------
    /**
     * Increases the age of the tree, and increases all of its stats if it grows up one stage
     */
    private void grow(){
        age++;
        if (treeGrowthCounter == treeGrowTime){
            maxImmunity++;
            maxHealth++;
            size++;
            nutrition += 20.0;
            water += 20.0;

            nutritionLevel = 50.0;
            waterLevel = 50.0;
            maxNutritionLevel += 100.0;
            maxWaterLevel += 100.0;
            nutritionPerTurn += 0.5;
            waterPerTurn += 0.5;

            fruitGrowTime -= 10;
            treeGrowTime += 100;
            treeGrowthCounter = 0;

            resize();
        }
    }

    /**
     * Restores a damaged immune system to full strength, and reduced health back to full health
     */
    private void recover(){
        immunity = immunity < maxImmunity ? immunity + 1 : immunity;
        health = health < maxHealth ? health + 1 : health;
    }

    /**
     * Scales the image of the tree to fit its size
     */
    private void resize(){
        image = new GreenfootImage(classImage);
        image.scale(10 * size, 20 * size);
        setImage(image);
    }

    /**
     * Creates a new fruit within the radius of influence of the tree
     */
    public void spawnFruit(){
        world.addObject(new Fruit(this), 
            getX() + (Greenfoot.getRandomNumber(radiusOfInfluence) + 25) * (Greenfoot.getRandomNumber(3) - 1),
            getY() - (Greenfoot.getRandomNumber(radiusOfInfluence) + 25) * (Greenfoot.getRandomNumber(3) - 1));
    }

    /**
     * If the tree is a living tree (and not a fruit reference replica), the tree will absorb nutrients and grow, or if it cannot
     * absorb nutrients, move towards death. During normal growth, the counters of the tree all increase.
     */
    public void act() 
    {
        if (living){
            boolean enoughNutrition = absorbNutrient(nutritionPerTurn);
            boolean enoughWater = absorbWater(waterPerTurn);

            if (!enoughNutrition){
                nutritionLevel = (nutritionLevel - nutritionPerTurn) < 0.0 ? 0.0 : nutritionLevel - nutritionPerTurn;
                treeGrowthCounter = (treeGrowthCounter - 1) < 0 ? 0 : treeGrowthCounter--;
                fruitGrowthCounter = (fruitGrowthCounter - 1) < 0 ? 0 : fruitGrowthCounter--;
                health--;
            } else if (!enoughWater){
                waterLevel = (waterLevel - waterPerTurn) < 0.0 ? 0.0 : waterLevel - waterPerTurn;
                treeGrowthCounter = (treeGrowthCounter - 1) < 0 ? 0 : treeGrowthCounter--;
                fruitGrowthCounter = (fruitGrowthCounter - 1) < 0 ? 0 : fruitGrowthCounter--;
                health--;
            } else {
                nutritionLevel -= nutritionPerTurn;
                waterLevel -= waterPerTurn;

                treeGrowthCounter++;
                fruitGrowthCounter++;
                if (fruitGrowthCounter >= fruitGrowTime){
                    fruitGrowthCounter = 0;
                    spawnFruit();
                }
                recover();
            }

            grow();

            //Kills the plant and returns its nutrients and water to its grid if it dies of old age of lack of health
            if (health <= 0 || age == lifeSpan){
                getGrid().changeFertility(nutritionLevel);
                getGrid().changeMoisture(waterLevel);
                getWorld().removeObject(this);
            }
        }
    }
}
