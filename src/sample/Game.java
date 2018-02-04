package sample;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Game {
    private ArrayList<Player> players = new ArrayList<>();
    private LinkedList<Card> setOfCards = new LinkedList<>();
    private int numberOfPlayers;
    private int numberOfCardsToTake = 0;
    private int numberOfTurnsToWait = 0;
    private boolean isFull;
    private static final int MAX_PLAYERS = 2;
    private String[] colors = {"hearts","spades","diamonds","clubs"};
    Card table = null;

    Player getPlayers()
    {
        Player playerToReturn = null;
        for(Player player : players)
        {
            if(player.getStatus()==2)
            {
                playerToReturn = player;
                break;
            }
        }
        return playerToReturn;
    }

    public Game()
    {
        numberOfPlayers = 0;
        for(String color : colors)
        {
            for(int val=1; val<14; val++)
            {
                Card toAdd = new Card(val, color);
                setOfCards.add(toAdd);
            }
        }
        table = this.giveCard();
        this.isFull = false;

    }
    boolean isFull()
    {
        return this.isFull;
    }

    int getMaxPlayers()
    {
        return this.MAX_PLAYERS;
    }

    int getNumberOfPlayers()
    {
        return this.numberOfPlayers;
    }
    boolean addPlayer(Player toAdd)
    {
        numberOfPlayers++;
        if(numberOfPlayers>MAX_PLAYERS)
        {
            numberOfPlayers--;
            return false;
        }else {
            players.add(toAdd);
            return true;
        }

    }

    boolean startGame()
    {
        int playersReadyToStart = 0;
        for(Player player : players)
        {
            if(player.getStatus()==3)
            {
                playersReadyToStart++;
            }
        }
        if(playersReadyToStart==getMaxPlayers())
        {
            /*for(Player player : players)
            {
                player.getUserMenu().sendToGame("start");
        }*/
        return true;
        }else
            return false;
    }

    Card giveCard()
    {
        if(setOfCards.size()==0)
        {
            generateSet();
        }
        Random randomCard = new Random();
        Card toGive = setOfCards.get(randomCard.nextInt(setOfCards.size()));
        setOfCards.remove(toGive);
        return toGive;
    }

    boolean putCardOnTable(Card toPut, Player player, String...color)
    {
        if((table==null)||(toPut.getColor().equals(table.getColor()))||(toPut.getValue()==table.getValue()))
        {
            table = toPut;
            player.setState(1);
            switch (toPut.getValue())
            {
                case 1:
                    action(toPut,player,color[0]);
                    break;
                case 2:
                    action(toPut,player);
                    break;
                case 3:
                    action(toPut,player);
                    break;
                case 4:
                    action(toPut,player);
                    break;
                case 11:
                    action(toPut,player);
                    break;
                case 13:
                    if((toPut.getColor()=="hearts")||(toPut.getColor()=="spades"))
                    {
                        action(toPut,player);
                        break;
                    }
                    else
                        break;
                default:
                    break;
            }
            player.getUserMenu().sendToGame("1");
            for(Player pla : players)
            {
                pla.getUserMenu().sendToGame(toPut.getFileName());
            }
            player.setState(1);
            players.get(getIndex(player,1)).setState(2);
            return true;
        }else
        {
            player.setState(1);
            players.get(getIndex(player,1)).setState(2);
            return false;
        }


    }

    void generateSet()
    {
        for(int i=0; i<4; i++)
        {
            String col = new String("hearts");
            for(int val=1; val<14; val++)
            {
                if((table.getColor()==col) && (table.getValue()==val))
                {

                }else
                {
                    Card toAdd = new Card(val, col);
                    setOfCards.add(toAdd);
                }

            }
        }
    }

    void action(Card card, Player player, String...color)
    {
        switch(card.getValue())
        {
            case 1:
                //send info to player about color
                table.changeColor(color[0]);
                break;
            case 2:

                numberOfCardsToTake += card.getValue();
                if(player.getNumberOfCardsToTake()!=0)
                {
                    player.setNumberOfCardsToTake(0);
                }
                players.get(getIndex(player,1)).setNumberOfCardsToTake(numberOfCardsToTake);
                numberOfCardsToTake=0;
                break;
            case 3:
                numberOfCardsToTake += card.getValue();
                if(player.getNumberOfCardsToTake()!=0)
                {
                    player.setNumberOfCardsToTake(0);
                }
                players.get(getIndex(player,1)).setNumberOfCardsToTake(numberOfCardsToTake);
                numberOfCardsToTake=0;
                break;
            case 4:
                numberOfTurnsToWait += card.getValue();
                if(player.getNumberOfTurnsToWait()!=0)
                {
                    player.setNumberOfTurnsToWait(0);
                }
                players.get(getIndex(player,1)).setNumberOfTurnsToWait(numberOfTurnsToWait);
                numberOfTurnsToWait=0;
                break;
            case 11:
                table.changeValue(card.getValue());
                break;

            case 13:
                numberOfCardsToTake += 5;
                if(player.getNumberOfCardsToTake()!=0)
                {
                    player.setNumberOfCardsToTake(0);
                }
                if(card.getColor()=="hearts")
                {
                    players.get(getIndex(player,1)).setNumberOfCardsToTake(numberOfCardsToTake);
                }else {
                    players.get(getIndex(player, -1)).setNumberOfCardsToTake(numberOfCardsToTake);
                }
                numberOfCardsToTake=0;
                break;

        }
    }

    int getIndex(Player player, int number)
    {
        int index = players.indexOf(player);
        int nextPlayer = index+1;
        int allPlayers = players.size();
        allPlayers--;
        if(nextPlayer>allPlayers)
        {
            return 0;
        }else if(nextPlayer<0)
        {
            return allPlayers;
        }
        else return nextPlayer;
    }

}
