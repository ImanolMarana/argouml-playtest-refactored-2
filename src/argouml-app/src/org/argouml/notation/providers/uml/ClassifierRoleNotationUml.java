/* $Id$
 *****************************************************************************
 * Copyright (c) 2009-2010 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michiel van der Wulp
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 2006-2009 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
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

package org.argouml.notation.providers.uml;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.argouml.application.events.ArgoEventPump;
import org.argouml.application.events.ArgoEventTypes;
import org.argouml.application.events.ArgoHelpEvent;
import org.argouml.i18n.Translator;
import org.argouml.model.Model;
import org.argouml.notation.NotationSettings;
import org.argouml.notation.providers.ClassifierRoleNotation;
import org.argouml.util.MyTokenizer;

/**
 * The UML notation for a ClassifierRole. <p>
 * 
 * The following is supported: <p>
 * 
 * <pre>
 * baselist := [base] [, base]*
 * classifierRole := [name] [/ role] [: baselist]
 * </pre>
 *
 * The <code>role </code> and <code>baselist</code> can be given in
 * any order.<p>
 * 
 * The <code>name</code> is the Instance name, not used for a ClassifierRole.<p>
 *
 * This syntax is compatible with the UML 1.3 and 1.4 specification.
 * 
 * @author Michiel van der Wulp
 */
public class ClassifierRoleNotationUml extends ClassifierRoleNotation {


    /**
     * The Constructor.
     * 
     * @param classifierRole the UML ClassifierRole
     */
    public ClassifierRoleNotationUml(Object classifierRole) {
        super(classifierRole);
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#getParsingHelp()
     */
    public String getParsingHelp() {
        return "parsing.help.fig-classifierrole";
    }

    /*
     * @see org.argouml.notation.providers.NotationProvider#parse(java.lang.Object, java.lang.String)
     */
    public void parse(Object modelElement, String text) {
        try {
            parseClassifierRole(modelElement, text);
        } catch (ParseException pe) {
            String msg = "statusmsg.bar.error.parsing.classifierrole";
            Object[] args = {pe.getLocalizedMessage(),
                             Integer.valueOf(pe.getErrorOffset()), };
            ArgoEventPump.fireEvent(new ArgoHelpEvent(
                    ArgoEventTypes.HELP_CHANGED, this,
                    Translator.messageFormat(msg, args)));
        }
    }
    
    /**
     * Parses a ClassifierRole represented by the following line of the format:
     *
     * <pre>
     * baselist := [base] [, base]*
     * classifierRole := [name] [/ role] [: baselist]
     * </pre>
     *
     * <code>role </code> and <code>baselist</code> can be given in
     * any order.<p>
     *
     * This syntax is compatible with the UML 1.3 specification.
     *
     * (formerly: "name: base" )
     *
     * @param cls the classifier role to apply any changes to
     * @param s the String to parse
     * @return the classifier role with the applied changes
     * @throws ParseException when it detects an error in the attribute string. 
     *                  See also ParseError.getErrorOffset().
     */
    protected Object parseClassifierRole(Object cls, String s)
            throws ParseException {

        MyTokenizer st = new MyTokenizer(s, " ,\t,/,:,\\,");
        ParsedData parsedData = new ParsedData();

        try {
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                parseToken(token, st, parsedData);
            }
        } catch (NoSuchElementException nsee) {
            String msg = "parsing.error.classifier.unexpected-end-attribute";
            throw new ParseException(Translator.localize(msg), s.length());
        }

        finalizeParsing(cls, parsedData);

        return cls;
    }

    private void parseToken(String token, MyTokenizer st, ParsedData parsedData)
            throws ParseException {
        if (" ".equals(token) || "\t".equals(token)) {
            return;
        } 
        
        if (isTokenType(token)) {
            handleTokenType(token, parsedData);
        } else if (parsedData.hasColon) {
            handleColon(token, st, parsedData);
        } else if (parsedData.hasSlash) {
            handleSlash(token, st, parsedData);
        } else {
            handleOther(token, st, parsedData);
        }
    }
    
    private boolean isTokenType(String token) {
        return "/".equals(token) || ":".equals(token) || ",".equals(token);
    }
    
    private void handleTokenType(String token, ParsedData parsedData) {
        parsedData.addBaseIfNeeded();
        
        if ("/".equals(token)) {
            parsedData.hasSlash = true;
            parsedData.hasColon = false;
        } else if (":".equals(token)) {
            parsedData.hasColon = true;
            parsedData.hasSlash = false;
        } 
    }
    
    private void handleColon(String token, MyTokenizer st, ParsedData parsedData) 
            throws ParseException {
        if (parsedData.base != null) {
            String msg = "parsing.error.classifier.extra-test";
            throw new ParseException(
                Translator.localize(msg),
                st.getTokenIndex());
        }

        parsedData.base = token;
    }
    
    private void handleSlash(String token, MyTokenizer st, ParsedData parsedData) 
            throws ParseException {
        if (parsedData.role != null) {
            String msg = "parsing.error.classifier.extra-test";
            throw new ParseException(
                Translator.localize(msg),
                st.getTokenIndex());
        }

        parsedData.role = token;
    }
    
    private void handleOther(String token, MyTokenizer st, ParsedData parsedData) 
            throws ParseException {
        if (parsedData.name != null) {
            String msg = "parsing.error.classifier.extra-test";
            throw new ParseException(
                Translator.localize(msg),
                st.getTokenIndex());
        }

        parsedData.name = token;
    }
    
    private void finalizeParsing(Object cls, ParsedData parsedData) {
        parsedData.addBaseIfNeeded();

        // TODO: What to do about object name???
        //    if (name != null)
        //      ;

        if (parsedData.role != null) {
            Model.getCoreHelper().setName(cls, parsedData.role.trim());
        }

        if (parsedData.bases != null) {
            updateBases(cls, parsedData.bases);
        }
    }

    private void updateBases(Object cls, List<String> bases) {
        // Remove bases that aren't there anymore
        Collection b = new ArrayList(Model.getFacade().getBases(cls));

        removeExistingBases(cls, bases, b);
        addNewBases(cls, bases, b);
    }
    
    private void removeExistingBases(Object cls, List<String> bases, Collection b) {
        Iterator it = b.iterator();
        while (it.hasNext()) {
            Object c = it.next();
            if (!bases.contains(Model.getFacade().getName(c))) {
                Model.getCollaborationsHelper().removeBase(cls, c);
            }
        }
    }
    
    private void addNewBases(Object cls, List<String> bases, Collection b) {
        Object ns = getNamespace(cls);
        Iterator it = bases.iterator();
        addBases:
        while (it.hasNext()) {
            String d = ((String) it.next()).trim();

            if (baseExists(b, d)) {
                continue addBases;
            }
            
            Object c = NotationUtilityUml.getType(d, ns);
            if (Model.getFacade().isACollaboration(
                    Model.getFacade().getNamespace(c))) {
                Model.getCoreHelper().setNamespace(c, ns);
            }
            Model.getCollaborationsHelper().addBase(cls, c);
        }
    }
    
    private boolean baseExists(Collection b, String baseName) {
        Iterator it2 = b.iterator();
        while (it2.hasNext()) {
            Object c = it2.next();
            if (baseName.equals(Model.getFacade().getName(c))) {
                return true;
            }
        }
        return false;
    }

    private Object getNamespace(Object cls) {
        Object ns = Model.getFacade().getNamespace(cls);
        if (ns != null && Model.getFacade().getNamespace(ns) != null) {
            ns = Model.getFacade().getNamespace(ns);
        } else {
            ns = Model.getFacade().getRoot(cls);
        }
        return ns;
    }

    private static class ParsedData {
        public String name;
        public String role;
        public String base;
        public List<String> bases;
        public boolean hasColon;
        public boolean hasSlash;

        public void addBaseIfNeeded() {
            if (base != null) {
                if (bases == null) {
                    bases = new ArrayList<String>();
                }
                bases.add(base);
                base = null;
            }
        }
    }

//Refactoring end