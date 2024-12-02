package sk.uniza.fri.hashfile;

import sk.uniza.fri.heapfile.Block;
import sk.uniza.fri.heapfile.IData;
import sk.uniza.fri.heapfile.IRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class HashFile<T extends IData<T>> implements IRecord<T> {

    private long actualSize;
    private RandomAccessFile randomAccessFileWriter;
    private long end;
    private long blockSize;
    private int depth;
    private long[] addreses;


    public HashFile(String paFilePath, int paBlockSize) {
        this.addreses = new long[2];
        this.actualSize = 0;
        try {
            this.randomAccessFileWriter = new RandomAccessFile(paFilePath, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.end = 0;
        this.blockSize = paBlockSize;
        this.depth = 1;

    }


    public int calculateHash(int paInput, int hashDepth) {

        BitSet bitSet = this.intToBitSet(paInput);

        int resultHash = 0;
        for (int i = 0; i < hashDepth; i++) {
            if (bitSet.get(i)) {
                resultHash |= (1 << i);
            }
        }

        return resultHash;

    }

    public int calculateHash(String paInput, int hashDepth) {

        BitSet bitSet = this.stringToBitSet(paInput);

        int resultHash = 0;
        for (int i = 0; i < hashDepth; i++) {
            if (bitSet.get(i)) {
                resultHash |= (1 << i);
            }
        }

        return resultHash;

    }

    private BitSet stringToBitSet(String inputString) {
        String substring = inputString.substring(0,3);
        BitSet bitSet = new BitSet();
        int sumValue = 0;
        for (int i = 0; i < 3; i++) {
            sumValue += (int) substring.charAt(i);
        }

        return this.intToBitSet(sumValue);
    }



    private BitSet intToBitSet(int paInput) {

        return BitSet.valueOf(new long[]{paInput});


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


    private void doubleAddressSpace(T paData) {

        this.depth++;

        long[] oldAdresses = this.addreses.clone();
        this.addreses = new long[this.addreses.length * 2];

        for (int i = 0; i < oldAdresses.length; i++) {
            this.addreses[i * 2] = oldAdresses[i];
            this.addreses[i * 2 + 1] = oldAdresses[i];
        }


    }


    public void initialiseHeapFileFromFile(String initFilePath) {
        try {
            RandomAccessFile loader = new RandomAccessFile(initFilePath, "rw");
            byte[] initBytes = new byte[40];
            loader.seek(0);
            loader.readFully(initBytes);
            this.fromByteArray(initBytes);
            loader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void splitBlock(HashBlock<T> blockInstance) {
        ArrayList<T> oldRecords = new ArrayList<>(blockInstance.getDataList());
        blockInstance.clearList();
        blockInstance.setDepth(blockInstance.getDepth() + 1);
        try {
            this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
            this.randomAccessFileWriter.write(blockInstance.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        boolean alreadySplit = false;
        for (T record : oldRecords) {
            try {

                int newHash = this.calculateHash(record.getHashparameter(), this.depth);
                int oldhash = this.calculateHash(record.getHashparameter(), blockInstance.getDepth() - 1);
                if (!alreadySplit) {
                    if (oldhash == newHash) {
                        this.randomAccessFileWriter.seek(this.addreses[newHash]);
                        HashBlock rehashedBlock = this.makeBlockInstance(record);
                        rehashedBlock.insertData(record);
                        this.randomAccessFileWriter.seek(this.addreses[newHash]);
                        this.randomAccessFileWriter.write(rehashedBlock.toByteArray());
                    } else {
                        this.randomAccessFileWriter.seek(this.end);
                        this.addreses[newHash] = this.end;
                        this.end += this.blockSize;
                        this.actualSize++;
                        HashBlock rehashedBlock = this.makeEmptyBlockInstance(record);
                        rehashedBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
                        rehashedBlock.setDepth(blockInstance.getDepth());
                        rehashedBlock.insertData(record);
                        this.randomAccessFileWriter.write(rehashedBlock.toByteArray());
                        alreadySplit = true;

                    }
                } else {
                    this.randomAccessFileWriter.seek(this.addreses[newHash]);
                    HashBlock rehashedBlock = this.makeBlockInstance(record);
                    rehashedBlock.insertData(record);
                    this.randomAccessFileWriter.seek(this.addreses[newHash]);
                    this.randomAccessFileWriter.write(rehashedBlock.toByteArray());
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void insert(T paData, int uniqueHashParam) {

        if (this.actualSize == 0) {
            try {
                this.randomAccessFileWriter.seek(this.randomAccessFileWriter.length());
                for (int i = 0; i < 2; i++) {
                    this.addEmptyBlock(paData);
                    this.actualSize++;
                    this.addreses[i] = this.end;
                    this.end += this.blockSize;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        boolean inserted = false;
        int resultHash = this.calculateHash(uniqueHashParam, this.depth);
        while (!inserted) {


            try {
                this.randomAccessFileWriter.seek(this.addreses[resultHash]);
                System.out.println(this.randomAccessFileWriter.getFilePointer());
                System.out.println(this.randomAccessFileWriter.length());
                HashBlock blockInstance = this.makeBlockInstance(paData);
                if (blockInstance.isFull()) {
                    if (blockInstance.getDepth() == this.depth) {
                        this.doubleAddressSpace(paData);
                    }
                    this.splitBlock(blockInstance);
                } else {
                    blockInstance.insertData(paData);
                    this.randomAccessFileWriter.seek(this.addreses[resultHash]);
                    this.randomAccessFileWriter.write(blockInstance.toByteArray());
                    inserted = true;
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    public void insert(T paData, String uniqueHashParam) {

        if (this.actualSize == 0) {
            try {
                this.randomAccessFileWriter.seek(this.randomAccessFileWriter.length());
                for (int i = 0; i < 2; i++) {
                    this.addEmptyBlock(paData);
                    this.actualSize++;
                    this.addreses[i] = this.end;
                    this.end += this.blockSize;
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        boolean inserted = false;
        int resultHash = this.calculateHash(uniqueHashParam, this.depth);
        while (!inserted) {


            try {
                this.randomAccessFileWriter.seek(this.addreses[resultHash]);
                System.out.println(this.randomAccessFileWriter.getFilePointer());
                System.out.println(this.randomAccessFileWriter.length());
                HashBlock blockInstance = this.makeBlockInstance(paData);
                if (blockInstance.isFull()) {
                    if (blockInstance.getDepth() == this.depth) {
                        this.doubleAddressSpace(paData);
                    }
                    this.splitBlock(blockInstance);
                } else {
                    blockInstance.insertData(paData);
                    this.randomAccessFileWriter.seek(this.addreses[resultHash]);
                    this.randomAccessFileWriter.write(blockInstance.toByteArray());
                    inserted = true;
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


    public T get(int hashParam, T paData) {
        try {

            this.randomAccessFileWriter.seek(this.addreses[this.calculateHash(hashParam, this.depth)]);
            HashBlock foundBlock = this.makeBlockInstance(paData);
            return (T) foundBlock.getRecord(paData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public T get(String hashParam, T paData) {
        try {

            this.randomAccessFileWriter.seek(this.addreses[this.calculateHash(hashParam, this.depth)]);
            HashBlock foundBlock = this.makeBlockInstance(paData);
            return (T) foundBlock.getRecord(paData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void printBlocks(T paData) {
        System.out.println("HeapFile size: " + this.actualSize);
        for (int i = 0; i < this.actualSize; i++) {
            long address = i * this.blockSize;
            System.out.println("ADDRESS PRINTED " + address);
            this.printBlock(paData, address);

        }
    }

    public void printBlock(T paData, long paAddress) {


        try {
            if (paAddress <= this.end) {
                this.randomAccessFileWriter.seek(paAddress);

                Block blockInstance = this.makeBlockInstance(paData);
                blockInstance.printBlock();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




    public void closeHashFile() {
        try {
            this.randomAccessFileWriter.setLength(0);
            this.randomAccessFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            hlpOutStream.writeLong(this.end);
            hlpOutStream.writeLong(this.blockSize);

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


            this.blockSize = hlpInStream.readLong();




        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }

    public ArrayList<HashBlock> getAllBlocks(T paData) {
        ArrayList<HashBlock> blocks = new ArrayList<>();
        for (int i = 0; i < this.actualSize; i++) {
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
