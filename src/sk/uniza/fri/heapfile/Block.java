package sk.uniza.fri.heapfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 15. 11. 2024 - 13:25
 *
 * @param <T> v bloku sú len tie záznamy ktoré implementujú interface IDATA
 * @author matus
 */
public class Block<T extends IData<T>> implements IRecord<T> {
    /**
     * The Valid count.
     */
    protected long validCount;
    private long next;
    private long previous;
    /**
     * The Block start.
     */
    protected long blockStart;
    /**
     * The Size.
     */
    protected long size;
    /**
     * The Size factor.
     */
    protected int sizeFactor;
    /**
     * The Record array.
     */
    protected ArrayList<T> recordArray;
    /**
     * The Instance creator.
     */
    protected T instanceCreator;
    private final int dataSize;


    /**
     * Vytvorí inštanciu bloku.
     *
     * @param data        inštancia ktoréa služi pre neskoršie generovanie inštancií v operačnej pamäti
     * @param paBlockSize veľkosť bloku
     */
    public Block(T data, long paBlockSize) {
        this.instanceCreator = data;
        this.size = paBlockSize;
        this.validCount = 0;
        this.next = -1;
        this.previous = -1;
        this.blockStart = 0;
        this.recordArray = new ArrayList<>();
        this.sizeFactor = (int)paBlockSize / (int)data.getSize();
        this.dataSize = 32;
        if (this.sizeFactor * data.getSize() + this.dataSize > this.size) {
            this.sizeFactor -= 1;
        }

    }

    /**
     * Gets data size.
     * Vráti veľkosť riadiacich informácií bloku
     * @return the data size
     */
    public int getDataSize() {
        return this.dataSize;
    }

    /**
     * Ak je blok z čiastočne prázdny/plný tak vráti true, inak false
     *
     * @return the boolean
     */
    public boolean isPartlyEmpty() {
        return (this.validCount < this.sizeFactor) && this.validCount > 0;
    }

    /**
     * Insert data.
     * Vloží záznam do bloku ale len ak sa zmestí teda ak je počet platných záznamov menej ako sizeFactor
     * @param paData the pa data
     */
    public void insertData(T paData) {
        if (this.recordArray.size() < this.sizeFactor) {
            this.recordArray.add(paData);
            this.validCount++;
        }

    }

    /**
     * Gets record.
     * Vráti záznam z bloku ak sa rovná tomu v parametri
     * @param paData parametrické dáta
     * @return záznam
     *
     */
    public T getRecord(T paData) {
        if (!this.recordArray.isEmpty()) {
            for (T t : this.recordArray) {
                if (t.myEquals(paData)) {
                    return t;
                }

            }
        }
        return null;
    }


    /**
     * Remove data.
     * odstráni záznam z bloku ak sa rovná tým v parameteri
     *
     * @param paData parametrické dáta
     */
    public void removeData(T paData) {
        if (!this.recordArray.isEmpty()) {
            for (int i = 0; i < this.recordArray.size(); i++) {
                if (this.recordArray.get(i).myEquals(paData)) {
                    this.recordArray.remove(i);
                    this.validCount--;
                    break;
                }

            }
        }

    }

    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            hlpOutStream.writeLong(this.blockStart);
            hlpOutStream.writeLong(this.validCount);
            hlpOutStream.writeLong(this.next);
            hlpOutStream.writeLong(this.previous);


            long recordsBytes = 0;
            for (T record : this.recordArray) {
                hlpOutStream.write(record.toByteArray());
                recordsBytes += record.getSize();
            }



            byte[] emptyArrray = new byte[(int)(this.size - recordsBytes - this.dataSize)];

            hlpOutStream.write(emptyArrray);


            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public void fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            this.blockStart = hlpInStream.readLong();
            this.validCount = hlpInStream.readLong();
            this.next = hlpInStream.readLong();
            this.previous = hlpInStream.readLong();

            for (int i = 0; i < this.validCount; i++) {

                T record = this.instanceCreator.createInstance();
                record.fromByteArray(hlpInStream.readNBytes((int)record.getSize()));
                this.recordArray.add(record);
            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }

    }

    /**
     * Is full boolean.
     * ak je blok plný tak vráti true inak false
     * @return the boolean
     */
    public boolean isFull() {
        return this.validCount == this.sizeFactor;
    }

    /**
     * Set next.
     * nastaví bloku nového nasledovníka
     *
     * @param next nový nasledovník
     */
    public void setNext(long next) {
        this.next = next;
    }

    /**
     * Set previous.
     * nastaví bloku nového predchodcu
     *
     * @param previous nový predchodca
     */
    public void setPrevious(long previous) {
        this.previous = previous;
    }

    /**
     * Is empty boolean.
     * Ak je blok prázdny vráti true, inak false
     *
     * @return the boolean
     */
    public boolean isEmpty() {
        return this.validCount == 0;
    }

    /**
     * Has references boolean.
     * ak blok nemá žiadne referencie tak vráti true, inak false
     * @return the boolean
     */
    public boolean hasReferences() {
        return this.previous != -1 && this.next != -1;
    }

    /**
     * Ak je blok skoro plný (chýba jeden blok do toho aby bol plný) vráti true, inak false
     *
     *
     * @return the boolean
     */
    public boolean isAlmostFull() {
        return this.sizeFactor - this.recordArray.size() == 1;
    }

    public long getSize() {
        return this.size;
    }


    /**
     * Get next.
     * Vráti nasledovníka bloku
     * @return nasledovník
     */
    public long getNext() {
        return this.next;
    }

    /**
     * Get previous.
     * Vráti predchodcu bloku
     * @return predchodca
     */
    public long getPrevious() {
        return this.previous;
    }

    public String toString() {
        String resultString = "BLOCK" + "\n" +
                " Start: " + this.blockStart + "\n" +
                " Size: " + this.size + "\n" +
                " Valid Count: " + this.validCount + "\n" +
                " Next: " + this.next + "\n" +
                " Previous: " + this.previous + "\n" +
                " DATA: " + "\n";


        for (T t : this.recordArray) {
            resultString += t.toString();
        }

        return resultString;
    }

    /**
     * Dlhší to string bloku s viascej informáciami.
     *
     * @return informácie o bloku
     */
    public String toStringLonger() {
        String resultString = "BLOCK" + "\n" +
                "_______________________________" + "\n" +
                "Start: " + this.blockStart + "\n" +
                "Size: " + this.size + "\n" +
                "Valid Count: " + this.validCount + "\n" +
                "Next: " + this.next + "\n" +
                "Previous: " + this.previous + "\n" +
                "_______________________________" + "\n" +
                "DATA: " + "\n" +
                "*****************************" + "\n";

        for (T t : this.recordArray) {
            resultString += t.toString();
        }
        resultString += "\n" + "*****************************\n";

        return resultString;
    }

    /**
     * Print block.
     * Vyprintuje blok
     */
    public void printBlock() {
        System.out.println(this.toString());
    }

    /**
     * Gets block start.
     * Vráti číslo začiatku bloku v súbore
     * @return číslo začiatku bloku
     */
    public long getBlockStart() {
        return this.blockStart;
    }

    /**
     * Sets block start.
     * Nastaví číslo začiatku bloku v súbore
     * @param blockStart nové číslo začiatku bloku
     */
    public void setBlockStart(long blockStart) {
        this.blockStart = blockStart;
    }

    /**
     * Gets instance creator.
     * Vráti vytvárač inštancií
     *
     * @return vytvárač inštancií
     */
    public T getInstanceCreator() {
        return this.instanceCreator;
    }

    /**
     * Gets valid count.
     * Vráti počet platných záznamov
     *
     * @return počet platných záznamov
     */
    public long getValidCount() {
        return this.validCount;
    }





}
