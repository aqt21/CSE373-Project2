// Andrew Tran
// Constance La
// CSE 373
// Project 2

package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

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
    private int chainSize;

    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(10);
        this.chainSize = 0;
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
        int hashCode = setHashCode(key);
        if (chains[hashCode] == null || !chains[hashCode].containsKey(key)) {
            throw new NoSuchKeyException();
        }
        return chains[hashCode].get(key);
    }

    @Override
    public void put(K key, V value) {
        int hashCode = setHashCode(key);
        
        if (chains[hashCode] == null) {
            chains[hashCode] = new ArrayDictionary<K, V>();
        }
        int sizeDifference = chains[hashCode].size();
        chains[hashCode].put(key, value);
        this.chainSize += (chains[hashCode].size() - sizeDifference);

        if ((double) this.chainSize / (double) chains.length > 0.75) {
            IDictionary<K, V>[] newChains = makeArrayOfChains(chains.length * 2 - 1);

            for (KVPair<K, V> pair : this) {
                int newHash = pair.getKey().hashCode();
                if (newChains[newHash % newChains.length] == null) {
                    newChains[newHash % newChains.length] = new ArrayDictionary<K, V>();
                }
                newChains[newHash % newChains.length].put(pair.getKey(), pair.getValue());
            }

            chains = newChains;
        } 
    }

    @Override
    public V remove(K key) {
        int hashCode = setHashCode(key);
        
        if (chains[hashCode] == null || !chains[hashCode].containsKey(key)) {
            throw new NoSuchKeyException();
        } else {
            this.chainSize--;
            return chains[hashCode].remove(key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        int hashCode = setHashCode(key);

        if (chains[hashCode] == null) {
            return false;
        } else {
            return chains[hashCode].containsKey(key);
        }
    }

    @Override
    public int size() {
        return this.chainSize;
    }
    
    // Returns hash code for given key
    private int setHashCode(K key) {
        if (key != null) {
            return Math.abs(key.hashCode()) % chains.length;
        } else {
            return 0;
        }
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
            for (int i = current; i < chains.length; i++) {
                if (iter != null) {
                    if (iter.hasNext()) {
                        return true;
                    }
                } 
                if (current == chains.length - 1) {
                    return false;
                }
                this.current++;
                if (chains[current] != null) {
                    iter = chains[current].iterator();
                } else {
                    iter = null;
                }
            }
            return false;

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
