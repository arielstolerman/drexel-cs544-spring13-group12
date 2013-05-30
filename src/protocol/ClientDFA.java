package protocol;

import client.*;
import devices.*;

public class ClientDFA extends DFA {

	// fields
	private ClientComm clientComm;
	private String user = null;
	private String pass = null;
	private Message response;

	// constructors
	
	public ClientDFA(ClientComm clientComm, String user, String pass) {
		super(null);
		this.clientComm = clientComm;
		this.user = user;
		this.pass = pass;
	}

	// process procedures

	@Override
	protected Message processIdle(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			state = ProtocolState.C_AWAITS_VERSION;
			return Message.POKE;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
	protected Message processClientAwaitsVersion(Message m) {
		if (m.opcode() == Message.OP_VERSION
				&& Client.VERSION.equals(m.content())) {
			state = ProtocolState.S_AWAITS_VERSION;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_VERSION;
	}

	@Override
	protected Message processServerAwaitsVersion(Message m) {
		if (m.opcode() == Message.OP_INTERNAL) {
			this.state = ProtocolState.C_AWAITS_CHALLENGE;
			return Message.CLIENT_VERSION;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
	protected Message processClientAwaitsChallenge(Message m) {
		if (m.opcode() == Message.OP_CHALLENGE) {
			response = new Message(DESAuth.genUserSemiResponse(
					user,
					pass,
					m.contentBytes()),
					Message.OP_RESPONSE);
			state = ProtocolState.S_AWAITS_RESPONSE;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
	protected Message processServerAwaitsResponse(Message m) {
		if (m.opcode() == Message.OP_INTERNAL && response != null) {
			this.state = ProtocolState.C_AWAITS_INIT;
			Message res = response;
			response = null;
			return res;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
	protected Message processClientAwaitsInit(Message m) {
		if (m.opcode() == Message.OP_INIT) {
			this.house = House.createHouseFromInit(m);
			System.out.println("::: Server house image at client side :::");
			this.house.prettyPrint();
			this.state = ProtocolState.S_AWAITS_ACTION;
			return process(Message.INTERNAL);
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
	protected Message processServerAwaitsAction(Message m) {
		// immediately after init message, signal client that no message
		// is to be sent to server until given user input
		if (m.opcode() == Message.OP_INTERNAL) {
			return Message.AWAITING_CLIENT_INPUT;
		}
		// process user action
		else if (m.opcode() == Message.OP_ACTION) {
			state = ProtocolState.C_AWAITS_CONFIRM;
			return m;
		}
		// process server update
		else if (m.opcode() == Message.OP_UPDATE) {
			house.doUpdate(new Update(m.bytes()));
			clientComm.killInput();
			house.prettyPrint();
			return Message.AWAITING_CLIENT_INPUT;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

	@Override
	protected Message processClientAwaitsConfirm(Message m) {
		if (m.opcode() == Message.OP_CONFIRM) {
			byte[] b = m.bytes();
			boolean confirmed = b[2] == 1;
			if (confirmed) {
				// apply confirmed message internally
				Action action = new Action(clientComm.getPostedActionAndReset());
				try {
					house.doAction(action);
				} catch (Exception e) {
					System.out.println("Internal error applying posted message on house");
					state = ProtocolState.IDLE;
					return Message.ERROR_GENERAL;
				}
				System.out.println("::: Action " + b[1]
						+ " confirmed, new state of the house :::");
				house.prettyPrint();
			}
			else {
				System.out.println("::: Action " + b[1] + " denied :::");
			}
			return Message.AWAITING_CLIENT_INPUT;
		}
		// error: go back to idle and return error message
		this.state = ProtocolState.IDLE;
		return Message.ERROR_GENERAL;
	}

}
