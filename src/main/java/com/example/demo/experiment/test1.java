package com.example.demo.experiment;
public class test1 {
    public static void main(String[] args) {
        SmallBird bird1 = new SmallBird();
        BigBird bird2 = new BigBird();

        showHeight(bird1, 100);
        showHeight(bird2, 200);
    }

    private static void showHeight(Fly bird, int height) {
        bird.showFlyHeight(height);
        System.out.println("abc");
    }
}