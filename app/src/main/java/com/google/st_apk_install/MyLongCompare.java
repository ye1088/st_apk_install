package com.google.st_apk_install;

import java.util.Comparator;

public class MyLongCompare implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		long i1 = ((Long)o1).longValue();
		long i2 = ((Long)o2).longValue();
		if (i1 < i2){
			return 1;
		}
		if (i1 > i2){
			return -1;
		}
		return 0;
	}

}
