package GUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

public class ChainingPopup {
    private String current_directory;

    private JFrame frame;

    private JButton generate;
    private JButton fileButton;

    public JComboBox selectTable;

    private JMenuBar attrPanel1;
    private JMenuBar attributeSelection;

    private JPanel panel1;
    private JPanel search1;
    private JPanel search2;
    private JPanel output;

    public JTable chain_table1;

    private JComboBox searchMenu1;
    private JComboBox searchMenu2;
    private JScrollPane scrollPane;
    private JLabel searchLabel2;

    enum ACTION {IDEALTEAM, TEAMCHAIN}

    private ACTION setting;

    private NCAAQuery.PostgresqlJava sqlConnection;
    private Graph g;
    private IdealTeam it;

    public ChainingPopup(NCAAQuery.PostgresqlJava conn) {
        $$$setupUI$$$();

        current_directory = System.getProperty("user.dir");
        frame = new JFrame("NCAAQuery Chaining Module");
        sqlConnection = conn;
        g = new Graph();
        it = new IdealTeam();
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(current_directory + File.separator + "src" + File.separator + "315Logo.png"));
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        init();

    }

    private void updateUI(Frame frame) {
        SwingUtilities.updateComponentTreeUI(frame);
        frame.pack();
    }

    private void init() {
        setComboBox(selectTable, sqlConnection.tables);
    }

    private void setComboBox(JComboBox target, ArrayList input) {

        final DefaultComboBoxModel combo_box_model = new DefaultComboBoxModel();
        combo_box_model.addElement("Select a table:");
        for (Object item : input) {
            if (item.toString().equals("Ideal_teams") || item.toString().equals("Team")) {
                combo_box_model.addElement(item);
            }
        }
        target.setModel(combo_box_model);
        updateInterface(frame);
    }


    private void updateSearch(Table selectedItem) {
        try {
            JMenu attrMenu = new JMenu();

            if (selectedItem.items == null || selectedItem.items.size() == 0) {
                selectedItem.setItems(sqlConnection.getItems(selectedItem));
            }

            Table stats;
            if (selectedItem.name.equals("Team")) { // team chaining

                stats = sqlConnection.getTable("Team_stats");
                setting = ACTION.TEAMCHAIN;

                searchMenu1.removeAllItems();
                searchMenu2.removeAllItems();
                searchMenu1.addItem("Search for " + selectedItem.name + ":");
                searchMenu2.addItem("Search for " + selectedItem.name + ":");

                for (DBObject obj : selectedItem.items) {
                    searchMenu1.addItem(obj);
                    searchMenu2.addItem(obj);
                } // Combo boxes are inherently searchable
                searchMenu1.setVisible(true);
                searchMenu2.setVisible(true);
                search2.setVisible(true);


            } else { // Ideal team selection

                stats = sqlConnection.getTable("Ideal_teams");
                setting = ACTION.IDEALTEAM;

                searchMenu1.removeAllItems();
                searchMenu2.removeAllItems();
                searchMenu1.addItem("Search for " + selectedItem.name + ":");

                //System.out.println("ITEMS :: " + selectedItem.items.toString());
                for (DBObject obj : selectedItem.items) {
                    searchMenu1.addItem(obj);
                } // Combo boxes are inherently searchable
                searchMenu1.setVisible(true);
                searchMenu2.setVisible(false);
                search2.setVisible(false);
            }
            updateUI(frame);
        } catch (Exception e) {
            System.out.println("updateSearch Error :: " + e.toString());
        }
    }

    private void updateInterface(Frame frame) {

        selectTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    JComboBox comboBox = (JComboBox) event.getSource();
                    Table selected = (Table) comboBox.getSelectedItem();
                    updateSearch(selected);
                } catch (Exception e) {
                    System.out.println("Table Select Error :: " + e.toString());
                }
            }
        });

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    // Selecting a file folder to save to
                    // Reference @ https://stackoverflow.com/questions/11580606/get-location-to-save-a-file-without-selecting-giving-a-file-in-save-open-dialog
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.showSaveDialog(null);

                    TableModel table = chain_table1.getModel();

                    File f;
                    if (setting == ACTION.TEAMCHAIN) {
                        f = new File(chooser.getSelectedFile() + File.separator + "NCAAQuery_VictoryChain.csv");
                    } else {
                        f = new File(chooser.getSelectedFile() + File.separator + "NCAAQuery_WinningTeam.csv");
                    }
                    if (f.exists()) {
                        f.delete();
                        f.createNewFile();
                    }
                    FileWriter csv = new FileWriter(f);
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        csv.write(table.getColumnName(i) + ",");
                    }
                    csv.write("\n");
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            csv.write(table.getValueAt(i, j).toString() + ",");
                        }
                        csv.write("\n");
                    }
                    csv.close();
                } catch (Exception e) {
                    System.out.println("SAVETOCSVButton Error :: " + e.toString());
                }
            }
        });

        generate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    if (selectTable.getSelectedItem().toString().equals("Select a table:")) {
                        JOptionPane.showMessageDialog(frame, "Please Choose a Setting", "ALERT", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    clearResults();
                    if (setting == ACTION.TEAMCHAIN) {
                        graphProcessingWrapper();
                    } else {
                        idealTeamWrapper();
                    }
                    updateUI(frame);
                } catch (Exception ex) {
                    System.out.println("generateButton Error :: " + ex.toString());
                }
            }
        });
    }

    private void clearResults() {
        chain_table1.clearSelection();
    }

    private void dataStructureInitialization() {
        try {

            Statement stmt = sqlConnection.conn.createStatement();
            ResultSet dat;
            String table, sqlStatement;
            ArrayList<String> tags = new ArrayList<>();

            if (setting == ACTION.IDEALTEAM) {
                tags.addAll(Arrays.asList("firstname", "lastname", "name", "avg", "count"));
                table = "team_players";
            } else {
                tags.addAll(Arrays.asList("winner_name", "loser_name", "seasonid"));
                table = "game_winners";
            }

            sqlStatement = "SELECT " + tags.toString().substring(1, tags.toString().length() - 1) + " FROM " + table;
            dat = stmt.executeQuery(sqlStatement);
            dataStructureSetup(dat, tags);

        } catch (Exception e) {
            System.out.println("graphInitialization Error :: " + e.toString());
        }
    }

    private void dataStructureSetup(ResultSet rs, ArrayList<String> tags) {
        try {

            Object pivot_column, pivot_column_two, pivot_column_three, target_column, aux_column;
            String fname, lname;

            while (rs.next()) {

                if (ACTION.TEAMCHAIN == setting) {
                    pivot_column = rs.getObject(tags.get(0)); // pivot reference, winner
                    target_column = rs.getObject(tags.get(1)); // target reference, loser
                    aux_column = rs.getObject(tags.get(2));
                    if ((pivot_column == null) || (target_column == null)) // checking for null cases - throws an exception in the addVertex function if present
                        continue;

                    g.addVertex(pivot_column);             // adding id into graph vertex
                    g.addVertex(target_column);            // adding auxiliary graph vertices
                    g.addAuxData(target_column, pivot_column, aux_column); // adding years victories were achieved over losing teams
                    g.addEdge(pivot_column, target_column);
                } else {

                    if (rs.getObject(tags.get(0)) == null) {
                        fname = "";
                    } else {
                        fname = rs.getObject(tags.get(0)).toString();
                    }
                    if (rs.getObject(tags.get(1)) == null) {
                        lname = "";
                    } else {
                        lname = rs.getObject(tags.get(1)).toString();
                    }
                    target_column = fname + " " + lname; // name

                    if (rs.getObject(tags.get(2)) == null || (rs.getObject(tags.get(3)) == null) || (rs.getObject(tags.get(4)) == null)) {
                        continue;
                    }

                    pivot_column = rs.getObject(tags.get(2)); // name
                    pivot_column_two = rs.getObject(tags.get(3)); // avg
                    pivot_column_three = rs.getObject(tags.get(4)); // count

                    it.addTeam(pivot_column); // adding team
                    it.addPlayer(pivot_column, new Pair(target_column, new Pair(pivot_column_two, pivot_column_three))); // adding player @ team

                }
            }

        } catch (Exception e) {
            System.out.println("vertexInitialization Error :: " + e.toString());
        }
    }

    private void idealTeamWrapper() {
        try {

            if (searchMenu1.getSelectedItem() == null || searchMenu1.getSelectedItem().equals("Search for Ideal_teams:")) {
                JOptionPane.showMessageDialog(frame, "Please Select a Team", "ALERT", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dataStructureInitialization();
            //it.prettyPrintTeams();

            ArrayList<Pair<Object, Pair<Object, Object>>> output = it.getIdealTeam(searchMenu1.getSelectedItem());
            Vector<Object[]> list = new Vector<>();

            for (Pair<Object, Pair<Object, Object>> objectPairPair : output) {

                list.add(new Object[]{
                        objectPairPair.getLeft().toString(),

                        objectPairPair.getRight().getLeft().toString(),

                        objectPairPair.getRight().getRight().toString()
                });

            }
            chain_table1.setModel(new DefaultTableModel(list.toArray(new Object[][]{}),
                    new String[]{"Player", "Average Points", "Seasons"}));

            // System.out.println("Finished idealTeamWrapper");
        } catch (Exception e) {
            System.out.println("idealTeamWrapper Error :: " + e.toString());
        }
    }

    private void graphProcessingWrapper() {
        DefaultTableModel model = (DefaultTableModel) chain_table1.getModel();
        try {

            if (searchMenu1.getSelectedItem() == null || searchMenu2.getSelectedItem() == null ||
                    searchMenu1.getSelectedItem().equals("Search for Team:") || searchMenu2.getSelectedItem().equals("Search for Team:")) {
                JOptionPane.showMessageDialog(frame, "Please Select Two Items", "ALERT", JOptionPane.WARNING_MESSAGE);
                return;
            }

            dataStructureInitialization();

            if ((g.getEdgeSize(searchMenu1.getSelectedItem()) < 1) || (g.getEdgeSize(searchMenu1.getSelectedItem()) == -1)) {
                JOptionPane.showMessageDialog(frame, "One Selection Has No Relationship to Any Other Selection", "ALERT", JOptionPane.WARNING_MESSAGE);
                return;
            }

            //g.prettyPrintGraph();
            if (g.BFSWrapper(searchMenu1.getSelectedItem(), searchMenu2.getSelectedItem()) == -1) {
                JOptionPane.showMessageDialog(frame, "The Second Selection Cannot Be Reached With the Given Data", "ALERT", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<Object[]> list = new ArrayList<>();
            ArrayList<Object> results = g.getResults();

            String initial_selection = "";
            for (int i = 0; i < results.size(); i += 2) {
                if (results.get(i).toString().equals(searchMenu1.getSelectedItem().toString())) {
                    initial_selection = results.get(i).toString();
                } else {

                    int reference_index = results.get(i + 1).toString().indexOf(initial_selection + ",") + initial_selection.length();

                    list.add(new Object[]{
                            initial_selection,

                            results.get(i).toString(),

                            results.get(i + 1).toString().substring(reference_index + 2, reference_index + 6),
                    });
                    initial_selection = results.get(i).toString();
                }
            }
            chain_table1.setModel(new DefaultTableModel(list.toArray(new Object[][]{}),
                    new String[]{"Winning Team", "Losing Team", "Year"}));

            updateUI(frame);

        } catch (Exception e) {
            // handle any errors
            System.out.println("graphTableView SQLException: " + e.toString());
        }
    }


// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.setBackground(new Color(-15181146));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel2.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setBackground(new Color(-1312257));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(4, 1, new Insets(20, 20, 20, 20), -1, -1));
        panel4.setBackground(new Color(-1246721));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 1, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel5.setBackground(new Color(-1180929));
        panel4.add(panel5, new GridConstraints(0, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        selectTable = new JComboBox();
        selectTable.setBackground(new Color(-1312257));
        Font selectTableFont = this.$$$getFont$$$("Segoe UI Black", -1, -1, selectTable.getFont());
        if (selectTableFont != null) selectTable.setFont(selectTableFont);
        selectTable.setForeground(new Color(-15181146));
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Select Table 1");
        selectTable.setModel(defaultComboBoxModel1);
        panel5.add(selectTable, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        search1 = new JPanel();
        search1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        search1.setBackground(new Color(-1247233));
        search1.setEnabled(true);
        search1.setForeground(new Color(-1));
        panel5.add(search1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        searchMenu1 = new JComboBox();
        searchMenu1.setBackground(new Color(-1312257));
        Font searchMenu1Font = this.$$$getFont$$$("Segoe UI Black", -1, -1, searchMenu1.getFont());
        if (searchMenu1Font != null) searchMenu1.setFont(searchMenu1Font);
        searchMenu1.setForeground(new Color(-15181146));
        searchMenu1.setToolTipText("Search");
        search1.add(searchMenu1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Search");
        search1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        search2 = new JPanel();
        search2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        search2.setBackground(new Color(-1247233));
        search2.setEnabled(true);
        search2.setForeground(new Color(-1));
        panel5.add(search2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        searchMenu2 = new JComboBox();
        searchMenu2.setBackground(new Color(-1312257));
        Font searchMenu2Font = this.$$$getFont$$$("Segoe UI Black", -1, -1, searchMenu2.getFont());
        if (searchMenu2Font != null) searchMenu2.setFont(searchMenu2Font);
        searchMenu2.setForeground(new Color(-15181146));
        searchMenu2.setToolTipText("Search");
        search2.add(searchMenu2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_NORTHWEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        searchLabel2 = new JLabel();
        searchLabel2.setText("Search");
        search2.add(searchLabel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        output = new JPanel();
        output.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        output.setBackground(new Color(-1312257));
        panel4.add(output, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        output.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        chain_table1 = new JTable();
        scrollPane1.setViewportView(chain_table1);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        panel6.setBackground(new Color(-1312257));
        panel4.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fileButton = new JButton();
        fileButton.setBackground(new Color(-1312769));
        Font fileButtonFont = this.$$$getFont$$$("Segoe UI Black", -1, -1, fileButton.getFont());
        if (fileButtonFont != null) fileButton.setFont(fileButtonFont);
        fileButton.setForeground(new Color(-15181146));
        fileButton.setHideActionText(false);
        fileButton.setText("SAVE TO FILE");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(fileButton, gbc);
        generate = new JButton();
        generate.setBackground(new Color(-1247233));
        Font generateFont = this.$$$getFont$$$("Segoe UI Black", -1, -1, generate.getFont());
        if (generateFont != null) generate.setFont(generateFont);
        generate.setForeground(new Color(-15181146));
        generate.setText("GENERATE");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(generate, gbc);
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/logo.png")));
        label2.setText("");
        panel1.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(30, 30), null, null, 1, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

}
