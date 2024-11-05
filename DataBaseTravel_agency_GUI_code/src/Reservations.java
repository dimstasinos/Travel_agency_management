import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;

public class Reservations extends JFrame {
    private JTable insert;
    private JTextField textFieldFN;
    private JTextField textFieldLN;
    private JComboBox comboBoxCH;
    private JPanel reservation;
    private JButton backButton;
    private JButton insertButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JComboBox<Object> comboBoxTID;
    private JComboBox<Integer> comboBoxSN;
    private String res_tr_id;
    private int res_seatnum;
    private String res_name;
    private String res_lname;
    private String res_isadult;
    private String[] columns_name = {"Trip id", "Seat number", "First Name", "Last Name", "Isadult"};
    private int selectedRow = -1;
    private String res_tr_id_1;
    private String res_seatnum_1;

    //Constructor που φτιάχνει το παράθυρο Reservations
    public Reservations(String username, String password) {

        setTitle(username);
        setContentPane(reservation);
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
            String query = "SELECT * FROM reservation";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                res_tr_id = String.valueOf(result.getObject("res_tr_id"));
                res_seatnum = Integer.parseInt(String.valueOf(result.getObject("res_seatnum")));
                res_name = String.valueOf(result.getObject("res_name"));
                res_lname = String.valueOf(result.getObject("res_lname"));
                res_isadult = String.valueOf(result.getObject("res_isadult"));
                Object[] add = {res_tr_id, res_seatnum, res_name, res_lname, res_isadult};
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
                comboBoxSN.setSelectedItem(temp.getValueAt(selectedRow, 1).toString());
                textFieldFN.setText(temp.getValueAt(selectedRow, 2).toString());
                if (temp.getValueAt(selectedRow, 4).toString().equals("ADULT")) {
                    comboBoxCH.setSelectedIndex(0);
                } else {
                    comboBoxCH.setSelectedIndex(1);
                }
                textFieldLN.setText(temp.getValueAt(selectedRow, 3).toString());
                res_tr_id_1 = comboBoxTID.getSelectedItem().toString();
                res_seatnum_1 = temp.getValueAt(selectedRow, 1).toString();

            }
        });

        //Εκκαθάριση των πεδίων του παραθύρου
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxTID.setSelectedIndex(0);
                comboBoxSN.setSelectedIndex(0);
                textFieldFN.setText("");
                comboBoxCH.setSelectedIndex(0);
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
                        Reservations.this.dispose();
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

        comboBoxTID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        username, password)) {
                    comboBoxSN.removeAllItems();
                    ArrayList<Integer> no_seat = new ArrayList<>();
                    String query = "SELECT tr_maxseats FROM trip where tr_id=?";
                    PreparedStatement print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxTID.getSelectedItem().toString());
                    ResultSet result = print_1.executeQuery();
                    result.next();
                    int seats = (int) result.getObject("tr_maxseats");

                    query = "SELECT res_seatnum FROM reservation where res_tr_id=?";
                    print_1 = connect.prepareStatement(query);
                    print_1.setString(1, comboBoxTID.getSelectedItem().toString());
                    result = print_1.executeQuery();

                    while (result.next()) {
                        no_seat.add(Integer.valueOf(result.getObject("res_seatnum").toString()));
                    }
                    boolean flag = false;

                    for (int i = 1; i <= seats; i++) {
                        for (Integer integer : no_seat) {
                            if (integer == i) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            comboBoxSN.addItem(i);
                        }
                        flag = false;
                    }

                    if (comboBoxSN.getItemCount() == 0) {
                        comboBoxSN.addItem(0);
                    }

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
                res_tr_id = comboBoxTID.getSelectedItem().toString();
                res_seatnum = Integer.parseInt(comboBoxSN.getSelectedItem().toString());
                res_isadult = (String) comboBoxCH.getSelectedItem();
                res_name = textFieldFN.getText();
                res_lname = textFieldLN.getText();

                if (res_tr_id.isEmpty() || res_name.isEmpty() || res_isadult.isEmpty() || res_lname.isEmpty()) {
                    throw new Exception("Give Data to all fields and correct types");
                }
                if (res_seatnum < 1) {
                    throw new Exception("No available seats");
                }

            } catch (Exception a) {
                JOptionPane.showMessageDialog(this, a, "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;

            }
            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {

                String query = "insert into reservation values " +
                        "(?,?,?,?,?)";

                PreparedStatement insert_1 = connect.prepareStatement(query);
                insert_1.setString(1, res_tr_id);
                insert_1.setString(2, String.valueOf(res_seatnum));
                insert_1.setString(3, res_name);
                insert_1.setString(4, res_lname);
                insert_1.setString(5, res_isadult);
                insert_1.executeUpdate();
                insert_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Reservations.this.dispose();
                        new Reservations(username, password);
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

                String upd_query = "update reservation set res_tr_id=?, res_seatnum=?," +
                        "res_name=?,res_lname=?,res_isadult=?" +
                        "where res_tr_id=? and res_seatnum=?";
                try {
                    res_tr_id = comboBoxTID.getSelectedItem().toString();
                    res_seatnum = Integer.parseInt(comboBoxSN.getSelectedItem().toString());
                    res_name = textFieldFN.getText();
                    res_isadult = (String) comboBoxCH.getSelectedItem();
                    res_lname = textFieldLN.getText();

                    if (res_tr_id.isEmpty() || res_name.isEmpty() || res_isadult.isEmpty() || res_lname.isEmpty()) {
                        throw new Exception("Give Data to all fields and correct types");
                    }
                    if (res_seatnum < 1) {
                        throw new Exception("No available seats");
                    }

                } catch (Exception a) {
                    JOptionPane.showMessageDialog(this, a, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement update = connect.prepareStatement(upd_query);
                update.setString(1, res_tr_id);
                update.setString(2, String.valueOf(res_seatnum));
                update.setString(3, res_name);
                update.setString(4, res_lname);
                update.setString(5, res_isadult);
                update.setString(6, res_tr_id_1);
                update.setString(7, res_seatnum_1);
                update.executeUpdate();
                update.close();
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Reservations.this.dispose();
                        new Reservations(username, password);
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

            res_tr_id = comboBoxTID.getSelectedItem().toString();
            res_seatnum = Integer.parseInt(comboBoxSN.getSelectedItem().toString());

            try (Connection connect = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/travel_agency",
                    username, password)) {


                String del_query = "delete from reservation where res_tr_id=? and res_seatnum=?";
                PreparedStatement delete_1 = connect.prepareStatement(del_query);
                delete_1.setString(1, res_tr_id);
                delete_1.setString(2, insert.getValueAt(selectedRow, 1).toString());
                delete_1.executeUpdate();
                delete_1.close();

                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Reservations.this.dispose();
                        new Reservations(username, password);
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
