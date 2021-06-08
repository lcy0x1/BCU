package page.info.edit;


import common.battle.data.CustomUnit;
import common.battle.data.PCoin;
import common.util.unit.Form;
import page.JBTN;
import page.Page;

import java.util.ArrayList;
import java.util.List;

public class PCoinEditPage extends Page {

    private static final long serialVersionUID = 1L;

    private final JBTN back = new JBTN(0, "back");
    private final JBTN addP = new JBTN(0, "add");
    private final JBTN remP = new JBTN(0, "rempc");
    private final boolean editable;
    private final CustomUnit uni;
    private final List<PCoinEditTable> pCoinEdits = new ArrayList<>();

    public PCoinEditPage(Page p, Form u, boolean edi) {
        super(p);
        uni = (CustomUnit) u.du;
        editable = edi;
        for (int i = 0; i < 5; i++)
            pCoinEdits.add(new PCoinEditTable(this, uni, i, editable));

        ini();
        resized();
    }

    @Override
    protected void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(addP, x, y, 400, 50, 300, 50);
        set(remP, x, y, 700, 50, 300, 50);
        for (int i = 0; i < 5; i++)
            set(pCoinEdits.get(i), x, y, i * 400, 200, 400, 1300);
    }

    private void addListeners() {
        back.addActionListener(arg0 -> changePanel(getFront()));

        addP.addActionListener(arg0 -> {
           if (uni.pcoin == null)
               uni.pcoin = new PCoin(uni);
           int slot = uni.pcoin.info.size();
           uni.pcoin.info.add(new int[]{slot + 1,10,0,0,0,0,0,0,0,0,slot + 1,8,-1});
           for (int i = 0; i < slot; i++)
               if (uni.pcoin.info.get(i)[0] == slot + 1) {
                   PCoinEditTable pc = pCoinEdits.get(i);
                   pc.setData();
                   pc.randomize();
            }
           setCoinTypes();
        });

        //PCoin Structure:
        //[0] = ability identifier, [1] = max lv, [2,4,6,8] = min lv values, [3,5,7,9] = max lv values, [10,11,12] = ???

        remP.addActionListener(arg0 -> {
            uni.pcoin = null;
            setCoinTypes();
        });
    }

    protected void setCoinTypes() {
        for (PCoinEditTable pcedi : pCoinEdits)
            pcedi.setCTypes(uni.pcoin != null && uni.pcoin.info.size() > pcedi.talent);
        setCoins();
    }

    //Changes the other talent indexes once a talent is removed from the list
    protected void removed() {
        if (uni.pcoin.info.size() == 0)
            uni.pcoin = null;
        setCoinTypes();
    }

    private void ini() {
        add(back);
        add(addP);
        add(remP);
        for (PCoinEditTable pce : pCoinEdits)
            add(pce);
        addListeners();
        setCoins();
    }

    private void setCoins() {
        if (uni.pcoin != null)
            uni.pcoin.update();
        for (PCoinEditTable pct : pCoinEdits)
            pct.setData();
        addP.setEnabled(editable && (uni.pcoin == null || uni.pcoin.info.size() < 5));
        remP.setEnabled(editable && uni.pcoin != null);
    }
}
