package view;

import controller.DataController;
import controller.LearningController;
import controller.Network;
import controller.Subject;
import model.Item;
import model.NetworkNode;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListView extends JFrame implements Observer {

    private LearningController controller;
    private DataController dataController;
    private JScrollPane scrollPane;
    public ListView(LearningController controller, DataController dataController){
        this.controller = controller;
        this.dataController = dataController;

        setTitle("Satisfactory Calculator List");
        setSize(720,480);
        scrollPane = createTable();
        add(scrollPane);
        setVisible(true);

    }

    private JScrollPane createTable(){
        var rawItems = dataController.getRawItems();
        Network network = this.controller.getResult();
        int rowLength = rawItems.size()+1;
        String[] columnNames = new String[rowLength];
        columnNames[0] = "Name";
        int counter = 1;
        for(var rawItemPair: rawItems.entrySet()) {
            columnNames[counter] = rawItemPair.getKey().getName();
            counter++;
        }

        Object[][] data = new Object[network.getAmountUsedMap().entrySet().size()][rowLength];

        int xcounter = 0;
        //iterate through items

        for(var l: network.getAmountUsedMap().entrySet()){

            Pair<Item, Float> pair = new ImmutablePair<>(l.getKey().getItem(), l.getValue());
            //var usedRawItems = network.calculateResourceConsumption(pair, new HashMap<>(), new HashMap<>());
            var usedRawItems = network.hasToProduceRawResources(
                    List.of(
                            new ImmutablePair<>(l.getKey().getItem(), 1f*l.getValue())),
                    new HashMap<>(), new HashMap<>());


            data[xcounter][0] = pair.getKey().getName()+": "+pair.getValue();
            //iter through  raw items
            int ycounter = 1;
            for(var rawItemPair: rawItems.entrySet()) {
                float amount = 0;
                Item rawItem = rawItemPair.getKey();
                for(var k:usedRawItems.entrySet()){
                    Item lel = k.getKey().getItem();
                    if(rawItem.equals(lel)){
                        amount = k.getValue();
                        break;
                    }
                }
                data[xcounter][ycounter] = amount;
                ycounter++;
            }
            xcounter++;
        }

        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if(columnIndex==0)
                    return String.class;
                else
                    return Float.class;
            }
        };
        JTable table = new JTable(tableModel);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        return scrollPane;

    }
    @Override
    public void update(Subject sub) {

    }
}
