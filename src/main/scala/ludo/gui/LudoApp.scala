package ludo.gui

import ludo.shared.*
import ludo.gui.components.*
import ludo.gui.mock.MockController
import scalafx.application.JFXApp3
import scalafx.application.JFXApp3.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.*
import scalafx.scene.control.{MenuBar, Menu, MenuItem}
import scalafx.scene.text.{Font, FontWeight}
import scalafx.scene.control.Label
import scalafx.geometry.{Insets, Pos}
import scalafx.application.Platform


object LudoApp extends JFXApp3:

  override def start(): Unit =

    // ── 1. Controller (cambia MockController por el real cuando esté listo) ──
    val controller: InterfazControlador = new MockController()

    // ── 2. Componentes ───────────────────────────────────────────────────────
    val boardView    = new BoardView(controller.board,controller.jugadores, pieceId => controller.moverPieza(pieceId))
    val controlPanel = new ControlPanel(controller)

    // ── 3. Suscripción a eventos ─────────────────────────────────────────────
    // TODA la lógica de reacción está aquí centralizada.
    // Cada evento actualiza solo el componente que corresponde.
    controller.subscribe { event =>
      // Los eventos del controller pueden venir de otro hilo.
      // Platform.runLater garantiza que los cambios de UI ocurran en el hilo de JavaFX.
      Platform.runLater {
        event match
          case inicioJuego(board) =>
            boardView.updateBoard(board)

          case LanzarDado(value) =>
            controlPanel.mostrar_dado(value)
            controlPanel.dadoHabilitado(false)   // espera que el jugador mueva

          case MoverPieza(_, nuevoTablero) =>
            boardView.updateBoard(nuevoTablero)
            controlPanel.dadoHabilitado(true)    // nuevo turno, puede volver a tirar

          case CambioTurno(jugador) =>
            controlPanel.updateTurn(jugador)

          case ActualizarMensajes(text) =>
            controlPanel.showMessage(text)

          case gameOver(winner) =>
            controlPanel.showMessage(s"¡${winner.name} ganó la partida!")
            controlPanel.dadoHabilitado(false)
      }
    }

    // ── 4. Menú ───────────────────────────────────────────────────────────────
    val menuBar = buildMenuBar(controller)

    // ── 5. Layout principal ───────────────────────────────────────────────────
    val root = new BorderPane:
      top    = menuBar
      center = boardView
      bottom = controlPanel

    // ── 6. Escena y Stage ────────────────────────────────────────────────────
    stage = new PrimaryStage:
      title  = "Ludo"
      width  = 620
      height = 880
      scene  = new Scene(root):
        stylesheets.add(getClass.getResource("/style.css").toExternalForm)

    // ── 7. Arranca el juego (2 jugadores por defecto) ─────────────────────────
    controller.comenzarJuego(2)

  // ── Menú de partida ───────────────────────────────────────────────────────
  private def buildMenuBar(controller: InterfazControlador): MenuBar =
    val gameMenu = new Menu("Partida"):
      items = Seq(
        new MenuItem("Nueva partida (2 jugadores)"):
          onAction = _ => controller.comenzarJuego(2)
        ,
        new MenuItem("Nueva partida (4 jugadores)"):
          onAction = _ => controller.comenzarJuego(4)
      )
    new MenuBar:
      menus = Seq(gameMenu)
