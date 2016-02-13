package hu.beesmarter.bsmart.fontrecognizer.config;

/**
 * Config for communication messages.
 */
public class CommunicationConfig {

	public static final String NAME_OF_TEAM = "BSMART";

	public static final String COMM_MSG_HELLO_CLIENT = "BeeZZZ 1.0 CLIENT HELLO";
	public static final String COMM_MSG_HELLO_SERVER = "BeeZZZ 1.0 SERVER HELLO";
	public static final String COMM_MSG_REQUEST_ASK_ID = "SEND YOUR ID";
	public static final String COMM_MSG_REPLY_ASK_ID = NAME_OF_TEAM;
	public static final String COMM_MSG_SERVER_ASK_ID_ACK = NAME_OF_TEAM + " ID ACK - IMAGES LEFT: ";
	public static final String COMM_MSG_CLIENT_REQUEST_NEXT_PICTURE = "RQSTNEXTPICTURE";
	public static final String COMM_MSG_SERVER_FONT_ACK_NEXT = "FONT ACK – IMAGES LEFT: ";
	public static final String COMM_MSG_SERVER_FONT_ACK_END = "FONT ACK – NO IMAGE LEFT - GOODBYE";
}
