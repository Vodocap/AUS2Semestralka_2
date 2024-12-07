package sk.uniza.fri.hashfile;

import sk.uniza.fri.heapfile.Block;
import sk.uniza.fri.heapfile.IData;
import sk.uniza.fri.heapfile.IHash;
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

    private long actualSize;
    private RandomAccessFile randomAccessFileWriter;
    private long end;
    private long blockSize;
    private int depth;
    private long[] addreses;
    private static int HASH_OFFSET = 7;


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


    public int calculateHash(T paData ,int hashDepth) {

        System.out.println(paData.getHash().toString());

        BitSet bitSet = this.reverseBitset(paData.getHash());

        System.out.println(bitSet.toString());

        int resultHash = 0;

        for (int i = HASH_OFFSET; i < hashDepth + HASH_OFFSET ; i++) {
            if (bitSet.get(i)) {
                resultHash += Math.pow(2, hashDepth - i + HASH_OFFSET - 1);
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

    private void insertBlockInFront(Block blockInstance, long newNext) {
        try {

            System.out.println("NEW NEXT " + newNext);
            System.out.println("FOR ADDRESS " + blockInstance.getBlockStart());
            this.randomAccessFileWriter.seek(newNext);

            Block nextblock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());

            nextblock.setPrevious(blockInstance.getBlockStart());
            blockInstance.setNext(nextblock.getBlockStart());
            blockInstance.setPrevious(-1);

            this.randomAccessFileWriter.seek(nextblock.getBlockStart());
            this.randomAccessFileWriter.write(nextblock.toByteArray());

            this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
            this.randomAccessFileWriter.write(blockInstance.toByteArray());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void splitBlock(HashBlock<T> blockInstance, int splitBlockHash) {

        ArrayList<T> oldRecords = new ArrayList<>(blockInstance.getDataList());
        blockInstance.clearList();
        blockInstance.setDepth(blockInstance.getDepth() + 1);
        if (this.addreses[splitBlockHash] != blockInstance.getBlockStart()) {
            System.out.println("Nerovnaju sa adresy, zly hash " + this.addreses[splitBlockHash] + " - " + blockInstance.getBlockStart());
        }
        HashBlock newBlock = new HashBlock<>(oldRecords.get(0), blockInstance.getSize());
        newBlock.setDepth(blockInstance.getDepth());

        System.out.println("SplittedBlockHash " + splitBlockHash);

        int druhaPozicia = -1;
        for (T oldRecord : oldRecords) {
            int rehashForRecord = this.calculateHash(oldRecord, blockInstance.getDepth());

            if (splitBlockHash == rehashForRecord) {
                System.out.println("Velkost Adresara " + this.addreses.length);
                System.out.println("Novej heš rovnaju sa " + rehashForRecord);
                blockInstance.insertData(oldRecord);
            } else {
                System.out.println("Velkost Adresara " + this.addreses.length);
                System.out.println("Novej heš nerovnaju sa " + rehashForRecord);
                newBlock.insertData(oldRecord);
                if (druhaPozicia == -1) {
                    druhaPozicia = rehashForRecord;
                }

                System.out.println("Druha pozicia " + druhaPozicia);
            }

        }
        
        if (newBlock.isPartlyEmpty() && blockInstance.isPartlyEmpty()) {
            try {
                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                this.randomAccessFileWriter.write(blockInstance.toByteArray());

                this.randomAccessFileWriter.seek(this.end);
                this.addreses[druhaPozicia] = this.end;
                this.end += this.blockSize;
                this.actualSize++;
                newBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
                this.randomAccessFileWriter.write(newBlock.toByteArray());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
        } else if (newBlock.isEmpty() && blockInstance.isFull()) {
            try {
                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                this.randomAccessFileWriter.write(blockInstance.toByteArray());

                if (this.addreses[splitBlockHash] != blockInstance.getBlockStart()) {
                    System.out.println("Nerovnaju sa adresy, zly hash " + this.addreses[splitBlockHash] + " - " + blockInstance.getBlockStart());
                }
                if (splitBlockHash % 2 == 0) {
                    this.addreses[splitBlockHash + 1] = this.addreses[splitBlockHash];
                } else {
                    this.addreses[splitBlockHash - 1] = this.addreses[splitBlockHash];
                }



            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (newBlock.isFull() && blockInstance.isEmpty()) {

            try {
                this.randomAccessFileWriter.seek(this.addreses[splitBlockHash]);
                this.addreses[druhaPozicia] = this.addreses[splitBlockHash];


                newBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
                this.randomAccessFileWriter.write(newBlock.toByteArray());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    }


    public void insert(T paData) {

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

        while (!inserted) {

            int resultHash = this.calculateHash(paData, this.depth);
            System.out.println("Na heši: " + resultHash);

            try {

                if (this.addreses[resultHash] == -1) {
                    this.randomAccessFileWriter.seek(this.end);
                    this.addreses[resultHash] = this.end;
                    this.end += this.blockSize;
                    this.actualSize++;
                    HashBlock rehashedBlock = this.makeEmptyBlockInstance(paData);
                    rehashedBlock.setBlockStart(this.randomAccessFileWriter.getFilePointer());
                    rehashedBlock.setDepth(this.depth);
                    rehashedBlock.insertData(paData);
                    this.randomAccessFileWriter.write(rehashedBlock.toByteArray());


                } else {

                    this.randomAccessFileWriter.seek(this.addreses[resultHash]);

                    System.out.println("Hash " + resultHash);
                    System.out.println("Adresa na insert " + this.addreses[resultHash]);

                    HashBlock blockInstance = this.makeBlockInstance(paData);

                    if (blockInstance.isFull()) {
                        if (blockInstance.getDepth() == this.depth) {
                            this.doubleAddressSpace();
                            resultHash = this.calculateHash(paData, this.depth);
                            System.out.println("Nova hlpka: " + this.depth);
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

            this.randomAccessFileWriter.seek(this.addreses[this.calculateHash(paData, this.depth)]);
            HashBlock foundBlock = this.makeBlockInstance(paData);
            return (T) foundBlock.getRecord(paData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void printBlocks(T paData) {
        System.out.println("HashFile size: " + this.actualSize);
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
