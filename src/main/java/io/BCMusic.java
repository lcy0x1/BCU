package io;

import common.pack.Identifier;
import common.pack.UserProfile;
import common.util.Data;
import common.util.stage.Music;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class BCMusic extends Data {
	private static final int INVALID = 0, CANNON_CHARGE = 1, TOUCH = 2;
	private static final int FACTOR = 20, TOT = 125;
	private static final byte[][] CACHE = new byte[TOT][];

	public static boolean play = true;
	public static Identifier<Music> music = null;
	public static int VOL_BG = 20, VOL_SE = 20, VOL_UI = 20;
	private static boolean[] secall = new boolean[TOT];

	public static BCPlayer BG;
	public static BCPlayer End;

	private static BCPlayer[] hit;
	private static BCPlayer[] hit1;
	private static BCPlayer[] baseHit;
	private static BCPlayer[] UI;

	private static boolean h, h1, bh;

	protected static Map<Integer, ArrayDeque<BCPlayer>> sounds = new HashMap<>();

	@SuppressWarnings("UnusedAssignment")
	public static void clear() {
		for (ArrayDeque<BCPlayer> clips : sounds.values()) {
			while (true) {
				BCPlayer c = clips.poll();

				if (c != null) {
					c.release();
					c = null;
				} else {
					break;
				}
			}
		}

		if (hit != null) {
			for (int i = 0; i < hit.length; i++) {
				if(hit[i] == null)
					continue;

				hit[i].release();
				hit[i] = null;
			}

			hit = null;
		}

		if (hit1 != null) {
			for (int i = 0; i < hit1.length; i++) {
				if(hit1[i] == null)
					continue;

				hit1[i].release();
				hit1[i] = null;
			}

			hit1 = null;
		}

		if (baseHit != null) {
			for (int i = 0; i < baseHit.length; i++) {
				if(baseHit[i] == null)
					continue;

				baseHit[i].release();
				baseHit[i] = null;
			}

			baseHit = null;
		}

		if (BG != null) {
			BG.release();
			BG = null;
		}

		if (UI != null) {
			for(int i = 0; i < UI.length; i++) {
				if(UI[i] == null)
					continue;

				UI[i].release();
				UI[i] = null;
			}

			UI = null;
		}

		sounds.clear();
	}

	public static synchronized void clickSound() {
		if (!play || VOL_SE == 0)
			return;
		try {
			if (CACHE[11] == null)
				loadSound(11, UserProfile.getBCData().musics.get(11), getVol(VOL_SE), false, 0);
			else
				loadSound(11, CACHE[11], getVol(VOL_SE), false, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void flush(boolean allow, boolean baseHP) {
		if (hit == null) {
			hit = new BCPlayer[2];

			for (int i = 0; i < hit.length; i++) {
				try {
					hit[i] = new BCPlayer(openFile(UserProfile.getBCData().musics.get(20)), 20, false);
					hit[i].setVolume(VOL_SE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (hit1 == null) {
			hit1 = new BCPlayer[2];

			for (int i = 0; i < hit1.length; i++) {
				try {
					hit1[i] = new BCPlayer(openFile(UserProfile.getBCData().musics.get(21)), 21, false);
					hit1[i].setVolume(VOL_SE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (baseHit == null) {
			baseHit = new BCPlayer[2];

			for (int i = 0; i < baseHit.length; i++) {
				try {
					baseHit[i] = new BCPlayer(openFile(UserProfile.getBCData().musics.get(22)), 22, false);
					baseHit[i].setVolume(VOL_SE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if(UI == null) {
			UI = new BCPlayer[3];

			try {
				UI[0] = new BCPlayer(openFile(UserProfile.getBCData().musics.get(15)), 15, false);
				UI[1] = new BCPlayer(openFile(UserProfile.getBCData().musics.get(28)), 28, false);
				UI[2] = new BCPlayer(openFile(UserProfile.getBCData().musics.get(10)), 10, false);

				UI[0].setVolume(VOL_UI);
				UI[1].setVolume(VOL_UI);
				UI[2].setVolume(VOL_UI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (baseHP) {
			if (End == null) {
				int sfx = 0;
				if (secall[SE_VICTORY])
					sfx = SE_VICTORY;
				else if (secall[SE_DEFEAT])
					sfx = SE_DEFEAT;
				if (sfx != 0)
					try {
						End = new BCPlayer(openFile(UserProfile.getBCData().musics.get(sfx)), sfx, false);
						End.setVolume(VOL_SE);
						if (CACHE[sfx] == null)
							loadSound(sfx, UserProfile.getBCData().musics.get(sfx), getVol(VOL_SE), false, 0);
						else
							loadSound(sfx, CACHE[sfx], getVol(VOL_SE), false, 0);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		} else if (End != null) {
			End.release();
			End = null;
			secall[SE_DEFEAT] = false;
			secall[SE_VICTORY] = false;
		}

		for (int i = 0; i < TOT; i++)
			if (secall[i] && allow && !baseHP)
				try {
					if (CACHE[i] == null)
						loadSound(i, UserProfile.getBCData().musics.get(i), getVol(VOL_SE), false, 0);
					else
						loadSound(i, CACHE[i], getVol(VOL_SE), false, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
		secall = new boolean[TOT];
	}

	public static synchronized void play(Identifier<Music> mus1, long loop) {
		music = mus1;
		Music f = Identifier.get(music);
		if (f != null)
			setBG(f, loop);
	}

	public static void preload() {
		for (int i : SE_ALL)
			BCMusic.CACHE[i] = UserProfile.getBCData().musics.get(i).data.getBytes();
	}

	public static synchronized void setBG(Music f, long loop) {
		if (!play)
			return;
		try {
			if (BG != null) {
				BG.release();
			}
			loadSound(-1, f, getVol(VOL_BG), true, loop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized void setBGVol(int vol) {
		VOL_BG = vol;

		if (BG != null) {
			BG.setVolume(vol);
		}
	}

	public static synchronized void setSE(int ind) {
		if (!play || VOL_SE == 0)
			return;
		secall[ind] = true;
	}

	public static synchronized void setSEVol(int vol) {
		VOL_SE = vol;

		for (ArrayDeque<BCPlayer> players : sounds.values()) {
			players.forEach((player) -> player.setVolume(vol));
		}
	}

	public static synchronized void setUIVol(int vol) {
		VOL_UI = vol;

		if(UI != null) {
			for (BCPlayer bcPlayer : UI) {
				bcPlayer.setVolume(vol);
			}
		}
	}

	public static synchronized void stopAll() {
		if (BG != null)
			BG.stop();

		for (ArrayDeque<BCPlayer> players : sounds.values()) {
			players.forEach(BCPlayer::stop);
		}

		clear();
	}

	private static float getVol(int vol) {
		return FACTOR * ((float) Math.log10(vol) - 2);
	}

	private static void loadSound(int ind, byte[] bytes, float vol, boolean b, long loop) throws Exception {
		// set ind to -1 to tell it's BG
		if (b) {
			Clip c = openFile(bytes);
			c.loop(Clip.LOOP_CONTINUOUSLY);
			if (BG != null) {
				BG.stop();
			}
			BG = new BCPlayer(c, -1, loop, true);
			BG.setVolume(VOL_BG);
			BG.start();
			return;
		}
		ArrayDeque<BCPlayer> clips = sounds.get(ind);

		if (clips == null) {
			clips = new ArrayDeque<>();
			sounds.put(ind, clips);
		}

		BCPlayer player = clips.poll();

		if (player != null) {
			player.rewind();
			player.start();
		} else {
			switch (ind) {
				case SE_SPEND_FAIL:
					if(UI != null && !UI[INVALID].isPlaying()) {
						if(!UI[TOUCH].isPlaying())
							UI[TOUCH].start();

						UI[INVALID].start();
					}
					break;
				case SE_SPEND_SUC:
				case SE_SPEND_REF:
					if(!UI[TOUCH].isPlaying())
						UI[TOUCH].start();

					loadSound(ind, openFile(bytes), false, VOL_UI);
					break;
				case SE_HIT_0:
					if (hit != null) {
						if (h) {
							hit[0].stop();
							hit[0].rewind();
							hit[1].start();
						} else {
							hit[1].stop();
							hit[1].rewind();
							hit[0].start();
						}
						h = !h;
					}
					break;
				case SE_HIT_1:
					if (hit1 != null) {
						if (h1) {
							hit1[0].stop();
							hit1[0].rewind();
							hit1[1].start();
						} else {
							hit1[1].stop();
							hit1[1].rewind();
							hit1[0].start();
						}
						h1 = !h1;
					}
					break;
				case SE_HIT_BASE:
					if (baseHit != null) {
						if (bh) {
							baseHit[0].stop();
							baseHit[0].rewind();
							baseHit[1].start();
						} else {
							baseHit[1].stop();
							baseHit[1].rewind();
							baseHit[0].start();
						}
						bh = !bh;
					}
					break;
				case 28:
					if(UI != null && !UI[CANNON_CHARGE].isPlaying()) {
						UI[CANNON_CHARGE].start();
					}
					break;
				default:
					loadSound(ind, openFile(bytes), false);
			}
		}
	}

	private static void loadSound(int ind, Clip c, boolean loop) {
		BCPlayer player = new BCPlayer(c, ind, loop);
		player.setVolume(VOL_SE);

		player.start();
	}

	private static void loadSound(int ind, Clip c, boolean loop, int vol) {
		BCPlayer player = new BCPlayer(c, ind, loop);
		player.setVolume(vol);

		player.start();
	}

	private static void loadSound(int ind, Music file, float vol, boolean b, long loop) throws Exception {
		// set ind to -1 to tell it's BG
		if (b) {
			Clip c = openFile(file);
			c.loop(Clip.LOOP_CONTINUOUSLY);
			if (BG != null) {
				BG.stop();
				BG.release();
			}
			BG = new BCPlayer(c, -1, loop, true);
			BG.setVolume(VOL_BG);
			BG.start();
			return;
		}
		ArrayDeque<BCPlayer> clips = sounds.get(ind);
		if (clips == null) {
			clips = new ArrayDeque<>();
			sounds.put(ind, clips);
			loadSound(ind, openFile(file), false);
		} else {
			BCPlayer player = clips.poll();
			if (player != null) {
				player.rewind();
				player.start();
			} else {
				loadSound(ind, openFile(file), false);
			}
		}
	}

	private static Clip openFile(byte[] data) throws Exception {
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		AudioInputStream raw = AudioSystem.getAudioInputStream(is);
		AudioFormat rf = raw.getFormat();
		int ch = rf.getChannels();
		float rate = rf.getSampleRate();
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
		AudioInputStream stream = AudioSystem.getAudioInputStream(format, raw);
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		Clip line = (Clip) AudioSystem.getLine(info);
		line.open(stream);
		raw.close();
		stream.close();
		return line;
	}

	private static Clip openFile(Music file) throws Exception {
		AudioInputStream raw = AudioSystem.getAudioInputStream(file.data.getStream());
		AudioFormat rf = raw.getFormat();
		int ch = rf.getChannels();
		float rate = rf.getSampleRate();
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
		AudioInputStream stream = AudioSystem.getAudioInputStream(format, raw);
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		Clip line = (Clip) AudioSystem.getLine(info);
		line.open(stream);
		raw.close();
		stream.close();
		return line;
	}
}
