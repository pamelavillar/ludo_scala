package ludo.gui.components

import ludo.shared.*
import scalafx.scene.layout.*
import scalafx.scene.control.{Button, Label, TextArea}
import scalafx.scene.text.{Font, FontWeight}
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.paint.Color
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.effect.DropShadow
import scalafx.scene.image.{Image,ImageView}

// ─────────────────────────────────────────────────────────────────────────────
// ControlPanel — panel inferior con:
//   • Indicador del jugador actual
//   • Cara visual del dado
//   • Botón "Lanzar dado"
//   • Caja de mensajes del juego
// ─────────────────────────────────────────────────────────────────────────────
class ControlPanel(controller: InterfazControlador) extends HBox:

  spacing = 12
  padding = Insets(16)
  style = "-fx-background-color: #FAFAFA; -fx-border-color: #E0E0E0; -fx-border-width: 1 0 0 0;"


  // Imagen de world cup

  private val imageView = new ImageView:
    image = new Image(getClass.getResourceAsStream("/img/world_cup.png"))
    fitWidth = 120
    fitHeight = 180
    preserveRatio = false

  // TURNO
  private val turnoLabel = new Label("Turno de: —"):
    font  = Font.font("Oswald", FontWeight.Medium, 16)
    style = "-fx-text-fill: #333;"


  private val indicaTurno = new HBox:
    spacing = 10
    alignment = Pos.CenterLeft
    children = Seq(
      new Circle:
        radius = 10
        fill   = Color.Gray
        id     = "turn-circle"
      ,
      turnoLabel
    )


  // DADO
  private val labelDado = new Label("?"):
    font  = Font.font("Oswald", FontWeight.Medium, 25)
    style = "-fx-text-fill: #222;"

  private val cajaDado = new StackPane:
    prefWidth  = 70
    prefHeight = 70
    alignment  = Pos.Center
    style = """
      -fx-background-color: white;
      -fx-border-color: #333;
      -fx-border-width: 3;
      -fx-border-radius: 10;
      -fx-background-radius: 10;
    """
    children = Seq(labelDado)

  // ── Botón lanzar dado ─────────────────────────────────────────────
  private val botonLanzar = new Button("Lanzar dado"):
    prefWidth  = 160
    prefHeight = 44
    font       = Font.font("Oswald", FontWeight.Medium, 15)
    style = """
      -fx-background-color: #1565C0;
      -fx-text-fill: white;
      -fx-background-radius: 8;
      -fx-cursor: hand;
    """
    onAction = _ => controller.lanzarDado()

  botonLanzar.onMouseEntered = _ =>
    botonLanzar.style = """
      -fx-background-color: #0D47A1;
      -fx-text-fill: white;
      -fx-background-radius: 8;
      -fx-cursor: hand;
    """
  botonLanzar.onMouseExited = _ =>
    botonLanzar.style = """
      -fx-background-color: #1565C0;
      -fx-text-fill: white;
      -fx-background-radius: 8;
      -fx-cursor: hand;
    """

  // ── mensajes
  private val areaMensaje = new TextArea:
    prefHeight = 40
    editable   = true
    wrapText   = true
    font       = Font.font("Oswald", 13)
    style      = "-fx-control-inner-background: #FFFFFF;"


  private val topRow = new HBox:
    spacing   = 20
    alignment = Pos.CenterLeft
    padding   = Insets(0, 0, 8, 0)
    children  = Seq(cajaDado, botonLanzar, indicaTurno)



  private val parteDerecha = new VBox:
    spacing = 12
    children = Seq(topRow, areaMensaje)
  
  children = Seq(imageView,parteDerecha)

  

  def mostrar_dado(value: Int): Unit =
    labelDado.text = caraDado(value)

  def updateTurn(jugador: InterfazJugador): Unit =
    val color = CellView.colorOf(jugador.color)
    turnoLabel.text = s"Turno de: ${jugador.name}"
   
    indicaTurno.children.head match
      case c: javafx.scene.shape.Circle =>
        c.setFill(color)
      case _ =>

  def showMessage(text: String): Unit =
    areaMensaje.text = s"${text}"

  def dadoHabilitado(enabled: Boolean): Unit =
    botonLanzar.disable = !enabled

  //  caras del dado 
  private def caraDado(n: Int): String = n match
    case 1 => "1"
    case 2 => "2"
    case 3 => "3"
    case 4 => "4"
    case 5 => "5"
    case 6 => "6"
    case _ => "?"
