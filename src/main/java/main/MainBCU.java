package main;

import common.CommonStatic;
import common.battle.BasisSet;
import common.io.PackLoader.ZipDesc.FileDesc;
import common.io.assets.Admin;
import common.io.assets.AssetLoader;
import common.pack.Context;
import common.pack.Source.Workspace;
import common.pack.UserProfile;
import common.pack.Context.ErrType;
import common.system.fake.ImageBuilder;
import common.util.AnimGroup;
import common.util.Data;
import common.util.stage.Replay;
import io.BCJSON;
import io.BCUReader;
import io.BCUWriter;
import jogl.GLBBB;
import jogl.util.GLIB;
import page.LoadPage;
import page.MainFrame;
import page.MainPage;
import page.Page;
import page.awt.AWTBBB;
import page.awt.BBBuilder;
import utilpc.Theme;
import utilpc.UtilPC;
import utilpc.awt.FIBI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainBCU {

	public static class AdminContext implements Context {

		@Override
		public boolean confirmDelete() {
			return Opts.conf();
		}

		@Override
		public boolean confirmDelete(File f) {
			return Opts.conf(f);
		}

		@Override
		public File getAssetFile(String string) {
			return new File("./assets/" + string);
		}

		@Override
		public File getAuxFile(String path) {
			return new File(path);
		}

		@Override
		public InputStream getLangFile(String file) {
			File f = new File(
					"./assets/lang/" + CommonStatic.Lang.LOC_CODE[CommonStatic.getConfig().lang] + "/" + file);
			if (!f.exists()) {
				String path = "common/util/lang/assets/" + file;
				return ClassLoader.getSystemResourceAsStream(path);
			}
			return Data.err(() -> new FileInputStream(f));
		}

		@Override
		public File getUserFile(String string) {
			return new File("./user/" + string);
		}

		@Override
		public File getWorkspaceFile(String relativePath) {
			return new File("./workspace/" + relativePath);
		}

		@SuppressWarnings("deprecation")
		@Override
		public void initProfile() {
			LoadPage.prog("reading assets");
			AssetLoader.load(LoadPage::prog);
			LoadPage.prog("reading BC data");
			UserProfile.getBCData().load(LoadPage::prog, LoadPage::prog);
			LoadPage.prog("reading local animations");
			Workspace.loadAnimations(null);
			LoadPage.prog("reading local animation group data");
			AnimGroup.readGroupData();
			LoadPage.prog("reading packs");
			UserProfile.loadPacks(LoadPage::prog);
			LoadPage.prog("reading basis");
			BasisSet.read();
			LoadPage.prog("reading replays");
			Replay.read();
			LoadPage.prog("finished reading");
		}

		@Override
		public void noticeErr(Exception e, ErrType t, String str) {
			if (noNeedToShow(t)) {
				System.out.println("???");
				System.out.println(str);
				e.printStackTrace();
				return;
			}
			e.printStackTrace(t == ErrType.INFO ? System.out : System.err);
			printErr(t, str);
		}

		@Override
		public boolean preload(FileDesc desc) {
			return Admin.preload(desc);
		}

		@Override
		public void printErr(ErrType t, String str) {
			if (noNeedToShow(t)) {
				System.out.println(str);
				return;
			}

			(t == ErrType.INFO ? System.out : System.err).println(str);
			if (t != ErrType.INFO)
				Opts.pop(str, "ERROR");
			if (t == ErrType.FATAL)
				CommonStatic.def.exit(false);
		}

		@Override
		public void loadProg(String str) {
			LoadPage.prog(str);
		}

		private boolean noNeedToShow(ErrType t) {
			if(t == ErrType.DEBUG)
				return true;
			else
				return !(t == ErrType.CORRUPT || t == ErrType.ERROR || t == ErrType.FATAL || t == ErrType.WARN || !MainBCU.WRITE);
		}
	}

	public static final int ver = 50029;

	public static int FILTER_TYPE = 1, prefLevel = 50;
	public static final boolean WRITE = !new File("./.project").exists();
	public static boolean preload = false, trueRun = true, loaded = false, USE_JOGL = false;
	public static boolean light = true, nimbus = false;
	public static String author = "";
	public static ImageBuilder<BufferedImage> builder;

	public static String getTime() {
		return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(MainBCU::noticeErr);
		trueRun = true;
		UserProfile.profile();
		CommonStatic.ctx = new AdminContext();
		CommonStatic.def = new UtilPC.PCItr();

		BCUWriter.logPrepare();
		BCUWriter.logSetup();
		BCUReader.readInfo();

		ImageBuilder.builder = builder = USE_JOGL ? new GLIB() : FIBI.builder;
		BBBuilder.def = USE_JOGL ? new GLBBB() : AWTBBB.INS;

		if (nimbus) {
			if (light) {
				Theme.LIGHT.setTheme();
				Page.BGCOLOR = new Color(255, 255, 255);
			} else {
				Theme.DARK.setTheme();
				Page.BGCOLOR = new Color(40, 40, 40);
			}
		} else {
			Page.BGCOLOR = new Color(255, 255, 255);
		}

		new MainFrame(Data.revVer(MainBCU.ver)).initialize();
		new Timer().start();

		BCJSON.check();
		CommonStatic.ctx.initProfile();

		BCUReader.getData$1();
		loaded = true;
		MainFrame.changePanel(new MainPage());
	}

	public static String validate(String str, char replace) {
		char[] chs = new char[] { '.', '/', '\\', ':', '*', '?', '"', '<', '>', '|' };
		for (char c : chs)
			str = str.replace(c, replace);
		return str;
	}

	private static void noticeErr(Thread t, Throwable e) {
		String msg = e.getMessage() + " in " + t.getName();
		Exception exc = (e instanceof Exception) ? (Exception) e : new Exception(msg, e);
		if (CommonStatic.ctx != null)
			CommonStatic.ctx.noticeErr(exc, ErrType.FATAL, msg);
		e.printStackTrace();
	}

}
