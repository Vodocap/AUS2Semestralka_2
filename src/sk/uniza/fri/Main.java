package sk.uniza.fri;

import sk.uniza.fri.data.OsobaTest;
import sk.uniza.fri.heapfile.HeapFile;

/**
 * Created by IntelliJ IDEA.
 * User: matus
 * Date: 15. 11. 2024
 * Time: 13:25
 */
public class Main {

    public static void main(String[] args) {

        HeapFile<OsobaTest> heapFile = new HeapFile<>("sub.bin", 100);
        OsobaTest osobaTest = new OsobaTest("Jozo", "Pytagora", 636565);
        OsobaTest osobaTest1 = new OsobaTest("Majo", "Aristotel", 9849849);
        OsobaTest osobaTest2 = new OsobaTest("Fero", "Sokrovec", 453453413);
        OsobaTest osobaTest3 = new OsobaTest("Mino", "Sangala", 59495196);
        OsobaTest osobaTest4 = new OsobaTest("Simon", "RabMag", 1244422);
        long adOs = heapFile.insert(osobaTest);
//        for (int i = 0; i < 4; i++) {
//            long adOs1 = heapFile.insert(osobaTest1);
//        }
        long adOs1 = heapFile.insert(osobaTest1);
        long adOs2 = heapFile.insert(osobaTest2);
        long adOs3 = heapFile.insert(osobaTest3);
        long adOs4 = heapFile.insert(osobaTest4);
        heapFile.delete(adOs, osobaTest);
        heapFile.delete(adOs1, osobaTest1);
        heapFile.delete(adOs4, osobaTest4);
        heapFile.printBlocks(osobaTest);

    }
}
