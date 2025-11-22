package rps

object Scoreboard {
  private var scores: Map[String, Int] = Map.empty.withDefaultValue(0)

  def updateWinner(name: String): Unit =
    scores = scores.updated(name, scores(name) + 1)

  def show(): Unit = {
    println(Console.YELLOW + "\n=== SCOREBOARD ===" + Console.RESET)
    scores.foreach { case (name, score) =>
      println(s"$name: $score wins")
    }
    println("==================\n")
  }
}
