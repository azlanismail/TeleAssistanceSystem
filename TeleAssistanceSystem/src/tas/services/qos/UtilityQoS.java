package tas.services.qos;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import adapt.plan.Planner;
import service.auxiliary.ServiceDescription;
import service.workflow.AbstractQoSRequirement;

public class UtilityQoS implements AbstractQoSRequirement {
	Planner plan;
	 
	@Override
    public ServiceDescription applyQoSRequirement(List<ServiceDescription> serviceDescriptions,String opName,Object[] params) {
	 
	 	System.out.println("Begin planning for utility based on stochastic games");
	 	
		//params: stage
		plan = new Planner(0); 
		
		//get the service type
		String serviceType = (String) serviceDescriptions.get(0).getServiceType();
 		     		
		//params: goal type, probe, service type, failedServiceId
 		plan.setConstantsParams(3,-1,serviceType,-1);    		
 		
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
