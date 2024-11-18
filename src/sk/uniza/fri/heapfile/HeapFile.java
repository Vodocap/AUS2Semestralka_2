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
    private int emptyBlocks;
    private int partlyEmptyBlocks;
    private String filePath;
    private int sizeNum;
    private RandomAccessFile randomAccessFileWriter;
    private int start;
    private int end;
    private static int BLOCK_SIZE = 250;


    public HeapFile(String paFilePath, int paSizeNum) {
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
            } else if (this.emptyBlocks != -1) {
                this.randomAccessFileWriter.seek(this.emptyBlocks);
            }
            Block newBlock = new Block(paData, BLOCK_SIZE);
            newBlock.insertData(paData);

            this.end += BLOCK_SIZE;
            newBlock.setNext(this.end);
            if (newBlock.isParltyEmpty()) {
                this.partlyEmptyBlocks = 0;
            }
            this.randomAccessFileWriter.write(newBlock.toByteArray());
            return this.randomAccessFileWriter.getFilePointer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(long address, T paData) {

    }

    public T get(long paAddress, T paData) {
        return null;
    }

    public void printBlocks(T paData) {
        for (int i = 0; i < this.sizeNum; i++) {
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
