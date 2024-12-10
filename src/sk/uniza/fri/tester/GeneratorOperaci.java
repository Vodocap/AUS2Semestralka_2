package sk.uniza.fri.tester;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.SearchZakaznikECV;
import sk.uniza.fri.data.SearchZakaznikID;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.heapfile.HeapFile;

import java.time.LocalDate;
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
    private HashFile hashFile;
    private HashMap<Zakaznik, Long> kontrolneData;
    private ArrayList<SearchZakaznikID> kontrolneHashID;
    private ArrayList<SearchZakaznikECV> kontrolneHashECV;
    private Random random;
    private NahodnyGenerator nahodnyGen;
    public GeneratorOperaci(String cestakSuboru, int velkostBlokov) {
        this.heapFile = new HeapFile<>(cestakSuboru, velkostBlokov);
        this.hashFile = new HashFile<>("hsh.bin", 70);
        this.kontrolneData = new HashMap<>();
        this.random = new Random();
        this.nahodnyGen = new NahodnyGenerator(262223);
        this.kontrolneHashID = new ArrayList();
        this.kontrolneHashECV = new ArrayList();
    }

    public void generujOperacieHeapFile(int pocetOperacii) {
        for (int i = 0; i < 1000; i++) {
            Zakaznik vlozenyZakaznik = new Zakaznik(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                    this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vygenerujUnikatneID(), new Navsteva(LocalDate.now(), this.random.nextDouble()), this.nahodnyGen.vygenerujECV());

            long adresaVlozenej = this.heapFile.insert(vlozenyZakaznik);
            System.out.println("INSERT: " + vlozenyZakaznik + " Na adrese: " + adresaVlozenej);
            if (adresaVlozenej != -1) {
                this.kontrolneData.put(vlozenyZakaznik, adresaVlozenej);
            } else {
                System.out.println("HEAPFILE BOL UZ PLNY");
            }
        }


        for (int i = 0; i < pocetOperacii; i++) {

            double generovanaHodnota = this.random.nextDouble();
            if (generovanaHodnota < 0.33) {
                Zakaznik vlozenyZakaznik = new Zakaznik(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                        this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vygenerujUnikatnyInt(), new Navsteva(LocalDate.now(), this.random.nextDouble()), this.nahodnyGen.vygenerujECV());

                long adresaVlozenej = this.heapFile.insert(vlozenyZakaznik);
                System.out.println("INSERT: " + vlozenyZakaznik + " Na adrese: " + adresaVlozenej);
                this.kontrolneData.put(vlozenyZakaznik, adresaVlozenej);
                if (adresaVlozenej != -1) {
                    this.kontrolneData.put(vlozenyZakaznik, adresaVlozenej);
                    this.skontrolujHeapFile();
                } else {
                    System.out.println("HEAPFILE BOL UZ PLNY");
                }

//                this.vypisheapFile();


            } else if (generovanaHodnota < 0.66) {
                if (!this.kontrolneData.isEmpty()) {
                    int mazanyindex = this.random.nextInt(this.kontrolneData.keySet().size());
                    ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                    this.heapFile.delete(this.kontrolneData.get(keys.get(mazanyindex)), (Zakaznik)keys.get(mazanyindex));
                    System.out.println("DELETE: " + keys.get(mazanyindex) + "Na adrese: " + this.kontrolneData.get(keys.get(mazanyindex)));
                    this.kontrolneData.remove((Zakaznik) keys.get(mazanyindex),this.kontrolneData.get(keys.get(mazanyindex)));
                    this.skontrolujHeapFile();
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
        this.heapFile.closeHeapFile("hfSave.bin");

    }

    private void vypisheapFile() {

        this.heapFile.printBlocks(new Zakaznik("Jano", "Hladac", 665, new Navsteva(LocalDate.now(), 10), "ASDADSD"));

    }


    private void vymazNPrvkov(int pocetPrvkov) {
        for (int i = 0; i < pocetPrvkov; i++) {
            if (!this.kontrolneData.keySet().isEmpty()) {
                int mazanyindex = this.random.nextInt(this.kontrolneData.keySet().size());
                ArrayList keys = new ArrayList(this.kontrolneData.keySet());
                this.heapFile.delete(this.kontrolneData.get(keys.get(mazanyindex)), (Zakaznik)keys.get(mazanyindex));
                System.out.println("DELETE: " + keys.get(mazanyindex) + "Na adrese: " + this.kontrolneData.get(keys.get(mazanyindex)));

                this.kontrolneData.remove((Zakaznik) keys.get(mazanyindex),this.kontrolneData.get(keys.get(mazanyindex)));
            }
        }

    }

    private void skontrolujHeapFile() {
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

    private void skontrolujHashFile(boolean iDOrECV) {

        int hladane = 0;
        int najdene = 0;

        if (iDOrECV) {
            for (SearchZakaznikID zakaznik : this.kontrolneHashID) {
                System.out.println("Hladana osoba " + zakaznik.toString());
                if (zakaznik.myEquals((SearchZakaznikID) this.hashFile.get(zakaznik))) {
                    najdene++;
                }
                hladane++;
            }
            System.out.println("Najdene: " + najdene + " hladane: " + hladane);
            if (hladane != najdene) {
                throw new RuntimeException("Nenaslo sa co sa malo");
            }
        } else {
            for (SearchZakaznikECV zakaznik : this.kontrolneHashECV) {
                System.out.println("Hladana osoba " + zakaznik.toString());
                if (zakaznik.myEquals((SearchZakaznikECV) this.hashFile.get(zakaznik))) {
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


    public void generujOperacieHash(int pocetOperacii, boolean iDOrECV) {
//        for (int i = 0; i < 1000; i++) {
//            Zakaznik vlozenyZakaznik = new Zakaznik(this.nahodnyGen.vygenerujUnikatnyString(0,15),
//                    this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vygenerujUnikatneID(), new Navsteva(Calendar.getInstance(), this.random.nextDouble()), this.nahodnyGen.vygenerujECV());
//
//            this.hashFile.insert(vlozenyZakaznik, vlozenyZakaznik.getID());
//            this.kontrolneHash.add(vlozenyZakaznik);
//        }


        if (iDOrECV) {
            for (int i = 0; i < pocetOperacii; i++) {

                double generovanaHodnota = this.random.nextDouble();

                if (generovanaHodnota < 0.50) {



                    SearchZakaznikID searchZakaznikID = new SearchZakaznikID(this.nahodnyGen.vygenerujUnikatneID());
                    searchZakaznikID.setECV(this.nahodnyGen.vygenerujECV());
                    this.hashFile.insert(searchZakaznikID);
                    System.out.println("INSERT: " + searchZakaznikID);
                    this.kontrolneHashID.add(searchZakaznikID);

//                this.vypisheapFile();


                } else if (generovanaHodnota > 0.50) {
                    if (!this.kontrolneHashID.isEmpty()) {
                        int hladanyIndex = this.random.nextInt(this.kontrolneHashID.size());
                        SearchZakaznikID hladanyZakaznik = this.kontrolneHashID.get(hladanyIndex);
//                    this.vypisheapFile();
                        System.out.println("Hladana osoba" + hladanyZakaznik.toString());
                        SearchZakaznikID najdenyZakaznik = (SearchZakaznikID) this.hashFile.get(hladanyZakaznik);
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

            this.hashFile.printBlocks(new SearchZakaznikID(10));
            this.skontrolujHashFile(iDOrECV);

        } else {
            for (int i = 0; i < pocetOperacii; i++) {

                double generovanaHodnota = this.random.nextDouble();

                if (generovanaHodnota < 0.50) {



                    SearchZakaznikECV searchZakaznikECV = new SearchZakaznikECV(this.nahodnyGen.vygenerujECV());
                    searchZakaznikECV.setID(this.nahodnyGen.vygenerujUnikatneID());
                    this.hashFile.insert(searchZakaznikECV);
                    System.out.println("INSERT: " + searchZakaznikECV);
                    this.kontrolneHashECV.add(searchZakaznikECV);

//                this.vypisheapFile();


                } else if (generovanaHodnota > 0.50) {
                    if (!this.kontrolneHashECV.isEmpty()) {
                        int hladanyIndex = this.random.nextInt(this.kontrolneHashECV.size());
                        SearchZakaznikECV hladanyZakaznik = this.kontrolneHashECV.get(hladanyIndex);
//                    this.vypisheapFile();
                        System.out.println("Hladana osoba" + hladanyZakaznik.toString());
                        SearchZakaznikECV najdenyZakaznik = (SearchZakaznikECV) this.hashFile.get(hladanyZakaznik);
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

            this.hashFile.printBlocks(new SearchZakaznikECV("bigchungus"));
            this.skontrolujHashFile(iDOrECV);
        }


//        this.heapFile.closeHeapFile("");

    }

}
