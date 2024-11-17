package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IData;
import sk.uniza.fri.heapfile.IRecord;

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
public class OsobaTest<T extends IData<T>> implements IData<T> {
    private String meno;
    private String priezvisko;
    private static int MENO_LENGHT = 15;
    private static int PRIEZVISKO_LENGHT = 20;
    private int ID;
    private ArrayList<T> zaznamy;
    private int size;

    public OsobaTest(String paMeno, String paPriezvisko, int paID) {
        this.zaznamy = new ArrayList<>();
        this.meno = paMeno;
        this.priezvisko = paPriezvisko;
        this.size = 0;
        this.ID = paID;

    }

    public void addZaznam(T paZaznam) {
        this.zaznamy.add(paZaznam);
    }

    @Override
    public T createInstance() {
        OsobaTest osobaCopy = new OsobaTest(this.meno, this.priezvisko, this.ID);
        for (T t : this.zaznamy) {
            osobaCopy.addZaznam(t);
        }
        return (T) osobaCopy;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            String writtenMeno = this.meno;
            if (this.meno.length() < MENO_LENGHT) {
                for (int i = this.meno.length(); i < MENO_LENGHT; i++) {
                    writtenMeno += "x";
                }
            }
            String writtenPriezvisko = this.priezvisko;
            if (this.priezvisko.length() < PRIEZVISKO_LENGHT) {
                for (int i = this.priezvisko.length(); i < PRIEZVISKO_LENGHT; i++) {
                    writtenPriezvisko += "x";
                }
            }

            hlpOutStream.writeInt(this.size);
            hlpOutStream.writeInt(this.ID);
            hlpOutStream.writeInt(this.meno.length());
            hlpOutStream.write(writtenMeno.getBytes());
            hlpOutStream.writeInt(this.priezvisko.length());
            hlpOutStream.write(writtenPriezvisko.getBytes());


            for (T record : this.zaznamy) {
                record.toByteArray();
            }

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
            int menoSize = hlpInStream.readInt();
            this.meno =  new String(hlpInStream.readNBytes(menoSize));
            hlpInStream.skipBytes(MENO_LENGHT - menoSize);
            int prizeviskoSize = hlpInStream.readInt();
            this.priezvisko =  new String(hlpInStream.readNBytes(prizeviskoSize));
            hlpInStream.skipBytes(PRIEZVISKO_LENGHT - prizeviskoSize);

//            for (int i = 0; i < this.validCount; i++) {
//
//                T record = this.recordSupplier.get();
//                record.createInstance();
//                record.fromByteArray(hlpInStream.readNBytes(record.getSize()));
//                this.recordArray.add(record);
//            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    @Override
    public boolean isValid() {
        return this.size == 0;
    }
}
