package page.info.filter;

import common.battle.data.CustomUnit;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.util.unit.Trait;
import page.Page;

import javax.swing.*;
import java.util.*;

import static utilpc.Interpret.SABIS;

public class UnitEditBox extends Page {

	private static final long serialVersionUID = 1L;

	private final boolean editable;

	private final Vector<String> vt = new Vector<>();
	private final Vector<String> va = new Vector<>();
	private final AttList trait = new AttList();
	private final AttList abis = new AttList(0, 1);
	private final JScrollPane jt;
	private final JScrollPane jab = new JScrollPane(abis);

	private boolean changing = false;
	private final List<Trait> traitList;
	private final CustomUnit cu;

	public UnitEditBox(Page p, UserPack pack, CustomUnit cun) {
		super(p);
		editable = pack.editable;
		traitList = new ArrayList<>(UserProfile.getBCData().traits.getList().subList(0,9));
		traitList.addAll(pack.traits.getList());
		for (UserPack pacc : UserProfile.getUserPacks())
			if (pack.desc.dependency.contains(pacc.desc.id))
				traitList.addAll(pacc.traits.getList());
		trait.setIcons(traitList);
		jt = new JScrollPane(trait);

		cu = cun;
		ini();
	}

	public void setData(int[] vals, ArrayList<Trait> ts) {
		changing = true;
		for (int k = 0; k < traitList.size(); k++)
			if (ts.contains(traitList.get(k)))
				trait.addSelectionInterval(k, k);
			else
				trait.removeSelectionInterval(k, k);
		int[] sel = trait.getSelectedIndices();
		trait.clearSelection();
		abis.clearSelection();
		for (int i = 0; i < SABIS.length; i++)
			if (((vals[0] >> i) & 1) > 0)
				abis.addSelectionInterval(i, i);
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
		int lev = SABIS.length;
		for (int i = 0; i < lev; i++)
			if (abis.isSelectedIndex(i))
				ans[0] |= 1 << i;
		for (int i = 0; i < traitList.size(); i++)
			if (trait.isSelectedIndex(i)) {
				if (!cu.traits.contains(traitList.get(i))) {
					cu.traits.add(traitList.get(i));
				}
			} else
				cu.traits.remove(traitList.get(i));
		getFront().callBack(ans);
	}

	private void ini() {
		for (Trait value : traitList)
			vt.add(value.name);
		Collections.addAll(va, SABIS);
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
