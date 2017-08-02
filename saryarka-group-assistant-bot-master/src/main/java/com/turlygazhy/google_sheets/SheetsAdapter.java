package com.turlygazhy.google_sheets;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.turlygazhy.entity.Member;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetsAdapter {

    static final String APPLICATION_NAME = "Google spreadsheet";
    final JsonFactory JSON_FACTORY = new GsonFactory();
    HttpTransport httpTransport;
    final List<String> SPREADSHEET_SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

    Sheets service;

    public void authorize(String securityFileName) throws Exception {
        InputStream stream = new FileInputStream(securityFileName);
        try {
            authorize(stream);
        } finally {
            stream.close();
        }
    }

    public void authorize(InputStream stream) throws Exception {

        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = GoogleCredential.fromStream(stream)
                .createScoped(SPREADSHEET_SCOPES);

        service = new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void writeData(String spreadsheetId,
                          String sheetName,
                          char colStart, int rowStart,
                          List<Member> myData) throws IOException {

        String writeRange = sheetName + "!" + colStart + rowStart + ":" + (char) (colStart + 5);

        List<List<Object>> writeData = new ArrayList<>();
        for (Member data : myData) {
            List<Object> dataRow = new ArrayList<>();
            dataRow.add(data.getFIO());
            dataRow.add(data.getCompanyName());
            dataRow.add(data.getNisha());
            dataRow.add("t.me/" +data.getUserName());
            dataRow.add(data.getCity());
            writeData.add(dataRow);
        }

        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");


        service.spreadsheets().values()
                .update(spreadsheetId, writeRange, vr)
                .setValueInputOption("RAW")
                .execute();
    }

    @SuppressWarnings("Duplicates")
    public void appendData(String spreadsheetId,
                           String sheetName,
                           char colStart, int rowStart,
                           List<Member> myData) throws IOException {

        String writeRange = sheetName + "!" + colStart + rowStart + ":" + (char) (colStart + 5);

        List<List<Object>> writeData = new ArrayList<>();
        for (Member data : myData) {
            List<Object> dataRow = new ArrayList<>();
            dataRow.add(data.getFIO());
            dataRow.add(data.getCompanyName());
            dataRow.add(data.getNisha());
            dataRow.add("t.me/" +data.getUserName());
            dataRow.add(data.getCity());
            writeData.add(dataRow);
        }

        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");


        service.spreadsheets().values()
                .append(spreadsheetId, writeRange, vr)
                .setValueInputOption("RAW")
                .execute();
    }
}
