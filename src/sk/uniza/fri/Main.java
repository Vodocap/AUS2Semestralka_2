package sk.uniza.fri;

import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;
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

        GeneratorOperaci generatorOperaci = new GeneratorOperaci("sub.bin", 1000, 8000);
        generatorOperaci.generujOperacie(100000);


//        HeapFile heapFile = new HeapFile("bin.bin", 10, 8000);
//        Navsteva navsteva = new Navsteva(Calendar.getInstance(), 500);
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
