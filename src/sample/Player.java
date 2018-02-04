package sample;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;

public class Player extends Thread{
    private Socket playerSocket;
    private Menu userMenu;
    private Game game;
    private LinkedList<Card> setOfCards = new LinkedList<>();
    private int numberOfTurnsToWait = 0;
    private int numberOfCardsToTake = 0;
    private int state;


    public Player(Socket socket, Game game, int state) throws IOException {
        this.playerSocket = socket;
        this.userMenu = new Menu(this.playerSocket);
        this.game = game;
        this.state = state;

    }

    Menu getUserMenu()
    {
        return this.userMenu;
    }

    public void run()
    {
        this.userMenu.sendToGame(game.table.getFileName());
        this.userMenu.sendToGame(Integer.toString(state));
        while(this.state!=0)
        {

            try {
                switch(userMenu.getLine())
                {
                    case 1:

                            setOfCards.add(this.game.giveCard());
                            this.userMenu.sendToGame(setOfCards.getLast().getFileName());

                        break;
                    case 2:
                        if(this.state==2) {
                            Card card = userMenu.getCard();
                            if(this.game.putCardOnTable(card, this))
                            {
                                this.userMenu.sendToGame("1");
                            }
                            this.setState(1);
                        }else
                            this.userMenu.sendToGame("1");
                        break;
                    case 3:
                        this.setState(3);

                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    Socket getPlayerSocket()
    {
        return this.playerSocket;
    }

    public void addCardToSet(Card cardToAdd)
    {
        setOfCards.add(cardToAdd);
    }

    public Card putCardOnTable(Card cardToPut)
    {
        Card toReturn = setOfCards.get(setOfCards.indexOf(cardToPut));
        setOfCards.remove(toReturn);
        return toReturn;
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

    void setNumberOfCardsToTake(int toTake)
    {
        if(toTake==0)
        {
            numberOfCardsToTake = 0;
        }else
        numberOfCardsToTake+=toTake;
    }

    int getNumberOfCardsToTake()
    {
        return this.numberOfCardsToTake;
    }


}
