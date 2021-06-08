package page.info;

import common.battle.BasisSet;
import common.system.Node;
import common.util.unit.Unit;
import page.JBTN;
import page.JTG;
import page.Page;
import page.basis.BasisPage;
import page.view.UnitViewPage;

import javax.swing.*;

public class UnitInfoPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");
	private final JBTN anim = new JBTN(0, "anim");
	private final JBTN prev = new JBTN(0, "prev");
	private final JBTN next = new JBTN(0, "next");
	private final JBTN find = new JBTN(0, "combo");
	private final JTG extr = new JTG(0, "extra");
	private final JPanel cont = new JPanel();
	private final JScrollPane jsp = new JScrollPane(cont);
	private final UnitInfoTable[] info;
	private final TreaTable trea;

	private final BasisSet b;
	private final Node<Unit> n;

	public UnitInfoPage(Page p, Node<Unit> de) {
		this(p, de, BasisSet.current(), false);
	}

	private UnitInfoPage(Page p, Node<Unit> de, BasisSet bas, boolean sp) {
		super(p);
		n = de;
		b = bas;

		info = new UnitInfoTable[n.val.forms.length];
		for (int i = 0; i < info.length; i++)
			info[i] = new UnitInfoTable(this, n.val.forms[i], sp);
		trea = new TreaTable(this);
		extr.setSelected(sp);
		ini();
		resized();
	}

	@Override
	public void callBack(Object newParam) {
		for (UnitInfoTable uit : info)
			uit.reset();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(prev, x, y, 300, 0, 200, 50);
		set(anim, x, y, 600, 0, 200, 50);
		set(next, x, y, 900, 0, 200, 50);
		set(find, x, y, 1200, 0, 200, 50);
		set(extr, x, y, 1500, 0, 200, 50);
		int h = 0;
		set(jsp, x, y, 50, 100, 1650, 1150);
		for (int i = 0; i < info.length; i++) {
			int ih = info[i].getH();
			set(info[i], x, y, 0, h, 1600, ih);
			info[i].resized();
			h += ih + (n.val.forms[i].descriptionGet().replace("<br>", "").length() == 0 ? 50 : 0);
		}
		cont.setPreferredSize(size(x, y, 1600, h).toDimension());
		jsp.getVerticalScrollBar().setUnitIncrement(size(x, y, 50));
		jsp.revalidate();
		set(trea, x, y, 1750, 100, 400, 1200);
	}

	private void addListeners() {
		back.addActionListener(arg0 -> changePanel(getFront()));

		prev.addActionListener(arg0 -> changePanel(new UnitInfoPage(getFront(), n.prev, b, extr.isSelected())));

		anim.addActionListener(arg0 -> {
			if (getFront() instanceof UnitViewPage)
				changePanel(getFront());
			else
				changePanel(new UnitViewPage(getThis(), n.val));
		});

		next.addActionListener(arg0 -> changePanel(new UnitInfoPage(getFront(), n.next, b, extr.isSelected())));

		find.addActionListener(arg0 -> {
			if (getFront() instanceof BasisPage) {
				changePanel(getFront());
				getFront().callBack(n.val);
				return;
			}
			Page p;
			changePanel(p = new BasisPage(getThis()));
			p.callBack(n.val);
		});

		extr.addActionListener(arg0 -> {
			for (UnitInfoTable inf : info) {
				inf.setDisplaySpecial(extr.isSelected());
			}
		});
	}

	private void ini() {
		add(back);
		add(prev);
		add(anim);
		add(next);
		add(find);
		add(extr);
		for (int i = 0; i < info.length; i++)
			cont.add(info[i]);
		cont.setLayout(null);
		add(jsp);
		add(trea);
		prev.setEnabled(n.prev != null);
		next.setEnabled(n.next != null);
		addListeners();
	}

}
