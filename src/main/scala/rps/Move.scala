package rps

sealed trait Move { def name: String }
case object Rock extends Move { val name = "Rock" }
case object Paper extends Move { val name = "Paper" }
case object Scissors extends Move { val name = "Scissors" }
case object Lizard extends Move { val name = "Lizard" }
case object Spock extends Move { val name = "Spock" }

object Move {
  val classicMoves = List(Rock, Paper, Scissors)
  val extendedMoves = List(Rock, Paper, Scissors, Lizard, Spock)

  def fromInput(input: String, moves: List[Move]): Option[Move] = input.toLowerCase match {
    case "rock" | "r"     => Some(Rock)
    case "paper" | "p"    => Some(Paper)
    case "scissors" | "s" => Some(Scissors)
    case "lizard" | "l"   => moves.find(_ == Lizard)
    case "spock" | "k"    => moves.find(_ == Spock)
    case _                => None
  }
}
