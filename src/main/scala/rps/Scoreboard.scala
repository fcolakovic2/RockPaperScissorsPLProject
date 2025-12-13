package rps

case class PlayerStats(
  name: String,
  matchesPlayed: Int = 0,
  matchesWon: Int = 0,
  roundsWon: Int = 0
) {
  def winRate: Double = 
    if (matchesPlayed == 0) 0.0 
    else (matchesWon.toDouble / matchesPlayed) * 100
}

object Scoreboard {
  private var stats: Map[String, PlayerStats] = Map.empty

  // Load stats on startup if available
  def initialize(): Unit = {
    SaveLoad.loadStats() match {
      case scala.util.Success(loadedStats) => 
        stats = loadedStats
        if (loadedStats.nonEmpty) println(Console.GREEN + "Loaded previous scoreboard data." + Console.RESET)
      case scala.util.Failure(_) => // No saved stats, start fresh
    }
  }

  // Expose stats for saving
  def getAllStats: Map[String, PlayerStats] = stats

  def updateMatchStats(p1Name: String, p1RoundsWon: Int, p2Name: String, p2RoundsWon: Int): Unit = {
    val p1Stats = stats.getOrElse(p1Name, PlayerStats(p1Name))
    val p2Stats = stats.getOrElse(p2Name, PlayerStats(p2Name))

    val p1Won = p1RoundsWon > p2RoundsWon
    val p2Won = p2RoundsWon > p1RoundsWon

    stats = stats.updated(
      p1Name,
      p1Stats.copy(
        matchesPlayed = p1Stats.matchesPlayed + 1,
        matchesWon = if (p1Won) p1Stats.matchesWon + 1 else p1Stats.matchesWon,
        roundsWon = p1Stats.roundsWon + p1RoundsWon
      )
    )

    stats = stats.updated(
      p2Name,
      p2Stats.copy(
        matchesPlayed = p2Stats.matchesPlayed + 1,
        matchesWon = if (p2Won) p2Stats.matchesWon + 1 else p2Stats.matchesWon,
        roundsWon = p2Stats.roundsWon + p2RoundsWon
      )
    )
  }

  // Save stats after each match completion
  def saveStats(): Unit = {
    SaveLoad.saveStats(stats)
  }

  def show(): Unit = {
    println(Console.YELLOW + "\n=== SCOREBOARD ===" + Console.RESET)
    if (stats.isEmpty) {
      println("No matches played yet.")
    } else {
      println("Sort by: (1) Matches Won  (2) Win Rate")
      val sortChoice = scala.io.StdIn.readLine("Choose sorting (default: 1): ")
      
      val sorted = sortChoice match {
        case "2" => stats.values.toList.sortBy(_.winRate)(Ordering.Double.reverse)
        case _   => stats.values.toList.sortBy(_.matchesWon)(Ordering.Int.reverse)
      }

      println(f"\n${"Player"}%-15s ${"Matches"}%-10s ${"Won"}%-10s ${"Rounds"}%-10s ${"Win Rate"}%-10s")
      println("=" * 65)
      sorted.foreach { ps =>
        println(f"${ps.name}%-15s ${ps.matchesPlayed}%-10d ${ps.matchesWon}%-10d ${ps.roundsWon}%-10d ${ps.winRate}%-10.1f%%")
      }
    }
    println("==================\n")
  }
}
