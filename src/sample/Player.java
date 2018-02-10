package sample;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class Player{
    private Socket playerSocket;
    private Menu userMenu;
    private Game game;
    private LinkedList<Card> setOfCards = new LinkedList<>();
    private int numberOfTurnsToWait = 0;
    private int state;
    int strike = 0;


    public Player(Socket socket, Game game, int state) throws IOException {
        this.playerSocket = socket;
        this.userMenu = new Menu(this.playerSocket);
        this.game = game;
        this.state = state;

    }
   void closeSocket() throws IOException {
       this.playerSocket.close();
   }
    LinkedList<Card> getSetOfCards()
    {
        return this.setOfCards;
    }
    Menu getUserMenu()
    {
        return this.userMenu;
    }
    int getSizeOfSetOfCards()
    {
        return setOfCards.size();
    }
    public void run()
    {

        while(!this.playerSocket.isClosed())
        {

            try {
                switch(userMenu.getLine())
                {

                    case 1:

                            setOfCards.add(this.game.giveCard());
                            this.userMenu.sendToGame(setOfCards.getLast().getFileName());

                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    int getStatus()
    {
        return this.state;
    }

    public void setNumberOfTurnsToWait(int turnsToWait)
    {
        if(turnsToWait==0)
        {
            this.numberOfTurnsToWait=0;
        }else
        this.numberOfTurnsToWait = turnsToWait;
    }

    int getNumberOfTurnsToWait()
    {
        return numberOfTurnsToWait;
    }

    public void setState(int stat)
    {
        this.state = stat;
    }


}
