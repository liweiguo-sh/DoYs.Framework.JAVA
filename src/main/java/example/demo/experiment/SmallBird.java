package example.demo.experiment;
public class SmallBird extends Bird implements Fly {
    @Override
    public void showFlyHeight(int flyHeight) {
        System.out.println("Small bird can fly " + flyHeight + "M.");
    }
}