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
 * @author matus
 */
public class Block<T extends IData<T>> implements IRecord<T> {
    protected long validCount;
    private long next;
    private long previous;
    protected long blockStart;
    protected long size;
    protected int sizeFactor;
    protected ArrayList<T> recordArray;
    protected T instanceCreator;


    public Block(T data, long paBlockSize) {
        this.instanceCreator = data;
        this.size = paBlockSize;
        this.validCount = 0;
        this.next = -1;
        this.previous = -1;
        this.blockStart = 0;
        this.recordArray = new ArrayList<>();
        this.sizeFactor = (int)paBlockSize / (int)data.getSize();
    }

    public boolean isPartlyEmpty() {
        return (this.validCount < this.sizeFactor) && this.validCount > 0;
    }

    public void insertData(T paData) {
        if (this.recordArray.size() < sizeFactor) {
            this.recordArray.add(paData);
            this.validCount++;
        }

    }

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
            hlpOutStream.writeLong(this.size);


            long recordsBytes = 0;
            for (T record : this.recordArray) {
                hlpOutStream.write(record.toByteArray());
                recordsBytes += record.getSize();
            }



            byte[] emptyArrray = new byte[(int)(this.size - recordsBytes - 40)];

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
            this.size = hlpInStream.readLong();

            for (int i = 0; i < this.validCount; i++) {

                T record = this.instanceCreator.createInstance();
                record.fromByteArray(hlpInStream.readNBytes((int)record.getSize()));
                this.recordArray.add(record);
            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }

    }

    public boolean isFull() {
        return this.validCount == sizeFactor;
    }

    public void setNext(long next) {
        this.next = next;
    }

    public void setPrevious(long previous) {
        this.previous = previous;
    }

    public boolean isEmpty() {
        return this.validCount == 0;
    }

    public boolean hasReferences() {
        return this.previous != -1 && this.next != -1;
    }

    public boolean isAlmostFull() {
        return sizeFactor - this.recordArray.size() == 1;
    }

    public long getSize() {
        return this.size;
    }


    public long getNext() {
        return this.next;
    }

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

    public void printBlock() {
        System.out.println(this.toString());
    }

    public long getBlockStart() {
        return this.blockStart;
    }

    public void setBlockStart(long blockStart) {
        this.blockStart = blockStart;
    }

    public T getInstanceCreator() {
        return this.instanceCreator;
    }

    public long getValidCount() {
        return this.validCount;
    }





}
