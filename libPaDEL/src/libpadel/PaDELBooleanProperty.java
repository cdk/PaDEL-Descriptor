/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libpadel;

import com.jidesoft.grid.BooleanCheckBoxCellEditor;
import com.jidesoft.grid.Property;
import com.jidesoft.swing.JideSwingUtilities;

/**
 *
 * @author phayapc
 */
public class PaDELBooleanProperty extends Property
{
    private Object value_;

    public PaDELBooleanProperty(String name, String description, String category)
    {
	super(name, description, Boolean.class, category);
	setEditorContext(BooleanCheckBoxCellEditor.CONTEXT);
    }

    public PaDELBooleanProperty(String name, String description, String category, Boolean defaultValue)
    {
	super(name, description, Boolean.class, category);
	setEditorContext(BooleanCheckBoxCellEditor.CONTEXT);
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

}
