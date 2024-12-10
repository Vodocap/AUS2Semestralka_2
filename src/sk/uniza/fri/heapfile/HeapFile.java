package sk.uniza.fri.heapfile;

import java.io.*;
import java.util.ArrayList;

/**
 * 15. 11. 2024 - 13:25
 *
 * @param <T> v HeapFile sú len tie bloke ktoré implementujú interface IDATA
 * @author matus
 */
public class HeapFile<T extends IData> {
    private long emptyBlocks;
    private long partlyEmptyBlocks;
    private int numberOfBlocks;
    private RandomAccessFile randomAccessFileWriter;
    private long end;
    private long blockSize;


    /**
     * Instantiates a new Heap file.
     * Konštruktor
     * @param paFilePath  cesta k súboru v ktorom sa budú nachádzať dáta
     * @param paBlockSize veľkosť blokov v súbore
     */
    public HeapFile(String paFilePath, int paBlockSize) {
        this.numberOfBlocks = 0;
        try {
            this.randomAccessFileWriter = new RandomAccessFile(paFilePath, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.end = 0;
        this.emptyBlocks = 0;
        this.partlyEmptyBlocks = -1;
        this.blockSize = paBlockSize;


    }

    /**
     * Inicializuje heapfile zo súboru.
     *
     * @param initFilePath cesta k súboru z ktorého sa inicializuje
     */
    public void initialiseHeapFileFromFile(String initFilePath) {
        try {
            RandomAccessFile loader = new RandomAccessFile(initFilePath, "rw");
            byte[] initBytes = new byte[32];
            loader.seek(0);
            loader.readFully(initBytes);
            this.fromByteArray(initBytes);
            loader.close();

            System.out.println("HEAPFILE INITIALISED: ");
            System.out.println(this.toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Insert long.
     *
     * @param paData záznam ktorý sa do štruktúry vloží
     * @return adresa na ktorej bol záznam vložený
     */
    public long insert(T paData) {
        try {

            if (this.partlyEmptyBlocks != -1) {

                this.randomAccessFileWriter.seek(this.partlyEmptyBlocks);
                Block blockInstance = this.makeBlockInstance(paData);
                blockInstance.insertData(paData);
                blockInstance.setBlockStart((int)(this.randomAccessFileWriter.getFilePointer() - this.blockSize));

                this.checkStatusInsert(blockInstance);

                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                this.randomAccessFileWriter.write(blockInstance.toByteArray());

                return blockInstance.getBlockStart();


            } else {

                if (this.emptyBlocks == -1 || this.emptyBlocks == this.end) {
                    this.randomAccessFileWriter.seek(this.end);
                    this.numberOfBlocks++;
                } else {
                    this.randomAccessFileWriter.seek(this.emptyBlocks);
                }


                Block blockInstance = null;



                if (this.end == this.emptyBlocks || this.emptyBlocks == -1) {
                    this.end += this.blockSize;
                    blockInstance = this.makeEmptyBlockInstance(paData);
                    blockInstance.setBlockStart((int)(this.randomAccessFileWriter.getFilePointer()));

                    blockInstance.insertData(paData);

                    this.checkStatusInsert(blockInstance);
                    this.randomAccessFileWriter.write(blockInstance.toByteArray());

                } else {
                    blockInstance = this.makeBlockInstance(paData);
                    blockInstance.setBlockStart((int)(this.randomAccessFileWriter.getFilePointer() - this.blockSize));

                    blockInstance.insertData(paData);

                    this.checkStatusInsert(blockInstance);
                    this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                    this.randomAccessFileWriter.write(blockInstance.toByteArray());
                }


                return blockInstance.getBlockStart();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    private Block makeBlockInstance(T paData) {
        Block blockInstance = new Block(paData, this.blockSize);
        blockInstance.fromByteArray(this.readBlock());
        return blockInstance;
    }

    private Block makeEmptyBlockInstance(T paData) {
        Block blockInstance = new Block(paData, this.blockSize);
        return blockInstance;
    }


    private byte[] readBlock() {
        byte[] readBlock = new byte[(int)this.blockSize];
        try {
            this.randomAccessFileWriter.readFully(readBlock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readBlock;
    }

    private void checkStatusInsert(Block blockInstance) {

        if (blockInstance.isFull()) {


            this.mendOldReferences(blockInstance);



            if (blockInstance.getBlockStart() == this.partlyEmptyBlocks) {
                this.partlyEmptyBlocks = blockInstance.getNext();
            }







            if (blockInstance.getValidCount() == 1) {
                this.emptyBlocks = this.end;
            }

            blockInstance.setPrevious(-1);
            blockInstance.setNext(-1);

        } else if (blockInstance.isPartlyEmpty() && blockInstance.getValidCount() == 1) {



            this.mendOldReferences(blockInstance);


            if (blockInstance.getNext() == -1 && blockInstance.getPrevious() == -1) {

                this.emptyBlocks = this.end;
            } else {

                this.emptyBlocks = blockInstance.getNext();

            }


            if (this.partlyEmptyBlocks != -1) {

                this.insertBlockInFront(blockInstance, this.partlyEmptyBlocks);

            } else {

                blockInstance.setNext(-1);
                blockInstance.setPrevious(-1);

            }
            this.partlyEmptyBlocks = blockInstance.getBlockStart();

        }



    }

    private void checkStatusDelete(Block blockInstance) {

        if (blockInstance.isPartlyEmpty() && blockInstance.isAlmostFull()) {

            if (this.partlyEmptyBlocks != -1) {

                this.insertBlockInFront(blockInstance, this.partlyEmptyBlocks);
            } else {
                blockInstance.setPrevious(-1);
                blockInstance.setNext(-1);
            }

            this.partlyEmptyBlocks = blockInstance.getBlockStart();


        } else if (blockInstance.isEmpty()) {

            this.mendOldReferences(blockInstance);

            if (blockInstance.getBlockStart() == this.partlyEmptyBlocks) {
                this.partlyEmptyBlocks = blockInstance.getNext();
            }


                if (this.emptyBlocks == this.end || this.emptyBlocks == -1) {
                    blockInstance.setNext(-1);
                    blockInstance.setPrevious(-1);




                } else {

                    this.insertBlockInFront(blockInstance, this.emptyBlocks);
                }

                this.emptyBlocks = blockInstance.getBlockStart();





        }


    }


    private void mendOldReferences(Block blockInstance) {

        if (blockInstance.getPrevious() != -1) {
            try {
                this.randomAccessFileWriter.seek(blockInstance.getPrevious());
                Block previousBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());

                if (blockInstance.getNext() != this.end) {
                    previousBlock.setNext(blockInstance.getNext());
                } else {
                    previousBlock.setNext(-1);
                }

                System.out.println("PREVIOUS " + previousBlock.getBlockStart() + " NOW POINTS TO - > " + previousBlock.getNext());

                this.randomAccessFileWriter.seek(previousBlock.getBlockStart());
                this.randomAccessFileWriter.write(previousBlock.toByteArray());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        if (blockInstance.getNext() != -1) {
                try {
                    this.randomAccessFileWriter.seek(blockInstance.getNext());
                    Block nextBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());




                    nextBlock.setPrevious(blockInstance.getPrevious());

                    this.randomAccessFileWriter.seek(nextBlock.getBlockStart());
                    this.randomAccessFileWriter.write(nextBlock.toByteArray());



                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }



    }


    private void insertBlockInFront(Block blockInstance, long newNext) {
        try {



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

    /**
     * Shorten file.
     * Skráti súbor
     * @param blockInstance inštancia bloku od ktorej sa má súbor skracovať
     */
    public void shortenFile(Block blockInstance) {

        if (blockInstance.isEmpty()) {

            int numberOfEmptyBlocks = 0;

            while (blockInstance.isEmpty() && blockInstance.getBlockStart() - this.blockSize > 0) {
                try {

                        this.mendOldReferences(blockInstance);


                        if (this.emptyBlocks == blockInstance.getBlockStart()) {

                            this.emptyBlocks = blockInstance.getNext();


                        }

                        numberOfEmptyBlocks++;


                    this.randomAccessFileWriter.seek(blockInstance.getBlockStart() - this.blockSize);
                    blockInstance = this.makeBlockInstance((T) blockInstance.getInstanceCreator());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

            this.end -= this.blockSize * numberOfEmptyBlocks;
            this.numberOfBlocks -= numberOfEmptyBlocks;
            try {
                this.randomAccessFileWriter.setLength(this.end);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }

    }


    /**
     * Delete.
     * Vymaže záznam zo štruktúry
     * @param paAdress adreza na ktorej očakávame záznam
     * @param paData   inštancia očakávaného záznamu na mazanie z operačnej pamäti
     */
    public void delete(long paAdress, T paData) {
        try {

            this.randomAccessFileWriter.seek(paAdress);

            Block blockInstance = this.makeBlockInstance(paData);

            blockInstance.removeData(paData);

            this.checkStatusDelete(blockInstance);

            if (blockInstance.isEmpty() && blockInstance.getBlockStart() == this.end - this.blockSize) {
                this.shortenFile(blockInstance);
                return;
            }

            this.randomAccessFileWriter.seek(paAdress);
            this.randomAccessFileWriter.write(blockInstance.toByteArray());




        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Update.
     * Vymení starý záznam za nový záznam
     *
     * @param paAddress adresa na ktorej sa nachádza blok
     * @param oldData   starý záznam
     * @param newData   nový záznam
     */
    public void update(long paAddress, T oldData, T newData) {
        try {
            this.randomAccessFileWriter.seek(paAddress);
            Block foundBlock = this.makeBlockInstance(oldData);
            foundBlock.removeData(oldData);
            foundBlock.insertData(newData);
            this.randomAccessFileWriter.seek(paAddress);
            this.randomAccessFileWriter.write(foundBlock.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Get.
     *
     * @param paAddress na ktorej očakávame blok
     * @param paData    parametrický záznam ktorý sa porovná
     * @return vráti záznam dát zo súboru
     */
    public T get(long paAddress, T paData) {
        try {
            this.randomAccessFileWriter.seek(paAddress);
            Block foundBlock = this.makeBlockInstance(paData);
            return (T) foundBlock.getRecord(paData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Print blocks.
     *
     * @param paData the pa data
     */
    public void printBlocks(T paData) {
        System.out.println("HeapFile size: " + this.numberOfBlocks);
        System.out.println("Empty Blocks: " + this.emptyBlocks);
        System.out.println("Partly Empty Blocks: " + this.partlyEmptyBlocks);
        for (int i = 0; i < this.numberOfBlocks; i++) {
            long address = i * this.blockSize;
            System.out.println("ADDRESS PRINTED " + address);;
            this.printBlock(paData, address);

        }
    }

    @Override
    public String toString() {
        return "HeapFile{" +
                "emptyBlocks=" + emptyBlocks +
                ", partlyEmptyBlocks=" + partlyEmptyBlocks +
                ", numberOfBlocks=" + numberOfBlocks +
                ", randomAccessFileWriter=" + randomAccessFileWriter +
                ", end=" + end +
                ", blockSize=" + blockSize +
                '}';
    }

    /**
     * To string string.
     *
     * @param paData the pa data
     * @return the string
     */
    public String toString(T paData) {
        String resultString = "Number of blocks: " + this.numberOfBlocks + "\nEmpty Blocks: " + this.emptyBlocks +
                "\nPartly Empty Blocks: " + this.partlyEmptyBlocks + "\n";
        for (int i = 0; i < this.numberOfBlocks; i++) {
            long address = i * this.blockSize;
            resultString += this.toStringBlock(paData, address);
            this.printBlock(paData, address);
        }

        return resultString;
    }

    /**
     * Gets all blocks.
     *
     * @param paData the pa data
     * @return the all blocks
     */
    public ArrayList<Block> getAllBlocks(T paData) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (int i = 0; i < this.numberOfBlocks; i++) {
            long address = i * this.blockSize;
            try {
                this.randomAccessFileWriter.seek(address);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Block blockInstance = this.makeBlockInstance(paData);
            blocks.add(blockInstance);
            blockInstance.printBlock();

        }

        return blocks;

    }


    /**
     * To string block string.
     *
     * @param paData    the pa data
     * @param paAddress the pa address
     * @return the string
     */
    public String toStringBlock(T paData, long paAddress) {

        try {
            if (paAddress <= this.end) {
                this.randomAccessFileWriter.seek(paAddress);

                Block blockInstance = this.makeBlockInstance(paData);
                return blockInstance.toString();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "";

    }

    /**
     * Print block.
     *
     * @param paData    the pa data
     * @param paAddress the pa address
     */
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

    /**
     * Zavrie .
     *
     * @param saveFilePath the save file path
     */
    public void closeHeapFile(String saveFilePath) {
        try {
            RandomAccessFile saver = new RandomAccessFile(saveFilePath, "rw");
            if (saver.length() < 1) {
                saver.seek(saver.length());

            } else {
                saver.seek(0);
            }
            saver.write(this.toByteArray());
            this.randomAccessFileWriter.close();
            saver.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {

            hlpOutStream.writeLong(this.end);
            hlpOutStream.writeLong(this.emptyBlocks);
            hlpOutStream.writeLong(this.partlyEmptyBlocks);
            hlpOutStream.writeLong(this.blockSize);

            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion to byte array.");
        }
    }

    private void fromByteArray(byte[] paByteArray) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(paByteArray);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {

            this.end = hlpInStream.readLong();
            this.emptyBlocks = hlpInStream.readLong();
            this.partlyEmptyBlocks = hlpInStream.readLong();
            this.blockSize = hlpInStream.readLong();
            this.numberOfBlocks = (int) (this.end / this.blockSize);



        } catch (IOException e) {
            throw new IllegalStateException("Error during conversion from byte array.");
        }
    }
}
