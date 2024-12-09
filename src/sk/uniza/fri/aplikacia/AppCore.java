package sk.uniza.fri.aplikacia;

import sk.uniza.fri.data.NahodnyGenerator;
import sk.uniza.fri.data.Navsteva;
import sk.uniza.fri.data.SearchZakaznikECV;
import sk.uniza.fri.data.SearchZakaznikID;
import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.heapfile.HeapFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class AppCore {
    private HashFile<SearchZakaznikID> hashFileID;
    private HashFile<SearchZakaznikECV> hashFileECV;
    private HeapFile<Zakaznik> heapFileStorage;
    private NahodnyGenerator nahodnyGen;

    public AppCore(String mainStorageFilePath, int blockSizeHeapFile, int blocksizeHashFiles, int zaciatokIdCislovania) {
        this.heapFileStorage = new HeapFile<Zakaznik>(mainStorageFilePath, blockSizeHeapFile);
        this.hashFileID = new HashFile("id.bin", blocksizeHashFiles);
        this.hashFileECV = new HashFile("ecv.bin", blocksizeHashFiles);
        this.nahodnyGen = new NahodnyGenerator(zaciatokIdCislovania);
    }


    public Object vratSearchZakaznika(Object parameterVyhladania) {
        if (parameterVyhladania instanceof String) {
            SearchZakaznikECV dummyZakaznik = new SearchZakaznikECV((String) parameterVyhladania);
            dummyZakaznik = this.hashFileECV.get(dummyZakaznik.createInstance());

            return dummyZakaznik;

        } else {
            SearchZakaznikID dummyZakaznik = new SearchZakaznikID((int) parameterVyhladania);
            dummyZakaznik = this.hashFileID.get(dummyZakaznik.createInstance());

            return dummyZakaznik;

        }
    }

    public Zakaznik vyhladajUdajeOVozidle(Object parameterVyhladania) {

        if (parameterVyhladania instanceof String) {

            SearchZakaznikECV dummyZakaznik = (SearchZakaznikECV)this.vratSearchZakaznika(parameterVyhladania);

            Zakaznik hladaciZakaznik = new Zakaznik("Hladaci", "Zakaznik", dummyZakaznik.getID(), new Navsteva(LocalDate.now(), 10), dummyZakaznik.getECV());
            System.out.println(hladaciZakaznik);
            System.out.println(this.heapFileStorage.get(dummyZakaznik.getAdresa(), hladaciZakaznik).createInstance());
            return this.heapFileStorage.get(dummyZakaznik.getAdresa(), hladaciZakaznik).createInstance();

        } else {
            SearchZakaznikID dummyZakaznik = (SearchZakaznikID)this.vratSearchZakaznika(parameterVyhladania);

            Zakaznik hladaciZakaznik = new Zakaznik("Hladaci", "Zakaznik", dummyZakaznik.getID(), new Navsteva(LocalDate.now(), 10), dummyZakaznik.getECV());
            System.out.println(hladaciZakaznik);
            System.out.println(this.heapFileStorage.get(dummyZakaznik.getAdresa(), hladaciZakaznik).createInstance());
            return this.heapFileStorage.get(dummyZakaznik.getAdresa(), hladaciZakaznik).createInstance();


        }

    }

    public void pridajVozidlo(String paMeno, String paPriezvisko, int paID, String paECV) {
        Zakaznik pridavanyZakaznik = new Zakaznik(paMeno, paPriezvisko, paID,new Navsteva(LocalDate.now(), 20),paECV);
        System.out.println("Pridany novy zagaznik: " + pridavanyZakaznik.toString());
        SearchZakaznikID pridavanyID = new SearchZakaznikID(paID);
        SearchZakaznikECV pridavanyECV = new SearchZakaznikECV(paECV);
        pridavanyECV.setID(paID);
        pridavanyID.setECV(paECV);
        long pridanaAdresa = this.heapFileStorage.insert(pridavanyZakaznik);
        pridavanyID.setAdresa(pridanaAdresa);
        pridavanyECV.setAdresa(pridanaAdresa);
        this.hashFileID.insert(pridavanyID);
        this.hashFileECV.insert(pridavanyECV);

    }

    public void pridajVozidlo(Zakaznik zakaznik) {

        SearchZakaznikID pridavanyID = new SearchZakaznikID(zakaznik.getID());
        SearchZakaznikECV pridavanyECV = new SearchZakaznikECV(zakaznik.getECV());
        pridavanyECV.setID(zakaznik.getID());
        pridavanyID.setECV(zakaznik.getECV());
        long pridanaAdresa = this.heapFileStorage.insert(zakaznik.createInstance());
        pridavanyID.setAdresa(pridanaAdresa);
        pridavanyECV.setAdresa(pridanaAdresa);
        this.hashFileID.insert(pridavanyID);
        this.hashFileECV.insert(pridavanyECV);

    }

    public boolean pridajNavstevu(Object parameterVyhladania, LocalDate paDatum, double paCena, ArrayList<String> prace) {
        Zakaznik pridavaneDoZakaznika = this.vyhladajUdajeOVozidle(parameterVyhladania);
        Navsteva pridavanaNavsteva = new Navsteva(paDatum, paCena);
        for (String s : prace) {
            pridavanaNavsteva.addPRaca(s);
        }
        return pridavaneDoZakaznika.addZaznam(pridavanaNavsteva);
    }

    public void zmenVozidlo(Object parameterVyhladania, Zakaznik noveVozidlo) {

        Zakaznik zmeneneVozidlo = this.vyhladajUdajeOVozidle(parameterVyhladania);
        if (parameterVyhladania instanceof String) {
            SearchZakaznikECV dummyZakaznik = new SearchZakaznikECV((String) parameterVyhladania);
            dummyZakaznik = this.hashFileECV.get(dummyZakaznik);
            System.out.println(zmeneneVozidlo);
            System.out.println(noveVozidlo);
            this.heapFileStorage.update(dummyZakaznik.getAdresa(), zmeneneVozidlo, noveVozidlo.createInstance());

        } else {
            SearchZakaznikID dummyZakaznik = new SearchZakaznikID((int) parameterVyhladania);
            dummyZakaznik = this.hashFileID.get(dummyZakaznik);
            System.out.println(zmeneneVozidlo);
            System.out.println(noveVozidlo);
            this.heapFileStorage.update(dummyZakaznik.getAdresa(), zmeneneVozidlo, noveVozidlo.createInstance());


        }

    }

    public long[] dajAdresar(int iDOrECV) {
        if (iDOrECV == 1) {
            return Arrays.copyOf(this.hashFileID.getAddreses(), this.hashFileID.getAddreses().length);
        } else {
            return Arrays.copyOf(this.hashFileECV.getAddreses(), this.hashFileECV.getAddreses().length);
        }
    }

    public void vygenerujNZakaznikov(int pocetVygenerovanychZakaznikov) {
        for (int i = 0; i < pocetVygenerovanychZakaznikov; i++) {

            this.pridajVozidlo(this.nahodnyGen.vygenerujUnikatnyString(0,15),
                    this.nahodnyGen.vygenerujUnikatnyString(0,20), this.nahodnyGen.vygenerujUnikatneID(), this.nahodnyGen.vygenerujECV());

        }
    }

    public ArrayList dajVsetkyBloky(int typ) {
        return switch (typ) {
            case 0 ->
                    this.heapFileStorage.getAllBlocks(new Zakaznik("Jano", "Hladac", 665, new Navsteva(LocalDate.now(), 10), "ASDADSD"));
            case 1 ->
                    this.hashFileID.getAllBlocks(new SearchZakaznikID(10000));
            case 2 ->
                    this.hashFileECV.getAllBlocks(new SearchZakaznikECV("10000"));
            default -> null;
        };
    }

    public void ulozAplikaciu(String heapfileUkladaciSubor) {
        this.heapFileStorage.closeHeapFile(heapfileUkladaciSubor);
        this.hashFileID.saveHashFileIntoFile("idatsave.bin", "idadsave.bin");
        this.hashFileECV.saveHashFileIntoFile("ecvatsave.bin", "ecvadsave.bin");
        try {
            RandomAccessFile numberSaver = new RandomAccessFile("idend.bin", "rw");
            numberSaver.seek(0);
            numberSaver.write(this.toByteArray());
            numberSaver.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void nacitajAplikaciu(String heapfileNacitavaciSubor) {
        this.heapFileStorage.initialiseHeapFileFromFile(heapfileNacitavaciSubor);
        this.hashFileID.initialiseHashFileFromFile("idatsave.bin", "idadsave.bin");
        this.hashFileECV.initialiseHashFileFromFile("ecvatsave.bin", "ecvadsave.bin");
        try {
            RandomAccessFile numberSaver = new RandomAccessFile("idend.bin", "rw");
            numberSaver.seek(0);
            byte[] numberBytes = new byte[4];
            numberSaver.read(numberBytes);
            this.fromByteArray(numberBytes);
            numberSaver.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            hlpOutStream.writeInt(this.nahodnyGen.getCurrentID());


            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    private void fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            this.nahodnyGen.setCurrentID(hlpInStream.readInt());


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }




}
