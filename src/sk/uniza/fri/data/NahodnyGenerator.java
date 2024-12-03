package sk.uniza.fri.data;

import java.util.ArrayList;
import java.util.Random;

/**
 * 15. 11. 2024 - 13:25
 *
 * @author matus
 */
public class NahodnyGenerator {
    private ArrayList<String> vygenerovaneStringy;
    private ArrayList<Integer> vygenerovaneInty;
    private ArrayList<Integer> vygenerovaneID;
    private Random random;
    private int currentID;


    public NahodnyGenerator(int paZaciatokID) {

        this.vygenerovaneStringy = new ArrayList<>();
        this.vygenerovaneInty = new ArrayList<>();
        this.vygenerovaneID = new ArrayList<>();
        this.random = new Random();
        this.currentID = paZaciatokID;
    }

    public int vygenerujUnikatnyInt() {
        int returnInt = this.random.nextInt();
        while (this.vygenerovaneInty.contains(returnInt)) {
            returnInt = this.random.nextInt();
        }
        return returnInt;
    }

    public int vygenerujUnikatneID() {
        int returnInt = this.currentID;
        this.currentID++;
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

            novyString += this.dajCharZCisla(this.returnCharpoolFromIndex(this.random.nextInt(0,3)));
        }
        return novyString;

    }

    public String vygenerujECV() {
        String noveECV = vygenerujString(4);
        while (this.vygenerovaneStringy.contains(noveECV)) {
            noveECV = vygenerujString(4);
        }
        String koniecECV = vygenerujUnikatnyString(6);
        noveECV += koniecECV;


        return noveECV;
    }

    public String vygenerujString(int minDlzka, int maxDlzka) {
        return this.vygenerujString(this.random.nextInt(minDlzka, maxDlzka));

    }

    public char dajCharZCisla(int cisloCharu) {


        return (char) (cisloCharu);
    }

    private char returnCharpoolFromIndex(int paInt) {
        return switch (paInt) {
            case 0 -> this.dajCharZCisla(this.random.nextInt(CharPools.LOWERCASE_CHARACTERS.lowerBound,CharPools.LOWERCASE_CHARACTERS.upperBound));
            case 1 -> this.dajCharZCisla(this.random.nextInt(CharPools.UPPERCASE_CHARACTERS.lowerBound,CharPools.UPPERCASE_CHARACTERS.upperBound));
            case 2 -> this.dajCharZCisla(this.random.nextInt(CharPools.NUMBERS.lowerBound,CharPools.NUMBERS.upperBound));
            default -> ' ';
        };
    }

}
