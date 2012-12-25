/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.util.rangesearch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p> A Layered Range Tree, a fractional cascading Range Tree. </br> the build
 * time is O(log<sup>d</sup>(n)n) </br> the search time is
 * O(log<sup>(d-1)</sup>(n)) </br> holdind a Layered Rang Tree need
 * O(log<sup>d</sup>(n)n) memory</br> in fact it's about
 * 72*log<sup>d</sup><sub>2</sub>(n)*n+32*log<sup>d-1</sup><sub>2</sub>*n+72*log<sub>2</sub>(n)*n+dataMemory
 * (bytes) where d means dimension;</br> </p> <p> The whole algorithm is
 * described minutely in Mark de Berg et. al. <i>Computational Geometry
 * Algorithms and Applications(Third Edition)</i> Ch5 </br> It should be pointed
 * out that the input datas should not contain duplicate objects.
 *
 * @param E
 * @author Man YUAN <epsilonyuan@gmail.com>
 */
public class LayeredRangeTree<E> {

    TreeNode<E> root;

    /**
     *
     * @param datas
     * @param comparators
     */
    public LayeredRangeTree(Collection<? extends E> datas, List<? extends Comparator<E>> comparators) {
        ArrayList<Comparator<E>> comps = new ArrayList<>(comparators);
        ArrayList<DictComparator<E>> dictComparators = new ArrayList<>(comps.size());
        ArrayList<ArrayList<E>> sortedDatas = new ArrayList<>(comps.size());
        for (int i = 0; i < comps.size(); i++) {
            DictComparator<E> dictComparator = new DictComparator<>(comps, false, i);
            dictComparators.add(dictComparator);
            ArrayList<E> sDatas = new ArrayList<>(datas);
            Collections.sort(sDatas, dictComparator);
            if (i == 0) {
                for (int j = 1; j < sDatas.size(); j++) {
                    if (0 == dictComparator.compare(sDatas.get(j - 1), sDatas.get(j))) {
                        throw new IllegalArgumentException("The input datas contains two elements which are indistinguishable for each other.");
                    }
                }
            }
            sortedDatas.add(sDatas);
        }

        root = new TreeNode<>(dictComparators, sortedDatas, 0);

    }

    public void rangeSearch(Collection<? super E> results, E from, E to) {
        results.clear();
        root.rangeQuary(results, from, to);
    }
}

final class TreeNode<T> {

    boolean isLeaf() {
        return left == null;
    }
    T key;
    DictComparator<T> dictComparator;
    FraCasData<T> fraCasData;
    TreeNode<T> associateTree;
    TreeNode<T> left;    //<=key
    TreeNode<T> right;  //>key

    TreeNode(ArrayList<DictComparator<T>> dictComparators, ArrayList<ArrayList<T>> sortedDatas, int mainKeyIndex) {
        ArrayList<T> datas = sortedDatas.get(mainKeyIndex);
        dictComparator = dictComparators.get(mainKeyIndex);
        if (datas.size() == 1) {
            key = datas.get(0);
        } else {
            int midIndex = (datas.size() - 1) / 2;
            key = datas.get(midIndex);
            ArrayList<ArrayList<T>> leftSorted = new ArrayList<>(sortedDatas.size());
            ArrayList<ArrayList<T>> rightSorted = new ArrayList<>(sortedDatas.size());

            for (int i = 0; i < sortedDatas.size(); i++) {
                ArrayList<T> lefts, rights;
                if (i < mainKeyIndex) {
                    lefts = null;
                    rights = null;
                } else {
                    List<T> sd = sortedDatas.get(i);
                    lefts = new ArrayList<>(midIndex + 1);
                    rights = new ArrayList<>(datas.size() - midIndex - 1);
                    for (T t : sd) {
                        if (dictComparator.compare(t, key) <= 0) {
                            lefts.add(t);
                        } else {
                            rights.add(t);
                        }
                    }
                }
                leftSorted.add(lefts);
                rightSorted.add(rights);
            }
            if (mainKeyIndex < dictComparators.size() - 2) {
                associateTree = new TreeNode(dictComparators, sortedDatas, mainKeyIndex + 1);
            } else {
                fraCasData = new FraCasData<>(
                        dictComparators.get(mainKeyIndex + 1),
                        sortedDatas.get(mainKeyIndex + 1),
                        leftSorted.get(mainKeyIndex + 1),
                        rightSorted.get(mainKeyIndex + 1));
            }
            left = new TreeNode<>(dictComparators, leftSorted, mainKeyIndex);
            right = new TreeNode<>(dictComparators, rightSorted, mainKeyIndex);
        }
    }

    public int dictCompare(T o1, T o2) {
        return dictComparator.compare(o1, o2);
    }

    TreeNode<T> getSplitNode(T from, T to) {
        TreeNode<T> v = this;
        boolean b = dictCompare(to, v.key) <= 0;
        while (!v.isLeaf() && (b || dictCompare(from, v.key) > 0)) {
            v = b ? v.left : v.right;
            b = dictCompare(to, v.key) <= 0;
        }
        return v;
    }

    void rangeQuary(Collection<? super T> results, T from, T to) {
        TreeNode<T> vs = getSplitNode(from, to);
        if (vs.isLeaf()) {
            checkTo(from, to, vs, results);
        } else {
            if (dictComparator.getMainKey() < dictComparator.getKeyDimensionSize() - 2) {
                TreeNode<T> v = vs.left;
                while (!v.isLeaf()) {
                    if (dictCompare(from, v.key) <= 0) {
                        v.right.rangeQuary(results, from, to);
                        v = v.left;
                    } else {
                        v = v.right;
                    }
                }
                checkTo(from, to, v, results);
                v = vs.right;
                while (!v.isLeaf()) {
                    if (dictCompare(v.key, to) <= 0) {
                        v.left.rangeQuary(results, from, to);
                        v = v.right;
                    } else {
                        v = v.left;
                    }
                }
                checkTo(from, to, v, results);
            } else {
                TreeNode<T> v = vs.left;
                int casIndex = 0;
                if (!v.isLeaf()) {
                    casIndex = v.fraCasData.searchCasIndex(from);
                }
                while (!v.isLeaf() && casIndex < v.fraCasData.datas.size()) {
                    if (dictCompare(from, v.key) <= 0) {
                        if (v.right.isLeaf()) {
                            checkTo(from, to, v.right, results);
                        } else {
                            v.right.fraCasData.checkTo(results, v.fraCasData.rightCas[casIndex], to);
                        }
                        casIndex = v.fraCasData.leftCas[casIndex];
                        v = v.left;
                    } else {
                        casIndex = v.fraCasData.rightCas[casIndex];
                        v = v.right;
                    }
                }
                if (v.isLeaf()) {
                    checkTo(from, to, v, results);
                }

                v = vs.right;
                if (!v.isLeaf()) {
                    casIndex = v.fraCasData.searchCasIndex(from);
                }
                while (!v.isLeaf() && casIndex < v.fraCasData.datas.size()) {
                    if (dictCompare(v.key, to) <= 0) {
                        if (v.left.isLeaf()) {
                            checkTo(from, to, v.left, results);
                        } else {
                            v.left.fraCasData.checkTo(results, v.fraCasData.leftCas[casIndex], to);
                        }
                        casIndex = v.fraCasData.rightCas[casIndex];
                        v = v.right;
                    } else {
                        casIndex = v.fraCasData.leftCas[casIndex];
                        v = v.left;
                    }
                }
                if (v.isLeaf()) {
                    checkTo(from, to, v, results);
                }
            }
        }
    }

    void checkTo(T from, T to, TreeNode<T> nd, Collection<? super T> results) {
        List<? extends Comparator> comparators = dictComparator.getComparators();
        boolean b = true;
        T nodeKey = nd.key;
        for (int i = dictComparator.getMainKey(); i < comparators.size(); i++) {
            if (comparators.get(i).compare(from, nodeKey) > 0 || comparators.get(i).compare(nodeKey, to) > 0) {
                b = false;
                break;
            }
        }
        if (b) {
            results.add(nodeKey);
        }
    }
}

/**
 * Fractional Cascading Data, acts as the range tree of the last dimension.
 */
final class FraCasData<T> {

    /**
     *
     * @param dictComparator
     * @param datas
     * @param leftDatas
     * @param rightDatas
     */
    FraCasData(DictComparator<T> dictComp, ArrayList<T> datas, ArrayList<T> leftDatas, ArrayList<T> rightDatas) {
        this.dictComparator = dictComp;
        this.datas = datas;
        leftCas = new int[datas.size()];
        rightCas = new int[datas.size()];
        int l = 0, r = 0;
        for (int i = 0; i < datas.size(); i++) {
            leftCas[i] = l;
            rightCas[i] = r;
            if (l < leftDatas.size() && datas.get(i) == leftDatas.get(l)) {
                l++;
            } else if (r < rightDatas.size()) {
                r++;
            }
        }
    }
    //
    DictComparator<T> dictComparator;
    ArrayList<T> datas;
    //fractional cascading datas:
    int[] leftCas;    //leftCase[i] is the smallest one that left.associate.datas[leftCas[i]]>=datas[i], if leftCas[i]>left.associate.data it should be -1
    int[] rightCas;  //like leftCase

    int searchCasIndex(T from) {
        int fromIndex = Collections.binarySearch(datas, from, dictComparator);
        if (fromIndex < 0) {
            return -fromIndex - 1;
        }
        return fromIndex;
    }

    void checkTo(Collection<? super T> results, int fromIndex, T to) {
        Comparator<T> comp = dictComparator.comparators.get(dictComparator.getMainKey());
        for (int i = fromIndex; i < datas.size(); i++) {
            T d = datas.get(i);
            if (comp.compare(d, to) > 0) {
                return;
            }
            results.add(d);
        }
    }
}