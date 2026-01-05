package rps

import scala.io.StdIn.readLine

object Menu {
  var lastP1: Option[Player] = None
  var lastP2: Option[Player] = None
  var lastMoves: List[Move] = Nil
  var lastFormat: String = "single"
  var lastTarget: Int = 1
 

  def mainMenu(): Unit = {
    println(Console.GREEN + """
    ==============================
        ROCK-PAPER-SCISSORS
    ==============================
    1. Start New Game
    2. Continue Saved Game
    3. View Scoreboard
    4. Exit
    """ + Console.RESET)

    readLine("Choose an option: ") match {
      case "1" => startNewGame()
      case "2" => continueGame()
      case "3" => Scoreboard.show(); mainMenu()
      case "4" => 
        // Save stats on exit
        Scoreboard.saveStats()
        println("Goodbye!")
      case _   => println("Invalid choice."); mainMenu()
    }
  }

  def continueGame(): Unit = {
    if (!SaveLoad.hasSavedGame()) {
      println(Console.RED + "No saved game found. Starting new game." + Console.RESET)
      startNewGame()
    } else {
      SaveLoad.loadGame() match {
        case scala.util.Success(state) =>
          println(Console.GREEN + "Loaded saved game!" + Console.RESET)
          println(s"Resuming: ${state.p1Name} vs ${state.p2Name}")
          println(s"Current score: ${state.p1Name} ${state.currentP1Wins} - ${state.p2Name} ${state.currentP2Wins}")
          
          // Reconstruct Player objects from state
          val player1 = Player(state.p1Name, state.p1IsAI, state.p1Difficulty)
          val player2 = Player(state.p2Name, state.p2IsAI, state.p2Difficulty)
          
          // Reconstruct moves list from ruleset
          val moves = if (state.ruleset == "classic") Move.classicMoves else Move.extendedMoves
          
          // Save state for potential rematch
          lastP1 = Some(player1)
          lastP2 = Some(player2)
          lastMoves = moves
          lastFormat = state.matchFormat
          lastTarget = state.targetRounds
          
          // Resume the match with current scores
          Game.playMatch(player1, player2, moves, state.matchFormat, state.targetRounds, state.currentP1Wins, state.currentP2Wins)
          
          // Delete save file after successful load
          SaveLoad.deleteSaveFile()
          
          // Post-game options
          println("Options: (r) Rematch, (m) Menu, (e) Exit")
          readLine().toLowerCase match {
            case "r" => rematch()
            case "m" => mainMenu()
            case "e" => println("Goodbye!")
            case _   => mainMenu()
          }
        case scala.util.Failure(e) =>
          println(Console.RED + s"Failed to load game: ${e.getMessage}" + Console.RESET)
          mainMenu()
      }
    }
  }

  def startNewGame(): Unit = {
    val p1Name = readLine("Enter Player 1 name: ")
    val mode = readLine("Single Player (s) or Multiplayer (m)? ").toLowerCase

    val player1 = Player(p1Name)
    val player2 = if (mode == "s") {
      val diff = readLine("Choose AI difficulty (Easy/Normal/Hard): ")
      Player("Computer", isAI = true, difficulty = diff.capitalize)
    } else {
      val p2Name = readLine("Enter Player 2 name: ")
      Player(p2Name)
    }

    val ruleset = readLine("Classic (c) or Extended (e)? ").toLowerCase
    val moves = if (ruleset == "c") Move.classicMoves else Move.extendedMoves

    println("Choose match format:\n1. Single Round\n2. Best of N\n3. First to K Wins")
     readLine("Option: ") match {
      case "1" =>
        lastFormat = "single"; lastTarget = 1
        Game.playMatch(player1, player2, moves, format = "single")
      case "2" =>
        val n = readLine("Enter odd N (3,5,7): ").toInt
        lastFormat = "bestOf"; lastTarget = n
        Game.playMatch(player1, player2, moves, format = "bestOf", n)
      case "3" =>
        val k = readLine("Enter K (wins needed): ").toInt
        lastFormat = "firstTo"; lastTarget = k
        Game.playMatch(player1, player2, moves, format = "firstTo", k)
      case _ =>
        println("Invalid format."); startNewGame()
    }


    // Save state for rematch
    lastP1 = Some(player1)
    lastP2 = Some(player2)
    lastMoves = moves


    println("Options: (r) Rematch, (m) Menu, (e) Exit")
    readLine().toLowerCase match {
      case "r" => rematch()
      case "m" => mainMenu()
      case "e" => println("Goodbye!")
      case _   => mainMenu()
    }
  }

  def rematch(): Unit = {
    (lastP1, lastP2) match {
      case (Some(p1), Some(p2)) =>
        Game.playMatch(p1, p2, lastMoves, lastFormat, lastTarget)
        println("Options: (r) Rematch, (m) Menu, (e) Exit")
        readLine().toLowerCase match {
          case "r" => rematch()
          case "m" => mainMenu()
          case "e" => println("Goodbye!")
          case _   => mainMenu()
        }
      case _ =>
        println("No previous game found. Start a new game first.")
        mainMenu()
    }
  }

}