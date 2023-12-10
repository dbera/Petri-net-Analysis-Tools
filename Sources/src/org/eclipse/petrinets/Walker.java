package org.eclipse.petrinets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class Walker 
{
	INet n;
	int scn_index;
	ArrayList<NetState> list_of_visited_states;
	//Map<String, String> map_of_state_to_scn_id;
	ArrayList<String> list_of_visited_scn_ids;
	boolean is_state_space_generated;

	public Walker(INet _n) { 
		n = _n; 
		scn_index = 0; 
		list_of_visited_states = new ArrayList<NetState>();
		//map_of_state_to_scn_id = new HashMap<String, String>();
		list_of_visited_scn_ids = new ArrayList<String>();
		is_state_space_generated = true;
	}

	/*public String generateCurrentStateLabel(ArrayList<String> listOfStateNames) {
		String stateLabel = new String();
		for(String elm : listOfStateNames) {
			stateLabel += "#" + elm;
		}
		return stateLabel;
	}*/

	public boolean areStatesEqual(NetState ns1, NetState ns2) {
		/*if(getStateName(ns1).equals(getStateName(ns2))) {
			return true;
		}*/
		if(ns1.isEqual(ns2)) return true;
		return false;
	}

	public HashSet<String> getStateName(NetState current_state) {
		HashSet<String> placeNames = new HashSet<String>();
		Iterator<Entry<String, Integer>> it = current_state.m.entrySet().iterator();
		while (it.hasNext()) {
			Marking.Entry<String, Integer> pair = (Marking.Entry<String, Integer>)it.next();
			if(pair.getValue() > 0) 
				placeNames.add(pair.getKey());
		}
		return placeNames;
	}

	public String getNodeIDofStateIfPresent() {
		String found_scn_id = new String();
		/*String state_name = new String();
		try { state_name = generateCurrentStateLabel(n.getCurrentStateName()); } catch (SimulatorException e) { e.printStackTrace(); }

		// obsolete.. 07.11.2019
		if(map_of_state_to_scn_id.containsKey(state_name))
			found_scn_id = map_of_state_to_scn_id.get(state_name);*/

		int idx = 0;
		for(NetState ns : list_of_visited_states) {
			if(areStatesEqual(ns, n.getCurrentState()))
				return list_of_visited_scn_ids.get(idx);
			idx++;
		}

		// obsolete.. 07.11.2019
		return found_scn_id;
	}

	// If it is not present then it will be added to two data structures!
	public boolean isPresentInVisitedStates(String curr_scn_id) {
		Iterator<NetState> iterNS = list_of_visited_states.iterator();
		while(iterNS.hasNext()) {
			NetState temp = iterNS.next();
			if(areStatesEqual(temp, n.getCurrentState())) {
				return true;
			}
		}

		// both are updated and read together on same index 1:1 mapping is implicit!
		list_of_visited_states.add(new NetState(n.getCurrentState()));
		list_of_visited_scn_ids.add(curr_scn_id);

		// obsolete.. 07.11.2019
		/*String state_name = new String();
		try { state_name = generateCurrentStateLabel(n.getCurrentStateName()); } catch (SimulatorException e) { e.printStackTrace(); }
		map_of_state_to_scn_id.put(state_name, curr_scn_id);*/

		return false;
	}

	/*public String generateCurrentStateLabel(ArrayList<String> listOfStateNames) {
		String stateLabel = new String();
		for(String elm : listOfStateNames) {
			stateLabel += "#" + elm;
		}
		return stateLabel;
	}*/
	
	public void generateRG(NetState net_state, int level_index, int depth)
	{
		System.out.println(">>>>>>>>>>>");
		System.out.println("Level Index: "+level_index);

		//c.selectParameterContext();
		//InputParametersContext ipCtx = new InputParametersContext(c.getInputParameterContext());

		ArrayList<Transition> enbList = n.getEnabledTransitions();
		String current_state_name = new String();
		try { current_state_name = n.getCurrentStateLabelWithTokens(); } 
		catch (SimulatorException e1) { e1.printStackTrace(); }

		boolean isVisitedState = isPresentInVisitedStates("N"+scn_index);
		GraphLogger.getInstance().addToMapNodeIDToStateName("N" + scn_index, current_state_name);
		// Stop Criteria is the Same a Above. TODO Consider making it a function.
		if(level_index > depth  || isVisitedState) {
			if(level_index > depth) is_state_space_generated = false;
			if(isVisitedState) {
				//ScenarioLogger.getInstance().addToBrLogs("\n"+"Node N" + scn_index+"<\""+current_state_name+"\">" + " : " + "Event_Set[ [\"CLOSURE_FOUND\"] nodeID: "+getNodeIDofStateIfPresent()+";] \n");
				GraphLogger.getInstance().addToBrLogs(GraphLogger.getInstance().generateTxtForClosureTransitions("N"+scn_index, "clo", getNodeIDofStateIfPresent()));
			}
			else {
				//FileWriter.getInstance().br_scn_writer.println("\n"+"Node N" + scn_index+"<\""+current_state_name+"\">" + " : " + "Event_Set[ [\"DEPTH_LIMIT_REACHED\"] ] \n");
				//ScenarioLogger.getInstance().addToBrLogs("\n"+"Node N" + scn_index+"<\""+current_state_name+"\">" + " : " + "Event_Set[ [\"DEPTH_LIMIT_REACHED\"] ] \n");
				GraphLogger.getInstance().addToBrLogs(GraphLogger.getInstance().generateTxtForClosureTransitions("N"+scn_index, "ter", getNodeIDofStateIfPresent()));
			}
			System.out.println("Stop Criteria! Returning ...");
			return;
		}
		NetState copy_current_state = new NetState(net_state);
		String trace_tag = new String();

		// Commented 05.08.2020
		//trace_tag += "Node N" + scn_index +"<\""+current_state_name+"\">"+" : "+"Event_Set [ \n";
		String source_node = "N" + scn_index;
		// Loop over all enabled events in current state
		while(enbList.size() > 0)
		{
			//FileWriter.getInstance().resetBrLoggedMsgs();
			GraphLogger.getInstance().resetBrStepLog();
			GraphLogger.getInstance().setBrScnIdx(scn_index+1);

			// Fire head of enabled transition list
			try { n.fireEnabledTransition(enbList.get(0)); } 
			catch (SimulatorException e) { e.printStackTrace(); }

			// All the logging has happened in Scenario Logger for this enabled transition
			GraphLogger.getInstance().incrementStepCount();

			//trace_tag += GraphLogger.getInstance().generateTxtFromBrStepLogs("N"+scn_index);
			trace_tag += GraphLogger.getInstance().generateTxtFromBrStepLogs(source_node);
			scn_index++;

			//trace_tag += GraphLogger.getInstance().generateTxtFromBrStepLogs("N"+scn_index);

			// Remove the Fired Transition
			enbList.remove(0);

			// Recursion
			generateRG(n.getCurrentState(), level_index+1, depth);

			// BackTracking
			n.setCurrentState(copy_current_state);
			//c.setInputParameterContext(ipCtx);

			// 05.08.2020: Not necessary for branching scenario where data does not play a role -> input params and result params
			// set the scenario logger back to current level
			// ScenarioLogger.getInstance().goToStep(level_index);
		}
		// commented 05.08.2020
		// trace_tag += "] \n";

		GraphLogger.getInstance().addToBrLogs(trace_tag);

		System.out.println("Finished All Transitions at Level, Returning...");
		System.out.println("<<<<<<<<<<<<");
	}

}