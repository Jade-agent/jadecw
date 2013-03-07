package jadeCW;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class HospitalAgent extends Agent {

	public int totalAppointments;
	public AID[] appointments;
	

	public void setup(){

		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			totalAppointments = Integer.parseInt((String) args[0]);
			appointments = new AID[totalAppointments];
		}


		String serviceName = "appointment-allocator";
		String serviceType = "allocate-appointments";

		// Register the serviceFAILURE
		System.out.println("Agent "+getLocalName()+" registering service \"" +serviceName
				+"\" of type \"alloFAILUREcate-appointments\"");
		try {
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setName(serviceName);
			sd.setType(serviceType);
			// Agents that want to use this service need to "know" the weather-forecast-ontology
			//sd.addOntologies("weather-forecast-ontology");
			// Agents that want to use this service need to "speak" the FIPA-SL language
			sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
			//sd.addProperties(new Property("country", "Italy"));
			dfd.addServices(sd);

			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new AllocateAppointment(this));
		addBehaviour(new RespondToQuery(this));
		addBehaviour(new RespondToProposal2(this));
		addBehaviour(new UpdateAppointments(this));

	}
	
	public int getSlotForAID(AID slotOwner){
		
		for (int i = 0; i< appointments.length; i++){
			if (appointments[i] == slotOwner)
				return i;
		}
		return -1;
		
	}
	
	public void takeDown(){
		for (int i = 0; i<appointments.length; i++){
			int j = i+1;
			if (appointments[i] != null)
				System.out.println(this.getName() + ": Appointment " + j + ": " + appointments[i].getName());
			else
				System.out.println(this.getName() + ": Appointment " + j + ": " + " is null");
		}
	}

}
