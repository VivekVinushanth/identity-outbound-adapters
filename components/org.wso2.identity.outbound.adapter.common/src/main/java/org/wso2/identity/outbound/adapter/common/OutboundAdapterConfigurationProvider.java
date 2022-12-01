/*
 * Copyright (c) 2022, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.identity.outbound.adapter.common;

import org.apache.commons.lang.StringUtils;
import org.wso2.identity.outbound.adapter.common.exception.AdapterConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Properties;

import static java.util.Optional.ofNullable;

/**
 * Class to build the output adapter configurations.
 */
public class OutboundAdapterConfigurationProvider {

    private static final String CONFIG_FILE_NAME = "identity-outbound-adapter.properties";
    private static final String CARBON_HOME = "carbon.home";
    private static final String REPOSITORY_DIR = "repository";
    private static final String CONF_DIR = "conf";
    private static final String IDENTITY_DIR = "identity";
    private static OutboundAdapterConfigurationProvider instance;
    private final Properties adapterProperties;

    private OutboundAdapterConfigurationProvider() throws AdapterConfigurationException {

        adapterProperties = this.loadProperties();
    }

    public static OutboundAdapterConfigurationProvider getInstance() throws AdapterConfigurationException {

        if (instance == null) {
            instance = new OutboundAdapterConfigurationProvider();
        }
        return instance;
    }

    private Properties loadProperties() throws AdapterConfigurationException {

        Properties properties = new Properties();

        Path path = Paths.get(System.getProperty(CARBON_HOME), REPOSITORY_DIR, CONF_DIR, IDENTITY_DIR)
                .resolve(CONFIG_FILE_NAME);

        if (Files.notExists(path)) {
            throw new AdapterConfigurationException(CONFIG_FILE_NAME + " configuration file doesn't exist.");
        }

        FileChannel channel;
        try {
            channel = FileChannel.open(path, StandardOpenOption.READ);

            try (InputStream inputStream = Channels.newInputStream(channel)) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            throw new AdapterConfigurationException("Error while retrieving the configuration file.", e);
        }

        return properties;
    }

    public Optional<String> getProperty(String propertyName) {

        return ofNullable(this.adapterProperties.getProperty(propertyName)).filter(StringUtils::isNotBlank);
    }

}