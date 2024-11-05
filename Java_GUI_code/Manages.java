import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Manages extends JFrame {
    private JTable insert;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Manages;
    private JComboBox<Object> comboBoxAT;
    private JComboBox<Object> comboBoxID;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private String mng_adm_AT;
    private String mng_br_code;
    private String[] columns_name = {"Admin AT", "Branch id"};
    private String mng_adm_AT_1;
    private String mng_br_code_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Manages
    public Manages(String username, String password) {
        setTitle(username);
        setContentPane(Manages);
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
            String query = "SELECT * FROM manages";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                mng_adm_AT = String.valueOf(result.getObject("mng_adm_AT"));
                mng_br_code = String.valueOf(result.getObject("mng_br_code"));
                Object[] add = {mng_adm_AT, mng_br_code};
                temp.addRow(add);
            }

            query = "SELECT adm_AT from admin";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxAT.addItem(result.getObject("adm_AT"));
            }

            query = "SELECT br_code from branch";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxID.addItem(result.getObject("br_code"));
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
                i = 0;
                while (i < comboBoxID.getItemCount()) {
                    if (comboBoxID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 1))) {
                        comboBoxID.setSelectedIndex(i);
                    }
                    i++;
                }

                mng_adm_AT_1 = comboBoxAT.getSelectedItem().toString();
                mng_br_code_1 = comboBoxID.getSelectedItem().toString();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxAT.setSelectedIndex(0);
                comboBoxAT.setSelectedIndex(0);
                textFieldLN.setText("");
                textFieldFN.setText("");
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
                        Manages.this.dispose();
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
                mng_adm_AT = comboBoxAT.getSelectedItem().toString();
                mng_br_code = comboBoxID.getSelectedItem().toString();

                if (mng_adm_AT.isEmpty() || mng_br_code.isEmpty()) {
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

                String query = "insert into manages values" +
                        "(?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, mng_adm_AT);
                insert_1.setString(2, mng_br_code);

                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Manages.this.dispose();
                        new Manages(username, password);
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

                String upd_query = "update manages set mng_adm_AT=?,mng_br_code=?" +
                        " where mng_adm_AT=? and mng_br_code=?";


                try {
                    mng_adm_AT = comboBoxAT.getSelectedItem().toString();
                    mng_br_code = comboBoxID.getSelectedItem().toString();

                    if (mng_adm_AT.isEmpty() || mng_br_code.isEmpty()) {
                        throw new Exception();
                    }

                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, mng_adm_AT);
                update.setString(2, mng_br_code);
                update.setString(5, mng_adm_AT_1);
                update.setString(6, mng_br_code_1);
                update.executeUpdate();

                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Manages.this.dispose();
                        new Manages(username, password);
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
            mng_adm_AT = comboBoxAT.getSelectedItem().toString();
            mng_br_code = comboBoxID.getSelectedItem().toString();


            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "select adm_type from admin where adm_AT=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, mng_adm_AT);
                ResultSet result = delete_1.executeQuery();
                result.next();
                String check = result.getObject("adm_type").toString();
                if (check.equals("ADMINISTRATIVE")) {
                    throw new SQLException("Can't delete an admin");
                }


                del_query = "delete from manages where mng_adm_AT=? and mng_adm_AT=?";
                delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, mng_adm_AT);
                delete_1.setString(2, mng_adm_AT);
                delete_1.executeUpdate();
                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Manages.this.dispose();
                        new Manages(username, password);
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
