/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libpadel;

import com.jidesoft.grid.FolderNameCellEditor;
import com.jidesoft.grid.Property;
import com.jidesoft.swing.JideSwingUtilities;

/**
 *
 * @author phayapc
 */
public class PaDELFolderProperty extends Property
{
    private Object value_;

    public PaDELFolderProperty(String name, String description, String category)
    {
	super(name, description, String.class, category);
	setEditorContext(FolderNameCellEditor.CONTEXT);
    }

    public PaDELFolderProperty(String name, String description, String category, String defaultValue)
    {
	super(name, description, String.class, category);
	setEditorContext(FolderNameCellEditor.CONTEXT);
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
