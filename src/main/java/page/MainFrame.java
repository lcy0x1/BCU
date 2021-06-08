package page;

import common.system.P;
import io.BCMusic;
import main.Opts;
import main.Printer;
import utilpc.PP;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;
import java.util.List;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static int x = 0, y = 0;
	public static Rectangle rect = null, crect = null;
	public static Font font = null;
	public static boolean closeClicked = false;

	public static String fontType = "Dialog";
	public static int fontStyle = Font.PLAIN;
	public static final int fontSize = 24;
	public static MainFrame F;

	private static Page mainPanel = null;

	public static void changePanel(Page p) {
		BCMusic.clickSound();
		F.FchangePanel(p);
	}

	public static void exitAll() {
		if (mainPanel != null)
			mainPanel.exitAll();
	}

	public static Page getPanel() {
		return mainPanel;
	}

	public static void timer(int t) {
		if (mainPanel != null && !F.changingPanel)
			mainPanel.timer(t);
	}

	protected static void resized() {
		F.Fresized();
	}

	private static void setFonts(int f) {
		List<Object> ks = new ArrayList<>();
		List<Object> fr = new ArrayList<>();
		font = new Font(fontType, fontStyle, f);
		FontUIResource fontRes = new FontUIResource(font);
		for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				ks.add(key);
				fr.add(fontRes);
			}
		}
		for (int i = 0; i < ks.size(); i++)
			UIManager.put(ks.get(i), fr.get(i));
	}

	private boolean settingsize = false, changingPanel = false;

	public MainFrame(String ver) {
		super(Page.get(MainLocale.PAGE, "title") + " Ver " + ver);
		//setIcon();
		setLayout(null);
		addListener();
		sizer();
	}

	// Currently unused function used to officially add an icon to BCU, it currently works but will be left unused until an icon is oficially added to BCU assets
	private void setIcon() {
		try {
			Image icons = ImageIO.read(new File("IconDirectoryHere.png"));
			super.setIconImage(icons);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initialize() {
		F = this;
		changePanel(new LoadPage());
		Fresized();
	}

	public void sizer() {
		if (crect == null) {
			PP screen = new PP(Toolkit.getDefaultToolkit().getScreenSize());
			rect = new PP(0, 0).toRectangle(new P(screen.x, screen.y));
			setBounds(rect);
			setVisible(true);
			int nx = rect.width - getRootPane().getWidth();
			int ny = rect.height - getRootPane().getHeight();
			crect = rect;
			if (nx != x || ny != y) {
				x = nx;
				y = ny;
			}
		} else {
			rect = crect;
			setBounds(rect);
			setVisible(true);
		}
	}

	private void addListener() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent arg0) {
				if (mainPanel != null)
					mainPanel.windowActivated();
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				if(!closeClicked) {
					changePanel(new SavePage());
				} else {
					Opts.warnPop("Saving progress...\nPlease wait!", "Saving");
				}
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				if (mainPanel != null)
					mainPanel.windowDeactivated();
			}
		});

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == KeyEvent.KEY_PRESSED && mainPanel != null)
				mainPanel.keyPressed((KeyEvent) event);
			if (event.getID() == KeyEvent.KEY_RELEASED && mainPanel != null)
				mainPanel.keyReleased((KeyEvent) event);
			if (event.getID() == KeyEvent.KEY_TYPED && mainPanel != null)
				mainPanel.keyTyped((KeyEvent) event);
		}, AWTEvent.KEY_EVENT_MASK);

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == MouseEvent.MOUSE_PRESSED && mainPanel != null)
				mainPanel.mousePressed((MouseEvent) event);
			if (event.getID() == MouseEvent.MOUSE_RELEASED && mainPanel != null)
				mainPanel.mouseReleased((MouseEvent) event);
			if (event.getID() == MouseEvent.MOUSE_CLICKED && mainPanel != null)
				mainPanel.mouseClicked((MouseEvent) event);
		}, AWTEvent.MOUSE_EVENT_MASK);

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == MouseEvent.MOUSE_MOVED && mainPanel != null)
				mainPanel.mouseMoved((MouseEvent) event);
			if (event.getID() == MouseEvent.MOUSE_DRAGGED && mainPanel != null)
				mainPanel.mouseDragged((MouseEvent) event);
		}, AWTEvent.MOUSE_MOTION_EVENT_MASK);

		Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
			if (event.getID() == MouseEvent.MOUSE_WHEEL && mainPanel != null)
				mainPanel.mouseWheel((MouseEvent) event);
		}, AWTEvent.MOUSE_WHEEL_EVENT_MASK);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent arg0) {
				setCrect();
			}

			@Override
			public void componentResized(ComponentEvent arg0) {
				Fresized();
				setCrect();
			}
		});
	}

	private void FchangePanel(Page p) {
		if (p == null)
			return;
		changingPanel = true;
		if (mainPanel != null)
			if (p.getFront() == mainPanel)
				mainPanel.leave();
			else
				mainPanel.exit();
		add(p);
		if (mainPanel != null)
			remove(mainPanel);
		mainPanel = p;
		validate();
		p.renew();
		repaint();
		changingPanel = false;
	}

	private void Fresized() {
		if (settingsize)
			return;
		settingsize = true;
		int w = getRootPane().getWidth();
		int h = getRootPane().getHeight();
		try {
			setFonts(Page.size(w, h, fontSize));
		} catch (ConcurrentModificationException cme) {
			Printer.p("MainFrame", 217, "Failed to set Font");
		}
		if (mainPanel != null)
			mainPanel.componentResized(w, h);
		settingsize = false;
	}

	private void setCrect() {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle r = getBounds();
		if ((r.x + r.width) >= 0 && r.y >= 0 && r.x < d.width && (r.y - r.height) < d.height)
			crect = r;
	}
}
