package page.info.edit;

import common.CommonStatic;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.Data;
import common.util.pack.Background;
import common.util.stage.*;
import org.jcodec.common.tools.MathUtil;
import page.*;
import page.view.BGViewPage;
import page.view.CastleViewPage;
import page.view.MusicPage;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

class HeadEditTable extends Page {

	private static final long serialVersionUID = 1L;

	private final JL hea = new JL(1, "ht00");
	private final JL len = new JL(1, "ht01");
	private final JBTN mus = new JBTN(1, "mus");
	private final JL max = new JL(1, "ht02");
	private final JBTN bg = new JBTN(1, "ht04");
	private final JBTN cas = new JBTN(1, "ht05");
	private final JTF name = new JTF();
	private final JTF jhea = new JTF();
	private final JTF jlen = new JTF();
	private final JTF jbg = new JTF();
	private final JTF jcas = new JTF();
	private final JTF jm0 = new JTF();
	private final JTF jmh = new JTF();
	private final JTF jm1 = new JTF();
	private final JTF jbgh = new JTF();
	private final JTF jbg1 = new JTF();
	private final JTG con = new JTG(1, "ht03");
	private final JTF[] star = new JTF[4];
	private final JTF jmax = new JTF();
	private final JL loop = new JL(1, "lop");
	private final JL loop1 = new JL(1, "lop1");
	private final JTF lop = new JTF();
	private final JTF lop1 = new JTF();
	private final JL minres = new JL(1, "minspawn");
	private final JL cost = new JL(1, "chcos");
	private final JTF minrest = new JTF();
	private final JTF cos = new JTF();
	private final LimitTable lt;

	private Stage sta;
	private final UserPack pac;
	private BGViewPage bvp;
	private CastleViewPage cvp;
	private MusicPage mp;

	private int bgl = 0;
	private int musl = 0;

	protected HeadEditTable(Page p, UserPack pack) {
		super(p);
		pac = pack;
		lt = new LimitTable(p, this, pac);
		ini();
	}

	@Override
	public void callBack(Object o) {
		setData(sta);
	}

	@Override
	protected void renew() {
		lt.renew();
		if (bvp != null && bvp.getSelected() != null) {
			Identifier<Background> val = bvp.getSelected().id;
			if (val == null)
				return;
			if (bgl == 0) {
				jbg.setText(val.toString());
				sta.bg = val;
			} else {
				jbg1.setText(val.toString());
				sta.bg1 = val;
			}
		}
		if (cvp != null) {
			Identifier<CastleImg> val = cvp.getVal();
			if (val == null)
				return;
			jcas.setText(val.toString());
			sta.castle = val;
		}

		if (mp != null) {
			Identifier<Music> val = mp.getSelected();
			if (musl == 0) {
				jm0.setText("" + val);
				sta.mus0 = val;
				if (sta.mus0 != null) {
					lop.setEnabled(true);
					getMusTime(sta.mus0, lop);
				} else {
					lop.setText("00:00.000");
					sta.loop0 = 0;
					lop.setEnabled(false);
				}
			} else {
				jm1.setText("" + val);
				sta.mus1 = val;
				if (sta.mus1 != null) {
					lop1.setEnabled(true);
					getMusTime(sta.mus1, lop1);
				} else {
					lop1.setText("00:00.000");
					sta.loop1 = 0;
					lop1.setEnabled(false);
				}
			}
		}

		minrest.setEnabled(sta != null);


		if (sta != null)
			minrest.setText(generateMinRespawn(sta.minSpawn, sta.maxSpawn));

		bvp = null;
		cvp = null;
		mp = null;
	}

	@Override
	protected void resized(int x, int y) {
		int w = 1400 / 8;
		set(name, x, y, 0, 0, w * 2, 50);
		set(hea, x, y, 0, 50, w, 50);
		set(jhea, x, y, w, 50, w, 50);
		set(len, x, y, w * 2, 50, w, 50);
		set(jlen, x, y, w * 3, 50, w, 50);
		set(max, x, y, w * 4, 50, w, 50);
		set(jmax, x, y, w * 5, 50, w, 50);
		set(con, x, y, w * 6, 50, w, 50);
		set(bg, x, y, 0, 100, w, 50);
		set(jbg, x, y, w, 100, w, 50);
		set(jbgh, x, y, w * 6, 100, w, 50);
		set(jbg1, x, y, w * 7, 100, w, 50);
		set(cas, x, y, w * 2, 100, w, 50);
		set(jcas, x, y, w * 3, 100, w, 50);
		set(minres, x, y, w * 4, 100, w, 50);
		set(minrest, x, y, w * 5, 100, w, 50);
		set(cost, x, y, w * 6, 0, w, 50);
		set(cos, x, y, w * 7, 0, w, 50);
		set(mus, x, y, 0, 150, w, 50);
		set(jm0, x, y, w, 150, w, 50);
		set(loop, x, y, w * 2, 150, w, 50);
		set(lop, x, y, w * 3, 150, w, 50);
		set(jmh, x, y, w * 4, 150, w, 50);
		set(jm1, x, y, w * 5, 150, w, 50);
		set(loop1, x, y, w * 6, 150, w, 50);
		set(lop1, x, y, w * 7, 150, w, 50);
		for (int i = 0; i < 4; i++)
			set(star[i], x, y, w * (2 + i), 0, w, 50);
		set(lt, x, y, 0, 200, 1400, 100);
		lt.componentResized(x, y);
	}

	protected void setData(Stage st) {
		sta = st;
		abler(st != null);
		if (st == null)
			return;
		change(true);
		name.setText(st.toString());
		jhea.setText("" + st.health);
		jlen.setText("" + st.len);
		jbg.setText("" + st.bg);
		jbgh.setText("<" + st.bgh + "% health:");
		jbg1.setText("" + st.bg1);
		jcas.setText("" + st.castle);
		jm0.setText("" + st.mus0);
		jmh.setText("<" + st.mush + "% health:");
		jm1.setText("" + st.mus1);
		jmax.setText("" + st.max);
		cos.setText("" + (st.getCont().price + 1));
		con.setSelected(!st.non_con);
		String str = get(1, "star") + ": ";
		for (int i = 0; i < 4; i++)
			if (i < st.getCont().stars.length)
				star[i].setText(i + 1 + str + st.getCont().stars[i] + "%");
			else
				star[i].setText(i + 1 + str + "/");
		Limit lim = st.lim;
		lt.setLimit(lim);
		change(false);

		lop.setText(convertTime(sta.loop0));
		lop1.setText(convertTime(sta.loop1));

		if (sta.mus0 != null) {
			lop.setEnabled(true);
			getMusTime(sta.mus0, lop);
		} else {
			lop.setText("00:00.000");
			lop.setToolTipText("No music");
			sta.loop0 = 0;
			lop.setEnabled(false);
		}

		if (sta.mus1 != null) {
			lop1.setEnabled(true);
			getMusTime(sta.mus1, lop1);
		} else {
			lop1.setText("00:00.000");
			lop1.setToolTipText("No music");
			sta.loop1 = 0;
			lop1.setEnabled(false);
		}

		minrest.setEnabled(true);
		minrest.setText(generateMinRespawn(st.minSpawn, st.maxSpawn));
	}

	private void abler(boolean b) {
		bg.setEnabled(b);
		cas.setEnabled(b);
		name.setEnabled(b);
		jhea.setEnabled(b);
		jlen.setEnabled(b);
		jbg.setEnabled(b);
		jbgh.setEnabled(b);
		jbg1.setEnabled(b);
		jcas.setEnabled(b);
		jmax.setEnabled(b);
		con.setEnabled(b);
		mus.setEnabled(b);
		jm0.setEnabled(b);
		jmh.setEnabled(b);
		jm1.setEnabled(b);
		for (JTF jtf : star)
			jtf.setEnabled(b);
		cos.setEnabled(b);
		lt.abler(b);
	}

	private void addListeners() {

		bg.addActionListener(arg0 -> {
			bvp = new BGViewPage(getFront(), pac.desc.id, sta.bg);
			changePanel(bvp);
		});

		cas.addActionListener(arg0 -> {
			cvp = new CastleViewPage(getFront(), CastleList.from(sta), sta.castle);
			changePanel(cvp);
		});

		mus.addActionListener(arg0 -> {
			mp = new MusicPage(getFront(), pac.desc.id);
			changePanel(mp);
		});

		con.addActionListener(arg0 -> {
			sta.non_con = !con.isSelected();
			setData(sta);
		});

	}

	private String convertTime(long milli) {
		long min = milli / 60 / 1000;
		double time = milli - (double) min * 60000;
		time /= 1000;
		NumberFormat nf = NumberFormat.getInstance(Locale.US);

		DecimalFormat df = (DecimalFormat) nf;

		df.applyPattern("#.###");
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

	private void getMusTime(Identifier<Music> mus1, JTF jtf) {
		Music f = Identifier.get(mus1);
		if (f == null || f.data == null) {
			jtf.setToolTipText("Music not found");
			return;
		}
		try {
			long duration = CommonStatic.def.getMusicLength(f);

			if (duration == -1) {
				jtf.setToolTipText("Invalid Format");
			} else if (duration == -2) {
				jtf.setToolTipText("Unsupported Format");
			} else if (duration == -3) {
				jtf.setToolTipText("Can't get duration");
			} else if (duration >= 0) {
				jtf.setToolTipText(convertTime(duration));
			} else {
				jtf.setToolTipText("Unknown error " + duration);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ini() {
		set(hea);
		set(len);
		set(max);
		add(bg);
		add(cas);
		add(con);
		add(mus);
		set(jhea);
		set(jlen);
		set(jbg);
		set(jbgh);
		set(jbg1);
		set(jcas);
		set(jmax);
		set(name);
		set(jm0);
		set(jmh);
		set(jm1);
		add(lt);
		set(loop);
		set(lop);
		set(loop1);
		set(lop1);
		set(minres);
		set(minrest);
		set(cost);
		set(cos);
		con.setSelected(true);

		for (int i = 0; i < 4; i++)
			set(star[i] = new JTF());
		addListeners();
		abler(false);
	}

	private void input(JTF jtf, String str) {
		if (sta == null)
			return;
		if (jtf == name) {
			str = str.trim();
			if (str.length() > 0)
				sta.name = str;
			return;
		}
		int val = CommonStatic.parseIntN(str);
		if (jtf == jhea) {
			if (val <= 0)
				return;
			sta.health = val;
		}
		if (jtf == jlen) {
			if (val > 8000)
				val = 8000;
			if (val < 2000)
				val = 2000;
			sta.len = val;
		}
		if (jtf == jmax) {
			if (val <= 0 || val > 50)
				return;
			sta.max = val;
		}
		for (int i = 0; i < 4; i++)
			if (jtf == star[i]) {
				String[] strs = str.split(" ");
				int[] vals = CommonStatic.parseIntsN(strs[strs.length - 1]);
				val = vals.length == 0 ? -1 : vals[vals.length - 1];
				int[] sr = sta.getCont().stars;
				if (i == 0 && val <= 0)
					val = 100;
				if (i < sr.length)
					if (val > 0)
						sr[i] = val;
					else
						sta.getCont().stars = Arrays.copyOf(sr, i);
				else if (val > 0) {
					int[] ans = new int[i + 1];
					for (int j = 0; j < i; j++)
						if (j < sr.length)
							ans[j] = sr[j];
						else
							ans[j] = sr[sr.length - 1];
					ans[i] = val;
					sta.getCont().stars = ans;
				}
			}

		if (jtf == jbgh)
			sta.bgh = val;

		if (jtf == jmh)
			sta.mush = val;

		if (jtf == lop) {
			long tim = toMilli(jtf.getText());

			if (tim != -1) {
				sta.loop0 = tim;
			}

			lop.setText(convertTime(sta.loop0));
		}

		if (jtf == lop1) {
			long tim = toMilli(jtf.getText());

			if (tim != -1) {
				sta.loop1 = tim;
			}

			lop1.setText(convertTime(sta.loop1));
		}

		if (jtf == minrest) {
			try {
				int[] vals = CommonStatic.parseIntsN(jtf.getText());

				if (vals.length == 1) {
					if (vals[0] <= 0)
						return;

					sta.minSpawn = sta.maxSpawn = vals[0];
				} else if (vals.length >= 2) {
					if (vals[0] <= 0 || vals[1] <= 0)
						return;

					if (vals[0] == vals[1]) {
						sta.minSpawn = sta.maxSpawn = vals[0];
					} else if (vals[0] < vals[1]) {
						sta.minSpawn = vals[0];
						sta.maxSpawn = vals[1];
					}
				}

				minrest.setText(generateMinRespawn(sta.minSpawn, sta.maxSpawn));
			} catch (Exception ignored) {
			}
		}


		if (jtf == jbg) {
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty())
				return;
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				Background b = UserProfile.getBCData().bgs.get(CommonStatic.safeParseInt(result[0]));
				if (b == null)
					return;
				jbg.setText(b.toString());
				sta.bg = b.getID();
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.parseIntN(p));
			PackData pack = PackData.getPack(p);
			if (pack == null)
				return;
			Background bg = pack.bgs.get(CommonStatic.safeParseInt(i));
			if (bg == null)
				return;

			jbg.setText(bg.toString());
			sta.bg = bg.getID();
		}

		if (jtf == jbg1) {
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty())
				return;
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				Background b = UserProfile.getBCData().bgs.get(CommonStatic.safeParseInt(result[0]));
				if (b == null)
					return;
				jbg1.setText(b.toString());
				sta.bg = b.getID();
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.parseIntN(p));
			PackData pack = PackData.getPack(p);
			if (pack == null)
				return;
			Background bg = pack.bgs.get(CommonStatic.safeParseInt(i));
			if (bg == null)
				return;

			jbg1.setText(bg.toString());
			sta.bg = bg.getID();
		}

		if (jtf == jcas) {
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty()) {
				jm0.setText("null");
				sta.mus0 = null;
				return;
			}
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				CastleImg c = CastleList.getList("000000").get(CommonStatic.safeParseInt(result[0]));
				if (c == null)
					return;
				jcas.setText(c.toString());
				sta.castle = c.getID();
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.safeParseInt(p));
			CastleList cl = CastleList.getList(p);
			if (cl == null)
				return;
			CastleImg castle = cl.get(CommonStatic.safeParseInt(i));
			if (castle == null)
				return;

			jcas.setText(castle.toString());
			sta.castle = castle.getID();
		}

		if (jtf == jm0) {
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty()) {
				jm0.setText("null");
				sta.mus0 = null;
				return;
			}
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				Music m = UserProfile.getBCData().musics.get(CommonStatic.safeParseInt(result[0]));
				if (m == null)
					return;
				jm0.setText(m.toString());
				sta.mus0 = m.getID();
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.safeParseInt(p));
			PackData pack = PackData.getPack(p);
			if (pack == null) {
				jm0.setText("null");
				sta.mus0 = null;
				return;
			}
			Music music = pack.musics.get(CommonStatic.safeParseInt(i));
			if (music == null)
				return;

			jm0.setText(str);
			sta.mus0 = music.getID();
		}

		if (jtf == jm1) {
			String[] result = CommonStatic.getPackContentID(str);
			if (result[0].isEmpty()) {
				jm1.setText("null");
				sta.mus1 = null;
				return;
			}
			if (result[1].isEmpty()) {
				if (!CommonStatic.isInteger(result[0]))
					return;
				Music m = UserProfile.getBCData().musics.get(CommonStatic.safeParseInt(result[0]));
				if (m == null)
					return;
				jm1.setText(m.toString());
				sta.mus1 = m.getID();
				return;
			}
			String p = result[0];
			String i = result[1];
			if (CommonStatic.isInteger(p))
				p = Data.hex(CommonStatic.safeParseInt(p));
			PackData pack = PackData.getPack(p);
			if (pack == null) {
				jm1.setText("null");
				sta.mus1 = null;
				return;
			}
			Music music = pack.musics.get(CommonStatic.safeParseInt(i));
			if (music == null)
				return;

			jm1.setText(str);
			sta.mus1 = music.getID();
		}

		if (jtf == cos) {
			sta.getCont().price = MathUtil.clip(val - 1, 0, 9);
		}
	}

	private void set(JLabel jl) {
		jl.setHorizontalAlignment(SwingConstants.CENTER);
		jl.setBorder(BorderFactory.createEtchedBorder());
		add(jl);
	}

	private void set(JTF jtf) {
		add(jtf);

		jtf.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				if (isAdj())
					return;
				if (jtf == jbg)
					bgl = 0;
				if (jtf == jbg1)
					bgl = 1;
				if (jtf == jm0)
					musl = 0;
				if (jtf == jm1)
					musl = 1;
			}

			@Override
			public void focusLost(FocusEvent fe) {
				if (isAdj())
					return;
				input(jtf, jtf.getText());
				setData(sta);
			}
		});

	}

	private long toMilli(String time) {
		try {
			long[] times = CommonStatic.parseLongsN(time);

			for (long t : times) {
				if (t < 0) {
					return -1;
				}
			}

			if (times.length == 1) {
				return times[0] * 1000;
			} else if (times.length == 2) {
				return (times[0] * 60 + times[1]) * 1000;
			} else if (times.length == 3) {
				if (times[2] < 1000) {
					return (times[0] * 60 + times[1]) * 1000 + times[2];
				} else {
					String decimal = Long.toString(times[2]).substring(0, 3);
					return (times[0] * 60 + times[1]) * 1000 + Integer.parseInt(decimal);
				}
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	private String generateMinRespawn(int min, int max) {
		if (min == max) {
			return min + "f";
		} else {
			return min + "f ~ " + max + "f";
		}
	}
}