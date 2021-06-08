package page.info.edit;

import common.battle.data.CustomEntity;
import common.battle.data.CustomUnit;
import common.pack.Identifier;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.system.Node;
import common.util.unit.Form;
import common.util.unit.Unit;
import page.JBTN;
import page.JL;
import page.JTF;
import page.Page;
import page.info.UnitInfoPage;
import page.info.filter.UnitEditBox;

import static utilpc.Interpret.EABIIND;
import static utilpc.Interpret.IMUSFT;

public class FormEditPage extends EntityEditPage {

	private static final long serialVersionUID = 1L;

	private final JL llv = new JL(1, "Lv");
	private final JL ldr = new JL(1, "price");
	private final JL lrs = new JL(1, "cdo");
	private final JL llr = new JL(1, "t7");
	private final JTF fdr = new JTF();
	private final JTF flv = new JTF();
	private final JTF frs = new JTF();
	private final JTF flr = new JTF();
	private final JBTN vuni = new JBTN(0, "vuni");
	private final JBTN stat = new JBTN(0, "stat");
	private final JBTN impt = new JBTN(0, "import");
	private final JBTN vene = new JBTN(0, "enemy");
	private final JBTN pcoin = new JBTN(0, "pcoin");
	private final JTF[] fdesc = new JTF[4];
	private final UnitEditBox ueb;
	private final Form form;
	private final CustomUnit cu;
	private int lv;
	private String[] uniDesc;

	public FormEditPage(Page p, UserPack pac, Form f) {
		super(p, pac.desc.id, (CustomEntity) f.du, pac.editable, false);
		form = f;
		cu = (CustomUnit) form.du;
		lv = f.unit.getPrefLv();
		ueb = new UnitEditBox(this, pac, cu);
		ini();
		setData((CustomUnit) f.du);
		resized();
	}

	@Override
	protected double getAtk() {
		return bas.t().getAtkMulti();
	}

	@Override
	protected double getLvAtk() {
		return form.unit.lv.getMult(lv);
	}

	@Override
	protected double getDef() {
		double mul = form.unit.lv.getMult(lv);
		double def = bas.t().getDefMulti();
		return mul * def;
	}

	@Override
	protected void getInput(JTF jtf, int[] v) {
		if (jtf == fdr)
			cu.price = (int) (v[0] / 1.5);
		if (jtf == flv) {
			if (v[0] <= 0)
				v[0] = 1;
			lv = v[0];
		}
		if (jtf == frs) {
			if (v[0] <= 60)
				v[0] = 60;
			cu.resp = bas.t().getRevRes(v[0]);
		}
		if (jtf == flr) {
			try {
				if (v.length == 1) {
					int firstLayer = v[0];
					cu.back = cu.front = firstLayer;
				} else if (v.length >= 2) {
					int firstLayer = v[0];
					int secondLayer = v[1];
					if (firstLayer == secondLayer) {
						cu.back = cu.front = firstLayer;
					} else if (firstLayer < secondLayer) {
						cu.back = firstLayer;
						cu.front = secondLayer;
					} else {
						cu.front = firstLayer;
						cu.back = secondLayer;
					}
				}

				flr.setText(interpretLayer(cu.back, cu.front));
			} catch (Exception ignored) { }
		}
	}

	@Override
	protected void ini() {
		llr.setToolTipText("<html>set possible layers of which units can spawn on.</html>");

		set(ldr);
		set(llv);
		set(lrs);
		set(llr);

		set(flv);

		set(fdr);
		set(frs);
		super.ini();

		set(flr);

		add(ueb);

		add(vuni);
		add(stat);
		add(impt);
		add(vene);
		add(pcoin);
		pcoin.setLnr(x -> changePanel(new PCoinEditPage(getThis(),form, editable)));
		for (int i = 0 ; i < fdesc.length ; i++)
			add(fdesc[i] = new JTF());

		for (JTF jtf : fdesc)
			jtf.setEnabled(editable);

		for (int i = 0; i < fdesc.length; i++) {
			int finalI = i;
			fdesc[i].setLnr(d -> changeDesc(finalI));
		}

		subListener(vene, impt, vuni, form.unit);

		stat.setLnr(x -> {
			Unit u = Identifier.get(cu.getPack().uid);
			Node<Unit> nu = Node.getList(UserProfile.getAll(cu.getPack().uid.pack, Unit.class), u);
			changePanel(new UnitInfoPage(this, nu));
		});
	}

	private void changeDesc(int line) {
		String txt = fdesc[line].getText().trim();
		if (!txt.equals("Description Line " + (line + 1))) {
			uniDesc[line] = txt;
			if (txt.length() > 63) {
				for (int i = line; i + 1 < fdesc.length; i++) {
					if (uniDesc[i].length() > 63) {
						uniDesc[i + 1] = uniDesc[i].substring(63) + uniDesc[i + 1];
						uniDesc[i] = uniDesc[i].substring(0, 63);
					}
				}
				if (uniDesc[fdesc.length - 1].length() > 63)
					uniDesc[fdesc.length - 1] = uniDesc[fdesc.length - 1].substring(0, 63);
			}
		}
		form.explanation = String.join("<br>", uniDesc);
		setData(cu);
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(llv, x, y, 50, 50, 100, 50);
		set(flv, x, y, 150, 50, 200, 50);
		set(ldr, x, y, 50, 350, 100, 50);
		set(fdr, x, y, 150, 350, 200, 50);
		set(lrs, x, y, 350, 50, 100, 50);
		set(frs, x, y, 450, 50, 200, 50);
		set(llr, x, y, 650, 50, 100, 50);
		set(flr, x, y, 750, 50, 200, 50);
		set(ueb, x, y, 50, 650, 600, 500);
		if (editable) {
			set(vuni, x, y, 650, 800, 200, 50);
			set(stat, x, y, 850, 800, 200, 50);
		} else {
			set(vuni, x, y, 650, 750, 200, 50);
			set(stat, x, y, 850, 750, 200, 50);
		}
		set(impt, x, y, 50, 1150, 200, 50);
		set(vene, x, y, 250, 1150, 200, 50);
		set(pcoin, x, y, 450, 1150, 200, 50);
		int h = 1000;
		for (JTF jtf : fdesc) {
			set(jtf, x, y, 650, h, 750, 50);
			h += 50;
		}
		ueb.resized();

	}

	@Override
	protected void setData(CustomEntity data) {
		super.setData(data);
		uniDesc = form.descriptionGet().split("<br>",4);
		for (int i = 0; i < fdesc.length; i++)
			fdesc[i].setText("" + (uniDesc[i].length() > 0 ? uniDesc[i] : "Description Line " + (i + 1)));
		flv.setText("" + lv);
		frs.setText("" + bas.t().getFinRes(cu.getRespawn()));
		fdr.setText("" + (int) (cu.getPrice() * 1.5));
		flr.setText(interpretLayer(cu.back, cu.front));
		int imu = 0;
		for (int j : EABIIND)
			if (j > 100) {
				int id = j - 100;
				if (cu.getProc().getArr(id).exists())
					imu |= 1 << id - IMUSFT;
			}
		ueb.setData(new int[] { cu.abi, imu }, data.traits);
		if (cu.getPCoin() != null)
			cu.pcoin.update();
	}

	private String interpretLayer(int back, int front) {
		if (front == back)
			return front + "";
		else
			return back + "~" + front;
	}
}
