package ludo.gui.components

import ludo.shared.*
import scalafx.Includes.*
import scalafx.scene.Node
import scalafx.scene.layout.*
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scalafx.scene.text.{Text, Font, FontWeight}
import scalafx.geometry.{Insets, Pos}

// ─────────────────────────────────────────────────────────────────────────────
// BaseZone — zona de inicio (casa) de un equipo: cuadrícula 2×2 con sus fichas
//
// Equivalente a BaseTeam.scala del repo original.
// Se actualiza cuando recibe un PieceMoved con cambios en las fichas de base.
// ─────────────────────────────────────────────────────────────────────────────
class BaseZone(color: PlayerColor, pieces: Vector[PieceInterface], onPieceClick: Int => Unit) extends GridPane:

  alignment = Pos.Center
  hgap = 6
  vgap = 6
  padding = Insets(10)

  private val img = color match
    case PlayerColor.Red    => "/img/francia.jpg"
    case PlayerColor.Blue   => "/img/cabo_verde.png"
    case PlayerColor.Green  => "/img/brazil.png"
    case PlayerColor.Yellow => "/img/españa.png"


  style = s"""
    -fx-background-color: ${toHex(CellView.colorOf(color))};
    -fx-background-image: url('$img');
     -fx-background-image: url('$img');
    -fx-background-repeat: no-repeat;
    -fx-background-position: center center;
    -fx-background-size: 80% 80%;

    
    -fx-background-radius: 8;
    -fx-border-width: 3;
    -fx-border-radius: 8;
  """

  // dibuja las 4 fichas en la cuadrícula 2×2
  private def drawPieces(ps: Vector[PieceInterface]): Unit =
    children.clear()
    ps.zipWithIndex.foreach { (piece, idx) =>
      val row = idx / 2
      val col = idx % 2
      val node = makePieceNode(piece)
      GridPane.setRowIndex(node, row)
      GridPane.setColumnIndex(node, col)
      children.add(node)
    }

  // fichas que ya salieron → casilla vacía gris
  private def makeEmptySlot(): Node =
    new StackPane:
      prefWidth  = CellView.SIZE
      prefHeight = CellView.SIZE
      style = """
        -fx-background-color: rgba(255,255,255,0.4);
        -fx-background-radius: 30;
      """

  private def makePieceNode(piece: PieceInterface): Node =
    if piece.isAtBase then
      val pieceColor = CellView.colorOf(piece.color)
      new StackPane:
        prefWidth  = CellView.SIZE
        prefHeight = CellView.SIZE
        alignment  = Pos.Center
        children = Seq(
          new Circle:
            radius = CellView.SIZE / 2.5
            fill   = pieceColor
            stroke = Color.White
            strokeWidth = 2
          ,
          new Text(piece.id.toString):
            font  = Font.font("monospace", FontWeight.Bold, 16)
            fill  = if piece.color == PlayerColor.Yellow then Color.Black else Color.White
        )
        onMouseClicked = _ => onPieceClick(piece.id)
        style = "-fx-cursor: hand;"
    else
      makeEmptySlot()

  // Redibuja cuando cambia el estado del board
  def update(newPieces: Vector[PieceInterface]): Unit =
    drawPieces(newPieces)

  // dibujado inicial
  drawPieces(pieces)

  private def toHex(c: Color): String =
    f"#${(c.red * 255).toInt}%02X${(c.green * 255).toInt}%02X${(c.blue * 255).toInt}%02X"
