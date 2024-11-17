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
    private RandomAccessFile randomAccessFileWriter;
    private long start;
    private long end;


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

    public long insert(T data) {
        try {

            if (this.partlyEmptyBlocks != -1) {
                this.randomAccessFileWriter.seek(this.partlyEmptyBlocks);
            } else if (this.emptyBlocks != -1) {
                this.randomAccessFileWriter.seek(this.emptyBlocks);
            }
            Block newBlock = new Block<>(this.sizeNum / 10);
            newBlock.insertData(data);
            this.end += 1;
            this.randomAccessFileWriter.write(newBlock.toByteArray());
            return this.randomAccessFileWriter.getFilePointer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(long address, T data) {

    }

    public T get(long paAddress, T paData) {
        return null;
    }

    public void printBlocks() {
        for (int i = 0; i < this.sizeNum; i++) {
            try {
                this.randomAccessFileWriter.seek(i);
                this.randomAccessFileWriter.read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
