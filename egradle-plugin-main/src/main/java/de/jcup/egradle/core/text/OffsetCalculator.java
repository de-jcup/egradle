package de.jcup.egradle.core.text;

public class OffsetCalculator {

	private static final int UNKNOWN_OFFSET = -1;
	
	/**
	 * Calculates offset for given line and column in lines
	 * @param lines
	 * @param line
	 * @param column
	 * @return offset calculated or -1 when offset is unknown
	 */
	public int calculatetOffset(CharSequence[] lines, int line, int column) {
		if (lines==null){
			return UNKNOWN_OFFSET;
		}
		if (line > lines.length){
			return UNKNOWN_OFFSET;
		}
		int columnIndex= column-1;
		int lineIndex = line-1;
		CharSequence lastSequence = lines[lineIndex];
		if (column>lastSequence.length()){
			return UNKNOWN_OFFSET;
		}
		
		int offset = 0;
		for (int index=0;index<lines.length;index++){
			CharSequence sequence = lines[index]; 
			if (index==lineIndex){
				// ABC
				// D
				// ^---Offset of D ist not column(1) but former offset + column-1(0)
				offset+=columnIndex;
				break;
			}else{
				offset+=sequence.length();
			}
		}
		
		return offset;
	}

}
