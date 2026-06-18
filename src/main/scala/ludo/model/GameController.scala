package ludo.model

import ludo.shared.*
import scala.util.Random

enum ZonaPieza {
  case Base, Pista, RectaFinal, Meta
}

import ZonaPieza.*

case class Pieza(
    id: Int,
    color: JugadorColor,
    posicion: Int,
    zona: ZonaPieza
) extends InterfazPieza {

  def estaEnBase: Boolean = {
    zona == Base
  }

  def estaEnCasa: Boolean = {
    zona == Meta
  }
}

case class Casilla(
    posicion: Int,
    enPista: Boolean,
    piece: Option[InterfazPieza],
    esInicioPieza: Option[JugadorColor],
    estaEnCasaOf: Option[JugadorColor],
    cantidadPiezas: Int = 0
) extends InterfazCasilla

case class Jugador(
    name: String,
    color: JugadorColor,
    piezasInternas: Vector[Pieza]
) extends InterfazJugador {

  def pieces: Vector[InterfazPieza] = {
    piezasInternas
  }

  def PiezasEnBase: Vector[InterfazPieza] = {
    piezasInternas.filter(_.estaEnBase)
  }

  def PiezasEnTablero: Vector[InterfazPieza] = {
    piezasInternas.filter(p => p.zona == Pista || p.zona == RectaFinal)
  }

  def piezasEnCasa: Vector[InterfazPieza] = {
    piezasInternas.filter(_.estaEnCasa)
  }
}

case class Tablero(
    CasillaPista: Vector[InterfazCasilla],
    CasillaCasa: Map[JugadorColor, Vector[InterfazCasilla]],
    basePieces: Map[JugadorColor, Vector[InterfazPieza]],
    size: Int = 52
) extends InterfazTablero

class GameController extends InterfazControlador {

  private var handlers: List[GameEvent => Unit] = Nil

  private def emit(event: GameEvent): Unit = {
    handlers.foreach(handler => handler(event))
  }

  private val ordenColores: Vector[JugadorColor] = Vector(
    JugadorColor.Red,
    JugadorColor.Blue,
    JugadorColor.Green,
    JugadorColor.Yellow
  )

  private val posicionesInicio: Map[JugadorColor, Int] = Map(
    JugadorColor.Red -> 1,
    JugadorColor.Blue -> 14,
    JugadorColor.Yellow -> 27,
    JugadorColor.Green -> 40
  )

  private val longitudPista: Int = 52
  private val longitudRectaFinal: Int = 5

  private var coloresActivos: Vector[JugadorColor] = {
    ordenColores.take(2)
  }

  private var jugadorActualIdx: Int = 0

  private var _lastDice: Option[Int] = None

  private var _pieces: Vector[Pieza] = {
    crearPiezas(coloresActivos)
  }

  private var _board: InterfazTablero = {
    construirTablero()
  }

  // Métodos del controlador

  def board: InterfazTablero = {
    _board
  }

  def jugadorActual: InterfazJugador = {
    jugadores(jugadorActualIdx)
  }

  def jugadores: Vector[InterfazJugador] = {
    coloresActivos.zipWithIndex.map { case (color, idx) =>
      Jugador(
        name = s"Jugador ${idx + 1}",
        color = color,
        piezasInternas = piezasDeColor(color)
      )
    }
  }

  def dadoUltimoValor: Option[Int] = {
    _lastDice
  }

  def piezasMovibles: Vector[InterfazPieza] = {
    _lastDice match {
      case None =>
        Vector.empty

      case Some(dado) =>
        piezasDeColor(jugadorActual.color)
          .filter(p => puedeMover(p, dado))
          .map(p => p: InterfazPieza)
    }
  }

  def subscribe(handler: GameEvent => Unit): Unit = {
    handlers = handler :: handlers
  }

  def unsubscribe(handler: GameEvent => Unit): Unit = {
    handlers = handlers.filterNot(h => h == handler)
  }

  def comenzarJuego(jugadorCount: Int): Unit = {
    val cantidad = jugadorCount.max(2).min(4)

    coloresActivos = ordenColores.take(cantidad)
    jugadorActualIdx = 0
    _lastDice = None
    _pieces = crearPiezas(coloresActivos)
    _board = construirTablero()

    emit(inicioJuego(_board))
    emit(CambioTurno(jugadorActual))
    emit(ActualizarMensajes(s"Partida iniciada con $cantidad jugadores. Turno de ${jugadorActual.name}."))
  }

  def lanzarDado(): Unit = {
    if (_lastDice.nonEmpty) {
      emit(ActualizarMensajes("Ya lanzaste el dado. Ahora debes elegir una ficha"))
      return
    }

    val value = Random.nextInt(6) + 1
    _lastDice = Some(value)

    emit(LanzarDado(value))

    val movibles = piezasMovibles

    if (movibles.isEmpty) {
      emit(ActualizarMensajes(s"${jugadorActual.name} $value, pero no tiene movimiento posibles."))

      _lastDice = None

      if (value == 6) {
        emit(ActualizarMensajes(s"${jugadorActual.name} saco 6, por eso puede volver a lanzar."))
      } else {
        avanzarTurno()
        emit(CambioTurno(jugadorActual))
        emit(ActualizarMensajes(s"Turno de ${jugadorActual.name}"))
      }

      emit(MoverPieza(_board, _board))
    } else {
      emit(ActualizarMensajes(s"${jugadorActual.name} saco $value. Elige una ficha."))
    }
  }

  def moverPieza(pieceId: Int): Unit = {
    _lastDice match {
      case None =>
        emit(ActualizarMensajes("Primero debes lanzar el dado."))

      case Some(dado) =>
        buscarPieza(pieceId) match {
          case None =>
            emit(ActualizarMensajes("No se encontró la ficha seleccionada."))

          case Some(pieza) =>
            if (pieza.color != jugadorActual.color) {
              emit(ActualizarMensajes(s"Esa ficha no te pertenece. PILLO!!!"))
            } else if (!puedeMover(pieza, dado)) {
              emit(ActualizarMensajes(s"Esa ficha no puede moverse con el dado $dado."))
            } else {
              val oldBoard = _board

              val piezaMovida = calcularNuevaPieza(pieza, dado).get

              actualizarPieza(pieceId, _ => piezaMovida)

              aplicarCapturaSiCorresponde(piezaMovida)

              _board = construirTablero()

              emit(MoverPieza(oldBoard, _board))

              val jugadorDespuesDeMover = jugadorActual

              if (gano(jugadorDespuesDeMover.color)) {
                _lastDice = None
                emit(gameOver(jugadorDespuesDeMover))
              } else {
                _lastDice = None

                if (dado == 6) {
                  emit(ActualizarMensajes(s"${jugadorActual.name} saco 6, vuelve a lanzar."))
                  emit(CambioTurno(jugadorActual))
                } else {
                  avanzarTurno()
                  emit(CambioTurno(jugadorActual))
                  emit(ActualizarMensajes(s"Turno de ${jugadorActual.name}."))
                }
              }
            }
        }
    }
  }

  // Datos iniciales

  private def crearPiezas(colores: Vector[JugadorColor]): Vector[Pieza] = {
    colores.zipWithIndex.flatMap { case (color, colorIdx) =>
      val baseId = colorIdx * 4

      (1 to 4).map { i =>
        Pieza(
          id = baseId + i,
          color = color,
          posicion = -1,
          zona = Base
        )
      }
    }
  }

  private def piezasDeColor(color: JugadorColor): Vector[Pieza] = {
    _pieces.filter(_.color == color)
  }

  private def construirTablero(): InterfazTablero = {
    val casillasPista: Vector[InterfazCasilla] = {
      (0 until longitudPista).map { pos =>
        val piezasEnCasilla =
          _pieces.filter(p => p.zona == Pista && p.posicion == pos)

        Casilla(
          posicion = pos,
          enPista = true,
          piece = piezasEnCasilla.headOption.map(p => p: InterfazPieza),
          esInicioPieza = colorInicioEn(pos),
          estaEnCasaOf = None,
          cantidadPiezas = piezasEnCasilla.size
        )
      }.toVector
    }

    val casillasCasa: Map[JugadorColor, Vector[InterfazCasilla]] = {
      JugadorColor.values.map { color =>
        val cells = (0 until longitudRectaFinal).map { pos =>
          val piezasEnCasilla =
            _pieces.filter(p =>
              p.zona == RectaFinal &&
                p.color == color &&
                p.posicion == pos
            )

          Casilla(
            posicion = pos,
            enPista = false,
            piece = piezasEnCasilla.headOption.map(p => p: InterfazPieza),
            esInicioPieza = None,
            estaEnCasaOf = Some(color),
            cantidadPiezas = piezasEnCasilla.size
          )
        }.toVector

        color -> cells
      }.toMap
    }

    val piezasBase: Map[JugadorColor, Vector[InterfazPieza]] = {
      JugadorColor.values.map { color =>
        color -> _pieces
          .filter(_.color == color)
          .map(p => p: InterfazPieza)
      }.toMap
    }

    Tablero(
      CasillaPista = casillasPista,
      CasillaCasa = casillasCasa,
      basePieces = piezasBase
    )
  }


  // Reglas de movimiento

  private def puedeMover(pieza: Pieza, dado: Int): Boolean = {
    if (pieza.zona == Meta) {
      false
    } else {
      calcularNuevaPieza(pieza, dado) match {
        case None =>
          false

        case Some(nuevaPieza) =>
          destinoDisponible(pieza, nuevaPieza)
      }
    }
  }

  private def calcularNuevaPieza(pieza: Pieza, dado: Int): Option[Pieza] = {
    pieza.zona match {
      case Base =>
        if (dado == 1 || dado == 6) {
          val inicio = posicionesInicio(pieza.color)
          Some(pieza.copy(posicion = inicio, zona = Pista))
        } else {
          None
        }

      case Pista =>
        moverDesdePista(pieza, dado)

      case RectaFinal =>
        moverDesdeRectaFinal(pieza, dado)

      case Meta =>
        None
    }
  }

  private def moverDesdePista(pieza: Pieza, dado: Int): Option[Pieza] = {
    val inicio = posicionesInicio(pieza.color)
    val relativa = distanciaRelativaDesdeInicio(pieza.posicion, inicio)
    val nuevaRelativa = relativa + dado

    if (nuevaRelativa <= 50) {
      val nuevaPosicion = (inicio + nuevaRelativa) % longitudPista
      Some(pieza.copy(posicion = nuevaPosicion, zona = Pista))
    } else {
      val posicionEnRecta = nuevaRelativa - 51

      if (posicionEnRecta < longitudRectaFinal) {
        Some(pieza.copy(posicion = posicionEnRecta, zona = RectaFinal))
      } else if (posicionEnRecta == longitudRectaFinal) {
        Some(pieza.copy(posicion = longitudRectaFinal, zona = Meta))
      } else {
        None
      }
    }
  }

  private def moverDesdeRectaFinal(pieza: Pieza, dado: Int): Option[Pieza] = {
    val nuevaPosicion = pieza.posicion + dado

    if (nuevaPosicion < longitudRectaFinal) {
      Some(pieza.copy(posicion = nuevaPosicion, zona = RectaFinal))
    } else if (nuevaPosicion == longitudRectaFinal) {
      Some(pieza.copy(posicion = longitudRectaFinal, zona = Meta))
    } else {
      None
    }
  }

  private def destinoDisponible(piezaOriginal: Pieza, piezaNueva: Pieza): Boolean = {
    piezaNueva.zona match {
      case Base =>
        false

      case Meta =>
        true

      case RectaFinal =>
        true

      case Pista =>
        val destino = piezaNueva.posicion

        val ocupantes = _pieces.filter { p =>
          p.id != piezaOriginal.id &&
            p.zona == Pista &&
            p.posicion == destino
        }

        if (ocupantes.isEmpty) {
        true
        }
        
        else if (esCasillaInicial(destino)) {

        ocupantes.forall(p => p.color == piezaOriginal.color)

        } 
        
        else {

        val todosSonPropias =
            ocupantes.forall(p => p.color == piezaOriginal.color)

        val todosSonEnemigas =
            ocupantes.forall(p => p.color != piezaOriginal.color)

        todosSonPropias || todosSonEnemigas
        
        }
    }
  }

  // Capturas y victoria

  private def aplicarCapturaSiCorresponde(piezaMovida: Pieza): Unit = {
    if (piezaMovida.zona != Pista) {
      return
    }

    if (esCasillaInicial(piezaMovida.posicion)) {
      return
    }

    val enemigas = _pieces.filter { p =>
      p.id != piezaMovida.id &&
        p.color != piezaMovida.color &&
        p.zona == Pista &&
        p.posicion == piezaMovida.posicion
    }

    enemigas.foreach { fichaCapturada =>
      actualizarPieza(
        fichaCapturada.id,
        p => p.copy(posicion = -1, zona = Base)
      )

      emit(ActualizarMensajes(s"Ficha ${fichaCapturada.id} fue capturada y volvió a base."))
    }
  }

  private def gano(color: JugadorColor): Boolean = {
    piezasDeColor(color).forall(p => p.zona == Meta)
  }

  // Helpers

  private def buscarPieza(pieceId: Int): Option[Pieza] = {
    _pieces.find(p => p.id == pieceId)
  }

  private def actualizarPieza(pieceId: Int, f: Pieza => Pieza): Unit = {
    _pieces = _pieces.map { p =>
      if (p.id == pieceId) {
        f(p)
      } else {
        p
      }
    }
  }

  private def avanzarTurno(): Unit = {
    jugadorActualIdx = (jugadorActualIdx + 1) % coloresActivos.size
  }

  private def distanciaRelativaDesdeInicio(posicionActual: Int, inicio: Int): Int = {
    (posicionActual - inicio + longitudPista) % longitudPista
  }

  private def colorInicioEn(posicion: Int): Option[JugadorColor] = {
    posicionesInicio.find { case (_, pos) => pos == posicion }.map(_._1)
  }

  private def esCasillaInicial(posicion: Int): Boolean = {
    posicionesInicio.values.exists(pos => pos == posicion)
  }
}