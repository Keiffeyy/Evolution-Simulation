/**import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Scanner;

/**
 * Write a description of class Title here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 *
public class Title extends World
{
private static int trees, bushes, prey, predators;
private static Scanner intScanner = new Scanner(System.in);
private static EvolutionWorld mainWorld;
private static boolean inactive;

/**
 * Constructor for objects of class Title.
 * 
 *
public Title()
{    
// Create a new world with 960x640 cells with a cell size of 1x1 pixels.
super(960, 640, 1);
setBackground(new GreenfootImage("TitleScreen.png"));
}

public void act(){
if (inactive){
System.out.print("Welcome to the Evolution Simulation!\nPlease enter the number of trees in your world: ");
trees = intScanner.nextInt();
System.out.print("Please enter the number of bushes: ");
bushes = intScanner.nextInt();
System.out.print("Please enter the number of predators (recommended more than 2): ");
predators = intScanner.nextInt();
System.out.print("Please enter the number of 10's of prey: ");
prey = intScanner.nextInt() * 10;

mainWorld = new EvolutionWorld(trees, bushes, predators, prey);

inactive = true;
Greenfoot.setWorld(mainWorld);
}
}
}*/

