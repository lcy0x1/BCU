package page.view;

import common.pack.PackData;
import common.pack.Source;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.anim.AnimCE;
import common.util.anim.AnimD;
import common.util.anim.EAnimI;
import common.util.unit.Enemy;
import main.Opts;
import page.JBTN;
import page.Page;
import page.anim.ImgCutEditPage;
import page.info.EnemyInfoPage;
import page.support.AnimLCR;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class EnemyViewPage extends AbViewPage {

	private static final long serialVersionUID = 1L;

	private final JList<Enemy> jlu = new JList<>();
	private final JScrollPane jspu = new JScrollPane(jlu);
	private final JBTN stat = new JBTN(0, "stat");
	private final JLabel source = new JLabel("Source of enemy icon: DB");

	public EnemyViewPage(Page p, Enemy e) {
		this(p, e.getID().pack);
		jlu.setSelectedValue(e, true);
	}

	public EnemyViewPage(Page p, String pac) {
		super(p);
		PackData pack = UserProfile.getPack(pac);
		if(pack != null) {
			jlu.setListData(new Vector<>(pack.enemies.getList()));
		}
		ini();
		resized();
	}

	public EnemyViewPage(Page p) {
		super(p);
		Vector<Enemy> v = new Vector<>();

		for(PackData pack : UserProfile.getAllPacks()) {
			v.addAll(pack.enemies.getList());
		}

		jlu.setListData(v);

		ini();
		resized();
	}

	@Override
	protected void resized(int x, int y) {
		super.resized(x, y);
		set(jspu, x, y, 50, 100, 300, 1100);
		set(stat, x, y, 400, 1000, 300, 50);
		set(source, x, y, 0, 50, 600, 50);
		jlu.setFixedCellHeight(size(x, y, 50));
	}

	@Override
	protected void updateChoice() {
		Enemy u = jlu.getSelectedValue();
		if (u == null)
			return;
		setAnim(u.anim);
	}

	private void addListeners() {

		jlu.addListSelectionListener(arg0 -> {
			if (arg0.getValueIsAdjusting())
				return;
			updateChoice();
		});

		stat.addActionListener(e -> {
			Enemy ene = jlu.getSelectedValue();
			if (ene == null)
				return;

			ListModel<Enemy> enes = jlu.getModel();
			List<Enemy> lis = new ArrayList<>();
			for (int i = 0;i < enes.getSize(); i++)
				lis.add(enes.getElementAt(i));
			ENode n = ENode.getList(lis,ene);

			changePanel(new EnemyInfoPage(getThis(), n));
		});

		ActionListener[] listeners = copy.getActionListeners();

		for(ActionListener listener : listeners)
			copy.removeActionListener(listener);

		copy.addActionListener(e -> {
			{
				Enemy ene = jlu.getSelectedValue();

				if(ene != null) {
					PackData pack = ene.getCont();

					if(pack != null)
						if(pack instanceof PackData.DefPack)
							copyAnim();
						else if(pack instanceof PackData.UserPack) {
							if(((PackData.UserPack) pack).editable || ((PackData.UserPack) pack).desc.allowAnim)
								copyAnim();
							else {
								String pass = Opts.read("Enter the password : ");

								if(pass == null)
									return;

								if(((Source.ZipSource) ((PackData.UserPack) pack).source).zip.matchKey(pass)) {
									copyAnim();
								} else {
									Opts.pop("You typed incorrect password", "Incorrect password");
								}
							}
						}
				}
			}
		});
	}

	private void ini() {
		preini();
		add(jspu);
		add(stat);
		add(source);
		jlu.setCellRenderer(new AnimLCR());

		addListeners();

	}

	private void copyAnim() {
		EAnimI ei = vb.getEnt();
		if (ei == null || !(ei.anim() instanceof AnimD))
			return;
		AnimD<?, ?> eau = (AnimD<?, ?>) ei.anim();
		Source.ResourceLocation rl = new Source.ResourceLocation(Source.ResourceLocation.LOCAL, "new anim");
		Source.Workspace.validate(Source.ANIM, rl);
		new AnimCE(rl, eau);
		changePanel(new ImgCutEditPage(getThis()));
	}
}
