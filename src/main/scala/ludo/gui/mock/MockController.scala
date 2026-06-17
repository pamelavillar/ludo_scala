package ludo.gui.mock

import ludo.shared.*

class MockController extends InterfazControlador:

  private var handlers: List[GameEvent => Unit] = Nil
  private def emit(event: GameEvent): Unit = handlers.foreach(_(event))

  // ── Datos falsos ─────────────────────────────────────────────────

  private def makePieces(colorParam: JugadorColor, baseOffset: Int): Vector[InterfazPieza] =
    (1 to 4).map { i =>
      new InterfazPieza:
        val id       = i
        val color    = colorParam
        val posicion = baseOffset + i - 1
        val estaEnBase = i > 1   // fichas 3 y 4 en base, 1 y 2 en tablero
        val estaEnCasa   = false
    }.toVector

  private val alljugadores: Vector[InterfazJugador] = Vector(
    JugadorColor.Red    -> 0,
    JugadorColor.Blue   -> 10,
    JugadorColor.Green  -> 20,
    JugadorColor.Yellow -> 30
  ).zipWithIndex.map { case ((colorParam, offset), idx) =>
    new InterfazJugador:
      val name          = s"Jugador ${idx + 1}"
      val color: JugadorColor = colorParam
      val pieces        = makePieces(color, offset)
      val PiezasEnBase  = pieces.filter(_.estaEnBase)
      val PiezasEnTablero = pieces.filterNot(_.estaEnBase)
      val piezasEnCasa  = Vector.empty
  }

  private def buildBoard(jugadores: Vector[InterfazJugador]): InterfazTablero =
  new InterfazTablero:

    val CasillaPista: Vector[InterfazCasilla] =
      (0 until 52).map { pos =>
        new InterfazCasilla:
          val posicion = pos
          val enPista = true

          val piece: Option[InterfazPieza] =
            jugadores.flatMap(_.PiezasEnTablero).find(_.posicion == pos)

          val esInicioPieza: Option[JugadorColor] = pos match
            case 1  => Some(JugadorColor.Red)
            case 14 => Some(JugadorColor.Blue)
            case 27 => Some(JugadorColor.Yellow)
            case 40 => Some(JugadorColor.Green)
            case _  => None

          val estaEnCasaOf: Option[JugadorColor] = None
      }.toVector

    val CasillaCasa: Map[JugadorColor, Vector[InterfazCasilla]] =
      JugadorColor.values.map { color =>
        color -> (0 until 5).map { pos =>
          new InterfazCasilla:
            val posicion = pos
            val enPista = false
            val piece: Option[InterfazPieza] = None
            val esInicioPieza: Option[JugadorColor] = None
            val estaEnCasaOf: Option[JugadorColor] = Some(color)
        }.toVector
      }.toMap

    val basePieces: Map[JugadorColor, Vector[InterfazPieza]] = jugadores.map(p => p.color -> p.PiezasEnBase).toMap

    val size = 52

  private var jugadorActualIdx = 0
  private var _lastDice: Option[Int] = None
  private var _board = buildBoard(alljugadores)

  // ── InterfazControlador ──────────────────────────────────────────

  def board: InterfazTablero             = _board
  def jugadorActual: InterfazJugador    = alljugadores(jugadorActualIdx)
  def jugadores: Vector[InterfazJugador]  = alljugadores
  def dadoUltimoValor: Option[Int]        = _lastDice
  def piezasMovibles: Vector[InterfazPieza] =
    jugadorActual.PiezasEnTablero.take(1) // solo la primera para demo

  def subscribe(handler: GameEvent => Unit): Unit =
    handlers = handler :: handlers

  def unsubscribe(handler: GameEvent => Unit): Unit =
    handlers = handlers.filterNot(_ == handler)

  def comenzarJuego(jugadorCount: Int): Unit =
    emit(inicioJuego(_board))
    //emit(ActualizarMensajes(s"¡Partida iniciada con $jugadorCount jugadores! Turno de ${jugadorActual.name}"))
    emit(CambioTurno(jugadorActual))

  def lanzarDado(): Unit =
    val value = scala.util.Random.nextInt(6) + 1
    _lastDice = Some(value)
    emit(LanzarDado(value))
    emit(ActualizarMensajes(s"${jugadorActual.name} sacó un $value — elige una ficha"))
    

  def moverPieza(pieceId: Int): Unit =
    val oldBoard = _board
    // simula mover la ficha 1 posición adelante
    emit(MoverPieza(oldBoard, _board))
    jugadorActualIdx = (jugadorActualIdx + 1) % alljugadores.size
    _lastDice = None
    emit(CambioTurno(jugadorActual))
    emit(ActualizarMensajes(s"Turno de ${jugadorActual.name}"))
