import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

public class Database extends JFrame {
    private JPanel Database;
    private JLabel user;
    private JButton button_Back;
    private JButton branchsButton;
    private JButton phonesButton;
    private JButton workersButton;
    private JButton driversButton;
    private JButton guidesButton;
    private JButton tripsButton;
    private JButton adminsButton;
    private JButton managesButton;
    private JButton languagesButton;
    private JButton destinationsButton;
    private JButton travelToButton;
    private JButton reservationsButton;
    private JButton eventsButton;
    private JButton ITButton;
    private JButton offersButton;
    private JButton reservationsOffersButton;
    private JButton logButton;

    //Constructor που φτιάχνει το παράθυρο Database
    public Database(String username, String password) {

        setTitle("Database");
        user.setText(username);
        setContentPane(Database);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);

        //Action Listeners για κάθε κουμπί του παραθύρου

        button_Back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new MainMenu(username, password);
                    }
                });
            }
        });


        workersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Workers(username, password);
                    }
                });
            }
        });

        travelToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Travel_to(username, password);
                    }
                });
            }
        });

        phonesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Phones(username, password);
                    }
                });
            }
        });
        guidesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Guide(username, password);
                    }
                });
            }
        });
        languagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Languages(username, password);
                    }
                });
            }
        });
        destinationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Destination(username, password);
                    }
                });
            }
        });
        eventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Event(username, password);
                    }
                });
            }
        });
        driversButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Driver(username, password);
                    }
                });
            }
        });
        reservationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Reservations(username, password);
                    }
                });
            }
        });
        managesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Manages(username, password);
                    }
                });
            }
        });
        adminsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Admin(username, password);
                    }
                });
            }
        });
        branchsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Branch(username, password);
                    }
                });
            }
        });
        tripsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Trip(username, password);
                    }
                });
            }
        });
        ITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new IT(username, password);
                    }
                });
            }
        });
        offersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Offers(username, password);
                    }
                });
            }
        });
        reservationsOffersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new ReservationOffers(username, password);
                    }
                });
            }
        });
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Database.this.dispose();
                        new Log(username, password);
                    }
                });
            }
        });
    }


}
