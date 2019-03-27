package com.test.cc;

import net.java.games.input.*;


public class Joystick implements Runnable {

    private Component component;
    private boolean affich =false;
    private Component[] components;
    private Controller[] controllers;
    private Controller controller;
    private boolean isRunning = false;
    private Thread thread;
    private EventQueue eventQueue;
    private Event event;
    private long timer;
    private int data;


    public Joystick(){

        init();
        start();

    }

    private synchronized void start(){
        if(isRunning)return;
        thread = new Thread(this);
        thread.start();
        isRunning = true;
    }

    private synchronized void stop(){
        if (!isRunning)return;

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning){
            if (controller !=null){
                controller.poll();
                eventQueue = controller.getEventQueue();
                event = new Event();
                eventQueue.getNextEvent(event);
                component = event.getComponent();
                if(component != null) {
                    formatPoolData();
                    if (affich) affiche();
                }

            }
            try {
                thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void init(){
        affich=false;
        timer = System.currentTimeMillis();
        controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for (int i = 0 ; i < controllers.length ; i++){
            System.out.println(controllers[i].getType() + " - " + controllers[i].getName());
            if (controllers[i].getType() == Controller.Type.GAMEPAD) {
                controller = controllers[i];
            }
        }

      components = controller.getComponents();

        for (int i = 0 ; i < components.length ; i++){
            System.out.println(components[i].getName() + " - " + components[i].getIdentifier());
        }


    }

    private void affiche(){
        if (System.currentTimeMillis()- timer > 20){
            timer += 20;
            System.out.println(component.getName() + " - " + data);
            System.out.println((int)(controller.getComponent(Component.Identifier.Axis.RX).getPollData()*100));
        }
    }

    private void formatPoolData(){
        if (component == null) {
            affich=false;
            return;
        }
        data = 0;
        data = ((int)(component.getPollData()*100));
        if (data > -3 && data <3){
            data = 0;
            affich = false;
        }else {
            affich = true;
        }
    }

}
