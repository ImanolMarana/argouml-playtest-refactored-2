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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.argouml.profile.internal.ocl.LambdaEvaluator;
import org.argouml.profile.internal.ocl.ModelInterpreter;

/**
 * Interprets invocations to OCL collections API
 * 
 * @author maurelio1234
 */
public class CollectionsModelInterpreter implements ModelInterpreter {

    /*
     * @see org.argouml.profile.internal.ocl.ModelInterpreter#invokeFeature(java.util.HashMap,
     *      java.lang.Object, java.lang.String, java.lang.String,
     *      java.lang.Object[])
     */
    @SuppressWarnings("unchecked")
    public Object invokeFeature(Map<String, Object> vt, Object subject,
                                 String feature, String type, Object[] parameters) {

        if (subject == null) {
            return null;
        }

        if (!(subject instanceof Collection)) {
            if (type.equals("->")) {
                Set ns = new HashSet();
                ns.add(subject);
                subject = ns;
            }
        }

        if (subject instanceof Collection) {
            if (type.equals("->")) {
                return handleCollectionFeatures(vt, (Collection) subject, feature, parameters);
            }
        }

        // these operations are ok for lists too
        if (subject instanceof Collection) {
            if (type.equals("->")) {
                return handleCommonCollectionFeatures((Collection) subject, feature, parameters);
            }
        }

        if (subject instanceof List) {
            if (type.equals("->")) {
                return handleListFeatures((List) subject, feature, parameters);
            }
        }
        // these operations are ok for bags too
        if (subject instanceof Set) {
            if (type.equals("->")) {
                return handleSetFeatures((Set) subject, feature, parameters);

            }
        }

        if (subject instanceof Bag) {
            if (type.equals("->")) {
                return handleBagFeatures((Bag) subject, feature, parameters);
            }
        }

        return null;
    }

    private Object handleBagFeatures(Bag subject, String feature, Object[] parameters) {
        if (feature.equals("count")) {
            return subject.count(parameters[0]);
        }
        return null;
    }

    private Object handleSetFeatures(Set subject, String feature, Object[] parameters) {
        switch (feature) {
            case "intersection":
                return calculateIntersection(subject, (Set) parameters[0]);
            case "including": {
                Set copy = (Set) cloneCollection(subject);
                copy.add(parameters[0]);
                return copy;
            }
            case "excluding": {
                Set copy = (Set) cloneCollection(subject);
                copy.remove(parameters[0]);
                return copy;
            }
            case "symmetricDifference":
                return calculateSymmetricDifference(subject, (Set) parameters[0]);
        }
        return null;
    }


    private Object handleListFeatures(List subject, String feature, Object[] parameters) {
        if (feature.equals("at")) {
            return subject.get((Integer) parameters[0]);
        } else if (feature.equals("first")) {
            return subject.get(0);
        } else if (feature.equals("last")) {
            return subject.get(subject.size());
        }
        return null;
    }

    private Object handleCommonCollectionFeatures(Collection subject, String feature, Object[] parameters) {
        switch (feature) {
            case "size":
                return subject.size();
            case "includes":
                return subject.contains(parameters[0]);
            case "excludes":
                return !subject.contains(parameters[0]);
            case "count":
                return (new HashBag<Object>(subject))
                        .count(parameters[0]);
            case "includesAll":
                return checkIncludesAll(subject, (Collection) parameters[0]);
            case "excludesAll":
                return checkExcludesAll(subject, (Collection) parameters[0]);
            case "isEmpty":
                return subject.isEmpty();
            case "notEmpty":
                return !subject.isEmpty();
            case "asSequence":
                return new ArrayList<Object>(subject);
            case "asBag":
                return new HashBag<Object>(subject);
            case "asSet":
                return new HashSet<Object>(subject);
            case "sum":
                return calculateSum(subject);
            case "union": {
                Collection copy = cloneCollection(subject);
                copy.addAll((Collection) parameters[0]);
                return copy;
            }
            case "append": {
                Collection copy = cloneCollection(subject);
                copy.add(parameters[0]);
                return copy;
            }
            case "prepend": {
                Collection copy = cloneCollection(subject);
                if (copy instanceof List) {
                    ((List) copy).add(0, parameters[0]);
                } else {
                    copy.add(parameters[0]);
                }
                return copy;
            }
        }
        return null;
    }

    private Object handleCollectionFeatures(Map<String, Object> vt, Collection subject, String feature, Object[] parameters) {
        switch (feature.toString().trim()) {
            case "select":
                return handleSelect(vt, subject, parameters);
            case "reject":
                return handleReject(vt, subject, parameters);
            case "forAll": {
                List<String> vars = (ArrayList<String>) parameters[0];
                Object exp = parameters[1];
                LambdaEvaluator eval = (LambdaEvaluator) parameters[2];
                return doForAll(vt, subject, vars, exp, eval);
            }
            case "collect":
                return handleCollect(vt, subject, parameters);
            case "exists":
                return handleExists(vt, subject, parameters);
            case "isUnique":
                return handleIsUnique(vt, subject, parameters);
            case "one":
                return handleOne(vt, subject, parameters);
            case "any":
                return handleAny(vt, subject, parameters);
        }
        return null;
    }


    private Object handleAny(Map<String, Object> vt, Collection subject, Object[] parameters) {
        List<String> vars = (ArrayList<String>) parameters[0];
        Object exp = parameters[1];
        LambdaEvaluator eval = (LambdaEvaluator) parameters[2];

        // TODO is it possible to use more than one variable?
        String varName = vars.get(0);
        Object oldVal = vt.get(varName);

        for (Object object : subject) {
            vt.put(varName, object);

            Object val = eval.evaluate(vt, exp);
            if (val instanceof Boolean && (Boolean) val) {
                return object;
            }
        }

        vt.put(varName, oldVal);

        return null;
    }

    private Object handleOne(Map<String, Object> vt, Collection subject, Object[] parameters) {
        // TODO: This code is cloned over and over again! - tfm
        List<String> vars = (ArrayList<String>) parameters[0];
        Object exp = parameters[1];
        LambdaEvaluator eval = (LambdaEvaluator) parameters[2];

        // TODO is it possible to use more than one variable?
        String varName = vars.get(0);
        Object oldVal = vt.get(varName);
        boolean found = false;

        for (Object object : subject) {
            vt.put(varName, object);

            Object val = eval.evaluate(vt, exp);
            if (val instanceof Boolean && (Boolean) val) {
                if (!found) {
                    found = true;
                } else {
                    return false;
                }
            }
        }

        vt.put(varName, oldVal);

        return found;
    }

    private Object handleIsUnique(Map<String, Object> vt, Collection subject, Object[] parameters) {
        List<String> vars = (ArrayList<String>) parameters[0];
        Object exp = parameters[1];
        LambdaEvaluator eval = (LambdaEvaluator) parameters[2];
        Bag<Object> res = new HashBag<Object>();

        // TODO is it possible to use more than one variable?
        String varName = vars.get(0);
        Object oldVal = vt.get(varName);

        for (Object object : subject) {
            vt.put(varName, object);

            Object val = eval.evaluate(vt, exp);
            res.add(val);
            if (res.count(val) > 1) {
                return false;
            }
        }

        vt.put(varName, oldVal);

        return true;
    }

    private Object handleExists(Map<String, Object> vt, Collection subject, Object[] parameters) {
        List<String> vars = (ArrayList<String>) parameters[0];
        Object exp = parameters[1];
        LambdaEvaluator eval = (LambdaEvaluator) parameters[2];

        // TODO is it possible to use more than one variable?
        String varName = vars.get(0);
        Object oldVal = vt.get(varName);

        for (Object object : subject) {
            vt.put(varName, object);

            Object val = eval.evaluate(vt, exp);
            if (val instanceof Boolean && (Boolean) val) {
                return true;
            }
        }

        vt.put(varName, oldVal);

        return false;
    }

    private Object handleCollect(Map<String, Object> vt, Collection subject, Object[] parameters) {
        List<String> vars = (ArrayList<String>) parameters[0];
        Object exp = parameters[1];
        LambdaEvaluator eval = (LambdaEvaluator) parameters[2];
        Bag res = new HashBag();

        // TODO is it possible to use more than one variable?
        String varName = vars.get(0);
        Object oldVal = vt.get(varName);

        for (Object object : subject) {
            vt.put(varName, object);
            Object val = eval.evaluate(vt, exp);
            res.add(val);
        }

        vt.put(varName, oldVal);

        return res;
    }

    private Object handleReject(Map<String, Object> vt, Collection subject, Object[] parameters) {
        List<String> vars = (ArrayList<String>) parameters[0];
        Object exp = parameters[1];
        LambdaEvaluator eval = (LambdaEvaluator) parameters[2];

        Collection col = cloneCollection(subject);
        List remove = new ArrayList();

        // TODO is it possible to use more than one variable?
        String varName = vars.get(0);
        Object oldVal = vt.get(varName);

        for (Object object : col) {
            vt.put(varName, object);
            Object res = eval.evaluate(vt, exp);
            if (res instanceof Boolean && (Boolean) res) {
                // if test is ok this element should not
                // be in the result set
                remove.add(object);
            }
        }

        col.removeAll(remove);
        vt.put(varName, oldVal);

        return col;
    }

    private Object handleSelect(Map<String, Object> vt, Collection subject, Object[] parameters) {
        List<String> vars = (List<String>) parameters[0];
        Object exp = parameters[1];
        LambdaEvaluator eval = (LambdaEvaluator) parameters[2];

        Collection col = cloneCollection(subject);
        List remove = new ArrayList();

        // TODO is it possible to use more than one variable?
        String varName = vars.get(0);
        Object oldVal = vt.get(varName);

        for (Object object : col) {
            vt.put(varName, object);
            Object res = eval.evaluate(vt, exp);
            if (res instanceof Boolean && (Boolean) res) {
                // do nothing
            } else {
                // if test fails this element should not
                // be in the result set
                remove.add(object);
            }
        }

        col.removeAll(remove);
        vt.put(varName, oldVal);

        return col;
    }


    private Set calculateSymmetricDifference(Set c1, Set c2) {
        Set r = new HashSet<Object>();

        for (Object o : c1) {
            if (!c2.contains(o)) {
                r.add(o);
            }
        }

        for (Object o : c2) {
            if (!c1.contains(o)) {
                r.add(o);
            }
        }

        return r;
    }

    private Object calculateIntersection(Set c1, Set c2) {
        Set r = new HashSet<Object>();

        for (Object o : c1) {
            if (c2.contains(o)) {
                r.add(o);
            }
        }

        for (Object o : c2) {
            if (c1.contains(o)) {
                r.add(o);
            }
        }

        return r;
    }

    private Integer calculateSum(Collection subject) {
        Integer sum = 0;

        for (Object object : subject) {
            sum += (Integer) object;
        }
        return sum;
    }

    private boolean checkExcludesAll(Collection subject, Collection collection) {
        for (Object object : collection) {
            if (subject.contains(object)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkIncludesAll(Collection subject, Collection col) {
        for (Object object : col) {
            if (!subject.contains(object)) {
                return false;
            }
        }
        return true;
    }


    private boolean doForAll(Map<String, Object> vt, Collection collection,
                             List<String> vars, Object exp, LambdaEvaluator eval) {
        if (vars.isEmpty()) {
            return (Boolean) eval.evaluate(vt, exp);
        } else {
            String var = vars.get(0);
            vars.remove(var);
            Object oldval = vt.get(var);

            for (Object element : collection) {
                vt.put(var, element);

                boolean ret = doForAll(vt, collection, vars, exp, eval);

                if (!ret) {
                    return false;
                }
            }

            vt.put(var, oldval);
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private Collection cloneCollection(Collection col) {
        if (col instanceof List) {
            return new ArrayList(col);
        } else if (col instanceof Bag) {
            return new HashBag(col);
        } else if (col instanceof Set) {
            return new HashSet(col);
        } else {
            throw new IllegalArgumentException();
        }
    }

//Refactoring end
     * @see org.argouml.profile.internal.ocl.ModelInterpreter#getBuiltInSymbol(java.lang.String)
     */
    public Object getBuiltInSymbol(String sym) {
        return null;
    }

}
