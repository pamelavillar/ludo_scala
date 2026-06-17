package ludo.gui.components

import ludo.shared.*
import scalafx.Includes.*
import scalafx.scene.layout.*
import scalafx.scene.shape.Circle
import scalafx.scene.paint.Color
import scalafx.scene.text.{Text, Font, FontWeight}
import scalafx.scene.Node
import scalafx.geometry.Pos

object CellView:

  val SIZE = 38.0

  // ─────────────────────────────────────────────────────────────
  // Convierte JugadorColor → Color JavaFX
  // Ahora maneja null correctamente
  // ─────────────────────────────────────────────────────────────
  def colorOf(pc: JugadorColor | Null): Color =
    pc match
      case null               => Color.LightGray
      case JugadorColor.Red    => Color.web("#E53935")
      case JugadorColor.Blue   => Color.web("#1E88E5")
      case JugadorColor.Green  => Color.web("#43A047")
      case JugadorColor.Yellow => Color.web("#FDD835")

  def apply(cell: InterfazCasilla, onPieceClick: Int => Unit): StackPane =
    new StackPane:
      minWidth = SIZE
      prefWidth = SIZE
      maxWidth = SIZE

      minHeight = SIZE
      prefHeight = SIZE
      maxHeight = SIZE
      alignment  = Pos.Center

      // fondo de la casilla
      val bg = new StackPane:
        minWidth = SIZE
        prefWidth = SIZE
        maxWidth = SIZE

        minHeight = SIZE
        prefHeight = SIZE
        maxHeight = SIZE
        style = buildStyle(cell)

      children = Seq(bg) ++ pieceOverlay(cell, onPieceClick)

  // ─────────────────────────────────────────────────────────────

  private def buildStyle(cell: InterfazCasilla): String =
    val baseColor =
      cell.esInicioPieza match
        case Some(color) => toHex(colorOf(color).darker)
        case None        => 
          cell.estaEnCasaOf match
            case Some(color) => toHex(colorOf(color))
            case None        => "#D0D0D0"

    s"""
      -fx-background-color: $baseColor;
      -fx-border-color: white;
      -fx-border-width: 2;
      -fx-border-radius: 4;
      -fx-background-radius: 4;
    """

  private def pieceOverlay(
      cell: InterfazCasilla,
      onPieceClick: Int => Unit
  ): Seq[Node] =

    cell.piece match
      case None =>
        Seq.empty

      case Some(piece) =>

        val pieceColor = colorOf(piece.color)

        val circle = new Circle:
          radius = SIZE / 2.5
          fill   = pieceColor
          stroke = Color.White
          strokeWidth = 2

        val label = new Text(piece.id.toString):
          font = Font.font("monospace", FontWeight.Bold, 16)

          fill =
            if piece.color == JugadorColor.Yellow then
              Color.Black
            else
              Color.White

        val container = new StackPane:
          children = Seq(circle, label)

          onMouseClicked = _ =>
            onPieceClick(piece.id)

          style = "-fx-cursor: hand;"

        Seq(container)

  private def toHex(c: Color): String =
    f"#${(c.red * 255).toInt}%02X${(c.green * 255).toInt}%02X${(c.blue * 255).toInt}%02X"