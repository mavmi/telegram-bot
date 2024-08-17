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
import jakarta.annotation.PostConstruct;
import mavmi.telegram_bot.hb.sheets.dto.CellPosition;
import mavmi.telegram_bot.hb.sheets.dto.Events;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleSheetsHandler {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS);
    private static final CellPosition EVENTS_CELL_FIRST = new CellPosition(new ArrayList<>(){{ add('B'); }}, 4);
    private static final CellPosition EVENTS_CELL_LAST = new CellPosition(new ArrayList<>(){{ add('A'); add('E'); }}, 4);

    @Value("${sheets.application-name}")
    private String applicationName;
    @Value("${sheets.sheet-id}")
    private String sheetId;
    @Value("${sheets.credentials-directory-path}")
    private String credentialsDirectoryPath;

    private Sheets service;

    public Events getAllEvents() {
        return null;
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
