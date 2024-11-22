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
    private int actualSize;
    private RandomAccessFile randomAccessFileWriter;

    private int end;
    private int blockSize;


    public HeapFile(String paFilePath, int paSizeNum, int paBlockSize) {
        this.actualSize = 0;
        this.end = 0;
        this.sizeNum = paSizeNum;
        this.emptyBlocks = 0;
        this.partlyEmptyBlocks = -1;
        this.blockSize = paBlockSize;
        try {
            this.randomAccessFileWriter = new RandomAccessFile(paFilePath, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


    }


    public int insert(T paData) {
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


            } else if (this.emptyBlocks != -1) {

                if (this.emptyBlocks == this.end) {
                    this.randomAccessFileWriter.seek(this.end);
//                    this.addEmptyBlock(paData);
                    this.actualSize++;

                }

                if ((this.end + this.blockSize > this.sizeNum * this.blockSize) && this.emptyBlocks == this.end) {
                    System.out.println("The Heapfile is full");
                    return -1;
                }

                this.randomAccessFileWriter.seek(this.emptyBlocks);
                Block blockInstance = null;
                if (this.end == this.emptyBlocks) {
                    this.end += this.blockSize;
                    blockInstance = this.makeEmptyBlockInstance(paData);
                    blockInstance.setBlockStart((int)(this.randomAccessFileWriter.getFilePointer()));


                    if (blockInstance.getNext() == blockInstance.getBlockStart()) {
                        System.out.println("SAM SEBA NEXT MENDOLD GETPREV");
                    }

                } else {
                    blockInstance = this.makeBlockInstance(paData);
                    blockInstance.setBlockStart((int)(this.randomAccessFileWriter.getFilePointer() - this.blockSize));
                }


                blockInstance.insertData(paData);

                this.checkStatusInsert(blockInstance);

                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                this.randomAccessFileWriter.write(blockInstance.toByteArray());

                return blockInstance.getBlockStart();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }

    private void addEmptyBlock(T paData) {
        Block emptyBlock = new Block<>(paData, this.blockSize);
        try {
            this.randomAccessFileWriter.write(emptyBlock.toByteArray());

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
        byte[] readBlock = new byte[this.blockSize];
        try {
            this.randomAccessFileWriter.readFully(readBlock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readBlock;
    }

    private void checkStatusInsert(Block blockInstance) {

        if (blockInstance.isFull()) {

            System.out.println("*************************************************");
            System.out.println("Partly empty stare " + this.partlyEmptyBlocks);
            this.partlyEmptyBlocks = blockInstance.getPrevious();
            System.out.println("Partly empty nove " + this.partlyEmptyBlocks);
            System.out.println("*************************************************");

            System.out.println("REF BEFORE MENDING TO FULL " + blockInstance.getPrevious() + " < - - > " + blockInstance.getNext());
            this.mendOldReferences(blockInstance);


            System.out.println("EMPTY NOVE " + this.emptyBlocks);

            blockInstance.setPrevious(-1);
            blockInstance.setNext(-1);

        } else if (blockInstance.isPartlyEmpty() && blockInstance.getValidCount() == 1) {


            System.out.println("REF BEFORE MENDING FROM EMPTY " + blockInstance.getPrevious() + " WILL POINT TO - > " + blockInstance.getNext());
            this.mendOldReferences(blockInstance);
            System.out.println("REF BEFORE MENDING FROM EMPTY " + blockInstance.getPrevious() + " POINTS TO - > " + blockInstance.getNext());

            if (blockInstance.getNext() == -1 && blockInstance.getPrevious() == -1 && this.end != (this.sizeNum * this.blockSize)) {
                System.out.println("SETTING TO END FROM - " + this.emptyBlocks);
                this.emptyBlocks = this.end;
            } else {

                this.emptyBlocks = blockInstance.getNext();
//                this.disconnectFromNext(blockInstance, blockInstance.getNext());
            }


            if (this.partlyEmptyBlocks != -1) {

                if (this.partlyEmptyBlocks != blockInstance.getBlockStart()) {
                    System.out.println("INSERT NEW PARTLY EMPTY");
                    this.insertBlockInFront(blockInstance, this.partlyEmptyBlocks);
                }

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
                System.out.println("DELETE PARLTY EMPTY FROM FULL");
                if (this.partlyEmptyBlocks != blockInstance.getBlockStart()) {
                    this.insertBlockInFront(blockInstance, this.partlyEmptyBlocks);
                } else {
                    System.out.println("konec a");
                }
            } else {
                blockInstance.setPrevious(-1);
                blockInstance.setNext(-1);
            }

            this.partlyEmptyBlocks = blockInstance.getBlockStart();


        } else if (blockInstance.isEmpty()) {


            if (blockInstance.getNext() != -1) {
                System.out.println("//////////////////////////////////////////////////////////////");
                System.out.println("NEXT BEFORE MENDING: ");
                this.printBlock((T)blockInstance.getInstanceCreator(), blockInstance.getNext());
            }

            this.mendOldReferences(blockInstance);


            this.partlyEmptyBlocks = blockInstance.getPrevious();

            if (blockInstance.getNext() != -1) {
                System.out.println("NEXT AFTER MENDING: ");
                this.printBlock((T)blockInstance.getInstanceCreator(), blockInstance.getNext());
                System.out.println("//////////////////////////////////////////////////////////////");
            }


            if (this.emptyBlocks == this.end) {

                blockInstance.setNext(-1);
                blockInstance.setPrevious(-1);
                System.out.println("SETTING AS END " + blockInstance.getNext());


            } else {
                System.out.println("DELETE NEW EMPTY");
                if (this.emptyBlocks == -1) {
                    System.out.println("adsdas");
                }
                this.insertBlockInFront(blockInstance, this.emptyBlocks);
            }

            this.emptyBlocks = blockInstance.getBlockStart();


        }

        if (blockInstance.getPrevious() == blockInstance.getBlockStart()) {
            System.out.println("JE KONEC P");
            System.out.println(blockInstance.getPrevious());
            this.printBlock((T)blockInstance.getInstanceCreator(), blockInstance.getBlockStart());
            System.out.println("____________________________________");
        } else if (blockInstance.getNext() == blockInstance.getBlockStart()) {
            System.out.println("JE KONEC N");
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
            if (blockInstance.getNext() != this.end) {
                try {
                    this.randomAccessFileWriter.seek(blockInstance.getNext());
                    Block nextBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());


                    System.out.println("Previous - " + nextBlock.getPrevious());

                    nextBlock.setPrevious(blockInstance.getPrevious());

                    this.randomAccessFileWriter.seek(nextBlock.getBlockStart());
                    this.randomAccessFileWriter.write(nextBlock.toByteArray());
                    System.out.println("NEXT " + nextBlock.getBlockStart() + " NOW POINTS TO - > " + nextBlock.getPrevious());
                    System.out.println("New previous - " + nextBlock.getPrevious());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }



    }

    private void disconnectFromNext(Block blockInstance, int nextAddress) {

        if (nextAddress != -1) {
            if (nextAddress != this.end) {
                try {
                    this.randomAccessFileWriter.seek(nextAddress);
                    Block nextBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());


                    System.out.println("Previous - " + nextBlock.getPrevious());

                    nextBlock.setPrevious(-1);

                    this.randomAccessFileWriter.seek(nextAddress);
                    this.randomAccessFileWriter.write(nextBlock.toByteArray());
                    System.out.println("NEXT " + nextBlock.getBlockStart() + " NOW POINTS TO - > " + nextBlock.getPrevious());
                    System.out.println("New previous - " + nextBlock.getPrevious());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }



    }

    private void insertBlockInFront(Block blockInstance, int newNext) {
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

    public void shortenFile(Block blockInstance) {
        if (blockInstance.getBlockStart() + this.blockSize == this.end) {
            int numberOfEmptyBlocks = 0;
            while (blockInstance.isEmpty()) {
                try {
                    this.randomAccessFileWriter.seek(blockInstance.getBlockStart() - this.blockSize);
                    blockInstance = this.makeBlockInstance((T) blockInstance.getInstanceCreator());
                    numberOfEmptyBlocks++;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

            this.actualSize -= numberOfEmptyBlocks;
        }

    }


    public void delete(int paAdress, T paData) {
        try {

            this.randomAccessFileWriter.seek(paAdress);

            Block blockInstance = this.makeBlockInstance(paData);
            blockInstance.removeData(paData);

            this.checkStatusDelete(blockInstance);

            this.randomAccessFileWriter.seek(paAdress);
            this.randomAccessFileWriter.write(blockInstance.toByteArray());


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public T get(long paAddress, T paData) {
        try {
            this.randomAccessFileWriter.seek(paAddress);
            Block foundBlock = this.makeBlockInstance(paData);
            return (T) foundBlock.getRecord(paData);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void printBlocks(T paData) {
        System.out.println("HeapFile size: " + this.actualSize);
        System.out.println("Empty Blocks: " + this.emptyBlocks);
        System.out.println("Partly Empty Blocks: " + this.partlyEmptyBlocks);
        for (int i = 0; i < this.actualSize; i++) {
            int address = i * this.blockSize;
            System.out.println("ADDRESS PRINTED " + address);;
            this.printBlock(paData, address);

        }
    }

    public void printBlock(T paData, int paAddress) {


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
}
