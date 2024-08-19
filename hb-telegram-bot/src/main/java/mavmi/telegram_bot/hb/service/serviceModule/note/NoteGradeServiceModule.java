package mavmi.telegram_bot.hb.service.serviceModule.note;

import mavmi.telegram_bot.common.database.model.HbNoteModel;
import mavmi.telegram_bot.common.database.model.HbParticipantModel;
import mavmi.telegram_bot.common.database.model.HbScoreModel;
import mavmi.telegram_bot.common.database.repository.HbNoteRepository;
import mavmi.telegram_bot.common.database.repository.HbParticipantRepository;
import mavmi.telegram_bot.common.database.repository.HbScoreRepository;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.HB_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.hb.cache.HbDataCache;
import mavmi.telegram_bot.hb.service.container.HbServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;
import mavmi.telegram_bot.hb.service.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.hb.sheets.GoogleSheetsHandler;
import mavmi.telegram_bot.hb.sheets.dto.CellPosition;
import mavmi.telegram_bot.hb.sheets.dto.Event;
import mavmi.telegram_bot.hb.sheets.dto.Events;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class NoteGradeServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public NoteGradeServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.container = new HbServiceMessageToServiceMethodContainer(this::onDefault);
    }

    @Override
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<HbServiceResponse, HbServiceRequest> method = container.getMethod(msg);
        return method.process(request);
    }

    private HbServiceResponse onDefault(HbServiceRequest request) {
        HbDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);
        HbNoteRepository noteRepository = commonServiceModule.getNoteRepository();
        HbScoreRepository scoreRepository = commonServiceModule.getScoreRepository();

        String peerUsername = dataCache.getHbSelectedUsername().toLowerCase();
        String eventName = dataCache.getHbSelectedEvent();
        String gradeStr = request.getMessageJson().getTextMessage();

        // На случай, если чел решил вернуться в предыдущую менюшку
        Events events = commonServiceModule.getGoogleSheetsHandler().getAllEvents();
        if (gradeStr.equals(commonServiceModule.getConstants().getButtons().getMenuBack())) {
            commonServiceModule.previousMenu();

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getInvalidEventName())
                    .build();
            ReplyKeyboardJson replyKeyboardJson = ReplyKeyboardJson
                    .builder()
                    .keyboardButtons(commonServiceModule.prepareEventsKeyboardButtons(events.getEventsByGroupName(dataCache.getHbSelectedEventGroup())))
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_KEYBOARD)
                    .messageJson(messageJson)
                    .replyKeyboardJson(replyKeyboardJson)
                    .build();
        }
        // Если грейд "присутствовал", то хуячим, чтобы было красиво
        if (gradeStr.equals(commonServiceModule.getConstants().getButtons().getGrade()[0])) {
            gradeStr = "3";
        }
        // Это если грейд - хуйня
        if (!verifyGrade(gradeStr)) {
            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getInvalidGrade())
                    .build();
            ReplyKeyboardJson replyKeyboardJson = ReplyKeyboardJson
                    .builder()
                    .keyboardButtons(commonServiceModule.prepareGradesKeyboard())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_KEYBOARD)
                    .messageJson(messageJson)
                    .replyKeyboardJson(replyKeyboardJson)
                    .build();
        }

        Event event = events.getEventByName(eventName);
        long grade = Long.parseLong(gradeStr);
        double gradeWithFactor = ((double) grade) * event.getFactor();
        double resultGradeWithFactor;
        long peersRow = getUsersSheetRow(peerUsername);
        List<Character> eventsColumn = getEventsColumn(eventName);
        CellPosition cellPosition = new CellPosition(eventsColumn, peersRow);

        // Чекаем, есть ли челик в хб_ноуте
        // Или делаем новую запись, или апдейт имеющейся
        Optional<HbNoteModel> hbNoteModelOptional = noteRepository.findByEduUsernameAndEventName(peerUsername, eventName);
        if (hbNoteModelOptional.isPresent()) {
            HbNoteModel noteModel = hbNoteModelOptional.get();
            resultGradeWithFactor = gradeWithFactor - noteModel.getGradeWithFactor();
            noteRepository.updateById(
                    noteModel.getId(),
                    peerUsername,
                    eventName,
                    grade,
                    gradeWithFactor,
                    cellPosition.toString()
            );
        } else {
            resultGradeWithFactor = gradeWithFactor;
            HbNoteModel noteModel = HbNoteModel
                    .builder()
                    .eduUsername(peerUsername)
                    .eventName(eventName)
                    .grade(grade)
                    .gradeWithFactor(gradeWithFactor)
                    .cellPosition(cellPosition.toString())
                    .build();
            noteRepository.save(noteModel);
        }

        // Тут идет апдейт тотал скора
        Optional<HbScoreModel> hbScoreModelOptional = scoreRepository.findByEduUsername(peerUsername);
        if (hbScoreModelOptional.isPresent()) {
            scoreRepository.updateScoreByEduUsername(peerUsername, hbScoreModelOptional.get().getScore() + resultGradeWithFactor);
        } else {
            HbScoreModel scoreModel = HbScoreModel
                    .builder()
                    .eduUsername(peerUsername)
                    .score(resultGradeWithFactor)
                    .fortune(0L)
                    .build();
            scoreRepository.save(scoreModel);
        }

        commonServiceModule.dropMenu();
        commonServiceModule.getGoogleSheetsHandler().writeCell(gradeStr, cellPosition);

        String outputMsg = "***" + commonServiceModule.getConstants().getPhrases().getSavePeerDataSuccess() + "*** " + "\n\n" +
                "***Пир:*** " + peerUsername + "\n" +
                "***Ивент:*** " + eventName + "\n" +
                "***Оценка:*** " + request.getMessageJson().getTextMessage();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(outputMsg)
                .build();
        return HbServiceResponse
                .builder()
                .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    private boolean verifyGrade(String gradeStr) {
        try {
            int grade = Integer.parseInt(gradeStr);
            return 0 <= grade && grade <= 5;
        } catch (Exception e) {
            return false;
        }
    }

    private long getUsersSheetRow(String eduUsername) {
        HbParticipantRepository participantRepository = commonServiceModule.getParticipantRepository();
        Optional<HbParticipantModel> participantModel = participantRepository.findByEduUsername(eduUsername);

        if (participantModel.isEmpty()) {
            participantRepository.insertNonExistingPeer(eduUsername);
            participantModel = participantRepository.findByEduUsername(eduUsername);
            commonServiceModule.getGoogleSheetsHandler().writeCell(eduUsername, new CellPosition(GoogleSheetsHandler.USERNAMES_COLUMN_CELL_POSITION.getColumn(), participantModel.get().getRow()));
        }

        return participantModel.get().getRow();
    }

    private List<Character> getEventsColumn(String eventName) {
        Events events = commonServiceModule.getGoogleSheetsHandler().getAllEvents();
        Event event = events.getEventByName(eventName);
        return event.getCellPosition().getColumn();
    }
}
