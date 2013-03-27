/* (c) Copyright by Man YUAN */
package net.epsilony.tsmf.util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class MiscellaneousUtils {

    public static <T> void addToList(T[][] array, List<? super T> dst) {
        for (int i = 0; i < array.length; i++) {
            dst.addAll(Arrays.asList(array[i]));
        }
    }
}
