package com.example.utils;


import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;

public class Base64ImageEncode {
	private static volatile Base64ImageEncode INSTANCE;
	private Queue<String> imageString ;
	private Base64ImageEncode() {
		// TODO Auto-generated constructor stub
		imageString = new LinkedList<String>();
	}
	 public static Base64ImageEncode getInstance() {
	        if (INSTANCE == null) {
	            synchronized (Base64ImageEncode.class) {
	                if (INSTANCE == null) {
	                    INSTANCE = new Base64ImageEncode();
	                }
	            }
	        }
	        return INSTANCE;
	    }
	public String getIamgeString(){
			String imgStr;
			synchronized (imageString) {
				imgStr =imageString.poll();
			}
			return imgStr;
		}
	public void setIamgeString(String imageStr){
		synchronized (imageString) {
			imageString.add(imageStr);
		}
	}
}
