package sk.uniza.fri.heapfile;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public interface IRecord<T> {
    int getSize();
    byte[] toByteArray();
    void fromByteArray(byte[] pabyteArray);



}
