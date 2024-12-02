package sk.uniza.fri.aplikacia;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.SearchZakaznikECV;
import sk.uniza.fri.data.SearchZakaznikID;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.heapfile.HeapFile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AppCore {
    private HashFile<SearchZakaznikID> hashFileID;
    private HashFile<SearchZakaznikECV> hashFileECV;
    private HeapFile<Zakaznik> heapFileStorage;
    private NahodnyGenerator nahodnyGen;
    private Random random;

    public AppCore(String mainStorageFilePath, int blockSize) {
        this.heapFileStorage = new HeapFile<Zakaznik>(mainStorageFilePath, blockSize);
        this.hashFileID = new HashFile("id.bin", blockSize);
        this.hashFileECV = new HashFile("ecv.bin", blockSize);
        this.nahodnyGen = new NahodnyGenerator();
        this.random = new Random();
    }


    private Object vratSearchZakaznika(Object parameterVyhladania) {
        if (parameterVyhladania instanceof String) {
            SearchZakaznikECV dummyZakaznik = new SearchZakaznikECV((String) parameterVyhladania);
            dummyZakaznik = (SearchZakaznikECV) this.hashFileECV.get((String)parameterVyhladania, dummyZakaznik.createInstance());

            return dummyZakaznik;

        } else {
            SearchZakaznikID dummyZakaznik = new SearchZakaznikID((int) parameterVyhladania);
            dummyZakaznik = (SearchZakaznikID)this.hashFileID.get((int)parameterVyhladania, dummyZakaznik.createInstance());

            return dummyZakaznik;

        }
    }

    public Zakaznik vyhladajUdajeOVozidle(Object parameterVyhladania) {

        if (parameterVyhladania instanceof String) {

            SearchZakaznikECV dummyZakaznik = (SearchZakaznikECV)this.vratSearchZakaznika(parameterVyhladania);

            Zakaznik hladaciZakaznik = new Zakaznik("Hladaci", "Zakaznik", dummyZakaznik.getID(), new Navsteva(Calendar.getInstance(), 10), dummyZakaznik.getECV());
            return (Zakaznik) this.heapFileStorage.get(dummyZakaznik.getAdresa(), hladaciZakaznik).createInstance();

        } else {
            SearchZakaznikID dummyZakaznik = (SearchZakaznikID)this.vratSearchZakaznika(parameterVyhladania);

            Zakaznik hladaciZakaznik = new Zakaznik("Hladaci", "Zakaznik", dummyZakaznik.getID(), new Navsteva(Calendar.getInstance(), 10), dummyZakaznik.getECV());
            return (Zakaznik) this.heapFileStorage.get(dummyZakaznik.getAdresa(), hladaciZakaznik).createInstance();

        }

    }

    public void pridajVozidlo(String paMeno, String paPriezvisko, int paID, String paECV) {
        Zakaznik pridavanyZakaznik = new Zakaznik(paMeno, paPriezvisko, paID,new Navsteva(Calendar.getInstance(), 20),paECV);
        SearchZakaznikID pridavanyID = new SearchZakaznikID(paID);
        SearchZakaznikECV pridavanyECV = new SearchZakaznikECV(paECV);
        pridavanyECV.setID(paID);
        pridavanyID.setECV(paECV);
        long pridanaAdresa = this.heapFileStorage.insert(pridavanyZakaznik);
        pridavanyID.setAdresa(pridanaAdresa);
        pridavanyECV.setAdresa(pridanaAdresa);
        this.hashFileID.insert(pridavanyID, pridavanyID.getID());
        this.hashFileECV.insert(pridavanyECV, pridavanyECV.getECV());

    }

    public void pridajVozidlo(Zakaznik zakaznik) {

        SearchZakaznikID pridavanyID = new SearchZakaznikID(zakaznik.getID());
        SearchZakaznikECV pridavanyECV = new SearchZakaznikECV(zakaznik.getECV());
        pridavanyECV.setID(zakaznik.getID());
        pridavanyID.setECV(zakaznik.getECV());
        long pridanaAdresa = this.heapFileStorage.insert(zakaznik.createInstance());
        pridavanyID.setAdresa(pridanaAdresa);
        pridavanyECV.setAdresa(pridanaAdresa);
        this.hashFileID.insert(pridavanyID, pridavanyID.getID());
        this.hashFileECV.insert(pridavanyECV, pridavanyECV.getECV());

    }

    public boolean pridajNavstevu(Object parameterVyhladania, Calendar paDatum, double paCena, ArrayList<String> prace) {
        Zakaznik pridavaneDoZakaznika = this.vyhladajUdajeOVozidle(parameterVyhladania);
        Navsteva pridavanaNavsteva = new Navsteva(paDatum, paCena);
        for (String s : prace) {
            pridavanaNavsteva.addPRaca(s);
        }
        return pridavaneDoZakaznika.addZaznam(pridavanaNavsteva);
    }

    public void zmenVozidlo(Object parameterVyhladania, Zakaznik noveVozidlo) {
        Zakaznik zmeneneVozidlo = this.vyhladajUdajeOVozidle(parameterVyhladania);
        SearchZakaznikECV searchZakaznikECV = (SearchZakaznikECV) this.vratSearchZakaznika(parameterVyhladania);
        this.heapFileStorage.update(searchZakaznikECV.getAdresa(), zmeneneVozidlo.createInstance(), noveVozidlo.createInstance());
    }

    public void zmenNavstevu(Zakaznik zakaznik, Navsteva novaNavsteva) {

    }

    public void zmenPracu(String novaPraca) {

    }

    public void vygenerujNZakaznikov(int pocetVygenerovanychZakaznikov) {
        for (int i = 0; i < pocetVygenerovanychZakaznikov; i++) {

            this.pridajVozidlo(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                    this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vygenerujUnikatnyInt(), this.nahodnyGen.vygenerujUnikatnyString(10));


        }
    }

    public ArrayList dajVsetkyBloky(int typ) {
        return switch (typ) {
            case 0 ->
                    this.heapFileStorage.getAllBlocks(new Zakaznik("Jano", "Hladac", 665, new Navsteva(Calendar.getInstance(), 10), "ASDADSD"));
            case 1 ->
                    this.hashFileID.getAllBlocks(new SearchZakaznikID(10000));
            case 2 ->
                    this.hashFileECV.getAllBlocks(new SearchZakaznikECV("10000"));
            default -> null;
        };
    }




}
