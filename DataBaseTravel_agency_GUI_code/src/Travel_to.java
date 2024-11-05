import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Travel_to extends JFrame {
    private JTable insert;
    private JTextField textFieldDE;
    private JTextField textFieldAR;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Travel_to;
    private JComboBox<Object> comboBoxTID;
    private JComboBox<Object> comboBoxDID;
    private JTextField textFieldD;
    private String to_tr_id;
    private String to_dst_id;
    private String to_arrival;
    private String to_departure;
    private String[] columns_name = {"Trip ID", "Destination ID", "Arrival Date", "Departure Date"};
    private String to_tr_id_1;
    private String to_dst_id_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Travel_to
    public Travel_to(String username, String password) {
        setTitle(username);
        setContentPane(Travel_to);
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
            String query = "SELECT * FROM travel_to";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                to_tr_id = String.valueOf(result.getObject("to_tr_id"));
                to_dst_id = String.valueOf(result.getObject("to_dst_id"));
                Timestamp date = result.getTimestamp("to_arrival");
                LocalDateTime time = date.toLocalDateTime();
                to_arrival = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                Timestamp date_1 = result.getTimestamp("to_departure");
                LocalDateTime time_1 = date_1.toLocalDateTime();
                to_departure = time_1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));

                Object[] add = {to_tr_id, to_dst_id, to_arrival, to_departure};
                temp.addRow(add);
            }

            query = "SELECT tr_id from trip";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxTID.addItem(result.getObject("tr_id"));
            }

            query = "SELECT dst_id from destination";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxDID.addItem(result.getObject("dst_id"));
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
                while (i < comboBoxTID.getItemCount()) {
                    if (comboBoxTID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 0))) {
                        comboBoxTID.setSelectedIndex(i);
                    }
                    i++;
                }
                i = 0;
                while (i < comboBoxDID.getItemCount()) {
                    if (comboBoxDID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 1))) {
                        comboBoxDID.setSelectedIndex(i);
                    }
                    i++;
                }
                textFieldAR.setText(temp.getValueAt(selectedRow, 2).toString());
                textFieldDE.setText(temp.getValueAt(selectedRow, 3).toString());
                to_tr_id_1 = comboBoxTID.getSelectedItem().toString();
                to_dst_id_1 = comboBoxDID.getSelectedItem().toString();

                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT dst_name FROM destination where dst_id=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxDID.getSelectedItem().toString());
                    ResultSet result = print_1.executeQuery();
                    while (result.next()) {
                        textFieldD.setText(result.getString("dst_name"));
                    }

                    print_1.close();


                } catch (SQLException t) {
                    JOptionPane.showMessageDialog(null, t, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }


            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldAR.setText("");
                comboBoxTID.setSelectedIndex(0);
                comboBoxDID.setSelectedIndex(0);
                textFieldDE.setText("");
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT dst_name FROM destination where dst_id=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxDID.getSelectedItem().toString());
                    ResultSet result = print_1.executeQuery();
                    while (result.next()) {
                        textFieldD.setText(result.getString("dst_name"));
                    }

                    print_1.close();

                    textFieldDE.setText("");
                } catch (SQLException t) {
                    JOptionPane.showMessageDialog(null, t, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
                        Travel_to.this.dispose();
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
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete(username, password);
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update(username, password);
            }
        });
        comboBoxDID.addComponentListener(new ComponentAdapter() {
        });
        //Εμφάνιση προορισμού
        comboBoxDID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT dst_name FROM destination where dst_id=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxDID.getSelectedItem().toString());
                    ResultSet result = print_1.executeQuery();
                    result.next();
                    textFieldD.setText(result.getString("dst_name"));
                    print_1.close();


                } catch (SQLException t) {
                    JOptionPane.showMessageDialog(null, t, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
    }

    //Μέθοδος για αλλαγή των δεδομένων στη βάση δεδομένων
    public void update(String username, String password) {
        if (selectedRow != -1) {
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String upd_query = "update travel_to set to_tr_id=?,to_dst_id=?,to_arrival=?," +
                        "to_departure=? where to_tr_id=? and to_dst_id=?";


                try {
                    to_tr_id = comboBoxTID.getSelectedItem().toString();
                    to_dst_id = comboBoxDID.getSelectedItem().toString();
                    to_arrival = textFieldAR.getText();
                    to_departure = textFieldDE.getText();


                    if (to_tr_id.isEmpty() || to_dst_id.isEmpty() || to_arrival.isEmpty() || to_departure.isEmpty()) {
                        throw new Exception();
                    }

                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, to_tr_id);
                update.setString(2, to_dst_id);
                update.setString(3, to_arrival);
                update.setString(4, to_departure);
                update.setString(5, to_tr_id_1);
                update.setString(6, to_dst_id_1);

                update.executeUpdate();

                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Travel_to.this.dispose();
                        new Travel_to(username, password);
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
            to_tr_id = comboBoxTID.getSelectedItem().toString();
            to_dst_id = comboBoxDID.getSelectedItem().toString();


            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from travel_to where to_tr_id=? and to_dst_id=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, to_tr_id);
                delete_1.setString(2, to_dst_id);

                delete_1.executeUpdate();
                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Travel_to.this.dispose();
                        new Travel_to(username, password);
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
                to_tr_id = comboBoxTID.getSelectedItem().toString();
                to_dst_id = comboBoxDID.getSelectedItem().toString();
                to_arrival = textFieldAR.getText();
                to_departure = textFieldDE.getText();

                if (to_tr_id.isEmpty() || to_dst_id.isEmpty() || to_arrival.isEmpty() || to_departure.isEmpty()) {
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

                String query = "insert into travel_to values" +
                        "(?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, to_tr_id);
                insert_1.setString(2, to_dst_id);
                insert_1.setString(3, to_arrival);
                insert_1.setString(4, to_departure);

                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Travel_to.this.dispose();
                        new Travel_to(username, password);
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
