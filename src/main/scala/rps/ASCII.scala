package rps

object ASCIIArt {
  def art(move: Move): String = move match {
    case Rock =>
      Console.WHITE + """
       (_____)
       (_____)
      |       |
      | ROCK  |
      |_______|
       (_____)
       (_____)
      """ + Console.RESET

    case Paper =>
      Console.WHITE + """
       _______
      |       |
      | PAPER |
      |_______|
       _______
      |       |
      |       |
      |       |
      |_______|
      """ + Console.RESET

    case Scissors =>
      Console.RED + """
       _______
      |       |
      |SCISSOR|
      |_______|
        \   /
         \ /
          X
         / \
        /   \
      """ + Console.RESET

    case Lizard =>
      Console.GREEN + """
       _______
      |       |
      |LIZARD |
      |_______|
         /^\
        (o.o)
        > ^ <
      """ + Console.RESET

    case Spock =>
      Console.BLUE + """
       _______
      |       |
      | SPOCK |
      |_______|
        \   /
         \ /
          |
          |
      """ + Console.RESET
  }
}
