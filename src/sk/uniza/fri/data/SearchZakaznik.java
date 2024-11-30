package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IData;

import java.io.*;

public class SearchZakaznik implements IData<SearchZakaznik> {

    private int ID;
    private String ECV;
    private long adresa;
    private static int ECV_LENGTH = 10;

    public SearchZakaznik(int paID, String paECV , long paAdresa) {
        this.ID = paID;
        this.ECV = paECV;
        this.adresa = paAdresa;
    }

    public int getID() {
        return ID;
    }

    public String getECV() {
        return this.ECV;
    }

    @Override
    public boolean myEquals(SearchZakaznik data) {

        return this.ID == data.getID() && this.ECV.equals(data.getECV());
    }

    @Override
    public SearchZakaznik createInstance() {

        return new SearchZakaznik(this.ID, this.ECV, this.adresa);
    }

    @Override
    public void print() {
        System.out.println(this.toString());
    }

    @Override
    public int getHashparameter() {
        return 0;
    }

    @Override
    public String toString() {
        return "IDSearchData{" +
                "ID=" + ID +
                ", ECV='" + ECV + '\'' +
                ", adresa=" + adresa +
                '}';
    }

    @Override
    public String getHashParameter() {
        return "";
    }

    @Override
    public long getSize() {
        return 18;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {


            String writtenECV = this.ECV;
            if (this.ECV.length() <= ECV_LENGTH) {
                for (int i = this.ECV.length(); i < ECV_LENGTH; i++) {
                    writtenECV += "x";
                }
            }

            hlpOutStream.writeInt(this.ID);
            hlpOutStream.writeInt(this.ECV.length());
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

            this.ID = hlpInStream.readInt();
            int eCVSize = hlpInStream.readInt();
            this.ECV = new String(hlpInStream.readNBytes(eCVSize));
            hlpInStream.skipBytes(ECV_LENGTH - eCVSize);



        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }
}
