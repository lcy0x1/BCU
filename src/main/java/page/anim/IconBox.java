package page.anim;

import common.CommonStatic;
import common.CommonStatic.BCAuxAssets;
import common.system.fake.FakeGraphics;
import common.system.fake.FakeImage;
import page.view.ViewBox;
import utilpc.PP;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static page.anim.IconBox.IBConf.*;

public interface IconBox extends ViewBox {

	class IBConf {

		public static final int[] line = new int[4];
		public static int mode = 0, type = 0, glow = 0;

	}

	class IBCtrl extends ViewBox.Controller {

		protected boolean drag;

		@Override
		public synchronized void mouseDragged(MouseEvent e) {
			if (!drag) {
				super.mouseDragged(e);
				return;
			}
			Point t = e.getPoint();
			int modifier = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			if ((e.getModifiers() & modifier) > 0) {
				line[2] += t.x - p.x;
				line[3] += t.y - p.y;
			} else {
				line[0] += t.x - p.x;
				line[1] += t.y - p.y;
			}
			p = t;
		}

		@Override
		public synchronized void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (cont.isBlank() && !new PP(p).out(line, 1, -5))
				drag = true;
		}

		@Override
		public synchronized void mouseReleased(MouseEvent e) {
			drag = false;
			super.mouseReleased(e);
		}

		public void postdraw(FakeGraphics gra) {
			if (cont.isBlank()) {
				BCAuxAssets aux = CommonStatic.getBCAssets();
				int t = mode == 0 ? type == 1 ? 1 : 0 : 3;
				FakeImage bimg = aux.ico[mode][t].getImg();
				int bw = bimg.getWidth();
				int bh = bimg.getHeight();
				double r = Math.min(1.0 * line[2] / bw, 1.0 * line[3] / bh);
				gra.setColor(FakeGraphics.BLACK);
				gra.drawRect(line[0] - 1, line[1] - 1, line[2] + 1, line[3] + 1);
				if (glow == 1) {
					gra.setComposite(FakeGraphics.BLEND, 256, 1);
					bimg = aux.ico[0][4].getImg();
					gra.drawImage(bimg, line[0], line[1], (int) (bw * r), (int) (bh * r));
					gra.setComposite(FakeGraphics.DEF, 0, 0);
				}
				if (mode == 0 && type > 1) {
					bimg = aux.ico[0][5].getImg();
					gra.drawImage(bimg, line[0], line[1], (int) (bw * r), (int) (bh * r));
				} else {
					bimg = aux.ico[mode][t].getImg();
					gra.drawImage(bimg, line[0], line[1], (int) (bw * r), (int) (bh * r));
				}

			}
		}

		public void predraw(FakeGraphics gra) {
			if (cont.isBlank()) {
				BCAuxAssets aux = CommonStatic.getBCAssets();
				if (mode == 0 && type > 1 || mode == 1) {
					FakeImage bimg = aux.ico[mode][type].getImg();
					int bw = bimg.getWidth();
					int bh = bimg.getHeight();
					double r = Math.min(1.0 * line[2] / bw, 1.0 * line[3] / bh);
					gra.drawImage(bimg, line[0], line[1], bw * r, bh * r);
					if (glow == 1) {
						gra.setComposite(FakeGraphics.BLEND, 256, -1);
						bimg = aux.ico[0][4].getImg();
						gra.drawImage(bimg, line[0], line[1], bw * r, bh * r);
						gra.setComposite(FakeGraphics.DEF, 0, 0);
					}
				}
			}
		}

	}

	void changeType();

	BufferedImage getClip();

	@Override
	IBCtrl getCtrl();

	void setBlank(boolean selected);

}