
package page.pack;

import common.CommonStatic;
import common.battle.data.CustomUnit;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.anim.AnimCE;
import common.util.unit.Form;
import common.util.unit.Unit;
import common.util.unit.UnitLevel;
import main.Opts;
import page.JBTN;
import page.JL;
import page.JTF;
import page.Page;
import page.info.edit.FormEditPage;
import page.support.AnimLCR;
import page.support.ReorderList;
import page.support.ReorderListener;
import page.support.UnitLCR;
import page.view.UnitViewPage;
import utilpc.Interpret;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.*;

public class UnitManagePage extends Page {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");
	private final Vector<UserPack> vpack = new Vector<>(UserProfile.getUserPacks());
	private final JList<UserPack> jlp = new JList<>(vpack);
	private final JScrollPane jspp = new JScrollPane(jlp);
	private final JList<Unit> jlu = new JList<>();
	private final JScrollPane jspu = new JScrollPane(jlu);
	private final ReorderList<Form> jlf = new ReorderList<>();
	private final JScrollPane jspf = new JScrollPane(jlf);
	private final JList<AnimCE> jld = new JList<>(new Vector<>(AnimCE.map().values()));
	private final JScrollPane jspd = new JScrollPane(jld);
	private final JList<UnitLevel> jll = new JList<>();
	private final JScrollPane jspl = new JScrollPane(jll);

	private final JBTN addu = new JBTN(0, "add");
	private final JBTN remu = new JBTN(0, "rem");
	private final JBTN addf = new JBTN(0, "add");
	private final JBTN remf = new JBTN(0, "rem");
	private final JBTN addl = new JBTN(0, "add");
	private final JBTN reml = new JBTN(0, "rem");
	private final JBTN edit = new JBTN(0, "edit");
	private final JBTN vuni = new JBTN(0, "vuni");
	private final JBTN cmbo = new JBTN(0, "combo");

	private final JTF jtff = new JTF();
	private final JTF maxl = new JTF();
	private final JTF maxp = new JTF();
	private final JTF jtfl = new JTF();
	private final JComboBox<String> rar = new JComboBox<>(Interpret.RARITY);
	private final JComboBox<UnitLevel> cbl = new JComboBox<>();

	private final JL lbp = new JL(0, "pack");
	private final JL lbu = new JL(0, "unit");
	private final JL lbd = new JL(0, "seleanim");
	private final JL lbml = new JL(0, "maxl");
	private final JL lbmp = new JL(0, "maxp");
	private final JL lbf = new JL(1, "forms");

	private UserPack pac;
	private Unit uni;
	private Form frm;
	private UnitLevel ul;
	private boolean changing = false;

	public UnitManagePage(Page p, UserPack pack) {
		super(p);

		pac = pack;
		ini();
	}

	@Override
	protected void renew() {
		setPack(pac);
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);

		int w = 50, dw = 150;

		set(lbp, x, y, w, 100, 400, 50);
		set(jspp, x, y, w, 150, 400, 600);
		w += 450;
		set(lbu, x, y, w, 100, 300, 50);
		set(jspu, x, y, w, 150, 300, 600);
		set(addu, x, y, w, 800, 150, 50);
		set(remu, x, y, w + dw, 800, 150, 50);
		set(vuni, x, y, w, 950, 300, 50);
		w += 300;
		set(lbf, x, y, w, 100, 300, 50);
		set(jspf, x, y, w, 150, 300, 600);
		set(jtff, x, y, w, 850, 300, 50);
		set(addf, x, y, w, 800, 150, 50);
		set(remf, x, y, w + dw, 800, 150, 50);
		set(edit, x, y, w, 950, 300, 50);
		w += 300;
		set(lbd, x, y, w, 100, 300, 50);
		set(jspd, x, y, w, 150, 300, 600);
		w += 350;
		set(lbml, x, y, w, 100, 300, 50);
		set(maxl, x, y, w, 150, 300, 50);
		set(lbmp, x, y, w, 200, 300, 50);
		set(maxp, x, y, w, 250, 300, 50);
		set(rar, x, y, w, 300, 300, 50);
		set(cbl, x, y, w, 400, 300, 50);
		set(cmbo, x, y, w, 500, 300, 50);
		w += 500;
		set(jspl, x, y, w, 150, 300, 500);
		set(jtfl, x, y, w, 700, 300, 50);
		set(addl, x, y, w, 750, 150, 50);
		set(reml, x, y, w + dw, 750, 150, 50);

	}

	private void addListeners() {

		back.addActionListener(arg0 -> changePanel(getFront()));

		jld.addListSelectionListener(arg0 -> {
			if (jld.getValueIsAdjusting())
				return;
			boolean edi = pac != null && pac.editable && jld.getSelectedValue() != null;
			addu.setEnabled(edi);
			addf.setEnabled(edi && uni != null);
		});

		jlp.addListSelectionListener(arg0 -> {
			if (changing || jlp.getValueIsAdjusting())
				return;
			changing = true;
			setPack(jlp.getSelectedValue());
			changing = false;
		});

		jlf.list = new ReorderListener<Form>() {

			@Override
			public void reordered(int ori, int fin) {
				List<Form> lsm = new ArrayList<>();
				Collections.addAll(lsm, uni.forms);
				Form sm = lsm.remove(ori);
				lsm.add(fin, sm);
				for (int i = 0; i < uni.forms.length; i++) {
					uni.forms[i] = lsm.get(i);
					uni.forms[i].fid = i;
				}
				changing = false;
			}

			@Override
			public void reordering() {
				changing = true;
			}

		};

	}

	private void addListeners$1() {

		jlu.addListSelectionListener(e -> {
			if (changing || jlu.getValueIsAdjusting())
				return;
			changing = true;
			setUnit(jlu.getSelectedValue());
			changing = false;
		});

		addu.addActionListener(arg0 -> {
			changing = true;
			CustomUnit cu = new CustomUnit();
			Unit u = new Unit(pac.getNextID(Unit.class), jld.getSelectedValue(), cu);
			pac.units.add(u);
			jlu.setListData(pac.units.toRawArray());
			jlu.setSelectedValue(u, true);
			setUnit(u);
			changing = false;
		});

		remu.addActionListener(arg0 -> {
			if (!Opts.conf())
				return;
			changing = true;
			int ind = jlu.getSelectedIndex();
			pac.units.remove(uni);
			uni.lv.units.remove(uni);
			jlu.setListData(pac.units.toRawArray());
			if (ind >= 0)
				ind--;
			jlu.setSelectedIndex(ind);
			setUnit(jlu.getSelectedValue());
			changing = false;
		});

		maxl.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				if (changing || uni == null)
					return;
				int lv = CommonStatic.parseIntN(maxl.getText());
				if (lv > 0)
					uni.max = lv;
				maxl.setText("" + uni.max);
			}

		});

		maxp.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				if (changing || uni == null)
					return;
				int lv = CommonStatic.parseIntN(maxp.getText());
				if (lv >= 0)
					uni.maxp = lv;
				maxp.setText("" + uni.maxp);
			}

		});

		rar.addActionListener(arg0 -> {
			if (changing)
				return;
			uni.rarity = rar.getSelectedIndex();
		});

		cbl.addActionListener(arg0 -> {
			if (changing || uni == null)
				return;
			UnitLevel sel = (UnitLevel) cbl.getSelectedItem();

			if(sel == null)
				return;

			uni.lv.units.remove(uni);
			uni.lv = sel;
			sel.units.add(uni);
			setUnit(uni);
			setLevel(ul);
		});

		cmbo.addActionListener(x -> changePanel(new ComboEditPage(getThis(), pac)));

	}

	private void addListeners$2() {

		jlf.addListSelectionListener(e -> {
			if (changing || jlf.getValueIsAdjusting())
				return;
			changing = true;
			setForm(jlf.getSelectedValue());
			changing = false;
		});

		addf.addActionListener(arg0 -> {
			changing = true;
			CustomUnit cu = new CustomUnit();
			AnimCE ac = jld.getSelectedValue();
			frm = new Form(uni, uni.forms.length, "new form", ac, cu);
			uni.forms = Arrays.copyOf(uni.forms, uni.forms.length + 1);
			uni.forms[uni.forms.length - 1] = frm;
			setUnit(uni);
			changing = false;
		});

		remf.addActionListener(arg0 -> {
			if (!Opts.conf())
				return;
			changing = true;
			int ind = jlf.getSelectedIndex();
			Form[] fs = new Form[uni.forms.length - 1];
			int x = 0;
			for (int i = 0; i < uni.forms.length; i++)
				if (i != ind)
					fs[x++] = uni.forms[i];
			uni.forms = fs;
			for (int i = 0; i < uni.forms.length; i++)
				uni.forms[i].fid = i;
			setUnit(uni);
			if (ind >= 0)
				ind--;
			jlf.setSelectedIndex(ind);
			setForm(jlf.getSelectedValue());
			changing = false;
		});

		jtff.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				frm.name = jtff.getText().trim();
			}

		});

		edit.addActionListener(e -> changePanel(new FormEditPage(getThis(), pac, frm)));

	}

	private void addListeners$3() {

		jll.addListSelectionListener(arg0 -> {
			if (changing || jll.getValueIsAdjusting())
				return;
			setLevel(jll.getSelectedValue());
		});

		addl.addActionListener(arg0 -> {
			changing = true;
			ul = new UnitLevel(pac.getNextID(UnitLevel.class), CommonStatic.getBCAssets().defLv);
			pac.unitLevels.add(ul);
			setPack(pac);
			changing = false;
		});

		reml.addActionListener(arg0 -> {
			changing = true;
			int ind = jll.getSelectedIndex();
			UnitLevel ul = jll.getSelectedValue();
			pac.unitLevels.remove(ul);
			setPack(pac);
			if (ind >= pac.unitLevels.size())
				ind--;
			jll.setSelectedIndex(ind);
			setLevel(jll.getSelectedValue());
			changing = false;
		});

		jtfl.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				int[] lvs = CommonStatic.parseIntsN(jtfl.getText());
				for (int i = 0; i < lvs.length; i++)
					if (lvs[i] > 0 && (i == 0 || lvs[i] >= ul.lvs[i - 1]))
						ul.lvs[i] = lvs[i];
				jtfl.setText(ul.toString());

			}

		});

		vuni.setLnr((e) -> changePanel(new UnitViewPage(this, pac.getSID())));
	}

	private void ini() {
		add(back);
		add(jspp);
		add(jspu);
		add(jspd);
		add(addu);
		add(remu);
		add(edit);
		add(vuni);
		add(jspf);
		add(jtff);
		add(addf);
		add(remf);
		add(edit);
		add(vuni);
		add(maxl);
		add(maxp);
		add(cbl);
		add(rar);
		add(lbp);
		add(lbu);
		add(lbd);
		add(lbml);
		add(lbmp);
		add(lbf);
		add(jspl);
		add(addl);
		add(reml);
		add(jtfl);
		add(cmbo);
		jlu.setCellRenderer(new UnitLCR());
		jlf.setCellRenderer(new AnimLCR());
		jld.setCellRenderer(new AnimLCR());
		setPack(pac);
		addListeners();
		addListeners$1();
		addListeners$2();
		addListeners$3();
	}

	private void setForm(Form f) {
		frm = f;
		if (jlf.getSelectedValue() != frm) {
			boolean boo = changing;
			changing = true;
			jlf.setSelectedValue(frm, true);
			changing = boo;
		}
		boolean b = frm != null && pac.editable;
		edit.setEnabled(frm != null && frm.du instanceof CustomUnit);
		remf.setEnabled(b && frm.fid > 0);
		jtff.setEnabled(b);
		if (frm != null) {
			jtff.setText(f.name);
		} else {
			jtff.setText("");
		}
	}

	private void setLevel(UnitLevel ulv) {
		ul = ulv;
		if (jll.getSelectedValue() != ul) {
			boolean boo = changing;
			changing = true;
			jll.setSelectedValue(ul, true);
			changing = boo;
		}
		boolean b = ul != null && pac.editable;
		jtfl.setEnabled(b);
		if (ul != null)
			jtfl.setText(ul.toString());
		else
			jtfl.setText("");
		reml.setEnabled(b && ul.units.size() == 0);
	}

	private void setPack(UserPack pack) {
		pac = pack;
		if (jlp.getSelectedValue() != pack) {
			boolean boo = changing;
			changing = true;
			jlp.setSelectedValue(pac, true);
			changing = boo;
		}
		boolean b = pac != null && pac.editable;
		addu.setEnabled(b && jld.getSelectedValue() != null);
		edit.setEnabled(b);
		addl.setEnabled(b);
		vuni.setEnabled(pac != null);
		boolean boo = changing;
		changing = true;
		if (pac == null) {
			jlu.setListData(new Unit[0]);
			jll.setListData(new UnitLevel[0]);
			cbl.removeAllItems();
		} else {
			jlf.allowDrag(pac.editable);
			jlu.setListData(pac.units.toRawArray());
			jlu.clearSelection();
			jll.setListData(pac.unitLevels.toArray());
			setLevel(jll.getSelectedValue());
			List<UnitLevel> ulist = UserProfile.getAll(pac.getSID(), UnitLevel.class);
			cbl.setModel(new DefaultComboBoxModel<>(ulist.toArray(new UnitLevel[0])));
		}
		changing = boo;
		if (pac == null || !pac.units.contains(uni))
			uni = null;
		if (pac == null || !pac.unitLevels.contains(ul))
			ul = null;
		setUnit(uni);
		setLevel(ul);
	}

	private void setUnit(Unit unit) {
		uni = unit;
		if (jlu.getSelectedValue() != uni) {
			boolean boo = changing;
			changing = true;
			jlu.setSelectedValue(uni, true);
			changing = boo;
		}
		boolean b = unit != null && pac.editable;
		remu.setEnabled(b);
		rar.setEnabled(b);
		cbl.setEnabled(b);
		addf.setEnabled(b && jld.getSelectedValue() != null && unit.forms.length < 3);
		maxl.setEditable(b);
		maxp.setEditable(b);
		boolean boo = changing;
		changing = true;
		if (unit == null) {
			jlf.setListData(new Form[0]);
			maxl.setText("");
			maxp.setText("");
			rar.setSelectedItem(null);
			cbl.setSelectedItem(null);
		} else {
			jlf.setListData(unit.forms);
			maxl.setText("" + uni.max);
			maxp.setText("" + uni.maxp);
			rar.setSelectedIndex(uni.rarity);
			cbl.setSelectedItem(uni.lv);
		}
		changing = boo;
		if (frm != null && frm.unit != unit)
			frm = null;
		setForm(frm);
	}

}
