import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * Write a description of class Bushes here.
 * 
 * @author Andy Lui
 * @version October 21, 2013
 */
public class Bush extends Flora
{
    //Beginning variables
    private boolean animalHidden; //If an animal is hiding in the bush.
    private int age, maxAge, height;
    private double nutritionOfFood;
    
    private double maxNutritionLevel, maxWaterLevel;
    private double nutritionPerTurn = 1.0;
    private double waterPerTurn = 1.0;
    private double nutritionLevel;
    private double waterLevel;
    private int timeEaten = 5; //How many times it can be eaten (from full) before it shrinks.
    
    final private int MAX_HEIGHT = 5;
    final private int GROWTH_PROPORTION = 2; //Changes how much the bush scales each time it increases in size.
    
    private static EvolutionWorld w;
    //GreenfootImage growImage = new GreenfootImage(this.getImage());
    GreenfootImage defaultImage = new GreenfootImage(this.getImage());
    GreenfootImage original = new GreenfootImage(this.getImage());
    
    /**
     * Creates the first bush.
     * 
     * @param EvolutionWorld w The World the bush is in.
     * @param double n How many nutrients does the bush need before it grows.
     * @param w How much water does the bush need before it can grow.
     * @param int timeEaten How many times the bush can be eaten before it shrinks in size from maxNutritionLevel (as defined above).
     */

    public Bush(EvolutionWorld w)
    {
        this.w = w;
        maxAge = 10*400; //Living for 10 days.
        maxNutritionLevel = 250; //Needed to grow in size.
        maxWaterLevel = 250;
        
        this.timeEaten = timeEaten;
        nutritionLevel = 1;
        waterLevel = 100;
        animalHidden = false;
        age = 0;
        height = 1;
  
    }
    
    /**
     * Secondary bushes that sprout after the original.
     * 
     * @param Bush b The original bush that it comes from.
     */
    public Bush (Bush b)
    {
        this.w = b.w;
        maxAge = b.maxAge - 1 + Greenfoot.getRandomNumber(3); //Living for 10 days.
        maxNutritionLevel = b.getMaxNutritionLevel(); //Needed to grow in size.
        maxWaterLevel = b.getMaxWaterLevel();
        
        timeEaten = b.getTimeEaten() - 1 + Greenfoot.getRandomNumber(3);
        nutritionLevel = 1 ;
        waterLevel = 100;
        animalHidden = false;
        age = 0;
        height = 1;
    }
    
    /**
     * Gets the maximum age of the bush.
     * 
     * @return int Returns the maximum age of a bush.
     */
    public int getMaxAge(){
        return maxAge;
    }
    
    //     public double getMaxNutritionLevel(){
    //         return maxNutritionLevel;
    //     }
    //     public double getMaxWaterLevel(){
    //         return maxWaterLevel;
    //     }
    /**
     * Gets how many times a bush can be eaten.
     * 
     * @return int Returns how many times the bush can be eaten/stage.
     */
    public int getTimeEaten(){
        return timeEaten;
    }
    
     /**
     * Act - do whatever the Bushes wants to do.
     */
    public void act() 
    {
        growBush();
 //       isAnimalHidden();
        
//         if (Greenfoot.isKeyDown("a"))
//             nutritionLevel += 10;
//             if (Greenfoot.isKeyDown("b"))
//             nutritionLevel -= 10;
    }    
    
    /**
     * Changes the size of the bush if a certain threshold is reached.
     * 
     * @param grow If true then it will grow bigger, if it is false then it will become smaller.
     */
    private void changeSize(boolean grow)
    {
        GreenfootImage growImage = new GreenfootImage(original);
        if (grow){
             //Scales the image to grow as the bush gains more nutrients (Size increases) 
            growImage.scale((int)(original.getWidth()+(GROWTH_PROPORTION*height)),(int) (original.getHeight()+(GROWTH_PROPORTION*height)));  
            //growImage = defaultImage;
        } else if (!grow && height >= 0){
            //TAKING FROM ORIGINAL!!
            growImage.scale((int)(defaultImage.getWidth()-(GROWTH_PROPORTION*height)),(int) (defaultImage.getHeight()-(GROWTH_PROPORTION*height)));
            //growImage = defaultImage;
        } 
        if (height == 0)
        { 
            growImage = new GreenfootImage(original);
        }
        this.setImage(growImage);
        this.defaultImage = new GreenfootImage (growImage);
        
    }
    
    private void isAnimalHidden()
    {
        //Detects whether or not there is an animal in the centre of the bush.
        //Used to determine whether or not another animal can hide in the bush.
        if (height > 0){
            Actor a = getOneObjectAtOffset(this.getX(), this.getY(), Actor.class);
            if (a != null)
                animalHidden = true;
        }
    }
    
    private void spawnNewBush()
    {
        int direction = Greenfoot.getRandomNumber(4);
       // Bush bush = new Bush(this, direction);
       List l = getObjectsInRange(80, Bush.class);
       if (l.size() < 2){
            if (direction == 0)
                getWorld().addObject(new Bush(w), this.getX(), this.getY() - 80);
            if (direction == 1)
                 getWorld().addObject(new Bush(w), this.getX(), this.getY() + 80);
            if (direction == 2)
                  getWorld().addObject(new Bush(w),this.getX() + 80, this.getY());
            if (direction == 3)
                  getWorld().addObject(new Bush(w), this.getX() - 80, this.getY());
       }
    }
    
    private void growBush()
    {
        age++;
        
        boolean enoughNutrient = absorbNutrient(nutritionPerTurn);
        boolean enoughWater = absorbWater(waterPerTurn);
        
        if (!enoughNutrient){
            nutritionLevel -= nutritionPerTurn;
        } else if (enoughNutrient)
            nutritionLevel += nutritionPerTurn;
        if (!enoughWater)
             waterLevel -= waterPerTurn;
        else if (enoughWater)
            waterLevel += waterPerTurn;
        
        //If it has enough nutrients to grow and it hasn't reached maximum potential.
        if (nutritionLevel > maxNutritionLevel && height < MAX_HEIGHT) //Parameters to be reworked.
        {
            height++;
            nutritionLevel = 1;
            changeSize(true); //Scales to the height when it succesfully grows
        } else if (nutritionLevel <= 0 && height >= 0){
            height--;
            nutritionLevel = maxNutritionLevel - 1;
            changeSize(false);
        }
        if (height == MAX_HEIGHT) {
            if (Greenfoot.getRandomNumber(3) == 0){
                height = 2;
                nutritionLevel = 1;
                changeSize(false);
                spawnNewBush();
            }
        }
        
        if (height < 0 || age >= maxAge || waterLevel == 0){
            getGrid().changeFertility(maxNutritionLevel);
            getGrid().changeMoisture(maxWaterLevel);
            getWorld().removeObject(this);
        }
           
        //To not let nutritionLevel overflow if needed
        if (nutritionLevel > maxNutritionLevel && height == MAX_HEIGHT)
            nutritionLevel = maxNutritionLevel;
        nutritionOfFood = maxNutritionLevel/timeEaten;
    }
    
    /**
     * When the plant is being eaten.
     * 
     * @return double[] How much nutrition the bush gives, how much water the bush gives.
     */
    public double[] digest()
    {
        nutritionLevel -= nutritionOfFood;
        //Reduces the size of the bush when it reaches the threshold.
        if (nutritionLevel <= 0 && height >= 0){
            height--;
            nutritionLevel = maxNutritionLevel;
            changeSize(false);
        }
        double[] foodAndWaterProvided = {nutritionOfFood, 0};
        return foodAndWaterProvided;
    }
    
    /**
     * If an animal is hidden in the bush.
     * 
     * @return boolean True if an animal is hidden.
     */
    public boolean getAnimalHidden()
    {
        isAnimalHidden();
        return animalHidden;
    }
    
   
    
   
}
