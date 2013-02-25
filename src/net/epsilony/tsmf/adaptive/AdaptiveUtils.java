/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.epsilony.tsmf.adaptive;

import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author <a href="mailto:epsilonyuan@gmail.com">Man YUAN</a>
 */
public class AdaptiveUtils {

    public static void fission(AdaptiveCell cell, boolean recursively, Collection<AdaptiveCell> newCellsOutput) {
        if (recursively) {
            recursivelyFission(cell, newCellsOutput);
        } else if (cell.isAbleToFissionToChildren()) {
            cell.fissionToChildren();
            if (null != newCellsOutput) {
                newCellsOutput.clear();
                newCellsOutput.addAll(Arrays.asList(cell.getChildren()));
            }

        }
    }

    public static void recursivelyFission(AdaptiveCell cell, Collection<AdaptiveCell> newCellsOutput) {
        if (null != newCellsOutput) {
            newCellsOutput.clear();
        }
        _recursivelyFission(cell, newCellsOutput);

    }

    private static void _recursivelyFission(AdaptiveCell cell, Collection<AdaptiveCell> newCell) {
        do {
            if (cell.isAbleToFissionToChildren()) {
                cell.fissionToChildren();
                if (null != newCell) {
                    newCell.addAll(Arrays.asList(cell.getChildren()));
                }
                break;
            } else {
                _recursivelyFission(cell.findOneFissionObstrutor(), newCell);
            }
        } while (true);
    }
}
