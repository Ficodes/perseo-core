/**
* Copyright 2015 Telefonica Investigación y Desarrollo, S.A.U
*
* This file is part of perseo-core project.
*
* perseo-core is free software: you can redistribute it and/or modify it under the terms of the GNU
* General Public License version 2 as published by the Free Software Foundation.
*
* perseo-core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
* implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
*
* You should have received a copy of the GNU General Public License along with perseo-core. If not, see
* http://www.gnu.org/licenses/.
*
* For those usages not covered by the GNU General Public License please contact with
* iot_support at tid dot es
*/

package com.telefonica.iot.perseo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author brox
 */
public final class Configuration {

    private Configuration() {
        super();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    private static final String PATH = "/etc/perseo-core.properties";
    private static final String ACTION_URL_PROP = "action.url";
    private static final String MAX_AGE_PROP = "rule.max_age";
    private static final long DEFAULT_MAX_AGE_PROP = 60000;
    private static final Properties PROPERTIES = new Properties();
    private static String actionRule;
    private static long maxAge = DEFAULT_MAX_AGE_PROP;

    static {
        LOGGER.debug("Configuration init: " + reload());
    }

    /**
     * Reads configuration file, and so refresh configuration.
     *
     * @return true if success, false otherwise
     */
    public static synchronized boolean reload() {
        LOGGER.info("Configuration is being reloaded");
        PROPERTIES.clear();
        InputStream stream;
        try {
            stream = new FileInputStream(PATH);
            PROPERTIES.load(stream);
            stream.close();
        } catch (IOException e) {
            LOGGER.warn("reload: " + e.getMessage());
        }

        actionRule = PROPERTIES.getProperty(ACTION_URL_PROP, System.getenv("PERSEO_FE_URL")) + "/actions/do";
        //Check maxAge numerical value
        String max_age_prop = PROPERTIES.getProperty(MAX_AGE_PROP, System.getenv("MAX_AGE"));

        try {
            maxAge = Long.parseLong(max_age_prop);
        } catch (NumberFormatException nfe) {
            maxAge = DEFAULT_MAX_AGE_PROP;
            if (max_age_prop != null && max_age_prop != "") {
                LOGGER.error("Invalid configuration value for " + MAX_AGE_PROP + ": " + nfe);
            }
        }

        return true;
    }

    /**
     *
     * @return URL to send actions (fired events by a rule)
     */
    public static synchronized String getActionURL() {
        return actionRule;
    }

    /**
     *
     * @return max age for keeping a rule that exist before a complete refresh
     * but it is not included in the new set. In case of error returns 0, so the
     * unexpected rules will be deleted immediately.
     */
    public static synchronized long getMaxAge() {
        return maxAge;
    }
}
