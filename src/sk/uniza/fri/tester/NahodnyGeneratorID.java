package sk.uniza.fri.tester;

import java.util.ArrayList;
import java.util.Random;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class NahodnyGeneratorID {
    private ArrayList<String> vygenerovaneStringy;
    private ArrayList<Integer> vygenerovaneInty;
    private Random random;

    public NahodnyGeneratorID() {
        this.vygenerovaneStringy = new ArrayList<>();
        this.vygenerovaneInty = new ArrayList<>();
        this.random = new Random();
    }

    public int vugenerujUnikatnyInt() {
        int returnInt = this.random.nextInt();
        while (this.vygenerovaneInty.contains(returnInt)) {
            returnInt = this.random.nextInt();
        }
        return returnInt;
    }

    public String vygenerujUnikatnyString(int minDlzka, int maxDlzka) {
        return this.vygenerujUnikatnyString(this.random.nextInt(minDlzka, maxDlzka + 1));
    }

    public String vygenerujUnikatnyString(int paDlzka) {
        String novyString = this.vygenerujString(paDlzka);
        while (this.vygenerovaneStringy.contains(novyString)) {
            novyString = this.vygenerujString(paDlzka);
        }
        return novyString;
    }

    public String vygenerujString(int paDlzka) {
        String novyString = "";
        for (int i = 0; i < paDlzka; i++) {
            novyString += this.vygenerujRandomChar();
        }
        return novyString;

    }

    public char vygenerujRandomChar() {
        return (char) (this.random.nextInt(65,91) + (this.random.nextInt(7,32) * (this.random.nextInt(2))));
    }

}
