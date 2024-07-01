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

package org.argouml.cognitive.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.plaf.metal.MetalIconFactory;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.argouml.application.helpers.ResourceLoaderWrapper;
import org.argouml.cognitive.Decision;
import org.argouml.cognitive.Designer;
import org.argouml.cognitive.Goal;
import org.argouml.cognitive.Poster;
import org.argouml.cognitive.ToDoItem;
import org.argouml.model.Model;
import org.argouml.uml.ui.UMLTreeCellRenderer;
import org.tigris.gef.base.Diagram;
import org.tigris.gef.base.Globals;
import org.tigris.gef.presentation.Fig;


/**
 * Displays an entry in the ToDo tree.
 *
 */
public class ToDoTreeRenderer extends DefaultTreeCellRenderer {
    ////////////////////////////////////////////////////////////////
    // class variables

    // general icons for poster
    private final ImageIcon postIt0     = lookupIconResource("PostIt0");
    private final ImageIcon postIt25    = lookupIconResource("PostIt25");
    private final ImageIcon postIt50    = lookupIconResource("PostIt50");
    private final ImageIcon postIt75    = lookupIconResource("PostIt75");
    private final ImageIcon postIt99    = lookupIconResource("PostIt99");
    private final ImageIcon postIt100   = lookupIconResource("PostIt100");

    // specialised icons for designer
    private final ImageIcon postItD0    = lookupIconResource("PostItD0");
    private final ImageIcon postItD25   = lookupIconResource("PostItD25");
    private final ImageIcon postItD50   = lookupIconResource("PostItD50");
    private final ImageIcon postItD75   = lookupIconResource("PostItD75");
    private final ImageIcon postItD99   = lookupIconResource("PostItD99");
    private final ImageIcon postItD100  = lookupIconResource("PostItD100");

    private UMLTreeCellRenderer treeCellRenderer = new UMLTreeCellRenderer();

    private static ImageIcon lookupIconResource(String name) {
        return ResourceLoaderWrapper.lookupIconResource(name);
    }

    ////////////////////////////////////////////////////////////////
    // TreeCellRenderer implementation

    /*
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(
     * javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int,
     * boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel,
        boolean expanded,
        boolean leaf, int row,
        boolean hasTheFocus) {

        Component r = super.getTreeCellRendererComponent(tree, value, sel,
            expanded, leaf,
            row, hasTheFocus);

        if (r instanceof JLabel) {
            JLabel lab = (JLabel) r;
            setIconForObject(lab, value);

            String tip = lab.getText() + " ";
            lab.setToolTipText(tip);
            tree.setToolTipText(tip);

            if (!sel) {
                lab.setBackground(getBackgroundNonSelectionColor());
            } else {
                Color high = Globals.getPrefs().getHighlightColor();
                high = high.brighter().brighter();
                lab.setBackground(high);
            }
            lab.setOpaque(sel);
        }
        return r;
    }

    private void setIconForObject(JLabel lab, Object value) {
        if (value instanceof ToDoItem) {
            setIconForToDoItem(lab, (ToDoItem) value);
        } else if (value instanceof Decision
                || value instanceof Goal
                || value instanceof Poster
                || value instanceof PriorityNode
                || value instanceof KnowledgeTypeNode) {
            lab.setIcon(MetalIconFactory.getTreeFolderIcon());
        } else if (value instanceof Diagram) {
            lab.setIcon(treeCellRenderer.getTreeCellRendererComponent(
                    null, value, false, false, true, 0, false).getIcon());
        } else {
            setIconForOther(lab, value);
        }
    }

    private void setIconForToDoItem(JLabel lab, ToDoItem item) {
        Poster post = item.getPoster();
        ImageIcon[] icons = post instanceof Designer
                ? new ImageIcon[] {postItD0, postItD25, postItD50, postItD75, postItD99, postItD100}
                : new ImageIcon[] {postIt0, postIt25, postIt50, postIt75, postIt99, postIt100};

        int progress = item.getProgress();
        if (progress >= 100) {
            lab.setIcon(icons[5]);
        } else if (progress >= 75) {
            lab.setIcon(icons[4]);
        } else if (progress >= 50) {
            lab.setIcon(icons[3]);
        } else if (progress >= 25) {
            lab.setIcon(icons[2]);
        } else if (progress > 0) {
            lab.setIcon(icons[1]);
        } else {
            lab.setIcon(icons[0]);
        }
    }

    private void setIconForOther(JLabel lab, Object value) {
        Object newValue = value;
        if (newValue instanceof Fig) {
            newValue = ((Fig) value).getOwner();
        }
        if (Model.getFacade().isAUMLElement(newValue)) {
            lab.setIcon(treeCellRenderer.getTreeCellRendererComponent(
                    null, newValue, false, false, true, 0, false).getIcon());
        }
    }
//Refactoring end


} /* end class ToDoTreeRenderer */
