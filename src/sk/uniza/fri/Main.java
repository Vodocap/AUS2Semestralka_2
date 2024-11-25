package sk.uniza.fri;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.heapfile.HeapFile;
import sk.uniza.fri.tester.GeneratorOperaci;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: matus
 * Date: 15. 11. 2024
 * Time: 13:25
 */
public class Main {

    public static void main(String[] args) {

//        GeneratorOperaci generatorOperaci = new GeneratorOperaci("sub.bin", 100000, 8000);
//        generatorOperaci.generujOperacie(1000000);


        HashFile hashFile = new HashFile("hsh.bin", 8000, 3);
        Navsteva navsteva = new Navsteva(Calendar.getInstance(), 500);
        Zakaznik zakaznik = new Zakaznik("Jozo", "Fero", 561651, navsteva, "AAEEDASF");

        NahodnyGenerator nahodnyGenerator = new NahodnyGenerator();


        for (int i = 0; i < 7; i++) {
            int nahodnyInt = nahodnyGenerator.vugenerujUnikatnyInt();
            hashFile.insert(new Zakaznik("Jozo", "Fero", nahodnyInt, navsteva, "AAEEDASF"), nahodnyInt);
        }

        hashFile.printBlocks(zakaznik);
        hashFile.closeHashFile();



//
//        String praca = "Katalyzator";
//        navsteva.addPRaca(praca);
//        System.out.println(navsteva);
//        Zakaznik zakaznik = new Zakaznik("Jozo", "Fero", 561651, navsteva, "AAEEDASF");
//        for (int i = 0; i < 5; i++) {
//            zakaznik.addZaznam(navsteva);
//        }
//        heapFile.insert(zakaznik);
//        heapFile.printBlocks(zakaznik);
//        heapFile.closeHeapFile();
    }
}
