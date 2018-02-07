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
    private int numberOfCardsToTake = 0;
    private int state;
    int strike = 0;


    public Player(Socket socket, Game game, int state) throws IOException {
        this.playerSocket = socket;
        this.userMenu = new Menu(this.playerSocket);
        this.game = game;
        this.state = state;

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
                    //give card
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
