package ludo.shared

// ── Colores de jugador ────────────────────────────────────────────
enum PlayerColor:
  case Red, Blue, Green, Yellow

// ── Modelos ───────────────────────────────────────────────────────
trait PieceInterface:
  def id: Int
  def color: PlayerColor
  def position: Int
  def isAtBase: Boolean
  def isHome: Boolean

trait CellInterface:
  def position: Int
  def isTrack: Boolean
  def piece: Option[PieceInterface]
  def isStartOf: Option[PlayerColor]
  def isHomeOf: Option[PlayerColor]

trait BoardInterface:
  def trackCells: Vector[CellInterface]
  def size: Int
  def homeCells: Map[PlayerColor, Vector[CellInterface]] // 5 por color
  def basePieces: Map[PlayerColor, Vector[PieceInterface]]

  def cells: Vector[CellInterface] = trackCells ++ homeCells.values.flatten

trait PlayerInterface:
  def name: String
  def color: PlayerColor
  def pieces: Vector[PieceInterface]
  def piecesAtBase: Vector[PieceInterface]
  def piecesOnBoard: Vector[PieceInterface]
  def piecesAtHome: Vector[PieceInterface]

// ── Eventos ───────────────────────────────────────────────────────
sealed trait GameEvent
case class DiceRolled(value: Int)                                        extends GameEvent
case class PieceMoved(oldBoard: BoardInterface, newBoard: BoardInterface) extends GameEvent
case class TurnChanged(player: PlayerInterface)                          extends GameEvent
case class MessageUpdated(text: String)                                  extends GameEvent
case class GameOver(winner: PlayerInterface)                             extends GameEvent
case class GameStarted(board: BoardInterface)                            extends GameEvent

// ── Contrato Controller ───────────────────────────────────────────
trait ControllerInterface:
  def board: BoardInterface
  def currentPlayer: PlayerInterface
  def players: Vector[PlayerInterface]
  def lastDiceValue: Option[Int]
  def movablePieces: Vector[PieceInterface]

  def startGame(playerCount: Int): Unit
  def rollDice(): Unit
  def movePiece(pieceId: Int): Unit

  def subscribe(handler: GameEvent => Unit): Unit
  def unsubscribe(handler: GameEvent => Unit): Unit
