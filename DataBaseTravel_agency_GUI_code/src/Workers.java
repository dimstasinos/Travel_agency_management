import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Workers extends JFrame {
    private JPanel workers;
    private JLabel Workers;
    private JTable insert;
    private JButton backButton;
    private JScrollPane scroll;
    private JButton insertButton;
    private JTextField textFieldAT;
    private JTextField textFieldLN;
    private JTextField textFieldFN;
    private JTextField textFieldSA;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton clearButton;
    private JComboBox<Object> comboBoxBID;
    int selectedRow = -1;
    private String wrk_AT;
    private String wrk_lname;
    private String wrk_name;
    private float wrk_salary;
    private String wrk_br_code;
    private String[] columns_name = {"AT", "Last Name", "First Name", "Salary", "Branch"};
    private String AT;

    //Constructor που φτιάχνει το παράθυρο Workers
    public Workers(String username, String password) {

        setContentPane(workers);
        setTitle(username);
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
            String query = "SELECT * FROM worker";
            ResultSet result = print.executeQuery(query);


            while (result.next()) {
                wrk_AT = String.valueOf(result.getObject("wrk_AT"));
                wrk_lname = String.valueOf(result.getObject("wrk_lname"));
                wrk_name = String.valueOf(result.getObject("wrk_name"));
                wrk_salary = Float.parseFloat(String.valueOf(result.getObject("wrk_salary")));
                wrk_br_code = String.valueOf(result.getObject("wrk_br_code"));
                Object[] add = {wrk_AT, wrk_lname, wrk_name, wrk_salary, wrk_br_code};
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


        //Αλλαγή στο προηγούμενο παράθυρο Database
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Workers.this.dispose();
                        new Database(username, password);
                    }
                });
            }
        });

        setVisible(true);

        //Εισαγωγή δεδομένων στα πεδία του παραθύρου
        insert.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                DefaultTableModel temp = (DefaultTableModel) insert.getModel();
                selectedRow = insert.getSelectedRow();
                textFieldAT.setText(temp.getValueAt(selectedRow, 0).toString());
                textFieldLN.setText(temp.getValueAt(selectedRow, 1).toString());
                textFieldFN.setText(temp.getValueAt(selectedRow, 2).toString());
                textFieldSA.setText(temp.getValueAt(selectedRow, 3).toString());
                int i = 0;
                while (i < comboBoxBID.getItemCount()) {
                    if (comboBoxBID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 4))) {
                        comboBoxBID.setSelectedIndex(i);
                    }
                    i++;
                }
                AT = textFieldAT.getText();
            }
        });

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insert(username, password);
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldAT.setText("");
                comboBoxBID.setSelectedIndex(0);
                textFieldFN.setText("");
                textFieldLN.setText("");
                textFieldSA.setText("");
                selectedRow = -1;
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

                String upd_query = "update worker set wrk_AT=?,wrk_lname=?," +
                        "wrk_name=?, wrk_salary=?,wrk_br_code=? " +
                        "where wrk_AT=?";
                try {
                    wrk_AT = textFieldAT.getText();
                    wrk_br_code = comboBoxBID.getSelectedItem().toString();
                    wrk_name = textFieldFN.getText();
                    wrk_lname = textFieldLN.getText();
                    wrk_salary = Float.parseFloat(textFieldSA.getText());

                    if (wrk_AT.isEmpty() || wrk_lname.isEmpty() || wrk_name.isEmpty() || wrk_salary < 0 || wrk_br_code.isEmpty()) {
                        throw new Exception();
                    }

                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, wrk_AT);
                update.setString(2, wrk_lname);
                update.setString(3, wrk_name);
                update.setString(4, String.valueOf(wrk_salary));
                update.setString(5, String.valueOf(wrk_br_code));
                update.setString(6, AT);
                update.executeUpdate();

                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Workers.this.dispose();
                        new Workers(username, password);
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
            wrk_AT = textFieldAT.getText();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {


                CallableStatement check = connect.prepareCall("call checkadmin(?,?)");
                check.setString(1, textFieldFN.getText());
                check.setString(2, textFieldLN.getText());
                check.executeUpdate();

                check.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Workers.this.dispose();
                        new Workers(username, password);
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
                wrk_AT = textFieldAT.getText();
                wrk_lname = textFieldLN.getText();
                wrk_name = textFieldFN.getText();
                wrk_salary = Float.parseFloat(textFieldSA.getText());
                wrk_br_code = comboBoxBID.getSelectedItem().toString();

                if (wrk_AT.isEmpty() || wrk_lname.isEmpty() || wrk_name.isEmpty() || wrk_salary < 0 || wrk_br_code.isEmpty()) {
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

                String query = "insert into worker values" +
                        "(?,?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, wrk_AT);
                insert_1.setString(2, wrk_lname);
                insert_1.setString(3, wrk_name);
                insert_1.setString(4, String.valueOf(wrk_salary));
                insert_1.setString(5, String.valueOf(wrk_br_code));

                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Workers.this.dispose();
                        new Workers(username, password);
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
