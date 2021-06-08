package page.support;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class TreeNodeExpander extends BasicTreeUI {
    private int lastWidth;
    private boolean leftToRight;
    private final JTree tree;

    public TreeNodeExpander(JTree tree) {
        super();

        this.tree = tree;
    }

    @Override
    protected void prepareForUIInstall() {
        super.prepareForUIInstall();
        leftToRight = tree.getComponentOrientation().isLeftToRight();
        lastWidth = tree.getParent().getWidth();
    }

    @Override
    protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
        return new NodeDimensionHandler();
    }

    @Override
    protected TreeCellRenderer createDefaultCellRenderer() {
        return new AnimTreeRenderer();
    }

    public class NodeDimensionHandler extends AbstractLayoutCache.NodeDimensions {

        @Override
        public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle bounds) {
            if(editingComponent != null && editingRow == row) {
                Dimension prefer = editingComponent.getPreferredSize();
                int rowHeight = getRowHeight();

                if(rowHeight > 0 && rowHeight != prefer.height)
                    prefer.height = rowHeight;

                if(bounds != null) {
                    bounds.x = getRowX(row, depth);
                    bounds.width = prefer.width;
                    bounds.height = prefer.height;
                } else {
                    bounds = new Rectangle(getRowX(row, depth), 0, prefer.width, prefer.height);
                }

                if(!leftToRight) {
                    bounds.x = lastWidth - bounds.width - bounds.x - 2;
                }

                return bounds;
            }

            if(currentCellRenderer != null) {
                Component comp = currentCellRenderer.getTreeCellRendererComponent(tree, value, tree.isRowSelected(row), expanded, treeModel.isLeaf(value), row, false);

                rendererPane.add(comp);
                comp.validate();

                Dimension prefer = comp.getPreferredSize();

                if(bounds != null) {
                    bounds.x = getRowX(row, depth);
                    bounds.width = lastWidth - bounds.x;
                    bounds.height = prefer.height;
                } else {
                    bounds = new Rectangle(getRowX(row, depth), 0, prefer.width, prefer.height);
                }

                if(!leftToRight) {
                    bounds.x = lastWidth - bounds.width - bounds.x - 2;
                }

                return bounds;
            }

            return null;
        }
    }
}