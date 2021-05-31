/* @author David Tsai
 * @created on Apr 22, 2003
 */
package backend;

import backend.stream.Parsable;
import engine.backend.PiecePhysics;
import gui.backend.PieceGraphics;

/**
 * Piece - represents a piece in the gizmoball game.
 */
public abstract interface Piece extends PiecePhysics, PieceGraphics, Parsable {

}
