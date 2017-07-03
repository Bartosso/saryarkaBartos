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
//            dataRow.add(data.getNaviki());
            dataRow.add(data.getContact());
//            dataRow.add(data.getPhoneNumber());
            writeData.add(dataRow);
        }

        ValueRange vr = new ValueRange().setValues(writeData).setMajorDimension("ROWS");
        service.spreadsheets().values()
                .update(spreadsheetId, writeRange, vr)
                .setValueInputOption("RAW")
                .execute();
    }

/*
Этот метод умеет добавлять данные в хвост, но с ним еще нужно разбираться
    private void  AddData() throws Exception {
        service = getSheetsService();
        String spreadSheetID = "1ZAFFrDgmkCcCVrw_zMFvOnogy0bQ258CxROT11R7LD0";
        //Integer sheetID = 123;
        String DateValue = "2015-07-13";
        List<RowData> rowData = new ArrayList<RowData>();
        List<CellData> cellData = new ArrayList<CellData>();
        CellData cell = new CellData();
        cell.setUserEnteredValue(new ExtendedValue().setStringValue(DateValue));
        cell.setUserEnteredFormat(new CellFormat().setNumberFormat(new NumberFormat().setType("DATE")));
        cellData.add(cell);
        rowData.add(new RowData().setValues(cellData));
        //Sheets.Spreadsheets
        BatchUpdateSpreadsheetRequest batchRequests = new BatchUpdateSpreadsheetRequest();
        BatchUpdateSpreadsheetResponse response;
        List<Request> requests = new ArrayList<Request>();
        AppendCellsRequest appendCellReq = new AppendCellsRequest();
        //appendCellReq.setSheetId( sheetID);
        appendCellReq.setRows(rowData);
        appendCellReq.setFields("userEnteredValue,userEnteredFormat.numberFormat");
        requests = new ArrayList<Request>();
        requests.add(new Request().setAppendCells(appendCellReq));
        batchRequests = new BatchUpdateSpreadsheetRequest();
        batchRequests.setRequests(requests);
        response = service.spreadsheets().batchUpdate(spreadSheetID, batchRequests).execute();
        System.out.println("Request \n\n");
        System.out.println(batchRequests.toPrettyString());
        System.out.println("\n\nResponse \n\n");
        System.out.println(response.toPrettyString());
    }
    */
}
