/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.sbt.jschool.session2;

import org.apache.commons.lang3.StringUtils;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class OutputFormatter {
    private PrintStream out;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
    private DecimalFormat moneyFormat = createFormat(new DecimalFormat("###,##0.00"), formatSymbols);
    private DecimalFormat numberFormat = createFormat(new DecimalFormat(), formatSymbols);
    private boolean[] isString;

    public OutputFormatter(PrintStream out) {
        this.out = out;
    }

    public void output(String[] names, Object[][] data) {
        Integer[] maxLength = getMaxLength(names, data);
        String horizontalTableBorder = createHorizontalTableBorder(maxLength);
        this.out.println(horizontalTableBorder);
        printLine(maxLength, names, true);
        this.out.println(horizontalTableBorder);
        for (Object[] lines : data) {
            printLine(maxLength, lines, false);
            this.out.println(horizontalTableBorder);
        }
    }

    private String createHorizontalTableBorder(Integer[] maxLength) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < maxLength.length; i++) {
            line.append('+').append(StringUtils.repeat('-', maxLength[i])).append((i == maxLength.length - 1) ? '+' : "");
        }
        return line.toString();
    }

    private void printLine(Integer[] maxLength, Object[] objects, boolean names) {
        String obj;
        for (int i = 0; i < objects.length; i++) {
            this.out.printf(
                    (i == 0 ? "|" : "") + "%" + ((isString[i] && !names) ? "-" : "") +
                            maxLength[i] + "s|" + (i == objects.length - 1 ? "%n" : ""),
                    names ? (obj = objects[i].toString())
                            .concat(StringUtils.repeat(' ', (maxLength[i] - obj.length()) / 2 + (maxLength[i] - obj.length()) % 2))
                          : getValue(objects[i]));
        }
    }

    private Integer[] getMaxLength(String[] names, Object[][] data) {
        Integer[] maxLength = new Integer[names.length];
        isString = new boolean[names.length];
        for (int i = 0; i < names.length; i++) {
            maxLength[i] = names[i] == null ? 1 : names[i].length();
        }
        for (int i = 0; i < data.length; i++) {
            int length;
            for (int j = 0; j < data[i].length; j++) {
                length = data[i][j] == null ? 1 : getValue(data[i][j]).length();
                if (!isString[j]) {
                    isString[j] = data[i][j] instanceof String;
                }
                if (length > maxLength[j]) {
                    maxLength[j] = length;
                }
            }
        }
        return maxLength;
    }

    private DecimalFormat createFormat(DecimalFormat format, DecimalFormatSymbols symbols) {
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        format.setGroupingSize(3);
        format.setGroupingUsed(true);
        format.setDecimalFormatSymbols(symbols);
        return format;
    }

    private String getValue(Object object) {
        if (object == null) {
            return "-";
        }
        if (object instanceof Date) {
            return dateFormat.format(object);
        }
        if (object instanceof Number) {
            if (object instanceof Float || object instanceof Double) {
                return moneyFormat.format(object);
            } else {
                return numberFormat.format(object);
            }
        }
        return object.toString();
    }
}
