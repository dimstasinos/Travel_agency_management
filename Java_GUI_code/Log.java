import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Log extends JFrame {
    private JTable insert;
    private JButton backButton;
    private JPanel Log;
    private String tableaction;
    private String action;
    private String lname_it;
    private String timestamp;
    private String[] columns_name = {"Table", "Action", "Last Name", "Time"};

    public Log(String username, String password) {
        setTitle(username);
        setContentPane(Log);
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
            String query = "SELECT * FROM log";
            ResultSet result = print.executeQuery(query);

            while (result.next()) {
                tableaction = String.valueOf(result.getObject("tableaction"));
                action = String.valueOf(result.getObject("action"));
                lname_it = String.valueOf(result.getObject("lname_it"));
                Date date_1 = result.getDate("timestamp");
                LocalDate date_1_1 = date_1.toLocalDate();
                timestamp = date_1_1.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                Object[] add = {tableaction, action, lname_it, timestamp};
                temp.addRow(add);
            }

            insert.setModel(temp);
            setVisible(true);

            connect.close();
            result.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e, "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        }

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Log.this.dispose();
                        new Database(username, password);
                    }
                });
            }
        });

    }

}
