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
    private long validCount;
    private long next;
    private long previous;
    private long blockStart;
    private long size;
    private long sizeFactor;
    private ArrayList<T> recordArray;
    T instanceCreator;


    public Block(T data, long paBlockSize, int sizeFactor) {
        this.instanceCreator = data;
        this.size = paBlockSize;
        this.validCount = 0;
        this.next = -1;
        this.previous = -1;
        this.blockStart = 0;
        this.recordArray = new ArrayList<>();
    }

    public boolean isPartlyEmpty() {
        return (this.validCount < sizeFactor) && this.validCount > 0;
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

            System.out.println(hlpByteArrayOutputStream.toByteArray().length);
            long recordsBytes = 0;
            for (T record : this.recordArray) {
                hlpOutStream.write(record.toByteArray());
                recordsBytes += record.getSize();
            }
            System.out.println(this.size);
            System.out.println(recordsBytes);
            System.out.println(this.size - recordsBytes - 40);
            byte[] emptyArrray = new byte[(int)(this.size - recordsBytes - 40)];

            hlpOutStream.write(emptyArrray);

            System.out.println(hlpByteArrayOutputStream.toByteArray().length);
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

    public void printBlock() {
        System.out.println("BLOCK");
        System.out.println("_______________________________");
        System.out.println("Start: " + this.blockStart);
        System.out.println("Size: " + this.size);
        System.out.println("Valid Count: " + this.validCount);
        System.out.println("Next: " + this.next);
        System.out.println("Previous: " + this.previous);
        System.out.println("_______________________________");
        for (T t : this.recordArray) {
            t.print();
        }
    }

    public long getBlockStart() {
        return this.blockStart;
    }

    public void setBlockStart(int blockStart) {
        this.blockStart = blockStart;
    }

    public T getInstanceCreator() {
        return this.instanceCreator;
    }

    public long getValidCount() {
        return this.validCount;
    }



}
