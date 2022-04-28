package myown.company.javareflection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AnimationPanel extends JPanel {
    //Values for Way of Riding car needed For Car class
    public static final boolean RIDE_RIGHT_WAY = false;
    public static final boolean RIDE_LEFT_WAY = true;
    public static final int DELAY_FOR_ANIMATION_MOVE_CAR = 30; // in seconds
    public static final int CAR_SIZE = 50;  // in px
    private static final int DELAY_FOR_SEMAPHORE = 5; // in seconds
    //List for our Cars
    List<Car> carListRidingRight = new ArrayList();
    List<Car> carListRidingLeft = new ArrayList();
    //Our locking help parameter
    private volatile boolean redLightRightWay = false;
    //Parameter to set semaphore signalisation
    private boolean semaphoreSignalGreenLeft = false;
    private boolean semaphoreSignalGreenRight = false;
    //Object needed to synchronize
    private Object lockForRightWay = new Object();
    private Object lockForLeftWay = new Object();
    //Queue of Cars to set them correctly
    private int carsInRightWayQueue = 0;
    private int carsInLeftWayQueue = 0;
    //Our images
    public static Image backGroundBridge = new ImageIcon("bridge.jpg").getImage();
    public static Image semaphoreImageGreen = new ImageIcon("semaphoreGreen.jpg").getImage();
    public static Image semaphoreImageRed = new ImageIcon("semaphoreRed.jpg").getImage();
    private ThreadGroup groupCarsRidingRight = new ThreadGroup("groupOfLeftCars");
    private ThreadGroup groupCarsRidingLeft = new ThreadGroup("groupOfLeftCars");
    private Thread carThread, carThreadLeft, GenerateCarThread,SemaphoreThread;

    //function that allows to paint on GUI
    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        //We are drawing our elements
        g.drawImage(backGroundBridge, 0, 0, null);
        for (int i = 0; i< carListRidingRight.size(); i++){
            g.drawImage(Car.carDrivingRight, carListRidingRight.get(i).getPositionX(), carListRidingRight.get(i).getPositionY(),null);
            g.drawImage(Car.carDrivingLeft, carListRidingLeft.get(i).getPositionX(), carListRidingLeft.get(i).getPositionY(),null);
        }
        if(semaphoreSignalGreenRight){
            g.drawImage(semaphoreImageGreen, 380,170,null );
        } else g.drawImage(semaphoreImageRed, 380,170,null );
        if(semaphoreSignalGreenLeft){
            g.drawImage(semaphoreImageGreen, 890,170,null );
        } else g.drawImage(semaphoreImageRed, 890,170,null );
    }
//Generating cars according to side
    public void addCarToLeft(int timeDelay) {
        carListRidingRight.add(new Car(0, 200));
        carThread = new Thread(groupCarsRidingRight, new CarRunnableRightWay((Car) carListRidingRight.get(carListRidingRight.size() - 1)));
        carThread.start();
        groupCarsRidingRight.list();
    }
    public void addCarToRight(int timeDelay) {
        carListRidingLeft.add(new Car(1300, 200));
        carThreadLeft = new Thread(groupCarsRidingLeft, new CarRunnableLeftWay((Car) carListRidingLeft.get(carListRidingLeft.size() - 1)));
        carThreadLeft.start();
        groupCarsRidingLeft.list();
    }
//Starting our threads semaphore and generated cars
    public void startAnimation(int timeDelayCarAppear, int timeDelaySemaphore) {
        SemaphoreThread = new Thread(groupCarsRidingRight,()-> semaphoreStart(timeDelaySemaphore));
        GenerateCarThread = new Thread(groupCarsRidingRight,()-> carAppear(timeDelayCarAppear));
        SemaphoreThread.start();
        GenerateCarThread.start();
    }

    private void carAppear(int timeDelay) {
        while(true) {
            addCarToRight(timeDelay);
            addCarToLeft(timeDelay);
            delayFunction(timeDelay);
        }
    }

    public void semaphoreStart(int delayTimeSemaphore){
        //always checking parameters
        while(true){
            redLightRightWay = true;
            //Created synchronized point with lockForLeftWay object and notify if cars can ride
            synchronized (lockForLeftWay) {
                delayFunction(delayTimeSemaphore); // Here we type time to let cars ride through bridge
                semaphoreSignalGreenLeft = true;
                lockForLeftWay.notifyAll();
            }
            delayFunction(DELAY_FOR_SEMAPHORE); //time needed car to exit bridge
            semaphoreSignalGreenLeft = false;
            redLightRightWay = false;
            synchronized (lockForRightWay) {
                delayFunction(delayTimeSemaphore);
                semaphoreSignalGreenRight = true;
                lockForRightWay.notifyAll();
            }
            delayFunction(DELAY_FOR_SEMAPHORE);
            semaphoreSignalGreenRight = false;
        }
    }

    private void delayFunction(int timeDelay) {
        try {
            TimeUnit.SECONDS.sleep(timeDelay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class CarRunnableRightWay implements Runnable {
        private Car car;

        public CarRunnableRightWay(Car car) {
            this.car = car;
        }

        @Override
        public void run() {

            while (true) {//Checking if car is on waiting position before entry to bridge and if yes check if there is green light
                if ((car.getPositionX() == 370 - (carsInRightWayQueue * CAR_SIZE))) {
                    synchronized (lockForRightWay) {
                        if (redLightRightWay == true) {
                            if (car.getPositionX() <= 390) {
                                try {
                                    carsInRightWayQueue++;
                                    lockForRightWay.wait();
                                } catch (InterruptedException e) {
                                    System.out.println("Car arrived to bridge");
                                }
                            }
                        }
                    }
                }//Checking if car is on waiting position before entry to bridge and if yes check if there is green light
                if(car.getPositionX() == 895){
                    if(carsInRightWayQueue > 0)
                        carsInRightWayQueue--;
                }
                car.moveCar(RIDE_RIGHT_WAY);
                repaint();
                delayMoveCarAnimation();
            }
        }
    }//Class needed to make threads
    class CarRunnableLeftWay implements Runnable {
        private Car car;

        public CarRunnableLeftWay(Car car) {
            this.car = car;
        }

        @Override
        public void run() {
            while (true) {
                if(car.getPositionX() == 900 + (carsInLeftWayQueue *  CAR_SIZE)) {
                    synchronized (lockForLeftWay) {
                        if (redLightRightWay == false) {
                             if (car.getPositionX() >= 880) {
                                try {
                                    carsInLeftWayQueue++;
                                    lockForLeftWay.wait();
                                } catch (InterruptedException e) {
                                    System.out.println("Car arrived to bridge");
                                }
                            }
                        }
                    }
                }
                if(car.getPositionX() == 895){
                    if(carsInLeftWayQueue > 0)
                        carsInLeftWayQueue--;
                }
                car.moveCar(RIDE_LEFT_WAY);
                repaint();
                delayMoveCarAnimation();
            }
        }
    }
//Class needed to simplify code review, time in ms
    private void delayMoveCarAnimation() {
        try {
            Thread.sleep(DELAY_FOR_ANIMATION_MOVE_CAR);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
