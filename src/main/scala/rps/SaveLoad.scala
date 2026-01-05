package rps

import java.io.{File, PrintWriter}
import scala.io.Source
import scala.util.{Try, Success, Failure}

// Data model for game state that needs to be saved
case class GameState(
  p1Name: String,
  p1IsAI: Boolean,
  p1Difficulty: String,
  p2Name: String,
  p2IsAI: Boolean,
  p2Difficulty: String,
  ruleset: String,        // "classic" or "extended"
  matchFormat: String,    // "single", "bestOf", "firstTo"
  targetRounds: Int,
  currentP1Wins: Int,
  currentP2Wins: Int,
  roundNumber: Int
  // TODO: Add round history if analytics needed: roundHistory: List[(Move, Move, String)]
)

object SaveLoad {
  private val SAVE_FILE = "game_state.txt"
  private val STATS_FILE = "scoreboard_stats.txt"

  // TODO: Implement proper JSON serialization using Circe or Play JSON
  // For now, using simple text format as placeholder
  
  def saveGame(state: GameState): Try[Unit] = Try {
    val writer = new PrintWriter(new File(SAVE_FILE))
    try {
      // Simple text format - replace with JSON later
      writer.println(s"p1Name=${state.p1Name}")
      writer.println(s"p1IsAI=${state.p1IsAI}")
      writer.println(s"p1Difficulty=${state.p1Difficulty}")
      writer.println(s"p2Name=${state.p2Name}")
      writer.println(s"p2IsAI=${state.p2IsAI}")
      writer.println(s"p2Difficulty=${state.p2Difficulty}")
      writer.println(s"ruleset=${state.ruleset}")
      writer.println(s"matchFormat=${state.matchFormat}")
      writer.println(s"targetRounds=${state.targetRounds}")
      writer.println(s"currentP1Wins=${state.currentP1Wins}")
      writer.println(s"currentP2Wins=${state.currentP2Wins}")
      writer.println(s"roundNumber=${state.roundNumber}")
      println(Console.GREEN + "Game saved successfully!" + Console.RESET)
    } finally {
      writer.close()
    }
  }

  def loadGame(): Try[GameState] = Try {
    val source = Source.fromFile(SAVE_FILE)
    try {
      val lines = source.getLines().toList
      val data = lines.map { line =>
        val parts = line.split("=", 2)
        parts(0) -> parts(1)
      }.toMap

      // TODO: Add proper error handling and validation
      GameState(
        p1Name = data("p1Name"),
        p1IsAI = data("p1IsAI").toBoolean,
        p1Difficulty = data("p1Difficulty"),
        p2Name = data("p2Name"),
        p2IsAI = data("p2IsAI").toBoolean,
        p2Difficulty = data("p2Difficulty"),
        ruleset = data("ruleset"),
        matchFormat = data("matchFormat"),
        targetRounds = data("targetRounds").toInt,
        currentP1Wins = data("currentP1Wins").toInt,
        currentP2Wins = data("currentP2Wins").toInt,
        roundNumber = data("roundNumber").toInt
      )
    } finally {
      source.close()
    }
  }

  def hasSavedGame(): Boolean = {
    new File(SAVE_FILE).exists()
  }

  def deleteSaveFile(): Unit = {
    val file = new File(SAVE_FILE)
    if (file.exists()) {
      file.delete()
    }
  }

  // TODO: Implement scoreboard persistence
  def saveStats(stats: Map[String, PlayerStats]): Try[Unit] = Try {
    val writer = new PrintWriter(new File(STATS_FILE))
    try {
      stats.foreach { case (name, ps) =>
        // Simple format - replace with JSON
        writer.println(s"${ps.name}|${ps.matchesPlayed}|${ps.matchesWon}|${ps.roundsWon}")
      }
      println(Console.GREEN + "Stats saved successfully!" + Console.RESET)
    } finally {
      writer.close()
    }
  }

  def loadStats(): Try[Map[String, PlayerStats]] = Try {
    val file = new File(STATS_FILE)
    if (!file.exists()) {
      Map.empty[String, PlayerStats]
    } else {
      val source = Source.fromFile(file)
      try {
        source.getLines().map { line =>
          val parts = line.split("\\|")
          val ps = PlayerStats(
            name = parts(0),
            matchesPlayed = parts(1).toInt,
            matchesWon = parts(2).toInt,
            roundsWon = parts(3).toInt
          )
          ps.name -> ps
        }.toMap
      } finally {
        source.close()
      }
    }
  }
}
