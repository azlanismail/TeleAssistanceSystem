package tas.adaptation;


import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import adapt.plan.Planner;
import service.adaptation.effectors.CacheEffector;
import service.adaptation.effectors.ConfigurationEffector;
import service.adaptation.effectors.WorkflowEffector;
import service.auxiliary.ServiceDescription;
import tas.adaptation.simple.GamesProbe;
import tas.services.assistance.AssistanceService;



public class GamesAdaptationEngine implements AdaptationEngine {

		String name = "Games-based Adaptation";
	    GamesProbe myProbe;
	    WorkflowEffector myEffector;
	    ConfigurationEffector confEffector;
	    CacheEffector cacheEffector;
	    AssistanceService assistanceService;
	    ServiceDescription service, newService;
	    Planner plan;
	      

	    public GamesAdaptationEngine(AssistanceService assistanceService) {
	    	this.assistanceService = assistanceService;
	    	myProbe = new GamesProbe();
	    	myProbe.connect(this);
	    	myEffector = new WorkflowEffector(assistanceService);
	    	confEffector = new ConfigurationEffector(assistanceService);
	    	cacheEffector = new CacheEffector(assistanceService);
	    	plan = new Planner(1); //means, the adaptation stage
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	System.err.println("Handling service failure by games-based adaptation");
	    	
	    	
	    	//this.myEffector.removeService(service);
	    	cacheEffector.getAllServices(service.getServiceType(), opName);
	    	confEffector.setMaxRetryAttempts(2);
	    	//start working with games
	    	setFailedServiceType(service.getServiceType());
	    	setFailedServiceId(service.getRegisterID());
	    	plan.generate();
	    	int sid = -1;
	    	try {
				sid = plan.getAdaptStrategyfromAdv();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	//get a new one
	    	newService = cacheEffector.getService(sid);
	    	System.out.println("The new service selected by games engine is :"+newService.getServiceName());
	    	assistanceService.setGamesAdaptPlan(true);
	    	assistanceService.setNewService(newService);
	    	
	    }

	    public void handleServiceNotFound(String serviceType, String opName) {
	    	System.err.println("Handling service not found by games-based adaptation");
	    }
	    
	    
	    
	 //   public void handleServiceInvocationFailure(){
	 //   	System.err.println("Handling service invocation failure");
	 //   	this.myEffector.refreshAllServices();
	 //   }
	    
	
	   
	    /**
	     * Objective: Provide the input for probe type
	     * @param probeTy
	     */
	    public void setQoSType(String qosType){
	    	if (qosType.equalsIgnoreCase("CostQoS") || qosType.equalsIgnoreCase("CostQoSGames")) {
	    		plan.setConstantsGoalType(0);
	    	}
	    	else if (qosType.equalsIgnoreCase("ReliabilityQoS") || qosType.equalsIgnoreCase("ReliabilityQoSGames")) {
	    		plan.setConstantsGoalType(1);
	    	}
	    	else if (qosType.equalsIgnoreCase("ResponseTimeQoS") || qosType.equalsIgnoreCase("ResponseTimeQoSGames")) {
	    		plan.setConstantsGoalType(2);
	    	}
	    	else if (qosType.equalsIgnoreCase("UtilityQoS")) {
	    		plan.setConstantsGoalType(3);
	    	}
	    	else if (qosType.equalsIgnoreCase("MultiQoS")) {
	    		plan.setConstantsGoalType(4);
	    	}else {
	    		System.out.println("QoS type cannot be mapped to prism model");
	    	}
	    }
	    
	    public void setProbe(String probeTy){
	    	if (probeTy.equalsIgnoreCase("ServiceFailure")) {
	    		plan.setConstantsProbe(0);
	    	}
	    	if (probeTy.equalsIgnoreCase("ServiceNotFound")) {
	    		plan.setConstantsProbe(1);
	    	}
	    }
	    
	    public void setFailedServiceType(String serviceType){
	    	System.out.println("Type detected and sent to the model is :"+serviceType);
	    	if (serviceType.equalsIgnoreCase("MedicalAnalysisService"))
	    		plan.setConstantsServiceType(0);
	    	if (serviceType.equalsIgnoreCase("AlarmService"))
	    		plan.setConstantsServiceType(1);
	    	if (serviceType.equalsIgnoreCase("DrugService"))
	    		plan.setConstantsServiceType(2);
	    }
	    
	    public void setFailedServiceId(int id){
	    	System.out.println("Id detected and sent to the model is :"+id);
	    	plan.setConstantsFailedServiceId(id);
	    }
	    
	    public void setMaxResponseTime(int maxRT){
	    	System.out.println("max response time is :"+maxRT);
	    	plan.setConstantsMaxResponseTime(maxRT);
	    }
	    
	    public void setMaxResponseTime(double maxFR){
	    	System.out.println("max failure rate is :"+maxFR);
	    	plan.setConstantsMaxFailureRate(maxFR);
	    }
	    	
	    public void setServiceProfiles(List<ServiceDescription> services) {
			
			HashMap properties;
	    	double cost = 0.0;
	    	int responseTime = 0;
	    	double failureRate = 0.0;
	    	
			for (int i = 0; i < services.size(); i++) {
			    properties = services.get(i).getCustomProperties();
			    if (properties.containsKey("Cost"))
			    	cost = (double) properties.get("Cost");
				if (properties.containsKey("ResponseTime"))
					responseTime = (int) properties.get("ResponseTime");
				if (properties.containsKey("FailureRate"))
					failureRate = (int) properties.get("FailureRate");
				plan.setConstantsServiceProfile(i,responseTime,cost,failureRate);	    
			}
			
		}
	    
	   
	    
	    public String getName(){
	    	return this.name;
	    }
	    

	    @Override
	    public void start() {
	    	System.out.println("start is calling from GamesAdaptation");
	    	assistanceService.getWorkflowProbe().register(myProbe);
	    	//assistanceService.setGamesPlan(true);
	    	this.myEffector.refreshAllServices();
	    	
	    }
	    
	    @Override
	    public void stop() {
	    	assistanceService.getWorkflowProbe().unRegister(myProbe);
	    }
}
