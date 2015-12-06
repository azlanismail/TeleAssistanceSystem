package tas.adaptation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import profile.InputProfileValue;
import profile.InputProfileVariable;
import profile.ProfileExecutor;
import service.adaptation.effectors.WorkflowEffector;
import service.atomic.AtomicService;
import service.composite.CompositeServiceClient;
import service.registry.ServiceRegistry;
import tas.services.alarm.AlarmService;
import tas.services.assistance.AssistanceService;
import tas.services.assistance.AssistanceServiceCostProbe;
import tas.services.drug.DrugService;
import tas.services.medical.MedicalAnalysisService;
import tas.services.profiles.ServiceDelayProfile;
import tas.services.profiles.ServiceFailureProfile;
import tas.services.qos.MinCostQoS;
import tas.services.qos.MinCostQoSGames;
import tas.services.qos.PreferencesQoS;
import tas.services.qos.ReliabilityQoS;
import tas.services.qos.ResponseTimeQoS;
import tas.services.qos.UtilityQoS;
import tas.services.qos.MultiQoS;

public class TASStart {

    private HashMap<String, AdaptationEngine> adaptationEngines = new LinkedHashMap<>();

    protected ServiceRegistry serviceRegistry;
    protected AssistanceService assistanceService;
    protected AssistanceServiceCostProbe monitor;
    protected WorkflowEffector workflowEffector;

    protected AlarmService alarm1;
    protected AlarmService alarm2;
    protected AlarmService alarm3;

    protected MedicalAnalysisService medicalAnalysis1;
    protected MedicalAnalysisService medicalAnalysis2;
    protected MedicalAnalysisService medicalAnalysis3;
    protected MedicalAnalysisService medicalAnalysis4;
    protected MedicalAnalysisService medicalAnalysis5;

    protected DrugService drugService;
    
    private boolean isStopped=false;
    private boolean isPaused=false;
    private int currentSteps;
    
    private Map<String,AtomicService> atomicServices=new HashMap<>();
    private List<Class<?>> serviceProfileClasses=new ArrayList<>();

    public synchronized void stop(){
    	isStopped=true;
    }
    
    private synchronized void start(){
    	isStopped=false;
    }
    
    public synchronized void pause(){
    	isPaused=true;
    }
    
    public synchronized void go(){
    	isPaused=false;
    	this.notifyAll();
    }
    
    public boolean isStopped(){
    	return isStopped;
    }
    
    public boolean isPaused(){
    	return isPaused;
    }
    
    public int getCurrentSteps(){
    	return currentSteps;
    }
    
    public void addAllServices(AtomicService... services){
    	for(AtomicService service:services)
    		atomicServices.put(service.getServiceDescription().getServiceName(), service);
    }
    
    public List<Class<?>> getServiceProfileClasses(){
    	return this.serviceProfileClasses;
    }
    
    public AtomicService getService(String name){
    	return atomicServices.get(name);
    }

    public ServiceRegistry getServiceRegistry() {
	return serviceRegistry;
    }

    public AssistanceService getAssistanceService() {
	return assistanceService;
    }

    public AssistanceServiceCostProbe getMonitor() {
    	System.out.println("Get monitor is called from TASStart");
	return monitor;
    }

    public TASStart() {

	initializeTAS();

	adaptationEngines.put("No Adaptation", new DefaultAdaptationEngine());
	adaptationEngines.put("Simple Adaptation", new MyAdaptationEngine(assistanceService));
	adaptationEngines.put("Games-based Adaptation", new GamesAdaptationEngine(assistanceService));
	//System.out.println("Size of adaptation engine is : "+adaptationEngines.size());
    }

    public HashMap<String, AdaptationEngine> getAdaptationEngines() {
    	System.out.println("getAdaptationEngines is called from  TASStart");
    	return adaptationEngines;
    }

    public void initializeTAS() {

	serviceRegistry = new ServiceRegistry();
	serviceRegistry.startService();

	// ALarm Services
	alarm1 = new AlarmService("AlarmService1", "service.alarmService1");
	alarm1.getServiceDescription().getCustomProperties().put("Cost", 4.0);
	alarm1.getServiceDescription().setOperationCost("triggerAlarm", 4.0);
	alarm1.getServiceDescription().getCustomProperties().put("ResponseTime", 11);
	alarm1.getServiceDescription().setResponseTime(11);
	alarm1.getServiceDescription().getCustomProperties().put("FailureRate", 0.11);
	alarm1.addServiceProfile(new ServiceFailureProfile(0.11));
	alarm1.startService();
	alarm1.register();

	alarm2 = new AlarmService("AlarmService2", "service.alarmService2");
	alarm2.getServiceDescription().getCustomProperties().put("Cost", 12.0);
	//alarm2.getServiceDescription().getCustomProperties().put("preferred", true);
	alarm2.getServiceDescription().setOperationCost("triggerAlarm", 12.0);
	alarm2.getServiceDescription().getCustomProperties().put("ResponseTime", 9);
	alarm2.getServiceDescription().setResponseTime(9);
	alarm2.getServiceDescription().getCustomProperties().put("FailureRate", 0.04);
	alarm2.addServiceProfile(new ServiceFailureProfile(0.04));
	alarm2.startService();
	alarm2.register();

	alarm3 = new AlarmService("AlarmService3", "service.alarmService3");
	alarm3.getServiceDescription().getCustomProperties().put("Cost", 2.0);
	alarm3.getServiceDescription().setOperationCost("triggerAlarm", 2.0);
	alarm3.getServiceDescription().getCustomProperties().put("ResponseTime", 3);
	alarm3.getServiceDescription().setResponseTime(3);
	alarm3.getServiceDescription().getCustomProperties().put("FailureRate", 0.18);
	alarm3.addServiceProfile(new ServiceFailureProfile(0.18));
	alarm3.startService();
	alarm3.register();

	// Medical Analysis Services
	medicalAnalysis1 = new MedicalAnalysisService("MedicalService1", "service.medical1");
	//medicalAnalysis1.getServiceDescription().getCustomProperties().put("preferred", false);
	medicalAnalysis1.getServiceDescription().getCustomProperties().put("Cost", 4.0);
	medicalAnalysis1.getServiceDescription().setOperationCost("analyzeData", 4.0);
	medicalAnalysis1.getServiceDescription().getCustomProperties().put("ResponseTime", 22);
	medicalAnalysis1.getServiceDescription().setResponseTime(22);
	medicalAnalysis1.getServiceDescription().getCustomProperties().put("FailureRate", 0.12);
	medicalAnalysis1.addServiceProfile(new ServiceFailureProfile(0.12));
	medicalAnalysis1.startService();
	medicalAnalysis1.register();

	medicalAnalysis2 = new MedicalAnalysisService("MedicalService2", "service.medical2");
	//medicalAnalysis2.getServiceDescription().getCustomProperties().put("preferred", true);
	medicalAnalysis2.getServiceDescription().getCustomProperties().put("Cost", 14.0);
	medicalAnalysis2.getServiceDescription().setOperationCost("analyzeData", 14.0);
	medicalAnalysis2.getServiceDescription().getCustomProperties().put("ResponseTime", 27);
	medicalAnalysis2.getServiceDescription().setResponseTime(27);
	medicalAnalysis2.getServiceDescription().getCustomProperties().put("FailureRate", 0.07);
	medicalAnalysis2.addServiceProfile(new ServiceFailureProfile(0.07));
	medicalAnalysis2.startService();
	medicalAnalysis2.register();

	medicalAnalysis3 = new MedicalAnalysisService("MedicalService3", "service.medical3");
	//medicalAnalysis3.getServiceDescription().getCustomProperties().put("preferred", false);
	medicalAnalysis3.getServiceDescription().getCustomProperties().put("Cost", 2.15);
	medicalAnalysis3.getServiceDescription().setOperationCost("analyzeData", 2.15);
	medicalAnalysis3.getServiceDescription().getCustomProperties().put("ResponseTime", 31);
	medicalAnalysis3.getServiceDescription().setResponseTime(31);
	medicalAnalysis3.getServiceDescription().getCustomProperties().put("FailureRate", 0.18);
	medicalAnalysis3.addServiceProfile(new ServiceFailureProfile(0.18));
	medicalAnalysis3.startService();
	medicalAnalysis3.register();
	
	medicalAnalysis4 = new MedicalAnalysisService("MedicalService4", "service.medical4");
	//medicalAnalysis4.getServiceDescription().getCustomProperties().put("preferred", false);
	medicalAnalysis4.getServiceDescription().getCustomProperties().put("Cost", 7.3);
	medicalAnalysis4.getServiceDescription().setOperationCost("analyzeData", 7.3);
	medicalAnalysis4.getServiceDescription().getCustomProperties().put("ResponseTime", 29);
	medicalAnalysis4.getServiceDescription().setResponseTime(29);
	medicalAnalysis4.getServiceDescription().getCustomProperties().put("FailureRate", 0.25);
	medicalAnalysis4.addServiceProfile(new ServiceFailureProfile(0.25));
	medicalAnalysis4.startService();
	medicalAnalysis4.register();
	
	medicalAnalysis5 = new MedicalAnalysisService("MedicalService5", "service.medical5");
	//medicalAnalysis5.getServiceDescription().getCustomProperties().put("preferred", false);
	medicalAnalysis5.getServiceDescription().getCustomProperties().put("Cost", 11.9);
	medicalAnalysis5.getServiceDescription().setOperationCost("analyzeData", 11.9);
	medicalAnalysis5.getServiceDescription().getCustomProperties().put("ResponseTime", 20);
	medicalAnalysis5.getServiceDescription().setResponseTime(20);
	medicalAnalysis5.getServiceDescription().getCustomProperties().put("FailureRate", 0.05);
	medicalAnalysis5.addServiceProfile(new ServiceFailureProfile(0.05));
	medicalAnalysis5.startService();
	medicalAnalysis5.register();
	

	// Drug Services
	drugService = new DrugService("DrugService", "service.drug");
	//drugService.getServiceDescription().getCustomProperties().put("preferred", true);
	drugService.getServiceDescription().getCustomProperties().put("Cost", 2.0);
	drugService.getServiceDescription().setOperationCost("changeDoses", 5.0);
	drugService.getServiceDescription().setOperationCost("changeDrug", 5.0);
	drugService.getServiceDescription().getCustomProperties().put("ResponseTime", 1);
	drugService.getServiceDescription().setResponseTime(1);
	drugService.getServiceDescription().getCustomProperties().put("FailureRate", 0.01);
	//drugService.addServiceProfile(new ServiceFailureProfile(0.01));
	drugService.startService();
	drugService.register();

	// Assistance Service. Workflow is provided by TAS_gui through executeWorkflow method
	assistanceService = new AssistanceService("TeleAssistanceService", "service.assistance", "resources/TeleAssistanceWorkflow.txt");
	assistanceService.startService();
	assistanceService.register();
	monitor = new AssistanceServiceCostProbe();
	System.out.println("registering the monitor");
	assistanceService.getCostProbe().register(monitor);
	assistanceService.getWorkflowProbe().register(monitor);
	
	//assistanceService.getWorkflowProbe().register(new AssistanceServiceDelayProbe());
	// assistanceService.getServiceInvocationProbe().register(monitor);
	assistanceService.addQosRequirement("ReliabilityQoS", new ReliabilityQoS());
	assistanceService.addQosRequirement("PreferencesQoS", new PreferencesQoS());
	assistanceService.addQosRequirement("CostQoS", new MinCostQoS());
	assistanceService.addQosRequirement("ResponseTimeQoS", new ResponseTimeQoS());
	assistanceService.addQosRequirement("CostQoSGames", new MinCostQoSGames());
	assistanceService.addQosRequirement("UtilityQoS", new UtilityQoS());
	assistanceService.addQosRequirement("MultiQoS", new MultiQoS());
	
	workflowEffector = new WorkflowEffector(assistanceService);
	
	this.addAllServices(alarm1,alarm2,alarm3,medicalAnalysis1,medicalAnalysis2,medicalAnalysis3,medicalAnalysis4,medicalAnalysis5,drugService);
	
	this.serviceProfileClasses.add(ServiceFailureProfile.class);
	this.serviceProfileClasses.add(ServiceDelayProfile.class);

	//RSPMessagingService.getInstance().setMessageLoss(20);
    }

    public void stopServices() {
	serviceRegistry.stopService();

	alarm1.stopService();
	alarm2.stopService();
	alarm3.stopService();

	medicalAnalysis1.stopService();
	medicalAnalysis2.stopService();
	medicalAnalysis3.stopService();

	drugService.stopService();
	assistanceService.stopService();

    }

    public void executeWorkflow(String workflowPath, String profilePath) {

	CompositeServiceClient client = new CompositeServiceClient("service.assistance");
	assistanceService.setWorkflow(workflowPath);
	workflowEffector.refreshAllServices();
	

	ProfileExecutor.readFromXml(profilePath);
	if (ProfileExecutor.profile != null) {
	    int maxSteps = (int) ProfileExecutor.profile.getMaxSteps();
	    InputProfileVariable variable = ProfileExecutor.profile.getVariable("pick");
	    List<InputProfileValue> values = variable.getValues();

	    int patientId = (int) ProfileExecutor.profile.getVariable("patientId").getValues().get(0).getData();
	    int pick;
	    // System.out.println("start executing workflow !!!");

	    start();
	    Random rand = new Random();
	    for (currentSteps = 0; currentSteps < maxSteps; currentSteps++) {
	    
	    	/*
	    	synchronized(this){
	    		while(isPaused()){
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    	}*/
	    System.out.println("Executing workflow :"+(currentSteps+1));	    	
		double probability = rand.nextDouble();
		double valueProbability = 0;
		for (int j = 0; j < values.size(); j++) {
		    if ((values.get(j).getRatio() + valueProbability) > probability) {
			pick = (int) values.get(j).getData();
			//System.out.println("invoke composite service from TASStart");
			client.invokeCompositeService(ProfileExecutor.profile.getQosRequirement(), patientId, pick);
			break;
		    } else
			valueProbability = valueProbability + values.get(j).getRatio();
		}
		
    	
    	if(isStopped)
    		break;
		
	    }
	    stop();
	    System.out.println("finish executing workflow !!!");
	}
    }
   
}
