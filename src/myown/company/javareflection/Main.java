package myown.company.javareflection;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main extends JFrame {
    //Our gui and Animation panel
    private JPanel panelButtons = new JPanel();
    private AnimationPanel animationPanel = new AnimationPanel();
    private int defaultSemaphoreTime;
    private int defaultCarAppearTime;

    public Main(){
        //Creating our GUI with title dimensions and elements
        this.setTitle("Bridge 1-Way");
        this.setBounds(100,100,1308,400);
        animationPanel.setBackground(Color.ORANGE);
        JButton bStart = (JButton)panelButtons.add(new JButton("Start"));
        JLabel jLabelTimeBetweenNewCars = (JLabel) panelButtons.add(new JLabel("Time for new car appears [s]"));
        JTextField jTextAreaTimeNewCars = (JTextField)panelButtons.add(new JTextField(5));
        JLabel jLabelTimeForOneWayBridge = (JLabel) panelButtons.add(new JLabel("Time for one cycle car"));
        JTextField jTextAreaSemaphoreTime = (JTextField)panelButtons.add(new JTextField(5));
        //Setting default semaphore time and car appear
        setDefaultTimeSemaphoreAndCarAppear();
        //Respond for our button start click with animation and check if there is any value typed for parameters
        bStart.addActionListener(e -> {
            if(!jTextAreaSemaphoreTime.getText().isEmpty()) {
                startAnimation(animationPanel,
                        Integer.parseInt(jTextAreaTimeNewCars.getText().toString()),
                        Integer.parseInt(jTextAreaSemaphoreTime.getText().toString()));
                } else {
                startAnimation(animationPanel,defaultCarAppearTime,defaultSemaphoreTime);
                }
            });
        //adding element on content pane with layouts
        this.getContentPane().add(animationPanel);
        this.getContentPane().add(panelButtons, BorderLayout.SOUTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    //Read defauls values from  Default Parameter.txt
    private void setDefaultTimeSemaphoreAndCarAppear() {
        BufferedReader reader = null;
        String descriptionLine;
        try {
            reader = new BufferedReader(new FileReader("Default Parameter.txt"));
            descriptionLine = reader.readLine();
            char chars[] = descriptionLine.toCharArray();
            defaultCarAppearTime = Integer.parseInt(String.valueOf(chars[0]));
            defaultSemaphoreTime = Integer.parseInt(String.valueOf(chars[2]));
            System.out.println("---------------------");
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startAnimation(JPanel panel, int timeDelayCarAppear, int timeDelaySemaphore) {
        animationPanel.startAnimation(timeDelayCarAppear, timeDelaySemaphore);
    }

    public static void main(String[] args) {
      new Main().setVisible(true);
    }
}
