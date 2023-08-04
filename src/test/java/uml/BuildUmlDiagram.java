package uml;

import de.elnarion.util.plantuml.generator.PlantUMLClassDiagramGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BuildUmlDiagram {

    public static void main(String[] args) {
        BuildUmlDiagram d = new BuildUmlDiagram();
        List<String> scanpackages = new ArrayList<>();
        scanpackages.add("controller");
        List<String> hideClasses = new ArrayList<>();
        PlantUMLClassDiagramGenerator generator =
                new PlantUMLClassDiagramGenerator(d.getClass().getClassLoader(),
                        scanpackages,null, hideClasses, false, false);
        try {
            String result = generator.generateDiagramText();
            System.out.println(result);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
