package sk.uniza.fri.tester;

import sk.uniza.fri.heapfile.HeapFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class GeneratorOperaci {
    private HeapFile heapFile;

    private HashMap<OsobaTest, Integer> kontrolneData;
    private Random random;
    private NahodnyGeneratorID nahodnyGen;
    public GeneratorOperaci(String cestakSuboru, int velkostHeapfilu, int velkostBlokov) {
        this.heapFile = new HeapFile<>(cestakSuboru, velkostHeapfilu, velkostBlokov);
        this.kontrolneData = new HashMap<>();
        this.random = new Random();
        this.nahodnyGen = new NahodnyGeneratorID();
    }

    public void generujOperacie(int pocetOperacii) {
        for (int i = 0; i < 1000; i++) {
            OsobaTest vlozenaOsoba = new OsobaTest(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                    this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vugenerujUnikatnyInt());

            int adresaVlozenej = this.heapFile.insert(vlozenaOsoba);
            System.out.println("INSERT: " + vlozenaOsoba + " Na adrese: " + adresaVlozenej);
            this.kontrolneData.put(vlozenaOsoba, adresaVlozenej);
        }


        for (int i = 0; i < pocetOperacii; i++) {

            double generovanaHodnota = this.random.nextDouble();
            if (generovanaHodnota < 0.33) {
                OsobaTest vlozenaOsoba = new OsobaTest(this.nahodnyGen.vygenerujUnikatnyString(5,15),
                        this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vugenerujUnikatnyInt());

                int adresaVlozenej = this.heapFile.insert(vlozenaOsoba);
                System.out.println("INSERT: " + vlozenaOsoba + " Na adrese: " + adresaVlozenej + "jozo");
                if (adresaVlozenej != -1) {
//                    this.vypisheapFile();
                    this.kontrolneData.put(vlozenaOsoba, adresaVlozenej);
                } else {
                    System.out.println("asdasda");
                }

//                this.vypisheapFile();


            } else if (generovanaHodnota < 0.66) {
                if (!this.kontrolneData.isEmpty()) {
                    int mazanyindex = this.random.nextInt(this.kontrolneData.keySet().size());
                    ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                    this.heapFile.delete(this.kontrolneData.get(keys.get(mazanyindex)), (OsobaTest)keys.get(mazanyindex));
                    System.out.println("DELETE: " + (OsobaTest)keys.get(mazanyindex) + "Na adrese: " + this.kontrolneData.get(keys.get(mazanyindex)));
//                    this.vypisheapFile();
                    this.kontrolneData.remove((OsobaTest)keys.get(mazanyindex),this.kontrolneData.get(keys.get(mazanyindex)));
                }

            } else if (generovanaHodnota > 0.66) {
                if (!this.kontrolneData.isEmpty()) {
                    int hladanyIndex = this.random.nextInt(this.kontrolneData.keySet().size());
                    ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                    OsobaTest hladanaOsoba = (OsobaTest)keys.get(hladanyIndex);
                    int hladanaAdresa = this.kontrolneData.get(keys.get(hladanyIndex));
//                    this.vypisheapFile();
                    System.out.println("Hladana adresa: " + hladanaAdresa);
                    System.out.println("Hladana osoba" + hladanaOsoba.toString());
                    OsobaTest najdenaOsoba = (OsobaTest)this.heapFile.get(hladanaAdresa, hladanaOsoba);
                    System.out.println("Najdena osoba: " + najdenaOsoba.toString());
                    if (!najdenaOsoba.myEquals(hladanaOsoba)) {
                        try {
                            throw new Exception ("Nenasla sa osoba co sa mala");
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }


        }


        this.vymazNPrvkov(200);
        this.vypisheapFile();
        this.skontrolujHeapFile();

    }

    private void vypisheapFile() {

        this.heapFile.printBlocks(new OsobaTest("Jano", "Hladac", 665));
    }


    private void vymazNPrvkov(int pocetPrvkov) {
        for (int i = 0; i < pocetPrvkov; i++) {
            if (!this.kontrolneData.keySet().isEmpty()) {
                int mazanyindex = this.random.nextInt(this.kontrolneData.keySet().size());
                ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                this.heapFile.delete(this.kontrolneData.get(keys.get(mazanyindex)), (OsobaTest)keys.get(mazanyindex));
                System.out.println("DELETE: " + (OsobaTest)keys.get(mazanyindex) + "Na adrese: " + this.kontrolneData.get(keys.get(mazanyindex)));
//                    this.vypisheapFile();
                this.kontrolneData.remove((OsobaTest)keys.get(mazanyindex),this.kontrolneData.get(keys.get(mazanyindex)));
            }
        }

    }

    private void skontrolujHeapFile() {
        ArrayList keys = new ArrayList(this.kontrolneData.keySet());
        int hladane = 0;
        int najdene = 0;

        for (OsobaTest osobaTest : kontrolneData.keySet()) {
            System.out.println("Hladana osoba " + osobaTest.toString() + " na adrese: " + this.kontrolneData.get(osobaTest));
            if (osobaTest.myEquals((OsobaTest) this.heapFile.get(this.kontrolneData.get(osobaTest), osobaTest))) {
                najdene++;
            }
            hladane++;
        }
        System.out.println("Najdene: " + najdene + " hladane: " + hladane);
        if (hladane != najdene) {
            throw new RuntimeException("Nenaslo sa co sa malo");
        }

    }

}
