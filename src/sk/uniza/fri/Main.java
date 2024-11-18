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
        OsobaTest osobaTest1 = new OsobaTest("Majo", "Aristotel", 416516);
        long adOs = heapFile.insert(osobaTest);
        long adOs1 = heapFile.insert(osobaTest1);
        heapFile.delete(adOs, osobaTest);
        heapFile.printBlocks(osobaTest);

    }
}
