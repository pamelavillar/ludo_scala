package ludo.gui.mock

import ludo.shared.*

class MockController extends ControllerInterface:

  private var handlers: List[GameEvent => Unit] = Nil
  private def emit(event: GameEvent): Unit = handlers.foreach(_(event))

  // ── Datos falsos ─────────────────────────────────────────────────

  private def makePieces(colorParam: PlayerColor, baseOffset: Int): Vector[PieceInterface] =
    (1 to 4).map { i =>
      new PieceInterface:
        val id       = i
        val color    = colorParam
        val position = baseOffset + i - 1
        val isAtBase = i > 1   // fichas 3 y 4 en base, 1 y 2 en tablero
        val isHome   = false
    }.toVector

  private val allPlayers: Vector[PlayerInterface] = Vector(
    PlayerColor.Red    -> 0,
    PlayerColor.Blue   -> 10,
    PlayerColor.Green  -> 20,
    PlayerColor.Yellow -> 30
  ).zipWithIndex.map { case ((colorParam, offset), idx) =>
    new PlayerInterface:
      val name          = s"Jugador ${idx + 1}"
      val color: PlayerColor = colorParam
      val pieces        = makePieces(color, offset)
      val piecesAtBase  = pieces.filter(_.isAtBase)
      val piecesOnBoard = pieces.filterNot(_.isAtBase)
      val piecesAtHome  = Vector.empty
  }

  private def buildBoard(players: Vector[PlayerInterface]): BoardInterface =
  new BoardInterface:

    val trackCells: Vector[CellInterface] =
      (0 until 52).map { pos =>
        new CellInterface:
          val position = pos
          val isTrack = true

          val piece: Option[PieceInterface] =
            players.flatMap(_.piecesOnBoard).find(_.position == pos)

          val isStartOf: Option[PlayerColor] = pos match
            case 1  => Some(PlayerColor.Red)
            case 14 => Some(PlayerColor.Blue)
            case 27 => Some(PlayerColor.Yellow)
            case 40 => Some(PlayerColor.Green)
            case _  => None

          val isHomeOf: Option[PlayerColor] = None
      }.toVector

    val homeCells: Map[PlayerColor, Vector[CellInterface]] =
      PlayerColor.values.map { color =>
        color -> (0 until 5).map { pos =>
          new CellInterface:
            val position = pos
            val isTrack = false
            val piece: Option[PieceInterface] = None
            val isStartOf: Option[PlayerColor] = None
            val isHomeOf: Option[PlayerColor] = Some(color)
        }.toVector
      }.toMap

    val basePieces: Map[PlayerColor, Vector[PieceInterface]] = players.map(p => p.color -> p.piecesAtBase).toMap

    val size = 52

  private var currentPlayerIdx = 0
  private var _lastDice: Option[Int] = None
  private var _board = buildBoard(allPlayers)

  // ── ControllerInterface ──────────────────────────────────────────

  def board: BoardInterface             = _board
  def currentPlayer: PlayerInterface    = allPlayers(currentPlayerIdx)
  def players: Vector[PlayerInterface]  = allPlayers
  def lastDiceValue: Option[Int]        = _lastDice
  def movablePieces: Vector[PieceInterface] =
    currentPlayer.piecesOnBoard.take(1) // solo la primera para demo

  def subscribe(handler: GameEvent => Unit): Unit =
    handlers = handler :: handlers

  def unsubscribe(handler: GameEvent => Unit): Unit =
    handlers = handlers.filterNot(_ == handler)

  def startGame(playerCount: Int): Unit =
    emit(GameStarted(_board))
    //emit(MessageUpdated(s"¡Partida iniciada con $playerCount jugadores! Turno de ${currentPlayer.name}"))
    emit(TurnChanged(currentPlayer))

  def rollDice(): Unit =
    val value = scala.util.Random.nextInt(6) + 1
    _lastDice = Some(value)
    emit(DiceRolled(value))
    emit(MessageUpdated(s"${currentPlayer.name} sacó un $value — elige una ficha"))
    

  def movePiece(pieceId: Int): Unit =
    val oldBoard = _board
    // simula mover la ficha 1 posición adelante
    emit(PieceMoved(oldBoard, _board))
    currentPlayerIdx = (currentPlayerIdx + 1) % allPlayers.size
    _lastDice = None
    emit(TurnChanged(currentPlayer))
    emit(MessageUpdated(s"Turno de ${currentPlayer.name}"))
