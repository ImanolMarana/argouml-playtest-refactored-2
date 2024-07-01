/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    tfmorris
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2008 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies. This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason. IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.profile.internal.ocl.uml14;

import java.util.Map;

import org.argouml.model.Model;
import org.argouml.profile.internal.ocl.ModelInterpreter;

/**
 * OCL API
 * 
 * @author maurelio1234
 */
public class OclAPIModelInterpreter implements ModelInterpreter {

    /*
     * @see org.argouml.profile.internal.ocl.ModelInterpreter#invokeFeature(java.util.Map,
     *      java.lang.Object, java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    public Object invokeFeature(Map<String, Object> vt, Object subject,
            String feature, String type, Object[] parameters) {
        if (type.equals(".")) {
            return handleDotType(subject, feature, parameters);
        }
        return null;
    }

    private Object handleDotType(Object subject, String feature, Object[] parameters) {
        String trimmedFeature = feature.toString().trim();
        if (trimmedFeature.equals("oclIsKindOf") || trimmedFeature.equals("oclIsTypeOf")) {
            return handleIsKindOfOrType(subject, parameters);
        } else if (trimmedFeature.equals("oclAsType")) {
            return subject;
        } else if (subject instanceof OclType) {
            return handleOclType(subject, trimmedFeature);
        } else if (subject instanceof String) {
            return handleStringType(subject, trimmedFeature, parameters);
        } else {
            return null;
        }
    }

    private boolean handleIsKindOfOrType(Object subject, Object[] parameters) {
        String typeName = ((OclType) parameters[0]).getName();
        return typeName.equals("OclAny") || Model.getFacade().isA(typeName, subject);
    }

    private Object handleOclType(Object subject, String feature) {
        if (feature.equals("name")) {
            return ((OclType) subject).getName();
        }
        return null;
    }

    private Object handleStringType(Object subject, String feature, Object[] parameters) {
        switch (feature) {
            case "size":
                return ((String) subject).length();
            case "concat":
                return ((String) subject).concat((String) parameters[0]);
            case "toLower":
                return ((String) subject).toLowerCase();
            case "toUpper":
                return ((String) subject).toUpperCase();
            case "substring":
                return ((String) subject).substring((Integer) parameters[0], (Integer) parameters[1]);
            default:
                return null;
        }
    }

//Refactoring end

    /*
     * @see org.argouml.profile.internal.ocl.ModelInterpreter#getBuiltInSymbol(java.lang.String)
     */
    public Object getBuiltInSymbol(String sym) {        
        if (sym.equals("OclType")) {
            return new OclType("OclType");
        // TODO implement OCLExpression
        } else if (sym.equals("OclExpression")) {
            return new OclType("OclExpression");
        }
        if (sym.equals("OclAny")) {
            return new OclType("OclAny");
        }
        return null;
    }

}
