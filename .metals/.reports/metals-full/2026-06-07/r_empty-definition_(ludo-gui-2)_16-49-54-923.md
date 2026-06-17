error id: file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/LudoApp.scala:ludo/gui/mock/MockController#
file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/LudoApp.scala
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol ludo/gui/mock/MockController#
empty definition using fallback
non-local guesses:

offset: 1103
uri: file://<HOME>/Downloads/ludo-gui%202/src/main/scala/ludo/gui/LudoApp.scala
text:
```scala
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

// ─────────────────────────────────────────────────────────────────────────────
// LudoApp — punto de entrada de la GUI
//
// Para conectar la lógica real de tu compañero, solo cambia esta línea:
//   val controller: InterfazControlador = new MockController()
// por:
//   val controller: InterfazControlador = new Controller()   // la impl real
//
// Nada más cambia.
// ─────────────────────────────────────────────────────────────────────────────
object LudoApp extends JFXApp3:

  override def start(): Unit =

    // ── 1. Controller (cambia MockController por el real cuando esté listo) ──
    val controller: InterfazControlador = new MockCo@@ntroller()

    // ── 2. Componentes ───────────────────────────────────────────────────────
    val boardView    = new BoardView(controller.board, pieceId => controller.moverPieza(pieceId))
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
            controlPanel.showMessage(s"🏆 ¡${winner.name} ganó la partida!")
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
      width  = 720
      height = 820
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

```


#### Short summary: 

empty definition using pc, found symbol in pc: 