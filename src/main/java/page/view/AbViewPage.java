package page.view;

import common.pack.Source;
import common.pack.Source.ResourceLocation;
import common.pack.Source.Workspace;
import common.util.anim.AnimCE;
import common.util.anim.AnimD;
import common.util.anim.AnimI;
import common.util.anim.EAnimI;
import io.BCUWriter;
import main.Timer;
import page.JBTN;
import page.JTG;
import page.MainLocale;
import page.Page;
import page.anim.ImgCutEditPage;
import page.awt.BBBuilder;
import page.view.ViewBox.Loader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbViewPage extends Page {

	private static final long serialVersionUID = 1L;
	private static final double res = 0.95;

	private final JBTN back = new JBTN(0, "back");
	protected final JBTN copy = new JBTN(0, "copy");
	private final JList<String> jlt = new JList<>();
	private final JScrollPane jspt = new JScrollPane(jlt);
	private final JSlider jst = new JSlider(100, 900);
	private final JSlider jtl = new JSlider();
	private final JTG jtb = new JTG(0, "pause");
	private final JBTN nex = new JBTN(0, "nextf");
	private final JTG gif = new JTG(0, "gif");
	private final JBTN png = new JBTN(0, "png");
	private final JBTN camres = new JBTN(0, "rescam");
	private final JLabel scale = new JLabel(MainLocale.getLoc(MainLocale.PAGE, "zoom"));

	protected final ViewBox vb;

	private Loader loader = null;
	protected boolean pause;
	private boolean changingT;
	private boolean changingtl;
	private final DecimalFormat df = new DecimalFormat("#.##");

	protected AbViewPage(Page p) {
		this(p, BBBuilder.def.getViewBox());
	}

	protected AbViewPage(Page p, ViewBox box) {
		super(p);
		vb = box;
	}

	protected void enabler(boolean b) {
		jtb.setEnabled(b);
		back.setEnabled(b);
		copy.setEnabled(b);
		jlt.setEnabled(b);
		jst.setEnabled(b);
		jtl.setEnabled(b && pause);
		nex.setEnabled(b && pause);
		gif.setEnabled(b);
		png.setEnabled(b && pause);
	}

	@Override
	protected void exit() {
		Timer.p = 33;
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (e.getSource() == vb)
			vb.mouseDragged(e);
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (e.getSource() == vb)
			vb.mousePressed(e);
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		if (e.getSource() == vb)
			vb.mouseReleased(e);
	}

	@Override
	protected void mouseWheel(MouseEvent e) {
		if (!(e.getSource() instanceof ViewBox))
			return;
		MouseWheelEvent mwe = (MouseWheelEvent) e;
		double d = mwe.getPreciseWheelRotation();
		vb.resize(Math.pow(res, d));
	}

	protected void preini() {
		add(back);
		add(camres);
		add(copy);
		add((Canvas) vb);
		add(jspt);
		add(jst);
		add(jtb);
		add(jtl);
		add(nex);
		add(gif);
		add(png);
		add(scale);
		jst.setPaintLabels(true);
		jst.setPaintTicks(true);
		jst.setMajorTickSpacing(100);
		jst.setMinorTickSpacing(25);
		jst.setValue(Timer.p * 100 / 33);
		jtl.setEnabled(false);
		jtl.setPaintTicks(true);
		jtl.setPaintLabels(true);
		png.setEnabled(false);
		addListener();
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(camres, x ,y, 525, 0, 200, 50);
		set(copy, x, y, 250, 0, 200, 50);
		set((Canvas) vb, x, y, 1000, 100, 1000, 600);
		set(jspt, x, y, 400, 550, 300, 400);
		set(jst, x, y, 1000, 750, 1000, 100);
		set(jtl, x, y, 1000, 900, 1000, 100);
		set(jtb, x, y, 1300, 1050, 200, 50);
		set(nex, x, y, 1600, 1050, 200, 50);
		set(png, x, y, 1300, 1150, 200, 50);
		set(gif, x, y, 1600, 1150, 400, 50);
		set(scale, x, y, 1000, 50, 200, 50);
	}

	protected <T extends Enum<T> & AnimI.AnimType<?, T>> void setAnim(AnimI<?, T> a) {
		if (!changingT) {
			int ind = jlt.getSelectedIndex();
			if (ind == -1)
				ind = 0;
			a.anim.check();
			String[] strs = a.anim.names();
			jlt.setListData(strs);
			if (ind >= strs.length)
				ind = 0;
			jlt.setSelectedIndex(ind);
		}
		if (jlt.getSelectedIndex() == -1)
			return;
		vb.setEntity(a.getEAnim(a.types()[jlt.getSelectedIndex()]));
		jtl.setMinimum(0);
		jtl.setMaximum(vb.getEnt().len());
		jtl.setLabelTable(null);
		if (vb.getEnt().len() <= 50) {
			jtl.setMajorTickSpacing(5);
			jtl.setMinorTickSpacing(1);
		} else if (vb.getEnt().len() <= 200) {
			jtl.setMajorTickSpacing(10);
			jtl.setMinorTickSpacing(2);
		} else if (vb.getEnt().len() <= 1000) {
			jtl.setMajorTickSpacing(50);
			jtl.setMinorTickSpacing(10);
		} else if (vb.getEnt().len() <= 5000) {
			jtl.setMajorTickSpacing(250);
			jtl.setMinorTickSpacing(50);
		} else {
			jtl.setMajorTickSpacing(1000);
			jtl.setMinorTickSpacing(200);
		}
	}

	@Override
	protected void timer(int t) {
		if (!pause)
			eupdate();
		vb.paint();
		if (loader == null)
			gif.setText(0, "gif");
		else
			gif.setText(loader.getProg());
		if (!gif.isSelected() && gif.isEnabled())
			loader = null;
		scale.setText(MainLocale.getLoc(MainLocale.PAGE, "zoom").replace("-", df.format(vb.getCtrl().siz * 100.0)));
	}

	protected abstract void updateChoice();

	private void addListener() {
		back.addActionListener(arg0 -> changePanel(getFront()));

		camres.setLnr(x -> { vb.resetPos();
		});

		copy.addActionListener(arg0 -> {
			EAnimI ei = vb.getEnt();
			if (ei == null || !(ei.anim() instanceof AnimD))
				return;
			AnimD<?, ?> eau = (AnimD<?, ?>) ei.anim();
			ResourceLocation rl = new ResourceLocation(ResourceLocation.LOCAL, "new anim");
			Workspace.validate(Source.ANIM, rl);
			new AnimCE(rl, eau);
			changePanel(new ImgCutEditPage(getThis()));
		});

		jlt.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			changingT = true;
			updateChoice();
			changingT = false;
		});

		jst.addChangeListener(arg0 -> {
			if (jst.getValueIsAdjusting())
				return;
			Timer.p = jst.getValue() * 33 / 100;
		});

		jtl.addChangeListener(arg0 -> {
			if (changingtl || !pause)
				return;
			if (vb.getEnt() != null)
				vb.getEnt().setTime(jtl.getValue());

		});

		jtb.addActionListener(arg0 -> {
			pause = jtb.isSelected();
			enabler(true);
		});

		nex.addActionListener(arg0 -> eupdate());

		png.addActionListener(arg0 -> {
			String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			File f = new File("./img/" + str + ".png");
			BCUWriter.writeImage(vb.getPrev(), f);
		});

		gif.addActionListener(arg0 -> {
			if (gif.isSelected())
				loader = vb.start();
			else
				vb.end(gif);
		});

	}

	private void eupdate() {
		vb.update();
		changingtl = true;
		if (vb.getEnt() != null)
			jtl.setValue(vb.getEnt().ind());
		changingtl = false;
	}

}
