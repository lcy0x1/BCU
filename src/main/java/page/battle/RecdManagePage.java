package page.battle;

import common.CommonStatic;
import common.pack.Source;
import common.util.stage.Replay;
import main.MainBCU;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.Page;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class RecdManagePage extends AbRecdPage {

	private static final long serialVersionUID = 1L;

	private final JBTN dele = new JBTN(0, "rem");
	private final JTF rena = new JTF();
	private final JList<Replay> jlr = new JList<>();
	private final JScrollPane jspr = new JScrollPane(jlr);

	public RecdManagePage(Page p) {
		super(p, true);
		preini();
		ini();
		resized();
	}

	@Override
	public Replay getSelection() {
		return jlr.getSelectedValue();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspr, x, y, 50, 100, 500, 1100);
		set(dele, x, y, 600, 400, 300, 50);
		set(rena, x, y, 600, 500, 300, 50);
	}

	@Override
	protected void setList() {
		change(true);
		Replay r = jlr.getSelectedValue();
		jlr.setListData(Replay.getMap().values().toArray(new Replay[0]));
		jlr.setSelectedValue(r, true);
		setRecd(r);
		change(false);
	}

	@Override
	protected void setRecd(Replay r) {
		try {
			int check = r == null || r.st == null ? 0 : r.st.get().health;
		} catch (Exception e) {
			Opts.pop("Please change the stage to a new one","Replay stage not found");
			r.st = null;
			r.marked = false;
		}
		super.setRecd(r);
		dele.setEnabled(r != null);
		rena.setEditable(r != null);
		rena.setText(r == null ? "" : r.rl == null ? "" : r.rl.id);
	}

	private void addListeners() {

		jlr.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (isAdj() || jlr.getValueIsAdjusting())
					return;
				setRecd(jlr.getSelectedValue());
			}

		});

		rena.setLnr(x -> {
			if (isAdj() || jlr.getValueIsAdjusting())
				return;
			Replay r = jlr.getSelectedValue();
			if (r == null)
				return;
			r.rename(MainBCU.validate(rena.getText().trim(),'#'));
			rena.setText(r.rl.id);
		});

		dele.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Replay r = jlr.getSelectedValue();
				File f = CommonStatic.ctx.getWorkspaceFile(r.rl.getPath(Source.REPLAY) + ".replay");
				if (f.exists())
					f.delete();
				if (!f.exists()) {
					Replay.getMap().remove(r.rl.id);
					setList();
				}
				setRecd(null);
			}
		});

	}

	private void ini() {
		add(jspr);
		add(dele);
		add(rena);
		addListeners();
	}

}
