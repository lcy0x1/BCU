package page.battle;

import common.util.stage.Replay;
import common.util.stage.Stage;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.Page;
import page.support.ReorderList;
import page.support.ReorderListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.List;

public class StRecdPage extends AbRecdPage {

	private static final long serialVersionUID = 1L;

	private final ReorderList<Replay> list = new ReorderList<>();
	private final JScrollPane jsp = new JScrollPane(list);
	private final JBTN addr = new JBTN(0, "add");
	private final JBTN remr = new JBTN(0, "rem");
	private final JTF jtf = new JTF();

	protected final Stage st;

	private RecdManagePage rmp;

	public StRecdPage(Page p, Stage stage, boolean edit) {
		super(p, edit);
		st = stage;
		preini();
		ini();
		resized();
	}

	@Override
	public Replay getSelection() {
		return list.getSelectedValue();
	}

	@Override
	protected void renew() {
		super.renew();
		if (rmp != null) {
			Replay recd = rmp.getSelection();
			if (recd != null && recd.st != null) {
				if (recd.st.get() == st || Opts.conf("stage mismatch. Do you really want to add?")) {
					st.recd.add(recd);
					recd.localize(st.getCont().getCont().getSID());
					setList();
				}
			}
		}
		rmp = null;
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jsp, x, y, 50, 100, 500, 1100);
		set(addr, x, y, 600, 400, 300, 50);
		set(remr, x, y, 950, 400, 300, 50);
		set(jtf, x, y, 600, 500, 300, 50);
	}

	@Override
	protected void setList() {
		change(true);
		Replay r = list.getSelectedValue();
		list.setListData(st.recd.toArray(new Replay[0]));
		if (st.recd.contains(r))
			list.setSelectedValue(r, true);
		setRecd(list.getSelectedValue());
		addr.setEnabled(editable);
		change(false);
	}

	@Override
	protected void setRecd(Replay r) {
		super.setRecd(r);
		remr.setEnabled(editable && r != null);
		jtf.setEditable(editable && r != null);
		jtf.setText(r == null ? "" : r.rl.toString());
	}

	private void addListeners() {

		addr.setLnr(e -> changePanel(rmp = new RecdManagePage(this)));

		remr.setLnr(e -> {
			Replay recd = list.getSelectedValue();
			st.recd.remove(recd);
			setList();
		});

		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (isAdj() || list.getValueIsAdjusting())
					return;
				setRecd(list.getSelectedValue());
			}

		});

		list.list = new ReorderListener<Replay>() {

			@Override
			public void reordered(int ori, int fin) {
				change(false);
				List<Replay> l = st.recd;
				Replay sta = l.remove(ori);
				l.add(fin, sta);
			}

			@Override
			public void reordering() {
				change(true);
			}

		};

		jtf.setLnr(x -> {
			Replay r = list.getSelectedValue();
			if (isAdj() || r == null)
				return;
			r.rename(jtf.getText());
		});

	}

	private void ini() {
		add(jsp);
		add(addr);
		add(remr);
		add(jtf);
		setList();
		addListeners();
	}

}
