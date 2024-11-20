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
    private int start;
    private int end;
    private int blockSize;


    public HeapFile(String paFilePath, int paSizeNum, int paBlockSize) {
        this.actualSize = 0;
        this.start = 0;
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

                this.checkStatus(blockInstance);

                this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                this.randomAccessFileWriter.write(blockInstance.toByteArray());

                return blockInstance.getBlockStart();


            } else if (this.emptyBlocks != -1) {

                if ((this.end + this.blockSize > this.sizeNum * this.blockSize) && this.emptyBlocks == this.end) {
                    System.out.println("The Heapfile is full");
                    return -1;
                }

                this.randomAccessFileWriter.seek(this.emptyBlocks);
                Block blockInstance = null;
                if (this.end == this.emptyBlocks) {
                    blockInstance = this.makeEmptyBlockInstance(paData);
                    blockInstance.setBlockStart((int)(this.randomAccessFileWriter.getFilePointer()));
                } else {
                    blockInstance = this.makeBlockInstance(paData);
                    blockInstance.setBlockStart((int)(this.randomAccessFileWriter.getFilePointer() - this.blockSize));
                }


                blockInstance.insertData(paData);
                this.checkStatus(blockInstance);

                if (this.emptyBlocks == this.end) {
                    this.end += this.blockSize;

                    blockInstance.setNext(this.end);
                    blockInstance.setPrevious(-1);

                    this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                    this.randomAccessFileWriter.write(blockInstance.toByteArray());
                    this.actualSize++;
                    this.emptyBlocks = blockInstance.getNext();
                    this.addEmptyBlock(paData);

                } else {

                    this.checkStatus(blockInstance);

                    this.randomAccessFileWriter.seek(blockInstance.getBlockStart());
                    this.randomAccessFileWriter.write(blockInstance.toByteArray());


                }

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
//        try {
////            System.out.println(this.randomAccessFileWriter.getFilePointer());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
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

    private void checkStatus(Block blockInstance) {

        if (blockInstance.isParltyEmpty()) {
            if (this.partlyEmptyBlocks != -1 && this.partlyEmptyBlocks != blockInstance.getBlockStart()) {
                if (blockInstance.hasReferences()) {
                    this.emptyBlocks = blockInstance.getNext();
                    this.mendOldReferences(blockInstance);
                }
                this.insertBlockInBetween(blockInstance, this.partlyEmptyBlocks);

            }
            this.partlyEmptyBlocks = blockInstance.getBlockStart();


        } else if (blockInstance.isEmpty()) {

            if (this.emptyBlocks == this.end) {
                this.emptyBlocks = blockInstance.getBlockStart();
                this.mendOldReferences(blockInstance);
                blockInstance.setNext(this.end);
                blockInstance.setPrevious(-1);

            } else {
                this.partlyEmptyBlocks = blockInstance.getNext();
                this.mendOldReferences(blockInstance);
                this.insertBlockInBetween(blockInstance, this.emptyBlocks);
                this.emptyBlocks = blockInstance.getBlockStart();
            }

            if (blockInstance.getNext() == -1) {
                this.partlyEmptyBlocks = blockInstance.getPrevious();
            }


//            try {
//                this.randomAccessFileWriter.seek(blockInstance.getPrevious());
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

            if (blockInstance.isEmpty()) {
                blockInstance.setNext(this.emptyBlocks);
                if (this.emptyBlocks != this.end) {

                    this.insertBlockInBetween(blockInstance, this.emptyBlocks);
                    this.emptyBlocks = blockInstance.getBlockStart();


                }
            }

        } else if (blockInstance.isFull()) {
            this.partlyEmptyBlocks = -1;
            this.mendOldReferences(blockInstance);
            if (this.emptyBlocks == blockInstance.getBlockStart()) {
                System.out.println("BOLO TAKE " + this.emptyBlocks + " - " + blockInstance.getBlockStart());
                this.emptyBlocks = this.end;
            }
            blockInstance.setPrevious(-1);
            blockInstance.setNext(-1);
        }
    }

    private void mendOldReferences(Block blockInstance) {
        if (blockInstance.getNext() != -1) {
            if (blockInstance.getNext() != this.end) {
                try {
                    this.randomAccessFileWriter.seek(blockInstance.getNext());
                    Block nextBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());
                    nextBlock.setPrevious(blockInstance.getPrevious());

                    this.randomAccessFileWriter.seek(blockInstance.getNext());
                    this.randomAccessFileWriter.write(nextBlock.toByteArray());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }



        if (blockInstance.getPrevious() != -1) {
            try {
                this.randomAccessFileWriter.seek(blockInstance.getPrevious());
                Block previousBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());
                previousBlock.setNext(blockInstance.getNext());

                this.randomAccessFileWriter.seek(blockInstance.getPrevious());
                this.randomAccessFileWriter.write(previousBlock.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void insertBlockInBetween(Block blockInstance, int newNext) {
        try {

            this.randomAccessFileWriter.seek(newNext);
            Block nextBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());



            if (nextBlock.getPrevious() == -1) {
                nextBlock.setPrevious(blockInstance.getBlockStart());

            } else {
                this.randomAccessFileWriter.seek(nextBlock.getPrevious());
                Block previousBlock = this.makeBlockInstance((T)blockInstance.getInstanceCreator());
                previousBlock.setNext(blockInstance.getBlockStart());
                blockInstance.setPrevious(previousBlock.getBlockStart());

                this.randomAccessFileWriter.seek(previousBlock.getBlockStart());
                this.randomAccessFileWriter.write(previousBlock.toByteArray());

            }

            blockInstance.setNext(nextBlock.getBlockStart());

            this.emptyBlocks = blockInstance.getBlockStart();

            this.randomAccessFileWriter.seek(nextBlock.getBlockStart());
            this.randomAccessFileWriter.write(nextBlock.toByteArray());

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

            this.checkStatus(blockInstance);

            this.randomAccessFileWriter.seek(paAdress);
            this.randomAccessFileWriter.write(blockInstance.toByteArray());

//            this.shortenFile(blockInstance);
//            if (blockInstance.isEmpty()) {
//                this.actualSize--;
//            }
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
        System.out.println("Partly Empty Blocks: " + this.emptyBlocks);
        for (int i = 0; i < this.actualSize; i++) {
            try {
                long address = i * this.blockSize;
                System.out.println("ADDRESS PRINTED " + address);;
                this.randomAccessFileWriter.seek(address);
                Block blockInstance = this.makeBlockInstance(paData);
                blockInstance.printBlock();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void printBlock(T paData, int paAddress) {


        try {
            System.out.println("ADDRESS PRINTED " + paAddress);;
            this.randomAccessFileWriter.seek(paAddress);
            Block blockInstance = this.makeBlockInstance(paData);
            blockInstance.printBlock();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
