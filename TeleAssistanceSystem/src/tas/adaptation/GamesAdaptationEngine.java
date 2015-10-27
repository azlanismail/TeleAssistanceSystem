package tas.adaptation;


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
			plan.synthesis();
	      	
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	this.myEffector.removeService(service);
	    	System.out.println("Handling service failure");
	    	plan.display();
	    }

	    public void handleServiceNotFound(String serviceType, String opName) {
	    	this.myEffector.refreshAllServices(serviceType, opName);
	    	System.out.println("Handling service not found");
	    }
	    
	    public void handleServiceInvocationFailure(){
	    	System.out.println("Handling service invocation failure");
	    	this.myEffector.refreshAllServices();
	    }
	    
	    public void handleServiceOperationTimeout(){
	    	System.out.println("Handling service operation timeout");
	    	mapStratwithEffector();
	    	
	    }
	   
	    public void mapStratwithEffector(){
	    	int ch = -1;
	    	
	    	plan.synthesis();
	    	ch = plan.getStrategy();
	    	if (ch == 1) {
	    		myEffector.refreshAllServices();
	    	}else if (ch == 2) {
	    		myEffector.updateWorkflow(workflow);
	    	}else
	    	{
	    		System.out.println("no action selected");
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
