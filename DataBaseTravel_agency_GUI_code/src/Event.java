import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Event extends JFrame {
    private JTable insert;
    private JTextField textFieldEVS;
    private JTextField textFieldEVE;
    private JTextField textFieldDS;
    private JPanel Event;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JComboBox<Object> comboBoxTID;
    private String ev_tr_id;
    private String ev_start;
    private String ev_end;
    private String ev_descr;
    private String[] columns_name = {"Trip id", "Start date", "End date", "Description"};
    private String ev_tr_id_1;
    private String ev_start_1;
    private int selectedRow = -1;

    //Constructor που φτιάχνει το παράθυρο Event
    public Event(String username, String password) {
        setTitle(username);
        setContentPane(Event);
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
            String query = "SELECT * FROM event";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                ev_tr_id = String.valueOf(result.getObject("ev_tr_id"));
                Timestamp date = result.getTimestamp("ev_start");
                LocalDateTime time = date.toLocalDateTime();
                ev_start = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                Timestamp date_1 = result.getTimestamp("ev_end");
                LocalDateTime time_1 = date_1.toLocalDateTime();
                ev_end = time_1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
                ev_descr = String.valueOf(result.getObject("ev_descr"));
                Object[] add = {ev_tr_id, ev_start, ev_end, ev_descr};
                temp.addRow(add);
            }

            query = "SELECT tr_id from trip";
            result = print.executeQuery(query);

            while (result.next()) {
                comboBoxTID.addItem(result.getObject("tr_id"));
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
                while (i < comboBoxTID.getItemCount()) {
                    if (comboBoxTID.getItemAt(i).toString().equals(temp.getValueAt(selectedRow, 0))) {
                        comboBoxTID.setSelectedIndex(i);
                    }
                    i++;
                }
                textFieldEVS.setText(temp.getValueAt(selectedRow, 1).toString());
                textFieldEVE.setText(temp.getValueAt(selectedRow, 2).toString());
                textFieldDS.setText(temp.getValueAt(selectedRow, 3).toString());
                ev_tr_id_1 = comboBoxTID.getSelectedItem().toString();
                ev_start_1 = textFieldEVS.getText();
            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxTID.setSelectedIndex(0);
                textFieldEVS.setText("");
                textFieldEVE.setText("");
                textFieldDS.setText("");
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
                        Event.this.dispose();
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
            ev_tr_id = comboBoxTID.getSelectedItem().toString();
            ev_start = textFieldEVS.getText();

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String del_query = "delete from event where ev_tr_id=? and ev_start=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, ev_tr_id);
                delete_1.setString(2, ev_start);
                delete_1.executeUpdate();
                delete_1.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Event.this.dispose();
                        new Event(username, password);
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

                String upd_query = "update event set ev_tr_id=?,ev_start=?," +
                        "ev_end=?, ev_descr=? where ev_tr_id=? and ev_start=?";

                try {
                    ev_tr_id = comboBoxTID.getSelectedItem().toString();
                    ev_start = textFieldEVS.getText();
                    ev_end = textFieldEVE.getText();
                    ev_descr = textFieldDS.getText();

                    if (ev_tr_id.isEmpty() || ev_start.isEmpty() || ev_end.isEmpty() || ev_descr.isEmpty()) {
                        throw new Exception();
                    }
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, "Give Data to all fields and correct types", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;

                }
                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, ev_tr_id);
                update.setString(2, ev_start);
                update.setString(3, ev_end);
                update.setString(4, ev_descr);
                update.setString(5, ev_tr_id_1);
                update.setString(6, ev_start_1);

                update.executeUpdate();
                update.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Event.this.dispose();
                        new Event(username, password);
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
                ev_tr_id = comboBoxTID.getSelectedItem().toString();
                ev_start = textFieldEVS.getText();
                ev_end = textFieldEVE.getText();
                ev_descr = textFieldDS.getText();

                if (ev_tr_id.isEmpty() || ev_start.isEmpty() || ev_end.isEmpty() || ev_descr.isEmpty()) {
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

                String query = "insert into event values" +
                        "(?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, ev_tr_id);
                insert_1.setString(2, ev_start);
                insert_1.setString(3, ev_end);
                insert_1.setString(4, ev_descr);

                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Event.this.dispose();
                        new Event(username, password);
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
