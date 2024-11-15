package sk.uniza.fri.heapfile;

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


    public HeapFile(String paFilePath, int paSizeNum) {
        this.sizeNum = paSizeNum;
        this.filePath = paFilePath;
    }

    public int insert(T data) {
        return 0;
    }

    public void delete(int address, RecordClass paRecord) {

    }

    public T get(int address) {
        return null;
    }
}
