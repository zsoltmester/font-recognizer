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
import java.util.concurrent.ExecutionException;

import hu.beesmarter.bsmart.fontrecognizer.analyzer.Font;
import hu.beesmarter.bsmart.fontrecognizer.config.AppConfig;
import hu.beesmarter.bsmart.fontrecognizer.config.CommunicationConfig;


public class ServerCommunicator {

	private static final String ENCODING = "ISO-8859-1";

	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private int port;
	private String address;
	private boolean socketEnabled;

	/**
	 * Constructor of the class.
	 *
	 * @param address the ip address.
	 * @param port    the port number.
	 * @throws IOException          please handle.
	 * @throws ExecutionException   please handle.
	 * @throws InterruptedException please handle.
	 */
	public ServerCommunicator(@NonNull final String address, final int port) throws IOException, ExecutionException, InterruptedException {
		this.address = address;
		this.port = port;
	}

	/**
	 * Starts the communication (enable socket).
	 *
	 * @return {@code true} if success, {@code false} otherwise.
	 * @throws ExecutionException   please handle.
	 * @throws InterruptedException please handle.
	 */
	public boolean startCommunication() throws ExecutionException, InterruptedException {
		boolean result = new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					socket = new Socket(address, port);
					InputStream inputStream = socket.getInputStream();
					OutputStream outputStream = socket.getOutputStream();
					br = new BufferedReader(new InputStreamReader(inputStream, ENCODING));
					outputStream = new BufferedOutputStream(outputStream);
					pw = new PrintWriter(new OutputStreamWriter(outputStream, ENCODING));
					socketEnabled = true;
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return socketEnabled;
			}
		}.execute().get();
		Log.d(AppConfig.LOG, "Connection succeed: " + String.valueOf(result));
		return result;
	}

	/**
	 * Ends the communication (close the socket).
	 *
	 * @return {@code true} if success, {@code false} otherwise.
	 * @throws IOException          please handle.
	 * @throws ExecutionException   please handle.
	 * @throws InterruptedException please handle.
	 */
	public boolean endCommuncation() throws IOException, ExecutionException, InterruptedException {
		return new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					socket.close();
					socketEnabled = false;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		}.execute().get();

	}

	/**
	 * Gets back how money picture will be sent by the server.
	 *
	 * @return how money pictures will be sent by the server.
	 * @throws ExecutionException   please catch it.
	 * @throws InterruptedException please catch it.
	 */
	public int helloServer() throws ExecutionException, InterruptedException {
		return new AsyncTask<String, Void, Integer>() {
			@Override
			protected Integer doInBackground(String... params) {
				String message = getMessage();
				if (message.equals(CommunicationConfig.COMM_MSG_HELLO_SERVER)) {
					sendMessage(CommunicationConfig.COMM_MSG_HELLO_CLIENT);
				}
				message = getMessage(); //SEND YOUR ID
				if (message.equals(CommunicationConfig.COMM_MSG_REQUEST_ASK_ID)) {
					sendMessage(CommunicationConfig.COMM_MSG_REPLY_ASK_ID);
				}
				message = getMessage();
				return getRemainingPictures(message);
			}
		}.execute().get();

	}

	/**
	 * Gets the next picture.
	 *
	 * @return the picutre in byte array.
	 * @throws ExecutionException   please handle.
	 * @throws InterruptedException please handle.
	 */
	public @NonNull byte[] getNextPicture() throws ExecutionException, InterruptedException {
		return new AsyncTask<Void, Void, byte[]>() {
			@Override
			protected byte[] doInBackground(Void... params) {
				sendMessage(CommunicationConfig.COMM_MSG_CLIENT_REQUEST_NEXT_PICTURE);
				String response = getMessage();
				return ImageEncoderUtils.convertToHex(formatResponseToPictureBytes(response));
			}
		}.execute().get();
	}

	/**
	 * Sends the thought font to the server and gets back how money pictures remained.
	 *
	 * @param fontObject the fontObject
	 * @return gets back the remaining pictures, or 0 if there is no more.
	 * @throws ExecutionException   please handle.
	 * @throws InterruptedException please handle.
	 */
	public int sendThoughtFont(@NonNull final Font fontObject) throws ExecutionException, InterruptedException {
		return new AsyncTask<String, Void, Integer>() {
			@Override
			protected Integer doInBackground(String... params) {
				sendMessage(fontObject.getFontName());
				String response = getMessage();
				return getRemainingPictures(response);
			}
		}.execute().get();
	}

	/**
	 * Check the server message that it is the last.
	 *
	 * @param message the server message.
	 * @return {@code true} if it is the last message.
	 */
	public boolean checkEndMessage(@NonNull String message) {
		return message.startsWith(CommunicationConfig.COMM_MSG_SERVER_FONT_ACK_END);
	}

	private void sendMessage(@NonNull String message) {
		pw.println(message);
		pw.flush();
	}

	private @Nullable String getMessage() {
		String message = null;
		try {
			message = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (message != null)
			Log.d(AppConfig.LOG, message);
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
		String splittedString = communicationMessage.substring(indexBeforeNumber + 1);
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
		Log.d(AppConfig.LOG, formattedString);
		return formattedString;
	}
}

