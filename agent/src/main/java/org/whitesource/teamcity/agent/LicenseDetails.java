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

/**
 * Details object for license. 
 * 
 * @author tom.shapira
 */
public class LicenseDetails {
	
	/* --- Static members --- */
	
	private static final int LICENSE_NAME_MAX_LENGTH = 16;
	
	/* --- Members --- */

	private String name;
	
	private int occurrences;
	
	private String color;
	
	private String height;
	
	/* --- Constructors --- */
	
	/**
	 * Default constructor
	 */
	public LicenseDetails() {
		this.occurrences = 0;
	}

	/* --- Getters / Setters --- */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) {
		this.occurrences = occurrences;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height + "px";
	}

	public String getShortName() {
		String shortName = name;
		if (name.length() > LICENSE_NAME_MAX_LENGTH) {
			shortName = name.substring(0, LICENSE_NAME_MAX_LENGTH - 2) + "..";
		}
		return shortName;
	}

}
