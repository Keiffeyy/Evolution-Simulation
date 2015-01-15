import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.Color;

/**
 * The black box that changes in transparency as the day goes on. Less transparent as the day goes on.
 * 
 * @author Keith Wong
 * @version October 19, 2013
 */
public class Background extends Actor
{
    //sets the image
    private GreenfootImage background = new GreenfootImage(960, 640);
    private int currentTransparency = 0;
    private EvolutionWorld world;
    /**
     * Creates an instacne of background.
     * 
     */
    public Background(EvolutionWorld world){
        this.world = world;
        background.setColor(Color.BLACK);
        background.fill();
        background.setTransparency(0); //set the transparency to 0 initially, since it starts in daytime
    }

    /**
     * runs every greenfoot frame, setting the image's transparency.
     */
    public void act() 
    {
        background.setTransparency((int)(world.getTimeOfDay() * 200.0));
        //test code
        //         if (Greenfoot.isKeyDown("left") == true)
        //             darken();
        //         if (Greenfoot.isKeyDown("right") == true)
        //             brighten();
        this.setImage(background);
    }

    /**
     * Makes this more transparent.
     */
    public void brighten(){
        currentTransparency--;
        //keep it withing upper bound
        if (currentTransparency > 255)
            currentTransparency = 255;
    }

    /**
     * Makes this less transparent.
     */
    public void darken(){
        currentTransparency++;
        //keep it within lower bound
        if (currentTransparency < 0)
            currentTransparency = 0;
    }
}
