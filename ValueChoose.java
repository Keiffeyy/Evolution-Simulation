import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.awt.*;
/**
 * Write a description of class ValueChoose here.
 * 
 * @author Andy Lui
 * @version October 21. 2013
 */
public class ValueChoose extends Actor
{

    private final int WIDTH = 80;
    private final int HEIGHT = 50;
    private int maxItem = 9;
    
    private Arrow topArrow;
    private Arrow bottomArrow ;
    private EvolutionWorld w;
    
    private GreenfootImage box; 
    private GreenfootImage blank = new GreenfootImage(WIDTH + 1, HEIGHT + 1);
    private int quantity = 0;
    
    private Font f = new Font (null, 0, 50);
    private Color black = new Color (0,0,0);
    /**
     * Creates a counter with two clickable arrows on the top and bottom.
     * 
     * @param int max The highest number that the counter can go up to.
     * @param int currentAmount The 'default' numbers that the counter starts at.
     * @param int imageSet The number corresponding to the images as defined by pictureDecode().
     * @param EvolutionWorld w The world that the counter will reside in.
     * 
     */
    public ValueChoose(int max,int currentAmount,int imageSet, EvolutionWorld w)
    {
        box = new GreenfootImage(WIDTH + 1, HEIGHT + 1);
        this.w = w;
        maxItem = max;
        quantity = currentAmount;
        //Setting custom images for each arrow.
        topArrow = new Arrow(pictureDecode(imageSet));
        bottomArrow = new Arrow(pictureDecode(imageSet));
        
        box.setFont(f);
        box.setColor(black);
        box.drawRect(0, 0, WIDTH, HEIGHT);
        if (quantity < 10)
            box.drawString("0"+Integer.toString(quantity), WIDTH/10, HEIGHT);
        else 
            box.drawString(Integer.toString(quantity), WIDTH/10, HEIGHT);
        this.setImage(box);
        
    }
    
    public void addedToWorld(World w)
    {
        w.addObject(topArrow, this.getX(), this.getY() - HEIGHT);
        w.addObject(bottomArrow, this.getX(), this.getY() + HEIGHT);
    }
    /**
     * Act - do whatever the ValueChoose wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public void act() 
    {
        update();
    }    
    
    private void update()
    {
        box = new GreenfootImage(blank);
        //Sets appropriate colour and font.
        box.setFont(f);
        box.setColor(black);
        
        //Draws the box and string.
        box.drawRect(0, 0, WIDTH, HEIGHT);
        if (quantity < 10)
            box.drawString("0"+Integer.toString(quantity), WIDTH/10, HEIGHT);
        else 
            box.drawString(Integer.toString(quantity), WIDTH/10, HEIGHT);
        this.setImage(box);
        
        if (Greenfoot.mouseClicked(topArrow))
            quantity++;
        if (Greenfoot.mouseClicked(bottomArrow))
            quantity--;
            
        //To make sure of appropriate quantity.    
        checkQuantity();
        
        //When enter is pressed, remove from world.
        if (Greenfoot.getKey() == "enter"){
            w.removeObject(topArrow);
            w.removeObject(bottomArrow);
        }
            
    }   
    
    private String pictureDecode(int image){        
        if (image == 1)
            return "Lion.png";
        if (image == 2)
            return "Sheep_Male.png";
        if (image == 3)
            return "orange.png";
        if (image == 4)
            return "grass.png";
        return "Lion.png";
    }
    
    private void checkQuantity()
    {
        if (quantity < 0)
            quantity = 0;
        if (quantity > maxItem)
            quantity = maxItem;
    }
    
    /**
     * Returns the value of the counter.
     *
     *@returns int The current value as shown on the counter.
     */
    public int getQuantity()
    {
        return quantity;
    }
}
