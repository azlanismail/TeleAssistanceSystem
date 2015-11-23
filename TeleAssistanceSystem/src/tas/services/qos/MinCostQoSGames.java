package tas.services.qos;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

import adapt.plan.Planner;
import service.auxiliary.ServiceDescription;
import service.workflow.AbstractQoSRequirement;

public class MinCostQoSGames implements AbstractQoSRequirement {

		Planner plan;
	 @Override
	    public ServiceDescription applyQoSRequirement(List<ServiceDescription> serviceDescriptions,String opName,Object[] params) {
		 
		 	System.out.println("Begin planning for min cost based on stochastic games");
		 	
			plan = new Planner(0); //which means the initial stage
     		
			//set the initial configuration
			plan.setConstantsGoalType(0);    	 
     		plan.setConstantsFailedServiceId(-1);
     		plan.setConstantsProbe(-1);
     		String serviceType = (String) serviceDescriptions.get(0).getServiceType();
     		plan.setServiceType(serviceType);
     		
     		//perform the synthesis
    		plan.adaptPlan();
    		
    		//get the selected service
    		int sid = -1;
	    	try {
				sid = plan.getAdaptStrategyfromFile();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	//assign the selected service
	    	    	
		 
		 //-------------------------
	
			int index = 0;
			int id = -1;
			HashMap properties;
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
