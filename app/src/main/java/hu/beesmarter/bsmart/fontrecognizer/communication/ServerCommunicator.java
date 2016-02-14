package hu.beesmarter.bsmart.fontrecognizer.communication;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;
import hu.beesmarter.bsmart.fontrecognizer.config.CommunicationConfig;


public class ServerCommunicator {

	public static final String TAG = ServerCommunicator.class.getSimpleName();

	private static final String ENCODING = "ISO-8859-1";

	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private int port;
	private String address;

	/**
	 * Constructor of the class.
	 *
	 * @param address the ip address.
	 * @param port    the port number.
	 */
	public ServerCommunicator(@NonNull final String address, final int port) {
		this.address = address;
		this.port = port;
	}

	/**
	 * Starts the communication (enable socket).
	 *
	 * @return {@code true} if success, {@code false} otherwise.
	 */
	public boolean startCommunication() throws IOException {
		socket = new Socket(address, port);
		InputStream inputStream = socket.getInputStream();
		OutputStream outputStream = socket.getOutputStream();
		br = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
		outputStream = new BufferedOutputStream(outputStream);
		pw = new PrintWriter(new OutputStreamWriter(outputStream, ENCODING));
		Log.d(TAG, "Connection succeeded!");
		return true;
	}

	/**
	 * Ends the communication (close the socket).
	 */
	public void endCommunication() throws IOException {
		socket.close();
	}

	/**
	 * Gets back how money picture will be sent by the server.
	 *
	 * @return how money pictures will be sent by the server.
	 */
	public int helloServer() throws IOException {
		String message = getMessage();
		if (message.equals(CommunicationConfig.COMM_MSG_HELLO_SERVER)) {
			sendMessage(CommunicationConfig.COMM_MSG_HELLO_CLIENT);
		}
		message = getMessage();
		if (message.equals(CommunicationConfig.COMM_MSG_REQUEST_ASK_ID)) {
			sendMessage(CommunicationConfig.COMM_MSG_REPLY_ASK_ID);
		}
		message = getMessage();
		return getRemainingPictures(message);
	}

	/**
	 * Gets the next picture.
	 *
	 * @return the picutre in byte array.
	 */
	public @NonNull byte[] getNextPicture() throws IOException {
		sendMessage(CommunicationConfig.COMM_MSG_CLIENT_REQUEST_NEXT_PICTURE);
		String response = getMessage();
		return ImageEncoderUtils.convertToHex(formatResponseToPictureBytes(response));
	}

	/**
	 * Sends the thought font to the server and gets back how money pictures remained.
	 *
	 * @param font the fontObject
	 */
	public void sendFont(@NonNull final Font font) throws IOException {
		sendMessage("Akiza Sans"); // TODO
		getMessage(); // clear buffer
	}

	private void sendMessage(@NonNull String message) {
		pw.println(message);
		pw.flush();
	}

	private @NonNull String getMessage() throws IOException {
		String message = br.readLine();
		if (message == null) {
			throw new IOException();
		}
		Log.d(TAG, message);
		return message;
	}

	/**
	 * Gets the number of the remaining pictures.
	 *
	 * @param communicationMessage the server message.
	 * @return the number of the remaining message, or 0 if there is no more.
	 */
	private int getRemainingPictures(@NonNull String communicationMessage) {
		int indexBeforeNumber = communicationMessage.lastIndexOf(": ");
		if (indexBeforeNumber == -1) {
			return 0;
		}
		String splittedString = communicationMessage.substring(indexBeforeNumber + 1).trim();
		return Integer.valueOf(splittedString);
	}


	/**
	 * Cuts the first >> and last << from the string.
	 *
	 * @param imageResponse the server response containing the image.
	 * @return the formatted string.
	 */
	private @NonNull String formatResponseToPictureBytes(@NonNull String imageResponse) {
		String formattedString = imageResponse.substring(2, imageResponse.length() - 1 - 2);
		Log.d(TAG, formattedString);
		return formattedString;
	}
}

