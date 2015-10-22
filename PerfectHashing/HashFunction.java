package cz.upol.vyjidacek;

import java.security.SecureRandom;

/**
 * Created by Roman Vyjídáček on 06.10.15.
 *
 * Class represents universal hash function. By call construct
 * you get random hash function from set of universal hash function.
 *
 */
public class HashFunction {

    /**
     * Prime that is > than biggest key.
     */
    private final Integer PRIME = 10007;

    /**
     * Table of table size with which is paired.
     */
    private  int tableSize;

    /**
     * Generator of random numbers.
     */
    private SecureRandom random;

    /**
     * Random values unique for each function. This values are used in computing
     * hash value of key.
     */
    private int a, b;

    /**
     * Create instance of hash function.
     * @param tableSize Size of table associate with the hash function.
     */
    public HashFunction(int tableSize) {
        random = new SecureRandom();
        a = getA();
        b = getB();
        this.tableSize = tableSize;
    }

    /**
     * Generate nonzero value for b. Value must ve less than PRIME - 1
     * @return Nonzero int value of b.
     */
    public int getB() {
        int number = random.nextInt(PRIME - 1);

        if (number == 0) {
            number = random.nextInt(PRIME - 1);
        }
        return number;
    }

    /**
     * Generate value for a. Value must ve less than PRIME - 1
     * @return Int value of a
     */
    public int getA() {
        return random.nextInt(PRIME - 1);
    }


    /**
     * Count hash function for key.
     * @param key Value for computing hash value.
     * @return Hash value.
     */
    public int countHash(Integer key) {
        return ((a * key + b) % PRIME) % tableSize;
    }



}
