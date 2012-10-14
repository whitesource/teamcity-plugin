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
package org.whitesource.teamcity.common;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit test for WssUtils.
 *
 * @author Edo.Shor
 */
public class WssUtilsTest {

    @Test
    public void testSplitParameters() {
        List<String> split = WssUtils.splitParameters("param1,param2,param3");
        assertEquals(3, split.size());
        assertEquals("param1", split.get(0));
        assertEquals("param2", split.get(1));
        assertEquals("param3", split.get(2));

        split = WssUtils.splitParameters("param1,  ,  param2,   param3");
        assertEquals(3, split.size());
        assertEquals("param1", split.get(0));
        assertEquals("param2", split.get(1));
        assertEquals("param3", split.get(2));

        split = WssUtils.splitParameters("param1\nparam2\r\nparam3\rparam4\n\n\r\r\n");
        assertEquals(4, split.size());
        assertEquals("param1", split.get(0));
        assertEquals("param2", split.get(1));
        assertEquals("param3", split.get(2));
        assertEquals("param4", split.get(3));

        split = WssUtils.splitParameters(null);
        assertNotNull(split);
        assertTrue(split.isEmpty());
    }

    @Test
    public void testSplitParametersMap() {

        Map<String,String> params = WssUtils.splitParametersMap(
                "param1=val1,param11=val11\nparam2=val2,param22=val22\r\nparam3=val3\rparam4=val4");

        assertEquals(6, params.size());
        assertEquals("val1", params.get("param1"));
        assertEquals("val11", params.get("param11"));
        assertEquals("val2", params.get("param2"));
        assertEquals("val22", params.get("param22"));
        assertEquals("val3", params.get("param3"));
        assertEquals("val4", params.get("param4"));

        params = WssUtils.splitParametersMap(
                "param1=val1,param11=val11,param111\nparam2=val2,param22=val22,param222=222=val222\r\nparam3=val3\rparam4=val4");

        assertEquals(6, params.size());
        assertEquals("val1", params.get("param1"));
        assertEquals("val11", params.get("param11"));
        assertEquals("val2", params.get("param2"));
        assertEquals("val22", params.get("param22"));
        assertEquals("val3", params.get("param3"));
        assertEquals("val4", params.get("param4"));

        params = WssUtils.splitParametersMap(null);
        assertNotNull(params);
        assertTrue(params.isEmpty());
    }
}
