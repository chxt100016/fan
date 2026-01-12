package com.chxt.domain.transaction.component;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class MailPickerTest {

    private MailPicker mailPicker;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Before
    public void setUp() {
        mailPicker = new MailPicker();
    }

    @Test
    public void initDateRange_noExclusions() {
        String startDate = LocalDate.now().minusDays(7).format(DATE_FORMATTER);
        mailPicker.initDateRange(startDate, List.of());

        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        assertEquals(1, dateRanges.size());
        assertEquals(startDate, dateRanges.get(0).getStartDateStr());
        assertEquals(LocalDate.now().format(DATE_FORMATTER), dateRanges.get(0).getEndDateStr());
        System.out.println(JSON.toJSONString(dateRanges));
    }

    @Test
    public void initDateRange_withExclusions() {
        String startDate = LocalDate.now().minusDays(10).format(DATE_FORMATTER);
        String excludeDate1 = LocalDate.now().minusDays(8).format(DATE_FORMATTER);
        String excludeDate2 = LocalDate.now().minusDays(5).format(DATE_FORMATTER);

        mailPicker.initDateRange(startDate, List.of(excludeDate1, excludeDate2));

        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        assertEquals(3, dateRanges.size());

        // Range 1: startDate -> excludeDate1 - 1
        assertEquals(startDate, dateRanges.get(0).getStartDateStr());
        assertEquals(LocalDate.now().minusDays(9).format(DATE_FORMATTER), dateRanges.get(0).getEndDateStr());
        System.out.println(JSON.toJSONString(dateRanges));

        // Range 2: excludeDate1 + 1 -> excludeDate2 - 1
        assertEquals(LocalDate.now().minusDays(7).format(DATE_FORMATTER), dateRanges.get(1).getStartDateStr());
        assertEquals(LocalDate.now().minusDays(6).format(DATE_FORMATTER), dateRanges.get(1).getEndDateStr());
        System.out.println(JSON.toJSONString(dateRanges));

        // Range 3: excludeDate2 + 1 -> now
        assertEquals(LocalDate.now().minusDays(4).format(DATE_FORMATTER), dateRanges.get(2).getStartDateStr());
        assertEquals(LocalDate.now().format(DATE_FORMATTER), dateRanges.get(2).getEndDateStr());
        System.out.println(JSON.toJSONString(dateRanges));
    }

    @Test
    public void initDateRange_excludeStartDate() {
        String startDate = LocalDate.now().minusDays(5).format(DATE_FORMATTER);

        mailPicker.initDateRange(startDate, List.of(startDate));

        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        assertEquals(1, dateRanges.size());

        assertEquals(LocalDate.now().minusDays(4).format(DATE_FORMATTER), dateRanges.get(0).getStartDateStr());
        assertEquals(LocalDate.now().format(DATE_FORMATTER), dateRanges.get(0).getEndDateStr());
    }

    @Test
    public void initDateRange_excludeEndDate() {
        String startDate = LocalDate.now().minusDays(5).format(DATE_FORMATTER);
        String today = LocalDate.now().format(DATE_FORMATTER);

        mailPicker.initDateRange(startDate, List.of(today));

        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        if (LocalDate.parse(startDate, DATE_FORMATTER).equals(LocalDate.now())) {
            assertTrue(dateRanges.isEmpty());
        } else {
            assertEquals(1, dateRanges.size());
            assertEquals(startDate, dateRanges.get(0).getStartDateStr());
            assertEquals(LocalDate.now().minusDays(1).format(DATE_FORMATTER), dateRanges.get(0).getEndDateStr());
        }
    }

    @Test
    public void initDateRange_consecutiveExclusions() {
        String startDate = LocalDate.now().minusDays(10).format(DATE_FORMATTER);
        String exclude1 = LocalDate.now().minusDays(8).format(DATE_FORMATTER);
        String exclude2 = LocalDate.now().minusDays(7).format(DATE_FORMATTER);

        mailPicker.initDateRange(startDate, List.of(exclude1, exclude2));

        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        assertEquals(2, dateRanges.size());

        // Range 1: startDate -> exclude1 - 1
        assertEquals(startDate, dateRanges.get(0).getStartDateStr());
        assertEquals(LocalDate.now().minusDays(9).format(DATE_FORMATTER), dateRanges.get(0).getEndDateStr());

        // Range 2: exclude2 + 1 -> now
        assertEquals(LocalDate.now().minusDays(6).format(DATE_FORMATTER), dateRanges.get(1).getStartDateStr());
        assertEquals(LocalDate.now().format(DATE_FORMATTER), dateRanges.get(1).getEndDateStr());
    }

    @Test
    public void initDateRange_exclusionsOutsideRange() {
        String startDate = LocalDate.now().minusDays(5).format(DATE_FORMATTER);
        String before = LocalDate.now().minusDays(10).format(DATE_FORMATTER);

        mailPicker.initDateRange(startDate, List.of(before));

        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        assertEquals(1, dateRanges.size());
        assertEquals(startDate, dateRanges.get(0).getStartDateStr());
        assertEquals(LocalDate.now().format(DATE_FORMATTER), dateRanges.get(0).getEndDateStr());
    }

    @Test
    public void initDateRange_startDateAfterNow() {
        String startDate = LocalDate.now().plusDays(1).format(DATE_FORMATTER);
        mailPicker.initDateRange(startDate, List.of());
        
        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        assertTrue(dateRanges.isEmpty());
    }

    @Test
    public void initDateRange_duplicateExclusions() {
        String startDate = LocalDate.now().minusDays(5).format(DATE_FORMATTER);
        String excludeDate = LocalDate.now().minusDays(3).format(DATE_FORMATTER);

        mailPicker.initDateRange(startDate, List.of(excludeDate, excludeDate));

        List<MailPicker.DateRange> dateRanges = mailPicker.getDateRanges();
        assertNotNull(dateRanges);
        assertEquals(2, dateRanges.size());

        // Range 1
        assertEquals(startDate, dateRanges.get(0).getStartDateStr());
        assertEquals(LocalDate.now().minusDays(4).format(DATE_FORMATTER), dateRanges.get(0).getEndDateStr());

        // Range 2
        assertEquals(LocalDate.now().minusDays(2).format(DATE_FORMATTER), dateRanges.get(1).getStartDateStr());
        assertEquals(LocalDate.now().format(DATE_FORMATTER), dateRanges.get(1).getEndDateStr());
    }
}
