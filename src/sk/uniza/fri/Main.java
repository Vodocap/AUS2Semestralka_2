package sk.uniza.fri;

import sk.uniza.fri.tester.GeneratorOperaci;

/**
 * Created by IntelliJ IDEA.
 * User: matus
 * Date: 15. 11. 2024
 * Time: 13:25
 */
public class Main {

    public static void main(String[] args) {

        GeneratorOperaci generatorOperaci = new GeneratorOperaci("sub.bin", 100000, 250);
        generatorOperaci.generujOperacie(1000000);


    }
}
