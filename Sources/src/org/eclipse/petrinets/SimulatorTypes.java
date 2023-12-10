package org.eclipse.petrinets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

enum Type {
	INT,
	REAL,
	STRING,
	BOOL
}

enum Transition_Type {
	COMMAND,
	REPLY,
	SIGNAL,
	NOTIFICATION,
	INTERNAL
}

// Mapping index to Transition Names
class TransitionIndex
{
	private HashMap<String, Integer> transition_map;
	
	public TransitionIndex() {
		 transition_map = new HashMap<String, Integer>();
		 
		 // Add the transition and indexes
		 transition_map.put("Off_IVacuumExpCryo_Evacuate_1", 0);
		 transition_map.put("Off_IVacuumExpCryo_Abort_1", 1);
		 transition_map.put("Off_IVacuumExpCryo_State_1", 2);
		 transition_map.put("Off_IVacuumExpCryo_CryoCycleState_1", 3);
		 transition_map.put("Off_IVacuumExpCryo_Vent_1", 4);
		 transition_map.put("Off_IVacuumExpCryo_ColumnValvesState_1", 5);
		 transition_map.put("Off_IVacuumExpCryo_Start_1", 6);
		 transition_map.put("Off_IVacuumExpCryo_OpenColumnValves_1", 7);
		 transition_map.put("Off_IVacuumExpCryo_CloseColumnValves_1", 8);
		 transition_map.put("Off_IVacuumExpCryo_Start_1_reply_11", 9);
		 transition_map.put("Off_IVacuumExpCryo_State_1_reply_11", 10);
		 transition_map.put("Off_IVacuumExpCryo_Vent_1_reply_21", 11);
		 transition_map.put("Off_IVacuumExpCryo_Vent_1_reply_11", 12);
		 transition_map.put("Off_IVacuumExpCryo_Evacuate_1_reply_11", 13);
		 transition_map.put("Off_IVacuumExpCryo_Evacuate_1_reply_21", 14);
		 transition_map.put("Off_IVacuumExpCryo_OpenColumnValves_1_reply_11", 15);
		 transition_map.put("Off_IVacuumExpCryo_Abort_1_reply_11", 16);
		 transition_map.put("Off_IVacuumExpCryo_CloseColumnValves_1_reply_11", 17);
		 transition_map.put("Off_IVacuumExpCryo_CryoCycleState_1_reply_11", 18);
		 transition_map.put("Off_IVacuumExpCryo_ColumnValvesState_1_reply_11", 19);
		 transition_map.put("Ready_IVacuumExpCryo_CloseColumnValves_1", 20);
		 transition_map.put("Ready_IVacuumExpCryo_Vent_2", 21);
		 transition_map.put("Ready_IVacuumExpCryo_CryoCycleState_1", 22);
		 transition_map.put("Ready_IVacuumExpCryo_Vent_1", 23);
		 transition_map.put("Ready_IVacuumExpCryo_OpenColumnValves_1", 24);
		 transition_map.put("Ready_IVacuumExpCryo_Abort_1", 25);
		 transition_map.put("Ready_IVacuumExpCryo_ColumnValvesState_1", 26);
		 transition_map.put("Ready_IVacuumExpCryo_State_1", 27);
		 transition_map.put("Ready_IVacuumExpCryo_Evacuate_1", 28);
		 transition_map.put("Ready_IVacuumExpCryo_Start_1", 29);
		 transition_map.put("Ready_IVacuumExpCryo_Start_2", 30);
		 transition_map.put("Ready_IVacuumExpCryo_Start_1_reply_11", 31);
		 transition_map.put("Ready_IVacuumExpCryo_Start_2_reply_11", 32);
		 transition_map.put("Ready_IVacuumExpCryo_State_1_reply_11", 33);
		 transition_map.put("Ready_IVacuumExpCryo_Vent_1_reply_31", 34);
		 transition_map.put("Ready_IVacuumExpCryo_Vent_1_reply_11", 35);
		 transition_map.put("Ready_IVacuumExpCryo_Vent_1_reply_21", 36);
		 transition_map.put("Ready_IVacuumExpCryo_Vent_2_reply_11", 37);
		 transition_map.put("Ready_IVacuumExpCryo_Evacuate_1_reply_11", 38);
		 transition_map.put("Ready_IVacuumExpCryo_OpenColumnValves_1_reply_11", 39);
		 transition_map.put("Ready_IVacuumExpCryo_OpenColumnValves_1_reply_21", 40);
		 transition_map.put("Ready_IVacuumExpCryo_Abort_1_reply_11", 41);
		 transition_map.put("Ready_IVacuumExpCryo_CloseColumnValves_1_reply_11", 42);
		 transition_map.put("Ready_IVacuumExpCryo_CloseColumnValves_1_reply_21", 43);
		 transition_map.put("Ready_IVacuumExpCryo_CryoCycleState_1_reply_11", 44);
		 transition_map.put("Ready_IVacuumExpCryo_ColumnValvesState_1_reply_11", 45);
		 transition_map.put("Vented_IVacuumExpCryo_CloseColumnValves_1", 46);
		 transition_map.put("Vented_IVacuumExpCryo_CryoCycleState_1", 47);
		 transition_map.put("Vented_IVacuumExpCryo_OpenColumnValves_1", 48);
		 transition_map.put("Vented_IVacuumExpCryo_Abort_1", 49);
		 transition_map.put("Vented_IVacuumExpCryo_Start_1", 50);
		 transition_map.put("Vented_IVacuumExpCryo_ColumnValvesState_1", 51);
		 transition_map.put("Vented_IVacuumExpCryo_Evacuate_1", 52);
		 transition_map.put("Vented_IVacuumExpCryo_Vent_1", 53);
		 transition_map.put("Vented_IVacuumExpCryo_State_1", 54);
		 transition_map.put("Vented_IVacuumExpCryo_Start_1_reply_11", 55);
		 transition_map.put("Vented_IVacuumExpCryo_State_1_reply_11", 56);
		 transition_map.put("Vented_IVacuumExpCryo_Vent_1_reply_11", 57);
		 transition_map.put("Vented_IVacuumExpCryo_Evacuate_1_reply_21", 58);
		 transition_map.put("Vented_IVacuumExpCryo_Evacuate_1_reply_11", 59);
		 transition_map.put("Vented_IVacuumExpCryo_OpenColumnValves_1_reply_11", 60);
		 transition_map.put("Vented_IVacuumExpCryo_Abort_1_reply_11", 61);
		 transition_map.put("Vented_IVacuumExpCryo_CloseColumnValves_1_reply_11", 62);
		 transition_map.put("Vented_IVacuumExpCryo_CryoCycleState_1_reply_11", 63);
		 transition_map.put("Vented_IVacuumExpCryo_ColumnValvesState_1_reply_11", 64);
		 transition_map.put("CryoRunning_IVacuumExpCryo_State_1", 65);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Evacuate_1", 66);
		 transition_map.put("CryoRunning_IVacuumExpCryo_OpenColumnValves_1", 67);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Start_1", 68);
		 transition_map.put("CryoRunning_IVacuumExpCryo_CloseColumnValves_1", 69);
		 transition_map.put("CryoRunning_IVacuumExpCryo_CryoCycleStateChanged_1_1", 70);
		 transition_map.put("CryoRunning_IVacuumExpCryo_CryoCycleState_1", 71);
		 transition_map.put("CryoRunning_IVacuumExpCryo_ColumnValvesState_1", 72);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Vent_1", 73);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Abort_1", 74);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Start_1_reply_11", 75);
		 transition_map.put("CryoRunning_IVacuumExpCryo_State_1_reply_11", 76);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Vent_1_reply_11", 77);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Evacuate_1_reply_11", 78);
		 transition_map.put("CryoRunning_IVacuumExpCryo_OpenColumnValves_1_reply_11", 79);
		 transition_map.put("CryoRunning_IVacuumExpCryo_Abort_1_reply_11", 80);
		 transition_map.put("CryoRunning_IVacuumExpCryo_CloseColumnValves_1_reply_11", 81);
		 transition_map.put("CryoRunning_IVacuumExpCryo_CryoCycleState_1_reply_11", 82);
		 transition_map.put("CryoRunning_IVacuumExpCryo_ColumnValvesState_1_reply_11", 83);
	}
	
	public int getTransitionIndex(String transition_name) 
	{
	    Iterator<Entry<String, Integer>> it = transition_map.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	    	HashMap.Entry<String, Integer> pair = (HashMap.Entry<String, Integer>)it.next();
	    	if(pair.getKey().equals(transition_name))
	    		return pair.getValue();
	    }
	    return -1;
	}
}

