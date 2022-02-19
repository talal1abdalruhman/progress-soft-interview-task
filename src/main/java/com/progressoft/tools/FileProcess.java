package com.progressoft.tools;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class FileProcess {
    String fileToRead;
    String fileToWrite;
    int targetColIdx = -1;

    public FileProcess(String fileToRead, String fileToWrite) {
            this.fileToRead = fileToRead;
            this.fileToWrite = fileToWrite;
    }

    public ArrayList<String> readFileCol(String colName) throws IOException {
        FileReader fileReader;
        BufferedReader bufferedReader;
        try {
            fileReader = new FileReader(fileToRead);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("source file not found");
        }
        targetColIdx = findTargetColIdx(bufferedReader, colName);
        String line;
        String[] rowData;
        ArrayList<String> targetCol = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            rowData = line.split(",");
            targetCol.add(rowData[targetColIdx]);
        }
        bufferedReader.close();
        fileReader.close();

        return targetCol;
    }

    public void writeColToFile(BigDecimal[] newColData, String newColName) throws IOException {
        FileReader fileReader;
        BufferedReader bufferedReader;
        try {
            fileReader = new FileReader(fileToRead);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("source file not found");
        }
        // to store new file data
        String dataToWrite = "";
        // read file headers and add new col head
        String line = bufferedReader.readLine();
        String[] rowData;
        rowData = line.split(",");
        for (int i = 0; i < rowData.length; i++) {
            dataToWrite += rowData[i] + ((i < rowData.length - 1) ? "," : "");
            if (i == targetColIdx) {
                if (i == rowData.length - 1) dataToWrite += ",";
                dataToWrite += newColName;
                if (targetColIdx < rowData.length - 1) dataToWrite += ",";
            }
        }
        dataToWrite += "\n";
        // read file values and add new col values
        int idx = 0;
        while ((line = bufferedReader.readLine()) != null) {
            rowData = line.split(",");
            for (int i = 0; i < rowData.length; i++) {
                dataToWrite += rowData[i] + ((i < rowData.length - 1) ? "," : "");
                if (i == targetColIdx) {
                    if (i == rowData.length - 1) dataToWrite += ",";
                    dataToWrite += newColData[idx++].setScale(2, RoundingMode.HALF_EVEN).toString();
                    if (targetColIdx < rowData.length - 1) dataToWrite += ",";
                }
            }
            dataToWrite += "\n";
        }
        bufferedReader.close();
        fileReader.close();

        //write new data with new col to destPath
        FileWriter fileWriter;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(fileToWrite);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(dataToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bufferedWriter.close();
        fileReader.close();
    }

    // find target column index and handle if it not exists
    private int findTargetColIdx(BufferedReader bufferedReader, String colName) throws IOException {
        int idx = -1;
        String headerRow = bufferedReader.readLine();
        String[] headers = headerRow.split(",");

        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(colName)) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            throw new IllegalArgumentException("column " + colName + " not found");
        }
        return idx;
    }
}
