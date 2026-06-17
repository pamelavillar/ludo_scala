package ludo.gui.components

import ludo.shared.*
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape.Polygon
import scalafx.scene.image._

class BoardView(board: InterfazTablero, jugadores: Vector[InterfazJugador],onPieceClick: Int => Unit) extends GridPane:

  // ── Configuración visual ─────────────────────────────────────────

  hgap = 2
  vgap = 2
  padding = Insets(8)
  alignment = Pos.Center

  style = """ -fx-background-color: #EEEEEE; -fx-background-radius: 12;"""

  // ── Bases ────────────────────────────────────────────────────────


  private val baseRed = new BaseZone(JugadorColor.Red, board.basePieces(JugadorColor.Red), onPieceClick)
  private val baseBlue =  new BaseZone(JugadorColor.Blue, board.basePieces(JugadorColor.Blue), onPieceClick)
  private val baseGreen = new BaseZone(JugadorColor.Green, board.basePieces(JugadorColor.Green), onPieceClick)
  private val baseYellow = new BaseZone(JugadorColor.Yellow, board.basePieces(JugadorColor.Yellow), onPieceClick)
  // ── Centro ──────────────────────────────────────────────────────

  private val areaCasas = dibujarCasas()

  // ── Referencias visuales de casillas ────────────────────────────

  private var CasillaPista: Map[Int, Node] = Map.empty

  // ── Track visual del tablero ────────────────────────────────────

  val trackposicions: Vector[(Int, Int)] = (

    (0 to 5).map(c => (c, 6)) ++

    (0 to 5).map(r => (6, r)).reverse ++
    Vector((7, 0)) ++
    (0 to 5).map(r => (8, r)) ++

    (9 to 14).map(c => (c, 6)) ++
    Vector((14, 7)) ++
    (9 to 14).map(c => (c, 8)).reverse ++

    (9 to 14).map(r => (8, r)) ++
    Vector((7, 14)) ++
    (9 to 14).map(r => (6, r)).reverse ++

    (0 to 5).map(c => (c, 8)).reverse ++
    Vector((0, 7))



  ).toVector

  val casaTrackPosiciones: Map[JugadorColor, Vector[(Int, Int)]] = Map(
    JugadorColor.Red -> ((1 to 5).map(c => (c, 7))).toVector,
    JugadorColor.Blue -> (1 to 5).map(r => (7, r)).toVector,
    JugadorColor.Green ->  (9 to 13).map(r => (7, r)).reverse.toVector,
    JugadorColor.Yellow -> (9 to 13).map(c =>(c,7)).reverse.toVector
  )

  // ── Construcción inicial ────────────────────────────────────────

  dibujarTablero(board)

  // ── Layout principal ────────────────────────────────────────────

  private def dibujarTablero(currentBoard: InterfazTablero): Unit =

    children.clear()
    CasillaPista = Map.empty

    columnConstraints.clear()
    rowConstraints.clear()

    for _ <- 0 until 15 do
      columnConstraints.add( new ColumnConstraints:
          minWidth = CellView.SIZE
          prefWidth = CellView.SIZE
          maxWidth = CellView.SIZE
          hgrow = Priority.Never
      )

    for _ <- 0 until 15 do
      rowConstraints.add( new RowConstraints:
          minHeight = CellView.SIZE
          prefHeight = CellView.SIZE
          maxHeight = CellView.SIZE
          vgrow = Priority.Never
      )

    // Bases
    placeAt(baseRed, 0, 0, 6, 6)
    placeAt(baseBlue, 9, 0, 6, 6)

    placeAt(baseGreen, 0, 9, 6, 6)
    placeAt(baseYellow, 9, 9, 6, 6)

    // Centro
    placeAt(areaCasas, 6, 6, 3, 3)

    // Casillas del camino que siguen las piezas en general
    currentBoard.CasillaPista.sortBy(_.posicion).zipWithIndex.foreach { case (cell, idx) =>
      val (col, row) = trackposicions(idx)
      val cellNode = CellView(cell, onPieceClick)
      placeAt(cellNode.delegate, col, row)
    }


  //casillas del camino que cada pieza sigue para llegar a casa
    currentBoard.CasillaCasa.foreach { case (color, cells) =>
      val posicions = casaTrackPosiciones(color)

      cells.sortBy(_.posicion).zipWithIndex.foreach { case (cell, idx) =>
        val (col, row) = posicions(idx)
        val cellNode = CellView(cell, onPieceClick)
        placeAt(cellNode.delegate, col, row)
      }
    }

  // ── Actualización visual ────────────────────────────────────────

  def updateBoard(nuevoTablero: InterfazTablero): Unit =
    baseRed.update(nuevoTablero.basePieces(JugadorColor.Red))
    baseBlue.update(nuevoTablero.basePieces(JugadorColor.Blue))
    baseGreen.update(nuevoTablero.basePieces(JugadorColor.Green))
    baseYellow.update(nuevoTablero.basePieces(JugadorColor.Yellow))

    dibujarTablero(nuevoTablero)

  // CASAS, ESTA MEDIO HARCODEADO XD

  private def dibujarCasas(): Node =

    val size = CellView.SIZE * 3
    val half = size / 2

    val red = new Polygon:
      points ++= Seq(
        0.0, size,
        0.0, 0.0,
        half, half
      )
      fill = Color.web("#E53935")

    val blue = new Polygon:
      points ++= Seq(
        0.0, 0.0,
        size, 0.0,
        half, half
      )
      fill = Color.web("#1E88E5")

    val yellow = new Polygon:
      points ++= Seq(
        size, 0.0,
        size, size,
        half, half
      )
      fill = Color.web("#FDD835")

    val green = new Polygon:
      points ++= Seq(
        size, size,
        0.0, size,
        half, half
      )
      fill = Color.web("#43A047")

    new Pane:
      prefWidth = size
      prefHeight = size
      children = Seq(red, blue, yellow, green)

  // ── Helpers ─────────────────────────────────────────────────────

  //ESTE DE PIECESOF YA NO LO USO HABRIA QUE BORRARLO 
  private def piecesOf(
    color: JugadorColor,
    b: InterfazTablero
  ): Vector[InterfazPieza] =
    b.cells
      .flatMap(_.piece)
      .filter(_.color == color)

  private def placeAt( node: Node, col: Int,row: Int,colSpan: Int = 1,rowSpan: Int = 1): Unit =

    GridPane.setColumnIndex(node, col)
    GridPane.setRowIndex(node, row)

    if colSpan > 1 then GridPane.setColumnSpan(node, colSpan)
    if rowSpan > 1 then GridPane.setRowSpan(node, rowSpan)

    children.add(node)


  private def toHex(c: Color): String =
    f"#${(c.red * 255).toInt}%02X${(c.green * 255).toInt}%02X${(c.blue * 255).toInt}%02X"
    