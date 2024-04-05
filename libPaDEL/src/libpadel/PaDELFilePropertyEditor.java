/**
 * L2FProd.com Common Components 7.3 License.
 *
 * Copyright 2005-2007 L2FProd.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libpadel;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.swing.ComponentFactory;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.l2fprod.common.swing.PercentLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * FilePropertyEditor. <br>
 *  
 */
public class PaDELFilePropertyEditor extends AbstractPropertyEditor {

  protected JTextField textfield;
  private JButton button;
  private JButton cancelButton;
    
  public PaDELFilePropertyEditor() {
    this(true);
  }
  
  public PaDELFilePropertyEditor(boolean asTableEditor) {
    editor = new JPanel(new PercentLayout(PercentLayout.HORIZONTAL, 0)) {
      public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textfield.setEnabled(enabled);
        button.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
      }
    };
    ((JPanel)editor).add("*", textfield = new JTextField());
    ((JPanel)editor).add(button = ComponentFactory.Helper.getFactory()
      .createMiniButton());
    if (asTableEditor) {
      textfield.setBorder(LookAndFeelTweaks.EMPTY_BORDER);
    }    
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectFile();
      }
    });
    ((JPanel)editor).add(cancelButton = ComponentFactory.Helper.getFactory()
      .createMiniButton());
    cancelButton.setText("X");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        selectNull();
      }
    });
  }
  
  public Object getValue() {
    return ((JTextComponent)textfield).getText();
  }

  public void setValue(Object value) {
    if (value == null) {
      ((JTextComponent)textfield).setText("");
    } else {
      ((JTextComponent)textfield).setText(String.valueOf(value));
    }
  }

  protected void selectFile() {
    
  }

  /**
   * Placeholder for subclasses to customize the JFileChooser shown to select a
   * file.
   * 
   * @param chooser
   */
  protected void customizeFileChooser(JFileChooser chooser) {    
  }
  
  protected void selectNull() {
    Object oldFile = (String)getValue();
    textfield.setText("");
    firePropertyChange(oldFile, null);
  }
  
}