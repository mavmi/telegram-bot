package mavmi.telegram_bot.telegram_bot;

import mavmi.telegram_bot.constants.Levels;

public class User {
    private int state;
    private int spamCount;
    private int botDice;
    private int userDice;
    private long id;
    private String username;
    private String firstName;
    private String lastName;

    public User(){
        this.state = Levels.MAIN_LEVEL;
    }

    public User setBotDice(int botDice){
        this.botDice = botDice;
        return this;
    }
    public int getBotDice(){
        return botDice;
    }

    public User setUserDice(int userDice){
        this.userDice = userDice;
        return this;
    }
    public int getUserDice(){
        return userDice;
    }

    public User setId(long id){
        this.id = id;
        return this;
    }
    public long getId(){
        return id;
    }

    public User setState(int state){
        this.state = state;
        return this;
    }
    public int getState(){
        return state;
    }

    public User setSpamCount(int spamCount){
        this.spamCount = spamCount;
        return this;
    }
    public int getSpamCount(){
        return spamCount;
    }

    public User setUsername(String username){
        this.username = username;
        return this;
    }
    public String getUsername(){
        return username;
    }

    public User setFirstName(String firstName){
        this.firstName = firstName;
        return this;
    }
    public String getFirstName(){
        return firstName;
    }

    public User setLastName(String lastName){
        this.lastName = lastName;
        return this;
    }
    public String getLastName(){
        return lastName;
    }

}
