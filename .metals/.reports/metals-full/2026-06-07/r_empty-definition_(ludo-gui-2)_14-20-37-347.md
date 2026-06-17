error id: file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/components/BoardView.scala:setColumnIndex
file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/components/BoardView.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol setColumnIndex
offset: 4404
uri: file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/components/BoardView.scala
text:
```scala
package ludo.gui.components

import ludo.shared.*
import scalafx.Includes.*
import scalafx.scene.Node
import scalafx.scene.layout.*
import scalafx.scene.paint.Color
import scalafx.geometry.{Insets, Pos}

// ─────────────────────────────────────────────────────────────────────────────
// BoardView — el tablero completo
//
// Layout de un tablero de Ludo estándar (15×15 celdas lógicas):
//
//   [Base Roja]   [Fila Top]     [Base Azul]
//   [Col Izq  ]   [Centro   ]    [Col Der  ]
//   [Base Verde]  [Fila Bot]     [Base Amarilla]
//
// Las 40 casillas del camino exterior están indexadas 0..39 en sentido horario.
// Cada equipo tiene su zona de base (2×2) y zona de llegada (4×1).
//
// NOTA: El layout exacto depende de la numeración que defina tu compañero
// en BoardInterface. Ajusta los índices en `buildTrack()` cuando lo tengas.
// ─────────────────────────────────────────────────────────────────────────────
class BoardView(board: BoardInterface, onPieceClick: Int => Unit) extends GridPane:

  hgap = 2
  vgap = 2
  padding = Insets(10)
  style = "-fx-background-color: #F5F5F5; -fx-background-radius: 12;"
  alignment = Pos.Center

  // Zonas de base (4 esquinas del tablero)
  private val baseRed    = new BaseZone(PlayerColor.Red,    piecesOf(PlayerColor.Red,    board), onPieceClick)
  private val baseBlue   = new BaseZone(PlayerColor.Blue,   piecesOf(PlayerColor.Blue,   board), onPieceClick)
  private val baseGreen  = new BaseZone(PlayerColor.Green,  piecesOf(PlayerColor.Green,  board), onPieceClick)
  private val baseYellow = new BaseZone(PlayerColor.Yellow, piecesOf(PlayerColor.Yellow, board), onPieceClick)

  // Celdas del camino exterior (40 casillas, índices 0..39)
  // Ajusta estos índices según lo que defina tu compañero
  private var trackCells: Map[Int, Node] = Map.empty

  buildLayout()

  // ── Layout ────────────────────────────────────────────────────────

  private def buildLayout(): Unit =
    children.clear()

    // Esquina superior izquierda → Base Roja (ocupa 6×6 celdas lógicas)
    add(baseRed, col = 0, row = 0, colSpan = 6, rowSpan = 6)

    // Esquina superior derecha → Base Azul
    add(baseBlue, col = 9, row = 0, colSpan = 6, rowSpan = 6)

    // Esquina inferior izquierda → Base Verde
    add(baseGreen, col = 0, row = 9, colSpan = 6, rowSpan = 6)

    // Esquina inferior derecha → Base Amarilla
    add(baseYellow, col = 9, row = 9, colSpan = 6, rowSpan = 6)

    // Pista exterior: 40 casillas alrededor del perímetro
    buildTrack()

  private def buildTrack(): Unit =
    // Mapeo de posición lógica (0..39) → (col, row) en el GridPane
    // Sentido horario empezando desde la salida de Rojo (pos=0, col=6, row=14)
    // TODO: ajustar coordenadas exactas cuando tengas la numeración de tu compañero
    val trackPositions: Vector[(Int, Int)] = (
      // Columna izquierda bajando (fila 8..14, col 6) → pos 0..6
      (6 to 14).map(r => (6, r)) ++
      // Fila inferior izquierda → pos 7..13
      (0 to 6).map(c => (c, 14)).reverse ++
      // ... (completa el resto del recorrido)
      Vector.empty
    ).toVector

    board.cells.zipWithIndex.foreach { (cell, idx) =>
      val cellNode = CellView(cell, onPieceClick)
      trackCells = trackCells + (idx -> cellNode)
      GridPane.setColumnIndex(cellNode, idx % 15)
      GridPane.setRowIndex(cellNode, 6 + idx / 15)
      children.add(cellNode)
    }

  // ── Actualización del estado ──────────────────────────────────────

  def updateBoard(newBoard: BoardInterface): Unit =
    newBoard.cells.foreach { cell =>
      trackCells.get(cell.position).foreach { node =>
        val newCell = CellView(cell, onPieceClick)
        val idx = children.indexOf(node)
        if idx >= 0 then children.set(idx, newCell)
        trackCells = trackCells + (cell.position -> newCell)
      }
    }
    baseRed.update(piecesOf(PlayerColor.Red, newBoard))
    baseBlue.update(piecesOf(PlayerColor.Blue, newBoard))
    baseGreen.update(piecesOf(PlayerColor.Green, newBoard))
    baseYellow.update(piecesOf(PlayerColor.Yellow, newBoard))

  // ── Helpers ───────────────────────────────────────────────────────

  private def piecesOf(color: PlayerColor, b: BoardInterface): Vector[PieceInterface] =
    b.cells.flatMap(_.piece).filter(_.color == color)

  private def add(
    node: Node,
    col: Int, row: Int,
    colSpan: Int = 1, rowSpan: Int = 1
  ): Unit =
    GridPane.setColumnInde@@x(node, col)
    GridPane.setRowIndex(node, row)
    GridPane.setColumnSpan(node, colSpan)
    GridPane.setRowSpan(node, rowSpan)
    children.add(node)

```


#### Short summary: 

empty definition using pc, found symbol in pc: 