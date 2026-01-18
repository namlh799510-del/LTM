package game;

public class GameLogic {
    public enum Choice {ROCK, PAPER, SCISSORS}

    public static String determineWinner(Choice p1, Choice p2) {
        if (p1 == p2) return "DRAW";
        if ((p1 == Choice.ROCK && p2 == Choice.SCISSORS)
                || (p1 == Choice.PAPER && p2 == Choice.ROCK)
                || (p1 == Choice.SCISSORS && p2 == Choice.PAPER)) {
            return "PLAYER1_WINS";
        }
        return "PLAYER2_WINS";
    }

    public static boolean isValidChoice(String input) {
        try {
            Choice.valueOf(input.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
