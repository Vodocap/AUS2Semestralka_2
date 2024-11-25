package sk.uniza.fri.heapfile;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public interface IData<T> extends IRecord<T> {
    boolean myEquals(T data);
    T createInstance();
    void print();
    int getHashparameter();
    String getHashParameter();
}
