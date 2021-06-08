package page.anim;

import common.CommonStatic;
import common.util.anim.*;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;
import page.support.AnimTreeRenderer;
import page.support.TreeNodeExpander;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaAnimEditPage extends Page implements AbEditPage {

	private static final long serialVersionUID = 1L;

	private static final double res = 0.95;

	private static final String[] mod = new String[] { "0 parent", "1 id", "2 sprite", "3 z-order", "4 pos-x",
			"5 pos-y", "6 pivot-x", "7 pivot-y", "8 scale", "9 scale-x", "10 scale-y", "11 angle", "12 opacity",
			"13 horizontal flip", "14 vertical flip", "50 extendX", "52 extendY" };

	private final JBTN back = new JBTN(0, "back");
	private final JTree jta = new JTree();
	private final AnimGroupTree agt = new AnimGroupTree(jta);
	private final JScrollPane jspu = new JScrollPane(jta);
	private final JList<String> jlt = new JList<>();
	private final JScrollPane jspt = new JScrollPane(jlt);
	private final JList<String> jlp = new JList<>();
	private final JScrollPane jspp = new JScrollPane(jlp);
	private final JList<String> jlm = new JList<>();
	private final JScrollPane jspm = new JScrollPane(jlm);
	private final JList<String> jlv = new JList<>(mod);
	private final JScrollPane jspv = new JScrollPane(jlv);
	private final MaAnimEditTable maet = new MaAnimEditTable(this);
	private final JScrollPane jspma = new JScrollPane(maet);
	private final PartEditTable mpet = new PartEditTable(this);
	private final JScrollPane jspmp = new JScrollPane(mpet);
	private final JTG jtb = new JTG(0, "pause");
	private final JBTN nex = new JBTN(0, "nextf");
	private final JSlider jtl = new JSlider();
	private final SpriteBox sb = new SpriteBox(this);
	private final AnimBox ab = AnimBox.getInstance();
	private final JBTN addp = new JBTN(0, "add");
	private final JBTN remp = new JBTN(0, "rem");
	private final JBTN addl = new JBTN(0, "addl");
	private final JBTN reml = new JBTN(0, "reml");
	private final JBTN advs = new JBTN(0, "advs");
	private final JBTN sort = new JBTN(0, "sort");
	private final JBTN camres = new JBTN(0, "rescam");
	private final JBTN zomres = new JBTN(0, "reszom");
	private final JLabel inft = new JLabel();
	private final JLabel inff = new JLabel();
	private final JLabel infv = new JLabel();
	private final JLabel infm = new JLabel();
	private final JLabel lmul = new JLabel("</>");
	private final JTF tmul = new JTF();
	private final EditHead aep;

	private Point p = null;
	private boolean pause;

	public MaAnimEditPage(Page p) {
		super(p);

		aep = new EditHead(this, 3);
		ini();
		resized();
	}

	public MaAnimEditPage(Page p, EditHead bar) {
		super(p);

		aep = bar;
		ini();
		resized();
	}

	@Override
	public void callBack(Object o) {
		if (o instanceof int[])
			change((int[]) o, rs -> {
				if (rs[0] == 0) {
					maet.setRowSelectionInterval(rs[1], rs[2]);
					setC(rs[1]);
				} else {
					mpet.setRowSelectionInterval(rs[1], rs[2]);
					setD(rs[1]);
				}
			});
		int ind = jlt.getSelectedIndex();
		AnimCE ac = maet.anim;
		if (ind < 0 || ac == null)
			return;
		int time = ab.getEntity() == null ? 0 : ab.getEntity().ind();
		ab.setEntity(ac.getEAnim(ac.types[ind]));
		ab.getEntity().setTime(time);
	}

	private void selectAnimNode(AnimCE ac) {
		DefaultMutableTreeNode selectedNode = agt.findAnimNode(ac, null);

		if(selectedNode != null) {
			agt.expandCurrentAnimNode(selectedNode);
			jta.setSelectionPath(new TreePath(selectedNode.getPath()));
		} else {
			jta.clearSelection();
		}
	}

	@Override
	public void setSelection(AnimCE a) {
		change(a, ac -> {
			selectAnimNode(ac);
			setA(ac);
		});
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (p == null)
			return;
		ab.ori.x += p.x - e.getX();
		ab.ori.y += p.y - e.getY();
		p = e.getPoint();
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (!(e.getSource() instanceof AnimBox))
			return;
		p = e.getPoint();
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		p = null;
	}

	@Override
	protected void mouseWheel(MouseEvent e) {
		if (!(e.getSource() instanceof AnimBox))
			return;
		MouseWheelEvent mwe = (MouseWheelEvent) e;
		double d = mwe.getPreciseWheelRotation();
		ab.setSiz(ab.getSiz() * Math.pow(res, d));
	}

	@Override
	protected void renew() {
		TreePath path = jta.getSelectionPath();

		if(path == null)
			return;

		if(!(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
			return;

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

		if(!(node.getUserObject() instanceof AnimCE))
			return;

		AnimCE da = (AnimCE) node.getUserObject();
		int ani = jlt.getSelectedIndex();
		int par = maet.getSelectedRow();
		int row = mpet.getSelectedRow();
		if (aep.focus == null) {
			agt.renewNodes();
		} else {
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("Animation");

			root.add(new DefaultMutableTreeNode(aep.focus));

			jta.setModel(new DefaultTreeModel(root));
		}
		change(0, x -> {
			if (da != null) {
				setA(da);
				selectAnimNode(da);
				if (ani >= 0 && ani < da.anims.length) {
					setB(da, ani);
					if (par >= 0 && par < maet.ma.parts.length) {
						setC(par);
						maet.setRowSelectionInterval(par, par);
						if (row >= 0 && row < mpet.part.moves.length) {
							setD(row);
							mpet.setRowSelectionInterval(row, row);
						}
					}
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

		set(addp, x, y, 300, 750, 200, 50);
		set(remp, x, y, 300, 800, 200, 50);
		set(lmul, x, y, 300, 650, 200, 50);
		set(tmul, x, y, 300, 700, 200, 50);
		set(jspv, x, y, 300, 850, 200, 450);
		set(jspma, x, y, 500, 650, 900, 650);
		set(jspmp, x, y, 1400, 650, 900, 650);
		set(jspu, x, y, 0, 50, 300, 400);
		set(jspt, x, y, 0, 450, 300, 300);
		set(jspm, x, y, 0, 750, 300, 550);
		set((Canvas) ab, x, y, 300, 50, 700, 500);
		set(jspp, x, y, 1000, 50, 300, 500);
		set(sb, x, y, 1300, 50, 1000, 500);
		set(addl, x, y, 2100, 550, 200, 50);
		set(reml, x, y, 2100, 600, 200, 50);
		set(jtl, x, y, 300, 550, 900, 100);
		set(jtb, x, y, 1200, 550, 200, 50);
		set(nex, x, y, 1200, 600, 200, 50);
		set(inft, x, y, 1400, 550, 250, 50);
		set(inff, x, y, 1650, 550, 250, 50);
		set(infv, x, y, 1400, 600, 250, 50);
		set(infm, x, y, 1650, 600, 250, 50);
		set(advs, x, y, 1900, 550, 200, 50);
		set(sort, x, y, 1900, 600, 200, 50);
		SwingUtilities.invokeLater(() -> jta.setUI(new TreeNodeExpander(jta)));
		aep.componentResized(x, y);
		maet.setRowHeight(size(x, y, 50));
		mpet.setRowHeight(size(x, y, 50));
		sb.paint(sb.getGraphics());
		ab.draw();
	}

	@Override
	protected void timer(int t) {
		if (!pause)
			eupdate();
		if (ab.getEntity() != null && mpet.part != null) {
			Part p = mpet.part;
			EPart ep = ab.getEntity().ent[p.ints[0]];
			inft.setText("frame: " + ab.getEntity().ind());
			inff.setText("part frame: " + (p.frame - p.off));
			infv.setText("actual value: " + ep.getVal(p.ints[1]));
			infm.setText("part value: " + p.vd);
		} else {
			inft.setText("");
			inff.setText("");
			infv.setText("");
			infm.setText("");
		}
		resized();
	}

	private void addListeners() {

		back.addActionListener(arg0 -> changePanel(getFront()));

		camres.setLnr(x -> {
			ab.ori.x = 0;
			ab.ori.y = 0;
		});

		zomres.setLnr(x -> ab.setSiz(0.5));

		jta.addTreeSelectionListener(a -> {
			if(isAdj())
				return;

			TreePath path = jta.getSelectionPath();

			if(path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
				return;

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if(!(node.getUserObject() instanceof AnimCE))
				return;

			setA((AnimCE) node.getUserObject());
		});

		jlt.addListSelectionListener(arg0 -> {
			if (isAdj() || jlt.getValueIsAdjusting())
				return;

			TreePath path = jta.getSelectionPath();

			if(path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode))
				return;

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

			if(!(node.getUserObject() instanceof AnimCE))
				return;

			AnimCE da = (AnimCE) node.getUserObject();
			int ind = jlt.getSelectedIndex();
			setB(da, ind);
		});

		jlp.addListSelectionListener(arg0 -> {
			if (isAdj() || jlp.getValueIsAdjusting())
				return;
			sb.sele = jlp.getSelectedIndex();
		});

		jlm.addListSelectionListener(arg0 -> {
			if (isAdj() || jlm.getValueIsAdjusting() || maet.ma == null)
				return;
			int ind = jlm.getSelectedIndex();
			for (int i = 0; i < maet.ma.n; i++)
				if (maet.ma.parts[i].ints[0] == ind) {
					setC(i);
					return;
				}
			setC(-1);
		});

	}

	private void addListeners$1() {
		ListSelectionModel lsm = maet.getSelectionModel();

		lsm.addListSelectionListener(e -> {
			if (isAdj() || lsm.getValueIsAdjusting())
				return;
			int ind = maet.getSelectedRow();
			change(ind, this::setC);
		});

		addp.addActionListener(arg0 -> change(0, x -> {
			int ind = maet.getSelectedRow() + 1;
			MaAnim ma = maet.ma;
			Part[] data = ma.parts;
			ma.parts = new Part[++ma.n];
			if (ind >= 0)
				System.arraycopy(data, 0, ma.parts, 0, ind);
			if (data.length - ind >= 0)
				System.arraycopy(data, ind, ma.parts, ind + 1, data.length - ind);
			Part np = new Part();
			np.validate();
			ma.parts[ind] = np;
			ma.validate();
			maet.anim.unSave("maanim add part");
			callBack(null);
			resized();
			lsm.setSelectionInterval(ind, ind);
			setC(ind);
			int h = mpet.getRowHeight();
			mpet.scrollRectToVisible(new Rectangle(0, h * ind, 1, h));
		}));

		remp.addActionListener(arg0 -> change(0, x -> {
			MaAnim ma = maet.ma;
			int[] rows = maet.getSelectedRows();
			Part[] data = ma.parts;
			for (int row : rows)
				data[row] = null;
			ma.n -= rows.length;
			ma.parts = new Part[ma.n];
			int ind = 0;
			for (Part datum : data)
				if (datum != null)
					ma.parts[ind++] = datum;
			ind = rows[rows.length - 1];
			ma.validate();
			maet.anim.unSave("maanim remove part");
			callBack(null);
			if (ind >= ma.n)
				ind = ma.n - 1;
			lsm.setSelectionInterval(ind, ind);
			setC(ind);
		}));

		tmul.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				double d = CommonStatic.parseIntN(tmul.getText()) * 0.01;
				if (!Opts.conf("times animation length by " + d))
					return;
				for (Part p : maet.ma.parts) {
					for (int[] line : p.moves)
						line[0] *= d;
					p.off *= d;
					p.validate();
				}
				maet.ma.validate();
				maet.anim.unSave("maanim multiply");
			}

		});

	}

	private void addListeners$2() {
		ListSelectionModel lsm = mpet.getSelectionModel();

		lsm.addListSelectionListener(e -> {
			if (isAdj() || lsm.getValueIsAdjusting())
				return;
			setD(lsm.getLeadSelectionIndex());
		});

		addl.addActionListener(arg0 -> {
			Part p = mpet.part;
			int[][] data = p.moves;
			p.moves = new int[++p.n][];
			System.arraycopy(data, 0, p.moves, 0, data.length);
			p.moves[p.n - 1] = new int[4];
			p.validate();
			maet.ma.validate();
			callBack(null);
			maet.anim.unSave("maanim add line");
			resized();
			change(p.n - 1, i -> lsm.setSelectionInterval(i, i));
			setD(p.n - 1);
			int h = mpet.getRowHeight();
			mpet.scrollRectToVisible(new Rectangle(0, h * (p.n - 1), 1, h));
		});

		reml.addActionListener(arg0 -> {
			int[] inds = mpet.getSelectedRows();
			if (inds.length == 0)
				return;
			Part p = mpet.part;
			List<int[]> l = new ArrayList<>();
			int j = 0;
			for (int i = 0; i < p.n; i++)
				if (j >= inds.length || i != inds[j])
					l.add(p.moves[i]);
				else
					j++;
			p.moves = l.toArray(new int[0][]);
			p.n = l.size();
			p.validate();
			maet.ma.validate();
			callBack(null);
			maet.anim.unSave("maanim remove line");
			int ind = inds[0];
			if (ind >= p.n)
				ind--;
			change(ind, i -> lsm.setSelectionInterval(i, i));
			setD(ind);
		});
	}

	private void addListeners$3() {

		jtb.addActionListener(arg0 -> {
			pause = jtb.isSelected();
			jtl.setEnabled(pause && ab.getEntity() != null);
		});

		nex.addActionListener(arg0 -> eupdate());

		jtl.addChangeListener(arg0 -> {
			if (isAdj() || !pause)
				return;
			ab.getEntity().setTime(jtl.getValue());
		});

		advs.setLnr(() -> new AdvAnimEditPage(this, maet.anim, maet.anim.types[jlt.getSelectedIndex()]));

		sort.setLnr(x -> Arrays.sort(maet.ma.parts));

	}

	private void eupdate() {
		ab.update();
		if (ab.getEntity() != null)
			change(0, x -> jtl.setValue(ab.getEntity().ind()));
	}

	private void ini() {
		add(aep);
		add(back);
		add(camres);
		add(zomres);
		add(jspu);
		add(jspp);
		add(jspt);
		add(jspm);
		add(jspv);
		add(jspma);
		add(jspmp);
		add(addp);
		add(remp);
		add(addl);
		add(reml);
		add(jtb);
		add(jtl);
		add(nex);
		add(sb);
		add((Canvas) ab);
		add(inft);
		add(inff);
		add(infv);
		add(infm);
		add(lmul);
		add(tmul);
		add(advs);
		add(sort);
		agt.renewNodes();
		jta.setCellRenderer(new AnimTreeRenderer());
		SwingUtilities.invokeLater(() -> jta.setUI(new TreeNodeExpander(jta)));
		inft.setBorder(BorderFactory.createEtchedBorder());
		inff.setBorder(BorderFactory.createEtchedBorder());
		infv.setBorder(BorderFactory.createEtchedBorder());
		infm.setBorder(BorderFactory.createEtchedBorder());
		lmul.setBorder(BorderFactory.createEtchedBorder());
		addp.setEnabled(false);
		remp.setEnabled(false);
		addl.setEnabled(false);
		reml.setEnabled(false);
		jtl.setEnabled(false);
		jtl.setPaintTicks(true);
		jtl.setPaintLabels(true);
		addListeners();
		addListeners$1();
		addListeners$2();
		addListeners$3();
	}

	private void setA(AnimCE dan) {
		change(dan, anim -> {
			aep.setAnim(anim);
			if (anim == null) {
				jlt.setListData(new String[0]);
				sb.setAnim(null);
				jlp.setListData(new String[0]);
				setB(null, -1);
				return;
			}
			int ind = jlt.getSelectedIndex();
			String[] val = anim.names();
			jlt.setListData(val);
			if (ind >= val.length)
				ind = val.length - 1;
			jlt.setSelectedIndex(ind);
			setB(anim, ind);
			sb.setAnim(anim);
			ImgCut ic = anim.imgcut;
			String[] name = new String[ic.n];
			for (int i = 0; i < ic.n; i++)
				name[i] = i + " " + ic.strs[i];
			jlp.setListData(name);
			MaModel mm = anim.mamodel;
			name = new String[mm.n];
			for (int i = 0; i < mm.n; i++)
				name[i] = i + " " + mm.strs0[i];
			jlm.setListData(name);
		});
	}

	private void setB(AnimCE ac, int ind) {
		change(0, x -> {
			MaAnim anim = ac == null || ind < 0 ? null : ac.getMaAnim(ac.types[ind]);
			addp.setEnabled(anim != null);
			tmul.setEditable(anim != null);
			advs.setEnabled(anim != null);
			sort.setEnabled(anim != null);
			jtl.setEnabled(anim != null);
			if (ac == null || ind == -1) {
				maet.setAnim(null, null);
				ab.setEntity(null);
				setC(-1);
				return;
			}
			int row = maet.getSelectedRow();
			maet.setAnim(ac, anim);
			ab.setEntity(ac.getEAnim(ac.types[ind]));
			if (row >= maet.getRowCount()) {
				maet.clearSelection();
				row = -1;
			}
			setC(row);

			jtl.setMinimum(0);
			jtl.setMaximum(ab.getEntity().len());
			jtl.setLabelTable(null);
			if (ab.getEntity().len() <= 50) {
				jtl.setMajorTickSpacing(5);
				jtl.setMinorTickSpacing(1);
			} else if (ab.getEntity().len() <= 200) {
				jtl.setMajorTickSpacing(10);
				jtl.setMinorTickSpacing(2);
			} else if (ab.getEntity().len() <= 1000) {
				jtl.setMajorTickSpacing(50);
				jtl.setMinorTickSpacing(10);
			} else if (ab.getEntity().len() <= 5000) {
				jtl.setMajorTickSpacing(250);
				jtl.setMinorTickSpacing(50);
			} else {
				jtl.setMajorTickSpacing(1000);
				jtl.setMinorTickSpacing(200);
			}
		});
	}

	private void setC(int ind) {
		remp.setEnabled(ind >= 0);
		addl.setEnabled(ind >= 0);
		Part p = ind < 0 || ind >= maet.ma.parts.length ? null : maet.ma.parts[ind];
		change(0, x -> {
			mpet.setAnim(maet.anim, maet.ma, p);
			mpet.clearSelection();
			ab.setSele(p == null ? -1 : p.ints[0]);

			if (ind >= 0) {
				if(p != null) {
					int par = p.ints[0];
					jlm.setSelectedIndex(par);
					jlv.setSelectedIndex(mpet.part.ints[1]);
					if (maet.getSelectedRow() != ind) {
						maet.setRowSelectionInterval(ind, ind);
						maet.scrollRectToVisible(maet.getCellRect(ind, 0, true));
					}
					ab.setSele(par);
					int ic = mpet.anim.mamodel.parts[par][2];
					jlp.setSelectedIndex(ic);
					Rectangle r = jlp.getCellBounds(ic, ic);
					if (r != null)
						jlp.scrollRectToVisible(r);
					sb.sele = jlp.getSelectedIndex();
				}
			} else
				maet.clearSelection();
		});
		setD(-1);
	}

	private void setD(int ind) {
		reml.setEnabled(ind >= 0);
		if (ind >= 0 && mpet.part.ints[1] == 2) {
			change(mpet.part.moves[ind][1], jlp::setSelectedIndex);
			sb.sele = jlp.getSelectedIndex();
		}
	}

}
