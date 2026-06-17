# ludo-gui — Esqueleto GUI en ScalaFX

## Estructura

```
src/main/scala/ludo/
├── shared/
│   └── Shared.scala          ← traits compartidos con la lógica (ControllerInterface, eventos, modelos)
├── gui/
│   ├── LudoApp.scala         ← entrada principal, suscripción a eventos, layout raíz
│   ├── mock/
│   │   └── MockController.scala  ← controller falso para desarrollo
│   └── components/
│       ├── CellView.scala    ← una casilla del tablero
│       ├── BaseZone.scala    ← zona 2×2 de fichas en casa (esquinas)
│       ├── BoardView.scala   ← tablero completo (15×15 grid)
│       └── ControlPanel.scala← dado, botón, turno, mensajes
```

## Cómo correr

```bash
sbt run
```

## Cómo conectar la lógica real

En `LudoApp.scala`, cambia **una sola línea**:

```scala
// Antes (desarrollo):
val controller: ControllerInterface = new MockController()

// Después (producción):
val controller: ControllerInterface = new Controller()   // implementación real
```

Nada más cambia en la GUI.

## Flujo de eventos

```
GUI                              Controller
 │  controller.rollDice()    →      │
 │                     DiceRolled   │
 │  ←─────────────────────────────  │   showDice(value)
 │  controller.movePiece(id) →      │
 │                    PieceMoved    │
 │  ←─────────────────────────────  │   boardView.updateBoard(newBoard)
 │                   TurnChanged    │
 │  ←─────────────────────────────  │   controlPanel.updateTurn(player)
```

## Pendiente: layout exacto del tablero

`BoardView.scala` tiene un TODO marcado en `buildTrack()`.
Una vez que tu compañero defina la numeración de las 40 casillas,
actualiza el `Map[Int, (col, row)]` en ese método para posicionarlas
correctamente en el GridPane de 15×15.
```
