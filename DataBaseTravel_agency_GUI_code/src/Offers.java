import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Offers extends JFrame {
    private JTable insert;
    private JTextField textFieldOID;
    private JTextField textFieldSD;
    private JTextField textFieldC;
    private JTextField textFieldED;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Offers;
    private JComboBox<Object> comboBoxID;
    private JTextField textFieldD;
    private String offer_id;
    private String start_offer_date;
    private String end_offer_date;
    private float offer_cost;
    private String offer_dst_id;
    private String[] columns_name = {"ID", "Start date", "End date", "Cost", "Destination id"};
    private String offer_id_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Offers
    public Offers(String username, String password) {
        setTitle(username);
        setContentPane(Offers);
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
            String query = "SELECT * FROM offers";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                offer_id = String.valueOf(result.getObject("offer_id"));
                Date date = result.getDate("start_offer_date");
                LocalDate date_1_2 = date.toLocalDate();
                start_offer_date = date_1_2.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                Date date_1 = result.getDate("end_offer_date");
                LocalDate date_1_1 = date_1.toLocalDate();
                end_offer_date = date_1_1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                offer_cost = Float.parseFloat(String.valueOf(result.getObject("offer_cost")));
                offer_dst_id = String.valueOf(result.getObject("offer_dst_id"));

                Object[] add = {offer_id, start_offer_date, end_offer_date, offer_cost, offer_dst_id};
                temp.addRow(add);
            }

            query = "SELECT dst_id from destination";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxID.addItem(result.getObject("dst_id"));
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
                textFieldOID.setText(temp.getValueAt(selectedRow, 0).toString());
                textFieldSD.setText(temp.getValueAt(selectedRow, 1).toString());
                textFieldED.setText(temp.getValueAt(selectedRow, 2).toString());
                textFieldC.setText(temp.getValueAt(selectedRow, 3).toString());
                int i = 0;
                while (i < comboBoxID.getItemCount()) {
                    if (comboBoxID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 4))) {
                        comboBoxID.setSelectedIndex(i);
                    }
                    i++;
                }
                offer_id_1 = textFieldOID.getText();

                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT dst_name FROM destination where dst_id=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxID.getSelectedItem().toString());
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
                textFieldOID.setText("");
                textFieldSD.setText("");
                textFieldED.setText("");
                textFieldC.setText("");
                comboBoxID.setSelectedIndex(0);
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT dst_name FROM destination where dst_id=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxID.getSelectedItem().toString());
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
                        Offers.this.dispose();
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

        //Εμφάνιση Προορισμού
        comboBoxID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT dst_name FROM destination where dst_id=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxID.getSelectedItem().toString());
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
    }

    //Μέθοδος για εισαγωγή νέων δεδομένων στη βάση δεδομένων
    public void insert(String username, String password) {
        if (selectedRow == -1) {

            try {
                offer_id = textFieldOID.getText();
                start_offer_date = textFieldSD.getText();
                end_offer_date = textFieldED.getText();
                offer_cost = Float.parseFloat(textFieldC.getText());
                offer_dst_id = comboBoxID.getSelectedItem().toString();

                if (offer_id.isEmpty() || start_offer_date.isEmpty() || end_offer_date.isEmpty() || offer_cost < 1 || offer_dst_id.isEmpty()) {
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


                String query = "insert into offers values" +
                        "(?,?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, offer_id);
                insert_1.setString(2, start_offer_date);
                insert_1.setString(3, end_offer_date);
                insert_1.setString(4, String.valueOf(offer_cost));
                insert_1.setString(5, offer_dst_id);
                insert_1.executeUpdate();
                insert_1.close();


                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Offers.this.dispose();
                        new Offers(username, password);
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


                try {
                    offer_id = textFieldOID.getText();
                    start_offer_date = textFieldSD.getText();
                    end_offer_date = textFieldED.getText();
                    offer_cost = Float.parseFloat(textFieldC.getText());
                    offer_dst_id = comboBoxID.getSelectedItem().toString();
                    ;

                    if (offer_id.isEmpty() || start_offer_date.isEmpty() || end_offer_date.isEmpty() || offer_cost < 1 || offer_dst_id.isEmpty()) {
                        throw new Exception();
                    }

                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }


                String upd_query = "update offers set offer_id=?,start_offer_date=?,end_offer_date=?," +
                        "offer_cost=?,offer_dst_id=? where offer_id=?";

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, offer_id);
                update.setString(2, start_offer_date);
                update.setString(3, end_offer_date);
                update.setString(4, String.valueOf(offer_cost));
                update.setString(5, offer_dst_id);
                update.setString(6, offer_id_1);
                update.executeUpdate();
                update.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Offers.this.dispose();
                        new Offers(username, password);
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

    //Μέθοδος για τη διαγραφή ενός στοιχείου από τη βάση δεδομένωνquery_2
    public void delete(String username, String password) {
        if (selectedRow != -1) {
            offer_id = textFieldOID.getText();


            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from offers where offer_id=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, offer_id);
                delete_1.executeUpdate();
                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Offers.this.dispose();
                        new Offers(username, password);
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
