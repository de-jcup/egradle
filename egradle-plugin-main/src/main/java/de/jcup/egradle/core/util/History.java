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
 package de.jcup.egradle.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
		 * Returns last entry but does not influence history. Element will stay on stack
		 * @return current or <code>null</code> if not existing.
		 */
		public T current(){
			if (list.size()==0){
				return null;
			}
			return list.get(list.size()-1);
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
		
		/**
		 * Returns list of current items inside history. The list is ordered from last to first
		 * @return a list of all items. 
		 */
		public List<T> toList() {
			List<T> toList = new ArrayList<>(list);
			Collections.reverse(toList);
			return toList;
		}
	}