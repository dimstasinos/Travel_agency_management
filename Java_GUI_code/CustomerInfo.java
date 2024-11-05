import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CustomerInfo extends JFrame {
    private JTable insert;
    private JTextField textFieldLN;
    private JButton OKButton;
    private JButton backButton;
    private JPanel CustomerInfo;
    private String l_name;
    private String f_name;
    private String offer_id;
    private String count;
    private String[] columns_name = {"First Name", "Last Name", "Offer id", "Count"};

    public CustomerInfo(String username, String password) {
        setTitle(username);
        setContentPane(CustomerInfo);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        setVisible(true);

        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textFieldLN.getText().isEmpty()) {
                    info_print(username, password);
                } else {
                    JOptionPane.showMessageDialog(null, "Give a last name", "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        CustomerInfo.this.dispose();
                        new MainMenu(username, password);
                    }
                });
            }
        });
    }

    public void info_print(String username, String password) {
        try (Connection connect = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/travel_agency",
                username, password)) {

            CallableStatement info = connect.prepareCall(
                    "call returnnames(?)");

            DefaultTableModel temp = new DefaultTableModel();
            l_name = textFieldLN.getText();
            temp.setColumnIdentifiers(columns_name);
            info.setString(1, String.valueOf(l_name));

            boolean resultAvailable = info.execute();

            while (resultAvailable) {
                ResultSet result = info.getResultSet();
                while (result.next()) {
                    l_name = String.valueOf(result.getObject("l_name"));
                    f_name = String.valueOf(result.getObject("f_name"));
                    offer_id = String.valueOf(result.getObject("res_offer_id"));
                    count = String.valueOf(result.getObject("count(l_name)"));

                    Object[] add = {l_name, f_name, offer_id, count};
                    temp.addRow(add);

                }
                resultAvailable = info.getMoreResults();
            }

            insert.setModel(temp);

            if (temp.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "There is no reservation with this name", "Try Again"
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
