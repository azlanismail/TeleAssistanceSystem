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
	    	plan = new Planner();
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	System.err.println("Handling service failure by games-based adaptation");
	    	
	    	
	    	//this.myEffector.removeService(service);
	    	cacheEffector.getAllServices(service.getServiceType(), opName);
	    	confEffector.setMaxRetryAttempts(2);
	    	//start working with games
	    	setFailedServiceType(service.getServiceType());
	    	setFailedServiceId(service.getRegisterID());
	    	plan.adaptPlan();
	    	int sid = -1;
	    	try {
				sid = plan.getAdaptStrategyfromFile();
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
	    
	 //   public void handleServiceOperationTimeout(){
	 //   	System.err.println("Handling service operation timeout");
	 //   	mapStratwithEffector();    	
	 //   }
	   
	    /**
	     * Objective: Provide the input for probe type
	     * @param probeTy
	     */
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
	    
	    public void mapStrategywithEffector(int choice){
	    	int ch = -1;
	    	plan.adaptPlan();
	    	try {
				ch = plan.getAdaptStrategyfromFile();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	switch (ch) {
	    	case 0: //myEffector.refreshAllServices();
	    			myEffector.removeService(this.service);
	    			break;
	    	case 1: //myEffector.refreshAllServices(this.serviceType, this.operationName);
	    		    myEffector.removeService(this.service);
	    			break;
	    //	case 2: myEffector.updateServiceDescription(oldDes, newDes);
	    //			break;
	    	default: System.err.println("no action selected");
	    			 break;
	    	}
	    }
	    
	    public String getName(){
	    	return this.name;
	    }
	    

	    @Override
	    public void start() {
	    	System.out.println("start is calling from GamesAdaptation");
	    	assistanceService.getWorkflowProbe().register(myProbe);
	    	assistanceService.setGamesPlan(true);
	    	this.myEffector.refreshAllServices();
	    	
	    }
	    
	    @Override
	    public void stop() {
	    	assistanceService.getWorkflowProbe().unRegister(myProbe);
	    }
}
