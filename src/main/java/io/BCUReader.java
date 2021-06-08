package io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import common.CommonStatic;
import common.CommonStatic.Config;
import common.io.DataIO;
import common.io.InStream;
import common.io.json.JsonDecoder;
import common.pack.Context.ErrType;
import common.pack.UserProfile;
import common.util.lang.MultiLangCont;
import common.util.stage.MapColc;
import common.util.stage.MapColc.DefMapColc;
import common.util.stage.Stage;
import common.util.stage.StageMap;
import common.util.unit.Combo;
import common.util.unit.Enemy;
import common.util.unit.Unit;
import main.MainBCU;
import main.Opts;
import page.LoadPage;
import page.MainFrame;
import page.MainLocale;
import page.battle.BattleInfoPage;
import page.support.Exporter;
import page.support.Importer;
import page.view.ViewBox;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class BCUReader extends DataIO {

	public static void getData$1() {
		readLang();
		BCMusic.preload();
	}

	public static InStream readBytes(File file) {
		return InStream.getIns(file);
	}

	public static void readInfo() {
		File f = new File("./user/config.json");
		if (f.exists()) {
			try (Reader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
				JsonElement je = JsonParser.parseReader(r);
				r.close();
				Config cfg = CommonStatic.getConfig();
				JsonDecoder.inject(je, Config.class, cfg);
				JsonObject jo = je.getAsJsonObject();
				int[] rect = JsonDecoder.decode(jo.get("crect"), int[].class);
				MainFrame.crect = new Rectangle(rect[0], rect[1], rect[2], rect[3]);
				MainBCU.preload = jo.get("preload").getAsBoolean();
				ViewBox.Conf.white = jo.get("transparent").getAsBoolean();
				MainBCU.USE_JOGL = jo.get("JOGL").getAsBoolean();
				MainBCU.prefLevel = jo.get("prefLv").getAsInt();
				MainBCU.FILTER_TYPE = jo.get("filter").getAsInt();
				BCMusic.play = jo.get("play_sound").getAsBoolean();
				BCMusic.VOL_BG = jo.get("volume_BG").getAsInt();
				BCMusic.VOL_SE = jo.get("volume_SE").getAsInt();
				if(jo.has("volume_UI")) {
					BCMusic.VOL_UI = jo.get("volume_UI").getAsInt();
				}
				MainLocale.exLang = jo.get("edit_lang").getAsBoolean();
				MainLocale.exTTT = jo.get("edit_tooltip").getAsBoolean();
				BattleInfoPage.DEF_LARGE = jo.get("large_screen").getAsBoolean();
				MainBCU.light = jo.get("style_light").getAsBoolean();
				MainBCU.nimbus = jo.get("style_nimbus").getAsBoolean();
				if(jo.has("author")) {
					MainBCU.author = jo.get("author").getAsString();
				}
				if(jo.has("rowlayout")) {
					CommonStatic.getConfig().twoRow = jo.get("rowlayout").getAsBoolean();
				}
				String[] exp = JsonDecoder.decode(jo.get("export_paths"), String[].class);
				String[] imp = JsonDecoder.decode(jo.get("import_paths"), String[].class);
				for (int i = 0; i < Exporter.curs.length; i++)
					Exporter.curs[i] = exp[i] == null ? null : new File(exp[i]);
				for (int i = 0; i < Importer.curs.length; i++)
					Importer.curs[i] = imp[i] == null ? null : new File(imp[i]);
			} catch (Exception e) {
				CommonStatic.ctx.noticeErr(e, ErrType.WARN, "failed to read config");
			}
		}
	}

	public static void readLang() {
		LoadPage.prog("reading language information");
		File f = new File("./assets/lang/");
		if (!f.exists())
			return;

		File[] fis = f.listFiles();

		if(fis != null) {
			for (File fi : fis) {
				String ni = fi.getName();
				if (!fi.isDirectory())
					continue;
				if (ni.length() != 2)
					continue;

				File[] fls = fi.listFiles();

				if(fls != null) {
					for (File fl : fls)
						try {
							String nl = fl.getName();

							if (nl.equals("ComboName.txt")) {
								Queue<String> qs = readLines(fl);
								if (qs != null) {
									for (String line : qs) {
										String[] str = line.trim().split("\t");
										List<Combo> combo = Arrays.stream(UserProfile.getBCData().combos.toArray())
												.filter(c -> c.name.equals(str[0]))
												.collect(Collectors.toList());
										if (combo.size() > 0)
											MultiLangCont.getStatic().COMNAME.put(ni, combo.get(0), str[1]);
									}
								}
								continue;
							}
							if (nl.equals("tutorial.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String line : qs) {
										String[] strs = line.trim().split("\t");
										if (strs.length != 3)
											continue;
										MainLocale.addTTT(ni, strs[0].trim(), strs[1].trim(), strs[2].trim());
									}
								continue;
							}
							if (nl.equals("StageName.txt")) {
								Queue<String> qs = readLines(fl);
								if (qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										if (strs.length == 1)
											continue;
										String idstr = strs[0].trim();
										String name = strs[strs.length - 1].trim();
										if (idstr.length() == 0 || name.length() == 0)
											continue;
										String[] ids = idstr.split("-");
										int id0 = CommonStatic.parseIntN(ids[0]);

										StageMap stm = DefMapColc.getMap(id0 * 1000);

										if(stm != null) {
											MapColc mc = stm.getCont();
											if (mc == null)
												continue;
											if (ids.length == 1) {
												MultiLangCont.getStatic().MCNAME.put(ni, mc, name);
												continue;
											}
											int id1 = CommonStatic.parseIntN(ids[1]);
											if (id1 >= mc.maps.size() || id1 < 0)
												continue;
											StageMap sm = mc.maps.get(id1);
											if (sm == null)
												continue;
											if (ids.length == 2) {
												MultiLangCont.getStatic().SMNAME.put(ni, sm, name);
												continue;
											}
											int id2 = CommonStatic.parseIntN(ids[2]);
											if (id2 >= sm.list.size() || id2 < 0)
												continue;
											Stage st = sm.list.get(id2);
											MultiLangCont.getStatic().STNAME.put(ni, st, name);
										}
									}
								continue;
							}
							if (nl.equals("UnitName.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Unit u = UserProfile.getBCData().units.get(CommonStatic.parseIntN(strs[0]));
										if (u == null)
											continue;
										for (int i = 0; i < Math.min(u.forms.length, strs.length - 1); i++)
											MultiLangCont.getStatic().FNAME.put(ni, u.forms[i], strs[i + 1].trim());
									}
								continue;
							}
							if (nl.equals("EnemyName.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Enemy e = UserProfile.getBCData().enemies.get(CommonStatic.parseIntN(strs[0]));
										if (e == null || strs.length < 2)
											continue;
										MultiLangCont.getStatic().ENAME.put(ni, e, strs[1].trim());
									}
								continue;
							}
							if (nl.equals("UnitExplanation.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Unit u = UserProfile.getBCData().units.get(CommonStatic.parseIntN(strs[0]));
										if (u == null)
											continue;
										for (int i = 0; i < Math.min(u.forms.length, strs.length - 1); i++)
											MultiLangCont.getStatic().FEXP.put(ni, u.forms[i], strs);
									}
								continue;
							}
							if (nl.equals("EnemyExplanation.txt")) {
								Queue<String> qs = readLines(fl);
								if(qs != null)
									for (String str : qs) {
										String[] strs = str.trim().split("\t");
										Enemy e = UserProfile.getBCData().enemies.get(CommonStatic.parseIntN(strs[0]));
										if (e == null || strs.length < 2)
											continue;
										MultiLangCont.getStatic().EEXP.put(ni, e, strs);
									}
								continue;
							}
							if (!nl.endsWith(".properties"))
								continue;
							MainLocale ml = new MainLocale(nl.split("\\.")[0] + "_" + ni);
							Queue<String> qs = readLines(fl);
							if(qs != null)
								for (String line : qs) {
									String[] strs = line.split("[=\t]", 2);
									if (strs.length < 2)
										continue;
									ml.res.put(strs[0], strs[1]);
								}
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			}
		}
	}

	public static Queue<String> readLines(File file) {
		try {
			return new ArrayDeque<>(Files.readAllLines(file.toPath()));
		} catch (IOException e) {
			Opts.ioErr("failed to read file " + file);
			e.printStackTrace();
			return null;
		}

	}

}
