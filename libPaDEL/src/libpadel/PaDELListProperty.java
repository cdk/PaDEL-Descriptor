/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libpadel;

import com.jidesoft.grid.*;
import com.jidesoft.swing.JideSwingUtilities;
import javax.swing.CellEditor;

/**
 *
 * @author phayapc
 */
public class PaDELListProperty extends Property
{
    private Object value_;
    CellEditorFactory cellEditor_;

    public PaDELListProperty(String name, String description, String category)
    {
	super(name, description, String.class, category);
	setEditorContext(new EditorContext("PaDELList"));
    }

    public PaDELListProperty(String name, String description, String category, String[] values)
    {
	super(name, description, String.class, category);
	setEditorContext(new EditorContext("PaDELList"));
	RegisterEditor(values);
    }

    public PaDELListProperty(String name, String description, String category, String[] values, String defaultValue)
    {
	super(name, description, String.class, category);
	setEditorContext(new EditorContext("PaDELList"));
	RegisterEditor(values);
	setValue(defaultValue);
    }

    @Override
    public void setValue(Object value)
    {
	Object old = getValue();
	if (!JideSwingUtilities.equals(old, value))
	{
	    value_ = value;
	    firePropertyChange(PROPERTY_VALUE, old, value);
	}
    }

    @Override
    public Object getValue()
    {
	return value_;
    }

    public void setList(String[] values)
    {
	RegisterEditor(values);
    }

    private void RegisterEditor(final String[] values)
    {
	CellEditorManager.unregisterEditor(String.class, new EditorContext("PaDELList"));

	cellEditor_ = new CellEditorFactory()
	{
	    public CellEditor create()
	    {
		ListComboBoxCellEditor lcbce = new ListComboBoxCellEditor();
		lcbce.setPossibleValues(values);
		return lcbce;
	    }
	};

	CellEditorManager.registerEditor(String.class, cellEditor_, new EditorContext("PaDELList"));
    }
}
