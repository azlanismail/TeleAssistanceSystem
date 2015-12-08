package tas.services.qos;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import adapt.multiplan.MultiPlanner;
import service.auxiliary.ServiceDescription;
import service.workflow.AbstractQoSRequirement;

public class MultiQoS implements AbstractQoSRequirement {
	MultiPlanner plan;
	
	@Override
    public ServiceDescription applyQoSRequirement(List<ServiceDescription> serviceDescriptions,String opName,Object[] params) {
	 
	 	System.out.println("Begin multi-objective planning based on stochastic games");
	 	
		//params: stage
		plan = new MultiPlanner(0); 
		
		//get the service type
		String serviceType = (String) serviceDescriptions.get(0).getServiceType();
 		     		
		//params: goal type, probe, service type, failedServiceId
 		plan.setConstantsParams(4,-1,serviceType,-1);    		
 		
 		//assign the service profiles to the model
 		int ind = -1;
 		int rt = 0;
 		double cs = 0.0;
 		double fr = 0.0;
 		HashMap properties;
 		for (int i = 0; i < serviceDescriptions.size(); i++) {
		    ind = (int) serviceDescriptions.get(i).getRegisterID();
		    properties = serviceDescriptions.get(i).getCustomProperties();
		    if (properties.containsKey("ResponseTime"))
		    	rt = (int) properties.get("ResponseTime");
		    if (properties.containsKey("Cost"))
		    	cs = (double) properties.get("Cost");
		    if (properties.containsKey("FailureRate"))
		    	fr = (double) properties.get("FailureRate");
		    
		    plan.setConstantsServiceProfile(ind, rt, cs, fr);
		}
 		//perform the synthesis
		plan.generate();
		
		//get the selected service
		int sid = -1;
    	try {
			sid = plan.getAdaptStrategyfromAdv();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//assign the selected service	
		int index = 0;
		int id = -1;
		for (int i = 0; i < serviceDescriptions.size(); i++) {
		    id = (int) serviceDescriptions.get(i).getRegisterID();
		    if (id == sid){
			    index = i;
			    break;
		    }
		}
		
		return serviceDescriptions.get(index);
	    }
}
