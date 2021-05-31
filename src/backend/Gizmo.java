/*
 * Created on Apr 21, 2003
 *
 * @author Ragu Vijaykumar
 */
package backend;

import backend.event.GizmoListener;
import engine.backend.GizmoPhysics;

/**
 *	Gizmo - represents a gizmo in the gizmoball game
 *
 *  @author Ragu Vijaykumar
 */
public abstract interface Gizmo extends GizmoPhysics, Piece, GizmoListener {

}
