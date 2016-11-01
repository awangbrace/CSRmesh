/**
 * File Name                   : List������
 * Author                      : Jason
 * Version                     : 1.0.0
 * Date                        : 2015/10/8
 * Revision History            : 
 * Copyright (c) 2015-16 Axalent Solutions (Shanghai) Co., Ltd. 
 * All rights reserved.
 */
package com.axalent.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListUtils {
	
	/**
	 * jdk 1.7��Ҫ����,��ʾʹ����ǰ�汾��sort������  ����Ȼ�ᱨ�� java.lang.IllegalArgumentException: Comparison method violates its general contract!
	 * @param list 
	 */
	@SuppressWarnings("unchecked")
	public static void sort(List<?> list, Comparator comparator) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");  
		Collections.sort(list, comparator);
	}
	
	

}
