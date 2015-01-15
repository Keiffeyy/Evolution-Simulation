import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
/**
 * Write a description of class Disease here.
 * 
 * @author Andy Lui
 * @version October 21, 2013
 */
public class Disease extends Organism
{
    private int complexionChange;

    private int mutateFactor;
    private int strength;
    private int sapSpeed;
    private int sapHunger;
    private int sapThirst;
    private int spreadSpeed;
    private int spreadDistance; //Radius around the host.
    
    private boolean isActive; //Whether or not the disease is active and cause harm to the host.
    private boolean poisonous;
    private boolean faunaExclusive;
    private Fauna target; //The host of the disease.
    private List possibleTargets; //Actors that the disease can spread to.

    private int timesSpread = 0;
    private final int MAX_SPREAD = 3; //How many times the disease can spread FROM THE CURRENT HOST.
    private final int ACT_FREQUENCY = 1000; // 1/ACT_FREQUENCY chance per act to take ressources from host.
 
    Disease [] d;
    public Disease (Fauna a, boolean isPoisonous, int strength, int spreadDistance)
    {
        target = a;
        poisonous = isPoisonous;
        this.strength = strength;
        this.spreadDistance = spreadDistance;
    }
    
      public void act() 
    {
        if (isActive && Greenfoot.getRandomNumber(1000) == 0)
        {
            sapTarget();
        }
    }    
    
    /**
     * Checks the surrounding of the host for other fauna that are close by.
     */
    private List areaCheck()
    {
         List possibleTargets = getWorld().getObjects(Fauna.class);
         for (int i = 0; i < possibleTargets.size(); i++)
         {
             Organism a = (Organism) possibleTargets.get(i);
             //Whether or not it can spread to the other actors from fauna.class
             if  (target.distanceBetweenObjects(target, a) > spreadDistance)
                possibleTargets.remove(i);
         }
         return possibleTargets;
    }
    
    private void spread()
    {
        possibleTargets = areaCheck();
        //Checks through each target
        for (int i = 0; i < possibleTargets.size(); i++)
        {
            Organism o= (Organism) possibleTargets.get(i);
            if (o.getDiseased()) //If already sick don't try to get it sick again.
                possibleTargets.remove(i);
            
            int immunity = o.getImmunity();
            if (this.strength > immunity && timesSpread < MAX_SPREAD) //If disease is stronger and can still spread
            {
               timesSpread++;
               mutateFactor = (int) Greenfoot.getRandomNumber(strength)/3 + strength/2 ;
                
                //SHOULD BE REFERENCED FROM THE CLASS
               //getWorld().addDisease(possibleTargets.get(i), poisonous);
               d[i] = new Disease((Fauna)possibleTargets.get(i), poisonous, mutateFactor, spreadDistance);
            }
        }
    }
    
    private void sapTarget()
    {
//         target.subtractSpeed(sapSpeed);
//         target.subtractHunger(sapHunger);
//         target.subtractThirst(sapThirst);
    }
    
    private void isActive()
    {
        //spreadSpeed number of days depending on strength.
        isActive = true;
    }
    
    public Actor getTarget()
    {
        return target;
    }
    
    public void cure()
    {
        isActive = false;
    }
}
