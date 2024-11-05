import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Phones extends JFrame {
    private JTable insert;
    private JTextField textFieldPN;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Phones;
    private JComboBox<Object> comboBoxBID;

    private String ph_br_code;
    private String ph_number;
    private String[] columns_name = {"Branch id", "Phone number"};
    private String ph_br_code_1;
    private String ph_number_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Phones
    public Phones(String username, String password) {
        setTitle(username);
        setContentPane(Phones);
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
            String query = "SELECT * FROM phones";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                ph_br_code = String.valueOf(result.getObject("ph_br_code"));
                ph_number = String.valueOf(result.getObject("ph_number"));

                Object[] add = {ph_br_code, ph_number};
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
                textFieldPN.setText(temp.getValueAt(selectedRow, 1).toString());
                int i = 0;
                while (i < comboBoxBID.getItemCount()) {
                    if (comboBoxBID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 0))) {
                        comboBoxBID.setSelectedIndex(i);
                    }
                    i++;
                }
                ph_number_1 = textFieldPN.getText();
                ph_br_code_1 = comboBoxBID.getSelectedItem().toString();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxBID.setSelectedIndex(0);
                textFieldPN.setText("");
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
                        Phones.this.dispose();
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
    }

    //Μέθοδος για αλλαγή των δεδομένων στη βάση δεδομένων
    public void update(String username, String password) {
        if (selectedRow != -1) {

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String upd_query = "update phones set ph_br_code=?,ph_number=?" +
                        " where ph_br_code=? and ph_number=?";

                try {
                    ph_br_code = comboBoxBID.getSelectedItem().toString();
                    ph_number = textFieldPN.getText();

                    if (ph_br_code.isEmpty() || ph_number.isEmpty()) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, ph_br_code);
                update.setString(2, ph_number);
                update.setString(3, ph_br_code_1);
                update.setString(4, ph_number_1);
                update.executeUpdate();

                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Phones.this.dispose();
                        new Phones(username, password);
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
            ph_br_code = comboBoxBID.getSelectedItem().toString();
            ph_number = textFieldPN.getText();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from phones where ph_br_code=?" +
                        "and ph_number=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, ph_br_code);
                delete_1.setString(2, ph_number);
                delete_1.executeUpdate();
                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Phones.this.dispose();
                        new Phones(username, password);
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
                ph_br_code = comboBoxBID.getSelectedItem().toString();
                ph_number = textFieldPN.getText();

                if (ph_br_code.isEmpty() || ph_number.isEmpty()) {
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

                String query = "insert into phones values" +
                        "(?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, ph_br_code);
                insert_1.setString(2, ph_number);

                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Phones.this.dispose();
                        new Phones(username, password);
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
