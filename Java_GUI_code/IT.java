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

public class IT extends JFrame {
    private JTable insert;
    private JTextField textFieldPAS;
    private JTextField textFieldSD;
    private JTextField textFieldED;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel It;
    private JComboBox<Object> comboBoxAT;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private String IT_AT;
    private String password;
    private String start_date;
    private String end_date;
    private String[] columns_name = {"IT AT", "Password", "Start Date", "End Date"};
    private String IT_AT_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο IT
    public IT(String username, String passwordb) {
        setTitle(username);
        setContentPane(It);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        DefaultTableModel temp = new DefaultTableModel();
        temp.setColumnIdentifiers(columns_name);

        //Τοποθέτηση στοιχείων από τη βάση δεδομένων στον πίνακα
        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, passwordb)) {

            Statement print = connect.createStatement();
            String query = "SELECT * FROM IT";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                IT_AT = String.valueOf(result.getObject("IT_AT"));
                password = String.valueOf(result.getObject("password"));
                Date date = result.getDate("start_date");
                LocalDate date_1_2 = date.toLocalDate();
                start_date = date_1_2.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                if (result.getObject("end_date") != null) {
                    Date date_1 = result.getDate("end_date");
                    LocalDate date_1_1 = date_1.toLocalDate();
                    end_date = date_1_1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                }
                Object[] add = {IT_AT, password, start_date, end_date};
                temp.addRow(add);
                end_date = null;
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
                textFieldPAS.setText(temp.getValueAt(selectedRow, 1).toString());
                textFieldSD.setText(temp.getValueAt(selectedRow, 2).toString());
                if (temp.getValueAt(selectedRow, 3) != null) {
                    textFieldED.setText(temp.getValueAt(selectedRow, 3).toString());
                } else {
                    textFieldED.setText("");
                }
                IT_AT_1 = comboBoxAT.getSelectedItem().toString();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxAT.setSelectedIndex(0);
                textFieldPAS.setText("");
                textFieldSD.setText("");
                textFieldED.setText("");
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
                        IT.this.dispose();
                        new Database(username, passwordb);
                    }
                });
            }
        });


        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insert(username, passwordb);
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update(username, passwordb);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delete(username, passwordb);
            }
        });
        comboBoxAT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, passwordb)) {

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
    public void insert(String username, String passwordb) {
        if (selectedRow == -1) {

            try {
                IT_AT = comboBoxAT.getSelectedItem().toString();
                password = textFieldPAS.getText();
                start_date = textFieldSD.getText();
                end_date = textFieldED.getText();

                if (IT_AT.isEmpty()) {
                    throw new Exception();
                }
            } catch (Exception a) {
                JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;

            }
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, passwordb)) {


                if (!textFieldPAS.getText().isEmpty()) {
                    if (!textFieldED.getText().isEmpty()) {
                        String query = "insert into IT values" +
                                "(?,?,?,?)";

                        PreparedStatement insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, IT_AT);
                        insert_1.setString(2, password);
                        insert_1.setString(3, start_date);
                        insert_1.setString(4, end_date);
                        insert_1.executeUpdate();
                        insert_1.close();

                    } else {
                        String query = "insert into IT (IT_AT,password,start_date)" +
                                "values (?,?,curdate())";

                        PreparedStatement insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, IT_AT);
                        insert_1.setString(2, password);
                        insert_1.executeUpdate();

                        query = "select wrk_lname FROM worker where wrk_AT like ?";
                        insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, IT_AT);
                        ResultSet result = insert_1.executeQuery();
                        result.next();
                        String wrk_dbname = (String) result.getObject("wrk_lname");

                        query = "CREATE USER ?@'localhost' IDENTIFIED BY ?";
                        insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, wrk_dbname);
                        insert_1.setString(2, password);
                        insert_1.executeUpdate();

                        query = "grant all privileges on *.* to ?@'localhost' with grant option";
                        insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, wrk_dbname);
                        insert_1.executeUpdate();

                        query = "FLUSH PRIVILEGES";
                        Statement print = connect.createStatement();
                        print.executeUpdate(query);


                        insert_1.close();
                    }
                } else {
                    if (!textFieldED.getText().isEmpty()) {
                        String query = "insert into IT values" +
                                "(?,DEFAULT,?,?)";

                        PreparedStatement insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, IT_AT);
                        insert_1.setString(2, start_date);
                        insert_1.setString(3, end_date);
                        insert_1.executeUpdate();
                        insert_1.close();


                    } else {
                        String query = "insert into IT (IT_AT,password,start_date)" +
                                "values (?,DEFAULT,curdate())";

                        PreparedStatement insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, IT_AT);
                        insert_1.executeUpdate();
                        query = "select wrk_lname FROM worker where wrk_AT like ?";
                        insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, IT_AT);
                        ResultSet result = insert_1.executeQuery();
                        result.next();
                        String wrk_dbname = (String) result.getObject("wrk_lname");

                        query = "CREATE USER ?@'localhost' IDENTIFIED BY ?";
                        insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, wrk_dbname);
                        insert_1.setString(2, "password");
                        insert_1.executeUpdate();

                        query = "grant all privileges on *.* to ?@'localhost' with grant option";
                        insert_1 = connect.prepareStatement(query);
                        insert_1.setString(1, wrk_dbname);
                        insert_1.executeUpdate();

                        query = "FLUSH PRIVILEGES";
                        Statement print = connect.createStatement();
                        print.executeUpdate(query);

                        insert_1.close();
                    }
                }

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        IT.this.dispose();
                        new IT(username, passwordb);
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
    public void update(String username, String passwordb) {
        if (selectedRow != -1) {
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, passwordb)) {

                try {
                    IT_AT = comboBoxAT.getSelectedItem().toString();
                    password = textFieldPAS.getText();
                    start_date = textFieldSD.getText();
                    end_date = textFieldED.getText();

                    if (IT_AT.isEmpty() || start_date.isEmpty()) {
                        throw new Exception();
                    }

                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }
                if (textFieldED.getText().isEmpty()) {
                    end_date = null;
                }

                if (!textFieldPAS.getText().isEmpty()) {

                    String upd_query;
                    PreparedStatement update;

                    if (end_date != null) {
                        upd_query = "update IT set IT_AT=?,password=?,start_date=?," +
                                "end_date=? where IT_AT=?";

                        update = connect.prepareStatement(upd_query);
                        update.setString(1, IT_AT);
                        update.setString(2, password);
                        update.setString(3, start_date);
                        update.setString(4, end_date);
                        update.setString(5, IT_AT_1);
                        update.executeUpdate();
                    } else {

                        upd_query = "update IT set IT_AT=?,password=?,start_date=?," +
                                "end_date=NULL where IT_AT=?";
                        update = connect.prepareStatement(upd_query);
                        update.setString(1, IT_AT);
                        update.setString(2, password);
                        update.setString(3, start_date);
                        update.setString(4, IT_AT_1);
                        update.executeUpdate();

                    }


                    if (textFieldED.getText().isEmpty()) {
                        upd_query = "select wrk_lname FROM worker where wrk_AT like ?";
                        update = connect.prepareStatement(upd_query);
                        update.setString(1, IT_AT);
                        ResultSet result = update.executeQuery();
                        result.next();
                        String wrk_dbname = (String) result.getObject("wrk_lname");


                        upd_query = "alter user ?@localhost identified by ?";
                        update = connect.prepareStatement(upd_query);
                        update.setString(1, wrk_dbname);
                        update.setString(2, password);
                        update.executeUpdate();
                    }

                    update.close();
                } else {
                    String upd_query = "update IT set IT_AT=?,start_date=?," +
                            "end_date=? where IT_AT=?";
                    PreparedStatement update = connect.prepareStatement(upd_query);
                    update.setString(1, IT_AT);
                    update.setString(2, start_date);
                    update.setString(3, end_date);
                    update.setString(4, IT_AT_1);
                    update.executeUpdate();

                    if (textFieldED.getText().isEmpty()) {
                        upd_query = "select wrk_lname FROM worker where wrk_AT like ?";
                        update = connect.prepareStatement(upd_query);
                        update.setString(1, IT_AT);
                        ResultSet result = update.executeQuery();
                        result.next();
                        String wrk_dbname = (String) result.getObject("wrk_lname");


                        upd_query = "alter user ?@localhost identified by ?";
                        update = connect.prepareStatement(upd_query);
                        update.setString(1, wrk_dbname);
                        update.setString(2, password);
                        update.executeUpdate();
                    }
                    update.close();
                }
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        IT.this.dispose();
                        new IT(username, passwordb);
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
    public void delete(String username, String passwordb) {
        if (selectedRow != -1) {

            IT_AT = comboBoxAT.getSelectedItem().toString();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, passwordb)) {

                String del_query = "delete from IT where IT_AT=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, IT_AT);
                delete_1.executeUpdate();

                if (!textFieldED.getText().isEmpty()) {
                    del_query = "select wrk_lname FROM worker where wrk_AT like ?";
                    delete_1 = connect.prepareStatement(del_query);
                    delete_1.setString(1, IT_AT);
                    ResultSet result = delete_1.executeQuery();
                    result.next();
                    String wrk_dbname = (String) result.getObject("wrk_lname");

                    del_query = "drop user ?@localhost";
                    delete_1 = connect.prepareStatement(del_query);
                    delete_1.setString(1, wrk_dbname);
                    delete_1.executeUpdate();
                }

                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        IT.this.dispose();
                        new IT(username, passwordb);
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
