package tas.adaptation.simple;

import java.io.File;

import service.adaptation.probes.interfaces.WorkflowProbeInterface;
import service.auxiliary.ServiceDescription;
import tas.adaptation.GamesAdaptationEngine;


public class GamesProbe implements WorkflowProbeInterface {

	GamesAdaptationEngine myAdaptationEngine;
	public int workflowInvocationCount=0;
	
	public void connect(GamesAdaptationEngine myAdaptationEngine) {
	    this.myAdaptationEngine = myAdaptationEngine;
	    System.out.println("Connection between games probe and adaptation engine is made :"+myAdaptationEngine.getName());
	}

	public void reset(){
	  	workflowInvocationCount=0;
	}
	  
	@Override
	public void workflowStarted(String qosRequirement, Object[] params) {
	    System.err.println("GamesProbe notifies workflow started");
	    //Log.addLog("WorkflowStarted", "Workflow Started monitoring");
	    System.out.println("QoS requirement received by games probe is :"+qosRequirement);
	    myAdaptationEngine.setQoSType(qosRequirement);
	    workflowInvocationCount++;	 
	    System.out.println("Current composition id is: "+workflowInvocationCount);
	    myAdaptationEngine.setCompId(workflowInvocationCount);
	}

	@Override
	public void workflowEnded(Object result, String qosRequirement, Object[] params) {
	    System.err.println("GamesProbe notifies workflow ended");
	}

	@Override
	public void serviceOperationInvoked(ServiceDescription service, String opName, Object[] params) {
	//	 System.err.println("GamesProbe notifies service invocation failed: " + service.getServiceName());
	//	 myAdaptationEngine.handleServiceInvocationFailure();
	}

	@Override
	public void serviceOperationReturned(ServiceDescription service, Object result, String opName, Object[] params) {
		
	}

	@Override
	public void serviceOperationTimeout(ServiceDescription service, String opName, Object[] params) {
	    System.err.println("GamesProbe notifies service failed: " + service.getServiceName());
	    // Remove service from cache
	    
	    myAdaptationEngine.setProbe("ServiceFailure");
	    myAdaptationEngine.handleServiceFailure(service, opName);
	}
	
	@Override
	public void serviceNotFound(String serviceType, String opName){
	    System.err.println("GamesProbe notifies service operation not found: " + serviceType + opName);
	    
	    myAdaptationEngine.setProbe("ServiceNotFound");
	    myAdaptationEngine.handleServiceNotFound(serviceType, opName);
	}
}
