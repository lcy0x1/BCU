package page.view;

import common.system.P;
import common.util.anim.EAnimI;
import page.JTG;
import page.RetFunc;
import page.awt.RecdThread;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Queue;

public interface ViewBox {

	class Conf {

		public static boolean white;

	}

	class Controller {

		public final P ori = new P(0, 0);
		public double siz = 0.5;
		protected Point p = null;
		protected ViewBox cont;

		public synchronized void mouseDragged(MouseEvent e) {
			if (p == null)
				return;
			ori.x += p.x - e.getX();
			ori.y += p.y - e.getY();
			p = e.getPoint();
		}

		public synchronized void mousePressed(MouseEvent e) {
			p = e.getPoint();
		}

		public synchronized void mouseReleased(MouseEvent e) {
			p = null;
		}

		public void resize(double pow) {
			siz *= pow;
		}

		public void setCont(ViewBox vb) {
			cont = vb;
		}

		public void resetPos() {
			ori.x = 0;
			ori.y = 0;
		}
	}

	class Loader implements RetFunc {

		public final RecdThread thr;

		private JTG jtb;

		public Loader(Queue<BufferedImage> list) {
			thr = RecdThread.getIns(this, list, null, RecdThread.GIF);
		}

		@Override
		public void callBack(Object o) {
			jtb.setEnabled(true);
		}

		public void finish(JTG btn) {
			jtb = btn;
			jtb.setEnabled(false);
		}

		public String getProg() {
			return "remain: " + thr.remain();
		}

		public void start() {
			thr.start();
		}

	}

	interface VBExporter {

		void end(JTG btn);

		BufferedImage getPrev();

		Loader start();

	}

	Color c0 = new Color(70, 140, 160), c1 = new Color(85, 185, 205);

	default void end(JTG btn) {
		if (getExp() != null)
			getExp().end(btn);
	}

	Controller getCtrl();

	EAnimI getEnt();

	VBExporter getExp();

	default BufferedImage getPrev() {
		if (getExp() != null)
			return getExp().getPrev();
		return null;
	}

	boolean isBlank();

	default void mouseDragged(MouseEvent e) {
		getCtrl().mouseDragged(e);
	}

	default void mousePressed(MouseEvent e) {
		getCtrl().mousePressed(e);
	}

	default void mouseReleased(MouseEvent e) {
		getCtrl().mouseReleased(e);
	}

	void paint();

	default void resetPos() { getCtrl().resetPos(); }

	default void resize(double pow) {
		getCtrl().resize(pow);
	}

	void setEntity(EAnimI ieAnim);

	default Loader start() {
		if (getExp() != null)
			return getExp().start();
		return null;
	}

	void update();
}