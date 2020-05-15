package com.example.demo.experiment;
public class BigBird extends Bird implements Fly {
    @Override
    public void showFlyHeight(int flyHeight) {
        System.out.println("Big   bird can fly " + flyHeight + "M.");
    }
}