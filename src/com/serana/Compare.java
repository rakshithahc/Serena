package com.serana;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import android.util.Log;

public class Compare {
	Hashtable<String,Integer> table;
	int[] ids;
	public void result(){
		table=new Hashtable<String,Integer>();
		table.put("mock1",R.drawable.mock1);
		table.put("mock2",R.drawable.mock2);
		table.put("mock3",R.drawable.mock3);
		table.put("mock4",R.drawable.mock4);
		table.put("mock5",R.drawable.mock5);
		table.put("mock6",R.drawable.mock6);
		table.put("mock7",R.drawable.mock7);
		table.put("mock8",R.drawable.mock8);
		table.put("mock9",R.drawable.mock9);
		table.put("mock10",R.drawable.mock10);
		table.put("mock11",R.drawable.mock11);
		table.put("mock12",R.drawable.mock12);
		table.put("mock13",R.drawable.mock13);
		table.put("mock14",R.drawable.mock14);
		table.put("mock15",R.drawable.mock15);
			}
	public void resultXlarge(){
		table=new Hashtable<String,Integer>();
		table.put("mock1",R.drawable.mock1x);
		table.put("mock2",R.drawable.mock2x);
		table.put("mock3",R.drawable.mock3x);
		table.put("mock4",R.drawable.mock4x);
		table.put("mock5",R.drawable.mock5x);
		table.put("mock6",R.drawable.mock6x);
		table.put("mock7",R.drawable.mock7x);
		table.put("mock8",R.drawable.mock8x);
		table.put("mock9",R.drawable.mock9x);
		table.put("mock10",R.drawable.mock10x);
		table.put("mock11",R.drawable.mock11x);
		table.put("mock12",R.drawable.mock12x);
		table.put("mock13",R.drawable.mock13x);
		table.put("mock14",R.drawable.mock14x);
		table.put("mock15",R.drawable.mock15x);
			}
	
	public int[] backgrounds(String size){
		if(size.equalsIgnoreCase("xlarge")){
			resultXlarge();
		}
		else if(size.equalsIgnoreCase("medium")){
		result();
		}

	ArrayList<String> colo=MyApplication.mockColors;
	ids=new int[table.size()];
     System.out.println("length"+colo.size());
	System.out.println(colo.toString());
	for(int y=0;y<table.size();y++){
	for (Entry<String, Integer> e : table.entrySet()) {
	    String key = e.getKey();
	    Object value = e.getValue();
	    Log.v(key, value.toString());
	    if(e.getKey().equalsIgnoreCase(colo.get(y))){
		    ids[y]=(Integer) value;

	    }
	}}
	

    return ids;

	}
	}
