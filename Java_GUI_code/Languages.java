import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Languages extends JFrame {
    private JTable insert;
    private JTextField textFieldLA;
    private JPanel Languages;
    private JButton backButton;
    private JButton insertButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton updateButton;
    private JComboBox<Object> comboBoxAT;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private String lng_gui_AT;
    private String lng_language;
    private String[] columns_name = {"Guide AT", "Language"};
    private int selectedRow = -1;
    private String lng_gui_AT_1;
    private String lng_language_1;

    //Constructor που φτιάχνει το παράθυρο Languages
    public Languages(String username, String password) {
        setTitle(username);
        setContentPane(Languages);
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
            String query = "SELECT * FROM languages";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                lng_gui_AT = String.valueOf(result.getObject("lng_gui_AT"));
                lng_language = String.valueOf(result.getObject("lng_language"));

                Object[] add = {lng_gui_AT, lng_language};
                temp.addRow(add);
            }

            query = "SELECT gui_AT from guide";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxAT.addItem(result.getObject("gui_AT"));
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
                textFieldLA.setText(temp.getValueAt(selectedRow, 1).toString());
                lng_gui_AT_1 = comboBoxAT.getSelectedItem().toString();
                lng_language_1 = textFieldLA.getText();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxAT.setSelectedIndex(0);
                textFieldLA.setText("");
                selectedRow = -1;
                textFieldFN.setText("");
                textFieldLN.setText("");
            }
        });

        //Αλλαγή στο προηγούμενο παράθυρο Database
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Languages.this.dispose();
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

    //Μέθοδος για αλλαγή των δεδομένων στη βάση δεδομένων
    public void update(String username, String password) {
        if (selectedRow != -1) {

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String upd_query = "update languages set lng_gui_AT=?,lng_language=?" +
                        " where lng_gui_AT=? and lng_language=?";

                try {
                    lng_gui_AT = comboBoxAT.getSelectedItem().toString();
                    lng_language = textFieldLA.getText();

                    if (lng_gui_AT.isEmpty() || lng_language.isEmpty()) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, lng_gui_AT);
                update.setString(2, lng_language);
                update.setString(3, lng_gui_AT_1);
                update.setString(4, lng_language_1);
                update.executeUpdate();

                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Languages.this.dispose();
                        new Languages(username, password);
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
    public void delete(String username, String password) {
        if (selectedRow != -1) {
            lng_gui_AT = comboBoxAT.getSelectedItem().toString();
            lng_language = textFieldLA.getText();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from languages where lng_gui_AT=?" +
                        "and lng_language=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, lng_gui_AT);
                delete_1.setString(2, lng_language);
                delete_1.executeUpdate();
                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Languages.this.dispose();
                        new Languages(username, password);
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
    public void insert(String username, String password) {
        if (selectedRow == -1) {

            try {
                lng_gui_AT = comboBoxAT.getSelectedItem().toString();
                lng_language = textFieldLA.getText();

                if (lng_gui_AT.isEmpty() || lng_language.isEmpty()) {
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

                String query = "insert into languages values" +
                        "(?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, lng_gui_AT);
                insert_1.setString(2, lng_language);
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Languages.this.dispose();
                        new Languages(username, password);
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
