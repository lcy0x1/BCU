package page.info.edit;

import common.util.Data;
import common.util.Data.Proc;
import common.util.lang.Formatter;
import page.JTF;
import page.Page;
import page.support.ListJtfPolicy;

import java.awt.*;

public abstract class ProcTable extends Page {

	static class AtkProcTable extends ProcTable {

		private static final long serialVersionUID = 1L;

		private static final int SEC = 16;
		private static final int[] INDS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 20, 27, 29, 31, 32, 21, 22, 23, 24, 25,
				26, 35, 36, 37 };

		protected AtkProcTable(Page p, boolean edit, boolean unit) {
			super(p, INDS, edit, unit);
		}

		@Override
		protected void resized(int x, int y) {
			int h = 0;
			for (int i = 0; i < group.length; i++) {
				int c = i < SEC ? 0 : 400;
				set(group[i].jlm, x, y, c, h, 350, 50);
				h += 50;
				for (int j = 0; j < group[i].list.length; j++) {
					SwingEditor se = (SwingEditor) group[i].list[j];
					se.resize(x, y, c, h, 350, 50);
					h += 50;
				}
				if (i == SEC - 1)
					h = 0;
			}
		}

	}

	static class MainProcTable extends ProcTable {

		private static final long serialVersionUID = 1L;

		private static final int[] INDS = { Data.P_STRONG, Data.P_LETHAL, Data.P_BURROW, Data.P_REVIVE, Data.P_CRITI,
				Data.P_IMUATK, Data.P_DMGCUT, Data.P_DMGCAP, Data.P_IMUKB, Data.P_IMUSTOP,
				Data.P_IMUSLOW, Data.P_IMUWAVE, Data.P_IMUWEAK, Data.P_IMUWARP, Data.P_IMUCURSE,
				Data.P_IMUSEAL, Data.P_IMUMOVING, Data.P_IMUPOI,
				Data.P_IMUPOIATK, Data.P_IMUVOLC, Data.P_IMUSUMMON
		};

		protected MainProcTable(Page p, boolean edit, boolean unit) {
			super(p, INDS, edit, unit);
		}

		@Override
		protected void resized(int x, int y) {
			int h = 0;
			for (int i = 0; i < INDS.length; i++) {
				set(group[i].jlm, x, y, 0, h, 300, 50);
				h += 50;
				for (int j = 0; j < group[i].list.length; j++) {
					SwingEditor se = (SwingEditor) group[i].list[j];
					se.resize(x, y, 0, h, 300, 50);
					h += 50;
				}
			}
			setPreferredSize(size(x, y, 300, h).toDimension());
		}

	}

	private static final long serialVersionUID = 1L;

	protected final int[] inds;
	protected final ListJtfPolicy ljp = new ListJtfPolicy();
	protected final SwingEditor.SwingEG[] group;
	private final boolean editable, isUnit;

	private Proc proc;

	protected ProcTable(Page p, int[] ind, boolean edit, boolean unit) {
		super(p);
		editable = edit;
		isUnit = unit;
		inds = ind;
		group = new SwingEditor.SwingEG[ind.length];
		ini();
	}

	@Override
	public Component add(Component comp) {
		Component ret = super.add(comp);
		if (comp instanceof JTF)
			ljp.add((JTF) comp);
		return ret;
	}

	protected void setData(Proc ints) {
		proc = ints;
		for (int i = 0; i < inds.length; i++)
			group[i].setData(proc.getArr(inds[i]));
	}

	private void ini() {
		Formatter.Context ctx = new Formatter.Context(!isUnit, false);
		for (int i = 0; i < group.length; i++) {
			group[i] = new SwingEditor.SwingEG(inds[i], editable, () -> getFront().callBack(null), ctx);
			add(group[i].jlm);
			for (int j = 0; j < group[i].list.length; j++) {
				SwingEditor se = (SwingEditor) group[i].list[j];
				se.add(this::add);
			}
		}
		setFocusTraversalPolicy(ljp);
		setFocusCycleRoot(true);
	}

}
