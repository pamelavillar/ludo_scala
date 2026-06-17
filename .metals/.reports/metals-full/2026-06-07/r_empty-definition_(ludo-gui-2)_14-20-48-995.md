file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/components/BoardView.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -ludo/shared/piecesOf.
	 -ludo/shared/piecesOf#
	 -ludo/shared/piecesOf().
	 -scalafx/Includes.piecesOf.
	 -scalafx/Includes.piecesOf#
	 -scalafx/Includes.piecesOf().
	 -scalafx/scene/layout/piecesOf.
	 -scalafx/scene/layout/piecesOf#
	 -scalafx/scene/layout/piecesOf().
	 -piecesOf.
	 -piecesOf#
	 -piecesOf().
	 -scala/Predef.piecesOf.
	 -scala/Predef.piecesOf#
	 -scala/Predef.piecesOf().
offset: 3952
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
// en InterfazTablero. Ajusta los índices en `buildTrack()` cuando lo tengas.
// ─────────────────────────────────────────────────────────────────────────────
class BoardView(board: InterfazTablero, onPieceClick: Int => Unit) extends GridPane:

  hgap = 2
  vgap = 2
  padding = Insets(10)
  style = "-fx-background-color: #F5F5F5; -fx-background-radius: 12;"
  alignment = Pos.Center

  // Zonas de base (4 esquinas del tablero)
  private val baseRed    = new BaseZone(JugadorColor.Red,    piecesOf(JugadorColor.Red,    board), onPieceClick)
  private val baseBlue   = new BaseZone(JugadorColor.Blue,   piecesOf(JugadorColor.Blue,   board), onPieceClick)
  private val baseGreen  = new BaseZone(JugadorColor.Green,  piecesOf(JugadorColor.Green,  board), onPieceClick)
  private val baseYellow = new BaseZone(JugadorColor.Yellow, piecesOf(JugadorColor.Yellow, board), onPieceClick)

  // Celdas del camino exterior (40 casillas, índices 0..39)
  // Ajusta estos índices según lo que defina tu compañero
  private var CasillaPista: Map[Int, Node] = Map.empty

  dibujarTablero()

  // ── Layout ────────────────────────────────────────────────────────

  private def dibujarTablero(): Unit =
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
  board.cells.zipWithIndex.foreach { (cell, idx) =>
    val cellNode = CellView(cell, onPieceClick)
    CasillaPista = CasillaPista + (idx -> cellNode)
    GridPane.setColumnIndex(cellNode, idx % 15)
    GridPane.setRowIndex(cellNode, 6 + idx / 15)
    children.add(cellNode)
  }

  // ── Actualización del estado ──────────────────────────────────────

  def updateBoard(nuevoTablero: InterfazTablero): Unit =
    nuevoTablero.cells.foreach { cell =>
      CasillaPista.get(cell.posicion).foreach { node =>
        val newCell = CellView(cell, onPieceClick)
        val idx = children.indexOf(node)
        if idx >= 0 then children.set(idx, newCell)
        CasillaPista = CasillaPista + (cell.posicion -> newCell)
      }
    }
    baseRed.update(piecesOf(JugadorColor.Red, nuevoTablero))
    baseBlue.update(piecesOf(JugadorColor.Blue, nuevoTablero))
    baseGreen.update(pi@@ecesOf(JugadorColor.Green, nuevoTablero))
    baseYellow.update(piecesOf(JugadorColor.Yellow, nuevoTablero))

  // ── Helpers ───────────────────────────────────────────────────────

  private def piecesOf(color: JugadorColor, b: InterfazTablero): Vector[InterfazPieza] =
    b.cells.flatMap(_.piece).filter(_.color == color)

  private def add(
    node: Node,
    col: Int, row: Int,
    colSpan: Int = 1, rowSpan: Int = 1
  ): Unit =
    GridPane.setColumnIndex(node, col)
    GridPane.setRowIndex(node, row)
    GridPane.setColumnSpan(node, colSpan)
    GridPane.setRowSpan(node, rowSpan)
    children.add(node)

```


#### Short summary: 

empty definition using pc, found symbol in pc: 