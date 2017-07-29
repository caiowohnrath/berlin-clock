package com.ubs.opsit.interviews;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;


class BerlinTimeConverter implements TimeConverter {

    private enum LightRow {
        SECOND_BLOCK(Collections.singletonList(
                Element.YELLOW),
                    Element.LF),
        FIVE_HOURS_BLOCK(Arrays.asList(
                Element.RED,
                Element.RED,
                Element.RED,
                Element.RED),
                          Element.LF),
        ONE_HOUR_BLOCK(Arrays.asList(
                Element.RED,
                Element.RED,
                Element.RED,
                Element.RED),
                          Element.LF),
        FIVE_MINUTES_BLOCK(Arrays.asList(
                Element.YELLOW,
                Element.YELLOW,
                Element.RED,
                Element.YELLOW,
                Element.YELLOW,
                Element.RED,
                Element.YELLOW,
                Element.YELLOW,
                Element.RED,
                Element.YELLOW,
                Element.YELLOW),
                          Element.LF),
        ONE_MINUTE_BLOCK(Arrays.asList(
                Element.YELLOW,
                Element.YELLOW,
                Element.YELLOW,
                Element.YELLOW),
                        Element.BLANK);

        private enum Element {
            RED("R"), YELLOW("Y"), OFF("O"), LF("\n"), BLANK("");

            private String element;

            Element(String element) {
                this.element = element;
            }

            @Override
            public String toString() {
                return element;
            }
        }

        private List<Element> row;
        private Element lineEnd;

        LightRow(List<Element> row, Element lineEnd) {
            this.row = row;
            this.lineEnd = lineEnd;
        }

        public String getRowLights(int numberOfLights) {
            return getSubListLights(numberOfLights)
                    + createOffSequence(row.size() - numberOfLights)
                    + lineEnd.toString();
        }

        private String getSubListLights(int numberOfLights) {
            if (numberOfLights == 0) {
                return Element.BLANK.toString();
            }

            return Joiner.on(Element.BLANK.toString()).join(row.subList(0, numberOfLights));
        }

        private String createOffSequence(int numberOfLights) {
            return Joiner.on(Element.BLANK.toString()).join(Collections.nCopies(numberOfLights, Element.OFF.toString()));
        }
    }

    private static final String REGEX_HOUR_PATTERN = "([01]?[0-9]|2[0-4]):([0-5][0-9]):([0-5][0-9])";

    public String convertTime(String aTime) {

        checkForValidTimeEntry(aTime);
        StringBuilder berlinClockTime = new StringBuilder();

        int seconds = getSeconds(aTime);
        berlinClockTime.append(LightRow.SECOND_BLOCK.getRowLights((seconds + 1) % 2));

        int hours = getHours(aTime);
        berlinClockTime.append(LightRow.FIVE_HOURS_BLOCK.getRowLights(hours / 5));
        berlinClockTime.append(LightRow.ONE_HOUR_BLOCK.getRowLights(hours % 5));

        int minutes = getMinutes(aTime);
        berlinClockTime.append(LightRow.FIVE_MINUTES_BLOCK.getRowLights(minutes / 5));
        berlinClockTime.append(LightRow.ONE_MINUTE_BLOCK.getRowLights(minutes % 5));

        return berlinClockTime.toString();
    }

    private void checkForValidTimeEntry(String aTime) {
        if (Strings.isNullOrEmpty(aTime) || !aTime.matches(REGEX_HOUR_PATTERN)) {
            throw new RuntimeException("Invalid aTime entry.");
        }
    }

    private int getHours(String aTime) {
        return Integer.parseInt(aTime.substring(0, 2));
    }

    private int getMinutes(String aTime) {
        return Integer.parseInt(aTime.substring(3, 5));
    }

    private int getSeconds(String aTime) {
        return Integer.parseInt(aTime.substring(6));
    }
}
