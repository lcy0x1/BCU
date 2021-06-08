package page.battle;

import common.CommonStatic;
import common.battle.*;
import common.battle.entity.AbEntity;
import common.battle.entity.Entity;
import common.util.Data;
import common.util.stage.Replay;
import common.util.stage.Stage;
import io.BCMusic;
import main.Opts;
import page.*;
import page.awt.BBBuilder;
import page.battle.BattleBox.OuterBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleInfoPage extends KeyHandler implements OuterBox {

	private static final long serialVersionUID = 1L;

	public static boolean DEF_LARGE = false;

	public static BattleInfoPage current = null;

	public static void redefine() {
		ComingTable.redefine();
		EntityTable.redefine();
	}

	private final JBTN back = new JBTN(0, "back");
	private final JBTN paus = new JBTN(0, "pause");
	private final JBTN next = new JBTN(0, "nextf");
	private final JBTN rply = new JBTN();
	private final JBTN row = new JBTN();
	private final EntityTable ut = new EntityTable(-1);
	private final ComingTable ct = new ComingTable(this);
	private final EntityTable et = new EntityTable(1);
	private final JScrollPane eup = new JScrollPane(ut);
	private final JScrollPane eep = new JScrollPane(et);
	private final JScrollPane ctp = new JScrollPane(ct);
	private final JLabel ebase = new JLabel();
	private final JLabel ubase = new JLabel();
	private final JLabel timer = new JLabel();
	private final JLabel ecount = new JLabel();
	private final JLabel ucount = new JLabel();
	private final JLabel stream = new JLabel();
	private final JLabel respawn = new JLabel();
	private final JTG jtb = new JTG(0, "larges");
	private final JSlider jsl = new JSlider();
	private final BattleBox bb;
	private final BattleField basis;

	private boolean pause = false, changedBG = false;
	private Replay recd;
	private boolean backClicked = false;

	private int spe = 0, upd = 0;
	private boolean musicChanged = false;

	public BattleInfoPage(Page p, Replay rec, int conf) {
		super(p);
		recd = rec;
		basis = new SBRply(rec);
		if ((conf & 1) == 0)
			bb = BBBuilder.def.getDef(this, basis);
		else
			bb = BBBuilder.def.getRply(this, basis, rec.rl.id, (conf & 4) != 0);
		jtb.setSelected((conf & 2) != 0);
		jtb.setEnabled((conf & 1) == 0);
		ct.setData(basis.sb.st);

		if (recd.rl != null)
			jsl.setMaximum(((SBRply) basis).size());
		ini();
		rply.setText(0, recd.rl == null ? "save" : "start");
		resized();
	}

	protected BattleInfoPage(Page p, SBRply rpl) {
		super(p);
		SBCtrl ctrl = rpl.transform(this);
		bb = BBBuilder.def.getCtrl(this, ctrl);
		pause = true;
		basis = ctrl;
		ct.setData(basis.sb.st);
		ini();
		rply.setText(0, "rply");
		resized();
		current = this;
	}

	protected BattleInfoPage(Page p, Stage st, int star, BasisLU bl, int[] ints) {
		super(p);
		long seed = new Random().nextLong();
		SBCtrl sb = new SBCtrl(this, st, star, bl.copy(), ints, seed);
		bb = BBBuilder.def.getCtrl(this, sb);
		basis = sb;
		ct.setData(basis.sb.st);
		jtb.setSelected(DEF_LARGE);
		ini();
		rply.setText(0, "rply");
		resized();
		current = this;
	}

	@Override
	public void callBack(Object o) {
		changePanel(getFront());
	}

	@Override
	public int getSpeed() {
		return spe;
	}

	@Override
	protected synchronized void keyTyped(KeyEvent e) {
		if (spe > -5 && e.getKeyChar() == ',') {
			spe--;
			bb.reset();
		}
		if (spe < (basis instanceof SBCtrl ? 5 : 7) && e.getKeyChar() == '.') {
			spe++;
			bb.reset();
		}
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == bb)
			bb.click(e.getPoint(), e.getButton());
	}

	@Override
	protected void mouseDragged(MouseEvent e) {
		if (e.getSource() == bb)
			bb.drag(e.getPoint());
	}

	@Override
	protected void mousePressed(MouseEvent e) {
		if (e.getSource() == bb)
			bb.press(e.getPoint());
	}

	@Override
	protected void mouseReleased(MouseEvent e) {
		if (e.getSource() == bb)
			bb.release();
	}

	@Override
	protected void mouseWheel(MouseEvent e) {
		if (e.getSource() == bb)
			bb.wheeled(e.getPoint(), ((MouseWheelEvent) e).getWheelRotation());
	}

	@Override
	protected void renew() {
		backClicked = false;

		if (basis.sb.getEBHP() < basis.sb.st.mush)
			if(basis.sb.st.mush == 0 || basis.sb.st.mush == 100)
				BCMusic.play(basis.sb.st.mus1, basis.sb.st.loop1);
			else {
				if(BCMusic.BG != null)
					BCMusic.BG.stop();

				BCMusic.play(basis.sb.st.mus1, basis.sb.st.loop1);
			}
		else
			BCMusic.play(basis.sb.st.mus0, basis.sb.st.loop0);
	}

	@Override
	protected synchronized void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(jtb, x, y, 2100, 0, 200, 50);
		if (jtb.isSelected()) {
			set(paus, x, y, 700, 0, 200, 50);
			set(rply, x, y, 900, 0, 200, 50);
			set(stream, x, y, 900, 0, 400, 50);
			set(next, x, y, 1100, 0, 200, 50);
			set(row, x, y, 1300, 0, 200, 50);
			set(ebase, x, y, 240, 0, 600, 50);
			set(timer, x, y, 1500, 0, 200, 50);
			set(ubase, x, y, 1740, 0, 200, 50);
			set((Canvas) bb, x, y, 190, 50, 1920, 1200);
			set(ctp, x, y, 0, 0, 0, 0);
			set(eep, x, y, 50, 100, 0, 0);
			set(eup, x, y, 50, 400, 0, 0);
			set(ecount, x, y, 50, 50, 0, 0);
			set(ucount, x, y, 50, 350, 0, 0);
			set(respawn, x, y, 0, 0, 0, 0);
			set(jsl, x, y, 0, 0, 0, 0);
		} else {
			set(ctp, x, y, 50, 850, 1200, 400);
			set(eep, x, y, 50, 100, 600, 700);
			set((Canvas) bb, x, y, 700, 300, 800, 500);
			set(row, x, y , 1300, 200, 200, 50);
			set(paus, x, y, 700, 200, 200, 50);
			set(rply, x, y, 900, 200, 200, 50);
			set(stream, x, y, 900, 200, 400, 50);
			set(next, x, y, 1100, 200, 200, 50);
			set(eup, x, y, 1650, 100, 600, 1100);
			set(ebase, x, y, 700, 250, 400, 50);
			set(timer, x, y, 1100, 250, 200, 50);
			set(ubase, x, y, 1300, 250, 200, 50);
			set(ecount, x, y, 50, 50, 600, 50);
			set(ucount, x, y, 1650, 50, 600, 50);
			set(respawn, x, y, 50, 800, 600, 50);
			set(jsl, x, y, 700, 800, 800, 50);
		}
		ct.setRowHeight(size(x, y, 50));
		et.setRowHeight(size(x, y, 50));
		ut.setRowHeight(size(x, y, 50));
	}

	@Override
	protected synchronized void timer(int t) {
		StageBasis sb = basis.sb;
		if (!pause) {
			upd++;
			if (spe < 0)
				if (upd % (1 - spe) != 0)
					return;
			basis.update();
			updateKey();
			if (spe > 0)
				for (int i = 0; i < Math.pow(2, spe); i++)
					basis.update();
			ct.update(sb.est);
			List<Entity> le = new ArrayList<>();
			List<Entity> lu = new ArrayList<>();
			for (Entity e : sb.le)
				(e.dire == 1 ? le : lu).add(e);
			et.setList(le);
			ut.setList(lu);
			BCMusic.flush(spe < 3, sb.ebase.health <= 0 || sb.ubase.health <= 0);
		}
		if (basis instanceof SBRply && recd.rl != null)
			change((SBRply) basis, b -> jsl.setValue(b.prog()));
		bb.paint();
		AbEntity eba = sb.ebase;
		long h = eba.health;
		long mh = eba.maxH;
		ebase.setText("HP: " + h + "/" + mh + ", " + 10000 * h / mh / 100.0 + "%");
		ubase.setText("HP: " + sb.ubase.health);
		timer.setText(sb.time + "f");
		ecount.setText(sb.entityCount(1) + "/" + sb.st.max);
		ucount.setText(sb.entityCount(-1) + "/" + sb.max_num);
		respawn.setText("respawn timer: " + sb.respawnTime + "f");
		resized();
		if (basis.sb.getEBHP() < basis.sb.st.bgh && basis.sb.st.bg1 != null) {
			if (!changedBG) {
				changedBG = true;
				basis.sb.changeBG(basis.sb.st.bg1);
			}
		} else if (changedBG) {
			changedBG = false;
			basis.sb.changeBG(basis.sb.st.bg);
		}

		if (sb.ebase.health <= 0 || sb.ubase.health <= 0) {
			if(BCMusic.BG != null)
				BCMusic.BG.stop();
			if (sb.ebase.health <= 0)
				CommonStatic.setSE(Data.SE_VICTORY);
			else
				CommonStatic.setSE(Data.SE_DEFEAT);
		} else {
			if (sb.getEBHP() <= sb.st.mush && BCMusic.music != sb.st.mus1)
				if(basis.sb.st.mush == 0 || basis.sb.st.mush == 100)
					BCMusic.play(basis.sb.st.mus1, basis.sb.st.loop1);
				else {
					if(!musicChanged && !backClicked) {
						if(BCMusic.BG != null)
							BCMusic.BG.stop();
						new Thread(() -> {
							try {
								Thread.sleep(Data.MUSIC_DELAY);

								if(backClicked)
									return;

								BCMusic.play(basis.sb.st.mus1, basis.sb.st.loop1);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}).start();

						musicChanged = true;
					}
				}
			else if (BCMusic.music != sb.st.mus0 && sb.getEBHP() > sb.st.mush) {
				if(!musicChanged && !backClicked) {
					if(BCMusic.BG != null)
						BCMusic.BG.stop();
					new Thread(() -> {
						try {
							Thread.sleep(Data.MUSIC_DELAY);

							if(backClicked)
								return;

							BCMusic.play(basis.sb.st.mus0, basis.sb.st.loop0);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}).start();

					musicChanged = true;
				}
			}
		}
		if (bb instanceof BBRecd) {
			BBRecd bbr = (BBRecd) bb;
			stream.setText("frame left: " + bbr.info());
		}
		if(bb.getPainter().dragging)
			bb.getPainter().dragFrame++;
	}

	private void addListeners() {

		jtb.setLnr(x -> {
			remove((Canvas) bb);
			resized();
			add((Canvas) bb);
			DEF_LARGE = jtb.isSelected();
		});

		back.setLnr(x -> {
			backClicked = true;
			BCMusic.stopAll();
			if (bb instanceof BBRecd) {
				BBRecd bbr = (BBRecd) bb;
				if (Opts.conf("Do you want to save this video?")) {
					bbr.end();
					return;
				} else {
					bbr.quit();
				}
			}
			changePanel(getFront());
		});

		rply.setLnr(x -> {
			backClicked = true;
			if (basis instanceof SBCtrl)
				changePanel(new BattleInfoPage(getThis(), ((SBCtrl) basis).getData(), 0));
			if (basis instanceof SBRply)
				if (recd.rl == null)
					changePanel(new RecdSavePage(getThis(), recd));
				else
					changePanel(new BattleInfoPage(this, (SBRply) basis));
		});

		paus.addActionListener(arg0 -> {
			pause = !pause;
			jsl.setEnabled(pause);
		});

		next.addActionListener(arg0 -> {
			pause = false;
			timer(0);
			pause = true;
		});

		row.addActionListener(a -> {
			CommonStatic.getConfig().twoRow = !CommonStatic.getConfig().twoRow;
			row.setText(get(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow"));
		});

		jsl.addChangeListener(e -> {
			if (jsl.getValueIsAdjusting() || isAdj() || !(basis instanceof SBRply))
				return;
			((SBRply) basis).restoreTo(jsl.getValue());
			bb.reset();
		});

	}

	private void ini() {
		add(back);
		add(eup);
		add(eep);
		add(ctp);
		add((Canvas) bb);
		add(paus);
		add(next);
		add(ebase);
		add(ubase);
		add(timer);
		add(ecount);
		add(ucount);
		add(respawn);
		add(jtb);
		add(row);
		row.setText(get(MainLocale.PAGE, CommonStatic.getConfig().twoRow ? "tworow" : "onerow"));
		if (bb instanceof BBRecd)
			add(stream);
		else {
			add(rply);
			if (recd != null && recd.rl != null) {
				add(jsl);
				jsl.setEnabled(pause);
			}
		}
		addListeners();
	}

}
