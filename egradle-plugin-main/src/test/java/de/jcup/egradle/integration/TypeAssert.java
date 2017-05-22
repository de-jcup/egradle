/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
 package de.jcup.egradle.integration;

import static org.junit.Assert.*;

import java.util.Iterator;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;

public class TypeAssert {

	public static TypeAssert assertType(Type type){
		assertNotNull("Type may not be null!", type);
		return new TypeAssert(type);
	}

	private Type type;
	

	private TypeAssert(Type type) {
		this.type=type;
	}
	public TypeAssert hasMethods(){
		if (type.getMethods().isEmpty()){
			fail("Type:"+type.getName()+" has no methods at all!");
		}
		return this;
	}
	public TypeAssert hasMethod(String name, String ...paramTypes){
		return hasMethodOrNot(true, name, paramTypes);
	}
	
	public TypeAssert hasNotMethod(String name, String ...paramTypes){
		return hasMethodOrNot(false, name, paramTypes);
	}
	
	private TypeAssert hasMethodOrNot(boolean expected ,String name, String ...paramTypes){
		if (name==null){
			throw new IllegalArgumentException("wrong usage in test case, name may never be null!");
		}
		if (expected){
			hasMethods();
		}else {
			if (type.getMethods().isEmpty()){
				/* no method so not also this one*/
				return this;
			}
		}
		for (Method m: type.getMethods()){
			if (!name.equals(m.getName())){
				continue;
			}
			if (paramTypes==null || paramTypes.length==0){
				if (m.getParameters().isEmpty()){
					/* ok - found without paramaters as wished*/
					return this;
				}
				/* not found */
				continue;
			}
			if (paramTypes.length!=m.getParameters().size()){
				/* not same length of params*/
				continue;
			}
			Iterator<Parameter> it = m.getParameters().iterator();
			boolean same=true;
			for (String paramType: paramTypes){
				Parameter nextMethodParameter = it.next();
				String nextParamType = nextMethodParameter.getTypeAsString();
				if (!paramType.equals(nextParamType)){
					same=false;
					break;
				}
			}
			if (same){
				if (expected){
					/* all params same - ok found */
					return this;
				}
				
				StringBuilder sb = new StringBuilder();
				sb.append("In type ").append(type.getName());
				sb.append(" exists a method like ");
				failOnMethod(name, sb, paramTypes);
				return null;
				
			}
		}
		if (!expected){
			return this;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("In type ").append(type.getName());
		sb.append(" exists no method like ");
		failOnMethod(name, sb, paramTypes);
		return null;
	}
	
	private void failOnMethod(String name, StringBuilder sb, String... paramTypes) {
		sb.append(name);
		sb.append("(");
		if (paramTypes!=null){
			boolean first=true;
			for (String param: paramTypes){
				if (!first){
					sb.append(",");
				}
				first=false;
				sb.append(param);
			}
		}
		sb.append(")");
		fail(sb.toString());
	}
	public TypeAssert hasName(String name) {
		if (name==null){
			throw new IllegalArgumentException("wrong usage in test case, name may never be null!");
		}
		assertEquals("Type name differs!", name,type.getName());
		return this;
	}
	
	public TypeAssert hasSuperType() {
		assertNotNull("super type not set!", type.getSuperTypeAsString());
		return this;
	}
	
	public TypeAssert hasSuperType(String superTypeAsString) {
		hasSuperType();
		assertEquals("Not expected super type!", superTypeAsString,type.getSuperTypeAsString());
		return this;
	}
	public TypeAssert hasProperty(String propertyName) {
		boolean foundProperty=false;
		for (Property p : type.getProperties()){
			if (p.getName().equals(propertyName)){
				foundProperty=true;
				break;
			}
		}
		if (!foundProperty){
			fail("Did not found property :"+propertyName+" in type:"+type.getName());
		}
		return this;
	}
	
	/**
	 * Assert type has given interface and interface type is not null
	 * @param interfaceName
	 * @return assert object
	 */
	public TypeAssert hasInterface(String interfaceName) {
		if (interfaceName==null){
			throw new IllegalArgumentException("Testcase corrupt: interface name may not be null");
		}
		boolean found = type.isImplementingInterface(interfaceName);
		
		if (!found){
			fail("Did not found interface :"+interfaceName+" in type:"+type.getName());
		}
		return this;
	}
	
	public TypeAssert isInterface() {
		return isInterface(true);
	}
	
	public TypeAssert isNotInterface() {
		return isInterface(false);
	}
	
	
	private TypeAssert isInterface(boolean expected) {
		boolean isInterface = type.isInterface();
		if (isInterface == expected){
			return this;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Type: ").append(type.getName()).append(" is ");
		if (expected){
			sb.append("not an interface!");
		}else{
			sb.append("an interface!");
		}
		fail(sb.toString());
		return this;
	}

	public TypeAssert isDecendantOf(String parentType) {
		if (!(type.isDescendantOf(parentType))){
			StringBuilder sb = new StringBuilder();
			sb.append("Type:").append(type.getName()).append("is not a descendant of:").append(parentType);
			fail(sb.toString());
		}
		return this;
	}

}
