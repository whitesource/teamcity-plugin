/**
 * Copyright (C) 2012 White Source Ltd.
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
 */
package org.whitesource.teamcity.agent;

import jetbrains.buildServer.util.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

//import jetbrains.buildServer.util.StringUtil;

/**
 * @author Edo.Shor
 */
public class GenericOssInfoExtractorTest {

    @Test
    public void testFilesetsLogic() throws URISyntaxException, UnsupportedEncodingException {

        File rootDir = new File(getClass().getResource("/").getPath());
        rootDir = rootDir.getParentFile().getParentFile();

        List<String> includes = Arrays.asList("**/teamcity/**/*.java");
//                Arrays.asList("main/**/*.java", "*.xml");

        List<File> files = new ArrayList<File>();
        for (String include : includes) {
            String regex = FileUtil.convertAntToRegexp(include);
            //".*main.*/*.java";
//                    FileUtil.convertAntToRegexp(include);

//            rootDir.li


            collectMatchedFiles(rootDir, Pattern.compile(regex), files);
        }

        System.out.println(rootDir.getAbsolutePath());
        System.out.println("----------------------");
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }
    }

    public void collectMatchedFiles(final File root, final Pattern pattern, final List<File> files) {
        collectMatchedFiles(root, root, pattern, files);
    }

    private void collectMatchedFiles(final File absoluteRoot, final File root, final Pattern pattern, final List<File> files) {
        final File[] dirs = root.listFiles();
        if (dirs == null) return;
        for (File dir : dirs) {
            if (dir.isFile()) {
                String relativePath = FileUtil.getRelativePath(absoluteRoot, dir);
                final String path = FileUtil.toSystemIndependentName(relativePath);
                if (pattern.matcher(path).matches()) {
                    files.add(dir);
                }
            } else {
                collectMatchedFiles(absoluteRoot, dir, pattern, files);
            }
        }
    }

}
