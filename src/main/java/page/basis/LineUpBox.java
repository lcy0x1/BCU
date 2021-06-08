
package page.basis;

import common.CommonStatic;
import common.battle.LineUp;
import common.system.P;
import common.system.SymCoord;
import common.system.VImg;
import common.system.fake.FakeGraphics;
import common.system.fake.FakeImage;
import common.util.Res;
import common.util.unit.Combo;
import common.util.unit.Form;
import common.util.unit.Unit;
import page.Page;
import utilpc.PP;
import utilpc.awt.FG2D;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class LineUpBox extends Canvas {

	private static final long serialVersionUID = 1L;

	private Form[] backup = new Form[5];
	private final Page page;
	private LineUp lu;
	private int pt = 0, time = 0;
	private Combo sc;
	private PP relative, mouse;

	protected Form sf;

	public LineUpBox(Page p) {
		page = p;
		setIgnoreRepaint(true);
	}

	@Override
	public void paint(Graphics g) {
		VImg[] slot = CommonStatic.getBCAssets().slot;
		Image bimg = createImage(600, 300);
		if (bimg == null)
			return;
		FakeGraphics gra = new FG2D(bimg.getGraphics());
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 5; j++) {
				Form f = getForm(i, j);
				VImg img;
				if (f == null)
					img = slot[0];
				else
					img = f.anim.getUni();
				if (sf == null || sf != f || relative == null)
					gra.drawImage(img.getImg(), 120 * j, 100 * i);
				if (f == null)
					continue;
				if (time == 0 && sc != null)
					for (Form fc : sc.forms)
						if (f.unit == fc.unit && f.fid >= fc.fid)
							gra.drawImage(slot[2].getImg(), 120 * j, 100 * i);
				if (time == 1 && sf != null && f.unit == sf.unit && relative == null)
					gra.drawImage(slot[1].getImg(), 120 * j, 100 * i);
				if (sf == null || sf != f || relative == null)
					Res.getCost(lu.getLv(f).getLvs()[0], true,
							new SymCoord(gra, 1, 120 * j, 100 * i + img.getImg().getHeight(), 2));
			}
		if (relative != null && sf != null) {
			Point p = relative.sf(mouse).toPoint();
			FakeImage uni = sf.anim.getUni().getImg();
			gra.drawImage(uni, p.x, p.y);
			Res.getCost(lu.getLv(sf).getLvs()[0], true, new SymCoord(gra, 1, p.x, p.y + uni.getHeight(), 2));
		}
		g.drawImage(bimg, 0, 0, getWidth(), getHeight(), null);
		pt++;
		if (pt == 5)
			time = 1 - time;
		pt %= 5;
	}

	public void setLU(LineUp l) {
		lu = l;
		backup = new Form[5];
	}

	protected void adjForm() {
		if (sf == null || getPos(sf) == -1)
			return;
		int i = getPos(sf);
		if (getForm(i).unit == sf.unit) {
			Form[] ufs = sf.unit.forms;
			sf = ufs[(getForm(i).fid + 1) % ufs.length];
			setForm(i, sf);
			lu.renew();
			page.callBack(null);
		}

	}

	protected void click(Point p) {
		p = getPos(p);
		select(getForm(p.y, p.x));
	}

	protected void drag(Point p) {
		if (relative == null || sf == null)
			return;
		mouse = new PP(p).divide(getScale());
		int ori = getPos(sf);
		Point pf = getPos(mouse);
		int fin = pf.x + pf.y * 5;
		if (ori != fin)
			jump(ori, fin);
	}

	protected void press(Point p) {
		click(p);
		PP ul = new PP(getPos(p)).times(new P(120, 100));
		relative = ul.sf(mouse = new PP(p).divide(getScale()));

	}

	protected void release(Point p) {
		relative = null;
		mouse = null;
	}

	protected void select(Combo c) {
		sc = c;
		time = 0;
		paint(getGraphics());
	}

	protected void select(Form f) {
		sf = f;
		if (f == null)
			return;
		if (getPos(f) == -1) {
			boolean b = false;
			for (int i = 0; i < 5; i++)
				if (backup[i] == null) {
					backup[i] = f;
					b = true;
					break;
				}
			if (!b)
				backup[4] = f;
		}
		time = 1;
		paint(getGraphics());
		page.callBack(f);
	}

	protected void setLv(int[] lv) {
		if (lv.length == 0 || sf == null)
			return;
		lu.setLv(sf.unit, sf.regulateLv(lv, lu.getLv(sf).getLvs()));
	}

	protected void setPos(int pos) {
		if (sf == null || getPos(sf) == -1)
			return;
		int p = getPos(sf);
		jump(p, pos);
	}

	protected void updateLU() {
		Set<Unit> su = new TreeSet<>();
		for (int i = 0; i < 10; i++)
			if (getForm(i) != null)
				su.add(getForm(i).unit);
		for (int i = 0; i < 5; i++)
			if (backup[i] != null && su.contains(backup[i].unit))
				backup[i] = null;
	}

	protected void resetBackup() {
		Arrays.fill(backup, null);
	}

	private Form getForm(int pos) {
		return pos < 10 ? lu.fs[pos / 5][pos % 5] : backup[pos % 5];
	}

	private Form getForm(int i, int j) {
		return i < 2 ? lu.fs[i][j] : backup[j];
	}

	private int getPos(Form f) {
		if (f == null)
			return -1;
		for (int i = 0; i < 15; i++)
			if (getForm(i) != null && getForm(i).unit == f.unit)
				return i;
		return -1;
	}

	private Point getPos(Point p) {
		PP siz = new PP(getSize());
		PP ans = new PP(p).times(new P(5, 3)).divide(siz);
		ans.limit(new PP(4, 2));
		return ans.toPoint();
	}

	private Point getPos(PP p) {
		PP ans = p.copy().times(new P(5, 3)).divide(new P(600, 300));
		ans.limit(new P(4, 2));
		return ans.toPoint();
	}

	private P getScale() {
		return new PP(getSize()).divide(new P(600, 300));
	}

	private void jump(int ior, int ifi) {
		Form f = getForm(ior);
		if (ior > ifi)
			for (int i = ifi; i <= ior; i++)
				f = setForm(i, f);
		else {
			if (ifi > 9) {
				f = setForm(ifi, f);
				if (ior > 9)
					setForm(ior, f);
				else
					for (int i = 0; i < 5; i++)
						if (backup[i] == null) {
							setForm(10 + i, f);
							break;
						}
				ifi = 9;
				f = null;
			}
			for (int i = ifi; i >= ior; i--)
				f = setForm(i, f);
		}
		lu.arrange();
		lu.renew();
		page.callBack(null);
	}

	private Form setForm(int pos, Form f) {
		Form ans = getForm(pos);
		if (pos < 10)
			lu.fs[pos / 5][pos % 5] = f;
		else
			backup[pos % 5] = f;
		return ans;
	}

}
