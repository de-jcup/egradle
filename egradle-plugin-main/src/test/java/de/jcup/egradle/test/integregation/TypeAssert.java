package de.jcup.egradle.test.integregation;

import static org.junit.Assert.*;

import java.util.Iterator;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeReference;

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
		if (name==null){
			throw new IllegalArgumentException("wrong usage in test case, name may never be null!");
		}
		hasMethods();
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
				/* all params same - ok found */
				return this;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("In type ").append(type.getName());
		sb.append(" exists no method like ");
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
		return null;
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
		boolean found = false;
		for (TypeReference ref: type.getInterfaces()){
			if (interfaceName.equals(ref.getTypeAsString())){
				found=true;
				/* check type is set...*/
				assertNotNull("Did found interface reference, but references does not contain type but null!",ref.getType());
			}
		}
		if (!found){
			fail("Did not found interface :"+interfaceName+" in type:"+type.getName());
		}
		return this;
	}

}
