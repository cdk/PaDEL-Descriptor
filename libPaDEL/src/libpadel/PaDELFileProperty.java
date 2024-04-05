/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package libpadel;

import com.jidesoft.combobox.FileChooserPanel;
import com.jidesoft.combobox.FileNameChooserExComboBox;
import com.jidesoft.combobox.PopupPanel;
import com.jidesoft.converter.StringConverter;
import com.jidesoft.grid.*;
import com.jidesoft.swing.JideSwingUtilities;
import java.io.File;
import javax.swing.CellEditor;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author phayapc
 */
public class PaDELFileProperty extends Property
{
    private Object value_;
    private CellEditorFactory cellEditor_;

    public PaDELFileProperty(String name, String description, String category)
    {
	super(name, description, String.class, category);
	setConverterContext(StringConverter.CONTEXT_FILENAME);
	setEditorContext(new EditorContext("Choose a file"));
	RegisterEditor(false, "Choose a file", false, null, null, null);
    }

    public PaDELFileProperty(String name, String description, String category, String defaultValue)
    {
	super(name, description, String.class, category);
	setConverterContext(StringConverter.CONTEXT_FILENAME);
	setEditorContext(new EditorContext("Choose a file"));
	setValue(defaultValue);
	RegisterEditor(false, "Choose a file", false, null, null, null);
    }

    public PaDELFileProperty(String name, String description, String category, String defaultValue, boolean isSaveDialog, String title, boolean includeDirectories, String[] extensions, String[] descriptions)
    {
	super(name, description, String.class, category);
	setConverterContext(StringConverter.CONTEXT_FILENAME);
	setEditorContext(new EditorContext(title));
	setValue(defaultValue);
	RegisterEditor(isSaveDialog, title, includeDirectories, extensions, descriptions, null);
    }

    public PaDELFileProperty(String name, String description, String category, String defaultValue, boolean isSaveDialog, String title, boolean includeDirectories, String[] extensions, String[] descriptions, String startDirectory)
    {
	super(name, description, String.class, category);
	setConverterContext(StringConverter.CONTEXT_FILENAME);
	setEditorContext(new EditorContext(title));
	setValue(defaultValue);
	RegisterEditor(isSaveDialog, title, includeDirectories, extensions, descriptions, startDirectory);
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

    private void RegisterEditor(final boolean isSaveDialog, final String title, final boolean includeDirectories, final String[] extensions, final String[] descriptions, final String startDirectory)
    {
	CellEditorManager.unregisterEditor(String.class, new EditorContext(title));

	cellEditor_ = new CellEditorFactory()
	{
	    public CellEditor create()
	    {
		return new FileNameCellEditor()
		{
		    @Override
		    protected FileNameChooserExComboBox createFileNameChooserComboBox()
		    {
			final FileNameChooserExComboBox comboBox = new FileNameChooserExComboBox()
			{
			    @Override
			    public PopupPanel createPopupComponent()
			    {
				FileChooserPanel panel = new FileChooserPanel("" + getSelectedItem())
				{
				    @Override
				    protected JFileChooser createFileChooser()
				    {
					String startPath = startDirectory!=null ? startDirectory : getCurrentDirectoryPath();
					JFileChooser fileChooser = new JFileChooser(startPath);
					if (isSaveDialog)
					{
					    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
					}
					else
					{
					    fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
					}
					if (includeDirectories)
					{
					    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					}
					else
					{
					    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					}
					if (extensions!=null && descriptions!=null && extensions.length==descriptions.length)
					{
					    for (int i=0, endi=extensions.length; i<endi; ++i)
					    {
						final String extension = extensions[i];
						final String description = descriptions[i];
						fileChooser.addChoosableFileFilter(new FileFilter()
						{
						    public boolean accept(File f)
						    {
							return f.isDirectory() || f.getName().endsWith(extension);
						    }

						    public String getDescription()
						    {
							return description;
						    }
						});
					    }
					}
					try
					{
					    fileChooser.setSelectedFile(new File(getCurrentDirectoryPath()));
					}
					catch (Exception e)
					{
					    // ignore
					}
					return fileChooser;
				    }
				};
				panel.setTitle(title);
				return panel;
			    }
			};
			comboBox.setEditable(true);
			return comboBox;
		    }
		};
	    };
	};
	CellEditorManager.registerEditor(String.class, cellEditor_, new EditorContext(title));
    }
}
