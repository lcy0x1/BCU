package page;

import common.CommonStatic;
import common.CommonStatic.Config;
import common.util.ImgCore;
import common.util.lang.MultiLangCont;
import io.BCMusic;
import io.BCUReader;
import main.MainBCU;
import main.Opts;
import page.view.ViewBox;
import utilpc.Theme;

import javax.swing.*;
import java.awt.*;

public class ConfigPage extends Page {

	private static final long serialVersionUID = 1L;

	private static Config cfg() {
		return CommonStatic.getConfig();
	}

	private final JBTN back = new JBTN(0, "back");
	private final JBTN filt = new JBTN(0, "filter" + MainBCU.FILTER_TYPE);
	private final JBTN rlla = new JBTN(0, "rllang");
	private final JTG prel = new JTG(0, "preload");
	private final JTG whit = new JTG(0, "white");
	private final JTG refe = new JTG(0, "axis");
	private final JTG jogl = new JTG(0, "JOGL");
	private final JTG musc = new JTG(0, "musc");
	private final JTG exla = new JTG(0, "exlang");
	private final JTG extt = new JTG(0, "extip");
	private final JL preflv = new JL(0, "preflv");
	private final JTF prlvmd = new JTF();
	private final JBTN[] left = new JBTN[4];
	private final JBTN[] right = new JBTN[4];
	private final JL[] name = new JL[4];
	private final JL[] vals = new JL[4];
	private final JL jlmin = new JL(0, "opamin");
	private final JL jlmax = new JL(0, "opamax");
	private final JL jlbg = new JL(0, "BGvol");
	private final JL jlse = new JL(0, "SEvol");
	private final JL jlui = new JL(0, "UIvol");
	private final JBTN theme = new JBTN(0, MainBCU.light ? "themel" : "themed");
	private final JBTN nimbus = new JBTN(0, MainBCU.nimbus ? "nimbus" : "tdefault");
	private final JSlider jsmin = new JSlider(0, 100);
	private final JSlider jsmax = new JSlider(0, 100);
	private final JSlider jsbg = new JSlider(0, 100);
	private final JSlider jsse = new JSlider(0, 100);
	private final JSlider jsui = new JSlider(0, 100);
	private final JList<String> jls = new JList<>(MainLocale.LOC_NAME);
	private final JBTN row = new JBTN(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow");

	private final JScrollPane jsps = new JScrollPane(jls);

	private boolean changing = false;

	protected ConfigPage(Page p) {
		super(p);

		ini();
		resized();
	}

	@Override
	protected void renew() {
		jlmin.setText(0, "opamin");
		jlmax.setText(0, "opamax");
		for (int i = 0; i < 4; i++) {
			name[i].setText(0, ImgCore.NAME[i]);
			vals[i].setText(0, ImgCore.VAL[cfg().ints[i]]);
		}
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jogl, x, y, 50, 100, 200, 50);
		set(prel, x, y, 50, 200, 200, 50);
		set(whit, x, y, 50, 300, 200, 50);
		set(refe, x, y, 50, 400, 200, 50);
		for (int i = 0; i < 4; i++) {
			set(name[i], x, y, 300, 100 + i * 100, 200, 50);
			set(left[i], x, y, 550, 100 + i * 100, 100, 50);
			set(vals[i], x, y, 650, 100 + i * 100, 200, 50);
			set(right[i], x, y, 850, 100 + i * 100, 100, 50);
		}
		set(jsps, x, y, 1100, 100, 200, 400);
		set(jlmin, x, y, 50, 500, 400, 50);
		set(jsmin, x, y, 50, 550, 1000, 100);
		set(jlmax, x, y, 50, 650, 400, 50);
		set(jsmax, x, y, 50, 700, 1000, 100);
		set(jlbg, x, y, 50, 800, 400, 50);
		set(jsbg, x, y, 50, 850, 1000, 100);
		set(jlse, x, y, 50, 950, 400, 50);
		set(jsse, x, y, 50, 1000, 1000, 100);
		set(jlui, x, y, 50, 1100, 400, 50);
		set(jsui, x, y, 50, 1150, 1000, 100);
		set(filt, x, y, 1100, 550, 200, 50);
		set(musc, x, y, 1350, 550, 200, 50);
		set(exla, x, y, 1100, 650, 450, 50);
		set(extt, x, y, 1100, 750, 450, 50);
		set(rlla, x, y, 1100, 850, 450, 50);
		set(nimbus, x, y, 1100, 950, 200, 50);
		set(theme, x, y, 1350, 950, 200, 50);
		set(row, x, y, 1100, 1050, 450, 50);
		set(preflv, x, y, 1100, 1150, 225, 50);
		set(prlvmd, x, y, 1325, 1150, 225, 50);
	}

	private void addListeners() {
		back.addActionListener(arg0 -> changePanel(getFront()));

		prel.addActionListener(arg0 -> MainBCU.preload = prel.isSelected());

		exla.addActionListener(arg0 -> {
			MainLocale.exLang = exla.isSelected();
			Page.renewLoc(getThis());
		});

		extt.addActionListener(arg0 -> {
			MainLocale.exTTT = extt.isSelected();
			Page.renewLoc(getThis());
		});

		rlla.addActionListener(arg0 -> {
			MultiLangCont.getStatic().clear();
			BCUReader.readLang();
			Page.renewLoc(getThis());
		});

		whit.addActionListener(arg0 -> ViewBox.Conf.white = whit.isSelected());

		refe.addActionListener(arg0 -> cfg().ref = refe.isSelected());

		jogl.addActionListener(arg0 -> {
			MainBCU.USE_JOGL = jogl.isSelected();
			if (Opts.conf("This requires restart to apply. Do you want to restart?"))
				CommonStatic.def.exit(true);
		});

		for (int i = 0; i < 4; i++) {
			int I = i;

			left[i].addActionListener(arg0 -> {
				cfg().ints[I]--;
				vals[I].setText(0, ImgCore.VAL[cfg().ints[I]]);
				left[I].setEnabled(cfg().ints[I] > 0);
				right[I].setEnabled(cfg().ints[I] < 2);
			});

			right[i].addActionListener(arg0 -> {
				cfg().ints[I]++;
				vals[I].setText(0, ImgCore.VAL[cfg().ints[I]]);
				left[I].setEnabled(cfg().ints[I] > 0);
				right[I].setEnabled(cfg().ints[I] < 2);
			});

		}

		jsmin.addChangeListener(arg0 -> cfg().deadOpa = jsmin.getValue());

		jsmax.addChangeListener(arg0 -> cfg().fullOpa = jsmax.getValue());

		jsbg.addChangeListener(arg0 -> BCMusic.setBGVol(jsbg.getValue()));

		jsse.addChangeListener(arg0 -> BCMusic.setSEVol(jsse.getValue()));

		jsui.addChangeListener(arg0 -> BCMusic.setUIVol(jsui.getValue()));

		jls.addListSelectionListener(arg0 -> {
			if (changing)
				return;
			changing = true;
			if (jls.getSelectedIndex() == -1) {
				jls.setSelectedIndex(cfg().lang);
			}
			cfg().lang = jls.getSelectedIndex();
			Page.renewLoc(getThis());
			changing = false;
		});

		filt.addActionListener(e -> {
			MainBCU.FILTER_TYPE = 1 - MainBCU.FILTER_TYPE;
			filt.setText(0, "filter" + MainBCU.FILTER_TYPE);
		});

		musc.addActionListener(arg0 -> BCMusic.play = musc.isSelected());

		nimbus.setLnr((b) -> {
			MainBCU.nimbus = !MainBCU.nimbus;

			if (Opts.conf("This requires restart to apply. Do you want to restart?"+(MainBCU.nimbus ? "\n\nWarning : Using Nimbus theme may result in high CPU usage" : "")))
				CommonStatic.def.exit(true);
			else
				MainBCU.nimbus = !MainBCU.nimbus;
		});

		theme.setLnr((b) -> {
			MainBCU.light = !MainBCU.light;

			if (MainBCU.light) {
				theme.setText(MainLocale.getLoc(MainLocale.PAGE, "themel"));
				Theme.LIGHT.setTheme();
				Page.BGCOLOR = new Color(255, 255, 255);
				setBackground(BGCOLOR);
				SwingUtilities.updateComponentTreeUI(this);
				theme.setToolTipText(MainLocale.getLoc(MainLocale.PAGE, "themel"));
			} else {
				theme.setText(MainLocale.getLoc(MainLocale.PAGE, "themed"));
				Theme.DARK.setTheme();
				Page.BGCOLOR = new Color(40, 40, 40);
				setBackground(BGCOLOR);
				SwingUtilities.updateComponentTreeUI(this);
				theme.setToolTipText(MainLocale.getLoc(MainLocale.PAGE, "themed"));
			}
		});

		row.addActionListener(a -> {
			CommonStatic.getConfig().twoRow = !CommonStatic.getConfig().twoRow;
			row.setText(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow");
		});
	}

	private void ini() {
		add(back);
		add(jogl);
		add(prel);
		add(refe);
		add(whit);
		add(jsps);
		set(jsmin);
		set(jsmax);
		add(jlmin);
		add(jlmax);
		add(filt);
		add(musc);
		add(rlla);
		add(exla);
		add(extt);
		add(jlbg);
		add(jlse);
		add(jlui);
		set(jsbg);
		set(jsse);
		set(jsui);
		add(nimbus);
		add(theme);
		add(row);
		add(preflv);
		add(prlvmd);
		set(prlvmd);
		prlvmd.setText("" + MainBCU.prefLevel);
		jls.setSelectedIndex(cfg().lang);
		jsmin.setValue(cfg().deadOpa);
		jsmax.setValue(cfg().fullOpa);
		jsbg.setValue(BCMusic.VOL_BG);
		jsse.setValue(BCMusic.VOL_SE);
		jsui.setValue(BCMusic.VOL_UI);
		for (int i = 0; i < 4; i++) {
			left[i] = new JBTN("<");
			right[i] = new JBTN(">");
			name[i] = new JL(0, ImgCore.NAME[i]);
			vals[i] = new JL(0, ImgCore.VAL[cfg().ints[i]]);
			add(left[i]);
			add(right[i]);
			add(name[i]);
			add(vals[i]);
			name[i].setHorizontalAlignment(SwingConstants.CENTER);
			vals[i].setHorizontalAlignment(SwingConstants.CENTER);
			name[i].setBorder(BorderFactory.createEtchedBorder());
			vals[i].setBorder(BorderFactory.createEtchedBorder());
			left[i].setEnabled(cfg().ints[i] > 0);
			right[i].setEnabled(cfg().ints[i] < 2);
		}
		exla.setSelected(MainLocale.exLang);
		extt.setSelected(MainLocale.exTTT);
		prel.setSelected(MainBCU.preload);
		whit.setSelected(ViewBox.Conf.white);
		refe.setSelected(cfg().ref);
		musc.setSelected(BCMusic.play);
		jogl.setSelected(MainBCU.USE_JOGL);
		if (!MainBCU.nimbus) {
			theme.setEnabled(false);
		}
		addListeners();
	}

	protected void set(JTF jtf) {
		jtf.setLnr(e -> {
			String text = jtf.getText().trim();
			if (text.length() > 0) {
				int[] v = CommonStatic.parseIntsN(text);
				MainBCU.prefLevel = Math.max(1, v[0]);
				jtf.setText("" + MainBCU.prefLevel);
			}
		});
	}

	private void set(JSlider sl) {
		add(sl);
		sl.setMajorTickSpacing(10);
		sl.setMinorTickSpacing(5);
		sl.setPaintTicks(true);
		sl.setPaintLabels(true);
	}

}
