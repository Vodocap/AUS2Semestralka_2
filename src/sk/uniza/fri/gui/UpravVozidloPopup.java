package sk.uniza.fri.gui;

import sk.uniza.fri.aplikacia.AppCore;
import sk.uniza.fri.data.Navsteva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class UpravVozidloPopup extends JFrame {
    private JPanel jpanel1;
    private JTextField menoTextField;
    private JTextField priezviskoTextField;
    private JButton upravVozidloButton;
    private JList list1;
    private JButton upravNavstevuButton;
    private JButton zrusButton;
    private JPanel jpanel;
    private MainWindow parentInstance;
    private AppCore appCore;

    public UpravVozidloPopup(AppCore appCoreInstance, MainWindow paParentInstance) {
        this.appCore = appCoreInstance;
        this.parentInstance = paParentInstance;

        this.menoTextField.setText(this.parentInstance.getCurrentZakaznik().getMeno());
        this.priezviskoTextField.setText(this.parentInstance.getCurrentZakaznik().getPriezvisko());
        this.list1.clearSelection();
        this.list1.setListData(this.parentInstance.getCurrentZakaznik().getZaznamyONasvsteve().toArray());
        this.upravNavstevuButton.setVisible(!this.parentInstance.getCurrentZakaznik().getZaznamyONasvsteve().isEmpty());

        this.zrusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpravVozidloPopup.this.dispose();
            }
        });

        this.upravVozidloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpravVozidloPopup.this.parentInstance.getCurrentZakaznik().setMeno(UpravVozidloPopup.this.menoTextField.getText());
                UpravVozidloPopup.this.parentInstance.getCurrentZakaznik().setPriezvisko(UpravVozidloPopup.this.priezviskoTextField.getText());

                if (UpravVozidloPopup.this.parentInstance.getComboBox1().getSelectedIndex() == 1) {

                    UpravVozidloPopup.this.appCore.zmenVozidlo(Integer.parseInt(UpravVozidloPopup.this.parentInstance.getParameterVyhladaniaTextField().getText()),
                            UpravVozidloPopup.this.parentInstance.getCurrentZakaznik());
                } else {
                    UpravVozidloPopup.this.appCore.zmenVozidlo(UpravVozidloPopup.this.parentInstance.getParameterVyhladaniaTextField().getText(),
                            UpravVozidloPopup.this.parentInstance.getCurrentZakaznik());
                }

                UpravVozidloPopup.this.parentInstance.updateZakaznikText();

            }
        });

        this.upravNavstevuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpravNavstevyPopup upravNavstevyPopup = new UpravNavstevyPopup(UpravVozidloPopup.this.appCore, (Navsteva) UpravVozidloPopup.this.list1.getSelectedValue(), UpravVozidloPopup.this.parentInstance);
                upravNavstevyPopup.setSize(1000, 800);
                upravNavstevyPopup.setContentPane(upravNavstevyPopup.$$$getRootComponent$$$());
                upravNavstevyPopup.pack();
                upravNavstevyPopup.setVisible(true);
            }
        });

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
        jpanel = new JPanel();
        jpanel.setLayout(new GridBagLayout());
        jpanel1 = new JPanel();
        jpanel1.setLayout(new GridBagLayout());
        jpanel1.setMinimumSize(new Dimension(400, 400));
        jpanel1.setPreferredSize(new Dimension(600, 600));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        jpanel.add(jpanel1, gbc);
        menoTextField = new JTextField();
        menoTextField.setPreferredSize(new Dimension(100, 30));
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
        upravVozidloButton = new JButton();
        upravVozidloButton.setText("uprav vozidlo");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(upravVozidloButton, gbc);
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
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setOpaque(false);
        scrollPane1.setPreferredSize(new Dimension(300, 40));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        jpanel1.add(scrollPane1, gbc);
        list1 = new JList();
        scrollPane1.setViewportView(list1);
        final JLabel label3 = new JLabel();
        label3.setText("Návšťevy");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        jpanel1.add(label3, gbc);
        upravNavstevuButton = new JButton();
        upravNavstevuButton.setText("Uprav Navstevu");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        jpanel1.add(upravNavstevuButton, gbc);
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
        return jpanel;
    }
}
