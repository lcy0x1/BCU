package page.info.edit;

import common.CommonStatic;
import common.battle.Basis;
import common.battle.BasisSet;
import common.battle.data.AtkDataModel;
import common.battle.data.CustomEntity;
import common.pack.Identifier;
import common.pack.IndexContainer.Indexable;
import common.pack.PackData;
import common.pack.UserProfile;
import common.util.Animable;
import common.util.anim.AnimCE;
import common.util.anim.AnimU;
import common.util.anim.AnimU.UType;
import common.util.lang.Editors;
import common.util.pack.Background;
import common.util.pack.Soul;
import common.util.pack.Soul.SoulType;
import common.util.unit.AbEnemy;
import common.util.unit.Enemy;
import common.util.unit.Form;
import common.util.unit.Unit;
import main.Opts;
import page.*;
import page.anim.DIYViewPage;
import page.info.edit.SwingEditor.EditCtrl;
import page.info.edit.SwingEditor.IdEditor;
import page.info.filter.AbEnemyFindPage;
import page.info.filter.EnemyFindPage;
import page.info.filter.UnitFindPage;
import page.support.ListJtfPolicy;
import page.support.ReorderList;
import page.support.ReorderListener;
import page.view.BGViewPage;
import page.view.EnemyViewPage;
import page.view.UnitViewPage;

import javax.swing.*;
import java.util.*;

import static common.util.Data.*;

public abstract class EntityEditPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");

	private final JL lhp = new JL(1, "HP");
	private final JL lhb = new JL(1, "HB");
	private final JL lsp = new JL(1, "speed");
	private final JL lra = new JL(1, "range");
	private final JL lwd = new JL(1, "width");
	private final JL lsh = new JL(1, "shield");
	private final JL ltb = new JL(1, "TBA");
	private final JL lbs = new JL(1, "tbase");
	private final JL ltp = new JL(1, "type");
	private final JL lct = new JL(1, "count");
	private final JL ldps = new JL(1,"DPS");
	private final JL lwp = new JL(1,"will");
	private final JL cdps = new JL();
	private final JTF fhp = new JTF();
	private final JTF fhb = new JTF();
	private final JTF fsp = new JTF();
	private final JTF fra = new JTF();
	private final JTF fwd = new JTF();
	private final JTF fsh = new JTF();
	private final JTF ftb = new JTF();
	private final JTF fbs = new JTF();
	private final JTF ftp = new JTF();
	private final JTF fct = new JTF();
	private final JTF fwp = new JTF();
	private final ReorderList<String> jli = new ReorderList<>();
	private final JScrollPane jspi = new JScrollPane(jli);
	private final JBTN add = new JBTN(0, "add");
	private final JBTN rem = new JBTN(0, "rem");
	private final JBTN copy = new JBTN(0, "copy");
	private final JBTN link = new JBTN(0, "link");
	private final JTG comm = new JTG(1, "common");
	private final JTF atkn = new JTF();
	private final JL lpst = new JL(1, "postaa");
	private final JL vpst = new JL();
	private final JL litv = new JL(1, "atkf");
	private final JL lrev = new JL(1, "post-HB");
	private final JL lres = new JL(1, "post-death");
	private final JL vrev = new JL();
	private final JL vres = new JL();
	private final JL vitv = new JL();
	private final JComboBox<AnimCE> jcba = new JComboBox<>();
	private final JComboBox<Soul> jcbs = new JComboBox<>();
	private final ListJtfPolicy ljp = new ListJtfPolicy();
	private final AtkEditTable aet;
	private final ProcTable.MainProcTable mpt;
	private final JScrollPane jspm;
	private final CustomEntity ce;
	private final String pack;

	private final ProcTable.AtkProcTable apt;
	private final JScrollPane jsp;

	private boolean changing = false;
	private EnemyFindPage efp;
	private UnitFindPage ufp;

	private SupPage<? extends Indexable<?, ?>> sup;
	private IdEditor<?> editor;

	protected final boolean editable;
	protected final Basis bas = BasisSet.current();

	public EntityEditPage(Page p, String pac, CustomEntity e, boolean edit, boolean isEnemy) {
		super(p);
		Editors.setEditorSupplier(new EditCtrl(isEnemy, this));
		pack = pac;
		ce = e;
		aet = new AtkEditTable(this, edit, !isEnemy);
		apt = new ProcTable.AtkProcTable(aet, edit, !isEnemy);
		aet.setProcTable(apt);
		jsp = new JScrollPane(apt);
		mpt = new ProcTable.MainProcTable(this, edit, !isEnemy);
		jspm = new JScrollPane(mpt);
		editable = edit;
		if (!editable)
			jli.setDragEnabled(false);
	}

	@Override
	public void callBack(Object o) {
		if (o instanceof int[]) {
			int[] vals = (int[]) o;
			if (vals.length == 2) {
				ce.abi = vals[0];
				ce.loop = (ce.abi & AB_GLASS) > 0 ? 1 : -1;
			}
		}
		setData(ce);
	}

	public SupPage<Background> getBGSup(IdEditor<Background> edi) {
		editor = edi;
		SupPage<Background> ans = new BGViewPage(this, pack);
		sup = ans;
		return ans;
	}

	public SupPage<AbEnemy> getEnemySup(IdEditor<AbEnemy> edi) {
		editor = edi;

		PackData.UserPack p = UserProfile.getUserPack(pack);

		SupPage<AbEnemy> ans;

		if(p != null) {
			ans = new AbEnemyFindPage(this, pack, p.desc.dependency.toArray(new String[0]));
		} else {
			ans = new AbEnemyFindPage(this);
		}

		sup = ans;

		return ans;
	}

	public SupPage<Unit> getUnitSup(IdEditor<Unit> edi) {
		editor = edi;

		PackData.UserPack p = UserProfile.getUserPack(pack);

		SupPage<Unit> ans;

		if(p != null) {
			ans = new UnitFindPage(this, pack, p.desc.dependency);
		} else {
			ans = new UnitFindPage(this);
		}

		sup = ans;
		return ans;
	}

	protected double getAtk() {
		return 1;
	}

	protected double getLvAtk() {
		return 1;
	}

	protected double getDef() {
		return 1;
	}

	protected abstract void getInput(JTF jtf, int[] v);

	protected void ini() {
		set(lhp);
		set(lhb);
		set(lsp);
		set(lwd);
		set(lsh);
		set(lra);
		set(ltb);
		set(lbs);
		set(ltp);
		set(lct);
		set(fhp);
		set(fhb);
		set(fsp);
		set(fsh);
		set(fwd);
		set(fra);
		set(ftb);
		set(fbs);
		set(ftp);
		set(fct);
		set(lwp);
		set(fwp);
		set(ldps);
		set(cdps);
		ljp.end();
		add(jspi);
		add(aet);
		add(jspm);
		add(add);
		add(rem);
		add(copy);
		add(link);
		add(back);
		set(atkn);
		set(lpst);
		set(vpst);
		set(litv);
		set(vitv);
		set(lrev);
		set(lres);
		set(vrev);
		set(vres);
		add(comm);
		add(jcbs);
		Vector<Soul> vec = new Vector<>();
		vec.add(null);
		vec.addAll(UserProfile.getAll(pack, Soul.class));
		jcbs.setModel(new DefaultComboBoxModel<>(vec));
		if (editable) {
			add(jcba);
			Vector<AnimCE> vda = new Vector<>();
			AnimCE ac = ((AnimCE) ce.getPack().anim);
			if (!ac.inPool())
				vda.add(ac);
			vda.addAll(AnimCE.map().values());
			jcba.setModel(new DefaultComboBoxModel<>(vda));
		}
		setFocusTraversalPolicy(ljp);
		setFocusCycleRoot(true);
		addListeners();
		atkn.setToolTipText("<html>use name \"revenge\" for attack during HB animation<br>"
				+ "use name \"resurrection\" for attack during death animation</html>");
		ftp.setToolTipText(
				"<html>" + "+1 for normal attack<br>" + "+2 to attack kb<br>" + "+4 to attack underground<br>"
						+ "+8 to attack corpse<br>" + "+16 to attack soul<br>" + "+32 to attack ghost<br>" +
						"+64 to attack entities that can revive others<br>" + "+128 to attack enter animations</html>");
		fwp.setToolTipText(
				"<html>" + "The amount of slots this entity will take of the limit"
				+ " when spawned</html>");

		add.setEnabled(editable);
		rem.setEnabled(editable);
		copy.setEnabled(editable);
		link.setEnabled(editable);
		atkn.setEnabled(editable);
		comm.setEnabled(editable);
		jcbs.setEnabled(editable);

		add(jsp);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void renew() {
		if (efp != null && efp.getSelected() != null
				&& Opts.conf("do you want to overwrite stats? This operation cannot be undone")) {
			Enemy e = efp.getSelected();
			ce.importData(e.de);
			setData(ce);
		}
		if (ufp != null && ufp.getForm() != null
				&& Opts.conf("do you want to overwrite stats? This operation cannot be undone")) {
			Form f = ufp.getForm();
			ce.importData(f.du);
			setData(ce);
		}
		if (sup != null && editor != null) {
			if(sup.getSelected() != null) {
				Identifier val = sup.getSelected().getID();
				editor.callback(val);
			}
		}
		sup = null;
		editor = null;
		efp = null;
		ufp = null;
	}

	@Override
	protected void resized(int x, int y) {
		setSize(x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(lhp, x, y, 50, 100, 100, 50);
		set(fhp, x, y, 150, 100, 200, 50);
		set(lhb, x, y, 50, 150, 100, 50);
		set(fhb, x, y, 150, 150, 200, 50);
		set(lsp, x, y, 50, 200, 100, 50);
		set(fsp, x, y, 150, 200, 200, 50);
		set(lsh, x, y, 50, 250, 100, 50);
		set(fsh, x, y, 150, 250, 200, 50);
		set(lwd, x, y, 50, 300, 100, 50);
		set(fwd, x, y, 150, 300, 200, 50);

		set(jspm, x, y, 1850, 100, 350, 900);
		mpt.componentResized(x, y);

		set(jspi, x, y, 350, 100, 300, 350);
		set(add, x, y, 350, 450, 150, 50);
		set(rem, x, y, 500, 450, 150, 50);
		set(copy, x, y, 350, 500, 150, 50);
		set(link, x, y, 500, 500, 150, 50);
		set(comm, x, y, 350, 550, 300, 50);
		set(atkn, x, y, 350, 600, 300, 50);

		set(lra, x, y, 50, 400, 100, 50);
		set(fra, x, y, 150, 400, 200, 50);
		set(ltb, x, y, 50, 450, 100, 50);
		set(ftb, x, y, 150, 450, 200, 50);
		set(lbs, x, y, 50, 500, 100, 50);
		set(fbs, x, y, 150, 500, 200, 50);
		set(ltp, x, y, 50, 550, 100, 50);
		set(ftp, x, y, 150, 550, 200, 50);
		set(lct, x, y, 50, 600, 100, 50);
		set(fct, x, y, 150, 600, 200, 50);
		set(lwp, x, y, 1400, 1000, 200, 50);
		set(fwp, x, y, 1600, 1000, 200, 50);

		set(aet, x, y, 650, 100, 400, 500);

		set(ldps, x, y, 650, 600, 200, 50);
		set(cdps, x, y, 850, 600, 200, 50);
		set(lpst, x, y, 650, 650, 200, 50);
		set(vpst, x, y, 850, 650, 200, 50);
		set(litv, x, y, 650, 700, 200, 50);
		set(vitv, x, y, 850, 700, 200, 50);
		set(jcba, x, y, 650, 750, 400, 50);

		if (editable) {
			set(lrev, x, y, 650, 850, 200, 50);
			set(vrev, x, y, 850, 850, 200, 50);
			set(lres, x, y, 650, 900, 200, 50);
			set(vres, x, y, 850, 900, 200, 50);
			set(jcbs, x, y, 650, 950, 400, 50);
		} else {
			set(lrev, x, y, 650, 800, 200, 50);
			set(vrev, x, y, 850, 800, 200, 50);
			set(lres, x, y, 650, 850, 200, 50);
			set(vres, x, y, 850, 850, 200, 50);
			set(jcbs, x, y, 650, 900, 400, 50);
		}

		jsp.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
		jspm.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
		apt.setPreferredSize(size(x, y, 750, 2500).toDimension());
		apt.resized(x, y);
		set(jsp, x, y, 1050, 100, 800, 900);
	}

	protected void set(JL jl) {
		jl.setHorizontalAlignment(SwingConstants.CENTER);
		add(jl);
	}

	protected void set(JTF jtf) {
		jtf.setEditable(editable);
		add(jtf);
		ljp.add(jtf);

		jtf.setLnr(e -> {
			input(jtf, jtf.getText().trim());
			setData(ce);
		});

	}

	protected void setData(CustomEntity data) {
		changing = true;
		fhp.setText("" + (int) (ce.hp * getDef()));
		fhb.setText("" + ce.hb);
		fsp.setText("" + ce.speed);
		fra.setText("" + ce.range);
		fwd.setText("" + ce.width);
		fsh.setText("" + ce.shield);
		ftb.setText("" + ce.tba);
		fbs.setText("" + ce.base);
		vpst.setText("" + ce.getPost());
		vitv.setText("" + ce.getItv());
		ftp.setText("" + ce.touch);
		fct.setText("" + ce.loop);
		fwp.setText("" + (ce.will + 1));
		cdps.setText("" + (int) (Math.round(getLvAtk() * ce.allAtk()) * getAtk()) * 30 / ce.getItv());
		comm.setSelected(data.common);
		mpt.setData(ce.rep.proc);
		int[][] raw = ce.rawAtkData();
		int pre = 0;
		int n = ce.atks.length;
		if (ce.rev != null)
			n++;
		if (ce.res != null)
			n++;
		String[] ints = new String[n];
		for (int i = 0; i < ce.atks.length; i++) {
			ints[i] = i + 1 + " " + ce.atks[i].str;
			pre += raw[i][1];
			if (pre >= ce.getAnimLen())
				ints[i] += " (out of range)";
		}
		int ix = ce.atks.length;
		if (ce.rev != null)
			ints[ix++] = ce.rev.str;
		if (ce.res != null)
			ints[ix] = ce.res.str;
		int ind = jli.getSelectedIndex();
		jli.setListData(ints);
		if (ind < 0)
			ind = 0;
		if (ind >= ints.length)
			ind = ints.length - 1;
		setA(ind);
		jli.setSelectedIndex(ind);
		Animable<AnimU<?>, UType> ene = ce.getPack();
		if (editable)
			jcba.setSelectedItem(ene.anim);
		jcbs.setSelectedItem(Identifier.get(ce.death));
		vrev.setText(ce.rev == null ? "x" : (KB_TIME[INT_HB] - ce.rev.pre + "f"));
		Soul s = Identifier.get(ce.death);
		vres.setText(ce.res == null ? "x" : s == null ? "-" : (s.len(SoulType.DEF) - ce.res.pre + "f"));
		changing = false;
	}

	protected void subListener(JBTN e, JBTN u, JBTN a, Object o) {
		e.setLnr(x -> changePanel(efp = new EnemyFindPage(getThis())));

		u.setLnr(x -> changePanel(ufp = new UnitFindPage(getThis())));

		a.setLnr(x -> {
			if (editable) {
				AnimCE anim = (AnimCE) jcba.getSelectedItem();

				if(anim != null)
					changePanel(new DIYViewPage(getThis(), anim));
			} else if (o instanceof Unit)
				changePanel(new UnitViewPage(getThis(), (Unit) o));
			else if (o instanceof Enemy)
				changePanel(new EnemyViewPage(getThis(), (Enemy) o));
		});

		e.setEnabled(editable);
		u.setEnabled(editable);
	}

	private void addListeners() {

		back.setLnr(e -> changePanel(getFront()));

		comm.setLnr(e -> {
			ce.common = comm.isSelected();
			setData(ce);
		});

		jli.addListSelectionListener(e -> {
			if (changing || jli.getValueIsAdjusting())
				return;
			changing = true;
			if (jli.getSelectedIndex() == -1)
				jli.setSelectedIndex(0);
			setA(jli.getSelectedIndex());
			changing = false;
		});

		jli.list = new ReorderListener<String>() {

			@Override
			public void reordered(int ori, int fin) {
				if (ori < ce.atks.length) {
					if (fin >= ce.atks.length)
						fin = ce.atks.length - 1;
					List<AtkDataModel> l = new ArrayList<>();
					Collections.addAll(l, ce.atks);
					l.add(fin, l.remove(ori));
					ce.atks = l.toArray(new AtkDataModel[0]);
				}
				setData(ce);
				changing = false;
			}

			@Override
			public void reordering() {
				changing = true;
			}

		};

		add.setLnr(e -> {
			changing = true;
			int n = ce.atks.length;
			int ind = jli.getSelectedIndex();
			if (ind >= ce.atks.length)
				ind = ce.atks.length - 1;
			AtkDataModel[] datas = new AtkDataModel[n + 1];
			if (ind + 1 >= 0)
				System.arraycopy(ce.atks, 0, datas, 0, ind + 1);
			ind++;
			datas[ind] = new AtkDataModel(ce);
			if (n - ind >= 0)
				System.arraycopy(ce.atks, ind, datas, ind + 1, n - ind);
			ce.atks = datas;
			setData(ce);
			jli.setSelectedIndex(ind);
			setA(ind);
			changing = false;
		});

		rem.setLnr(e -> remAtk(jli.getSelectedIndex()));

		copy.setLnr(e -> {
			changing = true;
			int n = ce.atks.length;
			int ind = jli.getSelectedIndex();
			ce.atks = Arrays.copyOf(ce.atks, n + 1);
			ce.atks[n] = ce.atks[ind].clone();
			setData(ce);
			jli.setSelectedIndex(n);
			setA(n);
			changing = false;
		});

		link.setLnr(e -> {
			changing = true;
			int n = ce.atks.length;
			int ind = jli.getSelectedIndex();
			ce.atks = Arrays.copyOf(ce.atks, n + 1);
			ce.atks[n] = ce.atks[ind];
			setData(ce);
			jli.setSelectedIndex(n);
			setA(n);
			changing = false;
		});

		jcba.addActionListener(arg0 -> {
			if (changing)
				return;
			ce.getPack().anim = (AnimCE) jcba.getSelectedItem();
			setData(ce);

		});

		jcbs.addActionListener(arg0 -> {
			if (changing)
				return;

			Soul s = (Soul) jcbs.getSelectedItem();

			ce.death = s == null ? null : s.getID();
			setData(ce);
		});

	}

	private AtkDataModel get(int ind) {
		if (ind < ce.atks.length)
			return ce.atks[ind];
		else if (ind == ce.atks.length)
			return ce.rev == null ? ce.res : ce.rev;
		else
			return ce.res;
	}

	protected void input(JTF jtf, String text) {

		if (jtf == atkn) {
			AtkDataModel adm = aet.adm;
			if (adm == null || adm.str.equals(text))
				return;
			text = ce.getAvailable(text);
			adm.str = text;
			if (text.equals("revenge")) {
				remAtk(adm);
				ce.rev = adm;
			}
			if (text.equals("resurrection")) {
				remAtk(adm);
				ce.res = adm;
			}
			return;
		}
		if (text.length() > 0) {
			int[] v = CommonStatic.parseIntsN(text);
			if (jtf == fhp) {
				v[0] /= getDef();
				if (v[0] <= 0)
					v[0] = 1;
				ce.hp = v[0];
			}
			if (jtf == fhb) {
				if (v[0] <= 0)
					v[0] = 1;
				if (v[0] > ce.hp)
					v[0] = ce.hp;
				ce.hb = v[0];
			}
			if (jtf == fsp) {
				if (v[0] < 0)
					v[0] = 0;
				if (v[0] > 150)
					v[0] = 150;
				ce.speed = v[0];
			}
			if (jtf == fra) {
				if (v[0] <= 0)
					v[0] = 1;
				ce.range = v[0];
			}
			if (jtf == fwd) {
				if (v[0] <= 0)
					v[0] = 1;
				ce.width = v[0];
			}
			if (jtf == fsh) {
				if (v[0] < 0)
					v[0] = 0;
				ce.shield = v[0];
			}
			if (jtf == ftb) {
				if (v[0] < 0)
					v[0] = 0;
				ce.tba = v[0];
			}
			if (jtf == fbs) {
				if (v[0] < 0)
					v[0] = 0;
				ce.base = v[0];
			}
			if (jtf == ftp) {
				if (v[0] < 1)
					v[0] = 1;
				ce.touch = v[0];
			}
			if (jtf == fct) {
				if (v[0] < -1)
					v[0] = -1;
				ce.loop = v[0];
			}
			if (jtf == fwp) {
				if (v[0] < 0)
					v[0] = 0;
				if (v[0] > 50)
					v[0] = 50;
				ce.will = v[0] - 1;
			}
			getInput(jtf, v);
		}
		setData(ce);
	}

	private void remAtk(AtkDataModel adm) {
		for (int i = 0; i < ce.atks.length; i++)
			if (ce.atks[i] == adm)
				remAtk(i);
	}

	private void remAtk(int ind) {
		changing = true;
		int n = ce.atks.length;
		if (ind >= n) {
			if (ind == n)
				if (ce.rev != null)
					ce.rev = null;
				else
					ce.res = null;
			else
				ce.res = null;
		} else if (n > 1) {
			AtkDataModel[] datas = new AtkDataModel[n - 1];
			if (ind >= 0)
				System.arraycopy(ce.atks, 0, datas, 0, ind);
			if (n - (ind + 1) >= 0)
				System.arraycopy(ce.atks, ind + 1, datas, ind + 1 - 1, n - (ind + 1));
			ce.atks = datas;
		}
		setData(ce);
		ind--;
		if (ind < 0)
			ind = 0;
		jli.setSelectedIndex(ind);
		setA(ind);
		changing = false;
	}

	private void setA(int ind) {
		AtkDataModel adm = get(ind);
		assert adm != null;
		link.setEnabled(editable && ind < ce.atks.length);
		copy.setEnabled(editable && ind < ce.atks.length);
		atkn.setEnabled(editable && ind < ce.atks.length);
		atkn.setText(adm.str);
		aet.setData(adm, getAtk(), getLvAtk());
		rem.setEnabled(editable && (ce.atks.length > 1 || ind >= ce.atks.length));
	}

}
