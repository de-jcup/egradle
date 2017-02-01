package de.jcup.egradle.core.api;

import java.util.ArrayList;
import java.util.List;

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
		 * Returns history entry, or when no more entry available <code>null</code>
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
	}