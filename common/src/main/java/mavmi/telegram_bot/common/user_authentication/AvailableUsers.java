package mavmi.telegram_bot.common.user_authentication;

import java.util.List;
import java.util.ArrayList;

public class AvailableUsers {
    private final List<UserInfo> availableUsers;

    public AvailableUsers(){
        availableUsers = new ArrayList<>();
    }

    public void addUser(UserInfo userInfo){
        availableUsers.add(userInfo);
    }
    public UserInfo get(int pos){
        return availableUsers.get(pos);
    }

    public boolean isUserAvailable(UserInfo userInfo){
        for (UserInfo availableUser : availableUsers){
            if (availableUser.equals(userInfo)) return true;
        }

        return false;
    }

}
