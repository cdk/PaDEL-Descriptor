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

package com.rapidminer.operator.chemistry;

import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;

/*
    This is the abstract class for all compound preprocessing operators.
 */
public abstract class CompoundAbstractProcessing extends Operator
{
    protected final InputPort molInput = getInputPorts().createPort("mol");
    protected final OutputPort molOutput = getOutputPorts().createPort("mol");
    protected final OutputPort molOriginal = getOutputPorts().createPort("ori");

    public CompoundAbstractProcessing(OperatorDescription description)
    {
        super(description);

        getTransformer().addPassThroughRule(molInput, molOriginal);
    }
}