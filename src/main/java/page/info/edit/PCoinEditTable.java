package page.info.edit;

import common.CommonStatic;
import common.battle.data.CustomUnit;
import common.pack.UserProfile;
import common.util.Data;
import common.util.lang.ProcLang;
import common.util.unit.Trait;
import main.MainBCU;
import org.jcodec.common.tools.MathUtil;
import page.*;
import utilpc.Interpret;
import utilpc.Theme;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@SuppressWarnings("deprecation")
class PCoinEditTable extends Page {

    private static final long serialVersionUID = 1L;

    private static class talentData {

        private static final long serialVersionUID = 1L;
        private final String name;
        private final int key;

        private talentData(String text, int ID) {
            name = text;
            key = ID;
        }

        @Override
        public String toString() { return name; }
        private int getValue() { return key; }
    }
    private static class NPList extends JList<talentData> {
        protected NPList() {
            if (MainBCU.nimbus)
                setSelectionBackground(MainBCU.light ? Theme.LIGHT.NIMBUS_SELECT_BG : Theme.DARK.NIMBUS_SELECT_BG);
        }
        protected void setListIcons() {
            setCellRenderer(new DefaultListCellRenderer() {

                private static final long serialVersionUID = 1L;

                @Override
                public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
                    JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
                    talentData td = (talentData)o;
                    int[] val = Data.PC_CORRES[td.getValue()];
                    switch (val[0]) {
                        case Data.PC_AB:
                            for (int i = 0; i < Data.ABI_TOT; i++)
                                if (((val[1] >> i) & 1) == 1) {
                                    jl.setIcon(UtilPC.createIcon(0, i));
                                }
                            break;
                        case Data.PC_P:
                        case Data.PC_IMU:
                            jl.setIcon(UtilPC.createIcon(1, val[1]));
                            break;
                        case Data.PC_BASE:
                            jl.setIcon(UtilPC.createIcon(4, val[1]));
                            break;
                        case Data.PC_TRAIT:
                            jl.setIcon(UtilPC.createIcon(3, val[1]));
                            break;
                    }
                    return jl;
                }
            });
        }
    }

    //ensures not every single talent is here, to avoid touching unused values, each number corresponds to a PC_CORRES array
    private final int[] allPC = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 50, 51, 52, 54, 56};

    private final CustomUnit unit;
    private final NPList ctypes = new NPList();
    private final JScrollPane stypes = new JScrollPane(ctypes);
    private final JL pCoin = new JL();
    private final JL jPCLV = new JL(0,"Max Lv");
    private final JTF PCoinLV = new JTF();
    private final JBTN delet = new JBTN(0,"rem");
    private final PCoinEditPage pcedit;
    private final boolean editable;
    private final JL[] chance = new JL[8];
    private final JTF[] tchance = new JTF[8];
    protected final int talent;

    private boolean changing;
    private int cTypesY = 550;

    protected PCoinEditTable(Page p, CustomUnit u, int ind, boolean edi) {
        super(p);
        pcedit = (PCoinEditPage) p;
        unit = u;
        talent = ind;
        editable = edi;

        ini();
    }

    @Override
    protected void resized(int x, int y) {
        set(pCoin, x, y, 0, 0, 400, 50);
        set(delet, x, y, 0, 50, 400, 50);
        set(jPCLV, x, y, 0, 100, 200, 50);
        set(PCoinLV, x, y, 200, 100, 200, 50);
        for (int i = 0; i < chance.length; i++) {
            set(chance[i], x, y, 0, 150 + i * 50, 200, 50);
            set(tchance[i], x, y, 200, 150 + i * 50, 200, 50);
        }
        set(stypes, x, y, 0, cTypesY, 400, 1050 - cTypesY);
    }

    private void addListeners() {
        PCoinLV.setLnr(arg0 -> {
            String txt = PCoinLV.getText().trim();
            int v = CommonStatic.parseIntN(txt);
            unit.pcoin.info.get(talent)[1] = MathUtil.clip(v, 1, 10);
            setData();
        });

        delet.addActionListener(arg0 -> {
            changing = true;
            unit.pcoin.info.remove(talent);
            pcedit.removed();
            changing = false;
        });

        ctypes.addListSelectionListener(evt -> {
            if (changing)
                return;
            changing = true;

            talentData np = ctypes.getSelectedValue();
            if (np == null) {
                changing = false;
                return;
            }
            unit.pcoin.info.get(talent)[0] = np.getValue();
            unit.pcoin.info.get(talent)[10] = np.getValue();

            int[] vals = Data.PC_CORRES[np.getValue()];
            if (vals[0] == Data.PC_P) {
                int low = unit.getProc().getArr(vals[1]).get(0) == 0 ? 1 : 0;
                unit.pcoin.info.get(talent)[2] = Math.max(low, unit.pcoin.info.get(talent)[4]);
                unit.pcoin.info.get(talent)[3] = Math.max(low, unit.pcoin.info.get(talent)[5]);
                if (!(vals[1] == Data.P_SATK || vals[1] == Data.P_VOLC || vals[1] == Data.P_CRIT)) {
                    int min = unit.getProc().getArr(vals[1]).get(1) == 0 ? 1 : 0;
                    unit.pcoin.info.get(talent)[4] = Math.max(min, unit.pcoin.info.get(talent)[4]);
                    unit.pcoin.info.get(talent)[5] = Math.max(min, unit.pcoin.info.get(talent)[5]);
                }
                if (vals[1] == Data.P_VOLC) {
                    unit.pcoin.info.get(talent)[8] = Math.max(1, unit.pcoin.info.get(talent)[8] / Data.VOLC_ITV) * Data.VOLC_ITV;
                    unit.pcoin.info.get(talent)[9] = Math.max(1, unit.pcoin.info.get(talent)[9] / Data.VOLC_ITV) * Data.VOLC_ITV;
                }
            }
            pcedit.setCoinTypes();
            changing = false;
        });

        for (int i = 0; i < tchance.length; i++) {
            int finalI = i + 2;
            tchance[i].setLnr(arg0 -> {
                if (changing)
                    return;
                changing = true;
                String txt = tchance[finalI - 2].getText().trim();
                int[] v = CommonStatic.parseIntsN(txt);
                if (v.length == 0) {
                    tchance[finalI - 2].setText("" + unit.pcoin.info.get(talent)[finalI]);
                    changing = false;
                    return;
                }

                int ind = finalI % 2 == 0 ? 1 : -1;
                int w = v.length > 1 ? v[1] : unit.pcoin.info.get(talent)[finalI + ind];

                int[] vals = Data.PC_CORRES[unit.pcoin.info.get(talent)[0]];

                if (vals[0] == Data.PC_BASE) {
                    if (vals[1] == Data.PC2_COST) {
                        v[0] = (int) (v[0] / 1.5);
                        if (v.length > 1)
                            w = (int) (w / 1.5);
                    } else if (vals[1] == Data.PC2_HB) {
                        v[0] = Math.min(v[0], unit.hp - unit.hb);
                        w = Math.min(w, unit.hp - unit.hb);
                    }
                }

                if (finalI < 4 || finalI < 6 && !(vals[1] == Data.P_SATK || vals[1] == Data.P_VOLC || vals[1] == Data.P_CRIT)) {
                    int min = vals[0] == Data.PC_BASE || unit.getProc().getArr(vals[1]).get((finalI - 2) / 2) == 0 ? 1 : 0;
                    v[0] = Math.max(min, v[0]);
                    if (v.length > 1)
                        v[1] = Math.max(min, v[1]);
                } else if (finalI >= 8 && vals[1] == Data.P_VOLC) {
                    v[0] = Math.max(1, v[0] / Data.VOLC_ITV) * Data.VOLC_ITV;
                    if (v.length > 1)
                        v[1] = Math.max(1, v[1] / Data.VOLC_ITV) * Data.VOLC_ITV;
                }

                if (tchance[finalI - 2 + ind].isEnabled())
                    if (ind == 1) {
                        unit.pcoin.info.get(talent)[finalI] = Math.min(v[0], w);
                        unit.pcoin.info.get(talent)[finalI + ind] = Math.max(v[0], w);
                    } else {
                        unit.pcoin.info.get(talent)[finalI] = Math.max(v[0], w);
                        unit.pcoin.info.get(talent)[finalI + ind] = Math.min(v[0], w);
                    }
                else {
                    unit.pcoin.info.get(talent)[finalI] = v[0];
                    unit.pcoin.info.get(talent)[finalI + ind] = v[0];
                }
                setData();
                changing = false;
            });
        }
    }

    private void ini() {
        add(delet);
        add(jPCLV);
        add(pCoin);
        add(stypes);
        add(PCoinLV);
        setCTypes(unit.pcoin != null && unit.pcoin.info.size() > talent);
        for (int i = 0; i < chance.length; i++) {
            add(chance[i] = new JL(0,i % 2 == 0 ? "Lv1 Value " + (1 + i / 2) : "Max Value " + (1 + i / 2)));
            add(tchance[i] = new JTF());
        }
        addListeners();
    }

    protected void setCTypes(boolean coin) {
        ArrayList<talentData> available = new ArrayList<>();
        if (coin) {
            for (int i : allPC) {
                int[] type = Data.PC_CORRES[i];
                talentData dat = new talentData(Interpret.PCTX[i], i);
                if (available.contains(dat))
                    break;
                // Verify if another talent is using this value
                boolean unused = true;
                for (int j = 0; j < unit.pcoin.info.size(); j++)
                    if (j != talent && unit.pcoin.info.get(j)[0] == i) {
                        unused = false;
                        break;
                    }

                boolean add = type[0] == Data.PC_BASE;
                if (type[0] == Data.PC_P)
                    add = unit.getProc().getArr(type[1]).get(0) < 100;
                if (type[0] == Data.PC_AB)
                    add = (unit.abi & type[1]) == 0;
                if (type[0] == Data.PC_TRAIT)
                    add = !(unit.getTraits().contains(UserProfile.getBCData().traits.get(type[1])));
                if (add && unused) {
                    available.add(dat);
                }
            }
            talentData[] td = new talentData[available.size()];
            for (int i = 0; i < td.length; i++)
                td[i] = available.get(i);
            ctypes.setListData(td);
            ctypes.setListIcons();
        } else
            ctypes.setListData(new talentData[0]);
    }

    protected void randomize() {
        ListModel<talentData> listModel = ctypes.getModel();
        int dat = listModel.getElementAt((int)(Math.random() * listModel.getSize())).getValue();
        unit.pcoin.info.get(talent)[0] = dat;
        unit.pcoin.info.get(talent)[10] = dat;
    }

    protected void setData() {
        changing = true;
        boolean pc = unit.pcoin != null && unit.pcoin.info.size() > talent;
        int[] type = pc ? Data.PC_CORRES[unit.pcoin.info.get(talent)[0]] : new int[]{-1, 0};
        PCoinLV.setEnabled(pc && editable && (type[0] == Data.PC_P || type[0] == Data.PC_BASE));
        if (!PCoinLV.isEnabled() && pc && editable)
            unit.pcoin.info.get(talent)[1] = 1;

        if (pc) {
            PCoinLV.setText("" + unit.pcoin.info.get(talent)[1]);
            int tal = unit.pcoin.info.get(talent)[0];
            ListModel<talentData> listModel = ctypes.getModel();
            for (int i = 0; i < listModel.getSize(); i++)
                if (listModel.getElementAt(i).getValue() == tal) {
                    ctypes.setSelectedIndex(i);
                    break;
                }
            enableSecondaries(type);
            for (int i = 0; i < tchance.length; i++)
                if (i >= 2 || !(type[0] == Data.PC_BASE && type[1] == Data.PC2_COST))
                    tchance[i].setText("" + unit.pcoin.info.get(talent)[2 + i]);
                else
                    tchance[i].setText("" + (int)(unit.pcoin.info.get(talent)[2 + i] * 1.5));
        }
        else {
            ctypes.clearSelection();
            enableSecondaries(type);
            PCoinLV.setText("");
            for (JTF t : tchance)
                t.setText("");
        }
        ctypes.setEnabled(pc && editable);
        delet.setEnabled(pc && editable);
        changing = false;
    }

    //Enables or disables text fields, depending on the needed values for the proc
    private void enableSecondaries(int[] pdata) {
        cTypesY = 550;
        int maxlv = pdata[0] != -1 ? unit.pcoin.info.get(talent)[1] : 0;
        if (pdata[0] == -1 || pdata[0] == Data.PC_AB || pdata[0] == Data.PC_TRAIT || pdata[0] == Data.PC_IMU) {
            for (JTF jtf : tchance)
                jtf.setVisible(false);
            for (JL jl : chance)
                jl.setVisible(false);
            cTypesY -= 400;
            if (pdata[0] != -1)
                if (pdata[0] == Data.PC_IMU) {
                    pCoin.setText(ProcLang.get().get(pdata[1]).full_name);
                    pCoin.setIcon(UtilPC.createIcon(1, pdata[1]));
                } else if (pdata[0] == Data.PC_AB) {
                    for (int i = 0; i < Data.ABI_TOT; i++) {
                        if (((pdata[1] >> i) & 1) == 1) {
                            pCoin.setText(Interpret.SABIS[i]);
                            pCoin.setIcon(UtilPC.createIcon(0, i));
                            break;
                        }
                    }
                } else {
                    Trait tr = UserProfile.getBCData().traits.get(pdata[1]);
                    pCoin.setText(Interpret.TRAIT[tr.id.id]);
                    if (tr.icon != null)
                        pCoin.setIcon(tr.obtainIcon());
                }
            else {
                pCoin.setText("(None)");
                pCoin.setIcon(null);
            }
        }
        if (pdata[0] == Data.PC_BASE) {
            for (int i = 0; i < tchance.length; i++) {
                chance[i].setVisible(i < 2);
                tchance[i].setVisible(i < 2);
            }
            cTypesY -= 300;
            String text = ctypes.getSelectedValue().toString() + (pdata[1] <= Data.PC2_SPEED || pdata[1] == Data.PC2_HB ? "+ " : "") + (pdata[1] <= Data.PC2_ATK ? "%" : "");
            chance[0].setText(text + " (Lv1)");
            chance[1].setText(text + " (Lv" + maxlv + ")");
            pCoin.setText(text);
            pCoin.setIcon(UtilPC.createIcon(4, pdata[1]));
        }
        if (pdata[0] == Data.PC_P) {
            int procChance = unit.getProc().getArr(pdata[1]).get(0);
            unit.pcoin.info.get(talent)[2] = Math.min(unit.pcoin.info.get(talent)[2], 100 - procChance);
            unit.pcoin.info.get(talent)[3] = Math.min(unit.pcoin.info.get(talent)[3], 100 - procChance);
            //This ensures raw proc chance + talent proc chance doesn't goes above 100%

            ProcLang.ItemLang lang = ProcLang.get().get(pdata[1]);
            String[] langText = lang.list();
            pCoin.setText(lang.full_name);
            pCoin.setIcon(UtilPC.createIcon(1, pdata[1]));

            chance[0].setVisible(true);
            chance[1].setVisible(true);
            tchance[0].setVisible(true);
            tchance[1].setVisible(true);
            chance[0].setText(lang.get(langText[0]).getNameValue() + "(Lv1)");
            chance[1].setText(lang.get(langText[0]).getNameValue() + "(Lv" + maxlv + ")");

            boolean Field4 = langText.length >= 4;
            cTypesY -= Field4 ? 0 : 100;
            chance[6].setVisible(Field4);
            chance[7].setVisible(Field4);
            tchance[6].setVisible(Field4);
            tchance[7].setVisible(Field4);
            if (Field4) {
                chance[6].setText(lang.get(langText[3]).getNameValue() + "(Lv1)");
                chance[7].setText(lang.get(langText[3]).getNameValue() + "(Lv" + maxlv + ")");
            }

            boolean Field3 = langText.length >= 3;
            cTypesY -= Field3 ? 0 : 100;
            chance[4].setVisible(Field3);
            chance[5].setVisible(Field3);
            tchance[4].setVisible(Field3);
            tchance[5].setVisible(Field3);
            if (Field3) {
                chance[4].setText(lang.get(langText[2]).getNameValue() + "(Lv1)");
                chance[5].setText(lang.get(langText[2]).getNameValue() + "(Lv" + maxlv + ")");
            }

            boolean Field2 = langText.length >= 2;
            cTypesY -= Field2 ? 0 : 100;
            chance[2].setVisible(Field2);
            chance[3].setVisible(Field2);
            tchance[2].setVisible(Field2);
            tchance[3].setVisible(Field2);
            if (Field2) {
                chance[2].setText(lang.get(langText[1]).getNameValue() + "(Lv1)");
                chance[3].setText(lang.get(langText[1]).getNameValue() + "(Lv" + maxlv + ")");
            }
        }
        for (int i = 0; i < tchance.length; i++) {
            if (!tchance[i].isVisible())
                break;
            tchance[i].setEnabled(editable && (i % 2 == 0 || maxlv != 1));
        }
        if (pdata[0] != -1 && editable) {
            for (int i = 0; i < tchance.length; i++)
                if (!tchance[i].isVisible())
                    unit.pcoin.info.get(talent)[2 + i] = 0;
                else if (!tchance[i].isEnabled())
                    unit.pcoin.info.get(talent)[2 + i] = unit.pcoin.info.get(talent)[1 + i];
                else
                    unit.pcoin.info.get(talent)[2 + i] = Math.max(0, unit.pcoin.info.get(talent)[2 + i]);
        }
    }
}
