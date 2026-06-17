package ludo.shared

// ── Colores de jugador ────────────────────────────────────────────
enum JugadorColor:
  case Red, Blue, Green, Yellow

// ── Modelos ───────────────────────────────────────────────────────
trait InterfazPieza:
  def id: Int
  def color: JugadorColor
  def posicion: Int
  def estaEnBase: Boolean
  def estaEnCasa: Boolean

trait InterfazCasilla:
  def posicion: Int
  def enPista: Boolean
  def piece: Option[InterfazPieza]
  def esInicioPieza: Option[JugadorColor]
  def estaEnCasaOf: Option[JugadorColor]

trait InterfazTablero:
  def CasillaPista: Vector[InterfazCasilla]
  def size: Int
  def CasillaCasa: Map[JugadorColor, Vector[InterfazCasilla]] // 5 por color
  def basePieces: Map[JugadorColor, Vector[InterfazPieza]]

  def cells: Vector[InterfazCasilla] = CasillaPista ++ CasillaCasa.values.flatten

trait InterfazJugador:
  def name: String
  def color: JugadorColor
  def pieces: Vector[InterfazPieza]
  def PiezasEnBase: Vector[InterfazPieza]
  def PiezasEnTablero: Vector[InterfazPieza]
  def piezasEnCasa: Vector[InterfazPieza]

// ── Eventos ───────────────────────────────────────────────────────
sealed trait GameEvent
case class LanzarDado(value: Int)                                        extends GameEvent
case class MoverPieza(oldBoard: InterfazTablero, nuevoTablero: InterfazTablero) extends GameEvent
case class CambioTurno(jugador: InterfazJugador)                          extends GameEvent
case class ActualizarMensajes(text: String)                                  extends GameEvent
case class gameOver(winner: InterfazJugador)                             extends GameEvent
case class inicioJuego(board: InterfazTablero)                            extends GameEvent

// ── Contrato Controller ───────────────────────────────────────────
trait InterfazControlador:
  def board: InterfazTablero
  def jugadorActual: InterfazJugador
  def jugadores: Vector[InterfazJugador]
  def dadoUltimoValor: Option[Int]
  def piezasMovibles: Vector[InterfazPieza]

  def comenzarJuego(jugadorCount: Int): Unit
  def lanzarDado(): Unit
  def moverPieza(pieceId: Int): Unit

  def subscribe(handler: GameEvent => Unit): Unit
  def unsubscribe(handler: GameEvent => Unit): Unit
