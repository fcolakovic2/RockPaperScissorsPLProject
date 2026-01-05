package rps

import scala.util.Random

object AI {
  def aiMove(player: Player, moves: List[Move], humanMoveHistory: List[Move]): Move = {
    player.difficulty match {
      case "Easy"   => moves(Random.nextInt(moves.size))
      case "Normal" => normalAI(moves, humanMoveHistory)
      case "Hard"   => hardAI(moves, humanMoveHistory)
      case _        => moves(Random.nextInt(moves.size))
    }
  }

  private def normalAI(moves: List[Move], history: List[Move]): Move = {
    if (history.isEmpty) {
      // No history yet, play random
      moves(Random.nextInt(moves.size))
    } else {
      // 60% counter last move, 40% random
      if (Random.nextDouble() < 0.6) {
        val lastMove = history.last
        val counters = getCounterMoves(lastMove, moves)
        counters(Random.nextInt(counters.size))
      } else {
        moves(Random.nextInt(moves.size))
      }
    }
  }

  private def hardAI(moves: List[Move], history: List[Move]): Move = {
    if (history.isEmpty) {
      // No history yet, play random
      moves(Random.nextInt(moves.size))
    } else {
      // 70% counter most common move from last 5, 30% random
      if (Random.nextDouble() < 0.7) {
        val recentMoves = history.takeRight(5)
        val mostCommon = recentMoves.groupBy(identity).maxBy(_._2.size)._1
        val counters = getCounterMoves(mostCommon, moves)
        counters(Random.nextInt(counters.size))
      } else {
        moves(Random.nextInt(moves.size))
      }
    }
  }

  private def getCounterMoves(move: Move, availableMoves: List[Move]): List[Move] = {
    val winMap: Map[Move, List[Move]] = Map(
      Rock -> List(Scissors, Lizard),
      Paper -> List(Rock, Spock),
      Scissors -> List(Paper, Lizard),
      Lizard -> List(Spock, Paper),
      Spock -> List(Scissors, Rock)
    )
    // Find moves that beat the given move
    availableMoves.filter(m => winMap.get(m).exists(_.contains(move)))
  }
}
