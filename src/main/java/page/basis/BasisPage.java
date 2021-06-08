package page.basis;

import common.CommonStatic;
import common.battle.BasisLU;
import common.battle.BasisSet;
import common.battle.LineUp;
import common.pack.UserProfile;
import common.system.Node;
import common.util.pack.NyCastle;
import common.util.unit.Combo;
import common.util.unit.Form;
import common.util.unit.Unit;
import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;
import page.info.TreaTable;
import page.support.ReorderList;
import page.support.ReorderListener;
import page.support.UnitLCR;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static common.battle.BasisSet.current;

public class BasisPage extends LubCont {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");
	private final JBTN unit = new JBTN(0, "vuif");
	private final JBTN setc = new JBTN(0, "set0");
	private final JBTN bsadd = new JBTN(0, "add");
	private final JBTN bsrem = new JBTN(0, "rem");
	private final JBTN bscop = new JBTN(0, "copy");
	private final JBTN badd = new JBTN(0, "add");
	private final JBTN brem = new JBTN(0, "rem");
	private final JBTN bcop = new JBTN(0, "copy");
	private final JBTN form = new JBTN(0, "form");
	private final JBTN reset = new JBTN(0, "renew");
	private final JBTN search = new JBTN(0, "search");
	private final JTG combo = new JTG(0, "combo");
	private final JTF bsjtf = new JTF();
	private final JTF bjtf = new JTF();
	private final JTF lvjtf = new JTF();
	private final JTF cjtf = new JTF();
	private final JBTN lvorb = new JBTN(0, "orb");
	private final JLabel pcoin = new JLabel();
	private final Vector<BasisSet> vbs = new Vector<>(BasisSet.list());
	private final ReorderList<BasisSet> jlbs = new ReorderList<>(vbs);
	private final JScrollPane jspbs = new JScrollPane(jlbs);
	private final Vector<BasisLU> vb = new Vector<>();
	private final ReorderList<BasisLU> jlb = new ReorderList<>(vb, BasisLU.class, "lineup");
	private final JScrollPane jspb = new JScrollPane(jlb);
	private final JList<String> jlcs = new JList<>(Interpret.COMF);
	private final JScrollPane jspcs = new JScrollPane(jlcs);
	private final JList<String> jlcl = new JList<>();
	private final JScrollPane jspcl = new JScrollPane(jlcl);
	private final ComboListTable jlc = new ComboListTable(this, lu());
	private final JScrollPane jspc = new JScrollPane(jlc);
	private final ComboList jlcn = new ComboList();
	private final JScrollPane jspcn = new JScrollPane(jlcn);
	private final LineUpBox lub = new LineUpBox(this);
	private final JList<Form> ul = new JList<>();
	private final JScrollPane jspul = new JScrollPane(ul);
	private final NyCasBox ncb = new NyCasBox();
	private final JBTN[] jbcsR = new JBTN[3];
	private final JBTN[] jbcsL = new JBTN[3];

	private boolean changing = false, outside = false, resize = true;

	private UnitFLUPage ufp;

	private final TreaTable trea = new TreaTable(this);
	private final JScrollPane jspt = new JScrollPane(trea);
	private Unit cunit;

	private String comboName = "";

	public BasisPage(Page p) {
		super(p);

		ini();
		resized();
	}

	public void requireResize() {
		resize = true;
	}

	@Override
	public void callBack(Object o) {
		if (o == null)
			changeLU();
		else if (o instanceof Unit) {
			Unit unit = (Unit) o;
			if (cunit != null && unit.compareTo(cunit) == 0)
				return;
			combo.setSelected(true);
			lub.select(unit.forms[unit.forms.length - 1]);
		} else if (o instanceof Form) {
			Unit unit = ((Form) o).unit;
			setLvs((Form) o);
			if (!combo.isSelected())
				return;
			if (cunit != null && unit.compareTo(cunit) == 0)
				return;
			cunit = unit;
			outside = true;
			changing = true;
			setCL(jlcs.getSelectedIndex());
			changing = false;
		}
	}

	@Override
	protected LineUpBox getLub() {
		return lub;
	}

	@Override
	protected void keyTyped(KeyEvent e) {
		if (trea.isFocusOwner())
			return;
		if (lvjtf.isFocusOwner())
			return;
		if (bjtf.isFocusOwner())
			return;
		if (bsjtf.isFocusOwner())
			return;
		if (cjtf.isFocusOwner())
			return;
		super.keyTyped(e);
		e.consume();
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == jlc)
			jlc.clicked(e.getPoint());
		super.mouseClicked(e);
	}

	@Override
	protected void renew() {
		if (ufp != null) {
			List<Form> lf = ufp.getList();
			if (lf != null)
				ul.setListData(Node.deRep(lf).toArray(new Form[0]));
		}
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jspbs, x, y, 50, 100, 200, 500);
		set(bsadd, x, y, 50, 600, 200, 50);
		set(bsrem, x, y, 50, 650, 200, 50);
		set(bscop, x, y, 50, 700, 200, 50);
		set(bsjtf, x, y, 50, 750, 200, 50);
		set(jspb, x, y, 275, 100, 200, 500);
		set(badd, x, y, 275, 600, 200, 50);
		set(brem, x, y, 275, 650, 200, 50);
		set(bcop, x, y, 275, 700, 200, 50);
		set(bjtf, x, y, 275, 750, 200, 50);
		set(ncb, x, y, 1036, 500, 128, 256);
		set(jspt, x, y, 1600, 150, 450, 600);
		set(lub, x, y, 500, 150, 600, 300);
		set(unit, x, y, 1350, 100, 200, 50);
		set(jspcs, x, y, 1500, 800, 300, 450);
		set(jspcl, x, y, 1800, 800, 300, 450);
		set(jspc, x, y, 50, 800, 1450, 450);
		set(cjtf, x, y, 500, 750, 400, 50);
		set(search, x, y, 900, 750, 200, 50);
		set(setc, x, y, 1100, 750, 200, 50);
		set(jspcn, x, y, 500, 500, 400, 250);
		set(jspul, x, y, 1300, 150, 300, 600);
		set(pcoin, x, y, 500, 50, 600, 50);
		set(lvjtf, x, y, 500, 100, 400, 50);
		set(form, x, y, 500, 450, 200, 50);
		set(reset, x, y, 700, 450, 200, 50);
		set(lvorb, x, y, 900, 100, 200, 50);
		set(combo, x, y, 900, 450, 200, 50);
		for (int i = 0; i < jbcsL.length; i++)
			set(jbcsL[i], x, y, 930, 500 + 103 * i, 100, 50);
		for (int i = 0; i < jbcsR.length; i++)
			set(jbcsR[i], x, y, 1170, 500 + 103 * i, 100, 50);
		jlc.setRowHeight(50);
		jlc.getColumnModel().getColumn(2).setPreferredWidth(size(x, y, 300));
		trea.resized(x, y);
		if (resize) {
			trea.setPreferredSize(size(x, y, trea.getPWidth(), trea.getPHeight()).toDimension());
			jspt.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
			jspt.revalidate();
			resize = false;
		}
	}

	@Override
	protected void timer(int t) {
		ncb.paint(ncb.getGraphics());
		super.timer(t);
	}

	private void addListeners$0() {

		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}
		});

		unit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ufp == null)
					ufp = new UnitFLUPage(getThis());
				changePanel(ufp);
			}
		});

		ul.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				changing = true;
				lub.select(ul.getSelectedValue());
				changing = false;
			}
		});

		form.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lub.adjForm();
				changeLU();
			}
		});

		lvjtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				int[] lv = CommonStatic.parseIntsN(lvjtf.getText());
				lub.setLv(lv);
				if (lub.sf != null)
					setLvs(lub.sf);
			}
		});

		lvorb.setLnr(x -> {
			if (lub.sf != null) {
				changePanel(new LevelEditPage(this, lu().getLv(lub.sf), lub.sf));
			}
		});

		for (int i = 0; i < 3; i++) {
			int I = i;

			jbcsR[i].addActionListener(e -> {
				current().sele.nyc[I]++;
				current().sele.nyc[I] %= NyCastle.TOT;
			});

			jbcsL[i].addActionListener(e -> {
				if (current().sele.nyc[I] == 0)
					current().sele.nyc[I] = NyCastle.TOT - 1;
				else
					current().sele.nyc[I] = (current().sele.nyc[I] - 1) % NyCastle.TOT;
			});
		}

	}

	private void addListeners$1() {

		jlbs.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (jlb.getValueIsAdjusting() || changing)
					return;
				changing = true;
				if (jlbs.getSelectedValue() == null)
					jlbs.setSelectedValue(BasisSet.current(), true);
				else
					setBS(jlbs.getSelectedValue());
				changing = false;
			}
		});

		jlb.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (jlb.getValueIsAdjusting() || changing)
					return;
				changing = true;
				if (jlb.getSelectedValue() == null)
					jlb.setSelectedValue(BasisSet.current().sele, true);
				else
					setB(jlb.getSelectedValue());
				changing = false;
			}
		});

		jlbs.list = new ReorderListener<BasisSet>() {

			@Override
			public void reordered(int ori, int fin) {
				changing = false;
				List<BasisSet> l = BasisSet.list();
				BasisSet b = l.remove(ori);
				l.add(fin, b);
			}

			@Override
			public void reordering() {
				changing = true;
			}

		};

		jlb.list = new ReorderListener<BasisLU>() {

			@Override
			public boolean add(BasisLU blu) {
				BasisSet.current().lb.add(blu);
				return true;
			}

			@Override
			public void reordered(int ori, int fin) {
				changing = false;
				List<BasisLU> l = BasisSet.current().lb;
				BasisLU b = l.remove(ori);
				l.add(fin, b);
			}

			@Override
			public void reordering() {
				changing = true;
			}

		};

		bsadd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				BasisSet b = new BasisSet();
				vbs.clear();
				vbs.addAll(BasisSet.list());
				jlbs.setListData(vbs);
				jlbs.setSelectedValue(b, true);
				setBS(b);
				changing = false;
			}
		});

		bsrem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				BasisSet.list().remove(current());
				vbs.clear();
				vbs.addAll(BasisSet.list());
				jlbs.setListData(vbs);
				BasisSet b = BasisSet.list().get(BasisSet.list().size() - 1);
				jlbs.setSelectedValue(b, true);
				setBS(b);
				changing = false;
			}
		});

		bscop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				BasisSet b = new BasisSet(current());
				vbs.clear();
				vbs.addAll(BasisSet.list());
				jlbs.setListData(vbs);
				jlbs.setSelectedValue(b, true);
				setBS(b);
				changing = false;
			}
		});

		badd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				BasisLU b = current().add();
				vb.clear();
				vb.addAll(current().lb);
				jlb.setListData(vb);
				jlb.setSelectedValue(b, true);
				setB(b);
				changing = false;
			}
		});

		brem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				BasisLU b = current().remove();
				vb.clear();
				vb.addAll(current().lb);
				jlb.setListData(vb);
				jlb.setSelectedValue(b, true);
				setB(b);
				changing = false;
			}
		});

		bcop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changing = true;
				BasisLU b = current().copyCurrent();
				vb.clear();
				vb.addAll(current().lb);
				jlb.setListData(vb);
				jlb.setSelectedValue(b, true);
				setB(b);
				changing = false;
			}
		});

		bsjtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = bsjtf.getText().trim();
				if (str.length() > 0)
					BasisSet.current().name = str;
				bsjtf.setText(BasisSet.current().name);
				jlbs.repaint();
			}
		});

		bjtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String str = bjtf.getText().trim();
				if (str.length() > 0)
					BasisSet.current().sele.name = str;
				bjtf.setText(BasisSet.current().sele.name);
				jlb.repaint();
			}
		});

	}

	private void addListeners$2() {
		jlcs.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (changing || e.getValueIsAdjusting())
					return;
				changing = true;
				if (jlcs.getSelectedValue() == null)
					jlcs.setSelectedIndex(0);
				setCS(jlcs.getSelectedIndex());
				changing = false;
			}
		});

		jlcl.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (changing || e.getValueIsAdjusting())
					return;
				changing = true;
				setCL(jlcs.getSelectedIndex());
				changing = false;
			}
		});

		jlcn.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (changing || e.getValueIsAdjusting())
					return;
				changing = true;
				setCN();
				changing = false;
			}
		});

		jlc.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (changing || arg0.getValueIsAdjusting())
					return;
				changing = true;
				setC();
				changing = false;
			}
		});

		setc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				lu().set(jlc.list.get(jlc.getSelectedRow()).forms);
				changeLU();
			}
		});

		reset.addActionListener(x -> {
			lub.resetBackup();
		});

		search.addActionListener(x -> {
			comboName = cjtf.getText();
			changing = true;
			setCL(jlcs.getSelectedIndex());
			changing = false;
		});

		cjtf.addActionListener(x -> {
			comboName = cjtf.getText();
			changing = true;
			setCL(jlcs.getSelectedIndex());
			changing = false;
		});

		combo.addActionListener(x -> {
			if (combo.isSelected() && lub.sf != null) {
				Unit unit = lub.sf.unit;
				setLvs(unit.forms[unit.forms.length - 1]);
				cunit = unit;
				outside = true;
			} else {
				cunit = null;
			}
			changing = true;
			setCL(jlcs.getSelectedIndex());
			changing = false;
		});
	}

	private void changeLU() {
		jlcn.setListData(lu().coms.toArray(new Combo[0]));
		setCN();
		updateSetC();
		lub.updateLU();
		setLvs(lub.sf);
		trea.callBack(null);
	}

	private void ini() {
		add(back);
		add(jspbs);
		add(jspb);
		add(jspt);
		add(bsadd);
		add(bsrem);
		add(bscop);
		add(badd);
		add(brem);
		add(bcop);
		add(lub);
		add(back);
		add(unit);
		add(jspcs);
		add(jspcl);
		add(jspc);
		add(jspcn);
		add(lub);
		add(jspul);
		add(setc);
		add(bsjtf);
		add(bjtf);
		add(form);
		add(lvjtf);
		add(pcoin);
		add(lvorb);
		add(ncb);
		add(reset);
		add(cjtf);
		add(search);
		add(combo);
		add(jbcsR[0] = new JBTN(0, ">"));
		add(jbcsR[1] = new JBTN(0, ">"));
		add(jbcsR[2] = new JBTN(0, ">"));
		add(jbcsL[0] = new JBTN(0, "<"));
		add(jbcsL[1] = new JBTN(0, "<"));
		add(jbcsL[2] = new JBTN(0, "<"));
		ul.setCellRenderer(new UnitLCR());
		int m0 = ListSelectionModel.SINGLE_SELECTION;
		int m1 = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		jlbs.setSelectedValue(current(), true);
		jlcs.setSelectedIndex(0);
		jlbs.setSelectionMode(m0);
		jlb.setSelectionMode(m0);
		jlcs.setSelectionMode(m0);
		jlcl.setSelectionMode(m1);
		jlcn.setSelectionMode(m0);
		ul.setSelectionMode(m0);
		jlc.getSelectionModel().setSelectionMode(m0);
		setCS(0);
		setBS(current());
		lub.setLU(lu());
		bsjtf.setText(BasisSet.current().name);
		bjtf.setText(BasisSet.current().sele.name);
		changeLU();
		addListeners$0();
		addListeners$1();
		addListeners$2();
		lvorb.setEnabled(lub.sf != null);
	}

	private LineUp lu() {
		return BasisSet.current().sele.lu;
	}

	private void setB(BasisLU b) {
		current().sele = b;
		lub.setLU(b == null ? null : b.lu);
		brem.setEnabled(current().lb.size() > 1);
		bjtf.setText(BasisSet.current().sele.name);
		ncb.set(b.nyc);
		changeLU();
		callBack(lub.sf);
		trea.callBack(null);
	}

	private void setBS(BasisSet bs) {
		BasisSet.setCurrent(bs);
		vb.clear();
		vb.addAll(bs.lb);
		jlb.setListData(vb);
		BasisLU b = bs.sele;
		jlb.setSelectedValue(b, true);
		bsjtf.setText(BasisSet.current().name);
		bsrem.setEnabled(current() != BasisSet.def());
		setB(b);
		jlcn.setBasis(bs);
		trea.callBack(null);
	}

	private void setC() {
		if (outside) {
			jlcs.setSelectedIndex(0);
			jlcl.setListData(Interpret.getComboFilter(0));
		}
		updateSetC();
	}

	private void setCL(int cs) {
		int[] cls = jlcl.getSelectedIndices();
		List<Combo> lc = new ArrayList<>();
		List<Combo> comboList = UserProfile.getBCData().combos.getList();
		UserProfile.getUserPacks().forEach(p -> comboList.addAll(p.combos.getList()));
		if (cls.length == 0) {
			for (int i = 0; i < CommonStatic.getBCAssets().filter[cs].length; i++) {
				int finalI = i;
				for (Combo c : comboList.stream()
						.filter(c -> c.type == CommonStatic.getBCAssets().filter[cs][finalI])
						.collect(Collectors.toList())) {
					String name = c.getName();
					if (name != null && name.toLowerCase().contains(comboName.toLowerCase()))
						lc.add(c);
				}
			}
		} else {
			for (int val : cls)
				for (Combo c : comboList.stream()
						.filter(c -> c.type == CommonStatic.getBCAssets().filter[cs][val])
						.collect(Collectors.toList())) {
					String name = c.getName();
					if (name != null && name.toLowerCase().contains(comboName.toLowerCase()))
						lc.add(c);
				}
		}
		if (cunit != null) {
			List<Combo> combos = cunit.allCombo();
			lc = lc.stream().filter(combos::contains).collect(Collectors.toList());
		}
		jlc.setList(lc);
		jlc.getSelectionModel().setSelectionInterval(0, 0);
		outside = false;
		setC();
	}

	private void setCN() {
		lub.select(jlcn.getSelectedValue());
	}

	private void setCS(int cs) {
		jlcl.setListData(Interpret.getComboFilter(cs));
		setCL(cs);
	}

	private void setLvs(Form f) {
		lvorb.setEnabled(f != null);

		if (f == null) {
			lvjtf.setText("");
			pcoin.setText("");
			return;
		}

		lvorb.setEnabled(f.orbs != null);

		String[] strs = UtilPC.lvText(f, lu().getLv(f).getLvs());
		lvjtf.setText(strs[0]);
		pcoin.setText(strs[1]);
	}

	private void updateSetC() {
		Combo com = jlc.list.size() > 0 && jlc.getSelectedRow() != -1 ? jlc.list.get(jlc.getSelectedRow()) : null;
		setc.setEnabled(com != null && !lu().contains(com));
		boolean b = false;
		if (com != null)
			b = lu().willRem(com);
		setc.setForeground(b ? Color.RED : Color.BLACK);
		setc.setText(0, "set" + (b ? "1" : "0"));

	}
}