package de.jollyday.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

import java.util.Properties;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProviderManagerTest {

	@Mock
	ConfigurationProvider defaultConfigurationProvider;
	@Mock
	ConfigurationProvider urlConfigurationProvider;

	@InjectMocks
	ConfigurationProviderManager configurationProviderManager = new ConfigurationProviderManager();

	@After
	public void teardown() {
		System.clearProperty(ConfigurationProvider.CONFIG_PROVIDERS_PROPERTY);
	}

	@Test
	public void testGetPropertiesWithEmptyProvidersList() {
		System.setProperty(ConfigurationProvider.CONFIG_PROVIDERS_PROPERTY, "");
		Properties resultProperties = configurationProviderManager.getConfigurationProperties(null);
		assertResult(resultProperties);
	}

	@Test
	public void testGetPropertiesWithWrongClass() {
		System.setProperty(ConfigurationProvider.CONFIG_PROVIDERS_PROPERTY, "java.lang.String");
		Properties resultProperties = configurationProviderManager.getConfigurationProperties(null);
		assertResult(resultProperties);
	}

	@Test
	public void testGetPropertiesWithCorrectClass() {
		System.setProperty(ConfigurationProvider.CONFIG_PROVIDERS_PROPERTY, getClass().getPackage().getName()
				+ ".TestProvider");
		Properties resultProperties = configurationProviderManager.getConfigurationProperties(null);
		assertResult(resultProperties);
		assertEquals("Wrong value for property: key", "value", resultProperties.getProperty("key"));
	}

	@Test
	public void testGetPropertiesWithWrongAndCorrectClass() {
		System.setProperty(ConfigurationProvider.CONFIG_PROVIDERS_PROPERTY, getClass().getPackage().getName()
				+ ".TestProvider,java.lang.String");
		Properties resultProperties = configurationProviderManager.getConfigurationProperties(null);
		assertResult(resultProperties);
		assertEquals("Wrong value for property: key", "value", resultProperties.getProperty("key"));
	}

	@Test
	public void testGetPropertiesWithManualOverride() {
		Properties manualProps = new Properties();
		manualProps.setProperty("MANUAL_KEY", "MANUAL_VALUE");
		manualProps.setProperty("manager.impl", "NewImpl");
		Properties resultProperties = configurationProviderManager.getConfigurationProperties(manualProps);
		assertResult(resultProperties);
		assertEquals("Wrong value for property: MANUAL_KEY", "MANUAL_VALUE", resultProperties.getProperty("MANUAL_KEY"));
		assertEquals("Wrong value for property: manager.impl", "NewImpl", resultProperties.getProperty("manager.impl"));
	}

	private void assertResult(Properties resultProperties) {
		assertNotNull(resultProperties);
		verify(defaultConfigurationProvider).putConfiguration(resultProperties);
		verify(urlConfigurationProvider).putConfiguration(resultProperties);
	}

}
