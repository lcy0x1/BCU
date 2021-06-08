package page.info.edit;

import common.battle.data.CustomEnemy;
import common.battle.data.CustomEntity;
import common.pack.PackData;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.unit.Enemy;
import org.jcodec.common.tools.MathUtil;
import page.JBTN;
import page.JL;
import page.JTF;
import page.Page;
import page.info.EnemyInfoPage;
import page.info.filter.EnemyEditBox;

import static utilpc.Interpret.EABIIND;
import static utilpc.Interpret.IMUSFT;

public class EnemyEditPage extends EntityEditPage {

	private static final long serialVersionUID = 1L;

	private final JL lbd = new JL(1, "limit");
	private final JL ldr = new JL(1, "drop");
	private final JTF fbd = new JTF();
	private final JTF fdr = new JTF();
	private final JTF fsr = new JTF();
	private final JBTN vene = new JBTN(0, "vene");
	private final JBTN stat = new JBTN(0, "stat");
	private final JBTN impt = new JBTN(0, "import");
	private final JBTN vuni = new JBTN(0, "unit");
	private final JTF[] edesc = new JTF[4];
	private final EnemyEditBox eeb;
	private final Enemy ene;
	private final CustomEnemy ce;
	private String[] eneDesc;

	public EnemyEditPage(Page p, Enemy e, PackData.UserPack pack) {
		super(p, e.id.pack, (CustomEntity) e.de, pack.editable, true);
		ene = e;
		ce = (CustomEnemy) ene.de;
		eeb = new EnemyEditBox(this, pack, ce);
		ini();
		setData((CustomEnemy) e.de);
		resized();
	}

	@Override
	protected void getInput(JTF jtf, int[] v) {
		if (jtf == fbd) {
			ce.limit = v[0];
		}
		if (jtf == fdr) {
			ce.drop = (int) (v[0] / bas.t().getDropMulti());
		}
		if (jtf == fsr) {
			v[0] = MathUtil.clip(v[0], 0, 4);
			ce.star = v[0];
		}
	}

	@Override
	protected void ini() {

		set(lbd);
		set(ldr);
		set(fdr);
		set(fbd);
		set(fsr);
		super.ini();
		add(eeb);
		add(vene);
		add(stat);
		add(impt);
		add(vuni);
		for (int i = 0 ; i < edesc.length ; i++)
			add(edesc[i] = new JTF());

		for (JTF jtf : edesc)
			jtf.setEnabled(editable);

		for (int i = 0; i < edesc.length; i++) {
			int finalI = i;
			edesc[i].setLnr(d -> changeDesc(finalI));
		}

		stat.setLnr(x -> changePanel(new EnemyInfoPage(this, ENode.getList(UserProfile.getAll(ene.id.pack, Enemy.class), ene))));
		subListener(impt, vuni, vene, ene);
	}

	private void changeDesc(int line) {
		String txt = edesc[line].getText().trim();
		if (!txt.equals("Description Line " + (line + 1))) {
			eneDesc[line] = txt;
			if (txt.length() > 63) {
				for (int i = line; i + 1 < edesc.length; i++) {
					if (eneDesc[i].length() > 63) {
						eneDesc[i + 1] = eneDesc[i].substring(63) + eneDesc[i + 1];
						eneDesc[i] = eneDesc[i].substring(0, 63);
					}
				}
				if (eneDesc[edesc.length - 1].length() > 63)
					eneDesc[edesc.length - 1] = eneDesc[edesc.length - 1].substring(0, 63);
			}
		}
		ene.desc = String.join("<br>", eneDesc);
		setData(ce);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(ldr, x, y, 50, 350, 100, 50);
		set(fdr, x, y, 150, 350, 200, 50);
		set(eeb, x, y, 50, 650, 600, 500);
		set(fsr, x, y, 50, 1150, 200, 50);
		if (editable) {
			set(vene, x, y, 650, 800, 200, 50);
			set(stat, x, y, 850, 800, 200, 50);
		} else {
			set(vene, x, y, 650, 750, 200, 50);
			set(stat, x, y, 850, 750, 200, 50);
		}
		set(lbd, x, y, 1400, 1050, 200, 50);
		set(fbd, x, y, 1600, 1050, 200, 50);

		set(impt, x, y, 250, 1150, 200, 50);
		set(vuni, x, y, 450, 1150, 200, 50);
		int h = 1000;
		for (JTF jtf : edesc) {
			set(jtf, x, y, 650, h, 750, 50);
			h += 50;
		}
		eeb.resized();

	}

	@Override
	protected void setData(CustomEntity data) {
		super.setData(data);
		fbd.setText(ce.getLim() + "");
		eneDesc = ene.descriptionGet().split("<br>",4);
		for (int i = 0; i < edesc.length; i++)
			edesc[i].setText("" + (eneDesc[i].length() > 0 ? eneDesc[i] : "Description Line " + (i + 1)));
		fsr.setText("star: " + ce.star);
		fdr.setText("" + (int) (ce.getDrop() * bas.t().getDropMulti()));
		int imu = 0;
		for (int j : EABIIND)
			if (j > 100) {
				int id = j - 100;
				if (ce.getProc().getArr(id).exists())
					imu |= 1 << id - IMUSFT;
			}
		if (ce.getLim() >= 100)
			fbd.setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least "
					+ (ce.getLim() - 100)
					+ " units from the base<br>once it passes that threshold."
					+ "</html>");
		else
			fbd.setToolTipText("<html>"
					+ "This enemy, if it's a boss, will always stay at least "
					+ (100 - ce.getLim())
					+ " units inside the base<br>once it passes that threshold."
					+ "</html>");
		eeb.setData(new int[] { ce.abi, imu }, data.traits);
	}

}
