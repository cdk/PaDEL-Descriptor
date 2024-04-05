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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import com.rapidminer.gui.tools.ExtendedJScrollPane;
import com.rapidminer.report.Tableable;
import com.rapidminer.tools.PaDELSimpleTable;

    
/**
 * Can be used to display (parts of) the data by means of a JTable.
 * 
 * @author Ingo Mierswa, Yap Chun Wei
 * @version $Id$
 */
public class PaDELSimpleTableViewer extends JPanel implements Tableable {
    
    private static final long serialVersionUID = 3236339829802763861L;
    
    private JLabel generalInfo = new JLabel();
    
    private PaDELSimpleTableViewerTable tableViewerTable;
    	
    public PaDELSimpleTableViewer(PaDELSimpleTable table) {
    	this(table, false);
    }
    
    public PaDELSimpleTableViewer(PaDELSimpleTable table, boolean autoResize) {
        super(new BorderLayout());
        
        // table view
        this.tableViewerTable = new PaDELSimpleTableViewerTable(autoResize);
        final JPanel tablePanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(generalInfo);
        tablePanel.add(infoPanel, BorderLayout.NORTH);
        
        JScrollPane tableScrollPane = new ExtendedJScrollPane(tableViewerTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        add(tablePanel);
        
        setPaDELTable(table);
    }

    public PaDELSimpleTableViewerTable getTable() {
        return tableViewerTable;
    }

    @Override
    public void prepareReporting() {
        tableViewerTable.prepareReporting();
    }

    @Override
    public void finishReporting() {
        tableViewerTable.finishReporting();
    }

    @Override
    public boolean isFirstLineHeader() { return true; }

    @Override
    public boolean isFirstColumnHeader() { return true; }
    
    public void setPaDELTable(PaDELSimpleTable table) {
        generalInfo.setText(table.getName() + " (" + table.getNumberOfRows() + " rows, " + table.getNumberOfColumns() + " columns)");
        tableViewerTable.setTable(table);
    }
    
    @Override
    public String getColumnName(int columnIndex) {
    	return tableViewerTable.getColumnName(columnIndex);
    }

    @Override
    public String getCell(int row, int column) {
        return tableViewerTable.getCell(row, column);
    }

    @Override
    public int getColumnNumber() {
        return tableViewerTable.getColumnNumber();
    }

    @Override
    public int getRowNumber() {
        return tableViewerTable.getRowNumber();
    }
}
