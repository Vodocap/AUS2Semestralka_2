package sk.uniza.fri.heapfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class Block<T extends IData<T>> implements IRecord<T> {
    private int validCount;
    private int next;
    private int previous;
    private int size;
    private ArrayList<T> recordArray;
    private Supplier<T> recordSupplier;

    public Block(int paBlockSize) {
        this.size = paBlockSize;
        this.validCount = 0;
        this.next = 0;
        this.previous = 0;
        this.recordArray = new ArrayList<>();
    }

    public void insertData(T paData) {
        if (this.recordArray.size() > this.size) {
            this.recordArray.add(paData);
        }

    }

    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            hlpOutStream.writeInt(this.validCount);
            hlpOutStream.writeInt(this.next);
            hlpOutStream.writeInt(this.previous);
            hlpOutStream.writeInt(this.size);
            for (T record : this.recordArray) {
                record.toByteArray();
            }

            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public void fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            this.validCount = hlpInStream.readInt();
            this.next = hlpInStream.readInt();
            this.previous = hlpInStream.readInt();
            this.size = hlpInStream.readInt();

            for (int i = 0; i < this.validCount; i++) {

                T record = this.recordSupplier.get();
                record.createInstance();
                record.fromByteArray(hlpInStream.readNBytes(record.getSize()));
                this.recordArray.add(record);
            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }

    }

    @Override
    public boolean isValid() {
        return this.validCount == 0;
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


}
