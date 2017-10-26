package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import misc.exceptions.NotYetImplementedException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!
    private int size;

    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(10);
        this.size = 0;

        for (int i = 0; i < 10; i++) {
            chains[i] = new ArrayDictionary<K, V>();
        }
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        int hashCode;
        if (key != null) {
            hashCode = Math.abs(key.hashCode()) % 10;
        } else {
            hashCode = 0;
        }
        return chains[hashCode].get(key);
    }

    @Override
    public void put(K key, V value) {
        int hashCode;
        if (key != null) {
            hashCode = Math.abs(key.hashCode()) % 10;
        } else {
            hashCode = 0;
        }
        int sizeDifference = chains[hashCode].size();
        chains[hashCode].put(key, value);

        this.size += (chains[hashCode].size() - sizeDifference);
    }

    @Override
    public V remove(K key) {
        int hashCode;
        if (key != null) {
            hashCode = Math.abs(key.hashCode()) % 10;
        } else {
            hashCode = 0;
        }
        if (chains[hashCode] == null || !chains[hashCode].containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            this.size--;
            return chains[hashCode].remove(key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        int hashCode;
        if (key != null) {
            hashCode = Math.abs(key.hashCode()) % 10;
        } else {
            hashCode = 0;
        }

        if (chains[hashCode] == null) {
            return false;
        } else {
            return chains[hashCode].containsKey(key);
        }
    }
    
    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }
    

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     * 3. Think about what exactly your *invariants* are. An *invariant*
     *    is something that must *always* be true once the constructor is
     *    done setting up the class AND must *always* be true both before and
     *    after you call any method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */

    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int current;
        private Iterator<KVPair<K, V>> iter;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.current = 0;
            if (this.chains[current] != null) {
                this.iter = this.chains[current].iterator();
            }

        }

        @Override
        public boolean hasNext() {
            if (current > 9) {
                return false;
            } else if (iter == null || !iter.hasNext()) {
                if (current == 9) {
                    return false;
                }
                current++;
                if (chains[current] != null) {
                    iter = chains[current].iterator();
                } else {
                    iter = null;
                }
                return hasNext();
            }
            return true;
        }

        @Override
        public KVPair<K, V> next() {
            if (this.hasNext()) {
                return iter.next();
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
