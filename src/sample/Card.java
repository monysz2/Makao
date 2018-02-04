package sample;

public class Card {
    private int value;
    private String color;
    private String fileName;

    public Card(int val, String col)
    {
        this.value = val;
        this.color = col;
        this.fileName = Integer.toString(val)+"_"+col+".png";
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public int getValue()
    {
        return this.value;
    }

    public String getColor()
    {
        return this.color;
    }

    public void changeColor(String col)
    {
        this.color = col;
    }

    public void changeValue(int val)
    {
        this.value = val;
    }
}
