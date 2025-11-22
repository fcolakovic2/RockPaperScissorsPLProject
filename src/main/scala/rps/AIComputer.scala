package rps

import scala.util.Random

object AI {
  def aiMove(player: Player, moves: List[Move]): Move = {
    player.difficulty match {
      case "Easy"   => moves(Random.nextInt(moves.size))
      case "Normal" => if (Random.nextDouble() < 0.7) moves(Random.nextInt(moves.size)) else Rock
      case "Hard"   => moves(Random.nextInt(moves.size)) // simplified pattern tracking
      case _        => moves(Random.nextInt(moves.size))
    }
  }
}
