/**
 * 
 */

package tas.adaptation;

import java.util.List;

import profile.ProfileExecutor;
import service.adaptation.effectors.CacheEffector;
import service.adaptation.effectors.ConfigurationEffector;
import service.adaptation.effectors.WorkflowEffector;
import service.auxiliary.ServiceDescription;
import tas.adaptation.simple.MyProbe;
import tas.services.assistance.AssistanceService;
import tas.services.qos.ReliabilityQoS;

/**
 * @author M. Usman Iftikhar
 * @email muusaa@lnu.se
 *
 */
public class MyAdaptationEngine implements AdaptationEngine {

	String name = "Simple Adaptation";
    MyProbe myProbe;
    WorkflowEffector myEffector;
    CacheEffector cacheEffector;
    ConfigurationEffector confEffector;
    AssistanceService assistanceService;
    
    
    
    public MyAdaptationEngine(AssistanceService assistanceService) {
    	this.assistanceService = assistanceService;
    	myProbe = new MyProbe();
    	myProbe.connect(this);
    	myEffector = new WorkflowEffector(assistanceService);
    	cacheEffector = new CacheEffector(assistanceService);
    	confEffector = new ConfigurationEffector(assistanceService);
    }
    
    public void handleServiceFailure(ServiceDescription service, String opName) {
    	System.err.println("Simple adaptation is trying to handle the failure");
    	System.err.println("Service "+service.getServiceName()+" has been removed");
    	confEffector.setMaxRetryAttempts(2); 
    	//this.myEffector.removeService(service);
    }

    public void handleServiceNotFound(String serviceType, String opName) {
    	myEffector.refreshAllServices(serviceType, opName);
    }
     
    public String getName(){
    	return this.name;
    }
    
    @Override
    public void start() {
    	System.out.println("start is calling from Simple Adaptation");
    	assistanceService.getWorkflowProbe().register(myProbe);
    	assistanceService.setGamesPlan(true);
    	myEffector.refreshAllServices();
    }
    
    @Override
    public void stop() {
    	assistanceService.getWorkflowProbe().unRegister(myProbe);
    }
}
