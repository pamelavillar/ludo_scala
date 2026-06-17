error id: file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/components/BoardView.scala:image.
file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/components/BoardView.scala
empty definition using pc, found symbol in pc: image.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -ludo/shared/scalafx/scence/image.
	 -scalafx/Includes.scalafx.scence.image.
	 -scalafx/scene/layout/scalafx/scence/image.
	 -scalafx/scence/image/scalafx/scence/image.
	 -scalafx/scence/image.
	 -scala/Predef.scalafx.scence.image.
offset: 260
uri: file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/components/BoardView.scala
text:
```scala
package ludo.gui.components

import ludo.shared.*
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Node
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.scene.shape.Polygon
import scalafx.scence.@@image._

class BoardView(board: BoardInterface, players: Vector[PlayerInterface],onPieceClick: Int => Unit) extends GridPane:

  // ── Configuración visual ─────────────────────────────────────────

  hgap = 2
  vgap = 2
  padding = Insets(8)
  alignment = Pos.Center

  style =
    """
      -fx-background-color: #EEEEEE;
      -fx-background-radius: 12;
    """

  // ── Bases ────────────────────────────────────────────────────────



  private val baseRed = new BaseZone(PlayerColor.Red, board.basePieces(PlayerColor.Red), onPieceClick)

  private val baseBlue =  new BaseZone(PlayerColor.Blue, board.basePieces(PlayerColor.Blue), onPieceClick)

  private val baseGreen = new BaseZone(PlayerColor.Green, board.basePieces(PlayerColor.Green), onPieceClick)

  private val baseYellow = new BaseZone(PlayerColor.Yellow, board.basePieces(PlayerColor.Yellow), onPieceClick)
  // ── Centro ──────────────────────────────────────────────────────

  private val centerPane = buildCenter()

  // ── Referencias visuales de casillas ────────────────────────────

  private var trackCells: Map[Int, Node] = Map.empty

  // ── Track visual del tablero ────────────────────────────────────

  val trackPositions: Vector[(Int, Int)] = (

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

  val homeTrackPositions: Map[PlayerColor, Vector[(Int, Int)]] = Map(
    PlayerColor.Red -> ((1 to 5).map(c => (c, 7))).toVector,
    PlayerColor.Blue -> (1 to 5).map(r => (7, r)).toVector,
    PlayerColor.Green ->  (9 to 13).map(r => (7, r)).reverse.toVector,
    PlayerColor.Yellow -> (9 to 13).map(c =>(c,7)).reverse.toVector
  )

  // ── Construcción inicial ────────────────────────────────────────

  buildLayout(board)

  // ── Layout principal ────────────────────────────────────────────

  private def buildLayout(currentBoard: BoardInterface): Unit =

    children.clear()
    trackCells = Map.empty

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
    placeAt(centerPane, 6, 6, 3, 3)

    // Casillas del track
    // 1) Track externo
    currentBoard.trackCells.sortBy(_.position).zipWithIndex.foreach { case (cell, idx) =>
      val (col, row) = trackPositions(idx)
      val cellNode = CellView(cell, onPieceClick)
      placeAt(cellNode.delegate, col, row)
    }


    currentBoard.homeCells.foreach { case (color, cells) =>
      val positions = homeTrackPositions(color)

      cells.sortBy(_.position).zipWithIndex.foreach { case (cell, idx) =>
        val (col, row) = positions(idx)
        val cellNode = CellView(cell, onPieceClick)
        placeAt(cellNode.delegate, col, row)
      }
    }

  // ── Actualización visual ────────────────────────────────────────

  def updateBoard(newBoard: BoardInterface): Unit =
    baseRed.update(newBoard.basePieces(PlayerColor.Red))
    baseBlue.update(newBoard.basePieces(PlayerColor.Blue))
    baseGreen.update(newBoard.basePieces(PlayerColor.Green))
    baseYellow.update(newBoard.basePieces(PlayerColor.Yellow))

    buildLayout(newBoard)

  // ── Centro del tablero ──────────────────────────────────────────

  private def buildCenter(): Node =

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

  private def piecesOf(
    color: PlayerColor,
    b: BoardInterface
  ): Vector[PieceInterface] =
    b.cells
      .flatMap(_.piece)
      .filter(_.color == color)

  private def placeAt(
    node: Node,
    col: Int,
    row: Int,
    colSpan: Int = 1,
    rowSpan: Int = 1
  ): Unit =

    GridPane.setColumnIndex(node, col)
    GridPane.setRowIndex(node, row)

    if colSpan > 1 then
      GridPane.setColumnSpan(node, colSpan)

    if rowSpan > 1 then
      GridPane.setRowSpan(node, rowSpan)

    children.add(node)

  private def toHex(c: Color): String =
    f"#${(c.red * 255).toInt}%02X${(c.green * 255).toInt}%02X${(c.blue * 255).toInt}%02X"
    
```


#### Short summary: 

empty definition using pc, found symbol in pc: image.