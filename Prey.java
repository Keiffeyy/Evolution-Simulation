import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.*;
import java.awt.Color;
/**
 * Walks, eats, and mates. Also dies. Is eaten by predators, and eats plants and fruits.
 * 
 * @author Keith Wong
 * @version October 21, 2013
 */
public class Prey extends Fauna
{
    //inherited from organism
    private int age;

    //colors
    private int r;
    private int g;
    private int b;
    private Color color;
    private Colour circle;

    //immunity to disease
    private int immunity;
    private int maxImmunity;

    private int health;
    private int maxHealth;

    //for child, rate at which values will deviate from the average of the parents
    private int mutationRate;

    private boolean diseased;

    //inherited from Fauna
    private boolean gestation = false;
    private int gestationPeriod;
    private int currentGestation;

    private int speed;

    private double hunger = 500;
    private double maxHunger = 500;
    private double hungerDecayRate = 0.5;
    private double thirst = 500;
    private double maxThirst = 500;
    private double thirstDecayRate = 0.5;

    //     private int sleepTime;
    //     private boolean isSleeping = false;

    //     private boolean poisoned;
    private boolean isMale; // male = true

    private Prey mateID;

    //exclusive to Prey
    private boolean isHiding = false; // true when hiding in a bush or tree

    private Predator targetedBy; // the predator that is chasing this
    private ArrayList<Flora> floraInRange; // all the plants that are within a certain radius
    private int numFloraInRange;
    private Flora targetFlora; // the flora that this is running towards 
    private int targetDistance; // the distance away from the target
    private int angle; // the angle between this and the target

    private ArrayList<Predator> predatorInRange; // the predators within a certain range
    private int numPredatorInRange;
    private boolean runAway = false; // true when being chased

    //dimensions of world
    private int worldX = 960;
    private int worldY = 640;

    private int moveStatus = 0; // 0 is moving, 1 is resting
    private int restCounter = 0; //when at 0 or less, this is moving
    //sets the initial destination of this
    private int desX = Greenfoot.getRandomNumber(worldX - 1) + 1;
    private int desY = Greenfoot.getRandomNumber(worldY - 1) + 1;

    private ArrayList<Prey> preyInRange; //finds all prey within a certain range for mating purposes

    //bars that follow this
    private GenericBar healthBar;
    private GenericBar gestationBar;

    //this world
    private EvolutionWorld w;
    /**
     * Creates an instance of Prey.
     * 
     * @param r The r color code
     * @param g The g color code
     * @param b The b color code
     * @param maxImmunity the upper bound of the animal's ability to fend off disease
     * @param maxHealth the upper bound of this' health
     * @param mutationRate the amount of deviation from the average that this' offspring will have
     * @param gestationPeriod how long it takes to complete a pregnancy
     * @param speed how fast this moves
     * @param isMale determines if this will be male or female
     */
    public Prey(EvolutionWorld w, int r, int g, int b, int maxImmunity, int maxHealth, int mutationRate, int gestationPeriod, int speed, boolean isMale){
        this.w = w;
        //Organism
        age = 0;
        //colors
        this.r = r;
        this.g = g;
        this.b = b;
        color = new Color(r,g,b);
        //immunity
        this.immunity = maxImmunity/2;
        this.maxImmunity = maxImmunity;
        //health
        this.health = maxHealth;
        this.maxHealth = maxHealth;
        //mutation rate for its children
        this.mutationRate = mutationRate;

        //Fauna
        this.gestationPeriod = gestationPeriod;
        this.speed = speed;
        this.isMale = isMale; // male when true, female when false
        if (isMale == true)
            this.setImage("Sheep_Male.png");
        else
            this.setImage("Sheep_Female.png");
    }

    /**
     * Is run once this is put into the world. Basically just creates the things that follow this around.
     * 
     * @param w The world that this is in.
     */
    protected void addedToWorld(World w){
        circle = new Colour(r, g, b, this);
        getWorld().addObject(circle, -100, -100);
        //Health Bar
        healthBar = new GenericBar(maxHealth, this, 8, 50, 0, 255, 0, 30);
        getWorld().addObject(healthBar, getX(), getY());

        //Bar for amount of pregnancy finished
        gestationBar = new GenericBar(gestationPeriod, this, 8, 50, 0, 0, 255, 45);
    }

    /**
     * Is run every Greenfoot frame. lowers hunger and thirst, causes aging, and does all the checks for predators, other prey, and food.
     * 
     */
    public void act(){
        hungerAndThirstDecay(); //subtracts hunger and thirst by a specific amount
        lackOfNutrients(); //subtracts health when hunger and thirst are below certain values
        age++; //increases age

        if (runAway == false){
            findNewDes(); //looks for a new destination if it is not running away or hiding
            if (mateID == null)
                findMate(); //finds a mate if it does not already have one
            if (mateID != null){    
                if (age > 1500 && isMale == false){
                    gestation = true;
                    getWorld().addObject(gestationBar, getX(), getY());
                }
            }
        }
        checkForPredators();

        if (gestation == true)
            currentGestation++;
        if (currentGestation >= gestationPeriod){
            currentGestation = 0; //reset the counter for future babies
            giveBirth();
        }
        if (hunger < maxHunger*0.8){
            moveToClosestEdibleFlora(); //will try to eat if they are below 80% hunger
        }

        //dies of old age
        if (age > 50000 || health <= 0)
            die();

        healthBar.update(health); //updates the health bar with current hp
        if (gestation == true)
            gestationBar.update(currentGestation); //updates the gestation bar with current gestation time

        //if theres an actor not in world error, then thatmeans that the target Flora is no longer in the world, and must be reset
        try{
            int checkIfStillInWorld = targetFlora.getX();
        } catch (Exception e){
            targetFlora = null;
        }
        //         if (w.getTimeOfDay() > 180000) //its "sleeping"
        //             moveStatus = 1;
    }

    /**
     * Lowers hunger and thirst levels by the decay rate.
     */
    private void hungerAndThirstDecay(){
        hunger -= hungerDecayRate;
        thirst -= thirstDecayRate;
    }

    /**
     * Lowers the health of this if hunger and thirst fall below certain levels.
     * 
     */
    private void lackOfNutrients(){
        //keeps hunger and thirst within the bounds of 0 and their respecitve maximums
        if (hunger < 0)
            hunger = 0;

        if (thirst < 0)
            thirst = 0;

        //lowers hp if this does not have enough nutrients
        if (hunger < 50)
            health--;
        else
            health++;

        if (thirst < 50)
            health--;
        else
            health++;
        if (health > maxHealth)
            health = maxHealth;
    }

    /**
     * removes itself and all other objects associated with it. Also adds nutrients back into the soil.
     * 
     */
    public void die(){
        Grid currentGrid = (Grid) getOneObjectAtOffset(0, 0, Grid.class);
        //add nutrients back into the soil
        currentGrid.changeMoisture(500);
        currentGrid.changeFertility(500);

        getWorld().removeObject(circle);
        getWorld().removeObject(gestationBar);
        getWorld().removeObject(healthBar);
        getWorld().removeObject(this);
    }

    /**
     * Finds a new destination for this. If it's not supposed to move, lowers the rest counter by 1.
     * 
     */
    public void findNewDes()
    {
        if (moveStatus == 0)
        {
            turnTowards(desX,desY);
            move(speed);
            if (checkDesReached() == true)
            {
                //when reached destination, 50% chance to find new destination, 50% chance to stay still for a while
                if (Greenfoot.getRandomNumber(2) == 0)
                {
                    newDes();
                }
                else
                {
                    moveStatus = 1; //stop moving
                    restCounter = Greenfoot.getRandomNumber(300);
                }
            }
        }
        if (moveStatus == 1)
        {
            //will start to move when restcounter reaches 0
            restCounter--;
            if (restCounter <= 0)
            {
                moveStatus = 0;
            }
        }
    }

    /**
     * Randomly generates a new destination.
     * 
     */
    private void newDes()
    {
        desX = Greenfoot.getRandomNumber(worldX);
        desY = Greenfoot.getRandomNumber(worldY);
    }

    /**
     * Checks to see if this has reached its destination, returns true if it has, false otherwise.
     * 
     * @return boolean true when destination is reached, false otherwise.
     * 
     */
    private boolean checkDesReached()
    {
        boolean reached = false;
        //distance from destination
        double distance = Math.sqrt((double)((Math.pow((double)(this.getX() - desX), 2) + Math.pow((double)(this.getY() - desY), 2))));
        if (distance < 10)
        {
            reached = true;
        }
        return reached;
    }

    /////////////////////////////////////////////////Hiding doesnt work because James didn't bother to check if I was hiding or not. As a consequence the bushes lose half their functionality.\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    //     /**
    //      * If there is something targeting this, then it proceeds to run towards the closest Tree or Bush. Stops looking to hide if there are no predators in the area.
    //      * 
    //      */
    //     protected void hide(){
    //         if (runAway == true){ //save on processing power if there is no predators around
    //             targetDistance = 81;
    //             if (isHiding == false){
    //                 getClosestFlora();
    //                 if (targetFlora != null && targetFlora.getClass() != Fruit.class){ //you cant hide inside a fruit
    //                     //move towards target
    //                     setRotation(angle);
    //                     move(speed);
    //                     isHiding = true; //so that this does not try to find a new destination
    //                 }
    //             }
    //         }
    // 
    //         //returns this back to normal if there is no predator in range
    //         if (getObjectsInRange(100, Predator.class).size() == 0){
    //             targetedBy = null;
    //             isHiding = false;
    //         }
    //     }

    /**
     * Sets the motion of this towards the target Flora.
     * 
     */
    protected void moveToClosestEdibleFlora(){
        getClosestFlora(); //finds the closest flora
        if (targetFlora != null && targetFlora.getClass() != Tree.class){
            //move towards target
            setRotation(angle);
            move(speed);
            //Eats the target when it touches it
            if (isTouching(targetFlora.getClass()) == true){
                if (targetFlora.getClass() == Bush.class)
                    eatBush();
                if (targetFlora.getClass() == Fruit.class)
                    eatFruit();
            }
        }
    }

    /**
     * Eats a bush, and replenishes nutrients.
     * 
     */
    private void eatBush(){
        Bush targetBush = (Bush) targetFlora; //cast into bush in order to use its methods
        //finds out how much hunger and thirst to replenish and then does it
        double[] nutrientsReceived = targetBush.digest();
        replenishNutrients(nutrientsReceived[0], nutrientsReceived[1]);
    }

    /**
     * Eats a fruit, and replenishes nutrients.
     * 
     */
    private void eatFruit(){
        Fruit targetFruit = (Fruit) targetFlora; //cast into Fruit in order to use its methods
        //finds out how much hunger and thirst to replenish and then does it
        double[] nutrientsReceived = targetFruit.digest(this);
        replenishNutrients(nutrientsReceived[0], nutrientsReceived[1]); 
    }

    /**
     * Replenishes nutrients.
     * 
     * @param hunger how much hunger is being restored.
     * @param thirst how much thirst is being restored.
     */
    protected void replenishNutrients(double hunger, double thirst){
        this.hunger += hunger;
        this.thirst += thirst;
        //keeps hunger and thirst within upper bounds
        if (hunger > maxHunger)
            hunger = maxHunger;
        if (thirst > maxThirst)
            thirst = maxThirst;
    }

    /**
     * Looks within a certain radius (40) and then determines the closest one.
     */
    protected void getClosestFlora(){
        floraInRange = (ArrayList) getNeighbours(50, true, Flora.class); //gets all Flora within a 50 unit radius
        if (floraInRange.size() > 0){ //checks to see if there are any, in roder to possibly save processing power
            targetFlora = floraInRange.get(0); //ensures no null pointer exceptions
            targetDistance = distanceBetweenObjects(this, targetFlora);
            for (int i = 0 ; i < numFloraInRange ; i++){ //loops through for each flora
                int newTargetDistance = distanceBetweenObjects(this, floraInRange.get(i)); //finds the distance between this and the target
                if (newTargetDistance < targetDistance)
                    targetFlora = floraInRange.get(i); // sets the current target to be the true target if it is closer than the previous one
            }
            angle = angleBetweenObjects(targetFlora, this); //sets the angle to be the angle between this and the target
        }
    }

    /**
     * Looks within a certain radius (100) and then finds the best angle to run away at.
     */
    protected void checkForPredators(){
        predatorInRange = (ArrayList) getNeighbours(100, true, Predator.class); //creates a list of all predators within a 100 unit radius
        numPredatorInRange = predatorInRange.size();
        if (numPredatorInRange > 0){ //will not run unless there is at least 1 predator in range
            runAway = true; //forces this to run in the optimal direction
            //             hide(); // checks if this should be running away
            int[] angleBetweenPredatorAndThis = new int[numPredatorInRange];//creates an array of integers to store the angle between each predator and this
            for (int i = 0 ; i < numPredatorInRange ; i++){ // runs through as many times as there are predators
                angleBetweenPredatorAndThis[i] = angleBetweenObjects(this, predatorInRange.get(i)); //gets the angle between this and the predator and stores it in the array
            }
            int sumOfAnglesBetweenPredatorAndThis = 0;
            for (int j = 0 ; j < angleBetweenPredatorAndThis.length ; j++){ // finds the average angle from this and the predators in order to find out what angle to run away at
                if (angleBetweenPredatorAndThis[j] < 0)
                    angleBetweenPredatorAndThis[j] += 360; //keeps the angle above 0
                sumOfAnglesBetweenPredatorAndThis += angleBetweenPredatorAndThis[j];
            }
            setRotation(sumOfAnglesBetweenPredatorAndThis/numPredatorInRange);
            moveStatus = 0;//forces this to move when it finds a predator
            move(speed);
        } else
            runAway = false; // if no predators, it does not need to run away
    }
    //test code 
    //     public int getDesX(){
    //         return desX;
    //     }
    // 
    //     public int getDesY(){
    //         return desY;
    //     }
    /**
     * Finds the closest Prey of the opposite gender within a certain radius (200), and mates with them.
     */
    private void findMate(){
        int closestMate = 201;
        int distanceOfMateFromThis = 201;
        preyInRange = (ArrayList) getNeighbours(100, true, Prey.class); //gets a list of all prey within 100 units
        int numPreyInRange = preyInRange.size();
        if (numPreyInRange > 0){ //only runs if there is at least 1 in range
            for (int i = 0 ; i < numPreyInRange ; i++){ //loops as many times as there are prey in range
                if (preyInRange.get(i).getIsMale() != this.isMale) //checks to be sure that they are opposite genders
                    distanceOfMateFromThis = distanceBetweenObjects(this, preyInRange.get(i)); //find distance between this and possible target
                if (distanceOfMateFromThis < closestMate){ //if current possible target is closer than previous target, set it as current target
                    closestMate = distanceOfMateFromThis;
                    mateID = preyInRange.get(i);
                }
            }
        }
    }

    /**
     * Gets the values of its partner and itself, averages them out, and then makes a new baby.
     * 
     */
    private void giveBirth(){
        int rPartner = mateID.getR(); //partner's R value
        int gPartner = mateID.getG(); //partner's G value
        int bPartner = mateID.getB(); //partner's b value
        int maxImmunityPartner = mateID.getMaxImmunity(); //partner's max immunity
        int maxHealthPartner = mateID.getMaxHealth(); //partner's max health
        int mutationRatePartner = mateID.getMutationRate(); //partner's mutation rate
        int gestationPeriodPartner = mateID.getGestationPeriod(); //partner's gestation period
        int speedPartner = mateID.getSpeed(); //partner's speed

        //seting values for the baby (average between the two paretns +/- mutationRate)
        int rBaby = getBabyValue(r, rPartner, mutationRate, mutationRatePartner);
        //keep values within 0 an 255
        if (rBaby > 255)
            rBaby = 255;
        else if (rBaby < 0)
            rBaby = 0;
        int gBaby = getBabyValue(g, gPartner, mutationRate, mutationRatePartner);
        //keep values within 0 an 255
        if (gBaby > 255)
            gBaby = 255;
        else if (gBaby < 0)
            gBaby = 0;
        int bBaby = getBabyValue(b, bPartner, mutationRate, mutationRatePartner);
        //keep values within 0 an 255
        if (bBaby > 255)
            bBaby = 255;
        if (bBaby < 0)
            bBaby = 0;

        int maxImmunityBaby = getBabyValue(maxImmunity, maxImmunityPartner, mutationRate, mutationRatePartner);
        int maxHealthBaby = getBabyValue(maxHealth, maxHealthPartner, mutationRate, mutationRatePartner);
        int mutationRateBaby = getBabyValue(mutationRate, mutationRatePartner, mutationRate, mutationRatePartner);
        int gestationPeriodBaby = getBabyValue(gestationPeriod, gestationPeriodPartner, mutationRate, mutationRatePartner);
        int speedBaby = getBabyValue(speed, speedPartner, mutationRate, mutationRatePartner);
        boolean maleBaby;
        //50% chance for boy or girl
        if (Greenfoot.getRandomNumber(2) == 0)
            maleBaby = true;
        else 
            maleBaby = false;
        //A baby is born
        getWorld().addObject(new Prey(w, rBaby, gBaby, bBaby, maxImmunityBaby, maxHealthBaby, mutationRateBaby, gestationPeriodBaby, speedBaby, maleBaby), getX(), getY());
    }

    /**
     * Sets targetBy to be the predator that is chasing this.
     * 
     * @param p The predator that is chasing this.
     */
    public void setTarget(Predator p){
        targetedBy = p;
    }

    /**
     * Returns whether or not this is hiding.
     * 
     * @return boolean True when hiding, false when not
     * 
     */
    public boolean getIsHiding(){
        return isHiding;
    }

    /**
     * Returns the predator that is chasing this.
     * 
     * @return Predator The predator that is chasing this.
     * 
     */
    public Predator getTargetedBy(){
        return targetedBy;
    }

    /**
     * Returns the r value of the rgb code.
     * 
     * @return int R value of RGB code.
     */
    public int getR(){
        return r;
    }

    /**
     * Returns the g value of the rgb code.
     * 
     * @return int G value of RGB code.
     */
    public int getG(){
        return g;
    }

    /**
     * Returns the b value of the rgb code.
     * 
     * @return int B value of RGB code.
     */
    public int getB(){
        return b;
    }

    /**
     * Returns the immunity of this to disease.
     * 
     * @return int Immunity to disease.
     */
    public int getImmunity(){
        return immunity;
    }

    /**
     * Returns the max immunity this can have.
     * 
     * @return int Max Immunity.
     */
    public int getMaxImmunity(){
        return maxImmunity;
    }

    /**
     * Returns whether or not this is diseased, true if is, false if it is not.
     */
    public boolean getDiseased(){
        return diseased;
    }

    /**
     * Returns current health.
     * 
     * @return int current health.
     */
    public int getHealth(){
        return health;
    }

    /**
     * Subtracts the health by a desired amount.
     * 
     * @param subtractedBy the amount that you want to subtract.
     */
    public void subtractHealth(int subtractedBy){
        health -= subtractedBy;
    }

    /**
     * Returns max health.
     * 
     * @return int max health.
     */
    public int getMaxHealth(){
        return maxHealth;
    }

    /**
     * Returns mutation rate.
     * 
     * @return int mutation rate.
     */
    public int getMutationRate(){
        return mutationRate;
    }

    /**
     * Returns if this is pregnant. True is pregnant, false if not.
     * 
     * @return boolean 
     */
    public boolean getGestation(){
        return gestation;
    }

    /**
     * Returns gestation period.
     * 
     * @return int gestation period.
     */
    public int getGestationPeriod(){
        return gestationPeriod;
    }

    /**
     * Returns time into pregnancy.
     * 
     * @return int time into pregnancy.
     */
    public int getCurrentGestation(){
        return currentGestation;
    }

    /**
     * Returns speed.
     * 
     * @return int speed.
     */
    public int getSpeed(){
        return speed;
    }

    /**
     * Returns hunger.
     * 
     * @return int hunger.
     */
    public double getHunger(){
        return hunger;
    }

    /**
     * Returns thirst.
     * 
     * @return int thirst.
     */
    public double getThirst(){
        return thirst;
    }

    //     /**
    //      * Returns how long this needs to sleep.
    //      * 
    //      * @ return int length of sleep.
    //      */
    //     public int getSleepTime(){
    //         return sleepTime;
    //     }
    // 
    //     /**
    //      * Returns if this is sleeping.
    //      * 
    //      * @return boolean true if sleeping, false if not.
    //      */
    //     public boolean getIsSleeping(){
    //         return isSleeping;
    //     }
    // 
    //     /**
    //      * Returns if this is poisoned.
    //      * 
    //      * @return boolean true if poisoned, false if not.
    //      * 
    //      */
    //     public boolean getPoisoned(){
    //         return poisoned;
    //     }

    /**
     *Returns gender.
     *
     *@return boolean true if male, false if female.
     */
    public boolean getIsMale(){
        return isMale;
    }
    //Test code
    //     public Prey getMateID(){
    //         return mateID;
    //     }
    // 
    //     public Flora getTargetFlora(){
    //         return targetFlora;
    //     }

    /**
     * Subtracts the speed by a certain amount when this is diseased.
     * 
     * @param subtract the amount to subtract.
     */
    public void subtractSpeed(int subtract){
        speed -= subtract;
        if (speed == 0)
            speed = 0;
    }

    /**
     * Saps hunger by a certain amount when diseased.
     * 
     * @param subtract the amount to subtract.
     */
    public void subtractHunger(int subtract){
        hunger -= subtract;
        if (hunger == 0)
            hunger = 0;
    }

    /**
     * Saps thirst by a certain amount when diseased.
     * 
     * @param subtract the amount to subtract.
     */
    public void subtractThirst(int subtract){
        thirst -= subtract;
        if (thirst == 0)
            thirst = 0;
    }
}