package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */

public class Navsteva implements IData<Navsteva> {
    private Calendar calendar;
    private double cena;
    private ArrayList<String> vykonanePrace;
    private static int MAX_PRAC = 10;
    private static int MAX_LENGHT_PRACE = 20;
    private int pocetPlatnychPrac;

    public Navsteva(Calendar paDatum, double paCena) {
        this.calendar = paDatum;
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
        return 264;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            int den = this.calendar.get(Calendar.DAY_OF_MONTH);
            int mesiac = this.calendar.get(Calendar.MONTH);
            int rok = this.calendar.get(Calendar.YEAR);


            hlpOutStream.writeInt(den);
            hlpOutStream.writeInt(mesiac);
            hlpOutStream.writeInt(rok);
            hlpOutStream.writeDouble(this.cena);
            hlpOutStream.writeInt(this.pocetPlatnychPrac);

            int recordsBytes = 0;

            for (String praca : this.vykonanePrace) {
                String writtenPraca = praca;

                if (praca.length() <= MAX_LENGHT_PRACE) {
                    for (int i = praca.length(); i < MAX_LENGHT_PRACE; i++) {
                        praca += "x";
                    }

                }
                recordsBytes += 24;
                hlpOutStream.writeInt(praca.length());
                hlpOutStream.write(praca.getBytes());

            }

            byte[] emptyArrray = new byte[(int)this.getSize() - recordsBytes - 24];

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


            int den = hlpInStream.readInt();
            int mesiac = hlpInStream.readInt();
            int rok = hlpInStream.readInt();
            this.calendar.set(Calendar.DAY_OF_MONTH, den);
            this.calendar.set(Calendar.MONTH, mesiac);
            this.calendar.set(Calendar.YEAR, rok);
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
                this.calendar.equals(data.getCalendar()) && data.getVykonanePrace().equals(this.vykonanePrace);
    }

    @Override
    public Navsteva createInstance() {
        Navsteva navstevaCopy = new Navsteva(this.calendar, this.cena );
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

    public Calendar getCalendar() {
        return this.calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public ArrayList<String> getVykonanePrace() {
        return this.vykonanePrace;
    }

    public void setVykonanePrace(ArrayList<String> vykonanePrace) {
        this.vykonanePrace = vykonanePrace;
    }

    public int getPocetPlatnychPrac() {
        return this.pocetPlatnychPrac;
    }

    public void setPocetPlatnychPrac(int pocetPlatnychPrac) {
        this.pocetPlatnychPrac = pocetPlatnychPrac;
    }

    @Override
    public String toString() {
        return "Navsteva{" +
                "datum=" + this.calendar.get(Calendar.DAY_OF_MONTH) + " - " + this.calendar.get(Calendar.MONTH) + " - " + this.calendar.get(Calendar.YEAR) +
                " , cena=" + cena +
                ", vykonanePrace=" + vykonanePrace +
                ", pocetPlatnychPrac=" + pocetPlatnychPrac +
                '}';
    }
}
