package com.rest.service.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageConfig {
	@Value("${validation.messages.path}")
	private String messagesPath;

	@Value("${validation.messages.reload-seconds:60}")
	private int reloadSeconds;

	@Bean
	MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		String[] basenames = getMessageBasenames();
		if (basenames.length > 0) {
			messageSource.setBasenames(basenames);
		} else {
			messageSource.setBasenames("classpath:messages/default");
		}
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(reloadSeconds);
		messageSource.setFallbackToSystemLocale(true);
		return messageSource;
	}

	@Bean
	LocalValidatorFactoryBean getValidator() {
		LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
		bean.setValidationMessageSource(messageSource());
		return bean;
	}

	private String[] getMessageBasenames() {
		List<String> basenames = new ArrayList<>();
		File messagesDir = new File(messagesPath);
		if (messagesDir.exists() && messagesDir.isDirectory()) {
			File[] propertyFiles = messagesDir
					.listFiles((dir, name) -> name.endsWith(".properties") && !name.contains("_"));
			if (propertyFiles != null) {
				for (File file : propertyFiles) {
					String filename = file.getName();
					String basename = filename.substring(0, filename.lastIndexOf('.'));
					basenames.add("file:" + messagesPath + "/" + basename);
				}
			}
		}
		return basenames.toArray(new String[0]);
	}
}
