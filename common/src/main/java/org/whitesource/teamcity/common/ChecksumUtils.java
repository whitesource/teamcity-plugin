/*
 * Copyright (C) 2011 JFrog Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file contains modifications to the original work made by White Source Ltd. 2012.
 */

package org.whitesource.teamcity.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to calculate SHA-1 hash codes for files.
 *
 * @author Noam Y. Tenne (Original)
 * @author Edo.Shor (White Source)
 */
public final class ChecksumUtils {

    /* --- Static members --- */

    private static final int BUFFER_SIZE = 32768;

    /* --- Static methods --- */

    /**
     * Calculates the given file's SHA-1 hash code.
     *
     * @param fileToCalculate File to calculate
     * @return Calculated SHA-1 for the given file.
     *
     * @throws IOException              Thrown if any error occurs while reading the file or calculating the checksums
     * @throws IllegalArgumentException Thrown if the given file to calc is null or non-existing or the algorithms var
     *                                  args is null
     */
    public static String calculateSHA1(File fileToCalculate) throws IOException {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        FileInputStream inputStream = new FileInputStream(fileToCalculate);

        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            int size = inputStream.read(buffer, 0, BUFFER_SIZE);
            while (size >= 0) {
                messageDigest.update(buffer, 0, size);
                size = inputStream.read(buffer, 0, BUFFER_SIZE);
            }
        } finally {
            inputStream.close();
        }

        StringBuilder sb = new StringBuilder();

        byte[] bytes = messageDigest.digest();
        for (byte aBinaryData : bytes) {
            String t = Integer.toHexString(aBinaryData & 0xff);
            if (t.length() == 1) {
                sb.append("0");
            }
            sb.append(t);
        }

        return sb.toString().trim();
    }
}
