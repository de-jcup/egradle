package de.jcup.egradle.other;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class ASMClassInspector {
	// https://jaxenter.de/classpath-scan-im-eigenbau-aus-der-java-trickkiste-12963
	public static void main(String[] args) throws FileNotFoundException, IOException {
		File file = new File("C:/dev_custom/projects/JCUP/gradle/subprojects/core/build/classes/main/org/gradle/api/Task.class");
		new ASMClassInspector().inspect(new FileInputStream(file));
	}
	
	public ASMClassMetaData inspect(InputStream inputStream) throws IOException{
		ClassReader reader = new ClassReader(inputStream);
		int flags=0;
		EGradleClassVisitor classVisitor = new EGradleClassVisitor();
		reader.accept(classVisitor, flags);
		return null;
	}
	
	class EGradleClassVisitor extends ClassVisitor{
		String className;
		
		EGradleClassVisitor() {
			super(Opcodes.ASM5);
		}
		
		@Override
		public void visit(int version, int access, String name, String signature, String superName,
				String[] interfaces) {
			className = name.replace('/', '.');
			System.out.println("className="+className);
		}
		@Override
		public void visitSource(String source, String debug) {
			if (source!=null){
				System.out.println("visit source:"+source+", debug="+debug);
			}
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			boolean isPublic = (access & Opcodes.ACC_PUBLIC)==Opcodes.ACC_PUBLIC;
			if (isPublic){
				System.out.println("visit:"+name+ (signature==null ? "()":signature));
			}
			return null;
		}
	}
}
