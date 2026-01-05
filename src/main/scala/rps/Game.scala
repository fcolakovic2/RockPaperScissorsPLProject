package rps

import scala.io.StdIn.readLine

object Game {

  def playMatch(p1: Player, p2: Player, moves: List[Move], format: String, target: Int = 1, initialP1Wins: Int = 0, initialP2Wins: Int = 0): Unit = {
    var p1Wins = initialP1Wins
    var p2Wins = initialP2Wins
    var humanMoveHistory: List[Move] = List()

    def matchOver: Boolean = format match {
      case "single"  => p1Wins + p2Wins >= 1
      case "bestOf"  => p1Wins > target / 2 || p2Wins > target / 2
      case "firstTo" => p1Wins >= target || p2Wins >= target
    }

    def remainingInfo: String = format match {
      case "single"  => ""
      case "bestOf"  => s" | Remaining: ${target - (p1Wins + p2Wins)} rounds"
      case "firstTo" => s" | First to: $target wins"
    }

    while (!matchOver) {
      val (m1, m2) =
        if (!p2.isAI) playMultiplayerRound(p1, p2, moves)
        else {
          val humanMove = getMove(p1, moves)
          humanMoveHistory = humanMoveHistory :+ humanMove
          val aiMove = AI.aiMove(p2, moves, humanMoveHistory)
          (humanMove, aiMove)
        }

      println(s"${p1.name} chose ${m1.name}:\n" + ASCIIArt.art(m1))
      println(s"${p2.name} chose ${m2.name}:\n" + ASCIIArt.art(m2))

      val winner = determineWinner(m1, m2, p1, p2)
      winner match {
        case Some(`p1`) => println(Console.BLUE + s"${p1.name} wins this round!" + Console.RESET); p1Wins += 1
        case Some(`p2`) => println(Console.RED + s"${p2.name} wins this round!" + Console.RESET); p2Wins += 1
        case None       => println(Console.CYAN + "It's a tie!" + Console.RESET)
      }
      println(s"Score: ${p1.name} $p1Wins - ${p2.name} $p2Wins$remainingInfo\n")
      
      // Offer save option after each round (if match not over)
      if (!matchOver) {
        val saveChoice = readLine("Save and quit? (y/n): ").toLowerCase
        if (saveChoice == "y" || saveChoice == "yes") {
          val ruleset = if (moves == Move.classicMoves) "classic" else "extended"
          val state = GameState(
            p1.name, p1.isAI, p1.difficulty,
            p2.name, p2.isAI, p2.difficulty,
            ruleset, format, target,
            p1Wins, p2Wins, p1Wins + p2Wins + 1
          )
          SaveLoad.saveGame(state)
          return  // Exit the match
        }
      }
    }

    val matchWinner = if (p1Wins > p2Wins) p1 else p2
    println(Console.GREEN + s"${matchWinner.name} wins the match!" + Console.RESET)
    Scoreboard.updateMatchStats(p1.name, p1Wins, p2.name, p2Wins)
    Scoreboard.saveStats()  // Auto-save stats after each match
  }

  def playMultiplayerRound(p1: Player, p2: Player, moves: List[Move]): (Move, Move) = {
  // Player 1 input
  val m1 = getMove(p1, moves)

  // Clear screen AFTER Player 1 finishes
  clearScreen()

  // Explicitly print Player 2’s prompt here
  val m2 = getMove(p2, moves)

  (m1, m2)
}

def getMove(player: Player, moves: List[Move]): Move = {
  val input = readLine(s"${player.name}, enter your move: ")
  Move.fromInput(input, moves).getOrElse {
    println("Invalid move."); getMove(player, moves)
  }
}

  def determineWinner(m1: Move, m2: Move, p1: Player, p2: Player): Option[Player] = {
    if (m1 == m2) None
    else {
      val winMap: Map[Move, List[Move]] = Map(
        Rock -> List(Scissors, Lizard),
        Paper -> List(Rock, Spock),
        Scissors -> List(Paper, Lizard),
        Lizard -> List(Spock, Paper),
        Spock -> List(Scissors, Rock)
      )
      if (winMap(m1).contains(m2)) Some(p1) else Some(p2)
    }
  }

  def clearScreen(): Unit = {
    println("\n" * 50)
    println("=== Next Player ===\n")
  }


}