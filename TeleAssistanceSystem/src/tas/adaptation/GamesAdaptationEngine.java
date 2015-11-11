package tas.adaptation;


import java.io.FileNotFoundException;
import java.util.Random;

import adapt.plan.Planner;
import service.adaptation.effectors.ConfigurationEffector;
import service.adaptation.effectors.WorkflowEffector;
import service.auxiliary.ServiceDescription;
import tas.adaptation.simple.GamesProbe;
import tas.services.assistance.AssistanceService;



public class GamesAdaptationEngine implements AdaptationEngine {

	    GamesProbe myProbe;
	    WorkflowEffector myEffector;
	    ConfigurationEffector confEffector;
	    AssistanceService assistanceService;
	    ServiceDescription service, newService;
	    Planner plan;
	      

	    public GamesAdaptationEngine(AssistanceService assistanceService) {
	    	this.assistanceService = assistanceService;
	    	myProbe = new GamesProbe();
	    	myProbe.connect(this);
	    	myEffector = new WorkflowEffector(assistanceService);
	    	confEffector = new ConfigurationEffector(assistanceService);
	    	plan = new Planner();
	    }
	    
	    
	    public void handleServiceFailure(ServiceDescription service, String opName) {
	    	System.err.println("Handling service failure by games-based adaptation");
	    	//this.myEffector.removeService(service);
	    	confEffector.setMaxRetryAttempts(2);
	    	setServiceType(service.getServiceType());
	    	setServiceId(service.getRegisterID());
	    	plan.generatePlan();
	    	int ch = -1;
	    	try {
				ch = plan.getAdaptStrategyfromFile();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	newService = assistanceService.getServiceDescription(ch);
	    	myEffector.updateServiceDescription(service, newService);
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
	    
	    public void setServiceType(String serviceType){
	    	System.out.println("Type detected and sent to the model is :"+serviceType);
	    	if (serviceType.equalsIgnoreCase("MedicalAnalysisService"))
	    		plan.setConstantsServiceType(0);
	    	if (serviceType.equalsIgnoreCase("AlarmService"))
	    		plan.setConstantsServiceType(1);
	    	if (serviceType.equalsIgnoreCase("DrugService"))
	    		plan.setConstantsServiceType(2);
	    }
	    
	    public void setServiceId(int id){
	    	System.out.println("Id detected and sent to the model is :"+id);
	    	plan.setConstantsServiceId(id);
	    }
	    	
	    
	    
	    public void mapStrategywithEffector(int choice){
	    	int ch = -1;
	    	plan.generatePlan();
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
