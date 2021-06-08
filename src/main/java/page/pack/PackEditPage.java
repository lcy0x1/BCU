package page.pack;

import common.CommonStatic;
import common.battle.data.CustomEnemy;
import common.io.PackLoader;
import common.pack.Context.ErrType;
import common.pack.PackData.UserPack;
import common.pack.Source;
import common.pack.Source.Workspace;
import common.pack.Source.ZipSource;
import common.pack.UserProfile;
import common.util.Data;
import common.util.anim.AnimCE;
import common.util.stage.MapColc;
import common.util.stage.StageMap;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import main.MainBCU;
import main.Opts;
import page.*;
import page.info.StageViewPage;
import page.info.edit.EnemyEditPage;
import page.info.edit.StageEditPage;
import page.support.AnimLCR;
import page.support.RLFIM;
import page.view.BGViewPage;
import page.view.CastleViewPage;
import page.view.EnemyViewPage;
import page.view.MusicPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class PackEditPage extends Page {

	private static final long serialVersionUID = 1L;

	private final JBTN back = new JBTN(0, "back");
	private final Vector<UserPack> vpack = new Vector<>(UserProfile.getUserPacks());
	private final JList<UserPack> jlp = new JList<>(vpack);
	private final JScrollPane jspp = new JScrollPane(jlp);
	private final JList<Enemy> jle = new JList<>();
	private final JScrollPane jspe = new JScrollPane(jle);
	private final JList<AnimCE> jld = new JList<>(new Vector<>(AnimCE.map().values()));
	private final JScrollPane jspd = new JScrollPane(jld);
	private final RLFIM<StageMap> jls = new RLFIM<>(() -> this.changing = true, () -> changing = false, this::setMap,
			StageMap::new);
	private final JScrollPane jsps = new JScrollPane(jls);
	private final JList<UserPack> jlr = new JList<>();
	private final JScrollPane jspr = new JScrollPane(jlr);
	private final JList<UserPack> jlt = new JList<>(vpack);
	private final JScrollPane jspt = new JScrollPane(jlt);

	private final JBTN addp = new JBTN(0, "add");
	private final JBTN remp = new JBTN(0, "rem");
	private final JBTN adde = new JBTN(0, "add");
	private final JBTN reme = new JBTN(0, "rem");
	private final JBTN adds = new JBTN(0, "add");
	private final JBTN rems = new JBTN(0, "rem");
	private final JBTN addr = new JBTN(0, "add");
	private final JBTN remr = new JBTN(0, "rem");
	private final JBTN edit = new JBTN(0, "edit");
	private final JBTN sdiy = new JBTN(0, "sdiy");
	private final JBTN vene = new JBTN(0, "vene");
	private final JBTN extr = new JBTN(0, "extr");
	private final JBTN vcas = new JBTN(0, "vcas");
	private final JBTN vbgr = new JBTN(0, "vbgr");
	private final JBTN vrcg = new JBTN(0, "recg");
	private final JBTN vrlr = new JBTN(0, "relr");
	private final JBTN cunt = new JBTN(0, "cunt");
	private final JBTN tdiy = new JBTN(0, "ctrt");
	private final JBTN ener = new JBTN(0, "ener");
	private final JBTN vmsc = new JBTN(0, "vmsc");
	private final JBTN unpk = new JBTN(0, "unpack");
	private final JBTN recd = new JBTN(0, "replay");
	private final JTF jtfp = new JTF();
	private final JTF jtfe = new JTF();
	private final JTF jtfs = new JTF();

	private final JL lbp = new JL(0, "pack");
	private final JL lbe = new JL(0, "enemy");
	private final JL lbd = new JL(0, "seleanim");
	private final JL lbs = new JL(0, "stage");
	private final JL lbr = new JL(0, "parent");
	private final JL lbt = new JL(0, "selepar");
	private final JLabel pid = new JLabel();
	private final JLabel pauth = new JLabel();
	private final JLabel animall = new JLabel();

	private UserPack pac;
	private Enemy ene;
	private StageMap sm;
	private boolean changing = false;

	public PackEditPage(Page p) {
		super(p);

		ini();
	}

	@Override
	protected void renew() {
		setPack(pac);
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);

		int w = 50, dw = 150;

		set(lbp, x, y, w, 100, 400, 50);
		set(jspp, x, y, w, 150, 400, 600);
		set(addp, x, y, w, 800, 200, 50);
		set(remp, x, y, w + 200, 800, 200, 50);
		set(jtfp, x, y, w, 850, 400, 50);
		set(extr, x, y, w, 950, 200, 50);
		set(unpk, x, y, w + 200, 950, 200, 50);
		set(pid, x, y, w, 1050, 400, 50);
		set(pauth, x, y, w, 1100, 400, 50);
		set(animall, x, y, w, 1150, 400, 50);

		w += 450;

		set(lbe, x, y, w, 100, 300, 50);
		set(jspe, x, y, w, 150, 300, 600);
		set(adde, x, y, w, 800, 150, 50);
		set(reme, x, y, w + dw, 800, 150, 50);
		set(jtfe, x, y, w, 850, 300, 50);
		set(edit, x, y, w, 950, 300, 50);
		set(vene, x, y, w, 1050, 300, 50);
		set(ener, x, y, w, 1150, 300, 50);

		w += 300;

		set(lbd, x, y, w, 100, 300, 50);
		set(jspd, x, y, w, 150, 300, 600);

		w += 50;

		set(vbgr, x, y, w, 850, 250, 50);
		set(vcas, x, y, w, 950, 250, 50);
		set(vrcg, x, y, w, 1050, 250, 50);
		set(vrlr, x, y, w, 1150, 250, 50);

		w += 300;

		set(lbs, x, y, w, 100, 300, 50);
		set(jsps, x, y, w, 150, 300, 600);
		set(adds, x, y, w, 800, 150, 50);
		set(rems, x, y, w + dw, 800, 150, 50);
		set(jtfs, x, y, w, 850, 300, 50);
		set(sdiy, x, y, w, 950, 300, 50);
		set(cunt, x, y, w, 1050, 300, 50);
		set(vmsc, x, y, w, 1150, 300, 50);

		w += 350;

		set(lbr, x, y, w, 100, 350, 50);
		set(jspr, x, y, w, 150, 350, 600);
		set(addr, x, y, w, 800, 175, 50);
		set(remr, x, y, w + 175, 800, 175, 50);

		set(recd, x, y, w, 950, 300, 50);
		set(tdiy, x, y, w, 1050, 300, 50);

		w += 350;

		set(lbt, x, y, w, 100, 350, 50);
		set(jspt, x, y, w, 150, 350, 600);
	}

	private void addListeners() {

		back.setLnr(x -> changePanel(getFront()));

		recd.setLnr(x -> changePanel(new RecdPackPage(this, pac)));

		vcas.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new CastleEditPage(getThis(), pac));
			else if (pac != null)
				changePanel(new CastleViewPage(getThis(), pac.castles));
		});

		vbgr.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new BGEditPage(getThis(), pac));
			else if (pac != null)
				changePanel(new BGViewPage(getThis(), pac.getSID()));
		});

		vrcg.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new CGLREditPage(getThis(), pac));
			else
				changePanel(new CharaGroupPage(getThis(), pac, true));
		});

		vrlr.addActionListener(arg0 -> {
			if (pac != null && pac.editable)
				changePanel(new CGLREditPage(getThis(), pac));
			else
				changePanel(new LvRestrictPage(getThis(), pac, true));
		});

		jld.addListSelectionListener(arg0 -> {
			if (jld.getValueIsAdjusting())
				return;
			adde.setEnabled(pac != null && jld.getSelectedValue() != null && pac.editable);
		});

	}

	private void addListeners$1() {

		addp.addActionListener(arg0 -> {
			changing = true;
			String str = Workspace.validateWorkspace(Workspace.generatePackID());
			pac = Data.err(() -> UserProfile.initJsonPack(str));
			pac.desc.author = MainBCU.author;
			vpack.add(pac);
			jlp.setListData(vpack);
			jlt.setListData(vpack);
			jlp.setSelectedValue(pac, true);
			setPack(pac);
			changing = false;
		});

		remp.addActionListener(arg0 -> {
			if (!Opts.conf())
				return;
			changing = true;
			int ind = jlp.getSelectedIndex();
			UserProfile.unloadPack(pac);
			UserProfile.remove(pac);
			pac.delete();
			vpack.remove(pac);
			jlp.setListData(vpack);
			jlt.setListData(vpack);
			if (ind > 0)
				ind--;
			jlp.setSelectedIndex(ind);
			setPack(jlp.getSelectedValue());
			changing = false;
		});

		jlp.addListSelectionListener(arg0 -> {
			if (changing || jlp.getValueIsAdjusting())
				return;
			changing = true;
			setPack(jlp.getSelectedValue());
			changing = false;
		});

		jtfp.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent fe) {
				String str = jtfp.getText().trim();
				if (pac.desc.name != null && pac.desc.name.equals(str))
					return;
				pac.desc.name = str;
			}

		});

		extr.setLnr(x -> {
			if (pac.editable) {
				Object[] result = Opts.showTextCheck("Password", "Decide the password of pack : ", pac.desc.allowAnim);

				if (result == null)
					return;

				String password = (String) result[0];
				String parentPassword = (String) result[2];

				pac.desc.allowAnim = (boolean) result[1];

				CommonStatic.ctx.noticeErr(() -> ((Workspace) pac.source).export(pac, password, parentPassword, (d) -> {
				}), ErrType.WARN, "failed to export pack");
			}
		});

		unpk.setLnr(x -> {
			String pass = Opts.read("Enter the password : ");

			if(pass == null)
				return;

			if(((Source.ZipSource) pac.source).zip.matchKey(pass)) {
				Data.err(() -> ((ZipSource) pac.source).unzip(pass, (d) -> {
				}));
				unpk.setEnabled(false);
				extr.setEnabled(true);
			} else {
				Opts.pop("You typed incorrect password", "Incorrect password");
			}
		});

	}

	private void addListeners$2() {

		jle.addListSelectionListener(e -> {
			if (changing || jle.getValueIsAdjusting())
				return;
			changing = true;
			setEnemy(jle.getSelectedValue());
			changing = false;
		});

		adde.addActionListener(arg0 -> {
			changing = true;
			CustomEnemy ce = new CustomEnemy();
			AnimCE anim = jld.getSelectedValue();
			ce.limit = Math.abs(anim.mamodel.parts[0][6] * 6);
			Enemy e = new Enemy(pac.getNextID(Enemy.class), anim, ce);
			pac.enemies.add(e);
			jle.setListData(pac.enemies.toRawArray());
			jle.setSelectedValue(e, true);
			setEnemy(e);
			changing = false;
		});

		reme.addActionListener(arg0 -> {
				if (!Opts.conf())
				return;
			changing = true;
			int ind = jle.getSelectedIndex();
			pac.enemies.remove(ene);
			jle.setListData(pac.enemies.toRawArray());
			if (ind >= 0)
				ind--;
			jle.setSelectedIndex(ind);
			setEnemy(jle.getSelectedValue());
			changing = false;
		});

		edit.setLnr(a -> {
			UserPack pack = UserProfile.getUserPack(ene.id.pack);

			if(pack == null)
				return;

			changePanel(new EnemyEditPage(getThis(), ene, pack));
		});

		jtfe.setLnr(e -> ene.name = jtfe.getText().trim());

		vene.setLnr(() -> new EnemyViewPage(getThis(), pac.getSID()));

		ener.setLnr(() -> new EREditPage(getThis(), pac));

	}

	private void addListeners$3() {

		sdiy.addActionListener(arg0 -> {
			if (pac.editable)
				changePanel(new StageEditPage(getThis(), pac.mc, pac));
			else {
				List<MapColc> lmc = Arrays.asList(new MapColc[] { pac.mc });
				changePanel(new StageViewPage(getThis(), lmc));
			}
		});

		cunt.addActionListener(arg0 -> changePanel(new UnitManagePage(getThis(), pac)));

		tdiy.addActionListener(arg0 -> {
			changePanel(new TraitEditPage(getThis(), pac));
		});

		vmsc.setLnr(() -> pac.editable ? new MusicEditPage(getThis(), pac)
				: new MusicPage(getThis(), pac.musics.getList()));

		jls.addListSelectionListener(arg0 -> {
			if (changing || jls.getValueIsAdjusting())
				return;
			changing = true;
			setMap(jls.getSelectedValue());
			changing = false;
		});

		adds.setLnr(jls::addItem);

		rems.setLnr(jls::deleteItem);

		jtfs.setLnr(x -> {
			if (sm != null)
				sm.name = jtfs.getText().trim();
		});

	}

	private void addListeners$4() {

		jlr.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			changing = true;
			setRely(jlr.getSelectedValue());
			changing = false;
		});

		jlt.addListSelectionListener(arg0 -> {
			if (changing || arg0.getValueIsAdjusting())
				return;
			checkAddr();
		});

		addr.addActionListener(arg0 -> {
			changing = true;
			UserPack rel = jlt.getSelectedValue();

			//Need to cache parented pack for situation when parent pack of parent pack has password
			ArrayList<String> passedParents = new ArrayList<>();

			if(rel.desc.parentPassword != null) {
				String pass = Opts.read("Enter the password for "+rel.getSID()+" : ");

				if(pass == null) {
					changing = false;
					return;
				}

				byte[] md5 = PackLoader.getMD5(pass.getBytes(StandardCharsets.UTF_8), 16);

				if(!Arrays.equals(rel.desc.parentPassword, md5)) {
					Opts.pop("You typed incorrect password", "Incorrect password");
					changing = false;
					return;
				}
			}

			pac.desc.dependency.add(rel.getSID());

			passedParents.add(rel.getSID());

			for (String id : rel.desc.dependency) {
				if (!pac.desc.dependency.contains(id)) {
					UserPack pack = UserProfile.getUserPack(id);

					if(pack == null) {
						Opts.pop("Can't get parent pack ["+id+".pack.bcuzip] from data, aborting parent pack adding", "Can't find parent pack");
						pac.desc.dependency.removeAll(passedParents);
						changing = false;
						return;
					}

					if(pack.editable) {
						pac.desc.dependency.add(id);
						passedParents.add(id);
					} else if(pack.desc.parentPassword != null) {
						String pass = Opts.read("Enter the password for "+id+" : ");

						if(pass == null) {
							changing = false;
							pac.desc.dependency.removeAll(passedParents);
							return;
						}

						byte[] md5 = PackLoader.getMD5(pass.getBytes(StandardCharsets.UTF_8), 16);

						if(!Arrays.equals(pack.desc.parentPassword, md5)) {
							Opts.pop("You typed incorrect password", "Incorrect password");
							pac.desc.dependency.removeAll(passedParents);
							changing = false;
							return;
						}

						pac.desc.dependency.add(id);
						passedParents.add(id);
					} else {
						pac.desc.dependency.add(id);
						passedParents.add(id);
					}
				}
			}
			updateJlr();
			jlr.setSelectedValue(rel, true);
			setRely(rel);
			changing = false;
		});

		remr.addActionListener(arg0 -> {
			changing = true;
			int ind = jlr.getSelectedIndex() - 1;
			UserPack rel = jlr.getSelectedValue();
			if (pac.relyOn(rel.getSID())) {
				StringBuilder sb = new StringBuilder();
				List<String> list = pac.foreignList(rel.getSID());
				for (String str : list)
					sb.append(str).append("\n");
				Opts.pop(sb.toString(), "list of dependency");
			} else {
				pac.desc.dependency.remove(rel.getSID());
				updateJlr();
				jlr.setSelectedIndex(ind);
				setRely(jlr.getSelectedValue());
			}
			changing = false;
		});

	}

	private void checkAddr() {
		if (pac == null) {
			addr.setEnabled(false);
			return;
		}
		UserPack rel = jlt.getSelectedValue();
		boolean b = pac.editable;
		b &= rel != null && !pac.desc.dependency.contains(rel.getSID());
		b &= rel != pac;
		if (b)
			for (String id : rel.desc.dependency)
				if (id.equals(pac.getSID()))
					b = false;
		addr.setEnabled(b);
	}

	private void ini() {
		add(back);
		add(jspp);
		add(jspe);
		add(jspd);
		add(addp);
		add(remp);
		add(jtfp);
		add(adde);
		add(reme);
		add(jtfe);
		add(edit);
		add(sdiy);
		add(jsps);
		add(adds);
		add(rems);
		add(jtfs);
		add(extr);
		add(jspr);
		add(jspt);
		add(addr);
		add(remr);
		add(vene);
		add(lbp);
		add(lbe);
		add(lbd);
		add(lbs);
		add(lbr);
		add(lbt);
		add(cunt);
		add(tdiy);
		add(vcas);
		add(vrcg);
		add(vrlr);
		add(vbgr);
		add(ener);
		add(vmsc);
		add(unpk);
		add(recd);
		add(pid);
		add(pauth);
		add(animall);
		jle.setCellRenderer(new AnimLCR());
		jld.setCellRenderer(new AnimLCR());
		setPack(null);
		addListeners();
		addListeners$1();
		addListeners$2();
		addListeners$3();
		addListeners$4();
	}

	private void setEnemy(Enemy e) {
		ene = e;
		boolean b = e != null && pac.editable;
		edit.setEnabled(e != null && e.de instanceof CustomEnemy);
		jtfe.setEnabled(b);
		reme.setEnabled(b);
		if (b) {
			jtfe.setText(e.name);
			boolean cont = false;

			for(EneRand rand : pac.randEnemies.getList()) {
				if(rand != null) {
					cont = cont || rand.contains(e.id, rand.id);
				}
			}

			reme.setEnabled(e.findApp(pac.mc).size() == 0 && !cont);
		}
	}

	private void setMap(StageMap map) {
		sm = map;
		rems.setEnabled(sm != null && pac.editable);
		jtfs.setEnabled(sm != null && pac.editable);
		if (sm != null)
			jtfs.setText(sm.name);
	}

	private void setPack(UserPack pack) {
		pac = pack;
		boolean b = pac != null && pac.editable;
		remp.setEnabled(pac != null && canBeDeleted(pac));

		if(pac != null)
			if(!canBeDeleted(pac)) {
				remp.setToolTipText(Page.get(MainLocale.PAGE, "packused"));
			} else {
				remp.setToolTipText(null);
			}

		jtfp.setEnabled(b);
		adde.setEnabled(b && jld.getSelectedValue() != null);
		adds.setEnabled(b);
		extr.setEnabled(pac != null);
		vcas.setEnabled(pac != null);
		vbgr.setEnabled(pac != null);
		vene.setEnabled(pac != null);
		vmsc.setEnabled(pac != null);
		recd.setEnabled(pac != null);
		boolean canUnpack = pac != null && !pac.editable;
		boolean canExport = pac != null && pac.editable;
		unpk.setEnabled(canUnpack);
		extr.setEnabled(canExport);
		if (b)
			jtfp.setText(pack.desc.name);
		if (pac == null) {
			jle.setListData(new Enemy[0]);
			jlr.setListData(new UserPack[0]);
		} else {
			jle.setListData(pac.enemies.toRawArray());
			jle.clearSelection();
			updateJlr();
			jlr.clearSelection();
		}
		checkAddr();
		boolean b0 = pac != null;
		sdiy.setEnabled(b0);
		tdiy.setEnabled(b0);
		if (b0) {
			jls.setListData(pac.mc, pac.mc.maps);
			jls.clearSelection();
		} else
			jls.setListData(null, null);
		setRely(null);
		setMap(null);
		setEnemy(null);
		pid.setVisible(pack != null);
		pauth.setVisible(pack != null);
		animall.setVisible(pack == null || !pack.editable);

		if(pack != null) {
			pid.setText("ID : "+pack.desc.id);
			if(pack.desc.author == null || pack.desc.author.isEmpty()) {
				pauth.setText("Author : (None)");
			} else {
				pauth.setText("Author : "+pack.desc.author);
			}
			if(pack.desc.allowAnim)
				animall.setText("Copy Anim : Allowed");
			else
				animall.setText("Copy Anim : Not Allowed");
		}
	}

	private void setRely(UserPack rel) {
		if (pac == null || rel == null) {
			remr.setEnabled(false);
			return;
		}
		boolean re = pac.relyOn(rel.getSID());
		remr.setText(0, re ? "rema" : "rem");
		remr.setForeground(re ? Color.RED : Color.BLACK);
		remr.setEnabled(true);
	}

	private void updateJlr() {
		UserPack[] rel = new UserPack[pac.desc.dependency.size()];
		for (int i = 0; i < pac.desc.dependency.size(); i++)
			rel[i] = UserProfile.getUserPack(pac.desc.dependency.get(i));
		jlr.setListData(rel);
	}

	private boolean canBeDeleted(UserPack pack) {
		for(UserPack p : UserProfile.getUserPacks()) {
			if(p.getSID().equals(pack.getSID()))
				continue;

			if(p.desc.dependency.contains(pack.getSID()))
				return false;
		}

		return true;
	}
}
