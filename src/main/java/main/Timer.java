package main;

import page.MainFrame;

import javax.swing.*;

public strictfp class Timer extends Thread {

	public static int p = 33;
	public static int inter = 0;

	protected static int state;

	private static int delay = 0;
	private Inv thr = null;

	@Override
	public void run() {
		while (true) {
			long m = System.currentTimeMillis();
			state = 0;
			SwingUtilities.invokeLater(thr = new Inv());
			try {
				boolean end = false;
				while (!end) {
					synchronized (thr) {
						end = state == 1;
					}
					if (!end)
						sleep(1);
				}
				thr.join();
				delay = (int) (System.currentTimeMillis() - m);
				inter = (inter * 9 + 100 * delay / p) / 10;
				int sle = delay >= p ? 1 : p - delay;
				sleep(sle);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}

strictfp class Inv extends Thread {

	@Override
	public void run() {
		MainFrame.timer(-1);
		synchronized (this) {
			Timer.state = 1;
		}

	}

}