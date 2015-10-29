package tas.adaptation;


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
	    Planner plan;
      

	    public GamesAdaptationEngine(AssistanceService assistanceService) {
	    	this.assistanceService = assistanceService;
	    	myProbe = new GamesProbe();
	    	myProbe.connect(this);
	    	myEffector = new WorkflowEffector(assistanceService);
	    	plan = new Planner();
			//plan.synthesis();
	      	
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	//this.myEffector.removeService(service);
	    	System.err.println("Handling service failure by games-based adaptation");
	    	//mapStratwithEffector(service, opName); 
	    	mapStratwithEffector(service.getServiceType(), opName);
	    	
	    }

	    public void handleServiceNotFound(String serviceType, String opName) {
	    	//this.myEffector.refreshAllServices(serviceType, opName);
	    	System.err.println("Handling service not found by games-based adaptation");
	    	mapStratwithEffector(serviceType, opName);
	    }
	    
	 //   public void handleServiceInvocationFailure(){
	 //   	System.err.println("Handling service invocation failure");
	 //   	this.myEffector.refreshAllServices();
	 //   }
	    
	 //   public void handleServiceOperationTimeout(){
	 //   	System.err.println("Handling service operation timeout");
	 //   	mapStratwithEffector();    	
	 //   }
	   
	    public void mapStratwithEffector(String serviceType, String opName){
	    	int ch = -1;
	    	Random rand = new Random();
	    	plan.synthesis();
	    	//ch = plan.getStrategy();
	    	ch = rand.nextInt(1);
	    	if (ch == 0) {
	    		myEffector.refreshAllServices();
	    	}else if (ch == 1) {
	    		myEffector.refreshAllServices(serviceType, opName);
	    	}else
	    	{
	    		System.err.println("no action selected");
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
