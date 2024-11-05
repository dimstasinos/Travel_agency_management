import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Trip extends JFrame {
    private JTable insert;
    private JTextField textFieldTID;
    private JTextField textFieldDE;
    private JTextField textFieldRE;
    private JTextField textFieldMS;
    private JTextField textFieldC;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Trip;
    private JComboBox<Object> comboBoxBID;
    private JComboBox<Object> comboBoxGAT;
    private JComboBox<Object> comboBoxDAT;
    private JTextField textFieldGFN;
    private JTextField textFieldGLN;
    private JTextField textFieldDFN;
    private JTextField textFieldDLN;
    private String tr_id;
    private String tr_departure;
    private String tr_return;
    private int tr_maxseats;
    private float tr_cost;
    private String tr_br_code;
    private String tr_gui_AT;
    private String tr_drv_AT;
    private String[] columns_name = {"ID", "Departure", "Return", "Maxseats", "Cost", "Branch id", "Guide AT", "Driver AT"};
    private String tr_id_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Trip
    public Trip(String username, String password) {
        setTitle(username);
        setContentPane(Trip);
        setMinimumSize(new Dimension(1000, 800));
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
            String query = "SELECT * FROM trip";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                tr_id = String.valueOf(result.getObject("tr_id"));
                Timestamp date = result.getTimestamp("tr_departure");
                LocalDateTime time = date.toLocalDateTime();
                tr_departure = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                Timestamp date_1 = result.getTimestamp("tr_return");
                LocalDateTime time_1 = date_1.toLocalDateTime();
                tr_return = time_1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                tr_maxseats = Integer.parseInt(String.valueOf(result.getObject("tr_maxseats")));
                tr_cost = Float.parseFloat(String.valueOf(result.getObject("tr_cost")));
                tr_br_code = String.valueOf(result.getObject("tr_br_code"));
                tr_gui_AT = String.valueOf(result.getObject("tr_gui_AT"));
                tr_drv_AT = String.valueOf(result.getObject("TR_DRV_AT"));
                Object[] add = {tr_id, tr_departure, tr_return, tr_maxseats, tr_cost, tr_br_code, tr_gui_AT, tr_drv_AT};
                temp.addRow(add);
            }


            query = "SELECT br_code from branch";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxBID.addItem(result.getObject("br_code"));
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
                textFieldTID.setText(temp.getValueAt(selectedRow, 0).toString());
                textFieldDE.setText(temp.getValueAt(selectedRow, 1).toString());
                textFieldRE.setText(temp.getValueAt(selectedRow, 2).toString());
                textFieldMS.setText(temp.getValueAt(selectedRow, 3).toString());
                textFieldC.setText(temp.getValueAt(selectedRow, 4).toString());
                int i = 0;
                while (i < comboBoxBID.getItemCount()) {
                    if (comboBoxBID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 5))) {
                        comboBoxBID.setSelectedIndex(i);
                    }
                    i++;
                }
                i = 0;
                while (i < comboBoxGAT.getItemCount()) {
                    if (comboBoxGAT.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 6))) {
                        comboBoxGAT.setSelectedIndex(i);
                    }
                    i++;
                }
                i = 0;
                while (i < comboBoxDAT.getItemCount()) {
                    if (comboBoxDAT.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 7))) {
                        comboBoxDAT.setSelectedIndex(i);
                    }
                    i++;
                }
                tr_id_1 = textFieldTID.getText();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldTID.setText("");
                textFieldDE.setText("");
                textFieldRE.setText("");
                textFieldMS.setText("");
                textFieldC.setText("");
                comboBoxBID.setSelectedIndex(0);
                comboBoxGAT.setSelectedIndex(0);
                comboBoxDAT.setSelectedIndex(0);
                textFieldGFN.setText("");
                textFieldGLN.setText("");
                textFieldDFN.setText("");
                textFieldDLN.setText("");
                selectedRow = -1;
            }
        });

        //Αλλαγή στο προηγούμενο παράθυρο Database
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Trip.this.dispose();
                        new Database(username, password);
                    }
                });
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

        comboBoxGAT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {
                    if (comboBoxGAT.getItemCount() != 0) {
                        String query = "SELECT wrk_name FROM worker where wrk_AT=?";
                        PreparedStatement print_1 = connect.prepareStatement(query);
                        print_1.setString(1, comboBoxGAT.getSelectedItem().toString());
                        ResultSet result = print_1.executeQuery();
                        result.next();
                        textFieldGFN.setText(result.getString("wrk_name"));

                        query = "SELECT wrk_lname FROM worker where wrk_AT=?";
                        print_1 = connect.prepareStatement(query);
                        print_1.setString(1, comboBoxGAT.getSelectedItem().toString());
                        result = print_1.executeQuery();
                        result.next();
                        textFieldGLN.setText(result.getString("wrk_lname"));

                        print_1.close();
                    }
                } catch (SQLException t) {
                    JOptionPane.showMessageDialog(null, t, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        comboBoxDAT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {
                    if (comboBoxDAT.getItemCount() != 0) {
                        String query = "SELECT wrk_name FROM worker where wrk_AT=?";
                        PreparedStatement print_1 = connect.prepareStatement(query);
                        print_1.setString(1, comboBoxDAT.getSelectedItem().toString());
                        ResultSet result = print_1.executeQuery();
                        result.next();
                        textFieldDFN.setText(result.getString("wrk_name"));


                        query = "SELECT wrk_lname FROM worker where wrk_AT=?";
                        print_1 = connect.prepareStatement(query);
                        print_1.setString(1, comboBoxDAT.getSelectedItem().toString());
                        result = print_1.executeQuery();
                        result.next();
                        textFieldDLN.setText(result.getString("wrk_lname"));

                        print_1.close();
                    }
                } catch (SQLException t) {
                    JOptionPane.showMessageDialog(null, t, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        comboBoxBID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {


                    if (comboBoxGAT.getItemCount() != 0) {
                        comboBoxGAT.removeAllItems();
                    }

                    String query = "select wrk_AT from worker inner join guide on wrk_AT=gui_AT where wrk_br_code=?";
                    PreparedStatement print_2 = connect.prepareStatement(query);
                    print_2.setString(1, comboBoxBID.getSelectedItem().toString());
                    ResultSet result = print_2.executeQuery();

                    while (result.next()) {
                        comboBoxGAT.addItem(result.getObject("wrk_AT"));
                    }

                    result.close();
                    if (comboBoxDAT.getItemCount() != 0) {
                        comboBoxDAT.removeAllItems();
                    }

                    query = "select wrk_AT from worker inner join driver on wrk_AT=drv_AT where wrk_br_code=?";
                    print_2 = connect.prepareStatement(query);
                    print_2.setString(1, comboBoxBID.getSelectedItem().toString());
                    result = print_2.executeQuery();

                    while (result.next()) {
                        comboBoxDAT.addItem(result.getObject("wrk_AT"));
                    }


                    print_2.close();


                } catch (SQLException t) {
                    JOptionPane.showMessageDialog(null, t, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
    }

    //Μέθοδος για εισαγωγή νέων δεδομένων στη βάση δεδομένων
    public void insert(String username, String password) {
        if (selectedRow == -1) {

            try {
                textFieldTID.setText("");
                tr_departure = textFieldDE.getText();
                tr_return = textFieldRE.getText();
                tr_maxseats = Integer.parseInt(textFieldMS.getText());
                tr_cost = Float.parseFloat(textFieldC.getText());
                tr_br_code = comboBoxBID.getSelectedItem().toString();
                tr_gui_AT = comboBoxGAT.getSelectedItem().toString();
                tr_drv_AT = comboBoxDAT.getSelectedItem().toString();

                if (tr_departure.isEmpty() || tr_return.isEmpty() || tr_maxseats < 1 || tr_cost < 1 || tr_br_code.isEmpty() || tr_gui_AT.isEmpty() || tr_drv_AT.isEmpty()) {
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

                String query = "insert into trip values" +
                        "(NULL,?,?,?,?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, tr_departure);
                insert_1.setString(2, tr_return);
                insert_1.setString(3, String.valueOf(tr_maxseats));
                insert_1.setString(4, String.valueOf(tr_cost));
                insert_1.setString(5, tr_br_code);
                insert_1.setString(6, tr_gui_AT);
                insert_1.setString(7, tr_drv_AT);
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Trip.this.dispose();
                        new Trip(username, password);
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

    //Μέθοδος για αλλαγή των δεδομένων στη βάση δεδομένων
    public void update(String username, String password) {
        if (selectedRow != -1) {
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String upd_query = "update trip set tr_id=?,tr_departure=?,tr_return=?,tr_maxseats=?,tr_cost=?," +
                        "tr_br_code=?,tr_gui_AT=?,tr_drv_AT=? where tr_id=?";
                try {
                    tr_id = textFieldTID.getText();
                    tr_departure = textFieldDE.getText();
                    tr_return = textFieldRE.getText();
                    tr_maxseats = Integer.parseInt(textFieldMS.getText());
                    tr_cost = Float.parseFloat(textFieldC.getText());
                    tr_br_code = comboBoxBID.getSelectedItem().toString();
                    tr_gui_AT = comboBoxGAT.getSelectedItem().toString();
                    tr_drv_AT = comboBoxDAT.getSelectedItem().toString();

                    if (tr_id.isEmpty() || tr_departure.isEmpty() || tr_return.isEmpty() || tr_maxseats < 1 || tr_cost < 1 || tr_br_code.isEmpty() || tr_gui_AT.isEmpty() || tr_drv_AT.isEmpty()) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, tr_id);
                update.setString(2, tr_departure);
                update.setString(3, tr_return);
                update.setString(4, String.valueOf(tr_maxseats));
                update.setString(5, String.valueOf(tr_cost));
                update.setString(6, tr_br_code);
                update.setString(7, tr_gui_AT);
                update.setString(8, tr_drv_AT);
                update.setString(9, tr_id_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Trip.this.dispose();
                        new Trip(username, password);
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

    //Μέθοδος για τη διαγραφή ενός στοιχείου από τη βάση δεδομένων
    public void delete(String username, String password) {
        if (selectedRow != -1) {

            tr_id = textFieldTID.getText();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from trip where tr_id=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, tr_id);
                delete_1.executeUpdate();
                delete_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Trip.this.dispose();
                        new Trip(username, password);
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


}
