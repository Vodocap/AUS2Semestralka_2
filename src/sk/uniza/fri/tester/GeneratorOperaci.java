package sk.uniza.fri.tester;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.heapfile.HeapFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class GeneratorOperaci {
    private HeapFile heapFile;

    private HashMap<Zakaznik, Long> kontrolneData;
    private Random random;
    private NahodnyGenerator nahodnyGen;
    public GeneratorOperaci(String cestakSuboru, int velkostHeapfilu, int velkostBlokov) {
        this.heapFile = new HeapFile<>(cestakSuboru, velkostHeapfilu, velkostBlokov, 4);
        this.kontrolneData = new HashMap<>();
        this.random = new Random();
        this.nahodnyGen = new NahodnyGenerator();
    }

    public void generujOperacie(int pocetOperacii) {
        for (int i = 0; i < 10000; i++) {
            Zakaznik vlozenyZakaznik = new Zakaznik(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                    this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vugenerujUnikatnyInt(), new Navsteva(Calendar.getInstance(), this.random.nextDouble()), this.nahodnyGen.vygenerujUnikatnyString(0,10));

            long adresaVlozenej = this.heapFile.insert(vlozenyZakaznik);
            System.out.println("INSERT: " + vlozenyZakaznik + " Na adrese: " + adresaVlozenej);
            if (adresaVlozenej != -1) {
//                    this.vypisheapFile();
                this.kontrolneData.put(vlozenyZakaznik, adresaVlozenej);
            } else {
                System.out.println("asdasda");
            }
        }


        for (int i = 0; i < pocetOperacii; i++) {

            double generovanaHodnota = this.random.nextDouble();
            if (generovanaHodnota < 0.33) {
                Zakaznik vlozenyZakaznik = new Zakaznik(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                        this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vugenerujUnikatnyInt(), new Navsteva(Calendar.getInstance(), this.random.nextDouble()), this.nahodnyGen.vygenerujUnikatnyString(0,10));

                long adresaVlozenej = this.heapFile.insert(vlozenyZakaznik);
                System.out.println("INSERT: " + vlozenyZakaznik + " Na adrese: " + adresaVlozenej);
                this.kontrolneData.put(vlozenyZakaznik, adresaVlozenej);
                if (adresaVlozenej != -1) {
//                    this.vypisheapFile();
                    this.kontrolneData.put(vlozenyZakaznik, adresaVlozenej);
                } else {
                    System.out.println("asdasda");
                }

//                this.vypisheapFile();


            } else if (generovanaHodnota < 0.66) {
                if (!this.kontrolneData.isEmpty()) {
                    int mazanyindex = this.random.nextInt(this.kontrolneData.keySet().size());
                    ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                    this.heapFile.delete(this.kontrolneData.get(keys.get(mazanyindex)), (Zakaznik)keys.get(mazanyindex));
                    System.out.println("DELETE: " + keys.get(mazanyindex) + "Na adrese: " + this.kontrolneData.get(keys.get(mazanyindex)));
//                    this.vypisheapFile();
                    this.kontrolneData.remove((Zakaznik) keys.get(mazanyindex),this.kontrolneData.get(keys.get(mazanyindex)));
                }

            } else if (generovanaHodnota > 0.66) {
                if (!this.kontrolneData.isEmpty()) {
                    int hladanyIndex = this.random.nextInt(this.kontrolneData.keySet().size());
                    ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                    Zakaznik hladanyZakaznik = (Zakaznik) keys.get(hladanyIndex);
                    long hladanaAdresa = this.kontrolneData.get(keys.get(hladanyIndex));
//                    this.vypisheapFile();
                    System.out.println("Hladana adresa: " + hladanaAdresa);
                    System.out.println("Hladana osoba" + hladanyZakaznik.toString());
                    Zakaznik najdenyZakaznik = (Zakaznik) this.heapFile.get(hladanaAdresa, hladanyZakaznik);
                    System.out.println("Najdena osoba: " + najdenyZakaznik.toString());
                    if (!najdenyZakaznik.myEquals(hladanyZakaznik)) {
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
        this.heapFile.closeHeapFile();

    }

    private void vypisheapFile() {

        this.heapFile.printBlocks(new Zakaznik("Jano", "Hladac", 665, new Navsteva(Calendar.getInstance(), 10), "ASDADSD"));
    }


    private void vymazNPrvkov(int pocetPrvkov) {
        for (int i = 0; i < pocetPrvkov; i++) {
            if (!this.kontrolneData.keySet().isEmpty()) {
                int mazanyindex = this.random.nextInt(this.kontrolneData.keySet().size());
                ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                this.heapFile.delete(this.kontrolneData.get(keys.get(mazanyindex)), (Zakaznik)keys.get(mazanyindex));
                System.out.println("DELETE: " + (Zakaznik)keys.get(mazanyindex) + "Na adrese: " + this.kontrolneData.get(keys.get(mazanyindex)));
//                    this.vypisheapFile();
                this.kontrolneData.remove((Zakaznik) keys.get(mazanyindex),this.kontrolneData.get(keys.get(mazanyindex)));
            }
        }

    }

    private void skontrolujHeapFile() {
        ArrayList keys = new ArrayList(this.kontrolneData.keySet());
        int hladane = 0;
        int najdene = 0;

        for (Zakaznik zakaznik : kontrolneData.keySet()) {
            System.out.println("Hladana osoba " + zakaznik.toString() + " na adrese: " + this.kontrolneData.get(zakaznik));
            if (zakaznik.myEquals((Zakaznik) this.heapFile.get(this.kontrolneData.get(zakaznik), zakaznik))) {
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
