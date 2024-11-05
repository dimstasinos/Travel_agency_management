import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ReservationOffers extends JFrame {
    private JTable insert;
    private JTextField textFieldRID;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private JComboBox<Object> comboBoxOID;
    private JTextField textFieldDEP;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton backButton;
    private JPanel reservationoffer;
    private String res_id;
    private String l_name;
    private String f_name;
    private String res_offer_id;
    private Float deposit;
    private String[] columns_name = {"Reservation id", "First Name", "Last Name", "Offer id", "Deposit"};
    private int selectedRow = -1;
    private String res_id_1;

    //Constructor που φτιάχνει το παράθυρο Reservations
    public ReservationOffers(String username, String password) {

        setTitle(username);
        setContentPane(reservationoffer);
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
            String query = "SELECT * FROM reservation_offers";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                res_id = String.valueOf(result.getObject("res_id"));
                f_name = String.valueOf(result.getObject("f_name"));
                l_name = String.valueOf(result.getObject("l_name"));
                res_offer_id = String.valueOf(result.getObject("res_offer_id"));
                deposit = Float.parseFloat(String.valueOf(result.getObject("deposit")));
                Object[] add = {res_id, f_name, l_name, res_offer_id, deposit};
                temp.addRow(add);
            }

            query = "SELECT offer_id from offers";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxOID.addItem(result.getObject("offer_id"));
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
                textFieldRID.setText(temp.getValueAt(selectedRow, 0).toString());
                textFieldLN.setText(temp.getValueAt(selectedRow, 2).toString());
                textFieldFN.setText(temp.getValueAt(selectedRow, 1).toString());
                comboBoxOID.setSelectedItem(temp.getValueAt(selectedRow, 3).toString());
                textFieldDEP.setText(temp.getValueAt(selectedRow, 4).toString());
                res_id_1 = textFieldRID.getText();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                textFieldRID.setText("");
                textFieldLN.setText("");
                textFieldFN.setText("");
                comboBoxOID.setSelectedIndex(0);
                textFieldDEP.setText("");
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
                        ReservationOffers.this.dispose();
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

    }

    //Μέθοδος για εισαγωγή νέων δεδομένων στη βάση δεδομένων
    public void insert(String username, String password) {
        if (selectedRow == -1) {

            try {

                res_id = textFieldRID.getText();
                l_name = textFieldLN.getText();
                f_name = textFieldFN.getText();
                res_offer_id = (String) comboBoxOID.getSelectedItem();
                deposit = Float.valueOf(textFieldDEP.getText());

                if (res_id.isEmpty() || l_name.isEmpty() || f_name.isEmpty() || res_offer_id.isEmpty()) {
                    throw new Exception("Give Data to all fields and correct types");
                }
                if (deposit < 20) {
                    throw new Exception("Τhe deposit cannot be less than 20 euros");
                }


            } catch (Exception a) {
                JOptionPane.showMessageDialog(this, a, "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;

            }
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String query = "insert into reservation_offers values " +
                        "(?,?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, res_id);
                insert_1.setString(2, l_name);
                insert_1.setString(3, f_name);
                insert_1.setString(4, res_offer_id);
                insert_1.setString(5, String.valueOf(deposit));
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ReservationOffers.this.dispose();
                        new ReservationOffers(username, password);
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

                String upd_query = "update reservation_offers set res_id=?, l_name=?," +
                        "f_name=?,res_offer_id=?,deposit=?" +
                        "where res_id=?";

                try {
                    res_id = textFieldRID.getText();
                    l_name = textFieldLN.getText();
                    f_name = textFieldFN.getText();
                    res_offer_id = comboBoxOID.getSelectedItem().toString();
                    deposit = Float.valueOf(textFieldDEP.getText());

                    if (res_id.isEmpty() || l_name.isEmpty() || f_name.isEmpty() || res_offer_id.isEmpty()) {
                        throw new Exception("Give Data to all fields and correct types");
                    }
                    if (deposit < 20) {
                        throw new Exception("Τhe deposit cannot be less than 20 euros");
                    }

                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, a, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, res_id);
                update.setString(2, l_name);
                update.setString(3, f_name);
                update.setString(4, res_offer_id);
                update.setString(5, String.valueOf(deposit));
                update.setString(6, res_id_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ReservationOffers.this.dispose();
                        new ReservationOffers(username, password);
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

            res_id = textFieldRID.getText();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from reservation_offers where res_id=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, res_id);
                delete_1.executeUpdate();
                delete_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ReservationOffers.this.dispose();
                        new ReservationOffers(username, password);
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
