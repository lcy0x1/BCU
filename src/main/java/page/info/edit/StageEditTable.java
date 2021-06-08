package page.info.edit;

import common.CommonStatic;
import common.pack.Identifier;
import common.pack.PackData;
import common.pack.PackData.UserPack;
import common.pack.UserProfile;
import common.system.ENode;
import common.util.Data;
import common.util.stage.SCDef;
import common.util.stage.SCDef.Line;
import common.util.stage.SCGroup;
import common.util.stage.Stage;
import common.util.unit.AbEnemy;
import common.util.unit.EneRand;
import common.util.unit.Enemy;
import page.MainFrame;
import page.MainLocale;
import page.Page;
import page.info.EnemyInfoPage;
import page.info.filter.AbEnemySelectionPage;
import page.pack.EREditPage;
import page.support.*;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class StageEditTable extends AbJTable implements Reorderable {

	private static final long serialVersionUID = 1L;

	private static String[] title;

	static {
		redefine();
	}

	protected static void redefine() {
		title = Page.get(MainLocale.INFO, "t", 9);
	}

	private SCDef stage;
	private final Page page;
	private final UserPack pack;

	private boolean changing = false;

	protected StageEditTable(Page p, UserPack pac) {
		page = p;
		pack = pac;
		setTransferHandler(new InTableTH(this));
		setDefaultRenderer(String.class, new EnemyTCR());
	}

	@Override
	public boolean editCellAt(int r, int c, EventObject e) {
		boolean result = super.editCellAt(r, c, e);
		Component editor = getEditorComponent();
		if (!(editor instanceof JTextComponent))
			return result;
		JTextComponent jtf = ((JTextComponent) editor);
		if (e instanceof KeyEvent)
			jtf.selectAll();
		if (lnk[c] == 1 && jtf.getText().length() > 0) {
			Object obj = get(r, c);

			if(obj != null)
				jtf.setText(((AbEnemy) obj).getID() + "");
		}
		return result;
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return c == 1 ? String.class : Object.class;
	}

	@Override
	public int getColumnCount() {
		return title.length;
	}

	@Override
	public String getColumnName(int c) {
		return title[lnk[c]];
	}

	@Override
	public synchronized int getRowCount() {
		if (stage == null)
			return 0;
		return stage.datas.length;
	}

	public void updateAbEnemy(AbEnemySelectionPage page) {
		if(page.getSelected() == null)
			stage.datas[page.index].enemy = null;
		else
			stage.datas[page.index].enemy = page.getSelected().getID();
	}

	@Override
	public String getToolTipText(MouseEvent e) {
		if (columnAtPoint(e.getPoint()) == 2) {
			return "{hp, atk}";
		} else {
			return null;
		}
	}

	@Override
	public synchronized Object getValueAt(int r, int c) {
		if (stage == null || r < 0 || c < 0 || r >= stage.datas.length || c > lnk.length)
			return null;
		return get(r, lnk[c]);
	}

	@Override
	public boolean isCellEditable(int r, int c) {
		return true;
	}

	@Override
	public synchronized void reorder(int ori, int fin) {
		if (fin > ori)
			fin--;
		if (fin == ori)
			return;
		Line[] info = stage.datas;
		int ior = info.length - ori - 1;
		int ifi = info.length - fin - 1;
		Line temp = info[ior];
		if (ior < ifi)
			System.arraycopy(info, ior + 1, info, ior, ifi - ior);
		else if (ior - ifi >= 0)
			System.arraycopy(info, ifi, info, ifi + 1, ior - ifi);
		info[ifi] = temp;
	}

	@Override
	public synchronized void setValueAt(Object arg0, int r, int c) {
		if (stage == null)
			return;
		if (r >= getRowCount())
			return;
		c = lnk[c];
		if (c == 1) {
			String[] is = CommonStatic.getPackEntityID((String) arg0);

			if(is[0] == null || is[0].isEmpty() || is[1] == null || is[1].isEmpty())
				return;

			setEnemy(r, is[0], is[1]);
		} else if (c > 3) {
			int[] is = CommonStatic.parseIntsN((String) arg0);
			if (is.length == 0)
				return;
			if (is.length == 1)
				set(r, c, is[0], -1);
			else
				set(r, c, is[0], is[1]);
		} else if (c == 0) {
			int i = ((String) arg0).length();
			set(r, c, i > 0 ? 1 : 0, 0);

		} else if (c == 2) {
			int[] data = CommonStatic.parseIntsN((String) arg0);

			if (data.length == 1) {
				if(data[0] == 0)
					return;

				set(r, c, data[0], -1);
			} else {
				if(data[0] == 0)
					return;

				set(r, c, data[0], data[1]);
			}
		} else {
			int i = arg0 instanceof Integer ? (Integer) arg0 : CommonStatic.parseIntN((String) arg0);
			set(r, c, i, 0);
		}
	}

	protected synchronized int addLine(AbEnemy enemy) {
		if (stage == null)
			return -1;
		int ind = getSelectedRow();
		if (ind == -1)
			ind = 0;
		Line[] info = stage.datas;
		int len = info.length;
		int sind = len - ind - 1;
		Line[] ans = new Line[len + 1];
		if (sind >= 0) {
			System.arraycopy(info, 0, ans, 0, sind);
			if (len + 1 - (sind + 1) >= 0) System.arraycopy(info, sind + 1 - 1, ans, sind + 1, len + 1 - (sind + 1));
		} else
			sind = 0;
		if (enemy == null && sind < info.length && getSelectedRow() >= 0)
			ans[sind] = info[sind].clone();
		else {
			ans[sind] = new Line();
			ans[sind].enemy = enemy == null ? null : enemy.getID();
			ans[sind].number = 1;
			ans[sind].castle_0 = 100;
			ans[sind].layer_0 = 0;
			ans[sind].layer_1 = 9;
			ans[sind].multiple = 100;
			ans[sind].mult_atk = 100;
		}
		stage.datas = ans;
		ind++;
		if (ind >= ans.length)
			ind = ans.length - 1;
		for (int i = 0; i < ans.length; i++)
			if (ans[i] == null)
				ans[i] = new Line();
		return ind;
	}

	protected synchronized void clicked(Point p, int button) {
		if (stage == null)
			return;
		int c = getColumnModel().getColumnIndexAtX(p.x);
		c = lnk[c];
		int r = p.y / getRowHeight();
		Line[] info = stage.datas;
		int len = info.length;
		if (r < 0 || r >= getRowCount() || c != 1)
			return;
		int ind = len - r - 1;
		if (info[ind] == null)
			return;

		if(button == MouseEvent.BUTTON1) {
			AbEnemy e = Identifier.get(info[ind].enemy);
			if (e instanceof Enemy) {
				List<Enemy> eList = new ArrayList<>();
				List<int[]> muls = new ArrayList<>();
				for (int i = info.length - 1; i >= 0; i--) {
					AbEnemy f = Identifier.get(info[i].enemy);
					if (!(f instanceof Enemy) || eList.contains(f))
						continue;
					eList.add((Enemy)f);
					if (f == e)
						muls.add(new int[]{info[ind].multiple,info[ind].mult_atk});
					else
						muls.add(new int[]{info[i].multiple,info[i].mult_atk});
				}
				MainFrame.changePanel(new EnemyInfoPage(page, ENode.getList(eList, (Enemy)e, muls)));
			} if (e instanceof EneRand)
				MainFrame.changePanel(new EREditPage(page, pack, (EneRand) e));
		} else if(button == MouseEvent.BUTTON3) {
			AbEnemySelectionPage find;

			if(pack == null) {
				find = new AbEnemySelectionPage(page, this, ind);
			} else {
				find = new AbEnemySelectionPage(page, this, ind, pack.getSID(), pack.desc.dependency.toArray(new String[0]));
			}

			MainFrame.changePanel(find);
		}
	}

	protected synchronized int remLine() {
		if (stage == null)
			return -1;
		int ind = getSelectedRow();
		Line[] info = stage.datas;
		if (info.length == 0)
			return -1;
		if (ind == -1)
			ind = 0;
		int sind = info.length - ind - 1;
		Line[] ans = new Line[info.length - 1];
		for (int i = 0; i < ans.length; i++)
			if (i < sind)
				ans[i] = info[i];
			else
				ans[i] = info[i + 1];
		stage.datas = ans;
		if (ans.length > 0) {
			if (ind == 0)
				ind = 1;
			return ind - 1;
		}
		return -1;
	}

	protected synchronized void setData(Stage st) {
		changing = true;
		if (cellEditor != null)
			cellEditor.stopCellEditing();
		changing = false;
		stage = st == null ? null : st.data;
		clearSelection();
	}

	private Object get(int r, int c) {
		if (r < 0 || r >= stage.datas.length)
			return null;
		Line[] info = stage.datas;
		Line data = info[info.length - r - 1];
		if (data == null)
			return null;
		if (c == 0)
			return data.boss == 1 ? "boss" : "";
		else if (c == 1)
			return Identifier.get(data.enemy);
		else if (c == 2)
			return (data.multiple == data.mult_atk ? data.multiple : CommonStatic.toArrayFormat(data.multiple, data.mult_atk)) + "%";
		else if (c == 3)
			return data.number == 0 ? "infinite" : data.number;
		else if (c == 4)
			return (data.castle_0 >= data.castle_1 ? data.castle_0 : data.castle_0 + "~" + data.castle_1) + "%";
		else if (c == 5)
			return Math.abs(data.spawn_0) >= Math.abs(data.spawn_1) ? data.spawn_0 : data.spawn_0 + "~" + data.spawn_1;
		else if (c == 6)
			return data.respawn_0 == data.respawn_1 ? data.respawn_0 : data.respawn_0 + "~" + data.respawn_1;
		else if (c == 7)
			return data.layer_0 == data.layer_1 ? data.layer_0 : data.layer_0 + "~" + data.layer_1;
		else if (c == 8) {
			int g = data.group;
			SCGroup scg = stage.sub.get(g);
			return scg == null ? g != 0 ? Data.trio(g) + " - invalid" : "" : scg.toString();
		}
		return null;
	}

	private void set(int r, int c, int v, int para) {
		if (changing)
			return;
		if (r < 0 || r >= stage.datas.length)
			return;
		if (c == 1 && (v < 0 || para == -1))
			return;
		if (c != 5 && c != 7 && v < 0)
			v = 0;
		Line[] info = stage.datas;
		Line data = info[info.length - r - 1];
		if (c == 0)
			data.boss = v;
		else if (c == 1) {
			PackData pack = UserProfile.getPack(Data.hex(v));

			if(pack != null) {
				Enemy e = pack.enemies.get(para);

				if(e != null) {
					data.enemy = e.id;
				}
			}
		}
		else if (c == 2) {
			data.multiple = v == 0 ? data.multiple : v;
			data.mult_atk = para == -1 ? v : para;
		} else if (c == 3)
			data.number = v;
		else if (c == 4)
			if (para == -1)
				data.castle_0 = data.castle_1 = v;
			else {
				data.castle_0 = Math.min(v, para);
				data.castle_1 = Math.max(v, para);
			}
		else if (c == 5)
			if (para == -1)
				data.spawn_0 = data.spawn_1 = v;
			else {
				if (Math.abs(v) > Math.abs(para)) {
					data.spawn_1 = v;
					data.spawn_0 = para;
				} else {
					data.spawn_0 = v;
					data.spawn_1 = para;
				}
			}
		else if (c == 6)
			if (para == -1)
				data.respawn_0 = data.respawn_1 = v;
			else {
				data.respawn_0 = Math.min(v, para);
				data.respawn_1 = Math.max(v, para);
			}
		else if (c == 7)
			if (para == -1)
				data.layer_0 = data.layer_1 = v;
			else {
				data.layer_0 = v;
				data.layer_1 = para;
			}
		else if (c == 8)
			data.group = v;
	}

	private void setEnemy(int r, String pack, String id) {
		if(pack.isEmpty())
			return;

		if(id.isEmpty())
			return;

		if(id.endsWith("r")) {
			if (!CommonStatic.isInteger(id.substring(0, id.length() - 1)))
				return;
		} else if(!CommonStatic.isInteger(id))
			return;

		Line[] info = stage.datas;

		Line data = info[info.length-r-1];

		String packID;

		if(CommonStatic.isInteger(pack))
			packID = Data.hex(CommonStatic.parseIntN(pack));
		else
			packID = pack;

		PackData p = UserProfile.getPack(packID);

		if(p == null)
			return;

		if(id.endsWith("r") && p instanceof PackData.DefPack)
			return;

		if(p instanceof UserPack && !this.pack.getSID().equals(packID) && !this.pack.desc.dependency.contains(packID))
			return;

		int entityID;
		boolean random;

		if(id.endsWith("r")) {
			entityID = CommonStatic.parseIntN(id.substring(0, id.length()-1));
			random = true;
		} else {
			entityID = CommonStatic.parseIntN(id);
			random = false;
		}

		if(random) {
			if(entityID >= p.randEnemies.size() || entityID < 0)
				return;

			AbEnemy ab = p.randEnemies.get(entityID);

			if(ab != null)
				data.enemy = ab.getID();
		} else {
			if(entityID >= p.enemies.size() || entityID < 0)
				return;

			AbEnemy ab = p.enemies.get(entityID);

			if(ab != null)
				data.enemy = ab.getID();
		}
	}
}
