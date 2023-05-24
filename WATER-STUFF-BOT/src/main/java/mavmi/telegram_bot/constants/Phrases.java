package mavmi.telegram_bot.constants;

public class Phrases {
    public static final String GREETINGS_MSG = "Здравствуйте.";
    public static final String SUCCESS_MSG = "Потрясающе.";
    public static final String ERROR_MSG = "Я не вдупляю.";
    public static final String ON_EMPTY_MSG = "Список растений пустой.";

    public static final String APPROVE_MSG = "Продолжить?";
    public static final String ADD_GROUP_MSG = "Введи название группы и разницу между днями полива.\n" +
                                                            "[НАЗВАНИЕ];[ЧИСЛО]";
    public static final String ENTER_GROUP_NAME_MSG = "Введи название группы.";
    public static final String ENTER_GROUP_DATA_MSG = "Введи новые данные в формате:\n" +
                                                            "[НАЗВАНИЕ]\n" +
                                                            "[РАЗНИЦА ПО ДНЯМ]\n" +
                                                            "[ДАТА ПОЛИВА] или null\n" +
                                                            "[ДАТА УДОБРЕНИЯ] или null";

    public static final String INVALID_GROUP_NAME_FORMAT_MSG = "Невалидный формат данных.";
    public static final String INVALID_GROUP_NAME_MSG = "Группы с таким названием нет.";
    public static final String OPERATION_CANCELED_MSG = "Галя, отмена.";

}
