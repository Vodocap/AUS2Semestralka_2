package sk.uniza.fri.gui;

import sk.uniza.fri.aplikacia.AppCore;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class NavstevaPopup extends JFrame {
    private JButton pridajNavstevuButton;
    private JTextField textFieldDen;
    private JTextField textFieldMesiac;
    private JTextField textFieldRok;
    private JTextField textFieldCena;
    private JButton pridajPracuButton;
    private JTextField textFieldPrace;
    private JList list1;
    private JPanel panel1;
    private JButton zrusButton;
    private AppCore appCore;
    private ArrayList<String> prace;
    private Navsteva vytvorenaNavsteva;
    private ArrayList navsetvas;
    private Object parentInstance;

    public NavstevaPopup(AppCore appCoreInstance, ArrayList navstevy, Object paParentInstance) {
        this.appCore = appCoreInstance;
        this.prace = new ArrayList<String>();
        this.navsetvas = navstevy;
        this.parentInstance = paParentInstance;
//        if (paParentInstance instanceof NavstevaPopup) {
//            this.parentInstance = (NavstevaPopup) paParentInstance;
//        } else {
//            this.parentInstance = (MainWindow) paParentInstance;
//        }


        this.list1.setListData(this.prace.toArray());

        this.textFieldPrace.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                boolean maxReached = NavstevaPopup.this.textFieldPrace.getText().length() > 20;
                if (maxReached) {
                    JOptionPane.showMessageDialog(null, "Limit je 20 znakov");
                }
            }
        });

        this.pridajPracuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (NavstevaPopup.this.prace.size() + 1 > 10) {
                    JOptionPane.showMessageDialog(null, "Limit je 10 prác");
                } else {
                    NavstevaPopup.this.prace.add(NavstevaPopup.this.textFieldPrace.getText());
                    NavstevaPopup.this.updateList();
                }

            }
        });

        this.pridajNavstevuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar datum = Calendar.getInstance();
                datum.set(Calendar.DAY_OF_MONTH, Integer.parseInt(NavstevaPopup.this.textFieldDen.getText()));
                datum.set(Calendar.MONTH, Integer.parseInt(NavstevaPopup.this.textFieldMesiac.getText()));
                datum.set(Calendar.YEAR, Integer.parseInt(NavstevaPopup.this.textFieldRok.getText()));
                Navsteva novaNavsteva = new Navsteva(datum, Double.parseDouble(NavstevaPopup.this.textFieldCena.getText()));
                for (String s : prace) {
                    novaNavsteva.addPRaca(s);
                }


                if (NavstevaPopup.this.parentInstance instanceof VozidloPopup) {
                    NavstevaPopup.this.navsetvas.add(novaNavsteva);
                    ((VozidloPopup) NavstevaPopup.this.parentInstance).updatelist();

                } else {
                    if (((MainWindow) NavstevaPopup.this.parentInstance).getComboBox1().getSelectedIndex() == 1) {
                        ((MainWindow) NavstevaPopup.this.parentInstance).getCurrentZakaznik().addZaznam(novaNavsteva);
                        NavstevaPopup.this.appCore.zmenVozidlo(Integer.parseInt(((MainWindow) NavstevaPopup.this.parentInstance).getParameterVyhladaniaTextField().getText()),
                                ((MainWindow) NavstevaPopup.this.parentInstance).getCurrentZakaznik());
                    } else {
                        ((MainWindow) NavstevaPopup.this.parentInstance).getCurrentZakaznik().addZaznam(novaNavsteva);
                        NavstevaPopup.this.appCore.zmenVozidlo(((MainWindow) NavstevaPopup.this.parentInstance).getParameterVyhladaniaTextField().getText(),
                                ((MainWindow) NavstevaPopup.this.parentInstance).getCurrentZakaznik().createInstance());

                    }

                    ((MainWindow) NavstevaPopup.this.parentInstance).updatelist();
                }

                NavstevaPopup.this.dispose();

            }
        });

        this.zrusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                NavstevaPopup.this.dispose();
            }
        });

    }

    public void updateList() {
        this.list1.clearSelection();
        this.list1.setListData(this.prace.toArray());
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
        panel1.setLayout(new GridBagLayout());
        textFieldRok = new JTextField();
        GridBagConstraints gbc;
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
        pridajNavstevuButton = new JButton();
        pridajNavstevuButton.setText("Pridaj návštevu");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(pridajNavstevuButton, gbc);
        pridajPracuButton = new JButton();
        pridajPracuButton.setText("PridajPracu");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(pridajPracuButton, gbc);
        textFieldPrace = new JTextField();
        textFieldPrace.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textFieldPrace, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("TextPrace");
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
        return panel1;
    }

}
