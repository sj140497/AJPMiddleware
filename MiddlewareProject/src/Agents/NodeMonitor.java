/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agents;

import Messages.Message;
import GUI.MessageBoard;
import Messages.MessageType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The node monitor is a GUI supporting monitor that can be hooked to agents and
 * then displays any messages that pass to them
 */
public class NodeMonitor extends MetaAgent {

	private MessageBoard monitorGUI;
	//A map of all nodes this monitor is keeping track of
	ArrayList<MetaAgent> listOfAllNodesThisMonitorIsWatching = new ArrayList();
	//Contains a map for each node being watched and the list of messages that have passed to them 
	HashMap<String, ArrayList<Message>> NodeMessageHistoryMap = new HashMap();
	//Contains a map of all nodes being watched and their history as a string
	HashMap<String, String> NodeHistoryAsStringMap = new HashMap<>();

	/**
	 * Constructor for the node monitor, creates a GUI instance and makes
	 * this visable
	 *
	 * @param name - The name of the monitor
	 */
	public NodeMonitor(String name) {
		super(name);
		monitorGUI = new MessageBoard(this);
		monitorGUI.setVisible(true);
	}

	/**
	 *
	 * Adds a new entry to the passed history of activity string
	 *
	 * @param message - The message details
	 * @param type - The type of message to be added
	 * @param date - The date the message was created
	 * @param sender - The sender of the message
	 * @param recip - The recipient of the message
	 * @param previousHistory - The previous history of that node
	 * @return A formatted node history
	 */
	private String formatHistory(String message, String type, String date, String sender, String recip, String previousHistory) {

		if (previousHistory == null) {
			previousHistory = "";
		}
		return previousHistory + String.format("-----------------------\n"
			+ " Date %s \n Type %s \n Sender %s \n Recpient  %s \n Message %s "
			+ " \n -----------------------\n\n", date, type, sender, recip, message);

	}

	/**
	 * Method used for testing only
	 *
	 * @param node - The node we wish to get the last message from
	 * @return - The last message recieved by that node
	 */
	public Message getLastMessage(String node) {
		int size = NodeMessageHistoryMap.get(node).size();
		return NodeMessageHistoryMap.get(node).get(size - 1);
	}

	/**
	 * This method changes the node that is currently being viewed in the
	 * GUI to do this, the last message to that node is retrieved and shown
	 * and then the overall hisory of that node is presented
	 *
	 * @param node
	 */
	public void changeNodeTo(String node) {

		ArrayList<Message> nodesList = NodeMessageHistoryMap.get(node);
		if (nodesList != null && !nodesList.isEmpty()) {
			Message lastMessage = nodesList.get(nodesList.size() - 1);
			updateAndShowMessage(lastMessage, false, node);
		}
	}

	/**
	 * This method extracts the relevant information from the passed message
	 * and passes that to the GUI to display
	 *
	 * @param message - The message to be shown
	 * @param isNewMessage - A boolean that determines if the message passed
	 * is a new one or re-displaying of an old one
	 */
	private void updateAndShowMessage(Message message, boolean isNewMessage, String nodeBeingMonitored) {
		String obj, type, sender, recip, date;
		switch (message.getMessageType()) {
			case PASS_MESSAGE:
				obj = (String) message.retrieveMessageItem();
				break;
			case ERROR:
				obj = (String) message.retrieveMessageItem();
				break;

			case ADD_NODE:
				MetaAgent n = (MetaAgent) message.retrieveMessageItem();
				obj = n.toString() + " node added with scope: " + n.getScope();
				break;
			case UPDATE_ADDRESSES:
				obj = "Address Updated";
				break;
			case ADDRESS_NOT_FOUND_MOVED_TO_LOST_PROPERTY:
				updateAndShowMessage((Message) message.retrieveMessageItem(), true, nodeBeingMonitored);
				return;
			case FAILED_TO_DELIVER:
				obj = (String) message.retrieveMessageItem();
				break;
			case WRONG_TYPE_OF_OBJECT_WAS_SENT_WITH_THIS_MESSAGE:
				obj = (String) message.retrieveMessageItem();
				break;
			case ADD_NODE_MONITOR:
				obj = "Adding a node monitor";
				break;
			default:
				obj = "";
				type = "";
		}

		type = message.getMessageType().toString();
		sender = message.getSender();
		recip = message.getRecipient();
		date = message.getTheDateOfCreation();

		//If This is a new message its details need to be added to the history of that node
		if (isNewMessage) {
			NodeHistoryAsStringMap.put(nodeBeingMonitored, formatHistory(obj, type, date, sender, recip, NodeHistoryAsStringMap.get(nodeBeingMonitored)));
		}

		monitorGUI.receivedNewMessage(nodeBeingMonitored, obj, type, date, sender, recip, NodeHistoryAsStringMap.get(nodeBeingMonitored));
	}

	/**
	 * This method adds the node to the watch list and creates a history for that node, it then sends a request to that node
	 * to add this object to its list of watchers
	 * @param message  - The message containing the request to add a node to this monitors watch list
	 */
	private void addNodeMontior(Message message) {

		MetaAgent agent = (MetaAgent) message.retrieveMessageItem();
		agent.addToQueue(new Message(MessageType.ADD_NODE_MONITOR, this.toString(), agent.toString(), this));
		listOfAllNodesThisMonitorIsWatching.add(agent);
		NodeMessageHistoryMap.put(agent.toString(), new ArrayList());
		NodeMessageHistoryMap.get(agent.toString()).add(message);
		NodeHistoryAsStringMap.put(agent.toString(), formatHistory("", message.getMessageType().toString(),
						message.getTheDateOfCreation(),
						message.getSender(), message.getRecipient(),
						NodeHistoryAsStringMap.get(agent.toString())));
	}

	/**
	 * This method adds the message to the message history and extracts the
	 * details to be displayed - because we want to insist that only
	 * messages meant for the monitor come to the monitor we make sure each
	 * message sent is wrapped in a node monitor type message that we
	 * extract and pass to be shown
	 *
	 * @param message - The message passed to the monitor
	 */
	@Override
	void handle(Message message) {

		if (NodeMessageHistoryMap.containsKey(message.getSender())) {
			NodeMessageHistoryMap.get(message.getSender()).add((Message) message.retrieveMessageItem());
		}
		switch (message.getMessageType()) {
			case ADD_NODE_MONITOR:
				addNodeMontior(message);
				break;
			case NODE_MONITOR_UPDATE:
				updateAndShowMessage((Message) message.retrieveMessageItem(), true, message.getSender());
				break;
			default:
				System.out.println("I should not have any other types of message");
				return;
		}
	}

}
