package mavmi.telegram_bot.monitoring.service.monitoring.serviceModule.common.buttons;

import lombok.Getter;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringConstants;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Component
public class ButtonsContainer {

    private final String[] hostButtons;
    private final String[] appsButtons;
    private final String[] privilegesInitButtons;
    private final String[] privilegesButtons;
    private final String[] privilegesAddButtons;
    private final String[] pmsEditButtons;
    private final String[] botAccessInitButtons;
    private final String[] botAccessButtons;
    private final Map<MonitoringServiceMenu, String[]> menuToButtons;

    public ButtonsContainer(MonitoringConstantsHandler constantsHandler) {
        MonitoringConstants constants = constantsHandler.get();

        this.hostButtons = new String[] {
                constants.getButtons().getServerInfo().getMemoryInfo(),
                constants.getButtons().getServerInfo().getRamInfo(),
                constants.getButtons().getServerInfo().getUsersInfo(),
                constants.getButtons().getServerInfo().getBackup(),
                constants.getButtons().getCommon().getExit()
        };
        this.appsButtons = new String[] {
                constants.getButtons().getApps().getPk(),
                constants.getButtons().getApps().getFp(),
                constants.getButtons().getApps().getGc(),
                constants.getButtons().getCommon().getExit()
        };
        this.privilegesInitButtons = new String[] {
                constants.getButtons().getCommon().getExit()
        };
        this.privilegesButtons = new String[] {
                constants.getButtons().getPrivileges().getInfo(),
                constants.getButtons().getPrivileges().getAddPrivilege(),
                constants.getButtons().getPrivileges().getDeletePrivilege(),
                constants.getButtons().getCommon().getExit()
        };
        this.privilegesAddButtons = Stream.concat(
                Arrays.stream(PRIVILEGE.values()).map(PRIVILEGE::getName),
                Stream.of(constants.getButtons().getCommon().getExit())
        ).toArray(String[]::new);
        this.pmsEditButtons = new String[] {
                constants.getButtons().getPms().getInfo(),
                constants.getButtons().getCommon().getExit()
        };
        this.botAccessInitButtons = new String[] {
                constants.getButtons().getCommon().getExit()
        };
        this.botAccessButtons = new String[] {
                constants.getButtons().getBotAccess().getInfo(),
                constants.getButtons().getBotAccess().getAddWaterStuff(),
                constants.getButtons().getBotAccess().getRevokeWaterStuff(),
                constants.getButtons().getBotAccess().getAddMonitoring(),
                constants.getButtons().getBotAccess().getRevokeMonitoring(),
                constants.getButtons().getCommon().getExit()
        };

        menuToButtons = Map.of(
                MonitoringServiceMenu.HOST, hostButtons,
                MonitoringServiceMenu.APPS, appsButtons,
                MonitoringServiceMenu.PRIVILEGES_INIT, privilegesInitButtons,
                MonitoringServiceMenu.PRIVILEGES, privilegesButtons,
                MonitoringServiceMenu.PRIVILEGES_ADD, privilegesAddButtons,
                MonitoringServiceMenu.PMS_EDIT, pmsEditButtons,
                MonitoringServiceMenu.BOT_ACCESS_INIT, botAccessInitButtons,
                MonitoringServiceMenu.BOT_ACCESS, botAccessButtons
        );
    }

    public String[] getButtons(MonitoringServiceMenu menu) {
        return menuToButtons.get(menu);
    }
}
