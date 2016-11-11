package com.vp.loveu.channel.utils;

import android.animation.TypeEvaluator;



public class MyTransEvaluator implements TypeEvaluator<Integer> {

	@Override
	public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
		
		return (int) (startValue*fraction);
	} 
}
