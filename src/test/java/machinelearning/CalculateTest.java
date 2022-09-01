package machinelearning;


import controller.DataController;
import controller.LearningController;
import view.ListView;
import view.MainView;
import view.Observer;

public class CalculateTest {

    public static void main(String[] args) {
        DataController dataController = new DataController();
        LearningController controller = new LearningController(dataController,false);

        Observer mainView = new MainView(controller);
        controller.attach(mainView);
        Observer listview = new ListView(controller, dataController);
        controller.attach(listview);
        /*
        while(true){
            controller.calculate();
        }
        
         */


    }
}
