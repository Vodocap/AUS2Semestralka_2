package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IData;
import sk.uniza.fri.tester.ZaznamTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class Zakaznik implements IData<Zakaznik> {


    private String meno;
    private String priezvisko;
    private String ECV;
    private static int MENO_LENGHT = 15;
    private static int PRIEZVISKO_LENGHT = 20;
    private static int ECV_LENGTH = 10;
    private static int MAX_ZAZNAMOV = 5;
    private int ID;
    private ArrayList<Navsteva> zaznamyONasvsteve;
    private int pocetPlatnychNavstev;
    private int size;
    private Navsteva vytvaracInstancii;

    public Zakaznik(String paMeno, String paPriezvisko, int paID, Navsteva paVytvaracInstancii, String paEcv) {
        this.vytvaracInstancii = paVytvaracInstancii;
        this.zaznamyONasvsteve = new ArrayList<>();
        this.ECV = paEcv;
        this.meno = paMeno;
        this.priezvisko = paPriezvisko;
        this.size = 1393;
        this.ID = paID;
        this.pocetPlatnychNavstev = 0;

    }

    public void addZaznam(Navsteva paNavsteva) {
        if (this.zaznamyONasvsteve.size() < MAX_ZAZNAMOV) {
            this.zaznamyONasvsteve.add(paNavsteva);
            this.pocetPlatnychNavstev--;
        } else {
            System.out.println("Viac navstev uz je zakazanych");
        }
    }

    public void removeZaznam(Navsteva paData) {
        if (!this.zaznamyONasvsteve.isEmpty()) {
            for (int i = 0; i < this.zaznamyONasvsteve.size(); i++) {
                if (this.zaznamyONasvsteve.get(i).myEquals(paData)) {
                    this.zaznamyONasvsteve.remove(i);
                    this.pocetPlatnychNavstev--;
                    break;
                }

            }
        }

    }

    public String getMeno() {
        return this.meno;
    }

    public void setMeno(String meno) {
        this.meno = meno;
    }

    public String getPriezvisko() {
        return this.priezvisko;
    }

    public void setPriezvisko(String priezvisko) {
        this.priezvisko = priezvisko;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public boolean myEquals(Zakaznik data) {
        return this.ID == data.getID() && this.meno.equals(data.getMeno()) && this.priezvisko.equals(data.getPriezvisko());
    }


    @Override
    public Zakaznik createInstance() {
        Zakaznik zakaznikCopy = new Zakaznik(this.meno, this.priezvisko, this.ID, this.vytvaracInstancii, this.ECV);
        for (Navsteva t : this.zaznamyONasvsteve) {
            zakaznikCopy.addZaznam(t.createInstance());
        }
        return zakaznikCopy;
    }

    @Override
    public void print() {
        System.out.println("Meno: " + this.meno);
        System.out.println("Priezvisko: " + this.priezvisko);
        System.out.println("ID: " + this.ID);
        System.out.println("Size: " + this.size);
        System.out.println("ECV: " + this.ECV);
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            String writtenMeno = this.meno;
            if (this.meno.length() <= MENO_LENGHT) {
                for (int i = this.meno.length(); i < MENO_LENGHT; i++) {
                    writtenMeno += "x";
                }
            }
            String writtenPriezvisko = this.priezvisko;
            if (this.priezvisko.length() <= PRIEZVISKO_LENGHT) {
                for (int i = this.priezvisko.length(); i < PRIEZVISKO_LENGHT; i++) {
                    writtenPriezvisko += "x";
                }
            }
            String writtenECV = this.ECV;
            if (this.ECV.length() <= ECV_LENGTH) {
                for (int i = this.ECV.length(); i < ECV_LENGTH; i++) {
                    writtenECV += "x";
                }
            }


            hlpOutStream.writeInt(this.size);
            hlpOutStream.writeInt(this.ID);
            hlpOutStream.writeInt(this.pocetPlatnychNavstev);
            hlpOutStream.writeInt(this.meno.length());
            hlpOutStream.write(writtenMeno.getBytes());
            hlpOutStream.writeInt(this.priezvisko.length());
            hlpOutStream.write(writtenPriezvisko.getBytes());
            hlpOutStream.writeInt(this.ECV.length());
            hlpOutStream.write(writtenECV.getBytes());

            int recordsBytes = 0;

            System.out.println(hlpByteArrayOutputStream.toByteArray().length);

            for (Navsteva navsteva : this.zaznamyONasvsteve) {

                hlpOutStream.write(navsteva.toByteArray());
                recordsBytes += navsteva.getSize();

            }
            byte[] emptyArrray = new byte[this.size - recordsBytes - 69];

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

            this.size = hlpInStream.readInt();
            this.ID = hlpInStream.readInt();
            this.pocetPlatnychNavstev = hlpInStream.readInt();
            int menoSize = hlpInStream.readInt();
            this.meno = new String(hlpInStream.readNBytes(menoSize));
            hlpInStream.skipBytes(MENO_LENGHT - menoSize);
            int prizeviskoSize = hlpInStream.readInt();
            this.priezvisko = new String(hlpInStream.readNBytes(prizeviskoSize));
            hlpInStream.skipBytes(PRIEZVISKO_LENGHT - prizeviskoSize);
            int eCVSize = hlpInStream.readInt();
            this.ECV = new String(hlpInStream.readNBytes(eCVSize));
            hlpInStream.skipBytes(ECV_LENGTH - eCVSize);

            for (int i = 0; i < this.pocetPlatnychNavstev; i++) {
                Navsteva navsteva = this.vytvaracInstancii.createInstance();
                navsteva.fromByteArray(hlpInStream.readNBytes((int)navsteva.getSize()));
                this.zaznamyONasvsteve.add(navsteva);
            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    @Override
    public String toString() {
        return "Zakaznik{" +
                "meno='" + meno + '\'' +
                ", priezvisko='" + priezvisko + '\'' +
                ", ECV='" + ECV + '\'' +
                ", ID=" + ID +
                ", zaznamyONasvsteve=" + zaznamyONasvsteve +
                ", pocetPlatnychNavstev=" + pocetPlatnychNavstev +
                ", size=" + size +
                '}';
    }

    public String getECV() {
        return this.ECV;
    }

    public void setECV(String ECV) {
        this.ECV = ECV;
    }

}
