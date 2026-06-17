object DebugTrackPositions extends App {

  val trackPositions: Vector[(Int, Int)] = (

    (0 to 5).map(c => (c, 6)) ++

    (0 to 5).map(r => (6, r)).reverse ++
    Vector((7, 0)) ++
    (0 to 5).map(r => (8, r)) ++

    (9 to 14).map(c => (c, 6)) ++
    Vector((14, 7)) ++
    (9 to 14).map(c => (c, 8)).reverse ++

    (9 to 14).map(r => (8, r)) ++
    Vector((7, 14)) ++
    (9 to 14).map(r => (6, r)).reverse ++

    (0 to 5).map(c => (c, 8)).reverse ++
    Vector((0, 7))

  ).toVector

  println("=== TRACK POSITIONS ===")
  println()

  trackPositions.zipWithIndex.foreach { case ((x, y), idx) =>
    println(f"$idx%2d -> ($x,$y)")
  }

  println()
  println(s"Total positions: ${trackPositions.length}")

  // Buscar duplicados
  val duplicates =
    trackPositions
      .groupBy(identity)
      .collect {
        case (pos, occurrences) if occurrences.size > 1 => pos
      }

  println()

  if (duplicates.nonEmpty) {
    println("=== DUPLICATES FOUND ===")
    duplicates.foreach(println)
  } else {
    println("No duplicates found ✔")
  }

  // Visualización 15x15
  println()
  println("=== BOARD VISUALIZATION ===")

  val boardSize = 15

  for (row <- 0 until boardSize) {

    for (col <- 0 until boardSize) {

      val idx = trackPositions.indexOf((col, row))

      if (idx >= 0)
        print(f"$idx%02d ")
      else
        print(" . ")
    }

    println()
  }
}