package sk.uniza.fri.heapfile;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public interface IRecord<T> {
    long getSize();
    byte[] toByteArray();
    void fromByteArray(byte[] pabyteArray);



}
