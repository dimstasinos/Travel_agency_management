import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Branch extends JFrame {
    private JTable insert;
    private JTextField textFieldBID;
    private JTextField textFieldAD;
    private JTextField textFieldADN;
    private JTextField textFieldCT;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JPanel Branch;
    private int br_code;
    private String br_street;
    private int br_num;
    private String br_city;
    private String[] columns_name = {"ID", "Address", "Address number", "City"};
    private String br_code_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Branch
    public Branch(String username, String password) {
        setTitle(username);
        setContentPane(Branch);
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
            String query = "SELECT * FROM branch";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                br_code = Integer.parseInt(String.valueOf(result.getObject("br_code")));
                br_street = String.valueOf(result.getObject("br_street"));
                br_num = Integer.parseInt(String.valueOf(result.getObject("br_num")));
                br_city = String.valueOf(result.getObject("br_city"));
                Object[] add = {br_code, br_street, br_num, br_city};
                temp.addRow(add);
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
                textFieldBID.setText(temp.getValueAt(selectedRow, 0).toString());
                textFieldAD.setText(temp.getValueAt(selectedRow, 1).toString());
                textFieldADN.setText(temp.getValueAt(selectedRow, 2).toString());
                textFieldCT.setText(temp.getValueAt(selectedRow, 3).toString());
                br_code_1 = textFieldBID.getText();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldBID.setText("");
                textFieldAD.setText("");
                textFieldADN.setText("");
                textFieldCT.setText("");
                selectedRow = -1;
            }
        });

        //Αλλαγή στο προηγούμενο παράθυρο MainMenu
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Branch.this.dispose();
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
                textFieldBID.setText("");
                br_street = textFieldAD.getText();
                br_num = Integer.parseInt(textFieldADN.getText());
                br_city = textFieldCT.getText();

                if (br_street.isEmpty() || br_num < 1 || br_city.isEmpty()) {
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

                String query = "insert into branch values" +
                        "(NULL,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, br_street);
                insert_1.setString(2, String.valueOf(br_num));
                insert_1.setString(3, br_city);
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Branch.this.dispose();
                        new Branch(username, password);
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

                String upd_query = "update branch set br_code=?, br_street=?,br_num=?,br_city=?" +
                        " where br_code=?";
                try {
                    br_code = Integer.parseInt(textFieldBID.getText());
                    br_street = textFieldAD.getText();
                    br_num = Integer.parseInt(textFieldADN.getText());
                    br_city = textFieldCT.getText();

                    if (br_code < 0 || br_street.isEmpty() || br_num < 1 || br_city.isEmpty()) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, String.valueOf(br_code));
                update.setString(2, br_street);
                update.setString(3, String.valueOf(br_num));
                update.setString(4, br_city);
                update.setString(5, br_code_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Branch.this.dispose();
                        new Branch(username, password);
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

            br_code = Integer.parseInt(textFieldBID.getText());

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from branch where br_code=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, String.valueOf(br_code));
                delete_1.executeUpdate();
                delete_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Branch.this.dispose();
                        new Branch(username, password);
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
