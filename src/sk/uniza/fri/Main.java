package sk.uniza.fri;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.tester.GeneratorOperaci;



/**
 * Created by IntelliJ IDEA.
 * User: matus
 * Date: 15. 11. 2024
 * Time: 13:25
 */
public class Main {

    public static void main(String[] args) {

        GeneratorOperaci generatorOperaci = new GeneratorOperaci("sub.bin", 6000);
        generatorOperaci.generujOperacieHeapFile(10000);
//        generatorOperaci.generujOperacieHash(5000, true);



        
    }
}
