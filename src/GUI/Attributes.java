package GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.util.ArrayList;
import java.util.List;

public class Attributes extends JFrame {
    private JList jList;
    private JList jListForCopy;
    private JButton copyButton;
    private static String[] listItems;

    String[] attributesOutput;

    public Attributes() {
    }

    public Attributes(String[] li) {

        setLayout(new FlowLayout());
        listItems = li;

        jList = new JList(listItems);
        jList.setFixedCellHeight(15);
        jList.setFixedCellWidth(100);
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setVisibleRowCount(4);
        add(new JScrollPane(jList));

        copyButton = new JButton("Copy>>>");

        copyButton.addActionListener(new ActionListener() {

            // Mouse clicked
            @Override
            public void actionPerformed(ActionEvent e) {
                jListForCopy.setListData(jList.getSelectedValues());
                List list = jList.getSelectedValuesList();
                String[] arr = new String[list.size()];

                for (int i =0; i < list.size(); i++) {
                    arr[i] = list.get(i) + "";
                }

                /*for (String x : arr) {
                    System.out.println(x + " ");
                }*/
            }
        });

        add(copyButton);
        jListForCopy = new JList();
        jListForCopy.setFixedCellHeight(15);
        jListForCopy.setFixedCellWidth(100);
        jList.setVisibleRowCount(4);
        jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        add(new JScrollPane(jListForCopy));
    }

    public void setList(String[] list) {
        new Attributes(list);
    }

    public void setList(ArrayList<String> list) {
        String[] l = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            l[i] = list.get(i);
        }

        new Attributes(l);
    }
}
