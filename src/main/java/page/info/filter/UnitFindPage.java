package page.info.filter;

import common.util.unit.Form;
import common.util.unit.Unit;
import page.*;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.List;

public class UnitFindPage extends Page implements SupPage<Unit> {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");
	private final JTG show = new JTG(0, "showf");
	private final UnitListTable ult = new UnitListTable(this);
	private final JScrollPane jsp = new JScrollPane(ult);
	private final UnitFilterBox ufb;
	private final JTF seatf = new JTF();
	private final JBTN seabt = new JBTN(0, "search");

	public UnitFindPage(Page p) {
		super(p);

		ufb = UnitFilterBox.getNew(this);
		ini();
		resized();
	}

	public UnitFindPage(Page p, String pack, List<String> parents) {
		super(p);

		ufb = UnitFilterBox.getNew(this, pack, parents);
		ini();
		resized();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void callBack(Object o) {
		ult.setList((List<Form>) o);
		resized();
	}

	public Form getForm() {
		if (ult.getSelectedRow() == -1)
			return null;
		return ult.list.get(ult.getSelectedRow());
	}

	public List<Form> getList() {
		return ult.list;
	}

	@Override
	public Unit getSelected() {
		Form f = getForm();
		return f == null ? null : f.unit;
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() != ult)
			return;
		ult.clicked(e.getPoint());
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(show, x, y, 250, 0, 200, 50);
		set(seatf, x, y, 550, 0, 1000, 50);
		set(seabt, x, y, 1600, 0, 200, 50);
		if (show.isSelected()) {
			int[] siz = ufb.getSizer();
			set(ufb, x, y, 50, 100, siz[0], siz[1]);
			int mx = 0, my = 0;
			if (siz[2] == 0)
				mx = siz[3];
			else
				my = siz[3];
			set(jsp, x, y, 50 + mx, 100 + my, 2200 - mx, 1150 - my);
		} else
			set(jsp, x, y, 50, 100, 2200, 1150);
		ult.setRowHeight(size(x, y, 50));
	}

	private void addListeners() {
		back.addActionListener(arg0 -> changePanel(getFront()));

		show.addActionListener(arg0 -> {
			if (show.isSelected())
				add(ufb);
			else
				remove(ufb);
		});

		seabt.setLnr((b) -> {
			if (ufb != null) {
				ufb.name = seatf.getText();

				ufb.callBack(null);
			}
		});

		seatf.addActionListener(e -> {
			if (ufb != null) {
				ufb.name = seatf.getText();

				ufb.callBack(null);
			}
		});
	}

	private void ini() {
		add(back);
		add(show);
		add(ufb);
		add(jsp);
		add(seatf);
		add(seabt);
		show.setSelected(true);
		addListeners();
	}

}
