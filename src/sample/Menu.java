package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Menu{
    private Socket playerSocket;
    PrintWriter out;
    BufferedReader inBuff;

    public Menu(Socket player) throws IOException {
        this.playerSocket = player;

        this.out = new PrintWriter(player.getOutputStream(), true);

        this.inBuff = new BufferedReader(new InputStreamReader(player.getInputStream()));
    }

    int getLine() throws IOException {

        while(true)
        {
            int response = Integer.parseInt(this.inBuff.readLine());
            if(response==-1)
            {
                break;
            }else
                return response;
        }
        return -1;
    }

    Card getCard() throws IOException {
        String[] card = this.inBuff.readLine().split("_");
        Card toReturn = new Card(Integer.parseInt(card[0]),card[1].substring(0,card[1].length()-4));
        return toReturn;
    }

    void sendToGame(String toSend)
    {
        out.println(toSend);
    }

}
