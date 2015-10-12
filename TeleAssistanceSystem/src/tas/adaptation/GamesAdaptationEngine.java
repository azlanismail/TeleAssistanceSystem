package tas.adaptation;


import service.adaptation.effector.WorkflowEffector;
import service.auxiliary.ServiceDescription;
import synthesis.PrismChecker;
import tas.adaptation.simple.GamesProbe;
import tas.services.assistance.AssistanceService;
import synthesis.PrismChecker;


public class GamesAdaptationEngine implements AdaptationEngine {

	    GamesProbe myProbe;
	    WorkflowEffector myEffector;
	    AssistanceService assistanceService;
	    PrismChecker pc;
      

	    public GamesAdaptationEngine(AssistanceService assistanceService) {
	    	this.assistanceService = assistanceService;
	    	this.pc = new PrismChecker();
	    	myProbe = new GamesProbe();
	    	myProbe.connect(this);
	    	myEffector = new WorkflowEffector(assistanceService);
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	this.myEffector.removeService(service);
	    	System.out.println("Handling service failure");
	    	pc.display();
	    }

	    public void handleServiceNotFound(String serviceType, String opName) {
	    	this.myEffector.refreshAllServices(serviceType, opName);
	    	System.out.println("Handling service not found");
	    	pc.display();
	    }
	    
	    public void handleServiceInvocationFailure(){
	    	System.out.println("Handling service invocation failure");
	    	this.myEffector.refreshAllServices();
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
