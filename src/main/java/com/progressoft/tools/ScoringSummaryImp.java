package com.progressoft.tools;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

public class ScoringSummaryImp implements ScoringSummary {
    private BigDecimal[] colValues;
    private final BigDecimal numOfValues;
    // hooks to store calculated values
    private BigDecimal mean = null;
    private BigDecimal standardDeviation = null;
    private BigDecimal variance = null;
    private BigDecimal median = null;
    private BigDecimal min = null;
    private BigDecimal max = null;

    public ScoringSummaryImp(int[] colValues) {
        this.colValues = Arrays.stream(colValues).mapToObj(BigDecimal::new).toArray(BigDecimal[]::new);
        // sort to find min and max
        Arrays.sort(this.colValues);
        // get values count
        numOfValues = new BigDecimal(colValues.length);
    }

    @Override
    public BigDecimal mean() {
        if (checkNullValue(mean)) {
            BigDecimal sumOfValues = new BigDecimal(0);
            for (BigDecimal value : colValues)
                sumOfValues = sumOfValues.add(value);
            mean = sumOfValues.divide(numOfValues, RoundingMode.HALF_EVEN);
        }
        return reScaleNum(mean);
    }

    @Override
    public BigDecimal standardDeviation() {
        if (checkNullValue(standardDeviation)) {
            if (checkNullValue(mean)) mean = mean();
            BigDecimal deviationsOfValues = new BigDecimal(0);
            for (BigDecimal value : colValues)
                deviationsOfValues = deviationsOfValues.add((value.subtract(mean)).pow(2));

            MathContext mathContext = new MathContext(10);
            standardDeviation = deviationsOfValues.divide(numOfValues, RoundingMode.HALF_EVEN).sqrt(mathContext);
        }
        return reScaleNum(standardDeviation);
    }

    @Override
    public BigDecimal variance() {
        if (checkNullValue(variance)) {
            if (checkNullValue(standardDeviation)) standardDeviation();
            variance = reScaleNum(standardDeviation.pow(2));
        }
        return variance;
    }

    @Override
    public BigDecimal median() {
        if (checkNullValue(median)) {
            if (colValues.length % 2 != 0) {
                median = colValues[colValues.length / 2];
            } else {
                median = colValues[colValues.length / 2]
                        .add(colValues[colValues.length / 2 + 1])
                        .divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN);
            }
        }
        return reScaleNum(median);
    }

    @Override
    public BigDecimal min() {
        if (checkNullValue(min))
            min = colValues[0];
        return reScaleNum(min);
    }

    @Override
    public BigDecimal max() {
        if (checkNullValue(max))
            max = colValues[colValues.length - 1];
        return reScaleNum(max);
    }

    // always check if the value is already calculated to avoid any wasted time
    private boolean checkNullValue(BigDecimal num) {
        if (num == null) return true;
        return false;
    }

    // set scale to BigDecimals
    private BigDecimal reScaleNum(BigDecimal num) {
        return num.setScale(2, RoundingMode.HALF_EVEN);
    }
}
