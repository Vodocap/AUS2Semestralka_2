package sk.uniza.fri.gui;

import sk.uniza.fri.aplikacia.AppCore;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class VozidloPopup extends JFrame {
    private JButton pridajVozidloButton;
    private JTextField ECVTextField;
    private JTextField IDTextField;
    private JTextField menoTextField;
    private JTextField priezviskoTextField;
    private JList list1;
    private JPanel jpanel1;
    private JButton pridajNavstevuButton;
    private JButton zrusButton;
    private AppCore appCore;
    private ArrayList<Navsteva> navstevas;

    public VozidloPopup(AppCore appCoreInstance) {
        this.appCore = appCoreInstance;
        this.navstevas = new ArrayList<Navsteva>();

        this.pridajVozidloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Navsteva dummyNavsteva = new Navsteva(LocalDate.now(), 10);
                Zakaznik pridavanyZakaznik = new Zakaznik(VozidloPopup.this.menoTextField.getText(), VozidloPopup.this.priezviskoTextField.getText(),
                        Integer.parseInt(VozidloPopup.this.IDTextField.getText()), dummyNavsteva.createInstance(), VozidloPopup.this.ECVTextField.getText());

                if (VozidloPopup.this.appCore.vratSearchZakaznika(pridavanyZakaznik.getECV()) == null &&
                        VozidloPopup.this.appCore.vratSearchZakaznika(pridavanyZakaznik.getID()) == null) {
                    for (Navsteva navsteva : VozidloPopup.this.navstevas) {
                        pridavanyZakaznik.addZaznam(navsteva);
                    }

                    VozidloPopup.this.appCore.pridajVozidlo(pridavanyZakaznik.createInstance());
                    VozidloPopup.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Vozidlo s takym ID/ECV uz existuje");
                }


            }
        });


        this.pridajNavstevuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NavstevaPopup navstevaPopup = new NavstevaPopup(VozidloPopup.this.appCore, VozidloPopup.this.navstevas, VozidloPopup.this);
                navstevaPopup.setSize(1000, 800);
                navstevaPopup.setContentPane(navstevaPopup.$$$getRootComponent$$$());
                navstevaPopup.pack();
                navstevaPopup.setVisible(true);
            }
        });

        this.zrusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VozidloPopup.this.dispose();
            }
        });


    }

    public void updatelist() {

        VozidloPopup.this.list1.clearSelection();
        VozidloPopup.this.list1.setListData(VozidloPopup.this.navstevas.toArray());


    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        jpanel1 = new JPanel();
        jpanel1.setLayout(new GridBagLayout());
        jpanel1.setMinimumSize(new Dimension(400, 400));
        jpanel1.setPreferredSize(new Dimension(600, 600));
        menoTextField = new JTextField();
        menoTextField.setPreferredSize(new Dimension(100, 30));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(menoTextField, gbc);
        priezviskoTextField = new JTextField();
        priezviskoTextField.setPreferredSize(new Dimension(100, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(priezviskoTextField, gbc);
        pridajVozidloButton = new JButton();
        pridajVozidloButton.setText("pridaj vozidlo");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(pridajVozidloButton, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Priezvisko");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        jpanel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Meno");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        jpanel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("ID");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        jpanel1.add(label3, gbc);
        IDTextField = new JTextField();
        IDTextField.setPreferredSize(new Dimension(100, 30));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(IDTextField, gbc);
        ECVTextField = new JTextField();
        ECVTextField.setPreferredSize(new Dimension(100, 30));
        ECVTextField.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(ECVTextField, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("EČV");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        jpanel1.add(label4, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setOpaque(false);
        scrollPane1.setPreferredSize(new Dimension(300, 40));
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        jpanel1.add(scrollPane1, gbc);
        list1 = new JList();
        scrollPane1.setViewportView(list1);
        final JLabel label5 = new JLabel();
        label5.setText("Návšťevy");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        jpanel1.add(label5, gbc);
        pridajNavstevuButton = new JButton();
        pridajNavstevuButton.setText("Pridaj Navstevu");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(pridajNavstevuButton, gbc);
        zrusButton = new JButton();
        zrusButton.setText("Zrus");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(zrusButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanel1;
    }

}
