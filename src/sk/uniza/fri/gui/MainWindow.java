package sk.uniza.fri.gui;

import sk.uniza.fri.aplikacia.AppCore;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainWindow extends JFrame {
    private JPanel panel1;
    private JButton vyhladajZakaznikaButton;
    private JButton nacitajZoSuboruButton;
    private JButton ulozDoSUboruButton;
    private JButton vygenerujZakaznikovButton;
    private JButton pridajAutoButton;
    private JButton pridajNavstevuButton;
    private JComboBox comboBox1;
    private JTextField nazovSuboruTextField;
    private JTextField parameterVyhladaniaTextField;
    private JTextField textField4;
    private JButton zobrazBlokyButton;
    private JList list1;
    private JComboBox comboBox2;
    private JButton upravAktualneVozidloButton;
    private JTextArea zakaznikTextArea;
    private JList jList2;
    private JButton zrusButton;
    private AppCore appCore;
    private ArrayList navstevy;
    private Zakaznik currentZakaznik;

    public MainWindow() {
        this.appCore = new AppCore("Bin.bin", 6000, 200, 262223);
        this.navstevy = new ArrayList();
        this.pridajNavstevuButton.setVisible(false);
        this.upravAktualneVozidloButton.setVisible(false);
        this.currentZakaznik = null;

        this.vygenerujZakaznikovButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.appCore.vygenerujNZakaznikov(Integer.valueOf(MainWindow.this.textField4.getText()));
            }
        });

        this.zobrazBlokyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.list1.clearSelection();
                MainWindow.this.list1.setListData(MainWindow.this.appCore.dajVsetkyBloky(MainWindow.this.comboBox2.getSelectedIndex()).toArray());


            }
        });

        this.list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (MainWindow.this.currentZakaznik != null) {
                    MainWindow.this.jList2.clearSelection();
                    MainWindow.this.jList2.setListData(((Navsteva) MainWindow.this.list1.getSelectedValue()).getVykonanePrace().toArray());
                } else {
                    MainWindow.this.jList2.clearSelection();
                }

            }
        });

        this.vyhladajZakaznikaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainWindow.this.comboBox1.getSelectedIndex() == 1) {
                    Zakaznik zakaznik = MainWindow.this.appCore.vyhladajUdajeOVozidle(Integer.parseInt(MainWindow.this.parameterVyhladaniaTextField.getText()));
                    MainWindow.this.zakaznikTextArea.setText(zakaznik.toString());
                    MainWindow.this.list1.clearSelection();
                    MainWindow.this.list1.setListData(zakaznik.getZaznamyONasvsteve().toArray());
                    MainWindow.this.currentZakaznik = zakaznik;
                } else {
                    Zakaznik zakaznik = MainWindow.this.appCore.vyhladajUdajeOVozidle(MainWindow.this.parameterVyhladaniaTextField.getText());
                    MainWindow.this.zakaznikTextArea.setText(zakaznik.toString());
                    MainWindow.this.list1.clearSelection();
                    MainWindow.this.list1.setListData(zakaznik.getZaznamyONasvsteve().toArray());
                    MainWindow.this.currentZakaznik = zakaznik;
                }

                if (MainWindow.this.currentZakaznik != null) {
                    MainWindow.this.pridajNavstevuButton.setVisible(true);
                    MainWindow.this.upravAktualneVozidloButton.setVisible(true);
                }


            }
        });

        this.pridajNavstevuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainWindow.this.comboBox1.getSelectedIndex() == 1) {
                    Zakaznik zakaznik = MainWindow.this.appCore.vyhladajUdajeOVozidle(Integer.parseInt(MainWindow.this.parameterVyhladaniaTextField.getText()));
                    MainWindow.this.zakaznikTextArea.setText(zakaznik.toString());
                    MainWindow.this.list1.clearSelection();
                    MainWindow.this.navstevy = zakaznik.getZaznamyONasvsteve();
                    MainWindow.this.list1.setListData(MainWindow.this.navstevy.toArray());
                    MainWindow.this.currentZakaznik = zakaznik;
                    NavstevaPopup navstevaPopup = new NavstevaPopup(MainWindow.this.appCore, zakaznik.getZaznamyONasvsteve(), MainWindow.this);
                    navstevaPopup.setSize(1000, 800);
                    navstevaPopup.setContentPane(navstevaPopup.$$$getRootComponent$$$());
                    navstevaPopup.pack();
                    navstevaPopup.setVisible(true);

                } else {
                    Zakaznik zakaznik = MainWindow.this.appCore.vyhladajUdajeOVozidle(MainWindow.this.parameterVyhladaniaTextField.getText());
                    MainWindow.this.zakaznikTextArea.setText(zakaznik.toString());
                    MainWindow.this.list1.clearSelection();
                    MainWindow.this.list1.setListData(zakaznik.getZaznamyONasvsteve().toArray());
                    MainWindow.this.navstevy = zakaznik.getZaznamyONasvsteve();
                    MainWindow.this.list1.setListData(MainWindow.this.navstevy.toArray());
                    MainWindow.this.currentZakaznik = zakaznik;
                    NavstevaPopup navstevaPopup = new NavstevaPopup(MainWindow.this.appCore, zakaznik.getZaznamyONasvsteve(), MainWindow.this);
                    navstevaPopup.setSize(1000, 800);
                    navstevaPopup.setContentPane(navstevaPopup.$$$getRootComponent$$$());
                    navstevaPopup.pack();
                    navstevaPopup.setVisible(true);
                }
            }
        });

        this.pridajAutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VozidloPopup vozidloPopup = new VozidloPopup(MainWindow.this.appCore);
                vozidloPopup.setSize(1000, 800);
                vozidloPopup.setContentPane(vozidloPopup.$$$getRootComponent$$$());
                vozidloPopup.pack();
                vozidloPopup.setVisible(true);

            }
        });

        this.zrusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.dispose();
            }
        });

        this.upravAktualneVozidloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                UpravVozidloPopup upravVozidloPopup = new UpravVozidloPopup(MainWindow.this.appCore, MainWindow.this);
                upravVozidloPopup.setSize(1000, 800);
                upravVozidloPopup.setContentPane(upravVozidloPopup.$$$getRootComponent$$$());
                upravVozidloPopup.pack();
                upravVozidloPopup.setVisible(true);
            }
        });

        this.ulozDoSUboruButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.appCore.ulozAplikaciu(MainWindow.this.nazovSuboruTextField.getText());
                MainWindow.this.dispose();
            }
        });

        this.nacitajZoSuboruButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainWindow.this.appCore.nacitajAplikaciu(MainWindow.this.nazovSuboruTextField.getText());
            }
        });
    }


    public void updatelist() {

        this.list1.clearSelection();
        this.list1.setListData(this.navstevy.toArray());

    }


    public JTextField getParameterVyhladaniaTextField() {
        return this.parameterVyhladaniaTextField;
    }

    public Zakaznik getCurrentZakaznik() {
        return this.currentZakaznik;
    }

    public JComboBox getComboBox1() {
        return this.comboBox1;
    }

    public void updateZakaznikText() {
        if (MainWindow.this.comboBox1.getSelectedIndex() == 1) {
            Zakaznik zakaznik = MainWindow.this.appCore.vyhladajUdajeOVozidle(Integer.parseInt(MainWindow.this.parameterVyhladaniaTextField.getText()));
            MainWindow.this.zakaznikTextArea.setText(zakaznik.toString());
            MainWindow.this.list1.clearSelection();
            MainWindow.this.navstevy = zakaznik.getZaznamyONasvsteve();
            MainWindow.this.list1.setListData(MainWindow.this.navstevy.toArray());


        } else {
            Zakaznik zakaznik = MainWindow.this.appCore.vyhladajUdajeOVozidle(MainWindow.this.parameterVyhladaniaTextField.getText());
            MainWindow.this.zakaznikTextArea.setText(zakaznik.toString());
            MainWindow.this.list1.clearSelection();
            MainWindow.this.list1.setListData(zakaznik.getZaznamyONasvsteve().toArray());
            MainWindow.this.navstevy = zakaznik.getZaznamyONasvsteve();
            MainWindow.this.list1.setListData(MainWindow.this.navstevy.toArray());
        }
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
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(37, 4, new Insets(0, 0, 0, 0), -1, -1));
        textField4 = new JTextField();
        panel1.add(textField4, new com.intellij.uiDesigner.core.GridConstraints(10, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Pocet vygenerovanych zakaznikov");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(9, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 15, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        list1 = new JList();
        scrollPane1.setViewportView(list1);
        pridajAutoButton = new JButton();
        pridajAutoButton.setText("PridajAuto");
        panel1.add(pridajAutoButton, new com.intellij.uiDesigner.core.GridConstraints(35, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        vyhladajZakaznikaButton = new JButton();
        vyhladajZakaznikaButton.setText("vyhladajZakaznika");
        panel1.add(vyhladajZakaznikaButton, new com.intellij.uiDesigner.core.GridConstraints(35, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ulozDoSUboruButton = new JButton();
        ulozDoSUboruButton.setText("UlozDoSUboru");
        panel1.add(ulozDoSUboruButton, new com.intellij.uiDesigner.core.GridConstraints(6, 3, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nazovSuboruTextField = new JTextField();
        panel1.add(nazovSuboruTextField, new com.intellij.uiDesigner.core.GridConstraints(4, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Nazov Suboru");
        panel1.add(label2, new com.intellij.uiDesigner.core.GridConstraints(3, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pridajNavstevuButton = new JButton();
        pridajNavstevuButton.setText("PridajNavstevu");
        panel1.add(pridajNavstevuButton, new com.intellij.uiDesigner.core.GridConstraints(34, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("ECV");
        defaultComboBoxModel1.addElement("ID");
        comboBox1.setModel(defaultComboBoxModel1);
        panel1.add(comboBox1, new com.intellij.uiDesigner.core.GridConstraints(36, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        parameterVyhladaniaTextField = new JTextField();
        panel1.add(parameterVyhladaniaTextField, new com.intellij.uiDesigner.core.GridConstraints(36, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        nacitajZoSuboruButton = new JButton();
        nacitajZoSuboruButton.setText("NacitajZoSuboru");
        panel1.add(nacitajZoSuboruButton, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        vygenerujZakaznikovButton = new JButton();
        vygenerujZakaznikovButton.setText("VygenerujZakaznikov");
        panel1.add(vygenerujZakaznikovButton, new com.intellij.uiDesigner.core.GridConstraints(11, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(15, 0, 19, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        jList2 = new JList();
        scrollPane2.setViewportView(jList2);
        zobrazBlokyButton = new JButton();
        zobrazBlokyButton.setText("ZobrazBloky");
        panel1.add(zobrazBlokyButton, new com.intellij.uiDesigner.core.GridConstraints(13, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBox2 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("HeapFile");
        defaultComboBoxModel2.addElement("HashID");
        defaultComboBoxModel2.addElement("HashECV");
        comboBox2.setModel(defaultComboBoxModel2);
        panel1.add(comboBox2, new com.intellij.uiDesigner.core.GridConstraints(12, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zakaznikTextArea = new JTextArea();
        panel1.add(zakaznikTextArea, new com.intellij.uiDesigner.core.GridConstraints(16, 3, 12, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        zrusButton = new JButton();
        zrusButton.setText("Zrus");
        panel1.add(zrusButton, new com.intellij.uiDesigner.core.GridConstraints(34, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        upravAktualneVozidloButton = new JButton();
        upravAktualneVozidloButton.setText("UpravAktualneVozidlo");
        panel1.add(upravAktualneVozidloButton, new com.intellij.uiDesigner.core.GridConstraints(34, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
