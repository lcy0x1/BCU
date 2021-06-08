package page.info.edit;

import common.CommonStatic;
import common.pack.Context.ErrType;
import common.pack.Identifier;
import common.pack.IndexContainer;
import common.util.Data;
import common.util.lang.Editors;
import common.util.lang.Editors.EditControl;
import common.util.lang.Editors.Editor;
import common.util.lang.Editors.EditorGroup;
import common.util.lang.Editors.EditorSupplier;
import common.util.lang.Formatter;
import common.util.lang.ProcLang;
import common.util.unit.EneRand;
import page.*;
import utilpc.UtilPC;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public abstract class SwingEditor extends Editor {

	public static class BoolEditor extends SwingEditor {

		public final JTG input;

		public BoolEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) throws Exception {
			super(eg, field, f, edit);
			input = new JTG(ProcLang.get().get(eg.proc).get(f));
			input.setLnr(this::edit);
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(input, x, y, x0, y0, w0, h0);
		}

		@Override
		public void setData() {
			field.setData(par.obj);
			input.setSelected(field.obj != null && field.getBoolean());
			input.setEnabled(edit && field.obj != null);
		}

		@Override
		protected void add(Consumer<JComponent> con) {
			con.accept(input);
		}

		private void edit(ActionEvent fe) {
			field.set(input.isSelected());
			update();
		}

	}

	public static class EditCtrl implements EditorSupplier {

		private final boolean isEnemy;
		private final EntityEditPage table;

		public EditCtrl(boolean isEnemy, EntityEditPage table) {
			this.isEnemy = isEnemy;
			this.table = table;
		}

		@Override
		public Editor getEditor(EditControl<?> ctrl, EditorGroup group, String f, boolean edit) {
			try {
				Editors.EdiField field = ctrl.getField(f);
				Class<?> fc = field.getType();
				if (fc == int.class) {
					return new IntEditor(group, field, f, edit);
				}
				if (fc == boolean.class)
					return new BoolEditor(group, field, f, edit);
				if (fc == Identifier.class) {
					if (group.proc.equals("THEME"))
						return new IdEditor<>(group, field, f, table::getBGSup, edit);
					else if (group.proc.equals("SUMMON"))
						if (isEnemy)
							return new IdEditor<>(group, field, f, table::getEnemySup, edit);
						else
							return new IdEditor<>(group, field, f, table::getUnitSup, edit);

				}
				throw new Exception("unexpected class " + fc);
			} catch (Exception e) {
				CommonStatic.ctx.noticeErr(e, ErrType.ERROR, "failed to generate editor");
			}
			return null;
		}

	}

	public static class IdEditor<T extends IndexContainer.Indexable<?, T>> extends SwingEditor {

		private final PageSup<T> page;

		public final JBTN input;
		public final JL jl;

		public IdEditor(EditorGroup par, Editors.EdiField field, String f, PageSup<T> page, boolean edit)
				throws Exception {
			super(par, field, f, edit);
			this.page = page;
			input = new JBTN(ProcLang.get().get(par.proc).get(f));
			jl = new JL("");
			input.setLnr(this::edit);
		}

		public final void callback(Identifier<T> id) {
			field.set(id);
			update();
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(input, x, y, x0, y0, 150, h0);
			Page.set(jl, x, y, x0 + 150, y0, w0 - 150, h0);
		}

		@Override
		protected void add(Consumer<JComponent> con) {
			con.accept(input);
			con.accept(jl);
		}

		@Override
		protected void setData() {
			field.setData(par.obj);

			Object obj = field.get();

			if(obj instanceof Identifier<?>)
				if(((Identifier<?>) obj).cls == EneRand.class)
					jl.setText("" + field.get() + " [Random]");
				else
					jl.setText("" + field.get());
			else
				jl.setText("" + field.get());

			input.setEnabled(edit && field.obj != null);
		}

		private void edit(ActionEvent fe) {
			MainFrame.changePanel(page.get(this).getThisPage());
		}

	}

	public static class IntEditor extends SwingEditor {

		public final JL label;
		public final JTF input = new JTF();

		public IntEditor(EditorGroup eg, Editors.EdiField field, String f, boolean edit) throws Exception {
			super(eg, field, f, edit);
			label = new JL(ProcLang.get().get(eg.proc).get(f));
			input.setLnr(this::edit);
		}

		@Override
		public void resize(int x, int y, int x0, int y0, int w0, int h0) {
			Page.set(label, x, y, x0, y0, 150, h0);
			Page.set(input, x, y, x0 + 150, y0, w0 - 150, h0);
		}

		@Override
		public void setData() {
			field.setData(par.obj);
			if (field.obj == null)
				input.setText("");
			else
				input.setText("" + field.getInt());
			input.setEnabled(edit && field.obj != null);
		}

		@Override
		protected void add(Consumer<JComponent> con) {
			con.accept(label);
			con.accept(input);
		}

		@SuppressWarnings("ConstantConditions")
		private void edit(FocusEvent fe) {
			field.setInt(Data.ignore(() -> CommonStatic.parseIntN(input.getText())));
			update();
		}

	}

	public interface PageSup<T extends IndexContainer.Indexable<?, T>> {

		SupPage<T> get(IdEditor<T> editor);

	}

	public static class SwingEG extends EditorGroup {

		public final JL jlm;

		public SwingEG(int ind, boolean edit, Runnable cb, Formatter.Context ctx) {
			super(Data.Proc.getName(ind), edit, cb);
			jlm = new JL(getItem(ctx));
			jlm.setIcon(UtilPC.createIcon(1, ind));
		}

		@Override
		public void setData(Object obj) {
			super.setData(obj);
			jlm.getLSC().update();
		}
	}

	public boolean edit;

	public SwingEditor(EditorGroup par, Editors.EdiField field, String f, boolean edit) throws Exception {
		super(par, field, f);
		this.edit = edit;
	}

	public abstract void resize(int x, int y, int x0, int y0, int w0, int h0);

	protected abstract void add(Consumer<JComponent> con);

}
