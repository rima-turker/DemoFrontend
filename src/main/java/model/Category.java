package model;

public class Category {
    private final int index;
    private final String name;
    private final String nameInTheNewsSite;
    
    public Category(
            int index,
            String name,
            String nameInTheNewsSite)
    {
        this.index = index;
        this.name = name;
        this.nameInTheNewsSite = nameInTheNewsSite;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getNameInTheNewsSite() {
        return nameInTheNewsSite;
    }
    
    
}
