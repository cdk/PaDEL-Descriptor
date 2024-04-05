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

import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.table.JTableHeader;
import com.rapidminer.gui.tools.ExtendedJTable;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.tools.PaDELSimpleTable;


/**
 * Can be used to display (parts of) a Table by means of a JTable.
 * 
 * @author Ingo Mierswa, Yap Chun Wei
 * @version $Id$
 */
public class PaDELSimpleTableViewerTable extends ExtendedJTable {
    
    private static final long serialVersionUID = 2256336437732071278L;
 
    private PaDELSimpleTableViewerTableModel model;
    
    public PaDELSimpleTableViewerTable(boolean autoResize) {
    	this(null, true, false, autoResize);
    }
    
    public PaDELSimpleTableViewerTable(PaDELSimpleTable table, boolean sortable, boolean columnMovable, boolean autoResize) {
        super(sortable, columnMovable, autoResize);
        if (table != null) {
            setTable(table);
        }
    }
	
    public void setTable(PaDELSimpleTable table) {
    	this.model = new PaDELSimpleTableViewerTableModel(table);
        setModel(model);
    }   

    /** This method ensures that the correct tool tip for the current column is delivered. */
    @Override
    protected JTableHeader createDefaultTableHeader() {
    	return new JTableHeader(columnModel) {    		
            private static final long serialVersionUID = 1L;

            @Override
            public String getToolTipText(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realColumnIndex = convertColumnIndexToModel(index);
                if ((realColumnIndex >= 0) && (realColumnIndex < getModel().getColumnCount()))
                    return "The column " + getModel().getColumnName(realColumnIndex);
                else
                    return "";
            }
    	};
    }
    
    public Color getCellColor(int row, int col) {
        if (row % 2 == 0) {
            return Color.WHITE;
        } else {
            return SwingTools.LIGHTEST_BLUE;
        }
    }
}
