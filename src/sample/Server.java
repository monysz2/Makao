package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {
    static LinkedList<Game> games = new LinkedList<>();
    static LinkedList<Player> players = new LinkedList<>();

    public Server() throws IOException {

    }

    public static void main(String args[]) throws IOException {
        {
            ServerSocket serverSocket=new ServerSocket(6666);
            Game gameToAdd = new Game();
            games.add(gameToAdd);
            int active = 2;
            while(true)
            {
                if(gameToAdd.isFull())
                {

                    gameToAdd = new Game();
                    games.add(gameToAdd);
                    active = 2;
                }
                Socket player = serverSocket.accept();
                Player playerToAdd = new Player(player,gameToAdd,active);
                active=1;
                gameToAdd.addPlayer(playerToAdd);
                players.add(playerToAdd);
                playerToAdd.start();
            }
        }


    }


}
