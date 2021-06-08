package page.anim;

import common.CommonStatic.EditLink;
import common.util.anim.AnimCE;
import io.BCUWriter;
import page.JBTN;
import page.Page;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditHead extends Page implements EditLink {

	private static final long serialVersionUID = 1L;

	private final JBTN undo = new JBTN(0, "undo");
	private final JBTN save = new JBTN(0, "save");
	private final JBTN view = new JBTN(0, "vdiy");
	private final JBTN icut = new JBTN(0, "caic");
	private final JBTN mmdl = new JBTN(0, "camm");
	private final JBTN manm = new JBTN(0, "cama");

	protected AnimCE focus;

	private DIYViewPage p0;
	private ImgCutEditPage p1;
	private MaModelEditPage p2;
	private MaAnimEditPage p3;
	private int val;
	private Page cur;
	private boolean changing = false;

	protected AnimCE anim;

	public EditHead(Page p, int v) {
		super(p.getFront());
		val = v;
		cur = p;
		focus = null;
		if (v == 0)
			p0 = (DIYViewPage) p;
		else if (v == 1)
			p1 = (ImgCutEditPage) p;
		else if (v == 2)
			p2 = (MaModelEditPage) p;
		else if (v == 3)
			p3 = (MaAnimEditPage) p;
		ini();
	}

	@Override
	public void review() {
		undo.setEnabled(anim != null && anim.history.size() > 1);
		cur.callBack("review");
		if (anim != null)
			undo.setToolTipText(anim.getUndo());
	}

	public void setAnim(AnimCE da) {
		if (changing)
			return;
		anim = da;
		if (anim != null)
			anim.link = this;
		review();
	}

	@Override
	protected void resized(int x, int y) {
		set(undo, x, y, 0, 0, 200, 50);
		set(save, x, y, 250, 0, 200, 50);
		set(view, x, y, 550, 0, 200, 50);
		set(icut, x, y, 800, 0, 200, 50);
		set(mmdl, x, y, 1050, 0, 200, 50);
		set(manm, x, y, 1300, 0, 200, 50);
	}

	private void addListeners() {
		EditHead thi = this;

		view.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (val == 0)
					return;
				changing = true;
				cur.remove(thi);
				if (p0 == null)
					p0 = new DIYViewPage(getFront(), thi);
				changePanel(cur = p0);
				cur.add(thi);
				((AbEditPage) cur).setSelection(anim);
				val = 0;
				changing = false;
			}

		});

		icut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (val == 1)
					return;
				changing = true;
				cur.remove(thi);
				if (p1 == null)
					p1 = new ImgCutEditPage(getFront(), thi);
				changePanel(cur = p1);
				cur.add(thi);
				((AbEditPage) cur).setSelection(anim);
				val = 1;
				changing = false;
			}

		});

		mmdl.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (val == 2)
					return;
				changing = true;
				cur.remove(thi);
				if (p2 == null)
					p2 = new MaModelEditPage(getFront(), thi);
				changePanel(cur = p2);
				cur.add(thi);
				((AbEditPage) cur).setSelection(anim);
				val = 2;
				changing = false;
			}

		});

		manm.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (val == 3)
					return;
				changing = true;
				cur.remove(thi);
				if (p3 == null)
					p3 = new MaAnimEditPage(getFront(), thi);
				changePanel(cur = p3);
				cur.add(thi);
				((AbEditPage) cur).setSelection(anim);
				val = 3;
				changing = false;
			}

		});

		save.setLnr((e) -> BCUWriter.writeData());

		undo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				anim.restore();
				review();
				((AbEditPage) cur).setSelection(anim);
			}

		});

	}

	private void ini() {
		add(view);
		add(icut);
		add(mmdl);
		add(manm);
		add(save);
		add(undo);
		addListeners();
	}

}
