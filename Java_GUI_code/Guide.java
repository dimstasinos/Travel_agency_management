import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Guide extends JFrame {
    private JTable insert;
    private JTextField textFieldGCV;
    private JButton backButton;
    private JButton insertButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Guide;
    private JButton updateButton;
    private JComboBox<Object> comboBoxGAT;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private String gui_at;
    private String gui_cv;
    private String[] columns_name = {"Guide AT", "Guide CV"};
    private String gui_at_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Guide
    public Guide(String username, String password) {
        setTitle(username);
        setContentPane(Guide);
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
            String query = "SELECT * FROM guide";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                gui_at = String.valueOf(result.getObject("gui_AT"));
                gui_cv = String.valueOf(result.getObject("gui_cv"));

                Object[] add = {gui_at, gui_cv};
                temp.addRow(add);
            }

            query = "SELECT wrk_AT from worker";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxGAT.addItem(result.getObject("wrk_AT"));
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
                while (i < comboBoxGAT.getItemCount()) {
                    if (comboBoxGAT.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 0))) {
                        comboBoxGAT.setSelectedIndex(i);
                    }
                    i++;
                }
                textFieldGCV.setText(temp.getValueAt(selectedRow, 1).toString());
                gui_at_1 = comboBoxGAT.getSelectedItem().toString();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxGAT.setSelectedIndex(0);
                textFieldGCV.setText("");
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
                        Guide.this.dispose();
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

                    String query = "SELECT wrk_name FROM worker where wrk_AT=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxGAT.getSelectedItem().toString());
                    ResultSet result = print_1.executeQuery();
                    result.next();
                    textFieldFN.setText(result.getString("wrk_name"));


                    query = "SELECT wrk_lname FROM worker where wrk_AT=?";
                    print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxGAT.getSelectedItem().toString());
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

    //Μέθοδος για τη διαγραφή ενός στοιχείου από τη βάση δεδομένων
    public void delete(String username, String password) {
        if (selectedRow != -1) {
            gui_at = comboBoxGAT.getSelectedItem().toString();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from guide where gui_at=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, gui_at);
                delete_1.executeUpdate();
                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Guide.this.dispose();
                        new Guide(username, password);
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

    //Μέθοδος για αλλαγή των δεδομένων στη βάση δεδομένων
    public void update(String username, String password) {
        if (selectedRow != -1) {
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String upd_query = "update guide set gui_AT=?,gui_cv=? " +
                        "where gui_AT=?";


                try {
                    gui_cv = textFieldGCV.getText();
                    gui_at = comboBoxGAT.getSelectedItem().toString();

                    if (gui_cv.isEmpty() || gui_at.isEmpty()) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, gui_at);
                update.setString(2, gui_cv);
                update.setString(3, gui_at_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Guide.this.dispose();
                        new Guide(username, password);
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
                gui_at = comboBoxGAT.getSelectedItem().toString();
                gui_cv = textFieldGCV.getText();

                if (gui_at.isEmpty() || gui_cv.isEmpty()) {
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

                String query = "insert into guide values" +
                        "(?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, gui_at);
                insert_1.setString(2, gui_cv);
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Guide.this.dispose();
                        new Guide(username, password);
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
