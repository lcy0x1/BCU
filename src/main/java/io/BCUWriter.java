package io;

import com.google.gson.JsonObject;
import common.CommonStatic;
import common.battle.BasisSet;
import common.io.DataIO;
import common.io.OutStream;
import common.io.json.JsonEncoder;
import common.pack.Context;
import common.pack.Context.ErrType;
import common.pack.Source;
import common.util.AnimGroup;
import common.util.Data;
import main.MainBCU;
import main.Opts;
import main.Printer;
import page.MainFrame;
import page.MainLocale;
import page.battle.BattleInfoPage;
import page.support.Exporter;
import page.support.Importer;
import page.view.ViewBox;
import res.AnimatedGifEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@SuppressWarnings("UnusedReturnValue")
public class BCUWriter extends DataIO {

	private static File log, ph;
	private static WriteStream ps;

	public static void logClose(boolean save) {
		writeOptions();
		if (save && MainBCU.loaded && MainBCU.trueRun) {
			writeData();
		}
		if (ps.writed) {
			ps.println("version: " + Data.revVer(MainBCU.ver));
		}
		ps.close();
		ph.deleteOnExit();
		if (log.length() == 0)
			log.deleteOnExit();
	}

	public static void logPrepare() {
		String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		log = new File("./logs/" + str + ".log");
		ph = new File("./logs/placeholder");

		if (!log.getParentFile().exists()) {
			boolean res = log.getParentFile().mkdirs();

			if(!res) {
				System.out.println("Can't create folder " + log.getParentFile().getAbsolutePath());
				return;
			}
		}

		try {
			if (!log.exists()) {
				boolean res = log.createNewFile();

				if(!res) {
					System.out.println("Can't create file " + log.getParentFile().getAbsolutePath());
					return;
				}
			}


			ps = new WriteStream(log);
		} catch (IOException e) {
			e.printStackTrace();
			Opts.pop(Opts.SECTY);
			System.exit(0);
		}
		try {
			if (ph.exists()) {
				if (!Opts.conf("<html>" + "Another BCU is running in this folder or last BCU doesn't close properly. "
						+ "<br> Are you sure to run? It might damage your save.</html>")) {
					System.exit(0);
				}
			} else {
				boolean res = ph.createNewFile();

				if(!res) {
					Opts.ioErr("Can't create file "+ph.getAbsolutePath());
				}
			}
		} catch (IOException ignored) {
		}
	}

	public static void logSetup() {
		if (MainBCU.WRITE) {
			System.setErr(ps);
			System.setOut(ps);
		}
	}

	public static PrintStream newFile(String str) {
		File f = new File(str);

		PrintStream out = null;
		try {
			Context.check(f);
			out = new PrintStream(f, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	public static boolean writeBytes(byte[] bs, String path) {
		File f = new File(path);
		FileOutputStream fos = null;
		try {
			Context.check(f);
			fos = new FileOutputStream(f);
			fos.write(bs);
			fos.close();
			return true;
		} catch (IOException e) {
			Printer.w(130, "IOE!!!");
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					Printer.w(131, "cannot close fos");
					e1.printStackTrace();
				}
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					Printer.w(139, "finally cannot close fos neither!");
					e1.printStackTrace();
				}
		}
		return false;
	}

	public static boolean writeBytes(OutStream os, String path) {
		os.terminate();
		byte[] md5 = os.MD5();
		File f = new File(path);
		boolean suc;
		if (!(suc = writeFile(os, f, md5))) {
			ps.println("failed to write file: " + f.getPath());
			if (Opts.writeErr0(f.getPath()))
				if (!(suc = writeFile(os, f, md5)))
					if (Opts.writeErr1(f.getPath()))
						new Exporter(os, Exporter.EXP_ERR);
		}
		return suc;
	}

	public static void writeData() {
		BasisSet.write();
		Source.Workspace.saveLocalAnimations();
		Source.Workspace.saveWorkspace();
		AnimGroup.writeAnimGroup();
		writeOptions();
	}

	public static void writeGIF(AnimatedGifEncoder age, String path) {
		if (path == null)
			path = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File f = new File("./img/" + path + ".gif");
		if (!f.getParentFile().exists()) {
			boolean res = f.getParentFile().mkdirs();

			if(!res) {
				Opts.ioErr("Can't create folder "+f.getParentFile().getAbsolutePath());
				return;
			}
		}
		age.start("./img/" + path + ".gif");
	}

	public static boolean writeImage(BufferedImage bimg, File f) {
		if (bimg == null)
			return false;
		boolean suc = Data.err(() -> Context.check(f));
		if (suc)
			try {
				suc = ImageIO.write(bimg, "PNG", f);
			} catch (IOException e) {
				e.printStackTrace();
				suc = false;
			}
		if (!suc) {
			ps.println("failed to write image: " + f.getPath());
			if (Opts.writeErr1(f.getPath()))
				new Exporter(bimg, Exporter.EXP_ERR);
		}
		return suc;
	}

	private static boolean writeFile(OutStream os, File f, byte[] md5) {
		boolean suc = Data.err(() -> Context.check(f));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			os.flush(fos);
			fos.close();
		} catch (IOException e) {
			suc = false;
			e.printStackTrace();
			Printer.w(130, "IOE!!!");
			Opts.ioErr("failed to write file " + f);
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		try {
			byte[] cont = Files.readAllBytes(f.toPath());
			byte[] nmd = MessageDigest.getInstance("MD5").digest(cont);
			suc &= Arrays.equals(md5, nmd);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}
		return suc;
	}

	private static void writeOptions() {
		File f = new File("./user/config.json");
		Data.err(() -> Context.check(f));
		JsonObject jo = JsonEncoder.encode(CommonStatic.getConfig()).getAsJsonObject();
		Rectangle r = MainFrame.crect;
		jo.add("crect", JsonEncoder.encode(new int[] { r.x, r.y, r.width, r.height }));
		jo.addProperty("preload", MainBCU.preload);
		jo.addProperty("transparent", ViewBox.Conf.white);
		jo.addProperty("JOGL", MainBCU.USE_JOGL);
		jo.addProperty("prefLv", MainBCU.prefLevel);
		jo.addProperty("filter", MainBCU.FILTER_TYPE);
		jo.addProperty("play_sound", BCMusic.play);
		jo.addProperty("volume_BG", BCMusic.VOL_BG);
		jo.addProperty("volume_SE", BCMusic.VOL_SE);
		jo.addProperty("volume_UI",BCMusic.VOL_UI);
		jo.addProperty("edit_lang", MainLocale.exLang);
		jo.addProperty("edit_tooltip", MainLocale.exTTT);
		jo.addProperty("large_screen", BattleInfoPage.DEF_LARGE);
		jo.addProperty("style_light", MainBCU.light);
		jo.addProperty("style_nimbus", MainBCU.nimbus);
		jo.addProperty("author", MainBCU.author);
		jo.addProperty("rowlayout", CommonStatic.getConfig().twoRow);
		String[] exp = new String[Exporter.curs.length];
		for (int i = 0; i < exp.length; i++)
			exp[i] = Exporter.curs[i] == null ? null : Exporter.curs[i].toString();
		String[] imp = new String[Importer.curs.length];
		for (int i = 0; i < imp.length; i++)
			imp[i] = Importer.curs[i] == null ? null : Importer.curs[i].toString();
		jo.add("export_paths", JsonEncoder.encode(exp));
		jo.add("import_paths", JsonEncoder.encode(imp));
		try (java.io.Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
			w.write(jo.toString());
		} catch (Exception e) {
			CommonStatic.ctx.noticeErr(e, ErrType.ERROR, "failed to write config");
		}
	}

}

class WriteStream extends PrintStream {

	protected boolean writed = false;

	public WriteStream(File file) throws FileNotFoundException {
		super(file);
	}

	@Override
	public void println(Object str) {
		super.println(str);
		writed = true;
	}

	@Override
	public void println(String str) {
		super.println(str);
		writed = true;
	}

}
