package page.info.filter;

import common.util.unit.Trait;
import main.MainBCU;
import page.JTG;
import page.Page;
import utilpc.Theme;
import utilpc.UtilPC;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import static utilpc.Interpret.*;

class AttList extends JList<String> {

	private static final long serialVersionUID = 1L;

	protected static void btnDealer(int x, int y, JTG[][] btns, JTG[] orop, int... ord) {
		int h = 0, or = 0;
		for (JTG[] sub : btns) {
			int w = 0, j = 0;
			if (ord[or] >= 0)
				Page.set(orop[ord[or]], x, y, 0, h, 200, 50);
			while (j < sub.length) {
				if (!sub[j].getText().equals("(null)")) {
					Page.set(sub[j], x, y, 250 + w % 10 * 175, h + w / 10 * 50, 175, 50);
					w++;
				}
				j++;
			}
			h += (w - 1) / 10 * 50 + 50;
			or++;
		}

	}

	protected AttList() {
		if (MainBCU.nimbus) {
			setSelectionBackground(MainBCU.light ? Theme.LIGHT.NIMBUS_SELECT_BG : Theme.DARK.NIMBUS_SELECT_BG);
		}
	}

	protected AttList(int type, int para) {
		if (MainBCU.nimbus) {
			setSelectionBackground(MainBCU.light ? Theme.LIGHT.NIMBUS_SELECT_BG : Theme.DARK.NIMBUS_SELECT_BG);
		}
		setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
				JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
				BufferedImage v;
				if (type == -1) {
					v = ind < para ? UtilPC.getIcon(0, EABIIND[ind]) : UtilPC.getIcon(1, ind - para);
				} else if (type == 0) {
					v = ind < SABIS.length ? UtilPC.getIcon(0, ind) : UtilPC.getIcon(1, ind - SABIS.length);
				} else
					v = UtilPC.getIcon(type, ind);
				if (v == null)
					return jl;
				jl.setIcon(new ImageIcon(v));
				return jl;
			}
		});
	}

	protected void setIcons(List<Trait> diyTraits) {
		setCellRenderer(new DefaultListCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
				JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
				Trait trait = diyTraits.get(ind);
				if (trait.BCTrait)
					jl.setIcon(UtilPC.createIcon(3, ind));
				else if (trait.icon != null)
					jl.setIcon(trait.obtainIcon());
				return jl;
			}
		});
	}

}