package sk.uniza.fri.hashfile;

import sk.uniza.fri.heapfile.IData;
import sk.uniza.fri.heapfile.IRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class HashFile<T extends IData<T> & IHash> implements IRecord<T> {

    private int numberOfBlocks;
    private RandomAccessFile randomAccessFileWriter;
    private long end;
    private int blockSize;
    private int depth;
    private long[] addreses;
    private static int HASH_OFFSET = 7;


    public HashFile(String paFilePath, int paBlockSize) {
        this.addreses = new long[2];
        Arrays.fill(this.addreses, -1);
        this.numberOfBlocks = 0;
        try {
            this.randomAccessFileWriter = new RandomAccessFile(paFilePath, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.end = 0;
        this.blockSize = paBlockSize;
        this.depth = 1;

    }

    public long[] getAddreses() {
        return this.addreses;
    }


    public int calculateHash(T paData ,int hashDepth) {


        BitSet bitSet = this.reverseBitset(paData.getHash());

        int resultHash = 0;
        for (int i = HASH_OFFSET; i < hashDepth + HASH_OFFSET ; i++) {
            if (bitSet.get(i)) {
                resultHash += Math.pow(2, hashDepth - i + HASH_OFFSET - 1);
            }

        }

        return resultHash;

    }


    public int bitsetToInt(BitSet paBitSet) {
        int resultHash = 0;

        for (int i = 0; i < 32 ; i++) {
            if (paBitSet.get(i)) {
                resultHash += Math.pow(2, 32 - i - 1);
            }

        }

        return resultHash;
    }

    private void addEmptyBlock(T paData) {
        HashBlock emptyBlock = new HashBlock(paData, this.blockSize);

        try {
            emptyBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
            emptyBlock.setDepth(this.depth);
            this.randomAccessFileWriter.write(emptyBlock.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private void doubleAddressSpace() {

        this.depth++;

        long[] oldAdresses = this.addreses.clone();
        this.addreses = new long[this.addreses.length * 2];

        for (int i = 0; i < oldAdresses.length; i++) {
            this.addreses[i * 2] = oldAdresses[i];
            this.addreses[i * 2 + 1] = oldAdresses[i];
        }


    }


    BitSet reverseBitset(BitSet bitSetToReverse) {
        BitSet revertedBitset = new BitSet(32);


        for (int i = 0; i < 32; i++) {
            if (bitSetToReverse.get(i)) {

                revertedBitset.set(32 - i - 1);

            }
        }

        return revertedBitset;
    }


    public void initialiseHashFileFromFile(String attributeFilePath, String adressesFilePath) {
        try {
            RandomAccessFile attributeLoader = new RandomAccessFile(attributeFilePath, "rw");
            byte[] attributeInitBytes = new byte[20];
            attributeLoader.seek(0);
            attributeLoader.readFully(attributeInitBytes);
            this.fromByteArray(attributeInitBytes);
            attributeLoader.close();

            RandomAccessFile adrressArrayLoader = new RandomAccessFile(adressesFilePath, "rw");
            adrressArrayLoader.seek(0);
            byte[] addressesInitBytes = new byte[this.addreses.length * 8];
            adrressArrayLoader.readFully(addressesInitBytes);
            this.adressesFromByteArray(addressesInitBytes);
            adrressArrayLoader.close();

            System.out.println("HASHFILE INITIALISED: ");
            System.out.println(this.toString());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveHashFileIntoFile(String attributeFilePath, String adressesFilePath) {
        try {
            RandomAccessFile attributeSaver = new RandomAccessFile(attributeFilePath, "rw");
            if (attributeSaver.length() < 1) {
                attributeSaver.seek(attributeSaver.length());
            } else {
                attributeSaver.seek(0);
            }
            attributeSaver.write(this.toByteArray());
            attributeSaver.close();

            RandomAccessFile adrressArraySaver = new RandomAccessFile(adressesFilePath, "rw");
            if (adrressArraySaver.length() < 1) {
                adrressArraySaver.seek(adrressArraySaver.length());
            } else {
                adrressArraySaver.seek(0);
            }
            adrressArraySaver.write(this.addressesToByteArray());
            adrressArraySaver.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public int getFirst(BitSet bitSet , int trimDepth) {
        bitSet.set(0, trimDepth, false);
        return bitsetToInt(this.reverseBitset(bitSet));
    }


    @Override
    public String toString() {
        return "HashFile{" +
                "actualSize=" + numberOfBlocks +
                ", randomAccessFileWriter=" + randomAccessFileWriter +
                ", end=" + end +
                ", blockSize=" + blockSize +
                ", depth=" + depth +
                ", addreses=" + Arrays.toString(addreses) +
                '}';
    }

    public void splitBlock(HashBlock<T> blockInstance, int splitBlockHash) {

        ArrayList<T> oldRecords = new ArrayList<>(blockInstance.getDataList());
//        System.out.println(Arrays.toString(this.addreses));

//        System.out.println(this.depth - blockInstance.getDepth());

        int range = (int) Math.pow(2, this.depth - blockInstance.getDepth());


        int first = this.getFirst(BitSet.valueOf(new long[]{(splitBlockHash)}), this.depth - blockInstance.getDepth());
        int last = first + range;
        int half = first + (range / 2);

//        System.out.println("Zaciatok: " + first);
//        System.out.println("Stred: " + half);
//        System.out.println("Koniec: " + last);
//        System.out.println("Rozsah: " + range);

        blockInstance.clearList();
        blockInstance.setDepth(blockInstance.getDepth() + 1);

        HashBlock<T> newBlock = new HashBlock<T>(oldRecords.get(0), blockInstance.getSize());
        newBlock.setDepth(blockInstance.getDepth());




        for (T oldRecord : oldRecords) {

            int rehashForRecord = this.calculateHash(oldRecord, blockInstance.getDepth());

            if (rehashForRecord % 2 == 0) {

                blockInstance.insertData(oldRecord);
            } else {

                newBlock.insertData(oldRecord);

            }

        }

        
        if (newBlock.isPartlyEmpty() && blockInstance.isPartlyEmpty()) {
            try {

                System.out.println("Blok sa rozdelil na 2 ");
                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                this.randomAccessFileWriter.write(blockInstance.toByteArray());


                for (int i = half; i < last; i++) {
                    this.addreses[i] = this.end;
                }


                this.randomAccessFileWriter.seek(this.end);
                this.end += this.blockSize;
                this.numberOfBlocks++;

                newBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
                this.randomAccessFileWriter.write(newBlock.toByteArray());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
        } else if (newBlock.isEmpty() && blockInstance.isFull()) {
            try {
                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                this.randomAccessFileWriter.write(blockInstance.toByteArray());


                System.out.println("Secia isli do stareho");


                for (int i = half; i < last; i++) {
                    this.addreses[i] = -1;
                }

                System.out.println(Arrays.toString(blockInstance.getDataList().toArray()));
                System.out.println(blockInstance.getBlockStart());



            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (newBlock.isFull() && blockInstance.isEmpty()) {

            try {
                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());

                System.out.println("Secia isli do noveho");
                System.out.println(Arrays.toString(newBlock.getDataList().toArray()));


                for (int i = first; i < half; i++) {
                    this.addreses[i] = -1;
                }


                newBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
                System.out.println(newBlock.getBlockStart());
                this.randomAccessFileWriter.write(newBlock.toByteArray());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    }


    public void insert(T paData) {

//        if (this.actualSize == 0) {
//            try {
//                this.randomAccessFileWriter.seek(this.randomAccessFileWriter.length());
//                for (int i = 0; i < 2; i++) {
//                    this.addEmptyBlock(paData);
//                    this.actualSize++;
//                    this.addreses[i] = this.end;
//                    this.end += this.blockSize;
//                }
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

        boolean inserted = false;

        while (!inserted) {

            int resultHash = this.calculateHash(paData, this.depth);
            System.out.println("Hash: " + resultHash);

            try {

                if (this.addreses[resultHash] == -1) {
                    this.randomAccessFileWriter.seek(this.end);
                    this.addreses[resultHash] = this.end;
                    this.end += this.blockSize;
                    this.numberOfBlocks++;
                    HashBlock rehashedBlock = this.makeEmptyBlockInstance(paData);
                    rehashedBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
                    rehashedBlock.setDepth(this.depth);
                    rehashedBlock.insertData(paData);
                    this.randomAccessFileWriter.write(rehashedBlock.toByteArray());
                    inserted = true;

                } else {

                    this.randomAccessFileWriter.seek(this.addreses[resultHash]);

                    System.out.println("Hash " + resultHash);
                    System.out.println("Adresa na insert " + this.addreses[resultHash]);

                    HashBlock blockInstance = this.makeBlockInstance(paData);

                    if (blockInstance.isFull()) {
                        if (blockInstance.getDepth() == this.depth) {
                            this.doubleAddressSpace();
                            resultHash = this.calculateHash(paData, this.depth);
                            System.out.println("Nova hlbka: " + this.depth);
                            System.out.println("Hash " + resultHash);
                        }

                        this.splitBlock(blockInstance, resultHash);

                    } else {
                        blockInstance.insertData(paData);
                        this.randomAccessFileWriter.seek(this.addreses[resultHash]);
                        this.randomAccessFileWriter.write(blockInstance.toByteArray());
                        inserted = true;
                    }
                }



            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }




    private HashBlock makeBlockInstance(T paData) {
        HashBlock blockInstance = new HashBlock(paData, this.blockSize);
        blockInstance.fromByteArray(this.readBlock());
        return blockInstance;
    }

    private HashBlock makeEmptyBlockInstance(T paData) {
        HashBlock blockInstance = new HashBlock(paData, this.blockSize);
        return blockInstance;
    }


    private byte[] readBlock() {
        byte[] readBlock = new byte[(int) this.blockSize];
        try {
            this.randomAccessFileWriter.readFully(readBlock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readBlock;
    }

    public T get(T paData) {
        try {

            System.out.println("Hlada sa na adrese: " + this.addreses[this.calculateHash(paData, this.depth)]);
            System.out.println("Podla hashu " + this.calculateHash(paData, this.depth));
            this.randomAccessFileWriter.seek(this.addreses[this.calculateHash(paData, this.depth)]);
            HashBlock foundBlock = this.makeBlockInstance(paData);
            return (T) foundBlock.getRecord(paData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void printBlocks(T paData) {
        System.out.println("HashFile size: " + this.numberOfBlocks);
        for (int i = 0; i < this.numberOfBlocks; i++) {
            long address = i * this.blockSize;
            System.out.println("ADDRESS PRINTED " + address);
            this.printBlock(paData, address);

        }
    }

    public void printBlock(T paData, long paAddress) {


        try {
            if (paAddress <= this.end) {
                this.randomAccessFileWriter.seek(paAddress);

                HashBlock blockInstance = this.makeBlockInstance(paData);
                blockInstance.printBlock();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    public void closeHashFile() {
        try {
            this.randomAccessFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] addressesToByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            for (long address : this.addreses) {
                hlpOutStream.writeLong(address);
            }

            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            hlpOutStream.writeLong(this.end);
            hlpOutStream.writeInt(this.blockSize);
            hlpOutStream.writeInt(this.depth);
            hlpOutStream.writeInt(this.addreses.length);

            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    public void fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            this.end = hlpInStream.readLong();
            this.blockSize = hlpInStream.readInt();
            this.depth = hlpInStream.readInt();
            int adressesLength = hlpInStream.readInt();
            System.out.println(adressesLength);
            this.addreses = new long[adressesLength];
            this.numberOfBlocks = (int) (this.end / this.blockSize);


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    public void adressesFromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            for (int i = 0; i < this.addreses.length; i++) {
                this.addreses[i] = hlpInStream.readLong();
            }


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    public ArrayList<HashBlock> getAllBlocks(T paData) {
        ArrayList<HashBlock> blocks = new ArrayList<>();
        for (int i = 0; i < this.numberOfBlocks; i++) {
            long address = i * this.blockSize;
            try {
                this.randomAccessFileWriter.seek(address);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            HashBlock blockInstance = this.makeBlockInstance(paData);
            blocks.add(blockInstance);
            blockInstance.printBlock();

        }

        return blocks;

    }

    @Override
    public long getSize() {
        return 0;
    }

}
