package sk.uniza.fri.heapfile;

import java.io.FileNotFoundException;
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


    public HeapFile(String paFilePath, int paSizeNum) {
        this.sizeNum = paSizeNum;
        try {
            this.randomAccessFileWriter = new RandomAccessFile(paFilePath, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    public int insert(T data) {
        return 0;
    }

    public void delete(int address, T data) {

    }

    public T get(int paAddress, T paData) {
        return null;
    }
}
