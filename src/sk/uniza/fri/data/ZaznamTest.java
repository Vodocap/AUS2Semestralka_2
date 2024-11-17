package sk.uniza.fri.data;

import sk.uniza.fri.heapfile.IRecord;

import java.util.Date;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class ZaznamTest implements IRecord {
    private boolean validity;
    private Date datum;
    private double cena;
    private String popis;
    private static int POPIS_SIZE = 20;
    private int size;

    public ZaznamTest(Date paDatum, double paCena, String paPopis) {
        this.datum = paDatum;
        this.cena = paCena;
        this.popis = paPopis;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public void fromByteArray(byte[] pabyteArray) {

    }

    @Override
    public boolean isValid() {
        return false;
    }
}
