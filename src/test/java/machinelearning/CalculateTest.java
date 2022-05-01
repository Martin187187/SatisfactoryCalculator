package machinelearning;


import controller.LearningController;
import view.MainView;
import view.Observer;

public class CalculateTest {

    public static void main(String[] args) {
        LearningController controller = new LearningController(false);

        Observer mainView = new MainView(controller);
        controller.attach(mainView);
        while(true){
            controller.calculate();
        }
    }
}
