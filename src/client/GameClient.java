package client;

import game.GameLogic;

import java.util.Scanner;

public class GameClient {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("(ROCK/PAPER/SCISSORS) p1 enter: ");
        GameLogic.Choice p1 = (GameLogic.Choice.valueOf(sc.nextLine()));

        System.out.println("(ROCK/PAPER/SCISSORS) p2 enter: ");
        GameLogic.Choice p2 = (GameLogic.Choice.valueOf(sc.nextLine()));

        String result = GameLogic.determineWinner(p1, p2);

        System.out.println(result);
    }
}
