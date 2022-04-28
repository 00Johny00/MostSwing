package myown.company.javareflection;

import javax.swing.*;
import java.awt.*;

public class Car {
    private int positionX;
    private int positionY;
    private static final int MOVE_SINGLE_TIME = 5;
    public static Image carDrivingLeft = new ImageIcon("carInLeft.png").getImage();
    public static Image carDrivingRight = new ImageIcon("carInRight.png").getImage();

    public Car(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public void moveCar(boolean turnLeftWay) {

        if(turnLeftWay){
            positionX-=MOVE_SINGLE_TIME;
        } else positionX +=MOVE_SINGLE_TIME;

    }
}
