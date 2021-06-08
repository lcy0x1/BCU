package page.info;

import common.CommonStatic;
import common.battle.BasisSet;
import common.battle.data.CustomEnemy;
import common.battle.data.DataEnemy;
import common.pack.UserProfile;
import common.util.Data;
import common.util.unit.Enemy;
import common.util.unit.Trait;
import page.JL;
import page.JTF;
import page.Page;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EnemyInfoTable extends Page {

	private static final long serialVersionUID = 1L;

	private final JL[][] main = new JL[4][8];
	private final JL[][] special = new JL[1][8];
	private final JL[][] atks;
	private final JLabel[] proc;
	private final JTF jtf = new JTF();
	private final JTextArea descr = new JTextArea();
	private final JScrollPane desc = new JScrollPane(descr);


	private final BasisSet b;
	private final Enemy e;
	private int multi, mulatk;
	private boolean displaySpecial;

	protected EnemyInfoTable(Page p, Enemy de, int mul, int mula) {
		super(p);
		b = BasisSet.current();

		e = de;
		multi = mul;
		mulatk = mula;
		atks = new JL[e.de.rawAtkData().length][8];
		List<Interpret.ProcDisplay> ls = Interpret.getAbi(e.de);
		ls.addAll(Interpret.getProc(e.de, true));
		proc = new JLabel[ls.size()];
		for (int i = 0; i < ls.size(); i++) {
			Interpret.ProcDisplay disp = ls.get(i);
			add(proc[i] = new JLabel(disp.toString()));
			proc[i].setBorder(BorderFactory.createEtchedBorder());
			if (disp.getIcon() != null)
				proc[i].setIcon(disp.getIcon());
		}
		ini();
	}

	protected void reset() {
		double mul = multi * e.de.multi(b) / 100;
		double mula = mulatk * e.de.multi(b) / 100;
		main[1][3].setText("" + (int) (e.de.getHp() * mul));
		main[1][7].setText("" + (int) (e.de.getDrop() * b.t().getDropMulti()));
		main[2][3].setText("" + (int) (e.de.allAtk() * mula * 30 / e.de.getItv()));
		int[][] atkData = e.de.rawAtkData();
		for (int i = 0; i < atks.length; i++)
			atks[i][1].setText("" + Math.round(atkData[i][0] * mula));
	}

	@Override
	protected void resized(int x, int y) {
		for (int i = 0; i < main.length; i++)
			for (int j = 0; j < main[i].length; j++)
				if (i * j != 1 && (i != 0 || j < 4))
					set(main[i][j], x, y, 200 * j, 50 * i, 200, 50);
		set(jtf, x, y, 200, 50, 200, 50);
		set(main[0][4], x, y, 800, 0, 800, 50);
		int h = main.length * 50;
		if (displaySpecial) {
			for (JL[] jls : special) {
				for (int j = 0; j < jls.length; j++)
					set(jls[j], x, y, 200 * j, h, 200, 50);
				h += 50;
			}
		} else {
			for (int i = 0; i < special.length; i++)
				for (int j = 0; j < special[i].length; j++)
					set(special[i][j], x, y, 200 * i, h, 0, 0);
		}
		for (int i = 0; i < atks.length; i++)
			for (int j = 0; j < atks[i].length; j++)
				set(atks[i][j], x, y, 200 * j, h + 50 * i, 200, 50);
		h += atks.length * 50;
		for (int i = 0; i < proc.length; i++)
			set(proc[i], x, y, i % 2 * 800, h + 50 * (i / 2), i % 2 == 0 && i + 1 == proc.length ? 1600 : 800, 50);
		h += proc.length * 25 + (proc.length % 2 == 1 ? 25 : 0);
		set(desc, x, y, 0, h, 1600, 200);
	}

	private void addListeners() {

		jtf.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				int[] data = CommonStatic.parseIntsN(jtf.getText().trim().replace("%", ""));

				if (data.length == 1) {
					if (data[0] != -1) {
						multi = mulatk = data[0];
					}

					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				} else if (data.length == 2) {
					if (data[0] != -1) {
						multi = data[0];
					}

					if (data[1] != -1) {
						mulatk = data[1];
					}

					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				} else {
					jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
				}

				reset();
			}

		});
	}

	protected int getH() {
		int l = main.length + atks.length;
		if (displaySpecial)
			l += special.length;
		return (l + (proc.length + (proc.length % 2 == 1 ? 1 : 0)) / 2) * 50 + (e.descriptionGet().replace("<br>", "").length() > 0 ? 200 : 0);
	}

	private void ini() {
		ArrayList<Trait> trs = e.de.getTraits();
		trs.sort(Comparator.comparingInt(t -> t.id.id));
		trs.sort(Comparator.comparing(t -> t.id.pack));
		trs.sort(Comparator.comparing(t -> !t.BCTrait));
		String[] TraitBox = new String[trs.size()];
		for (int i = 0; i < trs.size(); i++) {
			Trait trait = e.de.getTraits().get(i);
			if (trait.BCTrait)
				TraitBox[i] = Interpret.TRAIT[trait.id.id];
			else
				TraitBox[i] = trait.name;
		}
		for (int i = 0; i < main.length; i++)
			for (int j = 0; j < main[i].length; j++)
				if (i * j != 1 && (i != 0 || j < 5)) {
					add(main[i][j] = new JL());
					main[i][j].setBorder(BorderFactory.createEtchedBorder());
					if (i != 0 && j % 2 == 0 || i == 0 && j < 4)
						main[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				}
		for (int i = 0; i < special.length; i++) {
			for (int j = 0; j < special[i].length; j++) {
				add(special[i][j] = new JL());
				special[i][j].setBorder(BorderFactory.createEtchedBorder());
				if (j % 2 == 0)
					special[i][j].setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
		for (int i = 0; i < atks.length; i++)
			for (int j = 0; j < atks[i].length; j++) {
				add(atks[i][j] = new JL());
				atks[i][j].setBorder(BorderFactory.createEtchedBorder());
				if (j % 2 == 0)
					atks[i][j].setHorizontalAlignment(SwingConstants.CENTER);
			}
		add(jtf);
		jtf.setText(CommonStatic.toArrayFormat(multi, mulatk) + "%");
		int itv = e.de.getItv();
		main[0][0].setText("ID");
		main[0][1].setText("" + e.id);
		if (e.anim.getEdi() != null && e.anim.getEdi().getImg() != null)
			main[0][2].setIcon(UtilPC.getIcon(e.anim.getEdi()));
		main[0][3].setText(1, "trait");
		main[0][4].setText(Interpret.getTrait(TraitBox, e.de.getStar()));
		main[1][0].setText(1, "mult");
		main[1][2].setText(1, "HP");
		main[1][4].setText(1, "HB");
		main[1][5].setText("" + e.de.getHb());
		main[1][6].setText(1, "drop");
		main[1][7].setText("" + (int) (e.de.getDrop() * b.t().getDropMulti()));
		main[2][0].setText(1, "range");
		main[2][1].setText("" + e.de.getRange());
		main[2][2].setText("dps");
		main[2][4].setText(1, "speed");
		main[2][5].setText("" + e.de.getSpeed());
		main[2][6].setText(1, "atkf");
		main[2][7].setText(itv + "f");
		main[3][0].setText(1, "atktype");
		if (e.de.isRange()) {
			main[3][1].setText(1, "isr");
			main[3][1].setIcon(UtilPC.createIcon(2, Data.ATK_AREA));
		} else {
			main[3][1].setText(1, "single");
			main[3][1].setIcon(UtilPC.createIcon(2, Data.ATK_SINGLE));
		}
		main[3][2].setText(1, "shield");
		main[3][3].setText("" + e.de.getShield());
		main[3][4].setText(1, "TBA");
		main[3][5].setText(e.de.getTBA() + "f");
		main[3][6].setText(1, "postaa");
		special[0][0].setText(1, "count");
		special[0][1].setText(e.de.getAtkLoop() < 0 ? "infinite" : e.de.getAtkLoop() + "");
		special[0][2].setText(1, "width");
		special[0][3].setText(e.de.getWidth() + "");
		special[0][4].setText(1, "limit");
		special[0][5].setText(e.de.getLim() + "");
		special[0][6].setText(1, "will");
		special[0][7].setText("" + (e.de.getWill() + 1));
		int[][] atkData = e.de.rawAtkData();
		for (int i = 0; i < atks.length; i++) {
			atks[i][0].setText(1, "atk");
			atks[i][2].setText(1, "preaa");
			atks[i][3].setText(atkData[i][1] + "f");
			atks[i][4].setText(0, atkData[i][3] == -1 ? "igtr" : "cntr");
			atks[i][5].setText("" + (!(e.de instanceof DataEnemy) && ((CustomEnemy)e.de).atks[i].specialTrait));
			atks[i][6].setText(1, "dire");
			atks[i][7].setText("" + atkData[i][3]);
			itv -= atkData[i][1];
		}
		main[3][7].setText(e.de.getPost() + "f");
		if (e.de.getLim() >= 100)
			special[0][5].setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least"
					+ (e.de.getLim() - 100)
					+ " units from the base<br>once it passes that threshold."
					+ "</html>");
		else
			special[0][5].setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least"
					+ (100 - e.de.getLim())
					+ " units inside the base<br>once it passes that threshold."
					+ "</html>");
		String eDesc = e.descriptionGet().replace("<br>", "\n");
		if (eDesc.replace("\n","").length() > 0)
			add(desc);
		descr.setText(e.toString().replace(Data.trio(e.id.id) + " - ", "") + (e.de.getTraits().size() > 0 && !e.de.getTraits().contains(UserProfile.getBCData().traits.get(Data.TRAIT_WHITE)) ? " (" + Interpret.getTrait(TraitBox, 0) + ")" : "") + (e.de.getStar() >= 2 ? " (Cool Dude)" : "") + "\n" + eDesc);
		descr.setEditable(false);
		reset();
		addListeners();
		for (JLabel jl : proc) {
			String str = jl.getText();
			StringBuilder sb = new StringBuilder();
			FontMetrics fm = jl.getFontMetrics(jl.getFont());
			while (fm.stringWidth(str) >= 400) {
				int i = 1;
				String wrapped = str.substring(0, i);
				while (fm.stringWidth(wrapped) < 400)
					wrapped = str.substring(0, i++);

				int maximum;
				if (CommonStatic.getConfig().lang == 3)
					maximum = Math.max(wrapped.lastIndexOf("。"),wrapped.lastIndexOf("、"));
				else
					maximum = Math.max(Math.max(wrapped.lastIndexOf(" "), wrapped.lastIndexOf(".")), wrapped.lastIndexOf(","));

				if (maximum <= 0)
					maximum = Math.min(i,wrapped.length());

				wrapped = wrapped.substring(0, maximum);
				sb.append(wrapped).append("<br>");
				str = str.substring(wrapped.length());
			}
			sb.append(str);
			jl.setToolTipText("<html>" + sb.toString() + "</html>");
		}
	}

	public void setDisplaySpecial(boolean displaySpecial) {
		this.displaySpecial = displaySpecial;
	}
}
