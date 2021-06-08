package page.pack;

import common.CommonStatic;
import common.pack.Context;
import common.pack.Source;
import common.pack.UserProfile;
import common.util.unit.Form;
import common.util.unit.Enemy;
import common.util.unit.Unit;
import common.pack.PackData.UserPack;
import common.util.unit.Trait;
import common.pack.FixIndexList.FixIndexMap;
import main.MainBCU;
import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;
import page.support.Importer;
import utilpc.Theme;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class TraitEditPage extends Page {

    private static class TraitList extends JList<Trait> {

        private static final long serialVersionUID = 1L;

        protected TraitList() {
            if (MainBCU.nimbus)
                setSelectionBackground(MainBCU.light ? Theme.LIGHT.NIMBUS_SELECT_BG : Theme.DARK.NIMBUS_SELECT_BG);
        }
        protected void setTraitIcons() {
            setCellRenderer(new DefaultListCellRenderer() {
                private static final long serialVersionUID = 1L;
                @Override
                public Component getListCellRendererComponent(JList<?> l, Object o, int ind, boolean s, boolean f) {
                    JLabel jl = (JLabel) super.getListCellRendererComponent(l, o, ind, s, f);
                    Trait trait = (Trait)o;
                    if (trait.icon != null)
                        jl.setIcon(trait.obtainIcon());
                    return jl;
                }
            });
        }
    }

    private static final long serialVersionUID = 1L;

    private final TraitList jlct = new TraitList();
    private final JScrollPane jspct = new JScrollPane(jlct);

    private final JLabel jl = new JLabel();

    private final JBTN back = new JBTN(0, "back");
    private final JBTN addct = new JBTN(0, "add");
    private final JBTN remct = new JBTN(0, "rem");
    private final JBTN adicn = new JBTN(0, "icon");
    private final JBTN reicn = new JBTN(0, "remicon");
    private final JTG altrg = new JTG(0, "traitfect");
    private final JTF ctrna = new JTF();

    private final UserPack packpack;
    private final FixIndexMap<Trait> pct;

    private boolean changing = false;
    private final boolean editable;
    private Trait t;

    public TraitEditPage(Page p, UserPack pac) {
        super(p);
        packpack = pac;
        pct = pac.traits;
        ini();
        editable = pac.editable;
    }

    @Override
    protected synchronized void resized(int x, int y) {
        setBounds(0, 0, x, y);
        set(back, x, y, 0, 0, 200, 50);
        set(addct, x, y, 50, 1000, 150, 50);
        set(remct, x, y, 200, 1000, 150, 50);
        set(jspct, x, y, 50, 100, 300, 800);
        set(altrg, x, y, 50, 950, 300, 50);
        set(ctrna, x, y, 50, 900, 300, 50);
        set(adicn, x, y, 350, 100, 250, 50);
        set(jl, x, y, 450, 150, 50, 50);
        set(reicn, x, y, 350, 200, 250, 50);
    }

    private void addListeners$0() {

        back.setLnr(x -> changePanel(getFront()));

        adicn.addActionListener(arg0 -> getFile("Choose your file"));

        reicn.addActionListener(arg0 -> {
            File file = ((Source.Workspace) packpack.source).getTraitIconFile(t.id);
            if (file.delete()) {
                t.icon = null;
                jl.setIcon(null);
                reicn.setEnabled(false);
                jlct.setTraitIcons();
            }
        });

    }

    private void addListeners$CG() {
        addct.addActionListener(arg0 -> {
            changing = true;
            t = new Trait(packpack.getNextID(Trait.class));
            pct.add(t);
            updateCTL();
            jlct.setSelectedValue(t, true);
            changing = false;
        });
        remct.addActionListener(arg0 -> {
            if (t == null)
                return;
            changing = true;
            List<Trait> list = pct.getList();
            int ind = list.indexOf(t) - 1;
            if (ind < 0 && list.size() > 1)
                ind = 0;
            File file = ((Source.Workspace) packpack.source).getTraitIconFile(t.id);
            file.delete();
            list.remove(t);
            pct.remove(t);
            if (ind >= 0)
                t = list.get(ind);
            else
                t = null;
            updateCTL();
            changing = false;
        });
        altrg.addActionListener(arg0 -> {
            if (t == null)
                return;
            changing = true;
            t.targetType = !t.targetType;
            updateCTL();
            changing = false;
        });
        jlct.addListSelectionListener(arg0 -> {
            if (changing || jlct.getValueIsAdjusting())
                return;
            changing = true;
            t = jlct.getSelectedValue();
            updateCT();
            changing = false;
        });

        ctrna.setLnr(x -> {
            String str = ctrna.getText();
            if (t.name.equals(str))
                return;
            if (str.equals("")) {
                ctrna.setText(t.name);
                return;
            }
            t.name = str;
        });
    }

    private void updateCTL() {
        jlct.setListData(pct.toArray());
        jlct.setSelectedValue(t, true);
        jlct.setTraitIcons();
        updateCT();
    }

    private void updateCT() {
        altrg.setEnabled(t != null && editable);
        remct.setEnabled(t != null && !isUsedTrait(t) && editable);
        ctrna.setEnabled(t != null && editable);
        adicn.setEnabled(t != null && editable);
        setIconImage(t);
        ctrna.setText("");
        if (t != null) {
            ctrna.setText(t.name);
            altrg.setSelected(t.targetType);
            if (t.icon != null)
                jl.setIcon(t.obtainIcon());
            else
                jl.setIcon(null);
        }
        reicn.setEnabled(t != null && t.icon != null && editable);
    }

    private boolean isUsedTrait(Trait tr) {
        Collection<UserPack> pacs = UserProfile.getUserPacks();
        for (UserPack pacc : pacs) {
            if (pacc.desc.dependency.contains(packpack.desc.id) || pacc.desc.id.equals(packpack.desc.id)) {
                for (Enemy en : pacc.enemies.getList())
                    if (en.de.getTraits().contains(tr))
                        return true;
                for (Unit un : pacc.units.getList())
                    for (Form uf : un.forms)
                        if (uf.du.getTraits().contains(tr))
                            return true;
            }
        }
        return false;
    }

    private void ini() {
        add(back);
        add(addct);
        add(remct);
        add(jspct);
        add(altrg);
        add(ctrna);
        add(adicn);
        add(jl);
        add(reicn);
        jl.setIcon(null);
        addListeners$0();
        addListeners$CG();
        updateCTL();
    }

    private void getFile(String str) {
        BufferedImage bimg = new Importer(str).getImg();
        if (bimg == null)
            return;
        if (bimg.getWidth() != 41 && bimg.getHeight() != 41) {
            getFile("Wrong img size. Img size: w=41, h=41");
            return;
        }
        if (t.icon != null)
            t.icon.setImg(MainBCU.builder.build(bimg));
        else
            t.icon = MainBCU.builder.toVImg(bimg);
        try {
            File file = ((Source.Workspace) packpack.source).getTraitIconFile(t.id);
            Context.check(file);
            ImageIO.write(bimg, "PNG", file);
        } catch (IOException e) {
            CommonStatic.ctx.noticeErr(e, Context.ErrType.WARN, "failed to write file");
            getFile("Failed to save file");
            return;
        }
        updateCT();
        setIconImage(jlct.getSelectedValue());
    }

    private void setIconImage(Trait slt) {
        if (t == null)
            return;
        if (jlct.getSelectedValue() != slt) {
            changing = true;
            jlct.setSelectedValue(slt, true);
            jlct.setTraitIcons();
            changing = false;
        }
    }
}