package page.support;

import common.util.anim.AnimCE;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import page.anim.AnimGroupTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnimTreeTransfer extends TransferHandler {

    private static final long serialVersionUID = 1L;
    private DataFlavor nodeFlavor;
    private final DataFlavor[] flavors = new DataFlavor[1];
    private DefaultMutableTreeNode[] nodesToRemove;

    private  final AnimGroupTree agt;

    public AnimTreeTransfer(AnimGroupTree agt) {
        try {
            String mime = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +
                    javax.swing.tree.DefaultMutableTreeNode[].class.getName() +
                    "\"";
            nodeFlavor = new DataFlavor(mime);
            flavors[0] = nodeFlavor;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.agt = agt;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        if(!support.isDrop())
            return false;

        support.setShowDropLocation(true);

        if(!support.isDataFlavorSupported(nodeFlavor))
            return false;

        if(!(support.getDropLocation() instanceof JTree.DropLocation))
            return false;

        if(support.getDropAction() == COPY)
            return false;

        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

        //filter if drop location is anim or not

        TreePath path = dl.getPath();

        if(!(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
            return false;

        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) path.getLastPathComponent();

        if(targetNode.getUserObject() instanceof AnimCE)
            return false;

        if(!(support.getComponent() instanceof JTree))
            return false;

        JTree tree = (JTree) support.getComponent();

        int dropRow = tree.getRowForPath(dl.getPath());

        int[] selectedRows = tree.getSelectionRows();

        if(selectedRows == null)
            return false;

        for (int selectedRow : selectedRows) {
            if (selectedRow == dropRow)
                return false;

            //check if selected row contains container

            TreePath selectedPath = tree.getPathForRow(selectedRow);

            if(!(selectedPath.getLastPathComponent() instanceof DefaultMutableTreeNode))
                return false;

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();

            if(selectedNode.getUserObject() instanceof String)
                return false;
        }

        return true;
    }

    @Nullable
    @Override
    protected Transferable createTransferable(JComponent c) {
        if(!(c instanceof JTree))
            return null;

        JTree tree = (JTree) c;

        TreePath[] paths = tree.getSelectionPaths();

        if(paths != null) {
            //safe casting
            for(TreePath path : paths) {
                if(!(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
                    return null;
            }

            List<DefaultMutableTreeNode> copies = new ArrayList<>();
            List<DefaultMutableTreeNode> toRemove = new ArrayList<>();

            for (TreePath path : paths) {
                DefaultMutableTreeNode next = (DefaultMutableTreeNode) path.getLastPathComponent();

                copies.add(cloneNode(next));
                toRemove.add(next);
            }

            DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[0]);
            nodesToRemove = toRemove.toArray(new DefaultMutableTreeNode[0]);

            return new NodeTransferable(nodes);
        }

        return null;
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if(!(source instanceof JTree))
            return;

        if((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;

            if(!(tree.getModel() instanceof DefaultTreeModel))
                return;

            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

            if(nodesToRemove != null) {
                for (DefaultMutableTreeNode defaultMutableTreeNode : nodesToRemove) {
                    model.removeNodeFromParent(defaultMutableTreeNode);
                }
            }

            agt.applyNewNodes();
        }
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    public boolean importData(TransferSupport support) {
        if(!canImport(support))
            return false;

        DefaultMutableTreeNode[] nodes = null;

        try {
            Transferable t = support.getTransferable();
            nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodeFlavor);
        } catch (IOException | UnsupportedFlavorException e) {
            e.printStackTrace();
        }

        if(nodes == null)
            return false;

        if(!(support.getDropLocation() instanceof JTree.DropLocation))
            return false;

        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();

        int childIndex = dl.getChildIndex();

        TreePath dest = dl.getPath();

        if(!(dest.getLastPathComponent() instanceof  DefaultMutableTreeNode))
            return false;

        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getLastPathComponent();

        if(!(support.getComponent() instanceof JTree))
            return false;

        JTree tree = (JTree) support.getComponent();

        if(!(tree.getModel() instanceof DefaultTreeModel))
            return false;

        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        int index = childIndex;

        if(childIndex == -1)
            index = parent.getChildCount();

        for (DefaultMutableTreeNode node : nodes) {
            model.insertNodeInto(node, parent, index++);
        }

        return true;
    }

    private DefaultMutableTreeNode cloneNode(DefaultMutableTreeNode node) {
        return new DefaultMutableTreeNode(node.getUserObject());
    }

    public class NodeTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;

        public NodeTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodeFlavor.equals(flavor);
        }

        @NotNull
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if(!isDataFlavorSupported(flavor))
                throw new UnsupportedFlavorException(flavor);

            return nodes;
        }
    }
}