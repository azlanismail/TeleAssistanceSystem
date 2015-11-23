package tas.services.qos;

import java.util.HashMap;
import java.util.List;

import service.auxiliary.ServiceDescription;
import service.workflow.AbstractQoSRequirement;

public class ResponseTimeQoS implements AbstractQoSRequirement {
	 @Override
	    public ServiceDescription applyQoSRequirement(List<ServiceDescription> serviceDescriptions,String opName,Object[] params) {
		
		int minRT = Integer.MAX_VALUE;
		int index = 0;
		int rt;
		HashMap properties;
		for (int i = 0; i < serviceDescriptions.size(); i++) {
		    properties = serviceDescriptions.get(i).getCustomProperties();
		    if (properties.containsKey("ResponseTime")){
			rt = (int) properties.get("ResponseTime");
			if (rt < minRT){
			    minRT = rt;
			    index = i;
			}
		    }
		}
		
		return serviceDescriptions.get(index);
	    }
}
