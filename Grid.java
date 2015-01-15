import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;

/**
 * Grids contain all necessary information (i.e. fertility, moisture) for flora growth of any sort
 * 
 * @author Daniel Chung
 * @version Monday, October 21st, 2013
 */
public class Grid extends Actor
{
    //Grid factors
    private double neutralMoistureLevel = 4000.0;
    private double neutralNutrientLevel = 4000.0;
    private double moistureLevel = neutralMoistureLevel;
    private double nutrientLevel = neutralNutrientLevel;
    private double deAndReHydrationRate = 2.0;
    private double deAndReNutritionRate = 2.0;
    private int floodTimer = 0;
    private int droughtTimer = 0;
    //Colour determinants
    private int red = 100;
    private int green = 255 * (int)(nutrientLevel / (neutralNutrientLevel * 1.5)) > 255 ? 255 : 255 * (int)(nutrientLevel / (neutralNutrientLevel * 1.5));
    private int blue = 255 * (int)(moistureLevel / (neutralMoistureLevel * 1.5)) > 255 ? 255 : 255 * (int)(moistureLevel / (neutralMoistureLevel * 1.5));
    private Color colour = new Color(red, green, blue);
    //Images
    private GreenfootImage image = new GreenfootImage(80, 80);
    private int gridNumber;

    /**
     * Creates a grid with an index number, location, and image
     * 
     * @param   int gridNumber  The index number of the grid
     * @param   int positionX   The horizontal position of the grid
     * @param   int positionY   The vertical position of the grid
     */
    public Grid(int gridNumber, int positionX, int positionY){
        this.gridNumber = gridNumber;
        setLocation(positionX, positionY);

        image.setColor(changeColour());
        image.fill();
        setImage(image);
    }

    //Accessors ==================================================================
    /**
     * @return  double  The level of nutrients currently stored in this grid
     */
    public double getFertility(){
        return nutrientLevel;
    }

    /**
     * @return  double  The level of moisture currently stored in this grid
     */
    public double getMoisture(){
        return moistureLevel;
    }

    //Modifiers =====================================================================

    /**
     * Changes the colour values of the grid based on the fertility and moisture levels
     */
    private Color changeColour(){
        red = 150;
        green = (int)(255.0 * (nutrientLevel / neutralNutrientLevel)) > 255 ? 255 : (int)(255.0 * (nutrientLevel / neutralNutrientLevel));
        blue = (int)(155.0 * (moistureLevel / neutralMoistureLevel)) > 155 ? 155 : (int)(155.0 * (moistureLevel / neutralMoistureLevel));
        return new Color(red, green, blue);
    }

    /**
     * Adds the input double amount of moisture to the moisture level of the grid
     * 
     * @param   double  amount  The amount of moisture to add to the moisture level of the grid
     */
    public void changeMoisture(double amount){
        moistureLevel = (moistureLevel + amount) < 0 ? 0 : moistureLevel + amount;
    }

    /**
     * Adds the input double amount of fertility to the nutrition level of the grid, but does not allow fertility to drop below zero
     * 
     * @param   double  amount  The amount of fertility to add to the nutrition level of the grid
     */
    public void changeFertility(double amount){
        nutrientLevel = (nutrientLevel + amount) < 0 ? 0 : nutrientLevel + amount;
    }

    /**
     * Depletes the timers for the different disasters afflicting grid, and resets stats of grid when the disaster is over
     */
    private void depleteDisasterTimers(){
        if (floodTimer > 0 || droughtTimer > 0){
            floodTimer = floodTimer > 0 ? floodTimer-- : 0;
            droughtTimer = droughtTimer > 0 ? droughtTimer-- : 0;
            if (floodTimer == 0 && droughtTimer == 0){
                neutralMoistureLevel = 5000.0;
                deAndReHydrationRate = 2.0;
            }
        }
    }

    /**
     * Causes grid to dry rapidly to a defined drought level of moisture, for a given number of turns
     * 
     * @param   double  newNeutralMoistureLvl   The drought level of moisture
     * @param   int     droughtTime             The number of turns that the the drought will last
     */
    public void dry(double newNeutralMoistureLvl, int droughtTime){
        neutralMoistureLevel = newNeutralMoistureLvl;
        deAndReHydrationRate = 10.0;
        droughtTimer = droughtTime;
    }

    /**
     * Causes grid to moisturise rapidly to a defined flood level of moisture, for a given number of turns
     * 
     * @param   double  newNeutralMoistureLvl   The flood level of moisture
     * @param   int     floodTime               The number of turns that the the flood will last
     */
    public void flood(double newNeutralMoistureLvl, int floodTime){
        neutralMoistureLevel = newNeutralMoistureLvl;
        deAndReHydrationRate = 10.0;
        floodTimer = floodTime;
    }

    /**
     * Causes grid's moisture level to approach its neutral level at the rate of neutralisation
     */
    private void neutraliseMoisture(){
        if (moistureLevel > neutralMoistureLevel) {
            moistureLevel -= deAndReHydrationRate;
        } else if (moistureLevel < neutralMoistureLevel) {
            moistureLevel += deAndReHydrationRate;
        }
    }

    /**
     * Causes grid's nutrition level to approach its neutral level at the rate of neutralisation
     */
    private void neutraliseNutrition(){
        if (nutrientLevel > neutralNutrientLevel) {
            nutrientLevel -= deAndReNutritionRate;
        } else if (nutrientLevel < neutralNutrientLevel) {
            nutrientLevel += deAndReNutritionRate;
        }
    }

    /**
     * Neutralises grid's moisture and fertility, depletes countdown timers for disasters (weather), and 
     * changes colour of grid to reflect status
     */
    public void act(){
        neutraliseMoisture();
        neutraliseNutrition();
        depleteDisasterTimers();

        image.setColor(changeColour());
        image.fill();
        setImage(image);
    }
}
