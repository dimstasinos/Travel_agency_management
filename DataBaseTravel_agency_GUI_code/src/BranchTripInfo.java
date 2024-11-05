import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BranchTripInfo extends JFrame {
    private JTable insert;
    private JComboBox<Object> comboBoxBID;
    private JButton backButton;
    private JTextField textFieldSD;
    private JTextField textFieldED;
    private JButton OKButton;
    private JPanel Branchtripinfo;
    private String cost;
    private String seats;
    private String reservations;
    private String available_seats;
    private String driver_name;
    private String driver_lname;
    private String guide_name;
    private String guide_lname;
    private String departure;
    private String returning;

    //Constructor που φτιάχνει το παράθυρο BranchTripInfo
    public BranchTripInfo(String username, String password) {
        setTitle(username);
        setContentPane(Branchtripinfo);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        //Τοποθέτηση στοιχείων από τη βάση δεδομένων σε πεδίο του πίνακα
        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, password)) {

            Statement print = connect.createStatement();
            String query = "SELECT br_code FROM branch";
            ResultSet result = print.executeQuery(query);
            while (result.next()) {
                comboBoxBID.addItem(result.getObject("br_code"));
            }

            connect.close();
            result.close();
            setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }


        //Εμφάνιση δεδομένων
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textFieldED.getText().isEmpty() && !textFieldSD.getText().isEmpty()) {
                    printInfo(username, password);
                } else {
                    JOptionPane.showMessageDialog(null, "Give a start and end date", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Αλλαγή στο προηγούμενο παράθυρο MainMenu
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BranchTripInfo.this.dispose();
                        new MainMenu(username, password);
                    }
                });
            }
        });
    }

    //Μέθοδος που εμφανίζει τα δεδομένα στα πεδία του παραθύρου
    public void printInfo(String username, String password) {

        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, password)) {

            DefaultTableModel temp = new DefaultTableModel();
            String[] columns_name = {"Cost", "Seats", "Reservations", "Available Seats", "Driver Name", "Driver Last Name", "Guide Name", "Guide Last Name", "Departure", "Returning"};
            temp.setColumnIdentifiers(columns_name);
            CallableStatement check = connect.prepareCall("CALL checktripinfo(?,?,?)");
            ResultSet result;
            check.setString(1, String.valueOf(comboBoxBID.getSelectedItem()));
            check.setString(2, textFieldSD.getText().toString());
            check.setString(3, textFieldED.getText().toString());
            boolean resultAvailable = check.execute();

            while (resultAvailable) {

                result = check.getResultSet();
                while (result.next()) {
                    cost = String.valueOf(result.getObject("cost"));
                    seats = String.valueOf(result.getObject("maxseats"));
                    reservations = String.valueOf(result.getObject("reservations"));
                    available_seats = String.valueOf(result.getObject("available"));
                    driver_name = String.valueOf(result.getObject("driver_name"));
                    driver_lname = String.valueOf(result.getObject("driver_lastname"));
                    guide_name = String.valueOf(result.getObject("guide_name"));
                    guide_lname = String.valueOf(result.getObject("guide_lastname"));
                    Date date = result.getDate("departure");
                    LocalDate date_1_2 = date.toLocalDate();
                    departure = date_1_2.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    Date date_1 = result.getDate("returning");
                    LocalDate date_1_1 = date_1.toLocalDate();
                    returning = date_1_1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                    Object[] add = {cost, seats, reservations, available_seats, driver_name, driver_lname, guide_name, guide_lname, departure, returning};
                    temp.addRow(add);
                }
                resultAvailable = check.getMoreResults();
            }
            insert.setModel(temp);

            if (temp.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No trip", "Try Again"
                        , JOptionPane.ERROR_MESSAGE);
                return;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

}
