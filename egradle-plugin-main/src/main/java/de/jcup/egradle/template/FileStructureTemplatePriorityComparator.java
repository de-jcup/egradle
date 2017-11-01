package de.jcup.egradle.template;

import java.util.Comparator;

public class FileStructureTemplatePriorityComparator implements Comparator<FileStructureTemplate> {

	@Override
	public int compare(FileStructureTemplate o1, FileStructureTemplate o2) {
		if (o1==null){
			if (o2 ==null) {
				return 0;
			}
			return -1;
		}
		if (o2 ==null) {
			return 1;
		}
		int diff = o1.getPriority()-o2.getPriority();
		if (diff ==0){
			return 0;
		}
		if (diff>0) {
			return 1;
		}
		return -1;
	}

}
