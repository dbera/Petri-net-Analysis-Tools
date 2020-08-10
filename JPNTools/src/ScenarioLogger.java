

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class ScenarioLogger 
{
	private static ScenarioLogger scnLoggerInst;
	private static PrintWriter writer;
	private static Map<Integer, ArrayList<String>> simulationStepLogs; // registers console output to stepNo
	private static Map<Integer, ArrayList<String>> simulationUMLLogs; // registers plant UML sequence diagram spec to stepNo
	private static Map<Integer, ArrayList<String>> scenarioLogs; // registers scenario to stepNo
	//private static Map<Integer, ArrayList<String>> brScenarioLogs; // registers branching scenario to stepNo
	private static Map<Integer, ArrayList<String>> stateInfoLogs; // registers current state text to stepNo
	private static Map<Integer, String> stepToMachineLogs; // registers the server state machine name to stepNo
	private static Map<Integer, ArrayList<String>> stepToInputArgsLogs; // registers input arguments to stepNo
	private static Map<Integer, ArrayList<String>> stepToResultParamsLogs; // registers output arguments to stepNo
	private static Map<Integer, ArrayList<String>> stepToSCNInputArgsLogs; // registers input arguments to stepNo
	private static Map<Integer, ArrayList<String>> stepToSCNResultParamsLogs; // registers output arguments to stepNo

	private static ArrayList<String> scenariosList = new ArrayList<String>();
	private static ArrayList<String> validationsList = new ArrayList<String>();
	private static ArrayList<String> brScenarioLogs;
	private static ArrayList<String> brScenarioStepLogs;
	private static int stepNo;
	private static int brScnIdx;
	private static boolean brScnGen;

	// master boolean to start-stop logging
	private static boolean stopLogging;

	private static String initialStateTxt = new String();

	private static final String initStreamGraphTxt = "DGS004\r\n" + "\"reachabilitygraph.dgs\" 0 0";

	private static final String initTxt = "connections\r\n" + "(Client0, p, IVacuumExpCryo, Server0, ev)\n\n"+"events\n\n";
	private static final String staticTxt = " 1556100446.752046000 0.0 Client0 p Server0 ev IVacuumExpCryo ";
	private static final String postTxt = "End";

	private static final String startUMLTxt = "@startuml"+"\n autonumber \n";
	private static final String endUMLTxt = "\n"+"@enduml";

	private static final String startSCNTxt = 
			"import \"platform:/resource/VacuumV1/IVacuumExpCryo/IVacuumExpCryo.signature\"\n" +
					"Generating Sequence Diagrams for {\r\n" + 
					"Filter Type: ALL\r\n" + 
					"}\n";

	private ScenarioLogger() {}

	public static ScenarioLogger getInstance() {
		if (scnLoggerInst == null) 
		{
			scnLoggerInst = new ScenarioLogger();
			stepNo = 0;
			brScnIdx = 0;
			brScnGen = false;
			stopLogging = false;
			simulationStepLogs = new HashMap<Integer, ArrayList<String>>();
			simulationStepLogs.put(stepNo, new ArrayList<String>());
			simulationUMLLogs = new HashMap<Integer, ArrayList<String>>();
			simulationUMLLogs.put(stepNo, new ArrayList<String>());
			scenarioLogs = new HashMap<Integer, ArrayList<String>>();
			scenarioLogs.put(stepNo, new ArrayList<String>());
			stateInfoLogs = new HashMap<Integer, ArrayList<String>>();
			stateInfoLogs.put(stepNo, new ArrayList<String>());
			stepToMachineLogs = new HashMap<Integer, String>();
			stepToMachineLogs.put(stepNo, new String());
			stepToInputArgsLogs = new HashMap<Integer, ArrayList<String>>();
			stepToInputArgsLogs.put(stepNo, new ArrayList<String>());
			stepToResultParamsLogs = new HashMap<Integer, ArrayList<String>>();
			stepToResultParamsLogs.put(stepNo, new ArrayList<String>());
			stepToSCNInputArgsLogs = new HashMap<Integer, ArrayList<String>>();
			stepToSCNInputArgsLogs.put(stepNo, new ArrayList<String>());
			stepToSCNResultParamsLogs = new HashMap<Integer, ArrayList<String>>();
			stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>());
			scenariosList = new ArrayList<String>();
			validationsList = new ArrayList<String>();
			brScenarioLogs = new ArrayList<String>();
			brScenarioStepLogs = new ArrayList<String>();
		}
		return scnLoggerInst;
	}

	public void resetLogs() {
		stepNo = 0;
		brScnIdx = 0;
		brScnGen = false;
		stopLogging = false;
		simulationStepLogs = new HashMap<Integer, ArrayList<String>>();
		simulationStepLogs.put(stepNo, new ArrayList<String>());
		simulationUMLLogs = new HashMap<Integer, ArrayList<String>>();
		simulationUMLLogs.put(stepNo, new ArrayList<String>());
		scenarioLogs = new HashMap<Integer, ArrayList<String>>();
		scenarioLogs.put(stepNo, new ArrayList<String>());
		stateInfoLogs = new HashMap<Integer, ArrayList<String>>();
		stateInfoLogs.put(stepNo, new ArrayList<String>());
		stepToMachineLogs = new HashMap<Integer, String>();
		stepToMachineLogs.put(stepNo, new String());
		stepToInputArgsLogs = new HashMap<Integer, ArrayList<String>>();
		stepToInputArgsLogs.put(stepNo, new ArrayList<String>());
		stepToResultParamsLogs = new HashMap<Integer, ArrayList<String>>();
		stepToResultParamsLogs.put(stepNo, new ArrayList<String>());
		stepToSCNInputArgsLogs = new HashMap<Integer, ArrayList<String>>();
		stepToSCNInputArgsLogs.put(stepNo, new ArrayList<String>());
		stepToSCNResultParamsLogs = new HashMap<Integer, ArrayList<String>>();
		stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>());
		scenariosList = new ArrayList<String>();
		validationsList = new ArrayList<String>();
		brScenarioLogs = new ArrayList<String>();
		brScenarioStepLogs = new ArrayList<String>();
	}

	// invariant: stopLogging = false; TODO Throw exception
	public void incrementStepCount() { 
		stepNo++;
		simulationStepLogs.put(stepNo, new ArrayList<String>()); 
		simulationUMLLogs.put(stepNo, new ArrayList<String>());
		scenarioLogs.put(stepNo, new ArrayList<String>());
		stateInfoLogs.put(stepNo, new ArrayList<String>());
		stepToMachineLogs.put(stepNo, new String());
		stepToInputArgsLogs.put(stepNo, new ArrayList<String>());
		stepToResultParamsLogs.put(stepNo, new ArrayList<String>());
		stepToSCNInputArgsLogs.put(stepNo, new ArrayList<String>());
		stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>());
	}

	public int getStepCount() { return stepNo; }

	public void setInitialState(String txt) { initialStateTxt = "state:" + txt; }

	public void addToBrLogs(String elm) { brScenarioLogs.add(elm); }
	public void setBrScnIdx(int idx) { brScnIdx = idx; }
	public void setBrScnGen(boolean value) { brScnGen = value; }
	public void resetBrStepLog() { brScenarioStepLogs = new ArrayList<String>(); }
	public void addToBrStepLog(String elm) { brScenarioStepLogs.add(elm); }
	public String generateTxtFromBrStepLogs() { 
		String txt = new String();
		//addToBrStepLog("	["+generateNode(eventType, eventName) + " nodeID: N"+brScnIdx+"]; \n");
		txt += "\t[\n";
		for(String elm : brScenarioStepLogs) {
			txt += "\t" + elm + "\n";
		}
		txt += "\t" + " nodeID: N"+brScnIdx+"]; \n";
		return txt;
	}

	public void stopLoggingMsgs() { stopLogging = true; }
	public void startLoggingMsgs() { stopLogging = false; }

	// checking invariant, the increment function was called and an entry exists.
	// if it does not exist then it should crash for now.
	public void addToInputArgsLogs(String argList1, String argList2) {
		if(!stopLogging) {
			stepToInputArgsLogs.get(stepNo).add(argList1);
			stepToSCNInputArgsLogs.get(stepNo).add(argList2); }
	}

	public void addToResultParamsLogs(String argList1, String argList2) {
		if(!stopLogging) {
			stepToResultParamsLogs.get(stepNo).add(argList1);
			stepToSCNResultParamsLogs.get(stepNo).add(argList2); }
	}

	public void addToValidationsLog(String elm) { validationsList.add(elm); }

	// deprecated: do not use.
	public void addToLog(String eventType, String eventName) {
		simulationStepLogs.get(stepNo).add(eventType + staticTxt + eventName + "\n" + postTxt);

		if(eventType.equals("Command")) simulationUMLLogs.get(stepNo).add("client -[#0000FF]> server : "+eventName);
		else if(eventType.equals("Reply")) simulationUMLLogs.get(stepNo).add("server -[#red]-\\ client : "+eventName+"_Reply");
		else if(eventType.equals("Signal")) simulationUMLLogs.get(stepNo).add("client -[#0000FF]> server : "+eventName);
		else simulationUMLLogs.get(stepNo).add("server -[#red]-> client : "+eventName);
	}

	public String generateNode(String eventType, String eventName) 
	{
		String trace_tag = "";
		if(eventType.equals("Command")) {
			trace_tag += "COMMAND: "+ eventName +" ( \n"+getTabTxt(stepToSCNInputArgsLogs.get(stepNo))+" \t)";
		}
		else if(eventType.equals("Reply")) {
			trace_tag += "REPLY: ( \n"+getTabTxt(stepToSCNResultParamsLogs.get(stepNo))+" \t)";
			stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>()); // FIX Added: Added to prevent concatenation of notification arguments to reply arguments in branching scenarios
		}
		else if(eventType.equals("Signal")) {
			trace_tag += "SIGNAL: "+ eventName +" ( \n"+getTabTxt(stepToSCNInputArgsLogs.get(stepNo))+" \t)";
		}
		else {
			trace_tag += "NOTIFICATION: "+ eventName +" ( \n"+getTabTxt(stepToSCNResultParamsLogs.get(stepNo))+" \t)";
			stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>()); // FIX Added: Added to prevent concatenation of notification arguments to reply arguments in branching scenarios
		}
		return trace_tag;
	}

	public void addToLog(String eventType, String eventName, String machineID) 
	{
		if(!stopLogging) {
			simulationStepLogs.get(stepNo).add(eventType + staticTxt + eventName + "\n" + postTxt);

			//if(brScnGen) addToBrStepLog("	["+generateNode(eventType, eventName) + " nodeID: N"+brScnIdx+"]; \n");
			if(brScnGen) addToBrStepLog(generateNode(eventType, eventName));

			if(eventType.equals("Reply"))
				scenarioLogs.get(stepNo).add(
						eventType.substring(0, 1).toLowerCase() + eventType.substring(1) + " to command " + eventName + "\n" + 
								getTxt(stepToSCNInputArgsLogs.get(stepNo)) + 
								getTxt(stepToSCNResultParamsLogs.get(stepNo)));
			else
				scenarioLogs.get(stepNo).add(
						eventType.substring(0, 1).toLowerCase() + eventType.substring(1) + " " + 
								eventName + "\n" + 
								getTxt(stepToSCNInputArgsLogs.get(stepNo)) + 
								getTxt(stepToSCNResultParamsLogs.get(stepNo)));

			// Assumes that [addToInputArgsLogs] was called by the inputArgumentsContainer. So values must be present for the current sim step.
			if(eventType.equals("Command")) simulationUMLLogs.get(stepNo).add("client -[#0000FF]> server_"+machineID+" : "+eventName+ stepToInputArgsLogs.get(stepNo));
			else if(eventType.equals("Reply")) simulationUMLLogs.get(stepNo).add("server_"+machineID+" -[#005500]-\\ client : "+eventName+"_Reply");
			else if(eventType.equals("Signal")) simulationUMLLogs.get(stepNo).add("client -[#0000FF]> server_"+machineID+" : "+eventName+ stepToInputArgsLogs.get(stepNo));
			else simulationUMLLogs.get(stepNo).add("server_"+machineID+" -[#005500]-> client : "+eventName);

			if(eventType.equals("Reply") && !getTxt(stepToResultParamsLogs.get(stepNo)).isEmpty()) {
				simulationUMLLogs.get(stepNo).add("\nnote left #99FF99\n"+ getTxt(stepToResultParamsLogs.get(stepNo)) + "\n" + "endnote\n");
				stepToResultParamsLogs.put(stepNo, new ArrayList<String>());
				// FIX Added: Added to prevent concatenation of notification arguments to reply arguments in deterministic scenarios // 15.07.2020
				stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>());
			}
			if(eventType.equals("Notification") && !getTxt(stepToResultParamsLogs.get(stepNo)).isEmpty()) { 
				simulationUMLLogs.get(stepNo).add("\nnote left #99FF99\n"+ getTxt(stepToResultParamsLogs.get(stepNo)) + "\n" + "endnote\n");
				stepToResultParamsLogs.put(stepNo, new ArrayList<String>());
				// FIX Added: Added to prevent concatenation of notification arguments to reply arguments in deterministic scenarios // 15.07.2020
				stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>());
			}

			stepToMachineLogs.put(stepNo, "server_"+machineID);
		}
	}

	// checking invariant, the increment function was called and an entry exists.
	// if it does not exist then it should crash for now.

	public void addToStateInfo(ArrayList<String> stateName) {
		if(!stopLogging) {
			stateInfoLogs.get(stepNo).add("state: "+ simplifyReplyingStateNames(stateName)); } //stateName);
	}

	public void addToStateInfo(String stateName) {
		if(!stopLogging) {
			stateInfoLogs.get(stepNo).add("state: "+stateName); }
	}

	public ArrayList<String> simplifyReplyingStateNames(ArrayList<String> srcList) {
		ArrayList<String> dstList = new ArrayList<String>();

		for(String name : srcList) {
			if(name.contains("_")) {
				String head = name.split("_")[0];
				dstList.add(head);
			}
			else {
				dstList.add(name);
			}
		}
		return dstList;
	}

	public void goToStep(int destStepNo) 
	{
		ArrayList<Integer> listOfKeysToRemove = new ArrayList<Integer>();
		if(simulationStepLogs.containsKey(destStepNo)) {
			for(int k : simulationStepLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			stepNo = destStepNo;
			for(int k : listOfKeysToRemove) {
				simulationStepLogs.remove(k);
				if(k == stepNo)
					simulationStepLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<ScenarioLogger>\n\n");

		// Do the same for list of UML txt
		listOfKeysToRemove = new ArrayList<Integer>();
		if(simulationUMLLogs.containsKey(destStepNo)) {
			for(int k : simulationUMLLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			for(int k : listOfKeysToRemove) {
				simulationUMLLogs.remove(k);
				if(k == stepNo) 
					simulationUMLLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<UMLLogger>\n\n");


		// Do the same for list of scenarioLogs
		listOfKeysToRemove = new ArrayList<Integer>();
		if(scenarioLogs.containsKey(destStepNo)) {
			for(int k : scenarioLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			for(int k : listOfKeysToRemove) {
				scenarioLogs.remove(k);
				if(k == stepNo) 
					scenarioLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<ScenarioLogsLogger>\n\n");

		// Do the same for list of State Info txt
		listOfKeysToRemove = new ArrayList<Integer>();
		if(stateInfoLogs.containsKey(destStepNo)) {
			for(int k : stateInfoLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			for(int k : listOfKeysToRemove) {
				stateInfoLogs.remove(k);
				if(k == stepNo) 
					stateInfoLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<StateInfoLogger>\n\n");

		// Do the same for machine info logs
		listOfKeysToRemove = new ArrayList<Integer>();
		if(stepToMachineLogs.containsKey(destStepNo)) {
			for(int k : stepToMachineLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			stepNo = destStepNo;
			for(int k : listOfKeysToRemove) {
				stepToMachineLogs.remove(k);
				if(k == stepNo)
					stepToMachineLogs.put(stepNo, new String());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<stepToMachineLogs>\n\n");

		// Do the same for Input Args and Result Logs
		listOfKeysToRemove = new ArrayList<Integer>();
		if(stepToInputArgsLogs.containsKey(destStepNo)) {
			for(int k : stepToInputArgsLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			stepNo = destStepNo;
			for(int k : listOfKeysToRemove) {
				stepToInputArgsLogs.remove(k);
				if(k == stepNo)
					stepToInputArgsLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<stepToInputArgsLogs>\n\n");

		listOfKeysToRemove = new ArrayList<Integer>();
		if(stepToSCNInputArgsLogs.containsKey(destStepNo)) {
			for(int k : stepToSCNInputArgsLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			stepNo = destStepNo;
			for(int k : listOfKeysToRemove) {
				stepToSCNInputArgsLogs.remove(k);
				if(k == stepNo)
					stepToSCNInputArgsLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<stepToInputSCNArgsLogs>\n\n");

		listOfKeysToRemove = new ArrayList<Integer>();
		if(stepToResultParamsLogs.containsKey(destStepNo)) {
			for(int k : stepToResultParamsLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			stepNo = destStepNo;
			for(int k : listOfKeysToRemove) {
				stepToResultParamsLogs.remove(k);
				if(k == stepNo)
					stepToResultParamsLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<stepToResultArgsLogs>\n\n");

		listOfKeysToRemove = new ArrayList<Integer>();
		if(stepToSCNResultParamsLogs.containsKey(destStepNo)) {
			for(int k : stepToSCNResultParamsLogs.keySet()) {
				if(k >= destStepNo) 
					listOfKeysToRemove.add(k);
			}
			stepNo = destStepNo;
			for(int k : listOfKeysToRemove) {
				stepToSCNResultParamsLogs.remove(k);
				if(k == stepNo)
					stepToSCNResultParamsLogs.put(stepNo, new ArrayList<String>());
			}
		}
		else System.out.println("\n\n[ERROR] Cannot find step "+ stepNo + " !! --<stepToSCNResultArgsLogs>\n\n");
	}


	// input file for graph stream application: Extension: *.dgs
	public void writeToStreamingGraphFormat() {
		try { writer = new PrintWriter("reachabilitygraph"+ ".dgs", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }
		writer.println(initStreamGraphTxt);
		for(int k : simulationStepLogs.keySet()) {
			writer.println("\n" + "// Step No. " + k + "\n");
			for(String elm : simulationStepLogs.get(k)) {
				writer.println(elm + "\n");
			}
		}
		writer.close();
	}

	public void writeToEventsFile(int scenarioID)
	{
		try { writer = new PrintWriter("scenario"+ scenarioID +".events", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }
		writer.println(initTxt);
		for(int k : simulationStepLogs.keySet()) {
			writer.println("\n" + "// Step No. " + k + "\n");
			for(String elm : simulationStepLogs.get(k)) {
				writer.println(elm + "\n");
			}
		}
		writer.close();
	}

	public void writeToPlantUMLFile(int scenarioID)
	{
		try { writer = new PrintWriter("scenario"+ scenarioID +".plantuml", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }
		writer.println(startUMLTxt);
		if(isSimulationUMLLogsEmpty()) 
			writer.println("\n Simulator->Simulator: Waiting for User Input!!\\nClick One of the Enabled\\nEvents in the Button Panel\n");
		else 
		{
			for(int k : simulationUMLLogs.keySet()) 
			{
				//writer.println("\n" + "// Step No. " + k + "\n");
				for(String elm : simulationUMLLogs.get(k)) 
				{
					writer.println(elm + "\n");
				}
			}
		}
		writer.println(endUMLTxt);
		writer.close();
	}


	public void saveCurrentScenario(int scnID)
	{
		if(!isScenrioLogsEmpty()) 
		{
			scenariosList.add("Scenario S"+scnID);
			for(int k : scenarioLogs.keySet()) {
				for(String elm : scenarioLogs.get(k)) {
					scenariosList.add(elm + "\n");
				}
			}
		}
	}

	// Added to generate locally a scenarios file as collection of all sequences saved by the user.
	public void writeToScenarioFormatLocally()
	{
		// <RESOLVED> changed file location to root. TODO move to comma-gen
		String PATH = "../../../test-gen/SavedScenarios/netSimulatorIImage/scenarios.scn";
		//if(new File("sources.txt").isFile())	PATH = "../../../test-gen/SavedScenarios/netSimulatorIImage/scenarios.scn";
		//else PATH = "scenarios.scn";			  
		File file = new File(PATH);
		//File file = new File("../../../test-gen/SavedScenarios/ReachabilityGraph/scenarios.scn");
		file.getParentFile().mkdirs();
		try { writer = new PrintWriter("../../../test-gen/SavedScenarios/ReachabilityGraph/scenarios" +".scn", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }
		//writer.println("Check: "+scenariosList);
		//writer.println("Check1: "+ scenariosList);
		//writer.println("Check2: "+ scenarioLogs);
		writer.println(startSCNTxt);
		if(!scenariosList.isEmpty()) {
			for(String elm : scenariosList) {
				writer.println(elm + "\n");
			}
		}
		writer.close();
	}

	public void writeToScenarioFormat()
	{
		// <RESOLVED> changed file location to root. TODO move to comma-gen
		File file = new File("../../../../test-gen/ReachabilityGraph/deterministic_scenarios.scn");
		file.getParentFile().mkdirs();
		try { writer = new PrintWriter("../../../../test-gen/ReachabilityGraph/deterministic_scenarios" +".scn", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }
		//writer.println("Check: "+scenariosList);
		//writer.println("Check1: "+ scenariosList);
		//writer.println("Check2: "+ scenarioLogs);
		writer.println(startSCNTxt);
		if(!scenariosList.isEmpty()) {
			for(String elm : scenariosList) {
				writer.println(elm + "\n");
			}      	
		}
		writer.close();
	}

	public void writeToValidationsFormat()
	{
		// <RESOLVED> changed file location to root. TODO move to comma-gen
		File file = new File("../../../../test-gen/ReachabilityGraph/validations.txt");
		file.getParentFile().mkdirs();
		try { writer = new PrintWriter("../../../../test-gen/ReachabilityGraph/validations" +".txt", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }

		for(String elm : validationsList)
			writer.println(elm + "\n");

		writer.close();
	}

	public void writeValidationsToConsole()
	{
		// console printing
		/*System.out.println("");
		      System.out.println("***************************************************** ");
		      System.out.println("	Validation Report ");
		      System.out.println("***************************************************** ");
		      for(String elm : validationsList) System.out.println(elm + "\n");
		      System.out.println("\n ***************************************************** ");*/

		System.out.println("");
		System.out.println("\n ***************************************************** ");
		System.out.println("	[!] Generating Files into folder test-gen... ");
		System.out.println("		> branching_scenarios.brscn");
		System.out.println("		> deterministic_scenarios.scn");
		System.out.println("		> validations.txt");
		System.out.println("	[*] Finished!");
		System.out.println("	[!] Note: Refresh root project folder (F5) ");
		System.out.println(" ***************************************************** \n");    	
	}

	public void writeToBrScnFormat()
	{
		// <RESOLVED> changed file location to root. TODO move to comma-gen
		File file = new File("branching_scenarios.brscn");
		//		  	file.getParentFile().mkdirs();
		try { writer = new PrintWriter("branching_scenarios" +".brscn", "UTF-8"); } 
		catch (FileNotFoundException | UnsupportedEncodingException e) { e.printStackTrace(); }

		for(String elm : brScenarioLogs)
			writer.println(elm + "\n");

		writer.close();
	}

	public boolean isScenrioLogsEmpty() 
	{
		boolean outcome = true;

		for(int k : scenarioLogs.keySet()) 
		{
			if(!scenarioLogs.get(k).isEmpty()) outcome = false;
		}

		return outcome;
	}

	public boolean isSimulationUMLLogsEmpty() 
	{
		boolean outcome = true;

		for(int k : simulationUMLLogs.keySet()) 
		{
			if(!simulationUMLLogs.get(k).isEmpty()) outcome = false;
		}

		return outcome;
	}

	public String writeToPlantUMLFormat()
	{
		String txt = "";
		String oldState = "";

		txt += startUMLTxt;
		if(isSimulationUMLLogsEmpty()) {
			if(!stateInfoLogs.get(stepNo).isEmpty()) txt +=  "\nnote over client, server: "+stateInfoLogs.get(stepNo).get(0)+"\n";
			else txt +=  "\nnote over client, server: Click an enabled event!\n";
		}
		else 
		{
			//txt += "\nparticipant client"  + "\nparticipant server_IVacuum2StateMachine"+"\n";
			//txt +=  "\nnote over "+ "server_IVacuum2StateMachine" +": "+initialStateTxt+"\n";
			txt +=  "\nnote over client: " + initialStateTxt+"\n";

			txt += "\nparticipant client\n";
			txt += "box \"Server\" \r\n" + 
					"	participant server_IVacuum2StateMachine\r\n" +
					//"	participant server_xyz\r\n" + 
					"end box\n";

			for(int k : simulationUMLLogs.keySet()) 
			{
				for(String elm : simulationUMLLogs.get(k)) 
				{
					txt += "\t" + elm + "\n";
				}
				//txt += "\nend\n";
				//writer.println("\n" + "// Step No. " + k + "\n");
				if(stateInfoLogs.get(k).size() > 0)
					if(!stateInfoLogs.get(k).get(0).equals(oldState)) {
						//txt +=  "\nnote over client: "+stateInfoLogs.get(k).get(0)+"\n"; 
						txt +=  "\nnote over "+ stepToMachineLogs.get(k) +": "+stateInfoLogs.get(k).get(0)+"\n";
						//txt += "\nnote over client \n"+ getTxt(stepToResultParamsLogs.get(k)) + "\n" + "endnote\n";
						//txt +=  "\nnote over server_IVacuum2StateMachine: "+stateInfoLogs.get(k).get(0)+"\n";
						oldState = stateInfoLogs.get(k).get(0);
					}
			}
		}
		txt += endUMLTxt;
		return txt;
	}

	public String getTxt(ArrayList<String> strList) {
		String txt = new String();
		for(String elm : strList)
			txt += elm + "\n";
		return txt;
	}

	public String getTabTxt(ArrayList<String> strList) {
		String txt = new String();
		for(String elm : strList)
			txt += "\t" + elm + "\n";
		return txt;
	}
}
