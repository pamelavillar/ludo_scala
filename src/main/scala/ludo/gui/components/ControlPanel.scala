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
class ControlPanel(controller: ControllerInterface) extends HBox:

  spacing = 12
  padding = Insets(16)
  style = "-fx-background-color: #FAFAFA; -fx-border-color: #E0E0E0; -fx-border-width: 1 0 0 0;"


  // ----- Imagen de world cup

  private val imageView = new ImageView:
    image = new Image(getClass.getResourceAsStream("/img/world_cup.png"))
    fitWidth = 120
    fitHeight = 180
    preserveRatio = false

  // ── Indicador de turno ────────────────────────────────────────────
  private val turnLabel = new Label("Turno de: —"):
    font  = Font.font("Oswald", FontWeight.Medium, 16)
    style = "-fx-text-fill: #333;"


  private val turnIndicator = new HBox:
    spacing = 10
    alignment = Pos.CenterLeft
    children = Seq(
      new Circle:
        radius = 10
        fill   = Color.Gray
        id     = "turn-circle"
      ,
      turnLabel
    )


  // ── Dado visual ───────────────────────────────────────────────────
  private val diceLabel = new Label("?"):
    font  = Font.font("Oswald", FontWeight.Medium, 25)
    style = "-fx-text-fill: #222;"

  private val diceBox = new StackPane:
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
    children = Seq(diceLabel)

         // ── Botón lanzar dado ─────────────────────────────────────────────
  private val rollButton = new Button("Lanzar dado"):
    prefWidth  = 160
    prefHeight = 44
    font       = Font.font("Oswald", FontWeight.Medium, 15)
    style = """
      -fx-background-color: #1565C0;
      -fx-text-fill: white;
      -fx-background-radius: 8;
      -fx-cursor: hand;
    """
    onAction = _ => controller.rollDice()

  rollButton.onMouseEntered = _ =>
    rollButton.style = """
      -fx-background-color: #0D47A1;
      -fx-text-fill: white;
      -fx-background-radius: 8;
      -fx-cursor: hand;
    """
  rollButton.onMouseExited = _ =>
    rollButton.style = """
      -fx-background-color: #1565C0;
      -fx-text-fill: white;
      -fx-background-radius: 8;
      -fx-cursor: hand;
    """

  // ── Mensajes del juego ────────────────────────────────────────────
  private val messageArea = new TextArea:
    prefHeight = 90
    editable   = false
    wrapText   = true
    font       = Font.font("Oswald", 13)
    style      = "-fx-control-inner-background: #F0F0F0; -fx-border-radius: 6;"

  private def appendMessage(text: String): Unit =
    messageArea.appendText(s"• $text\n")
    messageArea.scrollTop = Double.MaxValue

  // ── Fila superior: dado + botón + turno ───────────────────────────
  private val topRow = new HBox:
    spacing   = 20
    alignment = Pos.CenterLeft
    padding   = Insets(0, 0, 8, 0)
    children  = Seq(diceBox, rollButton, turnIndicator)

  children = Seq(topRow, messageArea)


  private val rightPanel = new VBox:
    spacing = 12
    children = Seq(topRow, messageArea)
  
  children = Seq(imageView,rightPanel)

  // ── API pública ───────────────────────────────────────────────────

  def showDice(value: Int): Unit =
    diceLabel.text = diceFace(value)

  def updateTurn(player: PlayerInterface): Unit =
    val color = CellView.colorOf(player.color)
    turnLabel.text = s"Turno de: ${player.name}"
    // actualiza el círculo de color de turno
    turnIndicator.children.head match
      case c: javafx.scene.shape.Circle =>
        c.setFill(color)
      case _ =>

  def showMessage(text: String): Unit =
    messageArea.text = s"${text}"

  def setRollEnabled(enabled: Boolean): Unit =
    rollButton.disable = !enabled

  // ── Caras del dado como emoji ─────────────────────────────────────
  private def diceFace(n: Int): String = n match
    case 1 => "1"
    case 2 => "2"
    case 3 => "3"
    case 4 => "4"
    case 5 => "5"
    case 6 => "6"
    case _ => "?"
