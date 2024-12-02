package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class SearchZakaznikECV implements IData<SearchZakaznikECV> {
    private int ID;
    private String ECV;
    private long adresa;
    private static int ECV_LENGTH = 10;

    public SearchZakaznikECV(String ECV) {
        this.ECV = ECV;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getECV() {
        return this.ECV;
    }

    @Override
    public boolean myEquals(SearchZakaznikECV data) {

        return this.ECV.equals(data.getECV());
    }

    @Override
    public SearchZakaznikECV createInstance() {

        return new SearchZakaznikECV(this.ECV);
    }

    @Override
    public void print() {
        System.out.println(this.toString());
    }

    @Override
    public int getHashparameter() {
        return this.ID;
    }

    @Override
    public String getHashParameter() {
        return this.ECV;
    }


    @Override
    public String toString() {
        return "SearchZakaznikECV{" +
                "ID=" + ID +
                ", ECV='" + ECV + '\'' +
                ", adresa=" + adresa +
                '}';
    }




    @Override
    public long getSize() {
        return 22;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            String writtenECV = this.ECV;
            hlpOutStream.writeLong(this.adresa);
            hlpOutStream.writeInt(this.ID);
            hlpOutStream.write(writtenECV.getBytes());

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

            this.adresa = hlpInStream.readLong();
            this.ID = hlpInStream.readInt();
            this.ECV = new String(hlpInStream.readNBytes(ECV_LENGTH));

        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    public long getAdresa() {
        return this.adresa;
    }

    public void setAdresa(long adresa) {
        this.adresa = adresa;
    }
}
