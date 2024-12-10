package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IData;
import sk.uniza.fri.hashfile.IHash;

import java.io.*;
import java.util.BitSet;

public class SearchZakaznikID implements IData<SearchZakaznikID>, IHash {

    private int ID;
    private String ECV;
    private long adresa;
    private static int ECV_LENGTH = 10;

    public SearchZakaznikID(int paID) {
        this.ID = paID;
    }

    public int getID() {
        return ID;
    }

    public String getECV() {
        return this.ECV;
    }

    public void setECV(String ECV) {
        this.ECV = ECV;
    }

    @Override
    public boolean myEquals(SearchZakaznikID data) {

        return this.ID == data.getID();
    }

    @Override
    public SearchZakaznikID createInstance() {

        return new SearchZakaznikID(this.ID);
    }

    @Override
    public void printData() {
        System.out.println(this.toString());
    }




    @Override
    public String toString() {
        return "SearchZakaznikID{" +
                "ID=" + ID +
                ", ECV='" + ECV + '\'' +
                ", adresa=" + adresa +
                '}';
    }



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

    @Override
    public BitSet getHash() {

        return BitSet.valueOf(new long[]{(this.ID)});

    }
}
