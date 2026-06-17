
## Estructura

```
src/main/scala/ludo/
├── shared/
│   └── Shared.scala          
├── gui/
│   ├── LudoApp.scala         -> aqui corres todo es como el main
│   ├── mock/
│   │   └── MockController.scala  -> aqui se supone que debes desarrollar la logica, llamas a los traits de shared.scala y creas objetos tipo components (basezone,boardview,etc)
│   └── components/
│       ├── CellView.scala    -> casilla de tablero
│       ├── BaseZone.scala    -> las bases de cada tablero
│       ├── BoardView.scala   -> tablero de 15x15
│       └── ControlPanel.scala -> dado , mensajes 
```
