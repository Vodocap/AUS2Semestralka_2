package sk.uniza.fri.heapfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

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
    private T[] recordArray;

    public Block(int paBlockSize) {
        this.size = paBlockSize;
        this.validCount = 0;
        this.next = 0;
        this.previous = 0;
    }

    public byte[] toByteArray(Object paData) {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            if (paData instanceof Integer) {
                hlpOutStream.writeInt((Integer)paData);
            } else if (paData instanceof Double) {
                hlpOutStream.writeDouble((Double) paData);
            } else if (paData instanceof Character) {
                hlpOutStream.writeChar((Character) paData);
            } else if (paData instanceof String) {
                //TODO spravit tie stringy aby tam isli
            }

            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public Object fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            Object vystup = hlpInStream.readInt();
            return vystup;

        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }

    }

    @Override
    public T createInstance() {
        return null;
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