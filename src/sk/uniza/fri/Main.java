package sk.uniza.fri;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.tester.GeneratorOperaci;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: matus
 * Date: 15. 11. 2024
 * Time: 13:25
 */
public class Main {

    public static void main(String[] args) {

//        GeneratorOperaci generatorOperaci = new GeneratorOperaci("sub.bin", 6000);
////        generatorOperaci.generujOperacieHeapFile(10000);
//        generatorOperaci.generujOperacieHash(5000, false);


//        HashFile hashFile = new HashFile("hsh.bin", 8000);
//        Navsteva navsteva = new Navsteva(Calendar.getInstance(), 500);
//        Zakaznik zakaznik = new Zakaznik("Jozo", "Fero", 561651, navsteva, "AAEEDASF");
//
//        NahodnyGenerator nahodnyGenerator = new NahodnyGenerator();
//
//
//        for (int i = 0; i < 15; i++) {
//            int nahodnyInt = nahodnyGenerator.vygenerujUnikatnyInt();
//            String ECV = nahodnyGenerator.vygenerujECV();
//            hashFile.insert(new Zakaznik("Jozo", "Fero", nahodnyInt, navsteva, ECV), ECV);
//        }
//
//        hashFile.printBlocks(zakaznik);
//        hashFile.closeHashFile();


        LocalDate localDate = LocalDate.now();

        System.out.println(localDate);
        System.out.println(localDate.getLong(ChronoField.EPOCH_DAY));
        System.out.println(LocalDate.ofEpochDay(localDate.getLong(ChronoField.EPOCH_DAY)));

        
    }
}
