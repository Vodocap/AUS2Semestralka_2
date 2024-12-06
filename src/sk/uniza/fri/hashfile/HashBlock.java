package sk.uniza.fri.hashfile;

import sk.uniza.fri.heapfile.Block;
import sk.uniza.fri.heapfile.IData;

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
public class HashBlock<T extends IData<T>> extends Block<T> {
    private int depth;
    private final int dataSize;

    public HashBlock(IData<T> data, long paBlockSize) {
        super((T)data, paBlockSize);
        this.dataSize = 20;
        if (this.sizeFactor * data.getSize() + this.dataSize > this.size) {
            this.sizeFactor -= 1;
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            hlpOutStream.writeLong(this.blockStart);
            hlpOutStream.writeLong(this.validCount);
            hlpOutStream.writeInt(this.depth);


            long recordsBytes = 0;
            for (T record : this.recordArray) {
                hlpOutStream.write(record.toByteArray());
                recordsBytes += record.getSize();
            }



            byte[] emptyArrray = new byte[(int)(this.getSize() - recordsBytes - this.dataSize)];

            hlpOutStream.write(emptyArrray);
//            System.out.println(hlpOutStream.size());


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
            this.depth = hlpInStream.readInt();

            for (int i = 0; i < this.validCount; i++) {

                T record = this.instanceCreator.createInstance();
                record.fromByteArray(hlpInStream.readNBytes((int)record.getSize()));
                this.recordArray.add(record);
            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }

    }

    public String toString() {
        String resultString = "BLOCK" + "\n" +
                " Start: " + this.blockStart + "\n" +
                " Size: " + this.size + "\n" +
                " Valid Count: " + this.validCount + "\n" +
                " DATA: " + "\n";


        for (T t : this.recordArray) {
            resultString += t.toString();
        }

        return resultString;
    }

    public ArrayList<T> getDataList () {
        return this.recordArray;
    }

    public void clearList() {
        this.recordArray.clear();
        this.validCount = 0;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
