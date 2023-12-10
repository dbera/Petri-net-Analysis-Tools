package org.eclipse.petrinets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


class NetState
{
	//public HashMap<String,Integer> m;
	public Marking m;
	
	NetState() {
		//m = new HashMap<String,Integer>();
		m = new Marking();
	}
	
	NetState(NetState s) {
		//m = new HashMap<String,Integer>();
		m = new Marking();
		for(String key : s.m.keySet()) {
			m.put(key, s.m.get(key));
		}
	}
	
	boolean isEqual(NetState s) {
		for(String key : s.m.keySet()) {
			if(m.containsKey(key)) {
				if(!m.get(key).equals(s.m.get(key))) 
					return false;
			}
			else return false;
		}		
		return true;
	}
}

/*class NetState
{
	public Marking m;
	//public NetVariables smVarContainer;
	
	NetState() {
		m = new Marking();
		//smVarContainer = new NetVariables();
	}
	
	NetState(NetState s) {
		m = new Marking();
		   Iterator<Entry<String, Integer>> it = s.m.entrySet().iterator();
		   while (it.hasNext()) {
		       Marking.Entry<String, Integer> pair = (Marking.Entry<String, Integer>)it.next();
		       m.put(pair.getKey(), pair.getValue());
		   }
		//smVarContainer = new NetVariables(s.smVarContainer);
		
	}
}*/

class Marking extends HashMap<String, Integer> 
{
	private static final long serialVersionUID = 1L;
	
	/*public static void printMarking(Marking m1) {
		System.out.println("--------- START MARKING ---------");
		   Iterator<Entry<String, Integer>> it = m1.entrySet().iterator();
		   while (it.hasNext()) {
		       Marking.Entry<String, Integer> pair = (Marking.Entry<String, Integer>)it.next();
		       if(pair.getValue() != 0)
		       	System.out.println("Place: " + pair.getKey() + " => " + pair.getValue() + " Tokens");
		       //it.remove(); // avoids a ConcurrentModificationException
		   }
		   System.out.println("---------- END MARKING ----------");
	}
	
	public boolean isEqual(Marking m1, Marking m2) {
	    Iterator<Entry<String, Integer>> it1 = m1.entrySet().iterator();
	    
	    if(m1.size() != m2.size()) return false;
	    
	    while (it1.hasNext()) {
	        Marking.Entry<String, Integer> pair1 = (Marking.Entry<String, Integer>)it1.next();
	        if(!m2.containsKey(pair1.getKey())) return false;
	        if(m2.get(pair1.getKey()) != pair1.getValue()) return false;
	    }
	    return true;
	}*/
}

//Iterator<Entry<String, Integer>> it2 = m2.entrySet().iterator();
//Marking.Entry<String, Integer> pair2 = (Marking.Entry<String, Integer>)it2.next();
//System.out.println(pair1.getKey() + " = " + pair1.getValue());
//System.out.println(pair2.getKey() + " = " + pair2.getValue());
