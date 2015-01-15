import java.util.List;
import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;

/**
 * Evolution World
 * 
 * @author Keith Wong, James Ly, Andy Lui & Daniel Chung (But mostly Daniel)
 * @version 1.0
 */
public class EvolutionWorld extends World
{
    //General world settings - configurable
    private int trees = 4;
    private int bushes = 5;
    private int predators = 5;
    private int prey = 10;
    
    private int maximumActor = 20; //Maximum amount of starting actors
    private boolean start = false;

    //Time
    private int timeOfDay = 0;
    private int daysPassed = 0;
    private int lengthOfDay = 2400;
    private RadialMeter clock = new RadialMeter(null, lengthOfDay,
            timeOfDay, 50, 20, Color.BLACK, Color.CYAN);

    //Choosing starting number of each actor
    private ValueChoose predatorChoose = new ValueChoose(maximumActor,predators, 1, this);
    private ValueChoose preyChoose = new ValueChoose(maximumActor,prey,         2, this);
    private ValueChoose treeChoose = new ValueChoose(maximumActor,trees,        3, this);
    private ValueChoose bushChoose = new ValueChoose(maximumActor,bushes,       4, this);
            
    //Display of statistics
    private InfoBar infoBar = new InfoBar();
    private InfoBarPic infoBarPic = new InfoBarPic();

    //Grid system
    private Grid[][]grid;
    private Background background = new Background(this);

    //Disaster/Weather
    private int disasterFactor;
    private int disasterDuration;

    /**
     * Default constructor for objects of class EvolutionWorld.
     * 
     */
    public EvolutionWorld()
    {    
        
        // Create a new world with 960x640 cells with a cell size of 1x1 pixels.    
        super(960, 640, 1);
        grid = new Grid[12][8]; //Initialisation of grid system
        for (int x = 0, counter = 0; x < 12; x++){
            for (int y = 0; y < 8; y++, counter++){
                grid[x][y] = new Grid(counter, (int)(x * 80 + 40), (int)(y * 80 + 40));   //Each grid is 80 x 80
                addObject(grid[x][y], (int)(x * 80 + 40), (int)(y * 80 + 40));
            }
        }

        
        //Addition of initialised displays to world
        addObject(clock, getBackground().getWidth() / 2, getBackground().getHeight() - 150);
        addObject(infoBar, 480, 640);
        addObject(infoBarPic, 480, 600);
        addObject(background, getBackground().getWidth() / 2, getBackground().getHeight() / 2); //Shader for day/night cycle
        
        //Addition of initialised counters to choose amount of actors.
        addObject (predatorChoose, 100,300);
        addObject (preyChoose, 200,300);
        addObject (bushChoose, 300,300);
        addObject (treeChoose, 400,300);
        
        //The chance of disasters occuring, and the duration of a disaster
        disasterFactor = 1000;
        disasterDuration = 500;
    
        //The order of Actors from topmost layer to bottommost
        setPaintOrder(ValueChoose.class, Arrow.class, InfoBar.class, InfoBarPic.class, 
        RadialMeter.class, Background.class, GenericBar.class,  
        Tree.class, Bush.class, Predator.class,Colour.class, Prey.class, 
        Fruit.class, Grid.class);
    }

    //Accessors =======================================================
    /**
     * @return  int The length of a day in turns
     */
    public int getLengthOfDay(){
        return lengthOfDay;
    }
    
    /**
     * @return  int The number of prey in the world
     */
    public int getNumberOfPrey(){
        return prey;
    }

    /**
     * @return  double  The time of day as a decimal, where 0 = Start of day, while 1 = End of day
     */
    public double getTimeOfDay(){
        return (double)timeOfDay / (double) lengthOfDay;
    }

    /**
     * @return  double  The time of day in turns, as an integer
     */
    public int getHour(){
        return timeOfDay;
    }

    //Modifiers ========================================================

    //Grid Interaction ----------------------------------------------
    /**
     * Chooses a random disaster to run, and runs appropriate commands in all grids affected
     */
    public void initiateDisaster(){
        switch(Greenfoot.getRandomNumber(2)){
            case 0 :    //50% chance of a flood (rain)
            for (int x = 0, counter = 0; x < 12; x++){
                for (int y = 0; y < 8; y++, counter++){
                    grid[x][y].flood((double)(Greenfoot.getRandomNumber(5000) + 8000), 
                        100 + Greenfoot.getRandomNumber(disasterDuration - 100));
                }
            }
            break;
            case 1 :    //50% of a drought (very sunny)
            for (int x = 0, counter = 0; x < 12; x++){
                for (int y = 0; y < 8; y++, counter++){
                    grid[x][y].dry((double)(Greenfoot.getRandomNumber(100) + 300), 
                        100 + Greenfoot.getRandomNumber(disasterDuration - 100));
                }
            }
            break;
        }
    }

    private void spawnActors()
    {
        //Gets the user set inputs.
        trees = treeChoose.getQuantity();
        bushes = bushChoose.getQuantity();
        predators = predatorChoose.getQuantity();
        prey = preyChoose.getQuantity();
        
        //Removing value choosers from the world.
        removeObject(treeChoose);
        removeObject(bushChoose);
        removeObject(predatorChoose);
        removeObject(preyChoose);
        removeObjects(getObjects(Arrow.class));  
        
        //Initialisation of starting actors
        for (int i = 0; i < trees; i++)
            addObject(new Tree(this), Greenfoot.getRandomNumber(getBackground().getWidth() - 40) + 20, 
                Greenfoot.getRandomNumber(getBackground().getHeight() - 40) + 20);

        for (int i = 0; i < bushes; i++)
            addObject(new Bush(this), Greenfoot.getRandomNumber(getBackground().getWidth() - 40) + 20, 
                Greenfoot.getRandomNumber(getBackground().getHeight() - 40) + 20);

        for (int i = 0; i < predators; i++)
            addObject(new Predator(), Greenfoot.getRandomNumber(getBackground().getWidth() - 40) + 20, 
                Greenfoot.getRandomNumber(getBackground().getHeight() - 40) + 20);

        for (int i = 0; i < prey; i++)
            addObject(new Prey(this, Greenfoot.getRandomNumber(256), Greenfoot.getRandomNumber(256), Greenfoot.getRandomNumber(256), 100, 5000, 1, 500, 2, true), Greenfoot.getRandomNumber(getBackground().getWidth() - 40) + 20, 
                Greenfoot.getRandomNumber(getBackground().getHeight() - 40) + 20);

        for (int i = 0; i < prey; i++)
            addObject(new Prey(this, Greenfoot.getRandomNumber(256), Greenfoot.getRandomNumber(256), Greenfoot.getRandomNumber(256), 100, 5000, 1, 500, 2, false), Greenfoot.getRandomNumber(getBackground().getWidth() - 40) + 20, 
                Greenfoot.getRandomNumber(getBackground().getHeight() - 40) + 20);
    }
    
    //Counts the number of fauna and flora in the world
    private void countFaunaFlora(){
        trees = getObjects(Tree.class).size();
        bushes = getObjects(Bush.class).size();
        prey = getObjects(Prey.class).size();
        predators = getObjects(Predator.class).size();
    }

    /**
     * Moves the day along, resets statistics, and initiates disasters (weather phenomena) at random
     */
    public void act(){  
       if (Greenfoot.getKey() == "enter" && start == false){
           spawnActors();
           start = true;
       }
 
       if (start){
            //Moving the day along, and resetting it to 0 if it exceeds the length of a day
            timeOfDay = (timeOfDay >= lengthOfDay) ? 0 : timeOfDay + 1;
            clock.setCurrentValue(getHour());
            countFaunaFlora();
            infoBar.update(trees, bushes, prey, predators);
        
            //Increasing the day count every time the time is 0
            daysPassed = (timeOfDay == 0) ? daysPassed + 1 : daysPassed;
        
            if (Greenfoot.getRandomNumber(disasterFactor) == 0){    //Chance of a disaster is determined by the disaster factor
                initiateDisaster();
            }
        }
    }
}
