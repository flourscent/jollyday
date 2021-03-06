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
package de.jollyday;

import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.LocalDate;
import org.joda.time.ReadableInterval;

import de.jollyday.configuration.ConfigurationProviderManager;
import de.jollyday.util.CalendarUtil;
import de.jollyday.util.ClassLoadingUtil;

/**
 * Abstract base class for all holiday manager implementations. Upon call of
 * getInstance method the implementing class will be read from the
 * jollyday.properties file and instantiated.
 * 
 * @author Sven Diedrichsen
 * @version $Id: $
 */
public abstract class HolidayManager {

	private static final Logger LOG = Logger.getLogger(HolidayManager.class.getName());
	/**
	 * Configuration property for the implementing Manager class.
	 */
	private static final String MANAGER_IMPL_CLASS_PREFIX = "manager.impl";
	/**
	 * Signifies if caching of manager instances is enabled. If not every call
	 * to getInstance will return a newly instantiated and initialized manager.
	 */
	private static boolean managerCachingEnabled = true;
	/**
	 * This map represents a cache for manager instances on a per country basis.
	 */
	private static final Map<String, HolidayManager> MANAGER_CHACHE = new HashMap<String, HolidayManager>();

	/**
	 * Caches the holidays for a given year and state/region.
	 */
	private Map<String, Set<Holiday>> holidaysPerYear = new HashMap<String, Set<Holiday>>();
	/**
	 * The configuration properties.
	 */
	private Properties properties = new Properties();

	/**
	 * Utility for calendar operations
	 */
	protected CalendarUtil calendarUtil = new CalendarUtil();
	/**
	 * Utility to load classes.
	 */
	private static ClassLoadingUtil classLoadingUtil = new ClassLoadingUtil();
	/**
	 * Manager for configuration providers. Delivers the jollyday configuration.
	 */
	private static ConfigurationProviderManager configurationProviderManager = new ConfigurationProviderManager();

	/**
	 * Returns a HolidayManager instance by calling getInstance(NULL) and thus
	 * using the default locales country code. code.
	 * 
	 * @return default locales HolidayManager
	 */
	public static final HolidayManager getInstance() {
		return getInstance((String) null);
	}

	/**
	 * Returns a HolidayManager instance by calling getInstance(NULL,
	 * Properties) and thus using the default locales country code and the
	 * provided configuration properties.
	 * 
	 * @param properties
	 *            configuration properties to use
	 * @return default locales HolidayManager
	 */
	public static final HolidayManager getInstance(Properties properties) {
		return getInstance((String) null, properties);
	}

	/**
	 * Returns a HolidayManager for the provided country.
	 * 
	 * @param c
	 *            Country
	 * @return HolidayManager
	 */
	public static final HolidayManager getInstance(final HolidayCalendar c) {
		return getInstance(c.getId());
	}

	/**
	 * Returns a HolidayManager for the provided country with the provided
	 * configuration properties.
	 * 
	 * @param properties
	 *            the configuration properties
	 * @param c
	 *            Country
	 * @return HolidayManager
	 */
	public static final HolidayManager getInstance(final HolidayCalendar c, Properties properties) {
		return getInstance(c.getId(), properties);
	}

	/**
	 * Creates an HolidayManager instance. The implementing HolidayManager class
	 * will be read from the jollyday.properties file. If the calendar is NULL
	 * or an empty string the default locales country code will be used.
	 * 
	 * @param calendar
	 *            a {@link java.lang.String} object.
	 * @return HolidayManager implementation for the provided country.
	 */
	public static final HolidayManager getInstance(final String calendar) {
		return getInstance(calendar, null);
	}

	/**
	 * Creates an HolidayManager instance. The implementing HolidayManager class
	 * will be read from the configuration properties. If the calendar is NULL
	 * or an empty string the default locales country code will be used.
	 * 
	 * @param properties
	 *            the configuration properties
	 * @param calendar
	 *            a {@link java.lang.String} object.
	 * @return HolidayManager implementation for the provided country.
	 */
	public static final HolidayManager getInstance(final String calendar, Properties properties) {
		final String calendarName = prepareCalendarName(calendar);
		HolidayManager m = isManagerCachingEnabled() ? getFromCache(calendarName) : null;
		if (m == null) {
			m = createManager(calendarName, properties);
		}
		return m;
	}

	/**
	 * Creates an HolidayManager instance. The implementing HolidayManager class
	 * will be read from the jollyday.properties file. If the URL is NULL an
	 * exception will be thrown.
	 * 
	 * @param url
	 *            the URL to the calendar's file
	 * @return HolidayManager implementation for the provided country.
	 */
	public static final HolidayManager getInstance(final URL url) {
		return getInstance(url, null);
	}

	/**
	 * Creates an HolidayManager instance. The implementing HolidayManager class
	 * will be read from the jollyday.properties file. If the URL is NULL an
	 * exception will be thrown.
	 * 
	 * @param properties
	 *            the configuration properties
	 * @param url
	 *            the URL to the calendar's file
	 * @return HolidayManager implementation for the provided country.
	 */
	public static final HolidayManager getInstance(final URL url, Properties properties) {
		if (url == null) {
			throw new NullPointerException("Missing URL.");
		}
		HolidayManager m = isManagerCachingEnabled() ? getFromCache(url.toString()) : null;
		if (m == null) {
			m = createManager(url, properties);
		}
		return m;
	}

	/**
	 * Creates a new <code>HolidayManager</code> instance for the country and
	 * puts it to the manager cache.
	 * 
	 * @param calendar
	 *            <code>HolidayManager</code> instance for the calendar
	 * @return new
	 */
	private static HolidayManager createManager(final String calendar, Properties properties) {
		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Creating HolidayManager for calendar '" + calendar + "'. Caching enabled: "
					+ isManagerCachingEnabled());
		}
		Properties props = configurationProviderManager.getConfigurationProperties(properties);
		String managerImplClassName = readManagerImplClassName(calendar, props);
		HolidayManager m = instantiateManagerImpl(managerImplClassName);
		m.setProperties(props);
		m.init(calendar);
		if (isManagerCachingEnabled()) {
			putToCache(calendar, m);
		}
		return m;
	}

	/**
	 * Reads the managers implementation class from the properties config file.
	 * 
	 * @param calendar
	 *            the calendar name
	 * @param props
	 *            properties to read from
	 * @return the manager implementation class name
	 */
	private static String readManagerImplClassName(final String calendar, Properties props) {
		String managerImplClassName = null;
		if (calendar != null && props.containsKey(MANAGER_IMPL_CLASS_PREFIX + "." + calendar)) {
			managerImplClassName = props.getProperty(MANAGER_IMPL_CLASS_PREFIX + "." + calendar);
		} else if (props.containsKey(MANAGER_IMPL_CLASS_PREFIX)) {
			managerImplClassName = props.getProperty(MANAGER_IMPL_CLASS_PREFIX);
		} else {
			throw new IllegalStateException("Missing configuration '" + MANAGER_IMPL_CLASS_PREFIX
					+ "'. Cannot create manager.");
		}
		return managerImplClassName;
	}

	/**
	 * Instantiates the manager implementating class.
	 * 
	 * @param managerImplClassName
	 *            the managers class name
	 * @return the implementation class instantiated
	 */
	private static HolidayManager instantiateManagerImpl(String managerImplClassName) {
		try {
			Class<?> managerImplClass = classLoadingUtil.loadClass(managerImplClassName);
			Object managerImplObject = managerImplClass.newInstance();
			return HolidayManager.class.cast(managerImplObject);
		} catch (Exception e) {
			throw new IllegalStateException("Cannot create manager class " + managerImplClassName, e);
		}
	}

	/**
	 * Creates a new <code>HolidayManager</code> instance for the URL and puts
	 * it to the manager cache.
	 * 
	 * @param url
	 *            the URL to a file containing the calendar
	 * @return the holiday manager initialized with the provided URL
	 */
	private static HolidayManager createManager(final URL url, Properties properties) {
		if (LOG.isLoggable(Level.FINER)) {
			LOG.finer("Creating HolidayManager for URL '" + url + "'. Caching enabled: " + isManagerCachingEnabled());
		}
		Properties props = configurationProviderManager.getConfigurationProperties(properties);
		String managerImplClassName = readManagerImplClassName(null, props);
		HolidayManager m = instantiateManagerImpl(managerImplClassName);
		m.setProperties(props);
		m.init(url);
		if (isManagerCachingEnabled()) {
			putToCache(url.toString(), m);
		}
		return m;
	}

	/**
	 * Handles NULL or empty country codes and returns the default locals
	 * country codes for those. For all others the country code will be trimmed
	 * and set to lower case letters.
	 * 
	 * @param calendar
	 * @return trimmed and lower case country code.
	 */
	private static String prepareCalendarName(String calendar) {
		if (calendar == null || "".equals(calendar.trim())) {
			calendar = Locale.getDefault().getCountry().toLowerCase();
		} else {
			calendar = calendar.trim().toLowerCase();
		}
		return calendar;
	}

	/**
	 * Caches the manager instance for this country.
	 * 
	 * @param country
	 * @param manager
	 */
	private static void putToCache(final String country, final HolidayManager manager) {
		synchronized (MANAGER_CHACHE) {
			MANAGER_CHACHE.put(country, manager);
		}
	}

	/**
	 * Tries to retrieve a manager instance from cache by country.
	 * 
	 * @param country
	 * @return Manager instance for this country. NULL if none is cached yet.
	 */
	private static HolidayManager getFromCache(final String country) {
		synchronized (MANAGER_CHACHE) {
			return MANAGER_CHACHE.get(country);
		}
	}

	/**
	 * If true, instantiated managers will be cached. If false every call to
	 * getInstance will create new manager. True by default.
	 * 
	 * @param managerCachingEnabled
	 *            the managerCachingEnabled to set
	 */
	public static void setManagerCachingEnabled(boolean managerCachingEnabled) {
		HolidayManager.managerCachingEnabled = managerCachingEnabled;
	}

	/**
	 * <p>
	 * isManagerCachingEnabled.
	 * </p>
	 * 
	 * @return the managerCachingEnabled
	 */
	public static boolean isManagerCachingEnabled() {
		return managerCachingEnabled;
	}

	/**
	 * Clears the manager cache from all cached manager instances.
	 */
	public static void clearManagerCache() {
		synchronized (MANAGER_CHACHE) {
			MANAGER_CHACHE.clear();
		}
	}

	/**
	 * Calls isHoliday with JODA time object.
	 * 
	 * @param c
	 *            a {@link java.util.Calendar} object.
	 * @param args
	 *            a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean isHoliday(final Calendar c, final String... args) {
		return isHoliday(calendarUtil.create(c), args);
	}

	/**
	 * Show if the requested date is a holiday.
	 * 
	 * @param c
	 *            The potential holiday.
	 * @param args
	 *            Hierarchy to request the holidays for. i.e. args = {'ny'} ->
	 *            New York holidays
	 * @return is a holiday in the state/region
	 */
	public boolean isHoliday(final LocalDate c, final String... args) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append(c.getYear());
		for (String arg : args) {
			keyBuilder.append("_");
			keyBuilder.append(arg);
		}
		String key = keyBuilder.toString();
		if (!holidaysPerYear.containsKey(key)) {
			Set<Holiday> holidays = getHolidays(c.getYear(), args);
			holidaysPerYear.put(key, holidays);
		}
		return calendarUtil.contains(holidaysPerYear.get(key), c);
	}

	/**
	 * Returns a set of all currently supported calendar codes.
	 * 
	 * @return Set of supported calendar codes.
	 */
	public static Set<String> getSupportedCalendarCodes() {
		Set<String> supportedCalendars = new HashSet<String>();
		for (HolidayCalendar c : HolidayCalendar.values()) {
			supportedCalendars.add(c.getId());
		}
		return supportedCalendars;
	}

	/**
	 * <p>
	 * Getter for the field <code>properties</code>.
	 * </p>
	 * 
	 * @return the configuration properties
	 */
	protected Properties getProperties() {
		return properties;
	}

	/**
	 * <p>
	 * Setter for the field <code>properties</code>.
	 * </p>
	 * 
	 * @param properties
	 *            the configuration properties to set
	 */
	protected void setProperties(Properties properties) {
		this.properties.putAll(properties);
	}

	/**
	 * Returns the holidays for the requested year and hierarchy structure.
	 * 
	 * @param year
	 *            i.e. 2010
	 * @param args
	 *            i.e. args = {'ny'}. returns US/New York holidays. No args ->
	 *            holidays common to whole country
	 * @return the list of holidays for the requested year
	 */
	abstract public Set<Holiday> getHolidays(int year, String... args);

	/**
	 * Returns the holidays for the requested interval and hierarchy structure.
	 * 
	 * @param interval
	 *            the interval in which the holidays lie.
	 * @param args
	 *            a {@link java.lang.String} object.
	 * @return list of holidays within the interval
	 */
	abstract public Set<Holiday> getHolidays(ReadableInterval interval, String... args);

	/**
	 * Initializes the implementing manager for the provided calendar.
	 * 
	 * @param calendar
	 *            i.e. us, uk, de
	 */
	abstract public void init(String calendar);

	/**
	 * Initializes the implementing manager for the provided URL.
	 * 
	 * @param resource
	 *            the URL, to a file containing the calendar
	 *            <p style="color:red;font-style:italic">
	 *            Note 1: This can be omitted, in which case the default
	 *            behavior of loading from the classpath with a specific name
	 *            will be used
	 *            </p>
	 *            <p style="color:red;font-style:italic">
	 *            Note 2: If this parameter is not omitted, then it may be a
	 *            path to a file (relative or absolute) or a URL spec (such as
	 *            http://somehos/somefile, or ftp://somehost/somefile or
	 *            whatever is supported by the URL Stream Handlers installed on
	 *            the JVM)
	 *            </p>
	 * 
	 */
	abstract public void init(final URL resource);

	/**
	 * Returns the configured hierarchy structure for the specific manager. This
	 * hierarchy shows how the configured holidays are structured and can be
	 * retrieved.
	 * 
	 * @return Current calendars hierarchy
	 */
	abstract public CalendarHierarchy getCalendarHierarchy();

}
