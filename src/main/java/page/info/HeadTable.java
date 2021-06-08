package page.info;

import common.util.stage.*;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.pack.CharaGroupPage;
import page.pack.LvRestrictPage;
import page.support.AbJTable;
import page.view.BGViewPage;
import page.view.CastleViewPage;
import page.view.MusicPage;

import java.awt.*;
import java.text.DecimalFormat;

public class HeadTable extends AbJTable {

	private static final long serialVersionUID = 1L;

	private static String[] infs, limits, rarity;

	static {
		redefine();
	}

	public static void redefine() {
		infs = Page.get(MainLocale.INFO, "ht0", 6);
		limits = Page.get(MainLocale.INFO, "ht1", 7);
		rarity = new String[] { "N", "EX", "R", "SR", "UR", "LR" };
	}

	private Object[][] data;
	private Stage sta;
	private final Page page;

	protected HeadTable(Page p) {
		page = p;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 8;
	}

	@Override
	public String getColumnName(int arg0) {
		return "";
	}

	@Override
	public int getRowCount() {
		if (data == null)
			return 0;
		return data.length;
	}

	@Override
	public Object getValueAt(int r, int c) {
		if (data == null || r < 0 || c < 0 || r >= data.length || c >= data[r].length)
			return null;
		return data[r][c];
	}

	protected void clicked(Point p) {
		if (data == null)
			return;
		int c = getColumnModel().getColumnIndexAtX(p.x);
		int r = p.y / getRowHeight();
		if (r == 1 && c == 5)
			MainFrame.changePanel(new MusicPage(page, sta.mus0));
		if (r == 1 && c == 7)
			MainFrame.changePanel(new MusicPage(page, sta.mus1));
		if (r == 3 && c == 1)
			MainFrame.changePanel(new BGViewPage(page, null, sta.bg));
		if (r == 3 && c == 3)
			MainFrame.changePanel(new CastleViewPage(page, CastleList.from(sta), sta.castle));
		if (r == 3 && c == 5 && data[r][c] != null && data[r][c] instanceof LvRestrict)
			MainFrame.changePanel(new LvRestrictPage(page, (LvRestrict) data[r][c]));
		if (r == 3 && c == 7 && data[r][c] != null)
			MainFrame.changePanel(new CharaGroupPage(page, (CharaGroup) data[r][c]));
	}

	protected void setData(Stage st) {
		sta = st;
		Object[][] lstr = new Object[6][8];
		Object[] tit, bas, bas2, img, rar, reg;
		tit = lstr[0];
		bas = lstr[1];
		bas2 = lstr[2];
		img = lstr[3];
		rar = lstr[4];
		reg = lstr[5];
		tit[0] = "ID:";
		tit[1] = st.getCont().id + "-" + st.id();
		String star = Page.get(MainLocale.INFO, "star");
		for (int i = 0; i < st.getCont().stars.length; i++)
			tit[2 + i] = (i + 1) + star + ": " + st.getCont().stars[i] + "%";
		tit[6] = Page.get(MainLocale.INFO, "chcos");
		tit[7] = st.getCont().price + 1;
		bas[0] = infs[0];
		bas[1] = st.health;
		bas[2] = infs[1] + ": " + st.len;
		bas[3] = infs[2] + ": " + st.max;
		bas[4] = Page.get(MainLocale.INFO, "mus") + ":";
		bas[5] = st.mus0;
		bas[6] = "<" + st.mush + "%:";
		bas2[0] = Page.get(MainLocale.INFO, "minspawn");
		if(st.minSpawn == st.maxSpawn)
			bas2[1] = st.minSpawn + "f";
		else
			bas2[1] = st.minSpawn + "f ~ " + st.maxSpawn + "f";
		bas2[2] = MainLocale.getLoc(MainLocale.INFO, "ht03");
		bas2[3] = !st.non_con;
		bas2[4] = MainLocale.getLoc(MainLocale.INFO, "lop");
		bas2[5] = convertTime(st.loop0);
		bas2[6] = MainLocale.getLoc(MainLocale.INFO, "lop1");
		bas2[7] = convertTime(st.loop1);
		if(st.timeLimit != 0) {
			bas2[4] = Page.get(MainLocale.INFO, "time");
			bas2[5] = st.timeLimit +" min";
		}
		bas[7] = st.mus1;
		img[0] = infs[4];
		img[1] = st.bg;
		img[2] = "<" + st.bgh + "%";
		img[3] = st.bg1;
		img[4] = infs[5];
		img[5] = st.castle;
		Limit lim = st.getLim(0);
		if (lim != null) {
			if (lim.rare != 0) {
				rar[0] = limits[0];
				int j = 1;
				for (int i = 0; i < rarity.length; i++)
					if (((lim.rare >> i) & 1) > 0)
						rar[j++] = rarity[i];
			}
			if (lim.lvr != null) {
				img[4] = limits[6];
				img[5] = lim.lvr;
			}
			if (lim.group != null) {
				img[6] = limits[5];
				img[7] = lim.group;
			}
			if (lim.min + lim.max + lim.max + lim.line + lim.num > 0) {
				int i = 0;
				if (lim.min > 0) {
					reg[0] = limits[3];
					reg[1] = "" + lim.min;
					i = 2;
				}
				if (lim.max > 0) {
					reg[i] = limits[4];
					reg[i + 1] = "" + lim.max;
					i += 2;
				}
				if (lim.num > 0) {
					reg[i] = limits[1];
					reg[i + 1] = "" + lim.num;
					i += 2;
				}
				if (lim.line > 0)
					reg[i] = limits[2];
			}
		}
		data = lstr;
	}

	private String convertTime(long milli) {
		long min = milli / 60 / 1000;

		double time = milli - (double) min * 60000;

		time /= 1000;

		DecimalFormat df = new DecimalFormat("#.###");

		double s = Double.parseDouble(df.format(time));

		if (s >= 60) {
			s -= 60;
			min += 1;
		}

		if (s < 10) {
			return min + ":" + "0" + df.format(s);
		} else {
			return min + ":" + df.format(s);
		}
	}

}