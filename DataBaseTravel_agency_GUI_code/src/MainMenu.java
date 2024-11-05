import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public class MainMenu extends JFrame {
    private JLabel username_1;
    private JButton branchInformationsButton;
    private JButton dataBaseButton;
    private JButton customerInformationsButton;
    private JPanel MainMenu;
    private JButton logoutButton;
    private JButton branchTripsInformationButton;

    //Constructor που φτιάχνει το παράθυρο του MainMenu
    public MainMenu(String username, String password) {

        setTitle("Main Menu");
        username_1.setText(username);
        setContentPane(MainMenu);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);


        //Action Listeners για κάθε κουμπί του παραθύρου

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainMenu.this.dispose();
                        new Login();
                    }
                });
            }
        });
        dataBaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainMenu.this.dispose();
                        new Database(username, password);
                    }
                });
            }
        });


        branchInformationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainMenu.this.dispose();
                        new BranchInfo(username, password);
                    }
                });
            }
        });
        branchTripsInformationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainMenu.this.dispose();
                        new BranchTripInfo(username, password);
                    }
                });
            }
        });
        customerInformationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainMenu.this.dispose();
                        new CustomerInfo(username, password);
                    }
                });
            }
        });
    }


}
