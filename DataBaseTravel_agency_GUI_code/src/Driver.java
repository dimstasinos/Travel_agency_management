import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Driver extends JFrame {
    private JTable insert;
    private JTextField textFieldEX;
    private JComboBox comboBoxLI;
    private JComboBox comboBoxRoute;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Driver;
    private JComboBox<Object> comboBoxDAT;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private String drv_AT;
    private String drv_license;
    private String drv_route;
    private int drv_experience;
    private String[] columns_name = {"AT", "License", "Route", "Experience"};
    private String drv_AT_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Driver
    public Driver(String username, String password) {
        setTitle(username);
        setContentPane(Driver);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        DefaultTableModel temp = new DefaultTableModel();
        temp.setColumnIdentifiers(columns_name);

        //Τοποθέτηση στοιχείων από τη βάση δεδομένων στον πίνακα
        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, password)) {

            Statement print = connect.createStatement();
            String query = "SELECT * FROM driver";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                drv_AT = String.valueOf(result.getObject("drv_AT"));
                drv_license = String.valueOf(result.getObject("drv_license"));
                drv_route = String.valueOf(result.getObject("drv_route"));
                drv_experience = Integer.parseInt(String.valueOf(result.getObject("drv_experience")));
                Object[] add = {drv_AT, drv_license, drv_route, drv_experience};
                temp.addRow(add);
            }

            query = "SELECT wrk_AT from worker";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxDAT.addItem(result.getObject("wrk_AT"));
            }

            connect.close();
            result.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }

        insert.setModel(temp);
        setVisible(true);

        //Εισαγωγή δεδομένων στα πεδία του παραθύρου
        insert.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                DefaultTableModel temp = (DefaultTableModel) insert.getModel();
                selectedRow = insert.getSelectedRow();
                int i = 0;
                while (i < comboBoxDAT.getItemCount()) {
                    if (comboBoxDAT.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 0))) {
                        comboBoxDAT.setSelectedIndex(i);
                    }
                    i++;
                }
                textFieldEX.setText(temp.getValueAt(selectedRow, 3).toString());
                if (temp.getValueAt(selectedRow, 2).toString().equals("LOCAL")) {
                    comboBoxRoute.setSelectedIndex(0);
                } else {
                    comboBoxRoute.setSelectedIndex(1);
                }
                if (temp.getValueAt(selectedRow, 1).toString().equals("A")) {
                    comboBoxLI.setSelectedIndex(0);
                } else if (temp.getValueAt(selectedRow, 1).toString().equals("B")) {
                    comboBoxLI.setSelectedIndex(1);
                } else if (temp.getValueAt(selectedRow, 1).toString().equals("C")) {
                    comboBoxLI.setSelectedIndex(2);
                } else {
                    comboBoxLI.setSelectedIndex(3);
                }

                drv_AT_1 = comboBoxDAT.getSelectedItem().toString();
            }
        });

        //Αλλαγή στο προηγούμενο παράθυρο Database
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Driver.this.dispose();
                        new Database(username, password);
                    }
                });
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxDAT.setSelectedIndex(0);
                textFieldEX.setText("");
                comboBoxLI.setSelectedIndex(0);
                comboBoxRoute.setSelectedIndex(0);
                textFieldLN.setText("");
                textFieldFN.setText("");
                selectedRow = -1;
            }
        });

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insert(username, password);
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update(username, password);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete(username, password);
            }
        });
        comboBoxDAT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT wrk_name FROM worker where wrk_AT=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxDAT.getSelectedItem().toString());
                    ResultSet result = print_1.executeQuery();
                    result.next();
                    textFieldFN.setText(result.getString("wrk_name"));


                    query = "SELECT wrk_lname FROM worker where wrk_AT=?";
                    print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxDAT.getSelectedItem().toString());
                    result = print_1.executeQuery();
                    result.next();
                    textFieldLN.setText(result.getString("wrk_lname"));


                    print_1.close();


                } catch (SQLException t) {
                    JOptionPane.showMessageDialog(null, t, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
    }

    //Μέθοδος για τη διαγραφή ενός στοιχείου από τη βάση δεδομένων
    public void delete(String username, String password) {
        if (selectedRow != -1) {

            drv_AT = comboBoxDAT.getSelectedItem().toString();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from driver where drv_AT=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, drv_AT);
                delete_1.executeUpdate();
                delete_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Driver.this.dispose();
                        new Driver(username, password);
                    }
                });

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e, "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a row", "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    //Μέθοδος για αλλαγή των δεδομένων στη βάση δεδομένων
    public void update(String username, String password) {
        if (selectedRow != -1) {
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String upd_query = "update driver set drv_AT=?,drv_license=?,drv_route=?,drv_experience=?" +
                        " where drv_AT=?";
                try {
                    drv_AT = comboBoxDAT.getSelectedItem().toString();
                    drv_license = comboBoxLI.getSelectedItem().toString();
                    drv_route = comboBoxRoute.getSelectedItem().toString();
                    drv_experience = Integer.parseInt(textFieldEX.getText());

                    if (drv_AT.isEmpty() || drv_license.isEmpty() || drv_route.isEmpty() || drv_experience < 0) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, drv_AT);
                update.setString(2, drv_license);
                update.setString(3, drv_route);
                update.setString(4, String.valueOf(drv_experience));
                update.setString(5, drv_AT_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Driver.this.dispose();
                        new Driver(username, password);
                    }
                });

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e, "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select a row", "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }

    }

    //Μέθοδος για εισαγωγή νέων δεδομένων στη βάση δεδομένων
    public void insert(String username, String password) {
        if (selectedRow == -1) {

            try {
                drv_AT = comboBoxDAT.getSelectedItem().toString();
                drv_experience = Integer.parseInt(textFieldEX.getText());
                drv_license = (String) comboBoxLI.getSelectedItem();
                drv_route = (String) comboBoxRoute.getSelectedItem();

                if (drv_AT.isEmpty() || drv_experience < 0 || drv_license.isEmpty() || drv_route.isEmpty()) {
                    throw new Exception();
                }

            } catch (Exception a) {
                JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;

            }
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String query = "insert into driver values" +
                        "(?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, drv_AT);
                insert_1.setString(2, drv_license);
                insert_1.setString(3, drv_route);
                insert_1.setString(4, String.valueOf(drv_experience));
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Driver.this.dispose();
                        new Driver(username, password);
                    }
                });

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e, "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Clear Data", "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }

    }


}
