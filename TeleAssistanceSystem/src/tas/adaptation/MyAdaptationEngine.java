/**
 * 
 */

package tas.adaptation;

import profile.ProfileExecutor;
import service.adaptation.effectors.CacheEffector;
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

    MyProbe myProbe;
    WorkflowEffector myEffector;
    CacheEffector cacheEffector;
    AssistanceService assistanceService;
    
    public MyAdaptationEngine(AssistanceService assistanceService) {
    	this.assistanceService = assistanceService;
    	myProbe = new MyProbe();
    	myProbe.connect(this);
    	myEffector = new WorkflowEffector(assistanceService);
    	cacheEffector = new CacheEffector(assistanceService);
    }
    
    public void handleServiceFailure(ServiceDescription service, String opName) {
    	System.err.println("Simple adaptation is trying to handle the failure");
    	this.myEffector.removeService(service);
    	System.err.println("Service "+service.getServiceName()+" has been removed");
    	this.myEffector.refreshAllServices();
    	System.err.println("Services have been refreshed");
    	//assistanceService.applyQoSRequirement(qosRequirementName, descriptions, opName, params)
    	//assistanceService.addQosRequirement("ReliabilityQoS", new ReliabilityQoS());
    	//cacheEffector.
    	//myEffector.refreshAllServices(service.getServiceType(), opName);

    	//assistanceService.invokeCompositeService(ProfileExecutor.profile.getQosRequirement(), null);
    	
    	//System.out.println("The composite service : "+myEffector.getCompositeService());
    	
    }

    public void handleServiceNotFound(String serviceType, String opName) {
    	myEffector.refreshAllServices(serviceType, opName);
    }
     

    @Override
    public void start() {
    	assistanceService.getWorkflowProbe().register(myProbe);
    	myEffector.refreshAllServices();
    }
    
    @Override
    public void stop() {
    	assistanceService.getWorkflowProbe().unRegister(myProbe);
    }
}
