/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    maurelio1234
 *****************************************************************************
 *
 * Some portions of this file was previously release using the BSD License:
 */

// Copyright (c) 1996-2006 The Regents of the University of California. All
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

package org.argouml.uml.cognitive.critics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Icon;

import org.argouml.cognitive.Critic;
import org.argouml.cognitive.Designer;
import org.argouml.model.Model;
import org.argouml.uml.cognitive.UMLDecision;

/**
 * The critic for ambiguous names of a state.
 *
 * @author Jason Robbins
 */
public class CrDisambigStateName extends CrUML {

    /**
     * The constructor.
     */
    public CrDisambigStateName() {
        setupHeadAndDesc();
	addSupportedDecision(UMLDecision.NAMING);
	setKnowledgeTypes(Critic.KT_SYNTAX);
	addTrigger("name");
	addTrigger("parent");
    }

    /*
     * @see org.argouml.uml.cognitive.critics.CrUML#predicate2(
     *      java.lang.Object, org.argouml.cognitive.Designer)
     */
    @Override
    public boolean predicate2(Object dm, Designer dsgr) {
    if (!(Model.getFacade().isAState(dm))) {
      return NO_PROBLEM;
    }
    String myName = Model.getFacade().getName(dm);
    // TODO: should define a CompoundCritic
    if (myName == null || myName.equals("")) {
      return NO_PROBLEM;
    }
    String myNameString = myName;
    if (myNameString.length() == 0) {
      return NO_PROBLEM;
    }
    Collection pkgs = Model.getFacade().getElementImports2(dm);
    if (pkgs == null) {
      return NO_PROBLEM;
    }
    return isNameAmbiguous(pkgs, myNameString, dm);
  }

  private boolean isNameAmbiguous(Collection pkgs, String myNameString, Object dm) {
    for (Iterator iter = pkgs.iterator(); iter.hasNext();) {
      Object imp = iter.next();
      Object ns = Model.getFacade().getPackage(imp);
      if (ns == null) {
        return NO_PROBLEM;
      }
      Collection oes = Model.getFacade().getOwnedElements(ns);
      if (oes == null) {
        return NO_PROBLEM;
      }
      if (isNameInCollection(oes, myNameString, dm)) {
        return PROBLEM_FOUND;
      }
    }
    return NO_PROBLEM;
  }

  private boolean isNameInCollection(Collection oes, String myNameString, Object dm) {
    Iterator elems = oes.iterator();
    while (elems.hasNext()) {
      Object eo = elems.next();
      Object me = Model.getFacade().getModelElement(eo);
      if (!(Model.getFacade().isAClassifier(me))) {
        continue;
      }
      if (me == dm) {
        continue;
      }
      String meName = Model.getFacade().getName(me);
      if (meName == null || meName.equals("")) {
        continue;
      }
      if (meName.equals(myNameString)) {
        return true;
      }
    }
    return false;
  }
//Refactoring end
    }

    /*
     * @see org.argouml.cognitive.Poster#getClarifier()
     */
    @Override
    public Icon getClarifier() {
	return ClClassName.getTheInstance();
    }

    /*
     * @see org.argouml.uml.cognitive.critics.CrUML#getCriticizedDesignMaterials()
     */
    @Override
    public Set<Object> getCriticizedDesignMaterials() {
        Set<Object> ret = new HashSet<Object>();
        ret.add(Model.getMetaTypes().getState());
        return ret;
    }
    
    /**
     * The UID.
     */
    private static final long serialVersionUID = 5027208502429769593L;
} /* end class CrDisambigStateName */
