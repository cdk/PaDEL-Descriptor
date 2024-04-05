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
package com.rapidminer.tools;

import javax.swing.Icon;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.operator.ResultObjectAdapter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple dense matrix implementation.
 * 
 * @author Ingo Mierswa, Yap Chun Wei
 * @version $Id$
 * 
 */

public class PaDELSimpleTable extends ResultObjectAdapter {

    private static final long serialVersionUID = -7401313632199345833L;

    private static final String RESULT_ICON_NAME = "table.png";
	
    private static Icon resultIcon = null;

    static {
        resultIcon = SwingTools.createIcon("16/" + RESULT_ICON_NAME);
    }
    
    private ArrayList<ArrayList<String> > table = new ArrayList<ArrayList<String> >();
    private ArrayList<String> rowLabels = new ArrayList<String>();
    private ArrayList<String> colLabels = new ArrayList<String>();
    private String name;
//    private NumberFormat formatter;

    public PaDELSimpleTable(String name, String[] colLabels)
    {
        this(name, new String[] {}, colLabels);
    }
    
    public PaDELSimpleTable(String name, String[] rowLabels, String[] colLabels)
    {
        this.name = name;
	this.rowLabels.addAll(Arrays.asList(rowLabels));
        this.colLabels.addAll(Arrays.asList(colLabels));
        for (int i=0, endi=this.rowLabels.size(); i<endi; ++i)
        {
            this.table.add((ArrayList)this.colLabels.clone());
        }
    }

    public void addRow(String label, String[] values)
    {
        rowLabels.add(label);
        ArrayList<String> temp = new ArrayList<String>();
        temp.addAll(Arrays.asList(values));
        table.add(temp);
    }

    public void addColumn(String label, String[] values)
    {
        colLabels.add(label);
        for (int i=0, endi=table.size(); i<endi; ++i)
        {
            table.get(i).add(values[i]);
        }
    }
    
    public void setValue(int i, int j, String value)
    {
        table.get(i).set(j, value);
    }
    
    public void setValue(String rowLabel, String colLabel, String value)
    {
        int i = getRowIndex(rowLabel);
        if (i==-1) {
            return;
        }
        int j = getColumnIndex(colLabel);
        if (j==-1) {
            return;
        }
        setValue(i, j, value);
    }

    public String getValue(int i, int j)
    {
        return table.get(i).get(j);
    }
    
    public String getValue(String rowLabel, String colLabel)
    {
        return getValue(getRowIndex(rowLabel), getColumnIndex(colLabel));
    }
    
    public int getNumberOfRows()
    {
        return rowLabels.size();
    }
    
    public int getNumberOfColumns()
    {
        return colLabels.size();
    }
	
    @Override
    public String getName()
    {
        return name;
    }
    
    public String getRowName(int i)
    {
        return rowLabels.get(i);
    }
    
    public int getRowIndex(String name)
    {
        int maxRows = getNumberOfRows();
        for (int i=0; i<maxRows; ++i)
        {
            if (rowLabels.get(i).equals(name))
            {
                return i;
            }
        }
        return -1;
    }
    
    public int getColumnIndex(String name)
    {
        int maxCols = getNumberOfColumns();
        for (int i=0; i<maxCols; ++i)
        {
            if (colLabels.get(i).equals(name))
            {
                return i;
            }
        }
        return -1;
    }
    
    public String getColumnName(int j)
    {
        return colLabels.get(j);
    }
    
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer(name + ":" + Tools.getLineSeparator());
        int maxCols = getNumberOfColumns();
        for (int i=0; i<maxCols; ++i)
        {
                result.append("\t" + colLabels.get(i));
        }

        int maxRows = getNumberOfRows();
        for (int i=0; i<maxRows; ++i)
        {
            result.append(Tools.getLineSeparator() + rowLabels.get(i));
            for (int j=0; j<maxCols; ++j) {
                result.append("\t" + table.get(i).get(j));
            }
        }
        return result.toString();
    }
  
    public String getExtension() { return "den"; }
    
    public String getFileDescription() { return name.toLowerCase(); }
    
    @Override
    public Icon getResultIcon()
    {
        return resultIcon;
    }
}
