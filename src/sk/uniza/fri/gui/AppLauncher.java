package sk.uniza.fri.gui;

import sk.uniza.fri.tester.GeneratorOperaci;

public class AppLauncher {
    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setSize(1000, 800);
        mainWindow.setContentPane(mainWindow.$$$getRootComponent$$$());
        mainWindow.pack();
        mainWindow.setVisible(true);



    }
}
