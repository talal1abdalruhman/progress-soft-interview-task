package com.progressoft.tools;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;

public class NormalizerImp implements Normalizer {
    @Override
    public ScoringSummary zscore(Path csvPath, Path destPath, String colToStandardize) {
        csvPath = checkNullValue(csvPath);
        destPath = checkNullValue(destPath);
        colToStandardize = checkNullValue(colToStandardize);
        FileProcess fileProcess = new FileProcess(csvPath.toString(), destPath.toString());
        int[] dataCol = new int[0];
        try {
            dataCol = fileProcess.readFileCol(colToStandardize).stream().mapToInt(Integer::parseInt).toArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // getting summary
        ScoringSummaryImp summaryImp = new ScoringSummaryImp(dataCol);
        // calculate ZScore
        BigDecimal[] standardizedDataCol = new BigDecimal[dataCol.length];
        for (int i = 0; i < dataCol.length; i++) {
            BigDecimal number = new BigDecimal(dataCol[i]);
            standardizedDataCol[i] = number.subtract(summaryImp.mean())
                    .divide(summaryImp.standardDeviation(), RoundingMode.HALF_EVEN)
                    .setScale(2, RoundingMode.HALF_EVEN);
        }
        try {
            fileProcess.writeColToFile(standardizedDataCol, colToStandardize + "_z");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return summaryImp;
    }

    @Override
    public ScoringSummary minMaxScaling(Path csvPath, Path destPath, String colToNormalize) {
        csvPath = checkNullValue(csvPath);
        destPath = checkNullValue(destPath);
        colToNormalize = checkNullValue(colToNormalize);
        FileProcess fileProcess = new FileProcess(csvPath.toString(), destPath.toString());
        int[] dataCol = new int[0];
        try {
            dataCol = fileProcess.readFileCol(colToNormalize).stream().mapToInt(Integer::parseInt).toArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        // getting summary
        ScoringSummaryImp summaryImp = new ScoringSummaryImp(dataCol);
        // calculate MinMaxScaling
        BigDecimal[] normalizedDataCol = new BigDecimal[dataCol.length];
        for (int i = 0; i < dataCol.length; i++) {
            BigDecimal number = new BigDecimal(dataCol[i]);
            normalizedDataCol[i] = number.subtract(summaryImp.min())
                    .divide(summaryImp.max().subtract(summaryImp.min()), RoundingMode.HALF_EVEN);
        }

        try {
            fileProcess.writeColToFile(normalizedDataCol, colToNormalize + "_mm");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return summaryImp;
    }

    public Path checkNullValue(Path path) {
        if (path == null) return Paths.get("null_file");
        return path;
    }
    // overloading for multipurpose use
    public String checkNullValue(String col) {
        if (col == null) return "null_col";
        return col;
    }
}
