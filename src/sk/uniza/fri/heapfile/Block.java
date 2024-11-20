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
    private int validCount;
    private int next;
    private int previous;
    private int blockStart;
    private int size;
    private static int RECORD_LIMIT = 4;
    private ArrayList<T> recordArray;
    T instanceCreator;


    public Block(T data, int paBlockSize) {
        this.instanceCreator = data;
        this.size = paBlockSize;
        this.validCount = 0;
        this.next = -1;
        this.previous = -1;
        this.blockStart = 0;
        this.recordArray = new ArrayList<>();
    }

    public boolean isPartlyEmpty() {
        return this.recordArray.size() < RECORD_LIMIT && !this.recordArray.isEmpty();
    }

    public void insertData(T paData) {
        if (this.recordArray.size() < RECORD_LIMIT) {
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

            hlpOutStream.writeInt(this.blockStart);
            hlpOutStream.writeInt(this.validCount);
            hlpOutStream.writeInt(this.next);
            hlpOutStream.writeInt(this.previous);
            hlpOutStream.writeInt(this.size);

            int recordsBytes = 0;
            for (T record : this.recordArray) {
                hlpOutStream.write(record.toByteArray());
                recordsBytes += record.getSize();
            }
            byte[] emptyArrray = new byte[this.size - recordsBytes - 20];

            hlpOutStream.write(emptyArrray);

//            System.out.println(hlpByteArrayOutputStream.toByteArray().length);
            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public void fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            this.blockStart = hlpInStream.readInt();
            this.validCount = hlpInStream.readInt();
            this.next = hlpInStream.readInt();
            this.previous = hlpInStream.readInt();
            this.size = hlpInStream.readInt();

            for (int i = 0; i < this.validCount; i++) {

                T record = this.instanceCreator.createInstance();
                record.fromByteArray(hlpInStream.readNBytes(record.getSize()));
                this.recordArray.add(record);
            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }

    }

    public boolean isFull() {
        return this.recordArray.size() == RECORD_LIMIT;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public boolean isEmpty() {
        return this.validCount == 0;
    }

    public boolean hasReferences() {
        return this.getPrevious() != -1 || this.getNext() != -1;
    }

    public int getSize() {
        return this.size;
    }


    public int getNext() {
        return this.next;
    }

    public int getPrevious() {
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

    public int getBlockStart() {
        return this.blockStart;
    }

    public void setBlockStart(int blockStart) {
        this.blockStart = blockStart;
    }

    public T getInstanceCreator() {
        return this.instanceCreator;
    }

    public int getValidCount() {
        return this.validCount;
    }



}
