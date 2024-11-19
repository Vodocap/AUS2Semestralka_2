package sk.uniza.fri.heapfile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class HeapFile<T extends IData> {
    private long emptyBlocks;
    private long partlyEmptyBlocks;
    private String filePath;
    private int sizeNum;
    private int actualSize;
    private RandomAccessFile randomAccessFileWriter;
    private int start;
    private int end;
    private static int BLOCK_SIZE = 250;


    public HeapFile(String paFilePath, int paSizeNum) {
        this.actualSize = 0;
        this.start = 0;
        this.end = 0;
        this.sizeNum = paSizeNum;
        this.emptyBlocks = 0;
        this.partlyEmptyBlocks = -1;
        try {
            this.randomAccessFileWriter = new RandomAccessFile(paFilePath, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public long insert(T paData) {
        try {

            if (this.partlyEmptyBlocks != -1) {
                this.randomAccessFileWriter.seek(this.partlyEmptyBlocks);
                Block blockInstance = new Block(paData, BLOCK_SIZE);
                blockInstance.fromByteArray(this.readBlock());
                blockInstance.insertData(paData);
                this.randomAccessFileWriter.seek(this.partlyEmptyBlocks);
                this.randomAccessFileWriter.write(blockInstance.toByteArray());
                if (blockInstance.isParltyEmpty()) {
                    this.partlyEmptyBlocks = blockInstance.getPrevious();
                } else {
                    this.partlyEmptyBlocks = -1;
                }
                return blockInstance.getPrevious();


            } else if (this.emptyBlocks != -1) {

                this.randomAccessFileWriter.seek(this.emptyBlocks);
                Block blockInstance = new Block(paData, BLOCK_SIZE);
                blockInstance.insertData(paData);
                this.end += BLOCK_SIZE;
                blockInstance.setNext(this.end);
                blockInstance.setPrevious(this.end - BLOCK_SIZE);
                if (blockInstance.isParltyEmpty()) {
                    this.partlyEmptyBlocks = blockInstance.getPrevious();
                }

                this.randomAccessFileWriter.write(blockInstance.toByteArray());
                this.actualSize++;
                this.emptyBlocks = blockInstance.getNext();
                return blockInstance.getPrevious();
            }
            return -1;


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readBlock() {
        byte[] readBlock = new byte[BLOCK_SIZE];
        try {
            this.randomAccessFileWriter.readFully(readBlock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readBlock;
    }

    public void delete(long paAdress, T paData) {
        try {
            this.randomAccessFileWriter.seek(paAdress);
            Block blockInstance = new Block(paData, BLOCK_SIZE);
            blockInstance.fromByteArray(this.readBlock());
            blockInstance.removeData(paData);
            this.randomAccessFileWriter.seek(paAdress);
            this.randomAccessFileWriter.write(blockInstance.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T get(long paAddress, T paData) {
        return null;
    }

    public void printBlocks(T paData) {
        for (int i = 0; i < this.actualSize; i++) {
            try {
                this.randomAccessFileWriter.seek(i * BLOCK_SIZE);
                byte[] readBlock = new byte[BLOCK_SIZE];
                this.randomAccessFileWriter.readFully(readBlock);
                Block block = new Block(paData,BLOCK_SIZE);
                block.fromByteArray(readBlock);
                block.printBlock();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
