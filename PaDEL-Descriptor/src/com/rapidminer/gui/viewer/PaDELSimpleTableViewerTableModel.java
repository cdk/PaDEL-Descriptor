/*
 *  RapidMiner
 *
 *  Copyright (C) 2001-2010 by Rapid-I and the contributors
 *
 *  Complete list of developers available at our web site:
 *
 *       http://rapid-i.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package com.rapidminer.gui.viewer;

import com.rapidminer.tools.PaDELSimpleTable;
import javax.swing.table.AbstractTableModel;

/** The model for the {@link padel.gui.viewer.PaDELTableViewerTableModel}. 
 * 
 *  @author Ingo Mierswa, Yap Chun Wei
 *  @version $Id$
 */
public class PaDELSimpleTableViewerTableModel extends AbstractTableModel
{ 
    private static final long serialVersionUID = 2146832399423957373L;
    
    private transient PaDELSimpleTable table;
    
    public PaDELSimpleTableViewerTableModel(PaDELSimpleTable table) {
        this.table = table;
    }
    
    @Override
    public Class<?> getColumnClass(int column) {
        return String.class;
    }
    
    @Override
    public int getRowCount() {
        return table.getNumberOfRows();
    }

    @Override
    public int getColumnCount() {
        return table.getNumberOfColumns() + 1;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (col==0) {
            return table.getRowName(row);
        } else {
            return table.getValue(row, col-1);
        }
    }
    
    @Override
    public String getColumnName(int col) {
        if (col==0) {
            return new String("");
        } else {
            return table.getColumnName(col-1);
        }
    }
}
