package models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.BinaryOperator;

public class OrderedArrayList<E>
        extends ArrayList<E>
        implements OrderedList<E> {

    protected Comparator<? super E> ordening;   // the comparator that has been used with the latest sort
    protected int nSorted;                      // the number of items that have been ordered by barcode in the list
    // representation-invariant
    //      all items at index positions 0 <= index < nSorted have been ordered by the given ordening comparator
    //      other items at index position nSorted <= index < size() can be in any order amongst themselves
    //              and also relative to the sorted section

    public OrderedArrayList() {
        this(null);
    }

    public OrderedArrayList(Comparator<? super E> ordening) {
        super();
        this.ordening = ordening;
        this.nSorted = 0;
    }

    public Comparator<? super E> getOrdening() {
        return this.ordening;
    }

    @Override
    public void clear() {
        super.clear();
        this.nSorted = 0;
    }

    @Override
    public void sort(Comparator<? super E> c) {
        super.sort(c);
        this.ordening = c;
        this.nSorted = this.size();
    }

    @Override
    public void add(int index, E element) {
        if (index < nSorted)
            nSorted = index;

        super.add(index, element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        if (index < this.nSorted)
            this.nSorted = index - 1;

        return super.addAll(index, c);
    }

    @Override
    public E remove(int index) {
        if (index < nSorted)
            nSorted = index - 1;
        return super.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        if (indexOf(o) < nSorted)
            nSorted = indexOf(o);
        return super.remove(o);
    }

    // TODO override the ArrayList.add(index, item), ArrayList.remove(index) and Collection.remove(object) methods
    //  such that they sustain the representation invariant of OrderedArrayList
    //  (hint: only change nSorted as required to guarantee the representation invariant, do not invoke a sort)

    @Override
    public void sort() {
        if (this.nSorted < this.size()) {
            this.sort(this.ordening);
        }
    }

    @Override
    public int indexOf(Object item) {
        if (item != null)
            return indexOfByRecursiveBinarySearch((E) item);
        else {
            return -1;
        }
        // Binary search

//        if (index != -1)
//            return index;
//        // Linear search
//        // Ignore items before nSorted
//        for (int i = nSorted; i < this.size(); i++) {
//            if (this.get(i).equals(item))
//                return i;
//        }
        // Nothing found
//        return -1;
    }

    private int linearSearch(E searchItem) {
        //do a linear search on the unsorted section
        if (nSorted != -1) {
            for (int i = nSorted; i < this.size(); i++) {
                if (this.ordening.compare(this.get(i), searchItem) == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int indexOfByBinarySearch(E searchItem) {
        if (searchItem != null) {
            // some arbitrary choice to use the iterative or the recursive version
            return indexOfByIterativeBinarySearch(searchItem);
        } else {
            return -1;
        }
    }

    /**
     * finds the position of the searchItem by an iterative binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.ordening
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByIterativeBinarySearch(E searchItem) {

        int left = 0, right = this.nSorted - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;

            // Check if x is present at mid
            if (this.ordening.compare(searchItem, this.get(middle)) == 0)
                return middle;

            if ((this.ordening.compare(searchItem, this.get(middle)) > 0)) {
                // Search right half
                left = middle + 1;
            } else {
                // Search left half
                right = middle - 1;
            }
        }
        return linearSearch(searchItem);
    }


    /**
     * finds the position of the searchItem by a recursive binary search algorithm in the
     * sorted section of the arrayList, using the this.ordening comparator for comparison and equality test.
     * If the item is not found in the sorted section, the unsorted section of the arrayList shall be searched by linear search.
     * The found item shall yield a 0 result from the this.ordening comparator, and that need not to be in agreement with the .equals test.
     * Here we follow the comparator for ordening items and for deciding on equality.
     *
     * @param searchItem the item to be searched on the basis of comparison by this.ordening
     * @return the position index of the found item in the arrayList, or -1 if no item matches the search item.
     */
    public int indexOfByRecursiveBinarySearch(E searchItem) {

        int indexOfByRecursiveResult = indexOfByRecursive(searchItem, 0, nSorted - 1);

        if (indexOfByRecursiveResult != -1) {
            return indexOfByRecursiveResult;
        }
        return linearSearch(searchItem);
        //  return this.indexRecursiveSearch(searchItem, 0, this.nSorted - 1);
    }

    private int indexOfByRecursive(E searchItem, int left, int right) {
        //if from is larger than right then there is no target
        if (left > right) return -1;

        int middle = (left + right) / 2;
        E middleValue = this.get(middle);

        if (this.ordening.compare(middleValue, searchItem) < 0) {
            return indexOfByRecursive(searchItem, middle + 1, right);
        } else if (this.ordening.compare(middleValue, searchItem) > 0) {
            return indexOfByRecursive(searchItem, left, middle - 1);
        } else {
            return middle;
        }
    }

    /**
     * finds a match of newItem in the list and applies the merger operator with the newItem to that match
     * i.e. the found match is replaced by the outcome of the merge between the match and the newItem
     * If no match is found in the list, the newItem is added to the list.
     *
     * @param newItem
     * @param merger  a function that takes two items and returns an item that contains the merged content of
     *                the two items according to some merging rule.
     *                e.g. a merger could add the value of attribute X of the second item
     *                to attribute X of the first item and then return the first item
     * @return whether a new item was added to the list or not
     */
    @Override
    public boolean merge(E newItem, BinaryOperator<E> merger) {
        //No item = return false;
        if (newItem == null) return false;
        //match new item with old item
        int matchedItemIndex = this.indexOfByRecursiveBinarySearch(newItem);
        System.out.println(matchedItemIndex);
        //if there is no match item is simply added
        if (matchedItemIndex < 0) {
            this.add(newItem);
        }
        //else there is a match and we merge the new item and the match
        else {
            E match = this.get(matchedItemIndex);
            E merge = merger.apply(match, newItem);
            this.set(matchedItemIndex, merge);
        }
        return true;
    }
}
