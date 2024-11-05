import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Destination extends JFrame {
    private JTable insert;
    private JTextField textFieldDID;
    private JTextField textFieldNAME;
    private JTextField textFieldDES;
    private JComboBox comboBoxType;
    private JTextField textFieldLO;
    private JPanel Destination;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JTextField textFieldLA;
    private String dst_id;
    private String dst_name;
    private String dst_descr;
    private String dst_rtype;
    private String dst_language;
    private int dst_location;
    private String[] columns_name = {"ID", "Name", "Description", "Type", "Language", "Location"};
    private int selectedRow = -1;
    private String dst_id_1;

    //Constructor που φτιάχνει το παράθυρο Destination
    public Destination(String username, String password) {
        setTitle(username);
        setContentPane(Destination);
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
            String query = "SELECT * FROM destination";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                dst_id = String.valueOf(result.getObject("dst_id"));
                dst_name = String.valueOf(result.getObject("dst_name"));
                dst_descr = String.valueOf(result.getObject("dst_descr"));
                dst_rtype = String.valueOf(result.getObject("dst_rtype"));
                dst_language = String.valueOf(result.getObject("dst_language"));
                dst_location = Integer.parseInt(String.valueOf(result.getObject("dst_location")));
                Object[] add = {dst_id, dst_name, dst_descr, dst_rtype, dst_language, dst_location};
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
                textFieldDID.setText(temp.getValueAt(selectedRow, 0).toString());
                textFieldNAME.setText(temp.getValueAt(selectedRow, 1).toString());
                textFieldDES.setText(temp.getValueAt(selectedRow, 2).toString());
                if (temp.getValueAt(selectedRow, 3).toString().equals("LOCAL")) {
                    comboBoxType.setSelectedIndex(0);
                } else {
                    comboBoxType.setSelectedIndex(1);
                }
                textFieldLA.setText(temp.getValueAt(selectedRow, 4).toString());
                textFieldLO.setText(temp.getValueAt(selectedRow, 5).toString());
                dst_id_1 = textFieldDID.getText();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textFieldDID.setText("");
                textFieldNAME.setText("");
                textFieldDES.setText("");
                comboBoxType.setSelectedIndex(0);
                textFieldLA.setText("");
                textFieldLO.setText("");
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
                        Destination.this.dispose();
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

    //Μέθοδος για τη διαγραφή ενός στοιχείου από τη βάση δεδομένων
    public void delete(String username, String password) {
        if (selectedRow != -1) {

            dst_id = textFieldDID.getText();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from destination where dst_id=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, dst_id);
                delete_1.executeUpdate();
                delete_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Destination.this.dispose();
                        new Destination(username, password);
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

                String upd_query = "update destination set dst_id=?, dst_name=?,dst_descr=?,dst_rtype=?,dst_language=?," +
                        "dst_location=? where dst_id=?";
                try {
                    dst_id = textFieldDID.getText();
                    dst_name = textFieldNAME.getText();
                    dst_descr = textFieldDES.getText();
                    dst_rtype = (String) comboBoxType.getSelectedItem();
                    dst_language = textFieldLA.getText();
                    dst_location = Integer.parseInt(textFieldLO.getText());

                    if (dst_name.isEmpty() || dst_descr.isEmpty() || dst_rtype.isEmpty() || dst_language.isEmpty() || dst_location < 0) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, dst_id);
                update.setString(2, dst_name);
                update.setString(3, dst_descr);
                update.setString(4, dst_rtype);
                update.setString(5, dst_language);
                update.setString(6, String.valueOf(dst_location));
                update.setString(7, dst_id_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Destination.this.dispose();
                        new Destination(username, password);
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
                textFieldDID.setText("");
                dst_name = textFieldNAME.getText();
                dst_descr = textFieldDES.getText();
                dst_rtype = (String) comboBoxType.getSelectedItem();
                dst_language = textFieldLA.getText();
                dst_location = Integer.parseInt(textFieldLO.getText());

                if (dst_name.isEmpty() || dst_descr.isEmpty() || dst_rtype.isEmpty() || dst_language.isEmpty() || dst_location < 0) {
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

                String query = "insert into destination values" +
                        "(NULL,?,?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, dst_name);
                insert_1.setString(2, dst_descr);
                insert_1.setString(3, dst_rtype);
                insert_1.setString(4, dst_language);
                insert_1.setString(5, String.valueOf(dst_location));
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Destination.this.dispose();
                        new Destination(username, password);
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
