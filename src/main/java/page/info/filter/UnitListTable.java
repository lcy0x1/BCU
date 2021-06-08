package page.info.filter;

import common.battle.Basis;
import common.battle.BasisSet;
import common.battle.data.MaskUnit;
import common.system.Node;
import common.util.unit.Enemy;
import common.util.unit.Form;
import common.util.unit.Unit;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.UnitInfoPage;
import page.support.SortTable;
import page.support.UnitTCR;

import java.awt.*;

public class UnitListTable extends SortTable<Form> {

	private static final long serialVersionUID = 1L;

	private static String[] tit;

	static {
		redefine();
	}

	public static void redefine() {
		tit = new String[] { "ID", "name", Page.get(MainLocale.INFO, "pref"), "HP", "HB", "atk", Page.get(MainLocale.INFO, "range"),
				Page.get(MainLocale.INFO, "speed"), "dps", Page.get(MainLocale.INFO, "preaa"), "CD", Page.get(MainLocale.INFO, "price"), Page.get(MainLocale.INFO, "atkf"), Page.get(MainLocale.INFO, "will") };
	}

	private final Page page;

	public UnitListTable(Page p) {
		page = p;

		setDefaultRenderer(Enemy.class, new UnitTCR(lnk));

	}

	public void clicked(Point p) {
		if (list == null)
			return;
		int c = getColumnModel().getColumnIndexAtX(p.x);
		c = lnk[c];
		int r = p.y / getRowHeight();
		if (r < 0 || r >= list.size() || c != 1)
			return;
		Form e = list.get(r);
		Node<Unit> n = Node.getList(list, e);
		MainFrame.changePanel(new UnitInfoPage(page, n));

	}

	@Override
	public Class<?> getColumnClass(int c) {
		c = lnk[c];
		if (c == 1)
			return Enemy.class;
		return String.class;
	}

	@Override
	protected int compare(Form e0, Form e1, int c) {
		if (c == 0) {
			int val = e0.uid.compareTo(e1.uid);
			return val != 0 ? val : Integer.compare(e0.fid, e1.fid);
		}
		if (c == 1)
			return e0.toString().compareTo(e1.toString());
		int i0 = (int) get(e0, c);
		int i1 = (int) get(e1, c);
		return Integer.compare(i0, i1);
	}

	@Override
	protected Object get(Form e, int c) {
		Basis b = BasisSet.current();
		MaskUnit du = e.maxu();
		double mul = e.unit.lv.getMult(e.unit.getPrefLv());
		double atk = b.t().getAtkMulti();
		double def = b.t().getDefMulti();
		int itv = du.getItv();
		if (c == 0)
			return e.uid + "-" + e.fid;
		else if (c == 1)
			return e;
		else if (c == 2)
			return e.unit.getPrefLv();
		else if (c == 3)
			return (int) (du.getHp() * mul * def);
		else if (c == 4)
			return du.getHb();
		else if (c == 5)
			return (int) (Math.round(du.allAtk() * mul) * atk);
		else if (c == 6)
			return du.getRange();
		else if (c == 7)
			return du.getSpeed();
		else if (c == 8)
			return (int) (du.allAtk() * mul * atk * 30 / itv);
		else if (c == 9)
			return du.rawAtkData()[0][1];
		else if (c == 10)
			return b.t().getFinRes(du.getRespawn());
		else if (c == 11)
			return e.getDefaultPrice(1);
		else if (c == 12)
			return du.getItv();
		else if (c == 13)
			return du.getWill() + 1;
		return null;
	}

	@Override
	protected String[] getTit() {
		return tit;
	}

}
