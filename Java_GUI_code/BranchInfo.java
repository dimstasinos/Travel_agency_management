import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BranchInfo extends JFrame {
    private JComboBox<Object> comboBoxID;
    private JTextField textFieldAN;
    private JTextField textFieldALN;
    private JTextField textFieldRES;
    private JTextField textFieldRE;
    private JTextField textFieldS;
    private JButton backButton;
    private JPanel BranchInfo;
    private JTable insert;
    private JTextField textFieldAD;
    private JTextField textFieldADN;
    private JTextField textFieldC;
    private JList<String> PhoneList;
    private String wrk_name;
    private String wrk_lname;
    private float wrk_salary;
    private String br_street;
    private String br_num;
    private String br_city;
    private String[] columns_name = {"Last Name", "First Name", "Salary"};

    //Constructor που φτιάχνει το παράθυρο BranchInfo
    public BranchInfo(String username, String password) {

        setTitle(username);
        setContentPane(BranchInfo);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

        //Τοποθέτηση στοιχείων από τη βάση δεδομένων σε πεδίο του πίνακα
        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, password)) {

            Statement print = connect.createStatement();
            String query = "SELECT br_code from branch";
            ResultSet result = print.executeQuery(query);

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

        //Αλλαγή στο προηγούμενο παράθυρο MainMenu
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BranchInfo.this.dispose();
                        new MainMenu(username, password);
                    }
                });
            }
        });

        //Εμφάνιση δεδομένων
        comboBoxID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                data((Integer) comboBoxID.getSelectedItem(), username, password);
            }
        });
    }

    //Μέθοδος που εμφανίζει τα δεδομένα στα πεδία του παραθύρου
    public void data(int select, String username, String password) {


        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, password)) {


            CallableStatement info = connect.prepareCall(
                    "call branch_info_name(?)");

            info.setString(1, String.valueOf(select));

            ResultSet result = info.executeQuery();

            result.next();
            textFieldAN.setText(result.getString("wrk_name"));
            textFieldALN.setText(result.getString("wrk_lname"));


            info = connect.prepareCall(
                    "call branch_info_reservations(?)");

            info.setString(1, String.valueOf(select));
            result = info.executeQuery();
            result.next();
            textFieldRES.setText(result.getString("reservations"));
            textFieldRE.setText(result.getString("revenue"));


            DefaultTableModel temp = new DefaultTableModel();
            temp.setColumnIdentifiers(columns_name);

            String query = "SELECT wrk_name,wrk_lname,wrk_salary from worker where wrk_br_code=?";
            PreparedStatement data_wrk = connect.prepareStatement(query);
            data_wrk.setString(1, String.valueOf(select));
            result = data_wrk.executeQuery();

            while (result.next()) {
                wrk_lname = String.valueOf(result.getObject("wrk_lname"));
                wrk_name = String.valueOf(result.getObject("wrk_name"));
                wrk_salary = Float.parseFloat(String.valueOf(result.getObject("wrk_salary")));
                if (wrk_salary > 0) {
                    Object[] add = {wrk_lname, wrk_name, wrk_salary};
                    temp.addRow(add);
                }
            }

            insert.setModel(temp);


            query = "SELECT br_street,br_num,br_city from branch where br_code=?";
            PreparedStatement br_data = connect.prepareStatement(query);
            br_data.setString(1, String.valueOf(select));
            result = br_data.executeQuery();
            result.next();
            textFieldAD.setText(result.getString("br_street"));
            textFieldADN.setText(result.getString("br_num"));
            textFieldC.setText(result.getString("br_city"));

            DefaultListModel<String> phones = new DefaultListModel<>();
            query = "SELECT ph_number from phones where ph_br_code=?";
            br_data = connect.prepareStatement(query);
            br_data.setString(1, String.valueOf(select));
            result = br_data.executeQuery();

            while (result.next()) {
                phones.addElement(result.getString("ph_number"));
            }
            PhoneList.setModel(phones);

            info = connect.prepareCall(
                    "call branch_info_salaries(?)");

            info.setString(1, String.valueOf(select));
            result = info.executeQuery();
            result.next();
            textFieldS.setText(result.getString("salaries"));


            result.close();
            connect.close();


        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }


    }

}
