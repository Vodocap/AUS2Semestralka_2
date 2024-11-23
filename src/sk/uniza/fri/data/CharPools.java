package sk.uniza.fri.data;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public enum CharPools {
    LOWERCASE_CHARACTERS(97, 122),
    UPPERCASE_CHARACTERS(65, 90),
    NUMBERS(48, 57);

    public int lowerBound;
    public int upperBound;

    CharPools(int paLowerBound, int paUpperBound) {
        this.lowerBound = paLowerBound;
        this.upperBound = paUpperBound;
    }



}
