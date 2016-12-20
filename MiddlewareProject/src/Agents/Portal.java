/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agents;

import Messages.Message;
import Messages.MessageType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author chris
 */
public class Portal extends MetaAgent {
	
	
	//The children of the node
	private Set<MetaAgent> children = new HashSet<MetaAgent>();

	//The direct parent of the node
	private MetaAgent parent = null;

	//Should be maybe moved to the meta agent, this of course depending on what we do with the nodeMonitor
	private HashMap<MetaAgent, MetaAgent> registeredAddresses = new HashMap<MetaAgent, MetaAgent>();
        
        //Undelivered messages
        private HashMap<MetaAgent, Message> lostMessages = new HashMap<MetaAgent, Message>();

	//This should be in the meta object
	private LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<>();

	//Should be in the meta
	private Thread thread = new Thread();

	//A set of all the active monitors of the portal
	private Set<NodeMonitor> monitors = new HashSet<NodeMonitor>();


	//Constructor starts the thread
	public Portal(String name) {
		super(name);
		thread.start();
	}

	//Adds a node to its children
	private void addChild(MetaAgent child){
		children.add(child);
	}
        
        //Removes a node from its children
        private void removeChild(MetaAgent child){
            children.remove(child);
        }

	//Adds a monitor to report to with messages
	private void addMonitor(MetaAgent mon){
		monitors.add(mon);
	}

	//Removes a monitor 
	private void removeMontior(MetaAgent mon){
		monitors.remove(mon);
	}
	
	//Public enabling others to add messages to its queue
	public void addToQueue(Message message){
		queue.add(message);
	}

	//Forwards a message on to the recepients 
	private void sendMessage(Message message){
		MetaAgent recepient = message.getRecepient();
		if(registeredAddresses.containsKey(recepient)){
			registeredAddresses.get(recepient).addToQueue(message);
		}
		else{
			
		}
	}


	//Updates all current monitors with information about messages coming through
	private void updateMonitors(Message message){
		Iterator<NodeMonitor> it = monitors.iterator();
		while(it.hasNext()){
			it.next().addToQueue(Message message);
		}

	}

	private boolean isForMe(MetaAgent x){
		return x == this;
	}

	//Runs through all the children and updates with current addressbook
	private void updateChildrenWithAddressBook(MetaAgent scope){

		for (MetaAgent x : children){
			x.addToQueue(new Message(MessageType.UPDATE_ADDRESSES, this, x, registeredAddresses, scope));
		}
	}

	//Updates parent with address book
	private void updateParentWithAddressBook(MetaAgent scope){
		parent.addToQueue(new Message(MessageType.UPDATE_ADDRESSES, this, parent, registeredAddresses, scope));
	}

	//Adds a node  to the portals children and updates the address
	private void addNode(MetaAgent node){

		children.add(node);
		registeredAddresses.put(node, this);
		updateChildrenWithAddressBook(node.getScope());
		if(!scopedHere(node.getScope()))
			updateParentWithAddressBook(node.getScope());
	}

	private boolean scopedHere(MetaAgent scope){
		return scope == this;
	}

	private void updateAddressBook(Message message){
		HashMap<MetaAgent, MetaAgent> passedIn = (HashMap < MetaAgent, MetaAgent
								>) message.retrieveMessageItem();
		if(passedIn.equals(registeredAddresses)){
			return;
		}
		
		registeredAddresses.putAll(passedIn);
		updateChildrenWithAddressBook(message.getScope());
		if(!scopedHere(message.getScope())){
			updateParentWithAddressBook(message.getScope());
		}
		
	}

	//Extracts the message details and handles as appropriate
	private void extractMessageDetailsAndHandle(Message message){
		
		switch(message.getMessageType()){

			case PASS_MESSAGE:
				String theMessage = (String) message.retrieveMessageItem();
				break;
			case ADD_NODE:
				addNode((MetaAgent) message.retrieveMessageItem());
				break;
			case UPDATE_ADDRESSES:
				updateAddressBook(message);


			
		}

	}

	//Looks up the message address and passes on the message if its held in registered addresses
	//otherwise an error is send back and the message added to lost messages
	private void lookUpAndPassOn(Message message){

		if(registeredAddresses.containsKey(message.getRecipient())){
			registeredAddresses.get(message.getRecipient()).addToQueue(message);
		}
		else{
			//need to amend to make bounded and also think about what happens if it is already contained in the map
			lostMessages.put(message.getRecipient(), message);
			registeredAddresses.get(message.getSender()).addToQueue(new Message(MessageType.ADDRESS_NOT_FOUND_IN_LOST_PROPERTY, this, message.getSender(), null));
		}

	}

	//Handles a message pull
	private void handle(Message message){
		updateMonitors(message);		
		if(isForMe(message.getRecipient())){
			extractMessageDetailsAndHandle(message);
		}
		else{
			lookUpAndPassOn(message);
		}
	}
        
        //Merge 2 portals together
        private void mergePortal(MetaAgent portal){
            //something about merging portals here
        }

	@Override
	public void run() {

		while(true){
			handle(queue.remove());
			
		}
	}


}