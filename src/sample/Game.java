package sample;

import javafx.application.Platform;

import java.io.IOException;
import java.util.*;

public class Game extends Thread{
    private ArrayList<Player> players = new ArrayList<>();
    private LinkedList<Card> setOfCards = new LinkedList<>();
    private Player activePlayer;
    private int numberOfPlayers;
    private int numberOfCardsToTake = 0;
    private int numberOfTurnsToWait = 0;
    private static final int MAX_PLAYERS = 2;
    private String[] colors = {"hearts","spades","diamonds","clubs"};
    Card table = null;
    static final List<Integer> battleCards = Arrays.asList(2,3,13);
    static final List<Integer> actionCards = Arrays.asList(1,4,11);
    boolean battle = false ;
    boolean turns = false;
    boolean jack = false;
    boolean ace = false;


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


    }
    boolean isFull()
    {
        if(numberOfPlayers==MAX_PLAYERS)
            return true;
        else
            return false;

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


    void makao(Player player)
    {
        System.out.println("Cards in set: "+player.getSetOfCards().size());
        if(this.players.get(players.indexOf(player)).getSizeOfSetOfCards()==1)
        {
            sendMessageToPlayer("100",player);
            sendMessageToPlayer("101",players.get(getIndex(this.activePlayer,1)));
        }else if(this.players.get(players.indexOf(player)).getSizeOfSetOfCards()==0)
        {
            sendMessageToPlayer("200",player);
            sendMessageToPlayer("201",players.get(getIndex(this.activePlayer,1)));
            try {
                closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    void closeConnection() throws IOException {
        for(Player player : this.players)
        {
            player.getUserMenu().closeConnection();
            player.closeSocket();
        }
    }

    void sendInfo()
    {
        makao(activePlayer);
        sendMessageToAll("4");
        sendMessageToAll(table.getFileName());

    }


    void sendOpponentsCards()
    {
        sendMessageToAll("9");
        sendMessageToPlayer(Integer.toString(players.get(getIndex(activePlayer,1)).getSizeOfSetOfCards()),activePlayer);
        sendMessageToPlayer(Integer.toString(activePlayer.getSizeOfSetOfCards()),players.get(getIndex(activePlayer,1)));
    }
   public void run()
   {
       System.out.println("Size: "+players.size());
       activePlayer = players.get(0);
       for(int i=0;i<5;i++)
       {
           for(Player player : players)
           {
               System.out.println("Card send");
               sendCards(player);

           }
       }
       sendMessageToAll("4");
       sendMessageToAll(table.getFileName());
       sendMessageToAll("5");
       activePlayer.getUserMenu().sendToGame("1");
       players.get(1).getUserMenu().sendToGame("2");
       while(isLive())
       {
            sendOpponentsCards();
           if(activePlayer.getNumberOfTurnsToWait()!=0)
           {
               sendMessageToPlayer("2",activePlayer);
               activePlayer.setNumberOfTurnsToWait(activePlayer.getNumberOfTurnsToWait()-1);
               changeToken();
           }else {
               sendMessageToPlayer("20", activePlayer);
               try {
                   int activity = activePlayer.getUserMenu().getLine();
                   if (activity == 1) {
                       activePlayer.strike = 0;
                       Card toPut = activePlayer.getUserMenu().getCard();
                       System.out.println("Card received");
                       if (toPut.getValue() == 1) {
                           sendMessageToPlayer("30", activePlayer);

                           if (putCardOnTable(toPut, activePlayer, activePlayer.getUserMenu().getColor())) {
                               sendInfo();

                           } else
                               sendMessageToPlayer("-20", activePlayer);

                           sendMessageToAll("5");
                           sendMessageToAll(table.getColor());
                       } else {
                           if (putCardOnTable(toPut, activePlayer)) {
                               sendInfo();

                           } else
                               sendMessageToPlayer("-20", activePlayer);

                       }
                   } else {
                       System.out.println("sending card");
                       if (activePlayer.strike == 0) {
                           activePlayer.strike = 1;
                           sendCard(activePlayer);

                       } else
                           activePlayer.strike = 0;


                   }


               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
   }


    void sendCard(Player player) {
        Card toAdd = this.giveCard();
        player.getSetOfCards().add(toAdd);
        sendMessageToPlayer("10", player);
        sendMessageToPlayer(toAdd.getFileName(), player);
        if (battleCards.contains(table.getValue()) || actionCards.contains(table.getValue())  ) {

            if ((toAdd.getValue() != table.getValue()) && (battle || turns || ace)) {
                System.out.println("True");
                if(table.getValue()==4)
                {
                        sendTurnsAfterBattle(activePlayer);
                        numberOfTurnsToWait=0;
                        changeToken();
                }
                else if(numberOfCardsToTake!=0) {
                    sendCardsAfterBattle(activePlayer);
                    numberOfCardsToTake = 0;
                }else
                {

                    activePlayer.strike=0;
                    changeToken();

                }

            }else
            {
                activePlayer.strike=0;
                changeToken();
            }
        }else if(!(toAdd.getColor().equals(table.getColor())) )
        {
            if(toAdd.getValue()!=table.getValue())
            {
                activePlayer.strike=0;
                changeToken();
            }

        }

    }

    void changeToken()
    {
        activePlayer.setState(1);
        sendMessageToPlayer("2",activePlayer);
        activePlayer = players.get(getIndex(activePlayer,1));
        activePlayer.setState(2);
        sendMessageToPlayer("1",activePlayer);

    }

   void sendMessageToAll(String message)
   {
       for(Player player : players)
       {
           player.getUserMenu().sendToGame(message);
       }
   }
   void sendCards(Player player)
   {
       Card toAdd = this.giveCard();
       player.getSetOfCards().add(toAdd);
       sendMessageToPlayer("10", player);
       sendMessageToPlayer(toAdd.getFileName(), player);

   }
   void sendMessageToPlayer(String message, Player player)
   {
       player.getUserMenu().sendToGame(message);
   }
   boolean isLive()
   {
       for(Player pl : players) {
           if (pl.getSizeOfSetOfCards() == 0)
               return false;
       }
       return true;
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
    void sendTurnsAfterBattle (Player player)
    {
        sendMessageToPlayer("70",player);
        sendMessageToPlayer(Integer.toString(numberOfTurnsToWait),player);
        player.setNumberOfTurnsToWait(numberOfTurnsToWait);
        turns=false;
    }
    void sendCardsAfterBattle(Player player)
    {
        for(int i=0; i<numberOfCardsToTake-1;i++)
        {
            Card toAdd = this.giveCard();
            player.getSetOfCards().add(toAdd);
            sendMessageToPlayer("10",player);
            sendMessageToPlayer(toAdd.getFileName(),player);
        }
        activePlayer.setState(1);

        sendMessageToPlayer("2",activePlayer);
        activePlayer = players.get(getIndex(player,1));
        activePlayer.setState(2);
        sendMessageToPlayer("1",activePlayer);
        battle = false;

    }
    boolean putCardOnTable(Card toPut, Player player, String...color) throws IOException {

        if ((table == null) || (toPut.getColor().equals(table.getColor())) || toPut.getValue()==table.getValue()) {
            if (!battleCards.contains(toPut.getValue()) && battle) {
                if (toPut.getValue() != table.getValue()) {
                    return false;
                }
            }else if(!actionCards.contains(toPut.getValue()) && turns) {
                if (toPut.getValue() != 4) {
                    return false;
                }
            }else if(jack)
            {
                if(table.getValue()==toPut.getValue() || toPut.getValue()==11)
                {
                    table = toPut;
                    changeToken();
                    jack = false;
                    turns=false;
                    battle=false;
                    this.players.get(this.players.indexOf(activePlayer)).getSetOfCards().removeLast();
                    return true;
                }
                return false;
            }else if(ace)
            {
                if(table.getColor().equals(toPut.getColor()) || toPut.getValue()==1)
                {
                    table = toPut;
                    changeToken();
                    ace = false;
                    battle = false;
                    jack=false;
                    this.players.get(this.players.indexOf(activePlayer)).getSetOfCards().removeLast();
                    return true;
                }
                return false;
            }
            else {
                table = toPut;
                player.setState(1);
                switch (toPut.getValue()) {
                    case 1:
                        action(toPut, player, color[0]);
                        ace = true;
                        battle = false;
                        jack = false;
                        numberOfCardsToTake = 0;
                        break;
                    case 2:
                        action(toPut, player);
                        battle = true;
                        jack = false;
                        ace = false;
                        break;
                    case 3:
                        action(toPut, player);
                        battle = true;
                        jack = false;
                        ace = false;
                        break;
                    case 4:
                        System.out.println("4CARD");
                        action(toPut, player);
                        turns = true;
                        battle=false;
                        ace=false;
                        numberOfCardsToTake = 0;
                        break;
                    case 11:
                        sendMessageToPlayer("40", activePlayer);
                        action(toPut, player);
                        jack = true;
                        ace=false;
                        battle=false;
                        numberOfCardsToTake = 0;
                        break;
                    case 13:
                        if ((toPut.getColor().equals("hearts")) || (toPut.getColor().equals("spades"))) {
                            action(toPut, player);
                            battle = true;
                            jack = false;
                            ace = false;
                            break;
                        } else
                            break;
                    default:
                        break;
                }
                this.players.get(this.players.indexOf(activePlayer)).getSetOfCards().removeLast();
                sendMessageToPlayer("2", player);
                player.setState(1);
                activePlayer = players.get(getIndex(player, 1));
                activePlayer.setState(2);

                return true;
            }
        }

        return false;
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

    void action(Card card, Player player, String...color) throws IOException {
        switch(card.getValue())
        {
            case 1:

                table.changeColor(color[0]);
                break;
            case 2:

                numberOfCardsToTake += card.getValue();
                sendMessageToAll("6");
                sendMessageToAll(Integer.toString(numberOfCardsToTake));
                break;

            case 3:
                numberOfCardsToTake += card.getValue();
                sendMessageToAll("6");
                sendMessageToAll(Integer.toString(numberOfCardsToTake));

                break;
            case 4:
                numberOfTurnsToWait ++;
                sendMessageToAll("7");
                sendMessageToAll(Integer.toString(numberOfTurnsToWait));
                break;
            case 11:
                table.changeValue(Integer.parseInt(activePlayer.getUserMenu().getColor()));
                break;

            case 13:
                numberOfCardsToTake += 5;
                sendMessageToAll("6");
                sendMessageToAll(Integer.toString(numberOfCardsToTake));
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
