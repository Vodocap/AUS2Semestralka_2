package sk.uniza.fri.tester;

import sk.uniza.fri.heapfile.IRecord;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class ZaznamTest implements IRecord {
    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public void fromByteArray(byte[] pabyteArray) {

    }
}
