package sk.uniza.fri.heapfile;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public interface IRecord<T> extends IData<T> {
    boolean equals(Object data);

}
