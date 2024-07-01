/* $Id$
 *****************************************************************************
 * Copyright (c) 2009 Contributors - see below
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Bob Tarling
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

package org.argouml.core.propertypanels.ui;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;

import org.argouml.core.propertypanels.model.GetterSetterManager;

/**
 * Creates the XML Property panels
 * @author Bob Tarling
 */
class ListFactory implements ComponentFactory {
    
    public ListFactory() {
    }
    
    public JComponent createComponent(
            final Object modelElement,
            final String propName,
            final List<Class<?>> types) {
        DefaultListModel model = createListModel(modelElement, propName);

        if (model == null) {
            model = createSimpleListModel(types, modelElement, propName);
        }

        if (model != null) {
            return new RowSelector(model);
        }

        return null;
    }

    private DefaultListModel createListModel(Object modelElement, String propName) {
        switch (propName) {
            case "annotatedElement":
                return new UMLCommentAnnotatedElementListModel(modelElement);
            case "associationRole":
                return new UMLAssociationAssociationRoleListModel(modelElement);
            case "availableContents":
                return new UMLClassifierRoleAvailableContentsListModel(modelElement);
            case "availableFeature":
                return new UMLClassifierRoleAvailableFeaturesListModel(modelElement);
            case "classifierInState":
                return new UMLOFSStateListModel(modelElement);
            case "client":
                return new UMLDependencyClientListModel(modelElement);
            case "clientDependency":
                return new UMLModelElementClientDependencyListModel(modelElement);
            case "connection":
                return new UMLAssociationConnectionListModel(modelElement);
            case "constrainingElement":
                return new UMLCollaborationConstrainingElementListModel(
                        modelElement);
            case "contents":
                return new UMLPartitionContentListModel(modelElement);
            case "context":
                return new UMLSignalContextListModel(modelElement);
            case "deployedComponent":
                return new UMLNodeDeployedComponentListModel(modelElement);
            case "extend":
                return new UMLUseCaseExtendListModel(modelElement);
            case "extended_elements":
                return new UMLExtendedElementsListModel(modelElement);
            case "generalization":
                return new UMLGeneralizableElementGeneralizationListModel(
                        modelElement);
            case "include":
                return new UMLUseCaseIncludeListModel(modelElement);
            case "incoming":
                return new UMLStateVertexIncomingListModel(modelElement);
            case "instantiation":
                return new UMLCreateActionClassifierListModel(modelElement);
            case "link":
                return new UMLAssociationLinkListModel(modelElement);
            case "outgoing":
                return new UMLStateVertexOutgoingListModel(modelElement);
            case "partition":
                return new UMLActivityGraphPartitionListModel(modelElement);
            case "predecessor":
                return new UMLMessagePredecessorListModel(modelElement);
            case "resident":
                return new UMLContainerResidentListModel(modelElement);
            case "specialization":
                return new UMLGeneralizableElementSpecializationListModel(
                        modelElement);
            case "specification":
                return new UMLAssociationEndSpecificationListModel(modelElement);
            case "submachineState":
                return new UMLStateMachineSubmachineStateListModel(modelElement);
            case "supplier":
                return new UMLDependencySupplierListModel(modelElement);
            case "supplierDependency":
                return new UMLModelElementSupplierDependencyListModel(
                        modelElement);
            case "top":
                return new UMLStateMachineTopListModel(modelElement);
            case "transition":
                return new UMLEventTransitionListModel(modelElement);
            case "transitions":
                return new UMLStateMachineTransitionListModel(modelElement);
            case "typedValue":
                return new UMLTagDefinitionTypedValuesListModel(modelElement);
            default:
                return null;
        }
    }

    private DefaultListModel createSimpleListModel(List<Class<?>> types,
            Object modelElement, String propName) {
        if (types == null || types.isEmpty()) {
            return null;
        }
        final GetterSetterManager getterSetterManager =
            GetterSetterManager.getGetterSetter(types.get(0));
        if (getterSetterManager.contains(propName)) {
            return new SimpleListModel(propName, types, modelElement,
                    getterSetterManager);
        } else {
            return null;
        }
    }

//Refactoring end