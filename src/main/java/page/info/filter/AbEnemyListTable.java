package page.info.filter;

import common.battle.Basis;
import common.battle.BasisSet;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.EnemyInfoPage;
import page.pack.EREditPage;
import page.support.EnemyTCR;
import page.support.SortTable;

import java.awt.*;

public class AbEnemyListTable  extends SortTable<AbEnemy> {

    private static final long serialVersionUID = 1L;

    private static String[] tit;

    static {
        redefine();
    }

    public static void redefine() {
        tit = new String[] { "ID", "", "HP", "HB", "atk", Page.get(MainLocale.INFO, "range"), Page.get(MainLocale.INFO, "atkf"),
                Page.get(MainLocale.INFO, "speed"), Page.get(MainLocale.INFO, "drop"), Page.get(MainLocale.INFO, "preaa"), "hp/dps", "HP/HB/dps" };
    }

    private final Page page;

    private final Basis b = BasisSet.current();

    protected AbEnemyListTable(Page p) {
        page = p;

        setDefaultRenderer(Enemy.class, new EnemyTCR());
    }

    @Override
    public Class<?> getColumnClass(int c) {
        c = lnk[c];
        if (c == 1)
            return Enemy.class;
        return String.class;
    }

    protected void clicked(Point p) {
        if (list == null)
            return;
        int c = getColumnModel().getColumnIndexAtX(p.x);
        c = lnk[c];
        int r = p.y / getRowHeight();
        if (r < 0 || r >= list.size() || c != 1)
            return;
        AbEnemy e = list.get(r);

        if(e instanceof Enemy)
            MainFrame.changePanel(new EnemyInfoPage(page, ENode.getList(UserProfile.getAll(((Enemy)e).id.pack, Enemy.class), (Enemy)e)));
        else if(e instanceof EneRand)
            MainFrame.changePanel(new EREditPage(page, UserProfile.getUserPack(e.getID().pack)));

    }

    @Override
    protected int compare(AbEnemy e0, AbEnemy e1, int c) {
        if (c == 1)
            c--;
        if (c == 0) {
            return e0.compareTo(e1);
        }

        if(e0 instanceof EneRand)
            if(e1 instanceof Enemy)
                return -1;
            else if(e1 instanceof EneRand)
                return 0;

        if(e1 instanceof EneRand)
            if(e0 instanceof Enemy)
                return 1;

        int i0 = (int) get(e0, c);
        int i1 = (int) get(e1, c);

        return Integer.compare(i0, i1);
    }

    @Override
    protected Object get(AbEnemy abEnemy, int c) {
        if(abEnemy instanceof Enemy) {
            Enemy e = (Enemy) abEnemy;

            if (c == 0)
                return e.id;
            else if (c == 1)
                return e;
            else if (c == 2)
                return e.de.getHp();
            else if (c == 3)
                return e.de.getHb();
            else if (c == 4)
                return e.de.allAtk();
            else if (c == 5)
                return e.de.getRange();
            else if (c == 6)
                return e.de.getItv();
            else if (c == 7)
                return e.de.getSpeed();
            else if (c == 8)
                return (int) (e.de.getDrop() * b.t().getDropMulti());
            else if (c == 9)
                return e.de.rawAtkData()[0][1];
            else if (c == 10)
                return e.de.allAtk() == 0 ? Integer.MAX_VALUE : (int) ((long) e.de.getHp() * e.de.getItv() / e.de.allAtk());
            else if (c == 11)
                return e.de.getHb() < 2 ? (int) get(e, 10) : (int) get(e, 10) / e.de.getHb();
            else
                return null;
        } else {
            if(c == 0)
                return abEnemy.getID();
            else if(c == 1)
                return abEnemy;
            else if(c == 2)
                return "Random Enemy";
            return null;
        }
    }

    @Override
    protected String[] getTit() {
        return tit;
    }

}
