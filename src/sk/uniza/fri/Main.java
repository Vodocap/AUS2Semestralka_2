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

        HeapFile heapFile = new HeapFile<>("sub.bin", 100);
        OsobaTest osobaTest = new OsobaTest("Jozo", "Pytagora", 636565);
        heapFile.insert(osobaTest);
        heapFile.printBlocks();

    }
}
