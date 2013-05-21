/* (c) Copyright by Man YUAN */
package net.epsilony.tb.rangesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import net.epsilony.tb.pair.PairPack;
import net.epsilony.tb.pair.WithPair;
import net.epsilony.tb.pair.WithPairComparator;

/**
 * <p> A Layered Range Tree, a fractional cascading Range Tree. </br> the build time is \(O(log^d(n)n)\) </br> the
 * search time is \(O(log^{d-1}(n)\) </br>
 * holdind a Layered Rang Tree need \(O(log^d(n)n)\) memory</br>
 * in fact it's about \(72 log^d_2(n)n+32 log^{d-1}_2 n+72
 * log_2(n)n\)dataMemory (bytes) where d means dimension;</br> </p> <p> The whole algorithm is described minutely in
 * Mark de Berg et. al. <i>Computational Geometry Algorithms and Applications(Third Edition)</i> Ch5 </br> It should be
 * pointed out that the input keys should not contain duplicate objects.
 *
 * @version 1.0~beta
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class LayeredRangeTree<K, V> implements RangeSearcher<K, V> {

    private TreeNode root;
    private ArrayList<DictComparator<K>> dictComparators;

    public LayeredRangeTree(Collection<? extends WithPair<K, V>> datas, List<? extends Comparator<K>> comparators) {
        buildTree(comparators, datas);
    }

    public LayeredRangeTree(
            List<? extends K> keys,
            List<? extends V> values,
            List<? extends Comparator<K>> comparators) {
        List<WithPair<K, V>> pairs = PairPack.pack(keys, values, new LinkedList<WithPair<K, V>>());
        buildTree(comparators, pairs);
    }

    public static <T> LayeredRangeTree<T, T> factory(
            List<? extends T> keys, List<? extends Comparator<T>> comparators) {
        return new LayeredRangeTree<>(keys, keys, comparators);
    }

    public void rangeSearch(Collection<? super V> results, K from, K to) {
        results.clear();
        root.rangeSearch(results, from, to);
    }

    @Override
    public LinkedList<V> rangeSearch(K from, K to) {
        LinkedList<V> result = new LinkedList<>();
        rangeSearch(result, from, to);
        return result;
    }

    private void buildTree(
            List<? extends Comparator<K>> comparators, Collection<? extends WithPair<K, V>> datas) {
        dictComparators = new ArrayList<>(comparators.size());
        buildDictComparators(comparators);
        ArrayList<ArrayList<WithPair<K, V>>> sortedDatasByDimension = sortedListsByDimensions(datas);
        if (isContainingDuplicates(sortedDatasByDimension.get(0), 0)) {
            throw new IllegalArgumentException(
                    "The input datas contains two elements which are indistinguishable for each other.");
        }
        root = new TreeNode(sortedDatasByDimension, 0);
    }

    private void buildDictComparators(List<? extends Comparator<K>> comparators) {
        for (int i = 0; i < comparators.size(); i++) {
            DictComparator<K> dictComparator = new DictComparator<>(comparators, i);
            dictComparators.add(dictComparator);
        }
    }

    private ArrayList<ArrayList<WithPair<K, V>>> sortedListsByDimensions(Collection<? extends WithPair<K, V>> pairs) {
        ArrayList<ArrayList<WithPair<K, V>>> sortedPairLists = new ArrayList<>(numDimensions());
        for (int dimensionIndex = 0; dimensionIndex < numDimensions(); dimensionIndex++) {
            ArrayList<WithPair<K, V>> sortedPair = new ArrayList<>(pairs);
            Comparator<WithPair<K, V>> pairComp = new WithPairComparator<>(dictComparators.get(dimensionIndex));
            Collections.sort(sortedPair, pairComp);
            sortedPairLists.add(sortedPair);
        }
        return sortedPairLists;
    }

    private boolean isContainingDuplicates(ArrayList<WithPair<K, V>> sorteds, int primeDictCompareIndex) {
        Comparator<WithPair<K, V>> pairComp = new WithPairComparator<>(dictComparators.get(primeDictCompareIndex));
        for (int i = 1; i < sorteds.size(); i++) {
            if (0 == pairComp.compare(sorteds.get(i - 1), sorteds.get(i))) {
                return true;
            }
        }
        return false;
    }

    private WithPair<K, V> getMidPair(List<? extends WithPair<K, V>> sortedPairs) {
        int midIndex = (sortedPairs.size() - 1) / 2;
        WithPair<K, V> midPair = sortedPairs.get(midIndex);
        return midPair;
    }

    private void subDivide(List<? extends WithPair<K, V>> pairs,
            List<WithPair<K, V>> lessEqualTos,
            List<WithPair<K, V>> greaterThans,
            K divideKey, int dictComparatorIndex) {
        DictComparator<K> comparator = dictComparators.get(dictComparatorIndex);
        for (WithPair<K, V> p : pairs) {
            if (comparator.compare(p.getKey(), divideKey) <= 0) {
                lessEqualTos.add(p);
            } else {
                greaterThans.add(p);
            }
        }
    }

    private int numDimensions() {
        return dictComparators.size();
    }

    private final class TreeNode {

        private final K key;
        private final V value;
        private final int primeDimension;
        private FraCasTree fraCasAssociate = null;
        private TreeNode treeAssociate = null;
        private final TreeNode left;    //<=key
        private final TreeNode right;  //>key

        private TreeNode(List<ArrayList<WithPair<K, V>>> sortedDataLists, int primeDimension) {
            this.primeDimension = primeDimension;
            ArrayList<WithPair<K, V>> treeDatas = sortedDataLists.get(0);

            WithPair<K, V> midPair = getMidPair(treeDatas);
            key = midPair.getKey();
            value = midPair.getValue();

            if (treeDatas.size() == 1) {
                left = null;
                right = null;
                return;
            }

            ArrayList<ArrayList<WithPair<K, V>>> leftSortedDataLists = new ArrayList<>(sortedDataLists.size());
            ArrayList<ArrayList<WithPair<K, V>>> rightSortedDataLists = new ArrayList<>(sortedDataLists.size());
            subDivideLists(sortedDataLists, leftSortedDataLists, rightSortedDataLists);

            buildAssociates(primeDimension, sortedDataLists, leftSortedDataLists, rightSortedDataLists);

            left = new TreeNode(leftSortedDataLists, primeDimension);
            right = new TreeNode(rightSortedDataLists, primeDimension);
        }

        private boolean isLeaf() {
            return left == null;
        }

        private DictComparator<K> dictComparator() {
            return dictComparators.get(primeDimension);
        }

        private void subDivideLists(
                List<? extends List<? extends WithPair<K, V>>> lists,
                List<ArrayList<WithPair<K, V>>> leftLists,
                List<ArrayList<WithPair<K, V>>> rightLists) {
            int listsLength = lists.get(0).size();
            int leftListsLength = (listsLength - 1) / 2 + 1;
            int rightListsLength = listsLength - leftListsLength;
            for (int i = 0; i < lists.size(); i++) {
                ArrayList<WithPair<K, V>> leftSorteds, rightSorteds;
                List<? extends WithPair<K, V>> sortedPairs = lists.get(i);
                leftSorteds = new ArrayList<>(leftListsLength);
                rightSorteds = new ArrayList<>(
                        rightListsLength);
                subDivide(sortedPairs, leftSorteds, rightSorteds, key, primeDimension);

                leftLists.add(leftSorteds);
                rightLists.add(rightSorteds);
            }
        }

        private void buildAssociates(
                int primeDimension,
                List<ArrayList<WithPair<K, V>>> sortedDataLists,
                ArrayList<ArrayList<WithPair<K, V>>> leftSortedDataLists,
                ArrayList<ArrayList<WithPair<K, V>>> rightSortedDataLists) {
            if (isOnLastTwoDimension()) {
                fraCasAssociate = new FraCasTree(
                        sortedDataLists.get(1),
                        leftSortedDataLists.get(1),
                        rightSortedDataLists.get(1));
            } else {
                treeAssociate = new TreeNode(sortedDataLists.subList(1, sortedDataLists.size()), primeDimension + 1);
            }
        }

        private int dictCompare(K o1, K o2) {
            return dictComparator().compare(o1, o2);
        }

        private TreeNode findSplitNode(K from, K to) {
            TreeNode v = this;
            boolean b = dictCompare(to, v.key) <= 0;
            while (!v.isLeaf() && (b || dictCompare(from, v.key) > 0)) {
                v = b ? v.left : v.right;
                b = dictCompare(to, v.key) <= 0;
            }
            return v;
        }

        private void rangeSearch(Collection<? super V> results, K from, K to) {
            TreeNode splitNode = findSplitNode(from, to);
            if (splitNode.isLeaf()) {
                splitNode.checkTo(from, to, results);
            } else {
                final boolean leftSub = true;
                searchOnSplitSubTree(splitNode, from, to, leftSub, results);
                searchOnSplitSubTree(splitNode, from, to, !leftSub, results);
            }
        }

        private int searchCasIndex(K from) {
            if (fraCasAssociate != null) {
                return fraCasAssociate.searchCasIndex(from);
            } else {
                return -1;
            }
        }

        private void rangeSearchOnAssociate(
                Collection<? super V> results, K from, K to, TreeNode father, int casIndex, boolean onLeft) {
            if (null == fraCasAssociate) {
                treeAssociate.rangeSearch(results, from, to);
            } else {
                int fromIndex = father.subTreeCasIndex(onLeft, casIndex);
                fraCasAssociate.checkTo(results, fromIndex, to);
            }
        }

        private int subTreeCasIndex(boolean onLeft, int casIndex) {
            if (null == fraCasAssociate) {
                return -1;
            }
            return onLeft ? fraCasAssociate.leftCas[casIndex] : fraCasAssociate.rightCas[casIndex];
        }

        private void searchOnSplitSubTree(
                TreeNode splitNode, K from, K to, boolean onLeftSub, Collection<? super V> results) {
            TreeNode v = splitNode.getSubTree(onLeftSub);
            int casIndex = v.searchCasIndex(from);

            while (!v.isLeaf() && (v.fraCasAssociate == null || casIndex < v.fraCasAssociate.size())) {
                if (onLeftSub ? dictCompare(from, v.key) <= 0 : dictCompare(v.key, to) <= 0) {
                    TreeNode nd = v.getSubTree(!onLeftSub);
                    if (nd.isLeaf()) {
                        nd.checkTo(from, to, results);
                    } else {
                        nd.rangeSearchOnAssociate(results, from, to, v, casIndex, !onLeftSub);
                    }
                    casIndex = v.subTreeCasIndex(onLeftSub, casIndex);
                    v = v.getSubTree(onLeftSub);
                } else {
                    casIndex = v.subTreeCasIndex(!onLeftSub, casIndex);
                    v = v.getSubTree(!onLeftSub);
                }
            }
            if (v.isLeaf()) {
                v.checkTo(from, to, results);
            }
        }

        private TreeNode getSubTree(boolean leftSub) {
            if (leftSub) {
                return left;
            } else {
                return right;
            }
        }

        private void checkTo(K from, K to, Collection<? super V> results) {
            List<? extends Comparator<K>> comparators = dictComparator().getComparators();
            boolean b = true;
            for (int i = primeDimension; i < comparators.size(); i++) {
                if (comparators.get(i).compare(from, key) > 0 || comparators.get(i).compare(key, to) > 0) {
                    b = false;
                    break;
                }
            }
            if (b) {
                results.add(value);
            }
        }

        private boolean isOnLastTwoDimension() {
            return primeDimension >= numDimensions() - 2;
        }
    }

    /**
     * Fractional Cascading Data, acts as the range tree of the last dimension.
     */
    private final class FraCasTree {

        /**
         *
         * @param sortedDatas
         * @param leftDatas
         * @param rightDatas
         */
        private FraCasTree(
                ArrayList<WithPair<K, V>> sortedDatas,
                ArrayList<WithPair<K, V>> leftDatas,
                ArrayList<WithPair<K, V>> rightDatas) {
            this.keys = new ArrayList<>(sortedDatas.size());
            this.values = new ArrayList<>(sortedDatas.size());
            for (WithPair<K, V> pair : sortedDatas) {
                keys.add(pair.getKey());
                values.add(pair.getValue());
            }
            leftCas = new int[sortedDatas.size()];
            rightCas = new int[sortedDatas.size()];
            int l = 0, r = 0;
            for (int i = 0; i < sortedDatas.size(); i++) {
                leftCas[i] = l;
                rightCas[i] = r;
                if (l < leftDatas.size() && sortedDatas.get(i) == leftDatas.get(l)) {
                    l++;
                } else if (r < rightDatas.size()) {
                    r++;
                }
            }
        }
        //

        private DictComparator<K> dictComparator() {
            return dictComparators.get(dictComparators.size() - 1);
        }
        private ArrayList<K> keys;
        private ArrayList<V> values;
        //fractional cascading keys:
        private int[] leftCas;    //leftCase[i] is the smallest one that left.associate.keys[leftCas[i]]>=keys[i], 
        //if leftCas[i]>left.associate.data it should be -1
        private int[] rightCas;  //like leftCase

        private int searchCasIndex(K from) {
            int fromIndex = Collections.binarySearch(keys, from, dictComparator());
            if (fromIndex < 0) {
                return -fromIndex - 1;
            }
            return fromIndex;
        }

        private void checkTo(Collection<? super V> results, int fromIndex, K to) {
            Comparator<K> comp = dictComparator().comparators.get(dictComparator().getPrimeComparatorIndex());
            for (int i = fromIndex; i < keys.size(); i++) {
                K key = keys.get(i);
                if (comp.compare(key, to) > 0) {
                    return;
                }
                results.add(values.get(i));
            }
        }

        public int size() {
            return keys.size();
        }
    }
}
