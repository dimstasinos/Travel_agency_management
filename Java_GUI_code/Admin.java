import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Admin extends JFrame {
    private JTable insert;
    private JComboBox comboBoxTY;
    private JTextField textFieldDI;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Admin;
    private JComboBox<Object> comboBoxAT;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private String adm_AT;
    private String adm_type;
    private String adm_diploma;
    private int selectedRow = -1;
    private String adm_AT_1;

    //Constructor που φτιάχνει το παράθυρο Admin
    public Admin(String username, String password) {
        setTitle(username);
        setContentPane(Admin);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        DefaultTableModel temp = new DefaultTableModel();
        String[] columns_name = {"Admin AT", "Type", "Diploma"};
        temp.setColumnIdentifiers(columns_name);

        //Τοποθέτηση στοιχείων από τη βάση δεδομένων στον πίνακα
        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, password)) {

            Statement print = connect.createStatement();
            String query = "SELECT * FROM admin";
            ResultSet result = print.executeQuery(query);


            while (result.next()) {
                adm_AT = String.valueOf(result.getObject("adm_AT"));
                adm_type = String.valueOf(result.getObject("adm_type"));
                adm_diploma = String.valueOf(result.getObject("adm_diploma"));
                Object[] add = {adm_AT, adm_type, adm_diploma};
                temp.addRow(add);
            }

            query = "SELECT wrk_AT from worker";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxAT.addItem(result.getObject("wrk_AT"));
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
                while (i < comboBoxAT.getItemCount()) {
                    if (comboBoxAT.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 0))) {
                        comboBoxAT.setSelectedIndex(i);
                    }
                    i++;
                }
                textFieldDI.setText(temp.getValueAt(selectedRow, 2).toString());
                if (temp.getValueAt(selectedRow, 1).toString().equals("LOGISTICS")) {
                    comboBoxTY.setSelectedIndex(0);
                } else if (temp.getValueAt(selectedRow, 1).toString().equals("ADMINISTRATIVE")) {
                    comboBoxTY.setSelectedIndex(1);
                } else {
                    comboBoxTY.setSelectedIndex(2);
                }
                adm_AT_1 = comboBoxAT.getSelectedItem().toString();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxAT.setSelectedIndex(0);
                textFieldDI.setText("");
                comboBoxTY.setSelectedIndex(0);
                textFieldFN.setText("");
                textFieldLN.setText("");
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
                        Admin.this.dispose();
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
        comboBoxAT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {

                    String query = "SELECT wrk_name FROM worker where wrk_AT=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxAT.getSelectedItem().toString());
                    ResultSet result = print_1.executeQuery();
                    result.next();
                    textFieldFN.setText(result.getString("wrk_name"));


                    query = "SELECT wrk_lname FROM worker where wrk_AT=?";
                    print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxAT.getSelectedItem().toString());
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

    //Μέθοδος για εισαγωγή νέων δεδομένων στη βάση δεδομένων
    public void insert(String username, String password) {
        if (selectedRow == -1) {
            try {
                adm_AT = comboBoxAT.getSelectedItem().toString();
                adm_type = (String) comboBoxTY.getSelectedItem();
                adm_diploma = textFieldDI.getText();

                if (adm_AT.isEmpty() || adm_type.isEmpty() || adm_diploma.isEmpty()) {
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

                String query = "insert into admin values" +
                        "(?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, adm_AT);
                insert_1.setString(2, adm_type);
                insert_1.setString(3, adm_diploma);
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Admin.this.dispose();
                        new Admin(username, password);
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

                String upd_query = "update admin set adm_AT=?, adm_type=?,adm_diploma=?" +
                        " where adm_AT=?";
                try {
                    adm_AT = comboBoxAT.getSelectedItem().toString();
                    adm_type = (String) comboBoxTY.getSelectedItem();
                    adm_diploma = textFieldDI.getText();

                    if (adm_AT.isEmpty() || adm_type.isEmpty() || adm_diploma.isEmpty()) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, adm_AT);
                update.setString(2, adm_type);
                update.setString(3, adm_diploma);
                update.setString(4, adm_AT_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Admin.this.dispose();
                        new Admin(username, password);
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

            adm_AT = comboBoxAT.getSelectedItem().toString();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "select adm_type from admin where adm_AT=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, adm_AT);
                ResultSet result = delete_1.executeQuery();
                result.next();
                String check = result.getObject("adm_type").toString();
                if (check.equals("ADMINISTRATIVE")) {
                    throw new SQLException("Can't delete an admin");
                }

                del_query = "delete from admin where adm_AT=?";
                delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, adm_AT);
                delete_1.executeUpdate();
                delete_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Admin.this.dispose();
                        new Admin(username, password);
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
