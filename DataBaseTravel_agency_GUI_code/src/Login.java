import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class Login extends JFrame {


    private JPanel LoginPage;
    private JTextField inusername;
    private JPasswordField inPassword;
    private JButton loginButton;

    //Constructor που φτιάχνει το παράθυρο του login
    public Login() {


        setTitle("LOGIN");
        setContentPane(LoginPage);
        setMinimumSize(new Dimension(500, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        setVisible(true);

    }


    //Μέθοδος που ελέγχει τα στοιχεία που εισάγει ο χρήστης
    private void login() {

        String password;
        char[] pass_word;
        String username;

        username = inusername.getText();
        pass_word = inPassword.getPassword();
        password = String.valueOf(pass_word);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Give username and password", "Try Again"
                    , JOptionPane.ERROR_MESSAGE);
            return;
        } else {


            int flag = 0;

            while (flag == 0) {

                try (Connection connect = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/travel_agency",
                        "password", "1234")) {

                    //Καλούμε store procedure
                    CallableStatement check = connect.prepareCall("call check_lname(?)");

                    check.setString(1, username);
                    ResultSet result = check.executeQuery();
                    result.next();
                    String name_ckeck = (String) result.getObject("at_worker");
                    String pas_check = (String) result.getObject("password_1");

                    //Προβολή κατάλληλων μηνυμάτων
                    if (name_ckeck == null) {
                        JOptionPane.showMessageDialog(this, "Username does not exist", "Try Again"
                                , JOptionPane.ERROR_MESSAGE);
                        return;

                    } else if (pas_check == null) {
                        JOptionPane.showMessageDialog(this, "Username is not IT", "Try Again"
                                , JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!password.equals(pas_check)) {
                        JOptionPane.showMessageDialog(this, "Wrong Password", "Try Again"
                                , JOptionPane.ERROR_MESSAGE);
                        return;
                    } else {


                        String query = "SELECT wrk_lname FROM worker where wrk_AT=?";
                        PreparedStatement print = connect.prepareStatement(query);
                        print.setString(1, name_ckeck);
                        result = print.executeQuery();
                        result.next();
                        String user = (String) result.getObject("wrk_lname");

                        //Εμφάνιση παραθύρου MainMenu
                        flag = 1;
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Login.this.dispose();
                                new MainMenu(user, password);
                            }
                        });
                    }
                    result.close();
                    check.close();

                    //Προβολή κατάλληλων μηνυμάτων
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e, "Try Again"
                            , JOptionPane.ERROR_MESSAGE);
                    return;
                }

            }
        }
    }

    public static void main(String[] args) {


        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login();
            }
        });


    }


}
