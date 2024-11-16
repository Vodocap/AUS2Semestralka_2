package sk.uniza.fri.heapfile;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public interface IRecord<T> extends IData<T> {
    int getSize();
    byte[] toByteArray(Object object);
    Object fromByteArray(byte[] pabyteArray);
    T createInstance();


}
