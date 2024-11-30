package sk.uniza.fri.aplikacia;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.heapfile.HeapFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AppCore {
    private HashFile hashFileID;
    private HashFile hashFileECV;
    private HeapFile heapFileStorage;
    private NahodnyGenerator nahodnyGen;
    private Random random;

    public AppCore(String filePath, int blockSize) {
        this.heapFileStorage = new HeapFile<Zakaznik>(filePath, blockSize);
        this.nahodnyGen = new NahodnyGenerator();
    }


    public Zakaznik vyhladajUdajeOVozidle(Object parameterVyhladania) {
        if (parameterVyhladania instanceof String) {

        } else {

        }
        return null;
    }

    public void pridajVozidlo(String paMeno, String paPriezvisko, int paID, String paECV) {

    }

    public void pridajNavstevu(Object parameterVyhladania, Calendar paDatum, double paCena, ArrayList<String> prace) {

    }

    public void zmenUdaje() {

    }

    public void vygenerujNZakaznikov(int pocetVygenerovanychZakaznikov) {
        for (int i = 0; i < pocetVygenerovanychZakaznikov; i++) {

            Zakaznik vlozenyZakaznik = new Zakaznik(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                    this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vugenerujUnikatnyInt(), new Navsteva(Calendar.getInstance(), this.random.nextDouble()), this.nahodnyGen.vygenerujUnikatnyString(0,10));

            long adresaVlozenej = this.heapFileStorage.insert(vlozenyZakaznik);
            System.out.println("INSERT: " + vlozenyZakaznik + " Na adrese: " + adresaVlozenej);
        }
    }

    public ArrayList dajVsetkyBloky() {
        return this.heapFileStorage.getAllBlocks(new Zakaznik("Jano", "Hladac", 665, new Navsteva(Calendar.getInstance(), 10), "ASDADSD"));
    }




}
