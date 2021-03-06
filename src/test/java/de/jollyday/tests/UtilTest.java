/**
 * Copyright 2010 Sven Diedrichsen 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either 
 * express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */
package de.jollyday.tests;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.Assert;

import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.junit.Test;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.util.CalendarUtil;

/**
 * @author Sven
 * 
 */
public class UtilTest {

	private CalendarUtil calendarUtil = new CalendarUtil();

	@Test
	public void testWeekend() {
		LocalDate dateFriday = calendarUtil.create(2010, DateTimeConstants.MARCH, 12);
		LocalDate dateSaturday = calendarUtil.create(2010, DateTimeConstants.MARCH, 13);
		LocalDate dateSunday = calendarUtil.create(2010, DateTimeConstants.MARCH, 14);
		LocalDate dateMonday = calendarUtil.create(2010, DateTimeConstants.MARCH, 15);
		Assert.assertFalse(calendarUtil.isWeekend(dateFriday));
		Assert.assertTrue(calendarUtil.isWeekend(dateSaturday));
		Assert.assertTrue(calendarUtil.isWeekend(dateSunday));
		Assert.assertFalse(calendarUtil.isWeekend(dateMonday));
	}

	@Test
	public void testCalendarIslamicNewYear() {
		Set<LocalDate> expected = new HashSet<LocalDate>();
		expected.add(calendarUtil.create(2008, DateTimeConstants.JANUARY, 10));
		expected.add(calendarUtil.create(2008, DateTimeConstants.DECEMBER, 29));
		Set<LocalDate> holidays = calendarUtil.getIslamicHolidaysInGregorianYear(2008, 1, 1);
		Assert.assertNotNull(holidays);
		Assert.assertEquals("Wrong number of islamic new years in 2008.", expected.size(), holidays.size());
		Assert.assertEquals("Wrong islamic New Year holidays in 2008.", expected, holidays);
	}

	@Test
	public void testCalendarIslamicAschura2008() {
		Set<LocalDate> expected = new HashSet<LocalDate>();
		expected.add(calendarUtil.create(2008, DateTimeConstants.JANUARY, 19));
		Set<LocalDate> holidays = calendarUtil.getIslamicHolidaysInGregorianYear(2008, 1, 10);
		Assert.assertNotNull(holidays);
		Assert.assertEquals("Wrong number of islamic Aschura holidays in 2008.", expected.size(), holidays.size());
		Assert.assertEquals("Wrong islamic Aschura holidays in 2008.", expected, holidays);
	}

	@Test
	public void testCalendarIslamicAschura2009() {
		Set<LocalDate> expected = new HashSet<LocalDate>();
		expected.add(calendarUtil.create(2009, DateTimeConstants.JANUARY, 7));
		expected.add(calendarUtil.create(2009, DateTimeConstants.DECEMBER, 27));
		Set<LocalDate> holidays = calendarUtil.getIslamicHolidaysInGregorianYear(2009, 1, 10);
		Assert.assertNotNull(holidays);
		Assert.assertEquals("Wrong number of islamic Aschura holidays in 2009.", expected.size(), holidays.size());
		Assert.assertEquals("Wrong islamic Aschura holidays in 2009.", expected, holidays);
	}

	@Test
	public void testCalendarIslamicIdAlFitr2008() {
		Set<LocalDate> expected = new HashSet<LocalDate>();
		expected.add(calendarUtil.create(2008, DateTimeConstants.OCTOBER, 2));
		Set<LocalDate> holidays = calendarUtil.getIslamicHolidaysInGregorianYear(2008, 10, 1);
		Assert.assertNotNull(holidays);
		Assert.assertEquals("Wrong number of islamic IdAlFitr holidays in 2008.", expected.size(), holidays.size());
		Assert.assertEquals("Wrong islamic IdAlFitr holidays in 2008.", expected, holidays);
	}

	@Test
	public void testCalendarIslamicIdAlFitr2009() {
		Set<LocalDate> expected = new HashSet<LocalDate>();
		expected.add(calendarUtil.create(2009, DateTimeConstants.SEPTEMBER, 21));
		Set<LocalDate> holidays = calendarUtil.getIslamicHolidaysInGregorianYear(2009, 10, 1);
		Assert.assertNotNull(holidays);
		Assert.assertEquals("Wrong number of islamic IdAlFitr holidays in 2009.", expected.size(), holidays.size());
		Assert.assertEquals("Wrong islamic IdAlFitr holidays in 2009.", expected, holidays);
	}

	@Test
	public void testEaster2000() {
		checkEasterDate(2000, 4, 23);
	}

	@Test
	public void testEaster2001() {
		checkEasterDate(2001, 4, 15);
	}

	@Test
	public void testEaster2002() {
		checkEasterDate(2002, 3, 31);
	}

	@Test
	public void testEaster2003() {
		checkEasterDate(2003, 4, 20);
	}

	@Test
	public void testEaster2004() {
		checkEasterDate(2004, 4, 11);
	}

	@Test
	public void testEaster2005() {
		checkEasterDate(2005, 3, 27);
	}

	@Test
	public void testEaster2006() {
		checkEasterDate(2006, 4, 16);
	}

	@Test
	public void testEaster2007() {
		checkEasterDate(2007, 4, 8);
	}

	@Test
	public void testEaster2008() {
		checkEasterDate(2008, 3, 23);
	}

	@Test
	public void testEaster2009() {
		checkEasterDate(2009, 4, 12);
	}

	@Test
	public void testEaster2010() {
		checkEasterDate(2010, 4, 4);
	}

	@Test
	public void testEaster2011() {
		checkEasterDate(2011, 4, 24);
	}

	@Test
	public void testEaster2012() {
		checkEasterDate(2012, 4, 8);
	}

	@Test
	public void testEaster2013() {
		checkEasterDate(2013, 3, 31);
	}

	private void checkEasterDate(Integer year, int month, int day) {
		Assert.assertEquals("Wrong easter date.", calendarUtil.create(year, month, day),
				calendarUtil.getEasterSunday(year));
	}

	@Test
	public void testCalendarUtilEasterJulian() {
		Assert.assertEquals("Wrong easter date.", calendarUtil.create(1583, 4, 10), calendarUtil.getEasterSunday(1583));
	}

	@Test
	public void testCalendarUtilEasterGregorian() {
		Assert.assertEquals("Wrong easter date.", calendarUtil.create(1584, 4, 1), calendarUtil.getEasterSunday(1584));
	}

	@Test
	public void testCalendarUtilToday() {
		LocalDate today = new LocalDate(Calendar.getInstance(), ISOChronology.getInstance());
		Assert.assertEquals("Wrong date.", today, calendarUtil.create());
	}

	@Test
	public void testUmlaut() {
		final LocalDate aDate = calendarUtil.create(2010, DateTimeConstants.JANUARY, 6);
		final HolidayManager aMgr = HolidayManager.getInstance(HolidayCalendar.AUSTRIA);
		final Set<Holiday> hs = aMgr.getHolidays(new Interval(aDate.toDateTimeAtStartOfDay(), aDate
				.toDateTimeAtStartOfDay().plusDays(1)));
		Assert.assertNotNull(hs);
		Assert.assertEquals(1, hs.size());
		Assert.assertEquals("Heilige Drei K\u00F6nige", hs.iterator().next().getDescription(Locale.GERMANY));
	}

}
