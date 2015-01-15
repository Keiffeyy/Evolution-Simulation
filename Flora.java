import greenfoot.*;   //(World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.*;
/**
 * Superclass for trees, bushes and fruit.
 * 
 * @author Andy Lui, Daniel Chung
 * @version Monday, October 21st, 2013
 */
public abstract class Flora extends Organism
{
    //Vital Stats
    //protected int height;
    protected double maxNutritionLevel, maxWaterLevel;  //The maximum amount of nutrition and water that can be held in one turn
    protected double nutritionLevel, waterLevel;    //The current level of nutrition and water held
    protected double nutritionPerTurn, waterPerTurn;    //The nutrition and water consumed per turn

    //Fauna-interaction
    protected boolean poisonous;

    //Reproduction
    protected int radiusOfInfluence;    //Area in which seedlings can sprout directly from plant

    /**
     * Finds the grid that the flora is positioned on
     * 
     * @return  Grid    the grid on which the object is currently positioned
     */
    protected Grid getGrid(){
        return (Grid)getOneObjectAtOffset(0, 0, Grid.class);
    }

    //Accessors ================================================
    /**
     * @return  double  The maximum nutrition level of the plant
     */
    public double getMaxNutritionLevel(){
        return maxNutritionLevel;
    }

    /**
     * @return  double  The maximum water level of the plant
     */    
    public double getMaxWaterLevel(){
        return maxWaterLevel;
    }

    /**
     * @return  double  The current nutrition level of the plant
     */
    public double getNutritionLevel(){
        return nutritionLevel;
    }

    /**
     * @return  double  The current water level of the plant
     */
    public double getWaterLevel(){
        return waterLevel;
    }

    /**
     * @return  double  The nutrients required by the plant each turn
     */
    public double getNutritionPerTurn(){
        return nutritionPerTurn;
    }

    /**
     * @return  double  The water required by the plant each turn
     */
    public double getWaterPerTurn(){
        return waterPerTurn;
    }

    /**
     * @return  boolean Whether a point currently has another plant occupying it
     */
    public boolean isOccupied()
    {
        if (getNeighbours (10, true, Flora.class) != null)
        {
            return true;
        } else
            return false;
    }

    /**
     * @param   double  nutrientNeeded  The amount of nutrients required
     * @return  boolean Whether there were enough nutrients or not in the grid
     */
    protected boolean absorbNutrient(double nutrientNeeded)
    {
        boolean enoughNutrients;
        Grid grid = getGrid();

        if (grid.getFertility() - nutrientNeeded > 0.0){
            //If adding n nutrients will surpass maxNutritionLevel 
            if (nutritionLevel + nutrientNeeded > maxNutritionLevel)
            {
                //How many nutrients it takes to reach maxNutritionLevel.
                //(To exactly reach it and not surpass/take extra nutrients from tile)
                nutrientNeeded = (nutritionLevel + nutrientNeeded) - maxNutritionLevel;
            }

            if (nutritionLevel < maxNutritionLevel)
            {
                //Gets the tile that the flora is located on and takes nutrients from the tile
                // and adds it to the itself.
                nutritionLevel += nutrientNeeded;
            }
            grid.changeFertility(-nutrientNeeded);
            enoughNutrients = true;
        } else {
            enoughNutrients = false;
        }
        return enoughNutrients;
    }

    /**
     * @param   double  waterNeeded  The amount of water required
     * @return  boolean Whether there was enough water or not in the grid
     */
    protected boolean absorbWater (double waterNeeded)
    {
        boolean enoughWater;
        Grid grid = getGrid();

        if (grid.getMoisture() - waterNeeded > 0.0){
            //If adding too much water
            if (waterLevel + waterNeeded > maxWaterLevel)
            {
                //How much water it takes to reach maxNutritionLevel.
                // (To exactly reach it and not surpass/take extra water from tile)
                waterNeeded = (waterLevel + waterNeeded) - maxWaterLevel;
            }

            if (waterLevel < maxWaterLevel)
            {
                //Gets the moisture level on the tile and adds it to itself.
                grid.changeMoisture(-waterNeeded);
                waterLevel += waterNeeded;
            }
            enoughWater = true;
        } else {
            enoughWater = false;
        }
        return enoughWater;
    }
} 