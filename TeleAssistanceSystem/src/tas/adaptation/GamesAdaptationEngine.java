package tas.adaptation;


import java.io.FileNotFoundException;
import java.util.Random;

import adapt.plan.Planner;
import service.adaptation.effectors.WorkflowEffector;
import service.auxiliary.ServiceDescription;
import tas.adaptation.simple.GamesProbe;
import tas.services.assistance.AssistanceService;



public class GamesAdaptationEngine implements AdaptationEngine {

	    GamesProbe myProbe;
	    WorkflowEffector myEffector;
	    AssistanceService assistanceService;
	    ServiceDescription oldDescription, newDescription;
	    String serviceType, operationName;
	    Planner plan;
      

	    public GamesAdaptationEngine(AssistanceService assistanceService) {
	    	this.assistanceService = assistanceService;
	    	myProbe = new GamesProbe();
	    	myProbe.connect(this);
	    	myEffector = new WorkflowEffector(assistanceService);
	    	plan = new Planner();
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	System.err.println("Handling service failure by games-based adaptation");
	    	this.myEffector.removeService(service);
	    }

	    public void handleServiceNotFound(String serviceType, String opName) {
	    	System.err.println("Handling service not found by games-based adaptation");
	    	this.serviceType = serviceType;
	    	this.operationName = opName;
	    	mapStrategywithEffector();
	    }
	    
	    
	    
	 //   public void handleServiceInvocationFailure(){
	 //   	System.err.println("Handling service invocation failure");
	 //   	this.myEffector.refreshAllServices();
	 //   }
	    
	 //   public void handleServiceOperationTimeout(){
	 //   	System.err.println("Handling service operation timeout");
	 //   	mapStratwithEffector();    	
	 //   }
	   
	    public void mapStrategywithEffector(){
	    	int ch = -1;
	    	plan.synthesis();
	    	try {
				ch = plan.getAdaptStrategyfromFile();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	switch (ch) {
	    	case 0: myEffector.refreshAllServices();
	    			break;
	    	case 1: myEffector.refreshAllServices(this.serviceType, this.operationName);
	    			break;
	    //	case 2: myEffector.updateServiceDescription(oldDes, newDes);
	    //			break;
	    	default: System.err.println("no action selected");
	    			 break;
	    	}
	    }
	    

	    @Override
	    public void start() {
	    	assistanceService.getWorkflowProbe().register(myProbe);
	    	this.myEffector.refreshAllServices();
	    }
	    
	    @Override
	    public void stop() {
	    	assistanceService.getWorkflowProbe().unRegister(myProbe);
	    }
}
