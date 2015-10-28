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
	    	//plan = new Planner();
			//plan.synthesis();
	      	
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	//this.myEffector.removeService(service);
	    	System.err.println("Handling service failure by games-based adaptation");
	    	mapStratwithEffector(service, opName); 
	    	
	    }

	    public void handleServiceNotFound(String serviceType, String opName) {
	    	this.myEffector.refreshAllServices(serviceType, opName);
	    	System.err.println("Handling service not found");
	    }
	    
	 //   public void handleServiceInvocationFailure(){
	 //   	System.err.println("Handling service invocation failure");
	 //   	this.myEffector.refreshAllServices();
	 //   }
	    
	 //   public void handleServiceOperationTimeout(){
	 //   	System.err.println("Handling service operation timeout");
	 //   	mapStratwithEffector();    	
	 //   }
	   
	    public void mapStratwithEffector(ServiceDescription service, String opName){
	    	int ch = -1;
	    	Random rand = new Random();
	    	//plan.synthesis();
	    	//ch = plan.getStrategy();
	    	ch = rand.nextInt(1);
	    	if (ch == 0) {
	    		myEffector.removeService(service);
	    	}else if (ch == 1) {
	    		myEffector.refreshAllServices(service.getServiceType(), opName);
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
