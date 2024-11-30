package sk.uniza.fri.aplikacia;

import sk.uniza.fri.data.Zakaznik;
import sk.uniza.fri.hashfile.HashFile;
import sk.uniza.fri.heapfile.HeapFile;

import java.util.ArrayList;
import java.util.Calendar;

public class AppCore {
    private HashFile hashFileID;
    private HashFile hashFileECV;
    private HeapFile dataStorage;

    AppCore(String filePath, int blockSize) {
        this.dataStorage = new HeapFile<Zakaznik>(filePath, blockSize);
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




}
