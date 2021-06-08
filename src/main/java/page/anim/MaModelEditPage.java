package page.anim;

import common.CommonStatic;
import common.util.anim.*;
import page.JBTN;
import page.Page;
import page.support.AnimTreeRenderer;
import page.support.TreeNodeExpander;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.Arrays;

public class MaModelEditPage extends Page implements AbEditPage {

	private static final long serialVersionUID = 1L;

	private static final double res = 0.95;

	private final JBTN back = new JBTN(0, "back");
	private final JTree jlt = new JTree();
	private final AnimGroupTree agt = new AnimGroupTree(jlt);
	private final JScrollPane jspu = new JScrollPane(jlt);
	private final JList<String> jlp = new JList<>();
	private final JScrollPane jspp = new JScrollPane(jlp);
	private final JTree jtr = new JTree();
	private final JScrollPane jsptr = new JScrollPane(jtr);
	private final MaModelEditTable mmet = new MaModelEditTable(this);
	private final JScrollPane jspmm = new JScrollPane(mmet);
	private final SpriteBox sb = new SpriteBox(this);
	private final ModelBox mb = ModelBox.getInstance();
	private final JBTN revt = new JBTN(0, "revt");
	private final JBTN addl = new JBTN(0, "addl");
	private final JBTN reml = new JBTN(0, "reml");
	private final JBTN rema = new JBTN(0, "rema");
	private final JBTN sort = new JBTN(0, "sort");
	private final JBTN camres = new JBTN(0, "rescam");
	private final JBTN zomres = new JBTN(0, "reszom");
	private final EditHead aep;
	private Point p = null;
	private MMTree mmt;

	public MaModelEditPage(Page p) {
		super(p);

		aep = new EditHead(this, 2);
		ini();
		resized();
		agt.renewNodes();
	}

	public MaModelEditPage(Page p, EditHead bar) {
		super(p);

		aep = bar;
		ini();
		resized();
		agt.renewNodes();
	}

	@Override
	public void callBack(Object obj) {
		change(obj, o -> {
			if (o instanceof int[]) {
				int[] rs = (int[]) o;
				mmet.setRowSelectionInterval(rs[0], rs[1]);
				setB(rs[0]);
			}
			if (mb.getEntity() != null)
				mb.getEntity().organize();
			setTree(mmet.anim);
		});

	}

	private void selectAnimNode(AnimCE ac) {
		DefaultMutableTreeNode selectedNode = agt.findAnimNode(ac, null);

		if(selectedNode != null) {
			agt.expandCurrentAnimNode(selectedNode);
			jlt.setSelectionPath(new TreePath(selectedNode.getPath()));
		} else {
			jlt.clearSelection();
		}
	}


	@Override
	public void setSelection(AnimCE anim) {
		change(anim, ac -> {
			selectAnimNode(ac);
			setA(ac);
		});
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (p == null)
			return;
		mb.ori.x += p.x - e.getX();
		mb.ori.y += p.y - e.getY();
		p = e.getPoint();
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (!(e.getSource() instanceof ModelBox))
			return;
		p = e.getPoint();
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		p = null;
	}

	@Override
	protected void mouseWheel(MouseEvent e) {
		if (!(e.getSource() instanceof ModelBox))
			return;
		MouseWheelEvent mwe = (MouseWheelEvent) e;
		double d = mwe.getPreciseWheelRotation();
		mb.setSiz(mb.getSiz() * Math.pow(res, d));
	}

	@Override
	protected void renew() {
		change(this, page -> {
			TreePath path = jlt.getSelectionPath();

			if(path == null)
				return;

			if(!(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
				return;

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if(!(node.getUserObject() instanceof AnimCE))
				return;

			AnimCE da = (AnimCE) node.getUserObject();

			if (aep.focus == null) {
				agt.renewNodes();
			} else {
				DefaultMutableTreeNode root = new DefaultMutableTreeNode("Animation");

				root.add(new DefaultMutableTreeNode(aep.focus));

				jlt.setModel(new DefaultTreeModel(root));
			}
			if (da != null) {
				int row = mmet.getSelectedRow();
				setA(da);
				selectAnimNode(da);
				if (row >= 0 && row < mmet.mm.parts.length) {
					setB(row);
					mmet.setRowSelectionInterval(row, row);
				}
			} else
				setA(null);
			callBack(null);
		});
	}

	@Override
	protected synchronized void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(aep, x, y, 800, 0, 1750, 50);
		set(back, x, y, 0, 0, 200, 50);
		set(camres, x, y, 350, 0, 200, 50);
		set(zomres, x, y, 560, 0, 200, 50);
		set(jsptr, x, y, 0, 550, 300, 750);
		set(jspmm, x, y, 300, 550, 2000, 750);
		set(jspu, x, y, 0, 50, 300, 500);
		set((Canvas) mb, x, y, 300, 50, 700, 500);
		set(jspp, x, y, 1000, 50, 300, 500);
		set(sb, x, y, 1300, 50, 950, 400);
		set(sort, x, y, 1300, 500, 200, 50);
		set(revt, x, y, 1500, 500, 200, 50);
		set(addl, x, y, 1700, 500, 200, 50);
		set(reml, x, y, 1900, 500, 200, 50);
		set(rema, x, y, 2100, 500, 200, 50);
		SwingUtilities.invokeLater(() -> jlt.setUI(new TreeNodeExpander(jlt)));
		aep.componentResized(x, y);
		mmet.setRowHeight(size(x, y, 50));
		sb.paint(sb.getGraphics());
		mb.draw();
	}

	private void addLine() {
		change(0, o -> {
			int ind = mmet.getSelectedRow() + 1;
			if (ind == 0)
				ind++;
			MaModel mm = mmet.mm;
			int[] inds = new int[mm.n];
			for (int i = 0; i < mm.n; i++)
				inds[i] = i < ind ? i : i + 1;
			mmet.anim.reorderModel(inds);
			mm.n++;
			int[] move = new int[mm.n];
			for (int i = 0; i < mm.n; i++)
				move[i] = i < ind ? i : i - 1;
			mm.reorder(move);
			int[] newl = new int[14];
			newl[8] = newl[9] = newl[11] = 1000;
			mm.parts[ind] = newl;
			mmet.anim.unSave("mamodel add line");
			callBack(null);
			resized();
			mmet.setRowSelectionInterval(ind, ind);
			setB(ind);
			int h = mmet.getRowHeight();
			mmet.scrollRectToVisible(new Rectangle(0, h * ind, 1, h));
		});
	}

	private void addListeners$0() {

		back.setLnr(x -> changePanel(getFront()));

		camres.setLnr(x -> {
			mb.ori.x = 0;
			mb.ori.y = 0;
		});

		zomres.setLnr(x -> mb.setSiz(0.5));

		jlt.addTreeSelectionListener(a -> {
			if(isAdj())
				return;

			TreePath path = jlt.getSelectionPath();

			if(path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
				return;

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if(!(node.getUserObject() instanceof AnimCE))
				return;

			change((AnimCE) node.getUserObject(), this::setA);
		});

		jlp.addListSelectionListener(arg0 -> sb.sele = jlp.getSelectedIndex());

		ListSelectionModel lsm = mmet.getSelectionModel();

		lsm.addListSelectionListener(arg0 -> {
			if (isAdj() || lsm.getValueIsAdjusting())
				return;
			change(lsm.getLeadSelectionIndex(), this::setB);
		});

		addl.setLnr(x -> addLine());

		reml.setLnr(x -> removeLine());

		rema.setLnr(x -> removeTree());

	}

	private void addListeners$1() {

		revt.setLnr(x -> {
			mmet.anim.revert();
			callBack(null);
		});

		jtr.addTreeSelectionListener(arg0 -> {
			if (isAdj())
				return;
			Object o = jtr.getLastSelectedPathComponent();
			if (o == null)
				return;
			String str = o.toString();
			int ind = CommonStatic.parseIntN(str.split(" - ")[0]);
			if (ind == -1)
				return;
			setB(ind);
		});

		sort.addActionListener(new ActionListener() {

			private int p = 0;
			private int[] move, inds;
			private int[][] parts;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				p = 0;
				parts = mmet.mm.parts;
				int n = parts.length;
				move = new int[n];
				inds = new int[n];
				for (int i = 0; i < parts.length; i++)
					if (parts[i][0] == -1)
						add(i);
				mmet.anim.reorderModel(inds);
				mmet.mm.reorder(move);
				mmet.anim.unSave("sort");
			}

			private void add(int n) {
				inds[n] = p;
				move[p++] = n;
				for (int i = 0; i < parts.length; i++)
					if (parts[i][0] == n)
						add(i);
			}

		});

	}

	private void ini() {
		add(aep);
		add(back);
		add(camres);
		add(zomres);
		add(jspu);
		add(jspp);
		add(jspmm);
		add(revt);
		add(addl);
		add(reml);
		add(rema);
		add(jsptr);
		add(sort);
		add(sb);
		add((Canvas) mb);
		jlt.setCellRenderer(new AnimTreeRenderer());
		SwingUtilities.invokeLater(() -> jlt.setUI(new TreeNodeExpander(jlt)));
		jtr.setExpandsSelectedPaths(true);
		reml.setForeground(Color.RED);
		rema.setForeground(Color.RED);
		setA(null);
		addListeners$0();
		addListeners$1();
	}

	private void removeLine() {
		int[] rows = mmet.getSelectedRows();
		if (rows.length == 0)
			return;
		if (rows[0] == 0)
			rows = Arrays.copyOfRange(rows, 1, rows.length);
		change(rows, row -> {
			MaModel mm = mmet.mm;
			int[][] data = mm.parts;
			mm.n -= row.length;
			int[] inds = new int[data.length];
			int[] move = new int[mm.n];
			for (int j : row)
				data[j] = null;
			int ind = 0;
			for (int i = 0; i < data.length; i++)
				if (data[i] != null) {
					move[ind] = i;
					inds[i] = ind;
					ind++;
				} else
					inds[i] = -1;
			mmet.anim.reorderModel(inds);
			mm.reorder(move);
			mmet.anim.unSave("mamodel remove line");
			callBack(null);
			if (ind >= mm.n)
				ind--;
			mmet.setRowSelectionInterval(ind, ind);
			setB(ind);
		});
	}

	private void removeTree() {
		change(0, o -> {
			MaModel mm = mmet.mm;
			int[] rows = mmet.getSelectedRows();

			if (rows[0] == 0)
				return;

			boolean[] bs = new boolean[mm.n];
			int total = rows.length;
			for (int ind : rows)
				bs[ind] = true;
			total += mm.getChild(bs);
			mm.clearAnim(bs, mmet.anim.anims);
			int[] inds = new int[mm.n];
			int[] move = new int[mm.n - total];
			int j = 0;
			for (int i = 0; i < mm.n; i++)
				if (!bs[i]) {
					move[j] = i;
					inds[i] = j;
					j++;
				}
			mmet.anim.reorderModel(inds);
			mm.n -= total;
			mm.reorder(move);
			mmet.anim.unSave("mamodel remove tree");
			callBack(null);
			int ind = rows[0];
			if (ind >= mm.n)
				ind = mm.n - 1;
			if (ind >= 0)
				mmet.setRowSelectionInterval(ind, ind);
			setB(ind);
		});
	}

	private void setA(AnimCE anim) {
		aep.setAnim(anim);
		setTree(anim);
		addl.setEnabled(anim != null);
		sort.setEnabled(anim != null);
		revt.setEnabled(anim != null);
		if (anim == null) {
			mmet.setMaModel(null);
			mb.setEntity(null);
			sb.setAnim(null);
			jlp.setListData(new String[0]);
			setB(-1);
			return;
		}
		mmet.setMaModel(anim);
		mb.setEntity(new EAnimS(anim, anim.mamodel));
		ImgCut ic = anim.imgcut;
		String[] name = new String[ic.n];
		for (int i = 0; i < ic.n; i++)
			name[i] = i + " " + ic.strs[i];
		jlp.setListData(name);
		sb.setAnim(anim);
		mmet.clearSelection();
		setB(-1);
	}

	private void setB(int ind) {
		if (mb.getEntity() != null)
			mb.getEntity().sele = ind;
		reml.setEnabled(ind != -1);
		rema.setEnabled(ind != -1);
		if (ind < 0 || mmet.mm == null)
			return;
		if (mmet.getSelectedRow() != ind)
			change(ind, i -> {
				mmet.setRowSelectionInterval(i, i);
				mmet.scrollRectToVisible(mmet.getCellRect(i, 0, true));
			});
		if (mmt != null)
			change(ind, i -> mmt.select(i));
		int val = mmet.mm.parts[ind][2];
		jlp.setSelectedIndex(val);
		for (int row : mmet.getSelectedRows()) {
			for (int[] ints : mmet.mm.parts)
				if (ints[0] == row)
					reml.setEnabled(false);
			for (MaAnim ma : mmet.anim.anims)
				for (Part p : ma.parts)
					if (p.ints[0] == row)
						reml.setEnabled(false);
		}
	}

	private void setTree(AnimCE dat) {
		change(dat, anim -> {
			if (anim == null) {
				jtr.setModel(new DefaultTreeModel(null));
				mmt = null;
				return;
			}
			if (mmt == null || mmt.anim != anim)
				mmt = new MMTree(null, anim, jtr);
			mmt.renew();
		});
	}

}