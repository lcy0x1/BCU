package utilpc;

import com.google.common.io.Files;
import common.CommonStatic;
import common.CommonStatic.ImgReader;
import common.CommonStatic.Itf;
import common.battle.data.PCoin;
import common.io.InStream;
import common.pack.Context;
import common.pack.Source;
import common.pack.Source.ResourceLocation;
import common.system.VImg;
import common.system.fake.FakeImage;
import common.system.fake.ImageBuilder;
import common.system.files.FDByte;
import common.util.Data;
import common.util.anim.ImgCut;
import common.util.anim.MaAnim;
import common.util.anim.MaModel;
import common.util.pack.Background;
import common.util.stage.Music;
import common.util.unit.Form;
import common.util.unit.Trait;
import io.BCMusic;
import io.BCUReader;
import io.BCUWriter;
import utilpc.awt.FG2D;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UtilPC {

	public static class PCItr implements Itf {

		@Deprecated
		private static class MusicReader implements ImgReader {

			private final int pid, mid;

			private MusicReader(int p, int m) {
				pid = p;
				mid = m;
			}

			@Override
			@Deprecated
			public File readFile(InStream is) {
				byte[] bs = is.subStream().nextBytesI();
				String path = "./pack/music/" + Data.hex(pid) + "/" + Data.trio(mid) + ".ogg";
				File f = CommonStatic.def.route(path);
				Data.err(() -> Context.check(f));
				try {
					Files.write(bs, f);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return f;
			}

			@Override
			public FakeImage readImg(String str) {
				return null;
			}

			@Override
			public VImg readImgOptional(String str) {
				return null;
			}

		}

		@Deprecated
		private static class PCAL implements Source.AnimLoader {

			private String name;
			private FakeImage num;
			private final ImgCut imgcut;
			private final MaModel mamodel;
			private final MaAnim[] anims;
			private VImg uni, edi;

			private PCAL(InStream is) {
				name = "local animation";
				num = FakeImage.read(is.nextBytesI());
				imgcut = ImgCut.newIns(new FDByte(is.nextBytesI()));
				mamodel = MaModel.newIns(new FDByte(is.nextBytesI()));
				int n = is.nextInt();
				anims = new MaAnim[n];
				for (int i = 0; i < n; i++)
					anims[i] = MaAnim.newIns(new FDByte(is.nextBytesI()));
				if (!is.end()) {
					VImg vimg = ImageBuilder.toVImg(is.nextBytesI());
					if (vimg.getImg().getHeight() == 32)
						edi = vimg;
					else
						uni = vimg;
				}
				if (!is.end())
					uni = ImageBuilder.toVImg(is.nextBytesI());
			}

			private PCAL(InStream is, ImgReader r) {
				is.nextString();
				num = r.readImg(is.nextString());
				edi = r.readImgOptional(is.nextString());
				uni = r.readImgOptional(is.nextString());
				imgcut = ImgCut.newIns(new FDByte(is.nextBytesI()));
				mamodel = MaModel.newIns(new FDByte(is.nextBytesI()));
				int n = is.nextInt();
				anims = new MaAnim[n];
				for (int i = 0; i < n; i++)
					anims[i] = MaAnim.newIns(new FDByte(is.nextBytesI()));
			}

			@Override
			public VImg getEdi() {
				return edi;
			}

			@Override
			public ImgCut getIC() {
				return imgcut;
			}

			@Override
			public MaAnim[] getMA() {
				return anims;
			}

			@Override
			public MaModel getMM() {
				return mamodel;
			}

			@Override
			public ResourceLocation getName() {
				return new ResourceLocation(null, name);
			}

			@Override
			public FakeImage getNum() {
				return num;
			}

			@Override
			public int getStatus() {
				return 1;
			}

			@Override
			public VImg getUni() {
				return uni;
			}

		}

		@Override
		public void exit(boolean save) {
			CommonStatic.ctx.noticeErr(() -> BCUWriter.logClose(save), Context.ErrType.ERROR, "Save failed...");
			System.exit(0);
		}

		@Override
		public long getMusicLength(Music f) {
			if (f.data == null) {
				return -1;
			}

			try {
				OggTimeReader otr = new OggTimeReader(f);

				return otr.getTime();
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
		}

		@Override
		@Deprecated
		public ImgReader getMusicReader(int pid, int mid) {
			return new MusicReader(pid, mid);
		}

		@Override
		@Deprecated
		public ImgReader getReader(File f) {
			return null;
		}

		@Override
		@Deprecated
		public Source.AnimLoader loadAnim(InStream is, ImgReader r) {
			return r == null ? new PCAL(is) : new PCAL(is, r);
		}

		@Override
		@Deprecated
		public InStream readBytes(File fi) {
			return BCUReader.readBytes(fi);
		}

		@Override
		@Deprecated
		public File route(String path) {
			return new File(path);
		}

		@Override
		public void setSE(int ind) {
			BCMusic.setSE(ind);
		}

	}

	public static ImageIcon getBg(Background bg, int w, int h) {
		double r = h / 1100.0;
		int fw = (int) (768 * r);
		int fh = (int) (510 * r);
		bg.check();
		BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) temp.getGraphics();
		FG2D fg = new FG2D(g);
		if (bg.top && bg.parts.length > Background.TOP)
			for (int i = 0; i * fw < w; i++)
				fg.drawImage(bg.parts[Background.TOP], fw * i, 0, fw, fh);
		else {
			fg.gradRect(0, 0, w, fh, 0, 0, bg.cs[0], 0, fh, bg.cs[1]);
		}
		for (int i = 0; i * fw < w; i++)
			fg.drawImage(bg.parts[Background.BG], fw * i, fh, fw, fh);
		fg.gradRect(0, fh * 2, w, h - fh * 2, 0, fh * 2, bg.cs[2], 0, h, bg.cs[3]);
		g.dispose();
		return new ImageIcon(temp);
	}

	public static BufferedImage getIcon(int type, int id) {
		type += id / 100;
		id %= 100;
		if (CommonStatic.getBCAssets().icon[type][id] == null)
			return null;
		return (BufferedImage) CommonStatic.getBCAssets().icon[type][id].getImg().bimg();
	}

	public static ImageIcon createIcon(int type, int id) {
		BufferedImage img = getIcon(type, id);
		if (img != null)
			return new ImageIcon(img);
		return null;
	}

	public static ImageIcon getIcon(VImg v) {
		FakeImage img = v.getImg();
		if (img == null)
			return null;
		if (img.bimg() == null)
			return null;
		return new ImageIcon((Image) img.bimg());
	}

	public static String[] lvText(Form f, int[] lvs) {
		PCoin pc = f.du.getPCoin();
		if (pc == null)
			return new String[] { "Lv." + lvs[0], "" };
		else {
			String[] TraitsHolder = new String[pc.trait.size()];
			for (int i = 0 ; i < pc.trait.size() ; i++) {
				Trait trait = pc.trait.get(i);
				if (trait.BCTrait)
					TraitsHolder[i] = Interpret.TRAIT[trait.id.id];
				else
					TraitsHolder[i] = trait.name;
			}
			StringBuilder lab = new StringBuilder();

			if(pc.trait.size() > 0) {
				lab.append("[").append(Interpret.getTrait(TraitsHolder, 0)).append("]").append(" ");
			}

			lab.append(Interpret.PCTX[pc.info.get(0)[0]]);

			StringBuilder str = new StringBuilder("Lv." + lvs[0] + ", {");
			for (int i = 1; i < pc.info.size(); i++) {
				str.append(lvs[i]).append(",");
				lab.append(", ").append(getPCoinAbilityText(pc, i));
			}
			str.append(lvs[pc.info.size()]).append("}");
			return new String[] {str.toString(), lab.toString()};
		}
	}

	public static String getPCoinAbilityText(PCoin pc, int index) {
		if(index < 0 || index >= pc.info.size())
			return null;

		return Interpret.PCTX[pc.info.get(index)[0]];
	}
}
