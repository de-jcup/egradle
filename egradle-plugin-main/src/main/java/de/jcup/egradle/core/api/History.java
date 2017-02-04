package de.jcup.egradle.core.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A simple history implementation with an maximum amount of history entries
 * @author Albert Tregnaghi
 *
 * @param <T>
 */
public class History<T>{
	
		private List<T> list;
		private int max;
		
		public History(int max){
			this.max=max;
			this.list=new ArrayList<>(max+1);
		}
		/**
		 * @return maximum count of elements in history
		 */
		public int getMax() {
			return max;
		}
		
		/**
		 * @return current count of elements in history
		 */
		public int getCount(){
			return list.size();
		}
		
		/**
		 * Add entry. if maximum reached last entry will be removed
		 * @param content value to add, <code>null</code> is ignored
		 */
		public void add(T content){
			if (content==null){
				return;
			}
			if (max==0){
				return;
			}
			if (list.size()==max){
				list.remove(0);
			}
			list.add(content);
		}
		
		/**
		 * Returns last history entry and removes it from history, or when no more entry available <code>null</code>
		 * @return entry or <code>null</code>
		 */
		public T goBack(){
			int size = list.size();
			if (size==0){
				return null;
			}
			T historyContent = list.remove(size-1);
			return historyContent;
		}
		
		/**
		 * Clears all history content
		 */
		public void clear(){
			list.clear();
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("History (");
			sb.append(list.size());
			sb.append('/');
			sb.append(max);
			sb.append("):\n");
			int i=0;
			for (T data: list){
				sb.append(i);
				sb.append('=');
				sb.append(StringUtils.abbreviate(Objects.toString(data),40));
				sb.append("\n");
			}
			return sb.toString();
		}
	}