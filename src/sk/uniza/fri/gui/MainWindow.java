package sk.uniza.fri.gui;

import sk.uniza.fri.aplikacia.AppCore;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame {
    private JPanel panel1;
    private JButton vyhladajZakaznikaButton;
    private JButton nacitajZoSuboruButton;
    private JButton ulozDoSUboruButton;
    private JButton vygenerujZakaznikovButton;
    private JButton pridajAutoButton;
    private JButton pridajNavstevuButton;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JTextField textField3;
    private JTextField textField4;
    private JButton zobrazBlokyButton;
    private JList list1;
    private AppCore appCore;

    public MainWindow() {
        this.appCore = new AppCore("Bin.bin", 6000);





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
                MainWindow.this.list1.setListData(MainWindow.this.appCore.dajVsetkyBloky().toArray());

            }
        });
    }

    public void show() {
        this.setVisible(true);
    }
}
