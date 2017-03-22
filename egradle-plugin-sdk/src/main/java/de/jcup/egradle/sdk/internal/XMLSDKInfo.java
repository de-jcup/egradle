/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
 package de.jcup.egradle.sdk.internal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.jcup.egradle.sdk.SDKInfo;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "sdk")
public class XMLSDKInfo implements SDKInfo {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

	@XmlAttribute(name = "version")
	private String sdkVersion;

	@XmlElement(name = "gradleVersion")
	private String gradleVersion;

	@XmlElement(name = "installationDate")
	private String installationDate;

	@XmlElement(name = "creationDate")
	private String creationDate;

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public void setGradleVersion(String gradleVersion) {
		this.gradleVersion = gradleVersion;
	}

	public void setInstallationDate(Date date) {
		installationDate = DATE_FORMAT.format(date);
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = DATE_FORMAT.format(creationDate);
	}

	public String getGradleVersion() {
		return gradleVersion;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public Date getInstallationDate() {
		return getDateObjectFromString(installationDate);
	}

	public Date getCreationDate() {
		return getDateObjectFromString(creationDate);
	}

	private Date getDateObjectFromString(String dateAsString) {
		try {
			return DATE_FORMAT.parse(dateAsString);
		} catch (ParseException e) {
			return null;
		}
	}

}
