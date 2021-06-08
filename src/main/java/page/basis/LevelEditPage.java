package page.basis;

import common.CommonStatic;
import common.battle.BasisSet;
import common.battle.LineUp;
import common.util.Data;
import common.util.unit.Form;
import common.util.unit.Level;
import main.Opts;
import page.JBTN;
import page.JTF;
import page.MainLocale;
import page.Page;
import utilpc.Interpret;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LevelEditPage extends Page {

	private static final long serialVersionUID = 1L;

	private final Page p;

	private final Form f;
	private final Level lv;
	private final List<int[]> orbs = new ArrayList<>();

	private final JBTN bck = new JBTN(0, "back");
	private final JLabel pcoin = new JLabel();
	private final JTF levels = new JTF();
	private final JList<String> orbList = new JList<>();
	private final JScrollPane orbScroll = new JScrollPane(orbList);
	private final JBTN add = new JBTN(0, "add");
	private final JBTN rem = new JBTN(0, "rem");
	private final JBTN clear = new JBTN(0, "clear");
	private final OrbBox orbb = new OrbBox(new int[] {});
	private final JComboBox<String> type = new JComboBox<>();
	private final JComboBox<String> trait = new JComboBox<>();
	private final JComboBox<String> grade = new JComboBox<>();

	private List<Integer> traitData = new ArrayList<>();
	private List<Integer> gradeData = new ArrayList<>();

	private boolean updating = false;

	protected LevelEditPage(Page p, Level lv, Form f) {
		super(p);
		this.p = p;
		this.lv = lv;
		this.f = f;

		if (f.orbs != null) {
			if (lv.getOrbs() == null) {
				if (f.orbs.getSlots() != -1) {
					for (int i = 0; i < f.orbs.getSlots(); i++) {
						orbs.add(new int[] {});
					}
				}
			} else {
				orbs.addAll(Arrays.asList(lv.getOrbs()));
			}
		}

		ini();
		resized();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(bck, x, y, 0, 0, 200, 50);
		set(pcoin, x, y, 50, 100, 600, 50);
		set(levels, x, y, 50, 150, 350, 50);
		set(orbScroll, x, y, 50, 225, 350, 600);
		set(add, x, y, 50, 875, 175, 50);
		set(rem, x, y, 225, 875, 175, 50);
		set(orbb, x, y, 450, 425, 200, 200);
		set(type, x, y, 700, 425, 200, 50);
		set(trait, x, y, 700, 500, 200, 50);
		set(grade, x, y, 700, 575, 200, 50);
		set(clear, x, y, 50, 975, 350, 50);
	}

	@Override
	protected void timer(int t) {
		orbb.paint(orbb.getGraphics());
		super.timer(t);
	}

	private void addListeners() {
		bck.setLnr(x -> changePanel(getFront()));

		levels.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				int[] lvs = CommonStatic.parseIntsN(levels.getText());

				setLvOrb(lvs, generateOrb());
			}
		});

		orbList.addListSelectionListener(e -> {
			if (updating) {
				return;
			}

			rem.setEnabled(valid());
			type.setEnabled(valid());
			trait.setEnabled(valid());
			grade.setEnabled(valid());

			if (orbList.getSelectedIndex() != -1) {
				orbb.changeOrb(orbs.get(orbList.getSelectedIndex()));

				initializeDrops(orbs.get(orbList.getSelectedIndex()));
			} else {
				orbb.changeOrb(new int[] {});
			}
		});

		rem.setLnr(x -> {
			int index = orbList.getSelectedIndex();

			if (index != -1 && index < orbs.size()) {
				orbs.remove(index);
			}

			orbList.setListData(generateNames());

			setLvOrb(lv.getLvs(), generateOrb());
		});

		add.setLnr(x -> {
			int[] data = { 0, CommonStatic.getBCAssets().DATA.get(0), 0 };

			orbs.add(data);

			orbList.setListData(generateNames());

			setLvOrb(lv.getLvs(), generateOrb());
		});

		type.addActionListener(arg0 -> {
			if (updating) {
				return;
			}

			if (orbList.getSelectedIndex() != -1 && orbList.getSelectedIndex() < orbs.size()) {
				int[] data = orbs.get(orbList.getSelectedIndex());

				if (f.orbs != null && f.orbs.getSlots() != -1) {
					if (type.getSelectedIndex() == 0) {
						data = new int[] {};
					} else {
						if (data.length == 0) {
							data = new int[] { 0, 0, 0 };
						}

						data[0] = type.getSelectedIndex() - 1;
					}
				} else {
					data[0] = type.getSelectedIndex();
				}

				orbs.set(orbList.getSelectedIndex(), data);

				initializeDrops(data);

				orbb.changeOrb(data);

				setLvOrb(lv.getLvs(), generateOrb());
			}
		});

		trait.addActionListener(arg0 -> {
			if (updating) {
				return;
			}

			if (orbList.getSelectedIndex() != -1 && orbList.getSelectedIndex() < orbs.size()) {
				int[] data = orbs.get(orbList.getSelectedIndex());

				data[1] = traitData.get(trait.getSelectedIndex());

				orbs.set(orbList.getSelectedIndex(), data);

				initializeDrops(data);

				orbb.changeOrb(data);

				setLvOrb(lv.getLvs(), generateOrb());
			}
		});

		grade.addActionListener(arg0 -> {
			if (updating) {
				return;
			}

			if (orbList.getSelectedIndex() != -1 && orbList.getSelectedIndex() < orbs.size()) {
				int[] data = orbs.get(orbList.getSelectedIndex());

				data[2] = gradeData.get(grade.getSelectedIndex());

				orbs.set(orbList.getSelectedIndex(), data);

				initializeDrops(data);

				orbb.changeOrb(data);

				setLvOrb(lv.getLvs(), generateOrb());
			}
		});

		clear.setLnr(x -> {
			if (!Opts.conf())
				return;

			if (f.orbs == null)
				return;

			if (f.orbs.getSlots() != -1) {
				for (int i = 0; i < orbs.size(); i++) {
					orbs.set(i, new int[] {});
				}
			} else {
				orbs.clear();
			}

			orbb.changeOrb(new int[] {});
			setLvOrb(lv.getLvs(), generateOrb());
			orbList.setListData(generateNames());
		});
	}

	private String[] generateNames() {
		String[] res = new String[orbs.size()];

		for (int i = 0; i < orbs.size(); i++) {
			int[] o = orbs.get(i);

			if (o.length != 0) {
				res[i] = "Orb" + (i + 1) + " - {" + getType(o[0]) + ", " + getTrait(o[1]) + ", " + getGrade(o[2]) + "}";
			} else {
				res[i] = "Orb" + (i + 1) + " - None";
			}
		}

		return res;
	}

	private int[][] generateOrb() {
		if (orbs.isEmpty()) {
			return null;
		}

		int[][] data = new int[orbs.size()][];

		for (int i = 0; i < data.length; i++) {
			data[i] = orbs.get(i);
		}

		return data;
	}

	private String getGrade(int grade) {
		switch (grade) {
		case 0:
			return "D";
		case 1:
			return "C";
		case 2:
			return "B";
		case 3:
			return "A";
		case 4:
			return "S";
		default:
			return "Unknown Grade " + grade;
		}
	}

	private String getTrait(int trait) {
		StringBuilder res = new StringBuilder();

		for (int i = 0; i < Interpret.TRAIT.length; i++) {
			if (((trait >> i) & 1) > 0) {
				res.append(Interpret.TRAIT[i]).append("/ ");
			}
		}

		if (res.toString().endsWith("/ "))
			res = new StringBuilder(res.substring(0, res.length() - 2));

		return res.toString();
	}

	private String getType(int type) {
		if (type == 0) {
			return MainLocale.getLoc(MainLocale.UTIL, "ot0");
		} else if (type == 1) {
			return MainLocale.getLoc(MainLocale.UTIL, "ot1");
		} else {
			return "Unknown Type " + type;
		}
	}

	private void ini() {
		add(bck);
		add(pcoin);
		add(levels);
		add(orbb);

		if (f.orbs != null) {
			add(orbScroll);
			add(type);
			add(trait);
			add(grade);

			if (f.orbs.getSlots() == -1) {
				add(add);
				add(rem);
			}
		}

		add(clear);

		String[] strs = UtilPC.lvText(f, lu().getLv(f).getLvs());

		levels.setText(strs[0]);
		pcoin.setText(strs[1]);

		addListeners();

		orbList.setListData(generateNames());
		orbList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rem.setEnabled(valid());
		type.setEnabled(valid());
		trait.setEnabled(valid());
		grade.setEnabled(valid());
	}

	private void initializeDrops(int[] data) {
		updating = true;

		if (f.orbs == null) {
			return;
		}

		String[] types;

		if (f.orbs.getSlots() == -1) {
			types = new String[] { MainLocale.getLoc(MainLocale.UTIL, "ot0"), MainLocale.getLoc(MainLocale.UTIL, "ot1") };
		} else {
			types = new String[] { "None", MainLocale.getLoc(MainLocale.UTIL, "ot0"), MainLocale.getLoc(MainLocale.UTIL, "ot1") };
		}

		if (f.orbs.getSlots() != -1 && data.length == 0) {
			type.setModel(new DefaultComboBoxModel<>(types));

			type.setSelectedIndex(0);

			trait.setEnabled(false);
			grade.setEnabled(false);

			if (valid()) {
				int index = orbList.getSelectedIndex();

				orbList.setListData(generateNames());

				orbList.setSelectedIndex(index);
			}

			updating = false;

			return;
		}

		trait.setEnabled(true);
		grade.setEnabled(true);

		String[] traits;
		String[] grades;

		if (data[0] == Data.ORB_ATK) {
			traitData = new ArrayList<>(CommonStatic.getBCAssets().ATKORB.keySet());

			traits = new String[traitData.size()];

			for (int i = 0; i < traits.length; i++) {
				traits[i] = getTrait(traitData.get(i));
			}

			if (!traitData.contains(data[1])) {
				data[1] = traitData.get(0);
			}

			gradeData = CommonStatic.getBCAssets().ATKORB.get(data[1]);

			grades = new String[gradeData.size()];

			for (int i = 0; i < grades.length; i++) {
				grades[i] = getGrade(gradeData.get(i));
			}

		} else {
			traitData = new ArrayList<>(CommonStatic.getBCAssets().RESORB.keySet());

			traits = new String[traitData.size()];

			for (int i = 0; i < traits.length; i++) {
				traits[i] = getTrait(traitData.get(i));
			}

			if (!traitData.contains(data[1])) {
				data[1] = traitData.get(0);
			}

			gradeData = CommonStatic.getBCAssets().RESORB.get(data[1]);

			grades = new String[gradeData.size()];

			for (int i = 0; i < grades.length; i++) {
				grades[i] = getGrade(gradeData.get(i));
			}
		}

		if (!gradeData.contains(data[2])) {
			data[2] = gradeData.get(2);
		}

		type.setModel(new DefaultComboBoxModel<>(types));
		trait.setModel(new DefaultComboBoxModel<>(traits));
		grade.setModel(new DefaultComboBoxModel<>(grades));

		if (f.orbs.getSlots() != -1) {
			type.setSelectedIndex(data[0] + 1);
		} else {
			type.setSelectedIndex(data[0]);
		}

		trait.setSelectedIndex(traitData.indexOf(data[1]));

		grade.setSelectedIndex(gradeData.indexOf(data[2]));

		if (valid()) {
			int index = orbList.getSelectedIndex();

			orbs.set(index, data);

			orbList.setListData(generateNames());

			orbList.setSelectedIndex(index);
		}

		updating = false;
	}

	private LineUp lu() {
		return BasisSet.current().sele.lu;
	}

	private void setLvOrb(int[] lvs, int[][] orbs) {
		lu().setOrb(f.unit, lvs, orbs);
		p.callBack(null);
	}

	private boolean valid() {
		return orbList.getSelectedIndex() != -1 && orbs.size() != 0;
	}
}
