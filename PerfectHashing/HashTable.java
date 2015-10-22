package cz.upol.vyjidacek;

/**
 * Created by Roman Vyjídáček on 07.10.15.
 *
 * This class represents hash table with two parts, primary and secondary
 * hash tables. Primary table has one hash function. Each secondary has its own
 * hash function without clash.
 *
 */
public class HashTable {

    /**
     * Number of keys which is inserted into table.
     */
    private final int NUMBER_OF_KEYS = 4888;

    /**
     * Representation of hash table with 2D array of Integer.
     */
    private Integer[][] table;

    /**
     * Hash function forprimary table
     */
    private HashFunction mainHash;

    /**
     * Array of secondary hash functions
     */
    private HashFunction[] secondaryHashs;

    /**
     * Create instance of HashTable class.
     */
    public HashTable() {
        mainHash = new HashFunction(NUMBER_OF_KEYS);
        secondaryHashs = new HashFunction[NUMBER_OF_KEYS];
    }

    /**
     * Fill hash table with keys.
     * @param keys array of Integer keys.
     */
    public void fillWithKeys(Integer[] keys) {
        initTable(keys);

        for (Integer key : keys) {
            insert(key);
        }

        //If size of hash table with secondary tables is > than 4 * length of table
        //Get new main hash function and repeat initialization of hash table.
        if (getSize() > 4 * table.length) {
            mainHash = new HashFunction(NUMBER_OF_KEYS);
            table = new Integer[NUMBER_OF_KEYS][];
            fillWithKeys(keys);
        }

    }

    /**
     * Initialize main hash table.
     * @param keys Array of keys
     */
    private void initTable(Integer[] keys) {
        int[] counts = computeCounts(keys);

        //Foreach index i in primary table get value counts[i] and create
        //secondary table of size counts[i]^2 at index i in primary table.
        //If the value counts[i] is 0 to index i insert null.
        for (int i = 0; i < counts.length; i++) {

            if (counts[i] > 0) {
                int itemCount = counts[i];
                table[i] = new Integer[itemCount * itemCount];

            } else {
                table[i] = null;
            }
        }

        createSecondaryhash(keys, counts);
    }

    /**
     * Compute counts of keys that has been hashed into one index at primary table.
     * @param keys Array of keys.
     * @return array of counts at indexes at primary table.
     */
    private int[] computeCounts(Integer[] keys) {
        int[] counts = new int[table.length];

        //Init all indexes in counts to zero.
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }


        //Iterate keys and foreach key count index in primary table and increment this index.
        for (Integer i : keys) {
            int hashIndex = mainHash.countHash(i);
            counts[hashIndex]++;
        }

        return counts;
    }

    /**
     * For each secondary table create hash function without clash.
     * @param keys Array of integers
     * @param counts int array of
     */
    private void createSecondaryhash(Integer[] keys, int[] counts) {

        for (int i = 0; i < table.length; i++) {
            if (counts[i] != 0) {
                Integer[] keysHashToRow = getKeysForRow(i, keys, counts[i]);
                secondaryHashs[i] = findHashWithoutClash(counts[i], keysHashToRow);
            }
        }

    }

    /**
     * Find hash function without clash for keys stored in paramater keyHashToRow.
     * @param count count of keys hashed at the same index.
     * @param keysHashToRow keys hashed at the same index.
     * @return Hash function without clash.
     */
    private HashFunction findHashWithoutClash(int count, Integer[] keysHashToRow) {
        HashFunction secHash;
        boolean hasClash;

        //For each key count hash and test clash. If function hash clash create new function and
        //repeat until find hash function without clash.
        while (true) {
            secHash = new HashFunction(count * count);
            Integer[] row = new Integer[count * count];
            hasClash = false;

            for (Integer key : keysHashToRow) {
                    int index = secHash.countHash(key);

                    if (row[index] == null) {
                        row[index] = key;
                    } else {
                        hasClash = true;
                        break;
                    }
            }

            if (!hasClash) return secHash;

        }

    }

    /**
     * Count Hash for all keys and return the keys hashed at one index.
     * @param row index at primary table.
     * @param keys keys hashed at index row.
     * @param count count of keys hashed at index row.
     * @return Array with keys hashed at the same index.
     */
    private Integer[] getKeysForRow(int row, Integer[] keys, int count) {
        Integer[] rowItem = new Integer[count];
        int rowIndex = 0;

        for (Integer key : keys) {
            int hash = mainHash.countHash(key);

            if (hash == row) {
                rowItem[rowIndex] = key;
                rowIndex++;
            }
        }

        return rowItem;
    }

    /**
     * Insert value into hash table
     * @param value Integer value to insert.
     */
    private void insert(int value) {
        long index = mainHash.countHash(value);
        Integer[] row = table[(int) index];
        HashFunction secondaryHash = secondaryHashs[(int) index];

        int secondaryIndex = secondaryHash.countHash(value);

        if (row[secondaryIndex] != null) {
            System.out.println("Clash!!");
        }

        row[secondaryIndex] = value;


    }

    /**
     * Set size of primary table.
     * @param size Size of table.
     */
    public void setTableSize(int size) {
        table = new Integer[size][];
    }

    /**
     * Get size of table.
     * @return size of primary table.
     */
    public int getSize() {
        int size = 0;
        for (int row = 0; row < table.length; row++) {

            if (table[row] != null) {
                size += table[row].length;
            }
        }
        return size + NUMBER_OF_KEYS;
    }

    /**
     * Try to find value in hash table.
     * @param value Value to find.
     * @return Value if value is at table else -1.
     */
    public int find(int value) {
        int primaryIndex = mainHash.countHash(value);
        Integer[] row = table[primaryIndex];


        if (row == null) return -1;

        try {
            HashFunction secondaryHash = secondaryHashs[primaryIndex];
            int secondaryIndex = secondaryHash.countHash(value);
            Integer result = row[secondaryIndex];
            if (result == value) {
                return value;
            }

            return -1;
        } catch (NullPointerException e) {
            return -1;
        }


    }
}

