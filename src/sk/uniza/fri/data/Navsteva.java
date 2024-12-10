package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */

public class Navsteva implements IData<Navsteva> {
    private LocalDate datum;
    private double cena;
    private ArrayList<String> vykonanePrace;
    private static int MAX_PRAC = 10;
    private static int MAX_LENGHT_PRACE = 20;
    private int pocetPlatnychPrac;

    public Navsteva(LocalDate paDatum, double paCena) {
        this.datum = paDatum;
        this.cena = paCena;
        this.vykonanePrace = new ArrayList<>();
        this.pocetPlatnychPrac = 0;
    }

    public void addPRaca(String novaPraca) {
        if (this.vykonanePrace.size() < MAX_PRAC) {
            this.vykonanePrace.add(novaPraca);
            this.pocetPlatnychPrac++;
        }

    }



    public void removePraca(String odstranovanaPraca) {
        this.vykonanePrace.remove(odstranovanaPraca);
    }

    @Override
    public long getSize() {
        return 260;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {



            hlpOutStream.writeLong(this.datum.getLong(ChronoField.EPOCH_DAY));

            hlpOutStream.writeDouble(this.cena);
            hlpOutStream.writeInt(this.pocetPlatnychPrac);

            int recordsBytes = 0;

            for (String praca : this.vykonanePrace) {

                int validLength = praca.length();
                if (praca.length() <= MAX_LENGHT_PRACE) {
                    for (int i = praca.length(); i < MAX_LENGHT_PRACE; i++) {
                        praca += "x";
                    }

                }
                recordsBytes += 24;

                hlpOutStream.writeInt(validLength);
                hlpOutStream.write(praca.getBytes());

            }

            byte[] emptyArrray = new byte[(int)this.getSize() - recordsBytes - 20];

            hlpOutStream.write(emptyArrray);


            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    @Override
    public void fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {



            this.datum = LocalDate.ofEpochDay(hlpInStream.readLong());
            this.cena = hlpInStream.readDouble();
            this.pocetPlatnychPrac = hlpInStream.readInt();

            for (int i = 0; i < this.pocetPlatnychPrac; i++) {

                int pracaSize = hlpInStream.readInt();
                this.vykonanePrace.add(new String(hlpInStream.readNBytes(pracaSize)));
                hlpInStream.skipBytes(MAX_LENGHT_PRACE - pracaSize);

            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    @Override
    public boolean myEquals(Navsteva data) {
        return data.getCena() == this.cena && data.getPocetPlatnychPrac() == this.getPocetPlatnychPrac() &&
                this.datum.equals(data.getDatum()) && data.getVykonanePrace().equals(this.vykonanePrace);
    }

    @Override
    public Navsteva createInstance() {
        Navsteva navstevaCopy = new Navsteva(this.datum, this.cena );
        for (String praca : vykonanePrace) {
            navstevaCopy.addPRaca(praca);
        }
        return navstevaCopy;
    }

    @Override
    public void printData() {
        System.out.println(this.toString());
    }


    public double getCena() {
        return this.cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public LocalDate getDatum() {
        return this.datum;
    }

    public void setDatum(LocalDate calendar) {
        this.datum = calendar;
    }

    public ArrayList<String> getVykonanePrace() {
        return this.vykonanePrace;
    }


    public int getPocetPlatnychPrac() {
        return this.pocetPlatnychPrac;
    }

    @Override
    public String toString() {
        return "Navsteva{\n" +
                "datum=" + this.datum.toString() +
                "\n, cena=" + cena +
                "\n, vykonanePrace=" + vykonanePrace +
                "\n, pocetPlatnychPrac=" + pocetPlatnychPrac +
                '}';
    }
}
