package mavmi.telegram_bot.crv_bot.user;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class Users {
    private Map<Long, User> users = new HashMap<>();

    public void add(User user){
        users.put(user.getId(), user);
    }
    public User get(long id){
        return users.get(id);
    }
}
