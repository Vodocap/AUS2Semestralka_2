package sk.uniza.fri.gui;

import sk.uniza.fri.aplikacia.AppCore;
import sk.uniza.fri.data.Navsteva;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
public class UpravNavstevyPopup extends JFrame {
    private JPanel panel1;
    private JTextField textFieldRok;
    private JTextField textFieldMesiac;
    private JTextField textFieldDen;
    private JTextField textFieldCena;
    private JList list1;
    private JButton upravNavstevuButton;
    private JButton upravPracuButton;
    private JTextField textFieldPrace;
    private JButton zrusButton;
    private JPanel jpanel1;
    private ArrayList<String> prace;
    private Navsteva upravovanaNavsteva;
    private MainWindow parentInstance;
    private AppCore appCore;

    public UpravNavstevyPopup(AppCore appCoreInstance, Navsteva paUpravovanaNavsteva, MainWindow paParentInstance) {
        this.appCore = appCoreInstance;
        this.parentInstance = paParentInstance;
        this.upravovanaNavsteva = paUpravovanaNavsteva;
        this.prace = this.upravovanaNavsteva.getVykonanePrace();
        this.textFieldCena.setText(String.valueOf(paUpravovanaNavsteva.getCena()));
        this.textFieldDen.setText(String.valueOf(paUpravovanaNavsteva.getDatum().getDayOfMonth()));
        this.textFieldMesiac.setText(String.valueOf(paUpravovanaNavsteva.getDatum().getMonthValue()));
        this.textFieldRok.setText(String.valueOf(paUpravovanaNavsteva.getDatum().getYear()));
        this.list1.clearSelection();
        this.list1.setListData(this.prace.toArray());

        this.zrusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpravNavstevyPopup.this.dispose();
            }
        });

        this.upravPracuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpravNavstevyPopup.this.prace.set(UpravNavstevyPopup.this.list1.getSelectedIndex(), UpravNavstevyPopup.this.textFieldPrace.getText());
                if (UpravNavstevyPopup.this.parentInstance.getComboBox1().getSelectedIndex() == 1) {
                    UpravNavstevyPopup.this.appCore.zmenVozidlo(Integer.parseInt(UpravNavstevyPopup.this.parentInstance.getParameterVyhladaniaTextField().getText()),
                            UpravNavstevyPopup.this.parentInstance.getCurrentZakaznik());
                } else {
                    UpravNavstevyPopup.this.appCore.zmenVozidlo(UpravNavstevyPopup.this.parentInstance.getParameterVyhladaniaTextField().getText(),
                            UpravNavstevyPopup.this.parentInstance.getCurrentZakaznik().createInstance());
                }
                UpravNavstevyPopup.this.parentInstance.updateZakaznikText();
            }
        });

        this.list1.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                UpravNavstevyPopup.this.textFieldPrace.setText((String) UpravNavstevyPopup.this.list1.getSelectedValue());
            }
        });

        this.upravNavstevuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpravNavstevyPopup.this.upravovanaNavsteva.setCena(Double.parseDouble(UpravNavstevyPopup.this.textFieldCena.getText()));
                Calendar editedCalendar = Calendar.getInstance();
                LocalDate upravenyDatum = LocalDate.of(Integer.parseInt(UpravNavstevyPopup.this.textFieldRok.getText()),
                        Integer.parseInt(UpravNavstevyPopup.this.textFieldMesiac.getText()),
                        Integer.parseInt(UpravNavstevyPopup.this.textFieldDen.getText()));

                UpravNavstevyPopup.this.upravovanaNavsteva.setCena(Double.parseDouble(UpravNavstevyPopup.this.textFieldCena.getText()));
                UpravNavstevyPopup.this.upravovanaNavsteva.setDatum(upravenyDatum);

                if (UpravNavstevyPopup.this.parentInstance.getComboBox1().getSelectedIndex() == 1) {
                    UpravNavstevyPopup.this.appCore.zmenVozidlo(Integer.parseInt(UpravNavstevyPopup.this.parentInstance.getParameterVyhladaniaTextField().getText()),
                            UpravNavstevyPopup.this.parentInstance.getCurrentZakaznik());
                } else {
                    UpravNavstevyPopup.this.appCore.zmenVozidlo(UpravNavstevyPopup.this.parentInstance.getParameterVyhladaniaTextField().getText(),
                            UpravNavstevyPopup.this.parentInstance.getCurrentZakaznik().createInstance());
                }
                UpravNavstevyPopup.this.parentInstance.updateZakaznikText();

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
        jpanel1 = new JPanel();
        jpanel1.setLayout(new GridBagLayout());
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        jpanel1.add(panel1, gbc);
        textFieldRok = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textFieldRok, gbc);
        textFieldMesiac = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textFieldMesiac, gbc);
        textFieldDen = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textFieldDen, gbc);
        textFieldCena = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textFieldCena, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Deň");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Mesiac");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Rok");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Cena");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label4, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setMinimumSize(new Dimension(200, 200));
        scrollPane1.setPreferredSize(new Dimension(150, 0));
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.gridheight = 5;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPane1, gbc);
        list1 = new JList();
        scrollPane1.setViewportView(list1);
        upravNavstevuButton = new JButton();
        upravNavstevuButton.setText("Uprav návštevu");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(upravNavstevuButton, gbc);
        upravPracuButton = new JButton();
        upravPracuButton.setText("Uprav prácu");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(upravPracuButton, gbc);
        textFieldPrace = new JTextField();
        textFieldPrace.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textFieldPrace, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Novy text prace");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label5, gbc);
        zrusButton = new JButton();
        zrusButton.setText("Zrus");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(zrusButton, gbc);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return jpanel1;
    }
}
