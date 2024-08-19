package mavmi.telegram_bot.hb.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.hb.sheets.dto.CellPosition;
import mavmi.telegram_bot.hb.sheets.dto.Event;
import mavmi.telegram_bot.hb.sheets.dto.Events;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class GoogleSheetsHandler {

    public static final CellPosition USERNAMES_COLUMN_CELL_POSITION = new CellPosition(List.of('A'), 0);

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS);

    @Value("${sheets.application-name}")
    private String applicationName;
    @Value("${sheets.sheet-id}")
    private String sheetId;
    @Value("${sheets.credentials-directory-path}")
    private String credentialsDirectoryPath;
    @Value("${sheets.unsaved-values-file-path}")
    private String unsavedCellsFilePath;

    private Events events = new Events();
    private Sheets service;

    public Events getAllEvents() {
        return events;
    }

    @PostConstruct
    public void init() {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            service = new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                    .setApplicationName(applicationName)
                    .build();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }

        List<List<String>> sheet = readRange("B2:AJ4");
        if (sheet == null) {
            throw new RuntimeException("Не могу прочитать ивенты из таблички");
        }

        List<String> factors = sheet.get(0);
        List<String> eventGroupNames = sheet.get(1);
        List<String> eventName = sheet.get(2);
        String lastGroupName = null;

        for (int i = 0; i < eventName.size(); i++) {
            String name = eventName.get(i);

            String groupName;
            try {
                groupName = eventGroupNames.get(i);
                if (groupName.isEmpty()) {
                    groupName = lastGroupName;
                } else {
                    lastGroupName = groupName;
                }
            } catch (Exception e) {
                groupName = lastGroupName;
            }

            String factorStr = factors.get(i).replace(',', '.');
            double factor = Double.parseDouble(factorStr);

            CellPosition cellPosition = new CellPosition(new ArrayList<>() {{ add('B'); }}, 4);
            for (int j = 0; j < i; j++) {
                cellPosition.moveRight();
            }

            Event event = new Event(factor, name, groupName, cellPosition);
            events.addEvent(event);
        }
    }

    synchronized public void writeCell(String msg, CellPosition cellPosition) {
        ValueRange body = new ValueRange().setValues(List.of(List.of(msg)));
        try {
            service
                    .spreadsheets()
                    .values()
                    .update(sheetId, cellPosition.toString(), body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        } catch (IOException e) {
            log.warn("Не удалось записать данные в ячейку\n{}: {}", cellPosition, msg);
            e.printStackTrace(System.out);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(unsavedCellsFilePath))) {
                writer.write(cellPosition.toString());
                writer.write("\t");
                writer.write(msg);
                writer.newLine();
            } catch (IOException ex) {
                log.error("Это пиздец");
            }
        }
    }

    private List<List<String>> readRange(String range) {
        try {
            List<List<String>> output = new ArrayList<>();
            List<List<Object>> input = service
                    .spreadsheets()
                    .values()
                    .get(sheetId, range)
                    .execute()
                    .getValues();

            for (List<Object> objectsList : input){
                List<String> stringsList = new ArrayList<>();
                for (Object o : objectsList) {
                    stringsList.add((String) o);
                }
                output.add(stringsList);
            }

            return output;
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private String readCell(String cellPosition) {
        String value = null;

        try {
            value = (String) service
                    .spreadsheets()
                    .values()
                    .get(sheetId, cellPosition)
                    .execute()
                    .getValues()
                    .get(0)
                    .get(0);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return null;
        }

        return value;
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        InputStream in = new FileInputStream(credentialsDirectoryPath + "/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(credentialsDirectoryPath)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
