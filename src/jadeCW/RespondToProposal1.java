package jadeCW;

import java.io.IOException;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class RespondToProposal1 extends Behaviour {

	PatientAgent patientAgent;

	public RespondToProposal1(PatientAgent patientAgent) {
		super(patientAgent);
		this.patientAgent = patientAgent;
	}

	@Override
	public void action() {

		String conversationId = "request-swap-to-hospital";
		MessageTemplate reqTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
				MessageTemplate.MatchConversationId(conversationId));

		ACLMessage propsal = patientAgent.receive(reqTemplate);

		if (propsal != null){

			if (!reqTemplate.match(propsal)) {
				System.err.println("Message template doesn't match!");
			} 
			else if (propsal.getPerformative() == ACLMessage.PROPOSE){
				
				int recievedTimeSlot = Integer.valueOf(propsal.getContent());
				ACLMessage reply = propsal.createReply();

				if (patientAgent.getCurrentPriority() < 
						patientAgent.getPriorityOfTimeSlot(recievedTimeSlot)) {
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
				}
				else {
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					patientAgent.allocatedAppointment = recievedTimeSlot;
					
					try {
						informHospital(propsal.getSender());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				patientAgent.send(reply);
			}
			else {
				System.out.println("Performative does not match");
			}
		}
		else{
			block();
		}	
	}
	
	private void informHospital(AID swapPatientAgentAID) throws IOException{
		String conversationId = "request-swap-to-hospital";
		ACLMessage request = new ACLMessage(ACLMessage.INFORM);
		request.addReceiver(patientAgent.allocationAgent);
		request.setSender(patientAgent.getAID());
		request.setContentObject(swapPatientAgentAID);
		request.setConversationId(conversationId);
		request.setReplyWith(conversationId + " " + System.currentTimeMillis());
		patientAgent.send(request);
	}

	@Override
	public boolean done() {
		return false;
	}

}
