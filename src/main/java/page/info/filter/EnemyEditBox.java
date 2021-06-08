package page.info.filter;

import common.battle.data.CustomEnemy;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.unit.Trait;
import page.Page;

import javax.swing.*;
import java.util.*;

import static utilpc.Interpret.*;

public class EnemyEditBox extends Page {

	private static final long serialVersionUID = 1L;

	private final boolean editable;

	private final Vector<String> vt = new Vector<>();
	private final Vector<String> va = new Vector<>();
	private final AttList abis = new AttList(-1, EABIIND.length);
	private final AttList trait = new AttList();
	private final JScrollPane jt;
	private final JScrollPane jab = new JScrollPane(abis);

	private boolean changing = false;
	private final List<Trait> traitList;
	private final CustomEnemy ce;

	public EnemyEditBox(Page p, UserPack pack, CustomEnemy cen) {
		super(p);
		editable = pack.editable;
		traitList = new ArrayList<>(UserProfile.getBCData().traits.getList().subList(0,9));
		traitList.addAll(pack.traits.getList());
		for (UserPack pacc : UserProfile.getUserPacks())
			if (pack.desc.dependency.contains(pacc.desc.id))
				traitList.addAll(pacc.traits.getList());
		trait.setIcons(traitList);
		jt = new JScrollPane(trait);
		ce = cen;
		ini();
	}

	public void setData(int[] vals, ArrayList<Trait> cts) {
		changing = true;
		for (int k = 0; k < traitList.size(); k++)
			if (cts.contains(traitList.get(k)))
				trait.addSelectionInterval(k, k);
			else
				trait.removeSelectionInterval(k, k);
		int[] sel = trait.getSelectedIndices();
		trait.clearSelection();
		abis.clearSelection();
		for (int i = 0; i < EABIIND.length; i++) {
			int ind = EABIIND[i];
			if (ind < 100 ? ((vals[0] >> ind) & 1) > 0 : ((vals[1] >> (ind - 100 - IMUSFT)) & 1) > 0)
				abis.addSelectionInterval(i, i);
		}
		for (int area : sel)
			trait.addSelectionInterval(area, area);
		changing = false;
	}

	@Override
	protected void resized(int x, int y) {
		set(jt, x, y, 0, 0, 300, 500);
		set(jab, x, y, 300, 0, 300, 500);
	}

	private void confirm() {
		int[] ans = new int[2];
		for (int i = 0; i < EABIIND.length; i++)
			if (abis.isSelectedIndex(i))
				if (EABIIND[i] < 100)
					ans[0] |= 1 << EABIIND[i];
				else
					ans[1] |= 1 << (EABIIND[i] - 100 - IMUSFT);
		for (int i = 0; i < traitList.size(); i++)
			if (trait.isSelectedIndex(i)) {
				if (!ce.traits.contains(traitList.get(i))) {
					ce.traits.add(traitList.get(i));
				}
			} else
				ce.traits.remove(traitList.get(i));
		getFront().callBack(ans);
	}

	private void ini() {
		for (Trait diyTrait : traitList)
			vt.add(diyTrait.name);
		va.addAll(Arrays.asList(EABI).subList(0, EABIIND.length));
		trait.setListData(vt);
		abis.setListData(va);
		int m = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		trait.setSelectionMode(m);
		abis.setSelectionMode(m);
		set(trait);
		set(abis);
		add(jt);
		add(jab);
		trait.setEnabled(editable);
		abis.setEnabled(editable);
	}

	private void set(JList<?> jl) {

		jl.addListSelectionListener(arg0 -> {
			if (!changing && !jl.getValueIsAdjusting())
				confirm();
		});
	}
}
