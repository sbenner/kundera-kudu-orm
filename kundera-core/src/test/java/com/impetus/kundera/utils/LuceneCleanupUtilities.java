/**
 * Copyright 2012 Impetus Infotech.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.impetus.kundera.utils;

import com.impetus.kundera.PersistenceProperties;
import com.impetus.kundera.metadata.model.PersistenceUnitMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Provides utility methods for all Kundera Examples test cases
 *
 * @author amresh.singh
 */
public class LuceneCleanupUtilities {
    /** The log. */
    private static Logger log = LoggerFactory.getLogger(LuceneCleanupUtilities.class);

    public static void cleanLuceneDirectory(PersistenceUnitMetadata puMetadata) {
        if (puMetadata != null) {
            String luceneDir = puMetadata.getProperty(PersistenceProperties.KUNDERA_INDEX_HOME_DIR);
            cleanDir(luceneDir);
        }
    }

    public static void cleanDir(final String luceneDir) {
        if (luceneDir != null && luceneDir.length() > 0) {
            File directory = new File(luceneDir);
            // Get all files in directory
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && !(file.list().length == 0)) {

                        cleanDir(file.getPath());
                        file.delete();
                    } else {
                        file.delete();
                    }

                }
            }
        }
    }

}
